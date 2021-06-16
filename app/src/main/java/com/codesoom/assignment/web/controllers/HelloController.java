package com.codesoom.assignment.web.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API가 가동되고 있음을 알리는 응답 메시지를 반환합니다.
 */
@RestController
public class HelloController {
    /**
     * 인사 메시지를 반환합니다.
     * @return String 인사 메시지
     */
    @RequestMapping("/")
    public String sayHello() {
        return "Hello, world!";
    }
}
