package vn.truongngo.lib.dynamicquery.querydsl.sql.support;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;
import vn.truongngo.lib.dynamicquery.core.enumerate.Order;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderExpression;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.Restriction;
import vn.truongngo.lib.dynamicquery.querydsl.common.context.QuerydslSource;
import vn.truongngo.lib.dynamicquery.querydsl.sql.builder.QuerydslSqlVisitor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper to build querydsl SQLQuery object
 * @author Truong Ngo
 * @version 2.0.0
 * */
public class QuerydslSqlHelper {

    /**
     * Applies SELECT expressions from metadata to the given SQL query.
     *
     * @param paths     a mapping of aliases to {@link QuerydslSource}, used for resolving column references
     * @param query     the QueryDSL {@link SQLQuery} to apply SELECT clauses to
     * @param metadata  the dynamic query metadata containing SELECT expressions
     * @param visitor   the visitor responsible for converting expressions to QueryDSL {@link Expression}s
     */
    public static void select(Map<String, QuerydslSource> paths, SQLQuery<?> query, QueryMetadata metadata, QuerydslSqlVisitor visitor) {
        Expression<?>[] selects = metadata
                .getSelectClauses()
                .stream()
                .map(e -> visitor.visit(e, paths))
                .toArray(Expression[]::new);
        query.select(selects);
    }

    /**
     * Applies JOIN clauses from metadata to the given SQL query.
     *
     * <p>Supports both entity joins and subquery joins. The appropriate join type (LEFT, INNER, RIGHT)
     * is applied based on the metadata.</p>
     *
     * @param paths     a mapping of aliases to {@link QuerydslSource}, used for resolving join targets
     * @param query     the QueryDSL {@link SQLQuery} to apply JOIN clauses to
     * @param metadata  the dynamic query metadata containing JOIN expressions
     * @param visitor   the visitor used to convert ON conditions to QueryDSL {@link Predicate}s
     */
    public static void join(Map<String, QuerydslSource> paths, SQLQuery<?> query, QueryMetadata metadata, QuerydslSqlVisitor visitor) {
        if (!metadata.getJoinClauses().isEmpty()) {
            for (JoinExpression joinExpression : metadata.getJoinClauses()) {
                Predicate onCondition = (Predicate) visitor.visit(joinExpression.condition(), paths);

                QuerySource source = joinExpression.target();
                QuerydslSource dslSource = paths.get(source.getAlias());

                if (source instanceof EntityReferenceExpression) {
                    RelationalPath<?> entityPath = (RelationalPath<?>) dslSource.getSource();
                    switch (joinExpression.joinType()) {
                        case LEFT_JOIN -> query.leftJoin(entityPath);
                        case INNER_JOIN -> query.innerJoin(entityPath);
                        case RIGHT_JOIN -> query.rightJoin(entityPath);
                        default -> throw new IllegalStateException("Unexpected join type: " + joinExpression.joinType());
                    }
                } else {
                    SubQueryExpression<?> subquery = (SubQueryExpression<?>) dslSource.getSource();
                    Path<?> subqueryPath = dslSource.getAlias();
                    switch (joinExpression.joinType()) {
                        case LEFT_JOIN -> query.leftJoin(subquery, subqueryPath);
                        case INNER_JOIN -> query.innerJoin(subquery, subqueryPath);
                        case RIGHT_JOIN -> query.rightJoin(subquery, subqueryPath);
                        default -> throw new IllegalStateException("Unexpected join type: " + joinExpression.joinType());
                    }
                }



                query.on(onCondition);
            }
        }
    }

    /**
     * Applies WHERE predicates from metadata to the given SQL query.
     *
     * @param pathBuilders  a mapping of aliases to {@link QuerydslSource}, used for resolving column references
     * @param query         the QueryDSL {@link SQLQuery} to apply WHERE conditions to
     * @param metadata      the dynamic query metadata containing WHERE predicates
     * @param visitor       the visitor used to convert predicates to QueryDSL {@link Predicate}s
     */
    public static void where(Map<String, QuerydslSource> pathBuilders, SQLQuery<?> query, QueryMetadata metadata, QuerydslSqlVisitor visitor) {
        if (!metadata.getWhereClauses().isEmpty()) {
            for (vn.truongngo.lib.dynamicquery.core.expression.Predicate p : metadata.getWhereClauses()) {
                Predicate predicate = (Predicate) visitor.visit(p, pathBuilders);
                query.where(predicate);
            }
        }
    }

    /**
     * Applies GROUP BY expressions from metadata to the given SQL query.
     *
     * @param pathBuilders  a mapping of aliases to {@link QuerydslSource}, used for resolving expressions
     * @param query         the QueryDSL {@link SQLQuery} to apply GROUP BY clauses to
     * @param metadata      the dynamic query metadata containing GROUP BY expressions
     * @param visitor       the visitor used to convert expressions to QueryDSL {@link Expression}s
     */
    public static void groupBy(Map<String, QuerydslSource> pathBuilders, SQLQuery<?> query, QueryMetadata metadata, QuerydslSqlVisitor visitor) {
        if (!metadata.getGroupByClauses().isEmpty()) {
            for (vn.truongngo.lib.dynamicquery.core.expression.Expression g : metadata.getGroupByClauses()) {
                Expression<?> expression = visitor.visit(g, pathBuilders);
                query.groupBy(expression);
            }
        }
    }

    /**
     * Applies HAVING predicates from metadata to the given SQL query.
     *
     * @param pathBuilders  a mapping of aliases to {@link QuerydslSource}, used for resolving expressions
     * @param query         the QueryDSL {@link SQLQuery} to apply HAVING clauses to
     * @param metadata      the dynamic query metadata containing HAVING predicates
     * @param visitor       the visitor used to convert predicates to QueryDSL {@link Predicate}s
     */
    public static void having(Map<String, QuerydslSource> pathBuilders, SQLQuery<?> query, QueryMetadata metadata, QuerydslSqlVisitor visitor) {
        if (!metadata.getHavingClauses().isEmpty()) {
            for (vn.truongngo.lib.dynamicquery.core.expression.Predicate p : metadata.getHavingClauses()) {
                Predicate predicate = (Predicate) visitor.visit(p, pathBuilders);
                query.having(predicate);
            }
        }
    }

    /**
     * Applies ORDER BY expressions from metadata to the given SQL query.
     *
     * @param pathBuilders  a mapping of aliases to {@link QuerydslSource}, used for resolving expressions
     * @param query         the QueryDSL {@link SQLQuery} to apply ORDER BY clauses to
     * @param metadata      the dynamic query metadata containing ORDER BY expressions
     * @param visitor       the visitor used to convert order targets to QueryDSL {@link Expression}s
     */
    public static void orderBy(Map<String, QuerydslSource> pathBuilders, SQLQuery<?> query, QueryMetadata metadata, QuerydslSqlVisitor visitor) {
        if (!metadata.getOrderByClauses().isEmpty()) {
            for (OrderExpression o : metadata.getOrderByClauses()) {
                vn.truongngo.lib.dynamicquery.core.expression.Expression e = o.getTarget();
                Expression<?> expression = visitor.visit(e, pathBuilders);
                com.querydsl.core.types.Order order = !o.getOrder().equals(Order.ASC) ?
                        com.querydsl.core.types.Order.DESC :
                        com.querydsl.core.types.Order.ASC;
                @SuppressWarnings("all")
                OrderSpecifier<?> op = new OrderSpecifier(order, expression);
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
    public static void restriction(SQLQuery<?> query, QueryMetadata metadata) {
        Restriction restriction = metadata.getRestriction();
        if (restriction.isPaging()) {
            query.limit(restriction.getSize());
            query.offset(restriction.getOffset());
        }
    }

    /**
     * Builds a complete {@link SQLQuery} by applying SELECT, JOIN, WHERE, GROUP BY, HAVING, and ORDER BY clauses
     * in the correct order based on the provided {@link QueryMetadata}.
     *
     * <p>
     * This method serves as a centralized utility for assembling a dynamic SQL query using
     * metadata-driven clauses and a {@link QuerydslSqlVisitor} for expression conversion.
     * </p>
     *
     * @param metadata the {@link QueryMetadata} containing all parts of the query
     * @param sources  a mapping of alias names to {@link QuerydslSource} used for resolving references
     * @param query the {@link SQLQuery} instance being constructed
     * @param visitor  the {@link QuerydslSqlVisitor} responsible for converting expressions to QueryDSL constructs
     */
    public static void buildQuery(QueryMetadata metadata, Map<String, QuerydslSource> sources, SQLQuery<?> query, QuerydslSqlVisitor visitor) {
        QuerydslSqlHelper.select(sources, query, metadata, visitor);
        QuerydslSqlHelper.join(sources, query, metadata, visitor);
        QuerydslSqlHelper.where(sources, query, metadata, visitor);
        QuerydslSqlHelper.groupBy(sources, query, metadata, visitor);
        QuerydslSqlHelper.having(sources, query, metadata, visitor);
        QuerydslSqlHelper.orderBy(sources, query, metadata, visitor);
    }

    /**
     * Builds the sources context map from the given {@link QueryMetadata}.
     * This includes the `FROM` clause and any `JOIN` clauses, mapping aliases to corresponding {@link QuerydslSource}.
     *
     * @param metadata the metadata representing a dynamic query
     * @return a map of alias names to {@link QuerydslSource} instances
     */
    public static Map<String, QuerydslSource> getSourcesContext(QueryMetadata metadata) {

        Map<String, QuerydslSource> context = new LinkedHashMap<>();

        QuerySource from = metadata.getFrom();
        QuerydslSource source = querydslSource(from);

        if (from instanceof SetOperationExpression setOps) {
            context.put(setOps.getAlias(), source);
        } else if (from instanceof CommonTableExpression cte) {
            context.put(cte.getName(), source);
        } else if (from instanceof SubqueryExpression subquery) {
            context.put(subquery.getAlias(), source);
        } else {
            EntityReferenceExpression entityRef = (EntityReferenceExpression) from;
            String alias = entityRef.getAlias();
            context.put(alias, source);
        }

        List<JoinExpression> joins = metadata.getJoinClauses();
        for (JoinExpression join : joins) {
            QuerySource target = join.target();
            QuerydslSource joinSource = querydslSource(target);
            context.put(join.target().getAlias(), joinSource);
        }

        return context;
    }

    /**
     * Converts a generic {@link QuerySource} into a concrete {@link QuerydslSource}, handling all known subtypes.
     *
     * @param source the abstract source to convert
     * @return the corresponding {@link QuerydslSource}
     * @throws IllegalArgumentException if the source type is unsupported or null
     */
    public static QuerydslSource querydslSource(QuerySource source) {
        if (source == null) throw new IllegalArgumentException("Source is null");
        if (source instanceof EntityReferenceExpression entityRef) return tableSource(entityRef);
        if (source instanceof SubqueryExpression subquery) return subquerySource(subquery);
        if (source instanceof CommonTableExpression cte) return cteSource(cte);
        if (source instanceof SetOperationExpression setOperation) return setOpsSource(setOperation);
        throw new IllegalArgumentException("Unsupported source type: " + source.getClass());
    }

    /**
     * Builds a {@link QuerydslSource} for a table/entity reference.
     *
     * @param entityRef the entity reference
     * @return a {@link QuerydslSource} wrapping the table
     * @throws IllegalArgumentException if instantiation of the entity path fails
     */
    public static QuerydslSource tableSource(EntityReferenceExpression entityRef) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends RelationalPathBase<?>> clazz = (Class<? extends RelationalPathBase<?>>) entityRef.getEntityClass();
            String alias = entityRef.getAlias();
            RelationalPath<?> entity = clazz.getConstructor(String.class).newInstance(alias);
            return QuerydslSource.builder().source(entity).build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot instantiate " + entityRef.getEntityClass().getName(), e);
        }
    }

     /**
     * Builds a {@link QuerydslSource} for a subquery expression.
     *
     * @param subquery the subquery expression
     * @return a {@link QuerydslSource} representing the subquery with its alias
     */
    public static QuerydslSource subquerySource(SubqueryExpression subquery) {
        QueryMetadata metadata = subquery.getQueryMetadata();
        QuerydslSqlVisitor visitor = QuerydslSqlVisitor.getInstance();
        Map<String, QuerydslSource> sources = getSourcesContext(metadata);
        SQLQuery<?> sqlSubquery = new SQLQuery<Void>();
        QuerydslSqlHelper.buildQuery(metadata, sources, sqlSubquery, visitor);
        Path<?> alias = new PathBuilder<>(Tuple.class, subquery.getAlias());
        Expression<?> subqueryExpression = sqlSubquery.as(alias);
        return QuerydslSource.builder().source(subqueryExpression).alias(alias).build();
    }

    /**
     * Builds a {@link QuerydslSource} for a set operation expression (e.g., UNION, UNION ALL).
     *
     * @param cte the set operation expression
     * @return a {@link QuerydslSource} representing the set operation result
     * @throws UnsupportedOperationException if the set operator is unsupported
     */
    public static QuerydslSource cteSource(CommonTableExpression cte) {
        SQLQuery<?> cteQuery = new SQLQuery<Void>();
        QuerydslSource subquerySrc = subquerySource(cte.getSubquery());
        Path<?> alias = new PathBuilder<>(Tuple.class, cte.getName());
        cteQuery = cteQuery.with(alias, subquerySrc.getSource());
        Expression<?> cteExpression = cteQuery.as(alias);
        return QuerydslSource.builder().source(cteExpression).alias(alias).build();
    }

    /**
     * Builds a {@link QuerydslSource} for a set operation expression (e.g., UNION, UNION ALL).
     *
     * @param setOperation the set operation expression
     * @return a {@link QuerydslSource} representing the set operation result
     * @throws UnsupportedOperationException if the set operator is unsupported
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static QuerydslSource setOpsSource(SetOperationExpression setOperation) {
        SQLQuery<?> setOperationQuery = new SQLQuery<>();
        List<SubQueryExpression> queryExpressions = new ArrayList<>();

        for (QuerySource query : setOperation.getQueries()) {
            QuerydslSource source = querydslSource(query);
            queryExpressions.add((SubQueryExpression) source.getSource());
        }

        SubQueryExpression[] queryExpressionsArray = (SubQueryExpression[]) Array.newInstance(SubQueryExpression.class, queryExpressions.size());
        queryExpressionsArray = queryExpressions.toArray(queryExpressionsArray);

        Expression<?> setOpsExpression;
        Path<?> alias = null;

        if (setOperation.getAlias() != null) {
            alias = new PathBuilder<>(Tuple.class, setOperation.getAlias());
        }

        if (setOperation.getOperator().equals(SetOperationExpression.SetOperator.UNION)) {
            setOpsExpression = setOperation.getAlias() == null ?
                    setOperationQuery.union(queryExpressionsArray) :
                    setOperationQuery.union(alias, queryExpressionsArray);
        } else if (setOperation.getOperator().equals(SetOperationExpression.SetOperator.UNION_ALL)) {
            setOpsExpression = setOperation.getAlias() == null ?
                    setOperationQuery.unionAll(queryExpressionsArray) :
                    setOperationQuery.unionAll(alias, queryExpressionsArray);
        } else {
            throw new UnsupportedOperationException("Unsupported set operation: " + setOperation.getOperator() + " yet");
        }

        return QuerydslSource.builder().source(setOpsExpression).alias(alias).build();
    }
}
