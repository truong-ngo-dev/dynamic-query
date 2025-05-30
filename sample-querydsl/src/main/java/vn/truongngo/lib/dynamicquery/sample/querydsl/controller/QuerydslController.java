package vn.truongngo.lib.dynamicquery.sample.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.truongngo.lib.dynamicquery.sample.querydsl.projection.EmployeeProjection;
import vn.truongngo.lib.dynamicquery.sample.querydsl.service.ProjectionQueryService;
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
    private final ProjectionQueryService projectionQueryService;

//    @GetMapping("/test")
//    private List<LinkedHashMap<String, Object>> test() {
//        return querydslSqlService.testJoinSubQuery();
//    }

    @GetMapping("/test")
    private List<EmployeeProjection> test() {
        return projectionQueryService.testProjectionQuery1();
    }
}
