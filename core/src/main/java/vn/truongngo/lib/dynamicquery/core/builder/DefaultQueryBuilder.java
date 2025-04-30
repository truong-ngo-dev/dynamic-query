package vn.truongngo.lib.dynamicquery.core.builder;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.expression.JoinExpression;
import vn.truongngo.lib.dynamicquery.core.expression.Predicate;
import vn.truongngo.lib.dynamicquery.core.expression.Selection;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderSpecifier;
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
 * @version 1.0
 */
@Getter
public class DefaultQueryBuilder<Q> implements QueryBuilder<Q> {

    private QueryMetadata queryMetadata = new DefaultQueryMetadata();
    private final QueryBuilderStrategy<Q> strategy;

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
     * Set distinct to query
     * @return The current {@link QueryBuilder} instance.
     * */
    @Override
    public QueryBuilder<Q> distinct() {
        queryMetadata.setDistinct(true);
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
        for (String c : columns) {
            Selection expression = Expressions.column(c, queryMetadata.getEntityClass());
            queryMetadata.addSelect(expression);
        }
        return this;
    }

    /**
     * Adds a join clause to the query.
     *
     * @param entityClass The entity class to join with.
     * @param condition The predicate condition for the join.
     * @param alias The alias for the joined entity.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> join(Class<?> entityClass, Predicate condition, String alias) {
        JoinExpression joinExpression = Expressions.join(b -> b
                .join(Expressions.entity(entityClass))
                .as(alias)
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
     * @param orderSpecifiers One or more order specifiers.
     * @return The current {@link QueryBuilder} instance.
     */
    @Override
    public QueryBuilder<Q> orderBy(OrderSpecifier... orderSpecifiers) {
        for (OrderSpecifier o : orderSpecifiers) {
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
