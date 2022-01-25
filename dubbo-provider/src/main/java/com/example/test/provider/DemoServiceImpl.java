package com.example.test.provider;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import com.example.test.DemoService;
import org.apache.dubbo.rpc.RpcContext;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 调用日志
 * 
 * @author wangji
 * @date 2022/1/25 10:28 上午
 */
@DubboService(version = "1.0.0")
@Slf4j
public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {
        log.info("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] Hello " + name
                 + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name + ", response from provider: " + RpcContext.getContext().getLocalAddress();
    }
}
