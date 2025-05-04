package vn.truongngo.lib.dynamicquery.querydsl.jpa.jpql.builder;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import vn.truongngo.lib.dynamicquery.core.builder.QueryBuilderStrategy;
import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;
import vn.truongngo.lib.dynamicquery.querydsl.jpa.jpql.support.QuerydslJpaExpressionHelper;
import vn.truongngo.lib.dynamicquery.querydsl.jpa.jpql.support.QuerydslJpaHelper;

import java.util.Map;

/**
 * Query builder strategy implementation using QueryDSL JPA.
 *
 * <p>This class implements {@link QueryBuilderStrategy} for building dynamic JPQL queries
 * using QueryDSL's {@link JPAQuery}. It converts abstract {@link QueryMetadata} into concrete
 * QueryDSL query components by leveraging {@link QuerydslJpaVisitor} and utility helpers.</p>
 *
 * <p>Internally, this strategy performs the following:
 * <ul>
 *   <li>Resolves entity paths and aliases</li>
 *   <li>Creates a {@code JPAQuery} from the {@link EntityManager}</li>
 *   <li>Applies dynamic select, join, where, group by, having, order by, and restriction clauses</li>
 * </ul>
 * </p>
 *
 * @param <T> the result type of the JPAQuery
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@RequiredArgsConstructor
public class QuerydslJpaStrategy<T> implements QueryBuilderStrategy<JPAQuery<T>> {

    /**
     * Entity manager instance
     * */
    private final EntityManager em;

    /**
     * The singleton instance of the {@link QuerydslJpaVisitor}, used to convert expression models into QueryDSL expressions.
     */
    private final QuerydslJpaVisitor visitor = QuerydslJpaVisitor.getInstance();

    /**
     * Builds a {@link JPAQuery} based on the given {@link QueryMetadata}.
     *
     * <p>This method resolves the root path from metadata, creates a JPAQuery with the provided
     * {@link EntityManager}, sets the root entity source, and applies all dynamic clauses
     * using {@link QuerydslJpaHelper} utilities.</p>
     *
     * @param queryMetadata the query metadata containing select, join, filter, and other clauses
     * @return a fully constructed {@link JPAQuery} object ready for execution
     */
    @Override
    public JPAQuery<T> accept(QueryMetadata queryMetadata) {
        Map<String, Path<?>> pathBuilders = QuerydslJpaExpressionHelper.getSources(queryMetadata);
        String key = queryMetadata.getAlias() == null
                ? queryMetadata.getEntityClass().getSimpleName()
                : queryMetadata.getAlias();
        PathBuilder<?> root = (PathBuilder<?>) pathBuilders.get(key);
        JPAQuery<T> query = new JPAQuery<>(em);
        query.from(root);
        QuerydslJpaHelper.buildQuery(queryMetadata, pathBuilders, query, visitor);
        return query;
    }
}
