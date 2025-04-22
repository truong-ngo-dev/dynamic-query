package vn.truongngo.lib.dynamicquery.querydsl.builder;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import vn.truongngo.lib.dynamicquery.core.builder.AbstractQueryBuilder;
import vn.truongngo.lib.dynamicquery.querydsl.converter.QuerydslVisitor;
import vn.truongngo.lib.dynamicquery.querydsl.support.QuerydslExpressionHelper;
import vn.truongngo.lib.dynamicquery.querydsl.support.QuerydslHelper;

import java.util.Map;

/**
 * A QueryDSL-based query builder implementation for constructing JPA queries dynamically.
 * This builder leverages the visitor pattern to convert dynamic query metadata into QueryDSL
 * components like selections, joins, where conditions, group by, having, order by, and restrictions.
 *
 * <blockquote><pre>
 * // Example usage:
 * QuerydslQueryBuilder&lt;MyEntity&gt; queryBuilder = new QuerydslQueryBuilder&lt;&gt;(entityManager);
 * JPAQuery&lt;MyEntity&gt; query = queryBuilder.build();
 * </pre></blockquote>
 *
 * @param <T> the type of the entity that the query will return
 * @see AbstractQueryBuilder
 * @see JPAQuery
 * @since 1.0
 * @version 1.0
 */
@RequiredArgsConstructor
public class QuerydslQueryBuilder<T> extends AbstractQueryBuilder<T> {

    private final EntityManager em;
    private final QuerydslVisitor visitor = new QuerydslVisitor();

    /**
     * Builds a dynamic JPAQuery based on the metadata from the query.
     * The query is constructed by applying dynamic selections, joins, conditions, etc.
     *
     * <p>This method delegates the construction to various helper methods such as {@link QuerydslHelper#select},
     * {@link QuerydslHelper#join}, {@link QuerydslHelper#where}, etc., which populate the query based on the query
     * metadata provided.</p>
     *
     * @return a dynamically built JPAQuery that can be executed with the EntityManager
     */
    @Override
    public JPAQuery<T> build() {
        Map<String, Path<?>> pathBuilders = QuerydslExpressionHelper.getSources(getQueryMetadata());
        String key = getQueryMetadata().getAlias() == null ? getQueryMetadata().getEntityClass().getSimpleName() : getQueryMetadata().getAlias();
        PathBuilder<?> root = (PathBuilder<?>) pathBuilders.get(key);
        JPAQuery<T> query = new JPAQuery<>(em);
        query.from(root);

        // Apply selections, joins, conditions, and other query clauses dynamically
        QuerydslHelper.select(pathBuilders, query, getQueryMetadata(), visitor);
        QuerydslHelper.join(pathBuilders, query, getQueryMetadata(), visitor);
        QuerydslHelper.where(pathBuilders, query, getQueryMetadata(), visitor);
        QuerydslHelper.groupBy(pathBuilders, query, getQueryMetadata(), visitor);
        QuerydslHelper.having(pathBuilders, query, getQueryMetadata(), visitor);
        QuerydslHelper.orderBy(pathBuilders, query, getQueryMetadata(), visitor);
        QuerydslHelper.restriction(query, getQueryMetadata());

        return query;
    }
}
