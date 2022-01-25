#!/bin/sh -x
dir=$(cd `dirname $0`;pwd)
echo $dir
mvn clean package -Dmaven.test.skip=true && \
java -javaagent:/Users/wangji/dev/skywalking-agent/skywalking-agent.jar -Dskywalking.agent.service_name=provider \
-jar $dir/dubbo-provider/target/dubbo-provider.jar

