package vn.truongngo.lib.dynamicquery.querydsl.projection;

import com.querydsl.core.Fetchable;
import com.querydsl.core.SimpleQuery;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.truongngo.lib.dynamicquery.projection.query.ProjectionQueryExecutor;

import java.util.List;

/**
 * Singleton executor that handles the execution of Querydsl projection queries returning tuples,
 * and maps the results into projection objects of the specified type.
 * <p>
 * This executor supports execution of queries that return {@link Tuple} results,
 * and converts each tuple into an instance of the given projection class using
 * {@link QuerydslProjectionMapper}.
 * </p>
 *
 * <p><strong>Usage example:</strong></p>
 * <blockquote><pre>
 * Fetchable&lt;Tuple&gt; query = ...; // Build your Querydsl tuple query
 * List&lt;MyProjection&gt; results = QuerydslProjectionQueryExecutor.getInstance()
 *     .execute(query, MyProjection.class);
 * </pre></blockquote>
 *
 * <p>Note: The paged query execution method is currently not implemented and returns null.</p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public class QuerydslProjectionQueryExecutor implements ProjectionQueryExecutor<Fetchable<Tuple>> {

private static final QuerydslProjectionQueryExecutor INSTANCE = new QuerydslProjectionQueryExecutor();

    private QuerydslProjectionQueryExecutor() {}

    /**
     * Returns the singleton instance of this executor.
     *
     * @return the singleton instance
     */
    public static QuerydslProjectionQueryExecutor getInstance() {
        return INSTANCE;
    }

    /**
     * Executes the given Querydsl tuple query and maps the result to a list of projection instances.
     *
     * @param <P> the type of the projection
     * @param query the Querydsl query returning tuples to execute
     * @param projectionClass the class of the projection to map each tuple to
     * @return a list of projection instances mapped from the query result tuples
     */
    @Override
    public <P> List<P> execute(Fetchable<Tuple> query, Class<P> projectionClass) {
        List<Tuple> queryResults = query.fetch();
        return queryResults.stream()
                .map(t -> QuerydslProjectionMapper.mapTupleToProjection(t, projectionClass))
                .toList();
    }

    /**
     * Executes the given Querydsl tuple query with pagination support.
     * <p>
     * This method is currently not implemented and returns {@code null}.
     * </p>
     *
     * @param <P> the type of the projection
     * @param query the Querydsl query returning tuples to execute
     * @param projectionClass the class of the projection to map each tuple to
     * @param pageable the pagination information
     * @return a page of projection instances, or {@code null} if not implemented
     */
    @Override
    public <P> Page<P> execute(Fetchable<Tuple> query, Class<P> projectionClass, Pageable pageable) {
        return null;
    }
}
