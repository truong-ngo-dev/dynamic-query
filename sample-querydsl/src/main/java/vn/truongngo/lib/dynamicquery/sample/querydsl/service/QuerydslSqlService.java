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
import vn.truongngo.lib.dynamicquery.querydsl.sql.builder.QuerydslSqlStrategy;
import vn.truongngo.lib.dynamicquery.sample.QCompanies;
import vn.truongngo.lib.dynamicquery.sample.QEmployees;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static vn.truongngo.lib.dynamicquery.core.support.Expressions.column;
import static vn.truongngo.lib.dynamicquery.core.support.Expressions.entity;
import static vn.truongngo.lib.dynamicquery.core.support.Predicates.equal;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuerydslSqlService {

    private final DataSource dataSource;

    private QueryBuilder<SQLQuery<Tuple>> prepareQueryBuilder() {
        QueryBuilderStrategy<SQLQuery<Tuple>> qbs = new QuerydslSqlStrategy<>(dataSource);
        return new DefaultQueryBuilder<>(qbs);
    }

    @SuppressWarnings("all")
    public List<LinkedHashMap<String, Object>> testJoinQuery() {

        QueryBuilder<SQLQuery<Tuple>> qb = prepareQueryBuilder();
        QuerySource employee = entity(QEmployees.class);
        QuerySource company = entity(QCompanies.class);

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
}
