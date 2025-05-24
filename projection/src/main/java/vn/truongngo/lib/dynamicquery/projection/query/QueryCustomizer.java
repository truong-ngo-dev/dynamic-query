package vn.truongngo.lib.dynamicquery.projection.query;

import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;

/**
 * Interface for customizing a {@link QueryMetadata} instance before the query is built.
 * <p>
 * Implementations can modify the query metadata by adding additional predicates,
 * changing selections, joins, orders, or any other query-related configuration.
 * </p>
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface QueryCustomizer {

    /**
     * Customizes the given {@link QueryMetadata} instance.
     *
     * @param queryMetadata the query metadata to customize
     */
    void customize(QueryMetadata queryMetadata);
}
