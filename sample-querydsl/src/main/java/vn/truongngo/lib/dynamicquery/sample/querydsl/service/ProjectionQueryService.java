package vn.truongngo.lib.dynamicquery.sample.querydsl.service;

import com.querydsl.core.Fetchable;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.sql.SQLQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.truongngo.lib.dynamicquery.core.builder.QueryBuilderStrategy;
import vn.truongngo.lib.dynamicquery.projection.query.ProjectionQueryExecutor;
import vn.truongngo.lib.dynamicquery.projection.query.ProjectionQueryFactory;
import vn.truongngo.lib.dynamicquery.querydsl.jpa.jpql.QuerydslJpaStrategy;
import vn.truongngo.lib.dynamicquery.querydsl.projection.QuerydslProjectionQueryExecutor;
import vn.truongngo.lib.dynamicquery.querydsl.sql.QuerydslSqlStrategy;
import vn.truongngo.lib.dynamicquery.sample.querydsl.projection.EmployeeCriteria;
import vn.truongngo.lib.dynamicquery.sample.querydsl.projection.EmployeeProjection;

import javax.sql.DataSource;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectionQueryService {

    private final EntityManager em;
    private final DataSource dataSource;

    public List<EmployeeProjection> testProjectionQuery() {
        QueryBuilderStrategy<JPAQuery<Tuple>> qbs = new QuerydslJpaStrategy<>(em);
        ProjectionQueryFactory<JPAQuery<Tuple>> queryFactory = new ProjectionQueryFactory<>(qbs);
        EmployeeCriteria criteria = EmployeeCriteria.builder().firstName("Truong").ids("Bank").build();
        JPAQuery<Tuple> query = queryFactory.createQuery(EmployeeProjection.class, criteria);
        ProjectionQueryExecutor<Fetchable<Tuple>> executor = QuerydslProjectionQueryExecutor.getInstance();
        return executor.execute(query, EmployeeProjection.class);
    }

    public List<EmployeeProjection> testProjectionQuery1() {
        QueryBuilderStrategy<SQLQuery<Tuple>> qbs = new QuerydslSqlStrategy<>(dataSource, true);
        ProjectionQueryFactory<SQLQuery<Tuple>> queryFactory = new ProjectionQueryFactory<>(qbs);
        EmployeeCriteria criteria = EmployeeCriteria.builder().firstName("Truong").ids("Bank").build();
        SQLQuery<Tuple> query = queryFactory.createQuery(EmployeeProjection.class, criteria);
        ProjectionQueryExecutor<Fetchable<Tuple>> executor = QuerydslProjectionQueryExecutor.getInstance();
        return executor.execute(query, EmployeeProjection.class);
    }
}
