package sn.sdley.springbootstarter0.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import sn.sdley.springbootstarter0.config.AppProperties;

@Controller
class HomeController {

    private final String appName;
    private final AppProperties appProperties;

    HomeController(@Value("${spring.application.name}") String appName,
                   AppProperties appProperties) {
        this.appName = appName;
        this.appProperties = appProperties;
    }

    @GetMapping("/")
    String index() {
        System.out.println("Application Name: " + appName);
        System.out.println("Application Page Size: " + appProperties.getPage().getSize());
        return "forward:/index.html";
    }
}
