package vn.truongngo.lib.dynamicquery.querydsl.support;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;
import vn.truongngo.lib.dynamicquery.core.enumerate.Operator;
import vn.truongngo.lib.dynamicquery.querydsl.converter.QuerydslVisitor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class to support conversion between dynamic query metadata and QueryDSL constructs.
 *
 * <p>This class provides methods to:
 * <ul>
 *   <li>Create {@link PathBuilder} from entity metadata</li>
 *   <li>Convert join clauses and subqueries into {@link JPQLQuery}</li>
 *   <li>Generate QueryDSL predicates from custom predicate models</li>
 * </ul>
 *
 * <blockquote><pre>
 * Example:
 * QueryMetadata metadata = ...;
 * JPQLQuery<?> query = QuerydslExpressionHelper.convertToJpqlQuery(metadata);
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 1.0
 */
public class QuerydslExpressionHelper {

    /**
     * Creates a {@link PathBuilder} for the given class and alias.
     *
     * @param clazz the entity class
     * @param alias the alias to use; if {@code null}, simple class name is used
     * @return a path builder representing the entity
     */
    public static PathBuilder<?> getPathBuilder(Class<?> clazz, String alias) {
        String as = (alias == null) ? clazz.getSimpleName() : alias;
        return new PathBuilder<>(clazz, as);
    }

    /**
     * Extracts all path sources (main and joined entities) from the given metadata.
     *
     * @param queryMetadata the query metadata
     * @return a map of alias-to-path representations
     */
    public static Map<String, Path<?>> getSources(QueryMetadata queryMetadata) {
        Map<String, Path<?>> sources = new LinkedHashMap<>();

        PathBuilder<?> pathBuilder = getPathBuilder(queryMetadata.getEntityClass(), queryMetadata.getAlias());
        String key = (queryMetadata.getAlias() == null) ? queryMetadata.getEntityClass().getSimpleName() : queryMetadata.getAlias();
        sources.put(key, pathBuilder);

        List<JoinExpression> joins = queryMetadata.getJoinClauses();
        if (!joins.isEmpty()) {
            for (JoinExpression joinExpression : joins) {
                EntityReferenceExpression entityRef = (EntityReferenceExpression) joinExpression.target();
                PathBuilder<?> joinPath = getPathBuilder(entityRef.getEntityClass(), entityRef.getAlias());
                String joinKey = (entityRef.getAlias() == null) ? entityRef.getEntityClass().getSimpleName() : entityRef.getAlias();
                sources.put(joinKey, joinPath);

//                if (joinExpression.target() instanceof EntityReferenceExpression entityRef) {                                               // Querydsl currently not support
//                    PathBuilder<?> joinPath = getPathBuilder(entityRef.getEntityClass(), entityRef.getAlias());                             // join with subquery
//                    String joinKey = (entityRef.getAlias() == null) ? entityRef.getEntityClass().getSimpleName() : entityRef.getAlias();
//                    sources.put(joinKey, joinPath);
//                } else {
//                    SubqueryExpression sq = (SubqueryExpression) joinExpression.target();
//                    JPQLQuery<?> subquery = convertToJpqlQuery(sq.getQueryMetadata());
//                    @SuppressWarnings("rawtypes")
//                    SimplePath<? extends JPQLQuery> sqPath = Expressions.path(subquery.getClass(), sq.getAlias());
//                    sources.put(sq.getAlias(), sqPath);
//                }
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
    public static SubQueryExpression<?> convertToQuerydslSubquery(QueryMetadata metadata) {
        return convertToJpqlQuery(metadata);
    }

    /**
     * Converts dynamic query metadata into a QueryDSL {@link JPQLQuery}.
     *
     * @param metadata the query metadata
     * @return a QueryDSL JPQLQuery object representing the query
     */
    public static JPQLQuery<?> convertToJpqlQuery(QueryMetadata metadata) {
        QuerydslVisitor visitor = new QuerydslVisitor();
        Map<String, Path<?>> sources = getSources(metadata);
        JPQLQuery<?> subquery = JPAExpressions.select();
        String alias = (metadata.getAlias() == null) ? metadata.getEntityClass().getSimpleName() : metadata.getAlias();
        subquery.from((EntityPath<?>) sources.get(alias));
        QuerydslHelper.select(sources, subquery, metadata, visitor);
        QuerydslHelper.join(sources, subquery, metadata, visitor);
        QuerydslHelper.where(sources, subquery, metadata, visitor);
        QuerydslHelper.groupBy(sources, subquery, metadata, visitor);
        QuerydslHelper.having(sources, subquery, metadata, visitor);
        QuerydslHelper.orderBy(sources, subquery, metadata, visitor);
        return subquery;
    }

    /**
     * Converts a dynamic {@link ComparisonPredicate} to a QueryDSL {@link Predicate}.
     *
     * @param expression the comparison predicate
     * @param left       the left-hand side of the expression
     * @param right      the right-hand side of the expression
     * @return a QueryDSL predicate
     */
    public static Predicate getComparisionPredicate(ComparisonPredicate expression, Expression<?> left, Expression<?> right) {
        Operator operator = expression.getOperator();
        return switch (operator) {
            case EQUAL -> Expressions.predicate(Ops.EQ, left, right);
            case NOT_EQUAL -> Expressions.predicate(Ops.NE, left, right);
            case GREATER_THAN -> Expressions.predicate(Ops.GT, left, right);
            case LESS_THAN -> Expressions.predicate(Ops.LT, left, right);
            case GREATER_THAN_EQUAL -> Expressions.predicate(Ops.GOE, left, right);
            case LESS_THAN_EQUAL -> Expressions.predicate(Ops.LOE, left, right);
            case LIKE -> Expressions.predicate(Ops.LIKE, left, right);
            case NOT_LIKE -> Expressions.predicate(Ops.LIKE, left, right).not();
            case BETWEEN -> Expressions.predicate(Ops.BETWEEN, left, right);
            case NOT_BETWEEN -> Expressions.predicate(Ops.BETWEEN, left, right).not();
            case IS_NULL -> Expressions.predicate(Ops.IS_NULL, left);
            case IS_NOT_NULL -> Expressions.predicate(Ops.IS_NOT_NULL, left);
            case EXISTS -> Expressions.predicate(Ops.EXISTS, left);
            case NOT_EXISTS -> Expressions.predicate(Ops.EXISTS, left).not();
            case IN -> Expressions.predicate(Ops.IN, left, right);
            case NOT_IN -> Expressions.predicate(Ops.NOT_IN, left, right);
        };
    }
}
