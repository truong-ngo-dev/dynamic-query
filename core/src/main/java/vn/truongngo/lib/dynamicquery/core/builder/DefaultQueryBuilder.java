package vn.truongngo.lib.dynamicquery.core.builder;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderExpression;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.Restriction;
import vn.truongngo.lib.dynamicquery.core.support.Expressions;

import java.util.function.Consumer;

/**
 * Abstract base class for building dynamic queries. This class provides the
 * fundamental methods to construct a query with a specific entity class,
 * perform selections, joins, apply predicates, groupings, orderings, and restrictions.
 * <p>
 * This builder is highly customizable and designed for flexibility, allowing for
 * different query expressions to be added dynamically. The actual implementation
 * of how the query is executed can be extended by subclasses that provide
 * the concrete logic for handling the query building process.
 * </p>
 *
 * @param <Q> The type of the result object returned by the query.
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public class DefaultQueryBuilder<Q> implements QueryBuilder<Q> {

    /**
     * The metadata representing the components of the query to be built.
     */
    private QueryMetadata queryMetadata = new DefaultQueryMetadata();

    /**
     * The strategy used to build the query based on the provided metadata.
     */
    private final QueryBuilderStrategy<Q> strategy;

    /**
     * Constructs a {@code DefaultQueryBuilder} with the specified strategy.
     *
     * @param strategy the strategy to use for building the query
     */
    public DefaultQueryBuilder(QueryBuilderStrategy<Q> strategy) {
        this.strategy = strategy;
    }

    /**
     * Specifies the entity class for the query.
     *
     * @param entityClass The entity class to use in the query.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> from(Class<?> entityClass) {
        this.queryMetadata = new DefaultQueryMetadata(entityClass);
        return this;
    }

    /**
     * Specifies the entity class for the query along with an alias.
     *
     * @param entityClass The entity class to use in the query.
     * @param alias The alias to use for the entity.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> from(Class<?> entityClass, String alias) {
        this.queryMetadata = new DefaultQueryMetadata(entityClass, alias);
        return this;
    }

    /**
     * Specifies the subquery as source for the main query.
     *
     * @param subquery The subquery to use in the main query.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> from(SubqueryExpression subquery) {
        this.queryMetadata = new DefaultQueryMetadata(subquery);
        return this;
    }

    /**
     * Specifies the subquery as source for the main query with alias.
     *
     * @param subquery The subquery to use in the main query.
     * @param alias The alias to use for the subquery.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> from(SubqueryExpression subquery, String alias) {
        this.queryMetadata = new DefaultQueryMetadata(subquery, alias);
        return this;
    }

    /**
     * Specifies the common table expression as source for the main query.
     *
     * @param cte The common table expression to use in the main query.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> from(CommonTableExpression cte) {
        this.queryMetadata = new DefaultQueryMetadata(cte);
        return this;
    }

    /**
     * Specifies the common table expression as source for the main query with alias.
     *
     * @param cte The common table expression to use in the main query.
     * @param alias The alias to use for the cte.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> from(CommonTableExpression cte, String alias) {
        this.queryMetadata = new DefaultQueryMetadata(cte, alias);
        return this;
    }

    /**
     * Specifies the set operation expression as source for the main query.
     *
     * @param setOps The set operation expression to use in the main query.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> from(SetOperationExpression setOps) {
        this.queryMetadata = new DefaultQueryMetadata(setOps);
        return this;
    }

    /**
     * Specifies the set operation expression as source for the main query with alias.
     *
     * @param setOps The set operation expression to use in the main query.
     * @param alias The alias to use for the set operation expression.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> from(SetOperationExpression setOps, String alias) {
        this.queryMetadata = new DefaultQueryMetadata(setOps, alias);
        return this;
    }

    /**
     * Adds expressions to the select clause of the query.
     *
     * @param expressions One or more expressions to select.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> select(Selection... expressions) {
        for (Selection expression : expressions) {
            queryMetadata.addSelect(expression);
        }
        return this;
    }

    /**
     * Adds column names to the select clause of the query.
     *
     * @param columns One or more column names to select.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> select(String... columns) {
        if (queryMetadata.getFrom() instanceof SetOperationExpression) return this;                                                 // Not allow selection in set operation
        for (String c : columns) {
            Selection expression =
                    queryMetadata.getFrom() instanceof EntityReferenceExpression entity ? Expressions.column(c, entity) :
                    queryMetadata.getFrom() instanceof CommonTableExpression cte ? Expressions.column(c, cte) :
                    queryMetadata.getFrom() instanceof SubqueryExpression subquery ? Expressions.column(c, subquery) : null;
            if (expression == null) throw new IllegalArgumentException("Unsupported query source");
            queryMetadata.addSelect(expression);
        }
        return this;
    }

    /**
     * Set distinct to query
     * @return The current {@link QueryBuilder} instance.
     * */
    @Override
    public QueryBuilder<Q> distinct() {
        queryMetadata.setDistinct(true);
        return this;
    }

    /**
     * Adds a join clause to the query.
     *
     * @param target The expression to join with.
     * @param condition The predicate condition for the join.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> join(QuerySource target, Predicate condition) {
        JoinExpression joinExpression = Expressions.join(b -> b
                .join(target)
                .on(condition));
        queryMetadata.addJoin(joinExpression);
        return this;
    }

    /**
     * Adds a join clause to the query with a custom builder.
     *
     * @param builder The custom builder to configure the join.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> join(Consumer<JoinExpression.Builder> builder) {
        JoinExpression joinExpression = Expressions.join(builder);
        queryMetadata.addJoin(joinExpression);
        return this;
    }

    /**
     * Adds predicates to the where clause of the query.
     *
     * @param predicates One or more predicates to apply to the where clause.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> where(Predicate... predicates) {
        for (Predicate p : predicates) {
            queryMetadata.addWhere(p);
        }
        return this;
    }

    /**
     * Adds expressions to the group by clause of the query.
     *
     * @param expressions One or more expressions to group by.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> groupBy(Selection... expressions) {
        for (Selection e : expressions) {
            queryMetadata.addGroupBy(e);
        }
        return this;
    }

    /**
     * Adds predicates to the having clause of the query.
     *
     * @param predicates One or more predicates to apply to the having clause.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> having(Predicate... predicates) {
        for (Predicate p : predicates) {
            queryMetadata.addHaving(p);
        }
        return this;
    }

    /**
     * Adds order specifications to the query.
     *
     * @param orderExpressions One or more order specifiers.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> orderBy(OrderExpression... orderExpressions) {
        for (OrderExpression o : orderExpressions) {
            queryMetadata.addOrderBy(o);
        }
        return this;
    }

    /**
     * Sets a restriction on the query.
     *
     * @param restriction The restriction to apply to the query.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> restriction(Restriction restriction) {
        queryMetadata.setRestriction(restriction);
        return this;
    }

    /**
     * Builds the final query object using the configured {@link QueryBuilderStrategy}.
     * <p>
     * This method delegates the construction of the final query object (e.g., a QueryDSL or jOOQ query)
     * to the strategy implementation provided at construction time. It uses the current state of the
     * {@link QueryMetadata} that has been configured via the fluent API.
     *
     * @return The built query object of type {@code Q}, as produced by the underlying strategy.
     */
    @Override
    public Q build() {
        return strategy.accept(queryMetadata);
    }
}
