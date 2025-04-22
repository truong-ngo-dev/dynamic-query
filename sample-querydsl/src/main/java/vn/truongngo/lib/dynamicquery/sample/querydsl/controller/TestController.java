package vn.truongngo.lib.dynamicquery.sample.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.truongngo.lib.dynamicquery.sample.querydsl.service.TestService;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TestController {

    private final TestService testService;

    @GetMapping("/test")
    private List<LinkedHashMap<String, Object>> test() {
        return testService.testCountQuery();
    }
}
