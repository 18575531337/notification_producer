package com.haizhi.np.dispatch.portal;


import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.util.StringUtils;

/**
 *	@author zhangjunfei
 */
@Configuration
@ComponentScan(basePackages="com.haizhi.np.dispatch")
@EnableAspectJAutoProxy
@EnableTransactionManagement 
@EnableScheduling
@PropertySource({"classpath:/prop/jdbc.properties"
	,"classpath:/prop/http.properties"
	,"classpath:/prop/scheduled.properties"})
@ImportResource({"spring/spring-mongodb.xml"
	/*,"spring/springConfig.xml"*/})
public class SpringConfig implements EnvironmentAware{
	
	private static Logger logger = LogManager.getLogger(SpringConfig.class);

    private Environment env;
    
    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name="mySqlDataSource",destroyMethod="close")
    public ComboPooledDataSource mySqlDataSource(){
    	ComboPooledDataSource dataSource =new ComboPooledDataSource();
    	try {
    		dataSource.setDriverClass(env.getProperty("ds.mysql.driverClass"));
			dataSource.setJdbcUrl(env.getProperty("ds.mysql.jdbcUrl"));
			dataSource.setUser(env.getProperty("ds.mysql.username"));
			dataSource.setPassword(env.getProperty("ds.mysql.password"));
			
			logger.info("启动MYSQL数据库连接URL ："+env.getProperty("ds.mysql.jdbcUrl")
					+"            ;           用户名 : "+env.getProperty("ds.mysql.username"));
			
			dataSource.setMaxPoolSize(Integer.valueOf(env.getProperty("ds.mysql.maxPoolSize")));
			dataSource.setMaxIdleTime(Integer.valueOf(env.getProperty("ds.mysql.maxIdleTime")));
			dataSource.setMaxStatementsPerConnection(Integer.valueOf(env.getProperty("ds.mysql.maxStatementsPerConnection")));
			dataSource.setNumHelperThreads(Integer.valueOf(env.getProperty("ds.mysql.numHelperThreads")));
			
			dataSource.setIdleConnectionTestPeriod(Integer.valueOf(env.getProperty("ds.mysql.idleConnectionTestPeriod")));
			dataSource.setTestConnectionOnCheckin(Boolean.valueOf(env.getProperty("ds.mysql.testConnectionOnCheckin")));
			dataSource.setPreferredTestQuery(env.getProperty("ds.mysql.preferredTestQuery"));
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return dataSource;
    }
    
    @Bean(name="mySqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactory(){
    	SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();

    	Resource[] resources = null;
    	Resource mybatisConfig = null;
		try {
			resources = new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/**/*.xml");
			mybatisConfig = new PathMatchingResourcePatternResolver().getResource("classpath:mybatis/mybatis-config.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	sqlSessionFactory.setMapperLocations(resources);
    	sqlSessionFactory.setConfigLocation(mybatisConfig);
    	sqlSessionFactory.setDataSource(mySqlDataSource());
    	
    	return sqlSessionFactory;
    }
    
    @Bean(name="mapperScannerConfigurer")
    public MapperScannerConfigurer MapperScannerConfigurer(){
    	MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
    	mapperScannerConfigurer.setSqlSessionFactoryBeanName("mySqlSessionFactory");
    	mapperScannerConfigurer.setBasePackage("com.haizhi.np.dispatch.mapper");
    	return mapperScannerConfigurer;
    }
    
    @Bean(name="txManager")
    public DataSourceTransactionManager txManager(){
    	DataSourceTransactionManager txManager = new DataSourceTransactionManager();
    	txManager.setDataSource(mySqlDataSource());
    	return txManager;
    }
    
    @Bean(name="mongoClient")
    public MongoClient mongoClient(){
    	MongoClientOptions.Builder build = new MongoClientOptions.Builder();
    	//与目标数据库能够建立的最大connection数量为50
    	build.connectionsPerHost(env.getProperty("mongo.connectionsPerHost",Integer.class)); 
        //如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
        build.threadsAllowedToBlockForConnectionMultiplier(
        		env.getProperty("mongo.threadsAllowedToBlockForConnectionMultiplier",Integer.class)); 
        /*
         * 一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为2分钟
         * 这里比较危险，如果超过maxWaitTime都没有获取到这个连接的话，该线程就会抛出Exception
         * 故这里设置的maxWaitTime应该足够大，以免由于排队线程过多造成的数据库访问失败
         */
        build.maxWaitTime(env.getProperty("mongo.maxWaitTime",Integer.class));
        //与数据库建立连接的timeout设置为1分钟
        build.connectTimeout(env.getProperty("mongo.connectTimeout",Integer.class));

		MongoCredential mongoCredential = MongoCredential.createCredential(
				env.getProperty("mongo.user"),
				env.getProperty("mongo.defaultDbName"),
				env.getProperty("mongo.pwd").toCharArray());
		List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();

		credentialsList.add(mongoCredential);

    	//MongoClient mongoClient = new MongoClient(env.getProperty("mongo.host"), build.build());
		/*
    	ServerAddress address = new ServerAddress(env.getProperty("mongo.host"),
    			env.getProperty("mongo.port",Integer.class));
    	

    	
    	MongoClient mongoClient = new MongoClient(address, credentialsList, build.build());
*/

		List<ServerAddress> serverList = new ArrayList<ServerAddress>();
		String mongoHost = env.getProperty("mongo.host");
		for(String itemHost : mongoHost.split(";")){
			if(StringUtils.isEmpty(itemHost)){
				break;
			}
			String[] mongoAuth =  itemHost.split(":");
			if(StringUtils.isEmpty(mongoAuth[0]) || StringUtils.isEmpty(mongoAuth[1])){
				break;
			}
			//logger.debug(itemHost);
			serverList.add(new ServerAddress(mongoAuth[0], Integer.valueOf(mongoAuth[1])));
		}
		MongoClient mongoClient = new MongoClient(serverList, credentialsList, build.build());
		//MongoClientURI mongoURI = new MongoClientURI(env.getProperty("mongo.uri"));
		//MongoClient mongoClient = new MongoClient(mongoURI);
    	
    	logger.info("启动MONGODB 链接 ："+env.getProperty("mongo.host")
    		+":"+env.getProperty("mongo.port",Integer.class)+"          用户:"+env.getProperty("mongo.user")
    		+"       数据库名:"+env.getProperty("mongo.defaultDbName"));
    	
    	return mongoClient;
    }
    
    @Bean(name="mongoDbFactory")
    public MongoDbFactory mongoDbFactory(){
    	MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient(),
    			env.getProperty("mongo.defaultDbName"));
    	return mongoDbFactory;
    }

	public void setEnvironment(Environment environment) {
		// TODO Auto-generated method stub
		this.env=environment;
	}
}