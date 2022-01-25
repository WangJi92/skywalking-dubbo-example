## skywalking 安装
[skywalking 安装地址](https://skywalking.apache.org/downloads/)

### 1、下载apm 安装包
* [apache-skywalking-apm-8.9.1.tar.gz](https://www.apache.org/dyn/closer.cgi/skywalking/8.9.1/apache-skywalking-apm-8.9.1.tar.gz)

### 2、启动服务端 oapService &  webappService

```bash 
cd apache-skywalking-apm-bin/bin
bash startup.sh 
```
修改配置文件地址: apache-skywalking-apm-bin/config/application.yml
#### 修改webappService 端口
default webappService 界面服务 端口 8080 http://127.0.0.1:8080/trace 需要修改 /apache-skywalking-apm-bin/webapp/webapp.yml 可以进入就行修改

#### 修改oapService 存储
oapService 默认存储使用 SW_STORAGE:h2 ,如果使用mysql or other ，修改一下具体的配置项即可，mysql 会自动创建表结构哦，需要把jdbc的 apache-skywalking-apm-bin/oap-libs/mysql-connector-java-8.0.28.jar 放在这个目录哦。

### 3、客户端agent 设置

* 下载Java agent [apache-skywalking-java-agent-8.8.0.tgz](https://www.apache.org/dyn/closer.cgi/skywalking/java-agent/8.8.0/apache-skywalking-java-agent-8.8.0.tgz)

* 启动vm options 增加配置
  
  skywalking.agent.service_name=provider 为应用的名称

```bash
-javaagent:/Users/wangji/dev/skywalking-agent/skywalking-agent.jar -Dskywalking.agent.service_name=provider
```
 
![idea 启动配置vm options](pic/vm-config.png)

* 启动 consumer & provider


## 访问

### 访问consumer 接口

```bash
ab -n 100 -c 10  http://127.0.0.1:8081/sayHello?name=123
```

### 查看trace(webappService) & 本地日志

#### 进入sky 管理界面

[http://127.0.0.1:8080/trace](http://127.0.0.1:8080/trace)

#### 查看客户端日志
[How To Print trace ID in your logs In Sky](https://skywalking.apache.org/docs/skywalking-java/v8.8.0/en/setup/service-agent/java-agent/application-toolkit-logback-1.x/)
```xml
2022-01-25 19:17:01.735 [TID:de98a5b85f5e476ba249ed66edc06bd0.73.16431094211580001] [DubboServerHandler-30.11.176.38:12345-thread-2] INFO  c.e.test.provider.DemoServiceImpl -[19:17:01] Hello 13446555, request from consumer: /30.11.176.38:62365
2022-01-25 19:17:07.009 [TID:de98a5b85f5e476ba249ed66edc06bd0.75.16431094270040001] [DubboServerHandler-30.11.176.38:12345-thread-3] INFO  c.e.test.provider.DemoServiceImpl -[19:17:07] Hello 123, request from consumer: /30.11.176.38:62365
```




