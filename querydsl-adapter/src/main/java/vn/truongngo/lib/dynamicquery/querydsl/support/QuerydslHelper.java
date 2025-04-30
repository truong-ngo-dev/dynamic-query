package vn.truongngo.lib.dynamicquery.querydsl.support;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;
import vn.truongngo.lib.dynamicquery.core.enumerate.Order;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderSpecifier;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.Restriction;
import vn.truongngo.lib.dynamicquery.querydsl.converter.QuerydslVisitor;

import java.util.Map;

/**
 * Utility class that provides static helper methods to apply metadata-driven dynamic queries
 * to Querydsl's {@link JPQLQuery} object using a {@link QuerydslVisitor} to convert abstract
 * expression models into concrete QueryDSL expressions.
 *
 * @author Truong Ngo
 * @version 1.0
 */
public class QuerydslHelper {

    /**
     * Applies select clauses from metadata to the query.
     *
     * @param paths     path mapping for alias resolution
     * @param query     the QueryDSL JPQLQuery to apply select on
     * @param metadata  dynamic query metadata
     * @param visitor   visitor to convert expression model to QueryDSL
     */
    public static void select(Map<String, Path<?>> paths, JPQLQuery<?> query, QueryMetadata metadata, QuerydslVisitor visitor) {
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
    public static void join(Map<String, Path<?>> pathBuilders, JPQLQuery<?> query, QueryMetadata metadata, QuerydslVisitor visitor) {
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
    public static void where(Map<String, Path<?>> pathBuilders, JPQLQuery<?> query, QueryMetadata metadata, QuerydslVisitor visitor) {
        if (!metadata.getWhereClauses().isEmpty()) {
            for (vn.truongngo.lib.dynamicquery.core.expression.predicate.Predicate p : metadata.getWhereClauses()) {
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
    public static void groupBy(Map<String, Path<?>> pathBuilders, JPQLQuery<?> query, QueryMetadata metadata, QuerydslVisitor visitor) {
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
    public static void having(Map<String, Path<?>> pathBuilders, JPQLQuery<?> query, QueryMetadata metadata, QuerydslVisitor visitor) {
        if (!metadata.getHavingClauses().isEmpty()) {
            for (vn.truongngo.lib.dynamicquery.core.expression.predicate.Predicate p : metadata.getHavingClauses()) {
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
    public static void orderBy(Map<String, Path<?>> pathBuilders, JPQLQuery<?> query, QueryMetadata metadata, QuerydslVisitor visitor) {
        if (!metadata.getOrderByClauses().isEmpty()) {
            for (OrderSpecifier o : metadata.getOrderByClauses()) {
                vn.truongngo.lib.dynamicquery.core.expression.Expression e = o.getTarget();
                Expression<?> expression = visitor.visit(e, pathBuilders);
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

}
