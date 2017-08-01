package com.haizhi.np.dispatch.http;

import java.nio.charset.Charset;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.pool.BasicConnFactory;
import org.apache.http.impl.pool.BasicConnPool;
import org.apache.http.impl.pool.BasicPoolEntry;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;


/**
 *	@author zhangjunfei
 */
@Component
public class HttpSender implements EnvironmentAware{

	private static final Logger LOGGER = LogManager.getLogger(HttpSender.class);
	
	private Environment env;

	private static final int BUFFER_SIZE = 2048;

	private HttpRequestExecutor httpexecutor = null;

	private BasicConnPool pool = null;

	private ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;

	private HttpProcessor processor;

	private HttpHost httpHost;

	private int validateAfterInactivity = 10 * 1000;

	private int maxPerRoute = 100;
	
	//@Value("#{crawlerHost}")
	private String hosturi;
	
	//@Value("#{httpMaxTotal}")
	private int maxTotal;
	
	//@Value("#{httpTimeout}")
	private int timeOut;
//	private int maxTotal = 100;
//	private int timeOut = 60;
	
	public HttpSender(){}
	
	@PostConstruct
	public void init() {
		this.hosturi = env.getProperty("crawlerHost");
		this.maxTotal = env.getProperty("httpMaxTotal", Integer.class);
		this.timeOut = env.getProperty("httpTimeout",Integer.class);
		
		this.httpHost = buildHttpHost(this.hosturi);
		this.httpexecutor = new HttpRequestExecutor();
		this.pool = new BasicConnPool(new BasicConnFactory(buildSocketConfig(),
				buildConnectionConfig()));
		this.pool.setMaxPerRoute(this.httpHost, this.maxPerRoute);
		this.pool.setMaxTotal(this.maxTotal);
		this.pool.setValidateAfterInactivity(validateAfterInactivity);
		this.processor = HttpProcessorBuilder.create()
				.add(new RequestContent(true)).add(new RequestTargetHost())
				.add(new RequestConnControl())
				.add(new RequestUserAgent("Trade/1.1"))
				.add(new RequestExpectContinue(true)).build();
	}

	public String send(String uri, Object message) {
		Future<BasicPoolEntry> future = pool.lease(httpHost, null);

		boolean reusable = false;
		BasicPoolEntry entry = null;
		HttpPost request = null;
		HttpClientConnection conn = null;
		HttpCoreContext coreContext = null;
		try {
			entry = future.get();
			conn = entry.getConnection();
			conn.setSocketTimeout(timeOut * 1000);
			coreContext = HttpCoreContext.create();
			coreContext.setTargetHost(httpHost);

			request = new HttpPost(formatURI(uri));
			request.setEntity(new StringEntity(formatSendMsg(message), "utf-8"));
			LOGGER.debug(String.format(
					"Request URI: [%s], use connection: [%s]", request
							.getRequestLine().getUri(), conn.toString()));
			httpexecutor.preProcess(request, processor, coreContext);
		} catch (Exception e) {
			pool.closeExpired();
			throw new RuntimeException(String.format(
					"Send msg to [%s] use connection [%s] failed.",
					this.httpHost.toString(),
					conn == null ? "null" : conn.toString()), e);
		}
		try {

			HttpResponse response = httpexecutor.execute(request, conn,
					coreContext);
			httpexecutor.postProcess(response, processor, coreContext);
			LOGGER.debug(String.format("Response: [%s]",
					response.getStatusLine()));
			String resultMsg = EntityUtils.toString(response.getEntity());
			reusable = connStrategy.keepAlive(response, coreContext);
			return resultMsg;
		} catch (Exception e) {
			pool.closeExpired();
			throw new RuntimeException(String.format(
					"Recv msg from [%s] use connection [%s] failed.",
					this.httpHost.toString(), conn.toString()), e);
		} finally {
			pool.release(entry, reusable);
		}
	}

	public void setMaxPerRoute(int maxPerRoute) {
		this.maxPerRoute = maxPerRoute;
		this.pool.setDefaultMaxPerRoute(this.maxPerRoute);
	}


	public void setValidateAfterInactivity(int validateAfterInactivity) {
		this.validateAfterInactivity = validateAfterInactivity;
		this.pool.setValidateAfterInactivity(validateAfterInactivity);
	}

	private String formatSendMsg(Object message) {
		if (message instanceof String) {
			return (String) message;
		}
		return JSON.toJSONString(message,
						new SerializerFeature[] { SerializerFeature.DisableCircularReferenceDetect });
	}

	private String formatURI(String uri) {
		String slant = "/";
		if (uri == null || uri.length() == 0) {
			return slant;
		}
		if (uri.startsWith(slant)) {
			return uri;
		}
		return slant + uri;
	}

	private HttpHost buildHttpHost(String host) {
		int index = host.indexOf(":");
		if (index > 0) {
			return new HttpHost(host.substring(0, index), Integer.parseInt(host
					.substring(index + 1)));
		}
		return new HttpHost(host);
	}

	private ConnectionConfig buildConnectionConfig() {
		return ConnectionConfig.custom().setBufferSize(BUFFER_SIZE)
				.setCharset(Charset.forName("utf-8")).build();
	}

	private SocketConfig buildSocketConfig() {
		return SocketConfig.copy(SocketConfig.DEFAULT)
				.setSndBufSize(BUFFER_SIZE).setRcvBufSize(BUFFER_SIZE)
				.setSoKeepAlive(true).build();
	}

	public void setHosturi(String hosturi) {
		this.hosturi = hosturi;
	}

	public String getHosturi() {
		return hosturi;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public void setEnvironment(Environment environment) {
		// TODO Auto-generated method stub
		this.env = environment;
	}
}
