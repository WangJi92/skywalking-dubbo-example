package com.example.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.test.DemoService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wangji
 * @date 2022/1/25 10:33 上午
 */
@Controller
@Slf4j
public class DemoController {

    @DubboReference(version = "1.0.0", url = "dubbo://127.0.0.1:12345")
    private DemoService demoService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @ResponseBody
    @GetMapping("/sayHello")
    public String sayHello(@RequestParam String name) {
        log.info("get url{} {}",httpServletRequest.getRequestURI(),name);
        return demoService.sayHello(name);
    }

}
