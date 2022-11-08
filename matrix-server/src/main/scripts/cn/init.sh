#!/bin/bash

ENV="cn"
PORT="18080"
PROJECT_DIR="/home/kafka/pkg/matrix/MatrixServer"
JAR_NAME="matrix-server.jar"

JVM_ARGS="-Xms2048m -Xmx2048m -Xmn512m -Xss256k -XX:MaxMetaspaceSize=256m -XX:MetaspaceSize=256m \
            -Dfile.encoding=UTF-8 \
            -XX:+UseConcMarkSweepGC \
            -XX:+UseParNewGC \
            -XX:+UseCMSCompactAtFullCollection \
            -XX:CMSFullGCsBeforeCompaction=0 \
            -XX:CMSInitiatingOccupancyFraction=65 \
            -XX:SurvivorRatio=8 \
            -XX:+UseCMSInitiatingOccupancyOnly \
            -XX:+CMSScavengeBeforeRemark \
            -XX:+ParallelRefProcEnabled \
            -XX:+CMSClassUnloadingEnabled \
            -XX:+DisableExplicitGC \
            -XX:+PrintGCDateStamps \
            -XX:+PrintGCDetails \
            -Xloggc:$PROJECT_DIR/logs/gc.log \
            -XX:+HeapDumpOnOutOfMemoryError \
            -XX:HeapDumpPath=$PROJECT_DIR/logs/heapdump.hprof"
SPRING_ARGS="-Dspring.profiles.active=$ENV \
            -Dserver.port=$PORT"

function getPID(){
    ps aux | grep $JAR_NAME | grep java | awk '{print $2}'
}

function start(){
    ln -s $PROJECT_DIR/../logs logs

    PID=$(getPID)
    if [[ ! -z $PID ]]; then
        echo "Already running. PID: $PID"
        return 0
    fi

    cmd="java $JVM_ARGS $SPRING_ARGS -jar $JAR_NAME"
    echo $cmd

    nohup $cmd > /dev/null &

    echo "Starting.$(getPID)"
}

function stop(){
    PID=$(getPID)
    if [[ -z $PID ]]; then
        echo "Already stoped."
        return 0
    fi
    echo "kill $PID"
    kill $PID
    while [[ ! -z $(getPID) ]]; do 
        echo "stoping...$PID"
        sleep 1
    done

    echo "stoped."
}

function restart(){
    stop
    start
}

case $1 in
    "start") start ;;
    "stop") stop ;;
    "restart") restart ;;
    *) echo "Usage: {start|stop|restart}" ;;
esac

exit 0