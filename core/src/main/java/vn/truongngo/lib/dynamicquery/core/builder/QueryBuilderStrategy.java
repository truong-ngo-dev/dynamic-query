package vn.truongngo.lib.dynamicquery.core.builder;

/**
 * Strategy interface for building query objects from {@link QueryMetadata}.
 * <p>
 * Implementations of this interface are responsible for converting the abstract query metadata
 * into a concrete query object of type {@code Q}, using a specific query engine (e.g., QueryDSL, jOOQ).
 * <p>
 * This enables pluggable query building logic and allows the core builder to remain independent
 * of any specific query engine.
 *
 * @param <Q> The type of the final query object produced by the strategy (e.g., a QueryDSL query, jOOQ DSLContext).
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface QueryBuilderStrategy<Q> {

    /**
     * Accepts the given {@link QueryMetadata} and produces a query object of type {@code Q}.
     *
     * @param queryMetadata The metadata representing the structure and configuration of the query.
     * @return A query object built from the provided metadata.
     */
    Q accept(QueryMetadata queryMetadata);

}
