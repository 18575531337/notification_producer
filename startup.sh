#!/bin/bash
echo 欢迎进入数据调度系统作者By 张骏飞!

DIR="${BASH_SOURCE-$0}"
DIR=`dirname ${BASH_SOURCE-$0}`

ROOT_HOME=$DIR;export ROOT_HOME

RESOURCES_HOME=$DIR/resources/;export RESOURCES_HOME

LIB_HOME=$DIR/lib;export LIB_HOME

APP_CLASSPATH=$ROOT_HOME:$RESOURCES_HOME:$LIB_HOME/*;export APP_CLASSPATH

#export LC_ALL=zh_CN.UTF8

echo $APP_CLASSPATH

MAINPROG=com.haizhi.np.dispatch.portal.Startup

start(){
    #nohup java -Xms512m -Xmx1g -Xss256k -jar ./target/notification-provider-1.0.0-SNAPSHOT.jar > /dev/null 2>&1 &
    nohup java -Xms512m -Xmx1g -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:-OmitStackTraceInFastThrow -classpath $APP_CLASSPATH $MAINPROG >/dev/null 2>&1 &
    if [ $? -eq 0 ]
    then
        echo "[`date`] Startup $MAINPROG success."
        return 0
    else
        echo "[`date`] Startup $MAINPROG fail."
        return 1
    fi
}

stop(){
	echo stop!
    #ps -ef | grep notification-provider | grep -v grep | awk '{print $2}' | xargs kill
    #ps -ef | grep $MAINPROG | grep -v grep | awk '{print $2}' | xargs kill
    echo "[`date`] Begin stop $MAINPROG... "
    PROGID=`ps -ef|grep "$MAINPROG"|grep -v "grep"|sed -n '1p'|awk '{print $2" "$3}'`
	if [ -z "$PROGID" ]
	then
		echo "[`date`] Stop $MAINPROG fail, service is not exist."
		return 1
	fi

    kill -9 $PROGID
    if [ $? -eq 0 ]
    then
		echo "[`date`] Stop $MAINPROG success."
		return 0
    else
		echo "[`date`] Stop $MAINPROG fail."
		return 1
    fi
}

all(){
    ps -ef | grep $MAINPROG
}

case $1 in
start)
    echo 数据调度系统开始运行!
    start
    exit $?
    ;;
stop)
    echo 数据调度系统结束!
    stop
    exit $?
    ;;
all)
    all
    ;;
*)
    echo 请附带参数 start 或者 stop !
    ;;
esac



