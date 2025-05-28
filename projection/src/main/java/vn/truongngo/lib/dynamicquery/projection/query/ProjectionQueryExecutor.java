package vn.truongngo.lib.dynamicquery.projection.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * An abstraction for executing dynamically built queries and mapping the results to projection classes.
 * <p>
 * This executor is intended to work with queries generated via {@code ProjectionQueryFactory}
 * and execute them using the underlying query framework (e.g., QueryDSL, jOOQ, JPA).
 * It also handles mapping the query result to the provided projection class.
 * </p>
 *
 * @param <Q> the type of query object (e.g., {@code SQLQuery}, {@code JPAQuery}, etc.)
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface ProjectionQueryExecutor<Q> {

    /**
     * Executes the given query and returns the result mapped to instances of the specified projection class.
     *
     * @param <P> the type of the projection result
     * @param query the query object to be executed
     * @param projectionClass the class representing the projection type to map results t
     * @return a list of projection results
     */
    <P> List<P> execute(Q query, Class<P> projectionClass);

    /**
     * Executes the given query with pagination and returns the result mapped to instances of the specified projection class.
     *
     * @param <P> the type of the projection result
     * @param query the query object to be executed
     * @param projectionClass the class representing the projection type to map results to
     * @param pageable the pagination information, including page number and size
     * @return a {@link org.springframework.data.domain.Page} containing the paginated projection results
     */
    <P> Page<P> execute(Q query, Class<P> projectionClass, Pageable pageable);

}
