package vn.truongngo.lib.dynamicquery.core.builder;

import vn.truongngo.lib.dynamicquery.core.expression.Expression;
import vn.truongngo.lib.dynamicquery.core.expression.JoinExpression;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderSpecifier;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.Restriction;
import vn.truongngo.lib.dynamicquery.core.expression.predicate.Predicate;

import java.util.function.Consumer;

/**
 * QueryBuilder is a fluent API interface for constructing dynamic queries in an abstract way.
 * <p>
 * It supports operations like selecting columns, joining entities, applying predicates,
 * grouping, ordering, and applying restrictions. The actual query generation is deferred
 * to specific implementations (e.g., QueryDSL, jOOQ).
 *
 * @param <T> the result type of the built query (e.g., Tuple, DTO, etc.)
 * @author Truong Ngo
 * @version 1.0
 */
public interface QueryBuilder<T> {

    /**
     * Specifies the root entity to select from.
     *
     * @param entityClass the entity class
     * @return the current builder instance
     */
    QueryBuilder<T> from(Class<?> entityClass);

    /**
     * Specifies the root entity with a given alias.
     *
     * @param entityClass the entity class
     * @param alias the alias for the entity
     * @return the current builder instance
     */
    QueryBuilder<T> from(Class<?> entityClass, String alias);

    /**
     * Specifies the selection expressions (columns, functions, etc.)
     *
     * @param expressions the selection expressions
     * @return the current builder instance
     */
    QueryBuilder<T> select(Expression... expressions);

    /**
     * Specifies columns to select using their string representations.
     *
     * @param columns the column names or paths
     * @return the current builder instance
     */
    QueryBuilder<T> select(String... columns);

    /**
     * Adds a join with another entity based on a condition and alias.
     *
     * @param entityClass the class to join
     * @param condition the join condition
     * @param alias the alias for the joined entity
     * @return the current builder instance
     */
    QueryBuilder<T> join(Class<?> entityClass, Predicate condition, String alias);

    /**
     * Adds a join using a builder pattern.
     *
     * @param joinBuilder a consumer of {@link JoinExpression.Builder}
     * @return the current builder instance
     */
    QueryBuilder<T> join(Consumer<JoinExpression.Builder> joinBuilder);

    /**
     * Adds where conditions to the query.
     *
     * @param predicates one or more conditions to filter results
     * @return the current builder instance
     */
    QueryBuilder<T> where(Predicate... predicates);

    /**
     * Groups results by the given expressions.
     *
     * @param expressions expressions to group by
     * @return the current builder instance
     */
    QueryBuilder<T> groupBy(Expression... expressions);

    /**
     * Applies conditions to grouped records (having clause).
     *
     * @param predicates conditions on grouped results
     * @return the current builder instance
     */
    QueryBuilder<T> having(Predicate... predicates);

    /**
     * Specifies the ordering of query results.
     *
     * @param orderSpecifiers one or more order specifiers
     * @return the current builder instance
     */
    QueryBuilder<T> orderBy(OrderSpecifier... orderSpecifiers);

    /**
     * Adds restrictions (e.g., pagination, security filters).
     *
     * @param restriction a restriction to apply to the query
     * @return the current builder instance
     */
    QueryBuilder<T> restriction(Restriction restriction);

    /**
     * Builds and returns the final query object.
     *
     * @return the constructed query object
     */
    Object build();
}
