package com.example.test.controller;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.skywalking.apm.toolkit.trace.CallableWrapper;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.test.DemoService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author wangji
 * @date 2022/1/25 10:33 上午
 */
@Controller
@Slf4j
public class DemoController {

    @DubboReference(version = "1.0.0", url = "dubbo://127.0.0.1:12345")
    private DemoService        demoService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    private ExecutorService    executorService = Executors.newFixedThreadPool(10);

    private InnerCallable      innerCallable;

    @PostConstruct
    public void init() {
        innerCallable = new InnerCallable(demoService);
    }

    @ResponseBody
    @GetMapping("/sayHello")
    public String sayHello(@RequestParam(required = false, defaultValue = "hello name") String name) {
        log.info("get url{} {}", httpServletRequest.getRequestURI(), name);
        return demoService.sayHello(name);
    }

    @ResponseBody
    @GetMapping("/sayHelloAcrossThread")
    public String sayHelloAcrossThread(@RequestParam(required = false, defaultValue = "hello name") String name) throws ExecutionException,
                                                                                                                 InterruptedException {
        log.info("get url{}", httpServletRequest.getRequestURI());

        final Future<String> submit = executorService.submit(CallableWrapper.of(new Callable<String>() {

            @Override
            public String call() throws Exception {
                return demoService.sayHello(name);
            }
        }));
        return submit.get();
    }

    /**
     * 没有包装 CallableWrapper.of 不生效，跨线程失败
     * 
     * @param name
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @ResponseBody
    @GetMapping("/sayHelloNotAcrossThread")
    public String sayHelloNotAcrossThread(@RequestParam(required = false, defaultValue = "hello name") String name) throws ExecutionException,
                                                                                                                    InterruptedException {
        log.info("get url{}", httpServletRequest.getRequestURI());

        final Future<String> submit = executorService.submit(new Callable<String>() {

            @Override
            public String call() throws Exception {
                return demoService.sayHello(name);
            }
        });
        return submit.get();
    }

    /**
     * 跨线程 没有重新new Callable 加上了 @TraceCrossThread 也不会生效，这个处理逻辑根据 @TraceCrossThread 处理的,检查有这个注解的构造方法
     *  https://skywalking.apache.org/docs/skywalking-java/latest/en/setup/service-agent/java-agent/application-toolkit-trace-cross-thread/
     * @param name
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @ResponseBody
    @GetMapping("/sayHelloInnerRunnableNotNewConStruct")
    public String sayHelloInnerRunnableNotNewConStruct(@RequestParam(required = false, defaultValue = "hello name") String name) throws ExecutionException,
                                                                                                                                 InterruptedException {
        log.info("get url{}", httpServletRequest.getRequestURI());

        // 基于构造函数处理 这种不行...没有新new 一个callback sky agent 内部会处理哦...
        // https://skywalking.apache.org/docs/skywalking-java/latest/en/setup/service-agent/java-agent/application-toolkit-trace-cross-thread/
        final Future<String> submit = executorService.submit(innerCallable);
        return submit.get();
    }

    @ResponseBody
    @GetMapping("/sayHelloInnerRunnableNewConStruct")
    public String sayHelloInnerRunnableNewConStruct(@RequestParam(required = false, defaultValue = "hello name") String name) throws ExecutionException,
                                                                                                                              InterruptedException {
        log.info("get url{}", httpServletRequest.getRequestURI());

        // https://skywalking.apache.org/docs/skywalking-java/latest/en/setup/service-agent/java-agent/application-toolkit-trace-cross-thread/
        final Future<String> submit = executorService.submit(new InnerCallable(demoService));
        return submit.get();
    }

    @TraceCrossThread
    public static class InnerCallable implements Callable<String> {

        private DemoService demoService;

        public InnerCallable(DemoService demoService){
            this.demoService = demoService;
        }

        @Override
        @Trace(operationName = "test InnerRunnable")
        public String call() throws Exception {
            return demoService.sayHello("hello");
        }

    }

}
