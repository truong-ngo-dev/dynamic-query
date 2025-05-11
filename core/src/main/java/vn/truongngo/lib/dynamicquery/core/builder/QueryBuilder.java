package vn.truongngo.lib.dynamicquery.core.builder;

import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderExpression;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.Restriction;

import java.util.function.Consumer;

/**
 * QueryBuilder is a fluent API interface for constructing dynamic queries in an abstract way.
 * <p>
 * It supports operations like selecting columns, joining entities, applying predicates,
 * grouping, ordering, and applying restrictions. The actual query generation is deferred
 * to specific implementations (e.g., QueryDSL, jOOQ).
 *
 * @param <Q> the result type of the built query (e.g., Tuple, DTO, etc.)
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface QueryBuilder<Q> {

    /**
     * Specifies query source from the root entity to select from.
     *
     * @param entityClass the entity class
     * @return the current builder instance
     */
    QueryBuilder<Q> from(Class<?> entityClass);

    /**
     * Specifies the root entity with a given alias.
     *
     * @param entityClass the entity class
     * @param alias the alias for the entity
     * @return the current builder instance
     */
    QueryBuilder<Q> from(Class<?> entityClass, String alias);

    /**
     * Specifies a query source.
     *
     * @param source the query source expression
     * @return the current builder instance
     */
    QueryBuilder<Q> from(QuerySource source);

    /**
     * Specifies a query source with alias.
     *
     * @param source the query source expression
     * @param alias the alias for the entity
     * @return the current builder instance
     */
    QueryBuilder<Q> from(QuerySource source, String alias);

    /**
     * Specifies the selection expressions (columns, functions, etc.)
     *
     * @param expressions the selection expressions
     * @return the current builder instance
     */
    QueryBuilder<Q> select(Selection... expressions);

    /**
     * Specifies columns to select using their string representations.
     *
     * @param columns the column names or paths
     * @return the current builder instance
     */
    QueryBuilder<Q> select(String... columns);

    /**
     * Specifies that the query should eliminate duplicate rows using the DISTINCT keyword.
     *
     * @return the current builder instance
     */
    QueryBuilder<Q> distinct();

    /**
     * Adds a join with another query source based on a condition and alias.
     *
     * @param target the query source to join
     * @param condition the join condition
     * @return the current builder instance
     */
    QueryBuilder<Q> join(QuerySource target, Predicate condition);

    /**
     * Adds a join using a builder pattern.
     *
     * @param joinBuilder a consumer of {@link JoinExpression.Builder}
     * @return the current builder instance
     */
    QueryBuilder<Q> join(Consumer<JoinExpression.Builder> joinBuilder);

    /**
     * Adds where conditions to the query.
     *
     * @param predicates one or more conditions to filter results
     * @return the current builder instance
     */
    QueryBuilder<Q> where(Predicate... predicates);

    /**
     * Groups results by the given expressions.
     *
     * @param expressions expressions to group by
     * @return the current builder instance
     */
    QueryBuilder<Q> groupBy(Selection... expressions);

    /**
     * Applies conditions to grouped records (having clause).
     *
     * @param predicates conditions on grouped results
     * @return the current builder instance
     */
    QueryBuilder<Q> having(Predicate... predicates);

    /**
     * Specifies the ordering of query results.
     *
     * @param orderExpressions one or more order specifiers
     * @return the current builder instance
     */
    QueryBuilder<Q> orderBy(OrderExpression... orderExpressions);

    /**
     * Adds restrictions (e.g., pagination, security filters).
     *
     * @param restriction a restriction to apply to the query
     * @return the current builder instance
     */
    QueryBuilder<Q> restriction(Restriction restriction);

    /**
     * Builds and returns the final query object.
     *
     * @return the constructed query object
     */
    Q build();
}
