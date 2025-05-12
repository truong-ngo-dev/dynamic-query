package vn.truongngo.lib.dynamicquery.sample.querydsl.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.truongngo.lib.dynamicquery.core.builder.DefaultQueryBuilder;
import vn.truongngo.lib.dynamicquery.core.builder.QueryBuilder;
import vn.truongngo.lib.dynamicquery.core.builder.QueryBuilderStrategy;
import vn.truongngo.lib.dynamicquery.core.expression.QuerySource;
import vn.truongngo.lib.dynamicquery.querydsl.jpa.jpql.builder.QuerydslJpaStrategy;
import vn.truongngo.lib.dynamicquery.sample.querydsl.entity.Company;
import vn.truongngo.lib.dynamicquery.sample.querydsl.entity.Employee;

import static vn.truongngo.lib.dynamicquery.core.support.Expressions.*;
import static vn.truongngo.lib.dynamicquery.core.support.Predicates.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuerydslJpaService {

    private final EntityManager em;

    private QueryBuilder<JPAQuery<Tuple>> prepareQueryBuilder() {
        QueryBuilderStrategy<JPAQuery<Tuple>> qbs = new QuerydslJpaStrategy<>(em);
        return new DefaultQueryBuilder<>(qbs);
    }

    public List<LinkedHashMap<String, Object>> testJoinQuery() {

        QueryBuilder<JPAQuery<Tuple>> qb = prepareQueryBuilder();
        QuerySource employee = entity(Employee.class);
        QuerySource company = entity(Company.class);

        qb
                .from(employee)
                .join(b -> b
                        .join(company)
                        .on(equal(
                                column("companyId", employee),
                                column("id", company)))
                        )
                .select("firstName", "lastName")
                .select(column("name", company));

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

        QueryBuilder<JPAQuery<Tuple>> qb = prepareQueryBuilder();
        QuerySource employee = entity(Employee.class);
        QuerySource company = entity(Company.class);

        qb
                .from(company)
                .join(b -> b
                        .join(entity(Employee.class))
                        .on(equal(
                                column("companyId", employee),
                                column("id", company)
                        )))
                .select("id", "name")
                .select(count(column("id", employee)).as("totalEmployee"))
                .groupBy(
                        column("id", employee),
                        column("name", company));

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

        QueryBuilder<JPAQuery<Tuple>> qb = prepareQueryBuilder();
        QuerySource employee = entity(Employee.class);
        QuerySource company = entity(Company.class);

        qb
                .from(company)
                .select("id", "name")
                .where(in(
                        column("id", company),
                        subquery(b -> b
                                .from(employee)
                                .select(column("companyId", employee))
                                .where(equal(column("id", employee), 1))
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
