package vn.truongngo.lib.dynamicquery.querydsl.jpa.jpql.support;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;
import vn.truongngo.lib.dynamicquery.core.enumerate.Order;
import vn.truongngo.lib.dynamicquery.core.expression.EntityReferenceExpression;
import vn.truongngo.lib.dynamicquery.core.expression.JoinExpression;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderExpression;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.Restriction;
import vn.truongngo.lib.dynamicquery.querydsl.jpa.jpql.builder.QuerydslJpaVisitor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class that provides static helper methods to apply metadata-driven dynamic queries
 * to Querydsl's {@link JPQLQuery} object using a {@link QuerydslJpaVisitor} to convert abstract
 * expression models into concrete QueryDSL expressions.
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public class QuerydslJpaHelper {

    /**
     * Applies select clauses from metadata to the query.
     *
     * @param paths     path mapping for alias resolution
     * @param query     the QueryDSL JPQLQuery to apply select on
     * @param metadata  dynamic query metadata
     * @param visitor   visitor to convert expression model to QueryDSL
     */
    public static void select(Map<String, Path<?>> paths, JPQLQuery<?> query, QueryMetadata metadata, QuerydslJpaVisitor visitor) {
        Expression<?>[] selects = metadata
                .getSelectClauses()
                .stream()
                .map(e -> visitor.visit(e, paths))
                .toArray(Expression[]::new);
        query.select(selects);
    }

    /**
     * Applies join clauses from metadata to the query.
     *
     * @param pathBuilders  path mapping for alias resolution
     * @param query         the QueryDSL JPQLQuery to apply join on
     * @param metadata      dynamic query metadata
     * @param visitor       visitor to convert expression model to QueryDSL
     */
    public static void join(Map<String, Path<?>> pathBuilders, JPQLQuery<?> query, QueryMetadata metadata, QuerydslJpaVisitor visitor) {
        if (!metadata.getJoinClauses().isEmpty()) {
            for (JoinExpression joinExpression : metadata.getJoinClauses()) {
                Predicate onCondition = (Predicate) visitor.visit(joinExpression.condition(), pathBuilders);
                PathBuilder<?> joinTarget = (PathBuilder<?>) visitor.visit(joinExpression.target(), pathBuilders);      // Currently querydsl doesn't support join with subquery
                switch (joinExpression.joinType()) {                                                                    // Define entity view to archive this purpose
                    case INNER_JOIN -> query.innerJoin(joinTarget);
                    case LEFT_JOIN -> query.leftJoin(joinTarget);
                    case RIGHT_JOIN -> query.rightJoin(joinTarget);
                    default -> throw new IllegalStateException("Unexpected join type: " + joinExpression.joinType());
                }

                query.on(onCondition);
            }
        }
    }

    /**
     * Applies where predicates from metadata to the query.
     *
     * @param pathBuilders  path mapping for alias resolution
     * @param query         the QueryDSL JPQLQuery to apply where conditions
     * @param metadata      dynamic query metadata
     * @param visitor       visitor to convert expression model to QueryDSL
     */
    public static void where(Map<String, Path<?>> pathBuilders, JPQLQuery<?> query, QueryMetadata metadata, QuerydslJpaVisitor visitor) {
        if (!metadata.getWhereClauses().isEmpty()) {
            for (vn.truongngo.lib.dynamicquery.core.expression.Predicate p : metadata.getWhereClauses()) {
                Predicate predicate = (Predicate) visitor.visit(p, pathBuilders);
                query.where(predicate);
            }
        }
    }

    /**
     * Applies group-by expressions from metadata to the query.
     *
     * @param pathBuilders  path mapping for alias resolution
     * @param query         the QueryDSL JPQLQuery to apply group-by
     * @param metadata      dynamic query metadata
     * @param visitor       visitor to convert expression model to QueryDSL
     */
    public static void groupBy(Map<String, Path<?>> pathBuilders, JPQLQuery<?> query, QueryMetadata metadata, QuerydslJpaVisitor visitor) {
        if (!metadata.getGroupByClauses().isEmpty()) {
            for (vn.truongngo.lib.dynamicquery.core.expression.Expression g : metadata.getGroupByClauses()) {
                Expression<?> expression = visitor.visit(g, pathBuilders);
                query.groupBy(expression);
            }
        }
    }

    /**
     * Applies having predicates from metadata to the query.
     *
     * @param pathBuilders  path mapping for alias resolution
     * @param query         the QueryDSL JPQLQuery to apply having
     * @param metadata      dynamic query metadata
     * @param visitor       visitor to convert expression model to QueryDSL
     */
    public static void having(Map<String, Path<?>> pathBuilders, JPQLQuery<?> query, QueryMetadata metadata, QuerydslJpaVisitor visitor) {
        if (!metadata.getHavingClauses().isEmpty()) {
            for (vn.truongngo.lib.dynamicquery.core.expression.Predicate p : metadata.getHavingClauses()) {
                Predicate predicate = (Predicate) visitor.visit(p, pathBuilders);
                query.having(predicate);
            }
        }
    }

    /**
     * Applies order-by clauses from metadata to the query.
     *
     * @param pathBuilders  path mapping for alias resolution
     * @param query         the QueryDSL JPQLQuery to apply ordering
     * @param metadata      dynamic query metadata
     * @param visitor       visitor to convert expression model to QueryDSL
     */
    public static void orderBy(Map<String, Path<?>> pathBuilders, JPQLQuery<?> query, QueryMetadata metadata, QuerydslJpaVisitor visitor) {
        if (!metadata.getOrderByClauses().isEmpty()) {
            for (OrderExpression o : metadata.getOrderByClauses()) {
                Expression<?> expression = visitor.visit(o.getTarget(), pathBuilders);
                com.querydsl.core.types.Order order = o.getOrder().equals(Order.ASC) ?
                        com.querydsl.core.types.Order.ASC :
                        com.querydsl.core.types.Order.DESC;
                @SuppressWarnings("all")
                com.querydsl.core.types.OrderSpecifier<?> op = new com.querydsl.core.types.OrderSpecifier(order, expression);
                query.orderBy(op);
            }
        }
    }

    /**
     * Applies paging restriction from metadata to the query using {@code limit} and {@code offset}.
     *
     * @param query     the QueryDSL JPQLQuery to apply restrictions
     * @param metadata  dynamic query metadata
     */
    public static void restriction(JPQLQuery<?> query, QueryMetadata metadata) {
        Restriction restriction = metadata.getRestriction();
        if (restriction.isPaging()) {
            query.limit(restriction.getSize());
            query.offset(restriction.getOffset());
        }
    }

    /**
     * Constructs a complete {@link JPQLQuery} by sequentially applying various clauses based on the
     * provided {@link QueryMetadata}. This includes select, join, where, group by, having, and order by
     * clauses.
     *
     * <p>
     * This method serves as a centralized utility to assemble a dynamic query by delegating to specific
     * helper methods for each clause. It ensures that all relevant parts of the query are constructed
     * in the correct order.
     * </p>
     *
     * @param metadata the {@link QueryMetadata} containing all components of the dynamic query
     * @param sources  a mapping of alias names to {@link Path} objects used for resolving references
     * @param subquery the {@link JPQLQuery} instance being constructed
     * @param visitor  the {@link QuerydslJpaVisitor} responsible for converting abstract expressions to
     *                 concrete QueryDSL expressions
     */
    public static void buildQuery(QueryMetadata metadata, Map<String, Path<?>> sources, JPQLQuery<?> subquery, QuerydslJpaVisitor visitor) {
        QuerydslJpaHelper.select(sources, subquery, metadata, visitor);
        QuerydslJpaHelper.join(sources, subquery, metadata, visitor);
        QuerydslJpaHelper.where(sources, subquery, metadata, visitor);
        QuerydslJpaHelper.groupBy(sources, subquery, metadata, visitor);
        QuerydslJpaHelper.having(sources, subquery, metadata, visitor);
        QuerydslJpaHelper.orderBy(sources, subquery, metadata, visitor);
    }

    /**
     * Extracts all path sources (main and joined entities) from the given metadata.
     *
     * @param queryMetadata the query metadata
     * @return a map of alias-to-path representations
     */
    public static Map<String, Path<?>> getSourcesContext(QueryMetadata queryMetadata) {
        Map<String, Path<?>> sources = new LinkedHashMap<>();
        EntityReferenceExpression from = (EntityReferenceExpression) queryMetadata.getFrom();
        PathBuilder<?> pathBuilder = new PathBuilder<>(from.getEntityClass(), from.getAlias());
        sources.put(from.getAlias(), pathBuilder);

        List<JoinExpression> joins = queryMetadata.getJoinClauses();
        if (!joins.isEmpty()) {
            for (JoinExpression joinExpression : joins) {
                EntityReferenceExpression target = (EntityReferenceExpression) joinExpression.target();
                PathBuilder<?> joinPath = new PathBuilder<>(target.getEntityClass(), target.getAlias());
                sources.put(target.getAlias(), joinPath);
            }
        }

        return sources;
    }

    /**
     * Converts dynamic query metadata into a QueryDSL {@link SubQueryExpression}.
     *
     * @param metadata the query metadata
     * @return a QueryDSL subquery expression
     */
    public static SubQueryExpression<?> subquery(QueryMetadata metadata) {
        QuerydslJpaVisitor visitor = new QuerydslJpaVisitor();
        Map<String, Path<?>> sources = getSourcesContext(metadata);
        JPQLQuery<?> subquery = JPAExpressions.select();
        subquery.from((EntityPath<?>) sources.get(metadata.getFrom().getAlias()));
        QuerydslJpaHelper.buildQuery(metadata, sources, subquery, visitor);
        return subquery;
    }
}
