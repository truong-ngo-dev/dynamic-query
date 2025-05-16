package vn.truongngo.lib.dynamicquery.sample.querydsl.service;

import com.querydsl.core.Tuple;
import com.querydsl.sql.SQLQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.truongngo.lib.dynamicquery.core.builder.DefaultQueryBuilder;
import vn.truongngo.lib.dynamicquery.core.builder.QueryBuilder;
import vn.truongngo.lib.dynamicquery.core.builder.QueryBuilderStrategy;
import vn.truongngo.lib.dynamicquery.core.expression.QuerySource;
import vn.truongngo.lib.dynamicquery.core.expression.SubqueryExpression;
import vn.truongngo.lib.dynamicquery.core.support.Expressions;
import vn.truongngo.lib.dynamicquery.querydsl.sql.QuerydslSqlStrategy;
import vn.truongngo.lib.dynamicquery.sample.querydsl.entity.Company;
import vn.truongngo.lib.dynamicquery.sample.querydsl.entity.Employee;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static vn.truongngo.lib.dynamicquery.core.support.Expressions.*;
import static vn.truongngo.lib.dynamicquery.core.support.Predicates.equal;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuerydslSqlService {

    private final DataSource dataSource;

    private QueryBuilder<SQLQuery<Tuple>> prepareSqlBuilder() {
        QueryBuilderStrategy<SQLQuery<Tuple>> qbs = new QuerydslSqlStrategy<>(dataSource, false);
        return new DefaultQueryBuilder<>(qbs);
    }

    private QueryBuilder<SQLQuery<Tuple>> prepareJpaNativeBuilder() {
        QueryBuilderStrategy<SQLQuery<Tuple>> qbs = new QuerydslSqlStrategy<>(dataSource, true);
        return new DefaultQueryBuilder<>(qbs);
    }

    @SuppressWarnings("all")
    public List<LinkedHashMap<String, Object>> testJoinQuery() {

        QueryBuilder<SQLQuery<Tuple>> qb = prepareJpaNativeBuilder();
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

        SQLQuery<Tuple> sqlQuery = qb.build();
        List<Tuple> tuples = sqlQuery.fetch();
        log.info(sqlQuery.toString());

        List<LinkedHashMap<String, Object>> rs = new ArrayList<>();
        for (Tuple tuple : tuples) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("firstName", tuple.get(0, Object.class));
            map.put("lastName", tuple.get(1, Object.class));
            map.put("companyName", tuple.get(2, Object.class));
            rs.add(map);
        }
        return rs;
    }

    public List<Tuple> testJoinSubQuery() {

        QueryBuilder<SQLQuery<Tuple>> qb = prepareJpaNativeBuilder();
        QuerySource employee = entity(Employee.class).as("e");
        QuerySource company = entity(Company.class).as("c");

        QuerySource subEmp = entity(Employee.class).as("se");

        SubqueryExpression subquery = Expressions.subquery(b -> b
                .from(subEmp)
                .select(column("companyId", subEmp)).as("company_id")
                .select(avg(column("salary", subEmp)).as("avg_salary"))
                .groupBy(column("companyId", subEmp)))
                .as("salary_tb");

        qb
                .from(employee)
                .select("id")
                .select(column("name", company))
                .select(column("avg_salary", subquery))
                .join(b -> b
                        .join(company)
                        .on(equal(
                                column("companyId", employee),
                                column("id", company))))
                .join(b -> b
                        .join(subquery)
                        .on(equal(
                                column("id", company),
                                column("avg_salary", subquery)
                        )));

        SQLQuery<Tuple> sqlQuery = qb.build();
        log.info(sqlQuery.toString());
        List<Tuple> tuples = sqlQuery.fetch();

        return tuples;
    }
}
