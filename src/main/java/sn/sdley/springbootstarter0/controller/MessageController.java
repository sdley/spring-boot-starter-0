package sn.sdley.springbootstarter0.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.sdley.springbootstarter0.entities.Message;

@RestController
@Hidden
public class MessageController {

    @RequestMapping("/hello")
    public Message sayHello() {
        return new Message("Hello World!");
    }
}
