package vn.truongngo.lib.dynamicquery.sample.querydsl.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.truongngo.lib.dynamicquery.core.expression.SubqueryExpression;
import vn.truongngo.lib.dynamicquery.querydsl.builder.QuerydslQueryBuilder;
import vn.truongngo.lib.dynamicquery.sample.querydsl.entity.Company;
import vn.truongngo.lib.dynamicquery.sample.querydsl.entity.Employee;
import static vn.truongngo.lib.dynamicquery.core.support.Expressions.*;
import static vn.truongngo.lib.dynamicquery.core.support.Predicates.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuerydslService {

    private final EntityManager em;

    public List<LinkedHashMap<String, Object>> testJoinQuery() {
        QuerydslQueryBuilder<Tuple> qb = new QuerydslQueryBuilder<>(em);

        qb
                .from(Employee.class)
                .join(b -> b
                        .join(entity(Company.class))
                        .on(equal(
                                column("companyId", Employee.class),
                                column("id", Company.class)))
                        .as(Company.class.getSimpleName()
                        ))
                .select("firstName", "lastName")
                .select(column("name", Company.class));

        JPAQuery<Tuple> jpaQuery = qb.build();
        List<Tuple> tuples = jpaQuery.fetch();
        List<LinkedHashMap<String, Object>> rs = new ArrayList<>();
        for (Tuple tuple : tuples) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("firstName", tuple.get(0, Object.class));
            map.put("lastName", tuple.get(1, Object.class));
            map.put("companyName", tuple.get(0, Object.class));
            rs.add(map);
        }
        return rs;
    }

    public List<LinkedHashMap<String, Object>> testCountQuery() {
        QuerydslQueryBuilder<Tuple> qb = new QuerydslQueryBuilder<>(em);
        qb
                .from(Company.class)
                .join(b -> b
                        .join(entity(Employee.class))
                        .on(equal(
                                column("companyId", Employee.class),
                                column("id", Company.class)
                        ))
                        .as(Employee.class.getSimpleName()))
                .select("id", "name")
                .select(function("count", "totalEmployee", column("id", Employee.class)))
                .groupBy(
                        column("id", Company.class),
                        column("name", Company.class));

        JPAQuery<Tuple> jpaQuery = qb.build();
        List<Tuple> tuples = jpaQuery.fetch();
        List<LinkedHashMap<String, Object>> rs = new ArrayList<>();
        for (Tuple tuple : tuples) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("id", tuple.get(0, Object.class));
            map.put("name", tuple.get(1, Object.class));
            map.put("employeesNum", tuple.get(2, Object.class));
            rs.add(map);
        }
        return rs;
    }

    public List<LinkedHashMap<String, Object>> testSubQuery() {
        QuerydslQueryBuilder<Tuple> qb = new QuerydslQueryBuilder<>(em);

        qb
                .from(Company.class)
                .select("id", "name")
                .where(in(
                        column("id", Company.class),
                        (SubqueryExpression) subquery(b -> b
                                .from(Employee.class)
                                .select(column("companyId", Employee.class))
                                .where(equal(column("id", Employee.class), 1))
                )
        ));

        JPAQuery<Tuple> jpaQuery = qb.build();
        List<Tuple> tuples = jpaQuery.fetch();
        List<LinkedHashMap<String, Object>> rs = new ArrayList<>();
        for (Tuple tuple : tuples) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("id", tuple.get(0, Object.class));
            map.put("name", tuple.get(1, Object.class));
            rs.add(map);
        }
        return rs;
    }

}
