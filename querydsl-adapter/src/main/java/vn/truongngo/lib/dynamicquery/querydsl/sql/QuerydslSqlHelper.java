package vn.truongngo.lib.dynamicquery.querydsl.sql;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.Union;
import lombok.RequiredArgsConstructor;
import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;
import vn.truongngo.lib.dynamicquery.core.enumerate.Order;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderExpression;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.Restriction;
import vn.truongngo.lib.dynamicquery.metadata.jpa.JpaEntityMetadata;
import vn.truongngo.lib.dynamicquery.metadata.scanner.JpaEntityScanner;
import vn.truongngo.lib.dynamicquery.querydsl.common.QuerydslExpressionUtils;
import vn.truongngo.lib.dynamicquery.querydsl.common.QuerydslSource;
import vn.truongngo.lib.dynamicquery.querydsl.jpa.mapping.QEntity;
import vn.truongngo.lib.dynamicquery.querydsl.jpa.mapping.QEntityBuilder;

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
@RequiredArgsConstructor
public class QuerydslSqlHelper {

    /**
     * Indicates whether the Querydsl integration should operate in JPA entity mode.
     *
     * <p>
     * When set to {@code true}, query construction uses JPA entity classes and conventions,
     * allowing queries like {@code from(MyJpaEntity.class)} to be built on top of JPA models.
     * When {@code false}, queries are constructed using Querydsl's native SQL Q-class types,
     * generated directly from the database schema.
     * </p>
     */
    private final boolean jpaEntityMode;

    /**
     * Provides access to the singleton instance of {@link QuerydslSqlHelper} based on the {@code jpaEntityMode} flag.
     *
     * <p>This implementation is thread-safe and ensures lazy initialization of
     * singleton instances, one for JPA entity mode and one for native SQL mode.</p>
     *
     * <p>Use this method to obtain the appropriate helper for the query construction strategy
     * you require:</p>
     *
     * <blockquote><pre>
     * // Example usage:
     * QuerydslSqlHelper helper = QuerydslSqlHelper.getInstance(true);  // Use JPA entity mode
     * QuerydslSqlHelper helper = QuerydslSqlHelper.getInstance(false); // Use Querydsl SQL native mode
     * </pre></blockquote>
     *
     * @param jpaEntityMode {@code true} to build queries based on JPA entity classes; {@code false} to use Querydsl SQL Q-classes
     * @return the singleton instance of {@code QuerydslSqlHelper} for the specified mode
     */
    public static QuerydslSqlHelper getInstance(boolean jpaEntityMode) {
        return jpaEntityMode ? Holder.JPA : Holder.SQL;
    }

    /**
     * Lazy-loaded holder class implementing the Initialization-on-demand holder idiom
     * to provide thread-safe, lazy initialization of singleton instances.
     *
     * <p>This class holds two singleton instances of {@link QuerydslSqlHelper}:</p>
     * <ul>
     *   <li>{@code SQL} – instance for native Querydsl SQL mode (jpaEntityMode = false).</li>
     *   <li>{@code JPA} – instance for JPA entity mode (jpaEntityMode = true).</li>
     * </ul>
     *
     * <p>The instances are created only once when first accessed via {@link QuerydslSqlHelper#getInstance(boolean)}.</p>
     */
    private static class Holder {
        private static final QuerydslSqlHelper SQL = new QuerydslSqlHelper(false);
        private static final QuerydslSqlHelper JPA = new QuerydslSqlHelper(true);
    }

    /**
     * Applies SELECT expressions from metadata to the given SQL query.
     *
     * @param paths     a mapping of aliases to {@link QuerydslSource}, used for resolving column references
     * @param query     the QueryDSL {@link SQLQuery} to apply SELECT clauses to
     * @param metadata  the dynamic query metadata containing SELECT expressions
     * @param visitor   the visitor responsible for converting expressions to QueryDSL {@link Expression}s
     */
    public void select(Map<String, QuerydslSource> paths, SQLQuery<?> query, QueryMetadata metadata, QuerydslSqlVisitor visitor) {
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
    public void join(Map<String, QuerydslSource> paths, SQLQuery<?> query, QueryMetadata metadata, QuerydslSqlVisitor visitor) {
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
    public void where(Map<String, QuerydslSource> pathBuilders, SQLQuery<?> query, QueryMetadata metadata, QuerydslSqlVisitor visitor) {
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
    public void groupBy(Map<String, QuerydslSource> pathBuilders, SQLQuery<?> query, QueryMetadata metadata, QuerydslSqlVisitor visitor) {
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
    public void having(Map<String, QuerydslSource> pathBuilders, SQLQuery<?> query, QueryMetadata metadata, QuerydslSqlVisitor visitor) {
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
    public void orderBy(Map<String, QuerydslSource> pathBuilders, SQLQuery<?> query, QueryMetadata metadata, QuerydslSqlVisitor visitor) {
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
    public void restriction(SQLQuery<?> query, QueryMetadata metadata) {
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
    public void buildQuery(QueryMetadata metadata, Map<String, QuerydslSource> sources, SQLQuery<?> query, QuerydslSqlVisitor visitor) {
        select(sources, query, metadata, visitor);
        join(sources, query, metadata, visitor);
        where(sources, query, metadata, visitor);
        groupBy(sources, query, metadata, visitor);
        having(sources, query, metadata, visitor);
        orderBy(sources, query, metadata, visitor);
        restriction(query, metadata);
    }

    /**
     * Builds a {@link SQLQuery} based on the provided {@link QueryMetadata}, using {@link QuerydslSqlHelper}
     * and {@link QuerydslSqlVisitor} for expression resolution. This method supports joining against
     * subqueries (including set operations like UNION) and entity references.
     *
     * <p>
     * The method inspects the {@code from} clause in the {@link QueryMetadata} to determine whether it is:
     * <ul>
     *   <li>A {@link SetOperationExpression} (e.g., UNION), in which case it uses the corresponding {@link Union}
     *       as a subquery and applies any {@code orderBy} clauses directly on the result.</li>
     *   <li>An {@link EntityReferenceExpression}, in which case it directly uses the root entity's path.</li>
     *   <li>A nested {@link SQLQuery} (i.e., subquery in FROM), in which case it is aliased via {@link Path}.</li>
     * </ul>
     * After determining the appropriate source and alias, it registers the {@code from} clause with the
     * target query and delegates the rest of the query construction (joins, predicates, selects, etc.)
     * to the helper.
     * </p>
     *
     * @param queryMetadata the query metadata that contains structural info for building the SQL query
     * @param visitor the visitor that traverses and converts dynamic expressions to QueryDSL expressions
     * @param query the QueryDSL SQL query to be populated with from and other clauses
     * @param <T> the result type of the SQL query
     *
     * @see QueryMetadata
     * @see QuerydslSqlVisitor
     * @see SQLQuery
     * @see SetOperationExpression
     * @see EntityReferenceExpression
     */
    public <T> void buildQuery(QueryMetadata queryMetadata, QuerydslSqlVisitor visitor, SQLQuery<T> query) {
        Map<String, QuerydslSource> context = getSourcesContext(queryMetadata);
        QuerydslSource querydslSource = context.get(queryMetadata.getFrom().getAlias());
        String alias = queryMetadata.getFrom().getAlias();
        if (queryMetadata.getFrom() instanceof SetOperationExpression) {
            Union<?> union = (Union<?>) querydslSource.getSource();
            if (queryMetadata.getOrderByClauses() != null) {
                @SuppressWarnings("rawtypes")
                OrderSpecifier[] orderSpecifiers = queryMetadata.getOrderByClauses()
                        .stream()
                        .map(op -> QuerydslExpressionUtils.order(op, visitor, context))
                        .toArray(OrderSpecifier[]::new);
                query.from(union, querydslSource.getAlias()).orderBy(orderSpecifiers);
            }
        } else {
            if (queryMetadata.getFrom() instanceof EntityReferenceExpression) {
                Expression<?> root = context.get(alias).getSource();
                query.from(root);
            } else {
                SQLQuery<?> sqlQuery = (SQLQuery<?>) context.get(alias).getSource();
                Path<?> path = context.get(alias).getAlias();
                query.from(sqlQuery, path);
            }

            buildQuery(queryMetadata, context, query, visitor);
        }
    }

    /**
     * Builds the sources context map from the given {@link QueryMetadata}.
     * This includes the `FROM` clause and any `JOIN` clauses, mapping aliases to corresponding {@link QuerydslSource}.
     *
     * @param metadata the metadata representing a dynamic query
     * @return a map of alias names to {@link QuerydslSource} instances
     */
    public Map<String, QuerydslSource> getSourcesContext(QueryMetadata metadata) {

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
    public QuerydslSource querydslSource(QuerySource source) {
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
    public QuerydslSource tableSource(EntityReferenceExpression entityRef) {
        try {
            String alias = entityRef.getAlias();
            if (jpaEntityMode) {
                JpaEntityMetadata metadata = new JpaEntityScanner().scan(entityRef.getEntityClass());
                QEntity<?> qEntity = QEntityBuilder.build(metadata, alias, false);
                return QuerydslSource.builder().source(qEntity).build();
            } else {
                @SuppressWarnings("unchecked")
                Class<? extends RelationalPathBase<?>> clazz = (Class<? extends RelationalPathBase<?>>) entityRef.getEntityClass();
                RelationalPath<?> entity = clazz.getConstructor(String.class).newInstance(alias);
                return QuerydslSource.builder().source(entity).build();
            }
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
    public QuerydslSource subquerySource(SubqueryExpression subquery) {
        SQLQuery<?> sqlSubquery = new SQLQuery<Void>();
        buildQuery(subquery.getQueryMetadata(), QuerydslSqlVisitor.getInstance(jpaEntityMode), sqlSubquery);
        Path<?> alias = new PathBuilder<>(Tuple.class, subquery.getAlias());
        return QuerydslSource.builder().source(sqlSubquery).alias(alias).build();
    }

    /**
     * Builds a {@link QuerydslSource} for a set operation expression (e.g., UNION, UNION ALL).
     *
     * @param cte the set operation expression
     * @return a {@link QuerydslSource} representing the set operation result
     * @throws UnsupportedOperationException if the set operator is unsupported
     */
    public QuerydslSource cteSource(CommonTableExpression cte) {
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
    public QuerydslSource setOpsSource(SetOperationExpression setOperation) {
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
