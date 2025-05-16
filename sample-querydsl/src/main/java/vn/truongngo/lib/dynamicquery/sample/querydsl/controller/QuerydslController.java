package vn.truongngo.lib.dynamicquery.sample.querydsl.controller;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.truongngo.lib.dynamicquery.sample.querydsl.service.QuerydslJpaService;
import vn.truongngo.lib.dynamicquery.sample.querydsl.service.QuerydslSqlService;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuerydslController {

    private final QuerydslJpaService querydslJpaService;
    private final QuerydslSqlService querydslSqlService;

    @GetMapping("/test")
    private List<Tuple> test() {
        return querydslSqlService.testJoinSubQuery();
    }
}
