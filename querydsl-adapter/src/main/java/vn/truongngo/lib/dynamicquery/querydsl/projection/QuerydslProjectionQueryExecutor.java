package vn.truongngo.lib.dynamicquery.querydsl.projection;

import com.querydsl.core.Fetchable;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.truongngo.lib.dynamicquery.projection.query.ProjectionQueryExecutor;

import java.util.List;

public class QuerydslProjectionQueryExecutor implements ProjectionQueryExecutor<Fetchable<Tuple>> {

private static final QuerydslProjectionQueryExecutor INSTANCE = new QuerydslProjectionQueryExecutor();

    private QuerydslProjectionQueryExecutor() {}

    public static QuerydslProjectionQueryExecutor getInstance() {
        return INSTANCE;
    }

    @Override
    public <P> List<P> execute(Fetchable<Tuple> query, Class<P> projectionClass) {
        List<Tuple> queryResults = query.fetch();
        return queryResults.stream().map(t -> QuerydslProjectionMapper.mapTupleToProjection(t, projectionClass)).toList();
    }

    @Override
    public <P> Page<P> execute(Fetchable<Tuple> query, Class<P> projectionClass, Pageable pageable) {
        return null;
    }
}
