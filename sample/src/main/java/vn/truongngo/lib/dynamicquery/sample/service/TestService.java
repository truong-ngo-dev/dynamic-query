package vn.truongngo.lib.dynamicquery.sample.service;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.truongngo.lib.dynamicquery.core.expression.SubqueryExpression;
import vn.truongngo.lib.dynamicquery.querydsl.builder.QuerydslQueryBuilder;
import vn.truongngo.lib.dynamicquery.sample.entity.Company;
import vn.truongngo.lib.dynamicquery.sample.entity.Employee;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {

    private final EntityManager em;

    public List<LinkedHashMap<String, Object>> testJoinQuery() {
        QuerydslQueryBuilder<Tuple> qb = new QuerydslQueryBuilder<>(em);

        qb
                .from(Employee.class)
                .join(b -> b
                        .join(qb.entity(Company.class))
                        .on(qb.equal(
                                qb.column("companyId", Employee.class),
                                qb.column("id", Company.class)))
                        .as(Company.class.getSimpleName()
                ))
                .select("firstName", "lastName")
                .select(qb.column("name", Company.class));

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
                        .join(qb.entity(Employee.class))
                        .on(qb.equal(
                                qb.column("companyId", Employee.class),
                                qb.column("id", Company.class)
                        ))
                        .as(Employee.class.getSimpleName()))
                .select("id", "name")
                .select(qb.function("count", "totalEmployee", qb.column("id", Employee.class)))
                .groupBy(qb.column("id", Company.class), qb.column("name", Company.class));

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
                .where(qb.in(
                qb.column("id", Company.class),
                (SubqueryExpression) qb.subquery(b -> b
                        .from(Employee.class)
                        .select(qb.column("companyId", Employee.class))
                        .where(qb.equal(qb.column("id", Employee.class), 1))
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
