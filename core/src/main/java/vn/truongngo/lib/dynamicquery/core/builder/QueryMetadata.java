package vn.truongngo.lib.dynamicquery.core.builder;

import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderExpression;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.Restriction;

import java.util.List;
import java.util.Map;

/**
 * Interface that represents the metadata of a query. It stores the structural components of a query
 * such as SELECT, JOIN, WHERE, GROUP BY, HAVING, ORDER BY, and restrictions (e.g., limit/offset).
 * <p>
 * This metadata is later used by specific query engines (e.g., Querydsl, jOOQ, etc.)
 * to generate the final query string or query object for execution.
 *
 * <p>Example usage:
 * <blockquote><pre>
 * QueryMetadata metadata = new DefaultQueryMetadata(MyEntity.class);
 * metadata.addSelect(Expressions.column("name"));
 * metadata.addWhere(Predicates.greaterThan(Expressions.column("age"), 18);
 * metadata.addOrderBy(Expressions.orderBy(Expressions.column("name"), Order.ASC));
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface QueryMetadata {

    /**
     * Returns the FROM target expression (e.g., table, subquery, function, etc.).
     *
     * @return the FROM expression
     */
    QuerySource getFrom();

    /**
     * Returns the alias associated with the FROM expression, if any.
     *
     * @return the alias string
     */
    String getAlias();

    /**
     * Returns a mapping of alias names to query sources. This includes the
     * primary FROM source and any additional JOINed sources.
     * <p>
     * The key is the alias (or name) used in the query, and the value is the corresponding {@link QuerySource}.
     * </p>
     *
     * @return a map of aliases to query sources
     */
    Map<String, QuerySource> getSourceMap();

    /**
     * Sets the FROM clause using a given entity class.
     *
     * @param entityClass the entity class representing the FROM source
     */
    void setFrom(Class<?> entityClass);

    /**
     * Sets the FROM clause using a given entity class and alias.
     *
     * @param entityClass the entity class representing the FROM source
     * @param alias       the alias to assign to the FROM clause
     */
    void setFrom(Class<?> entityClass, String alias);

    /**
     * Sets the FROM expression with query source.
     *
     * @param source  the query source
     */
    void setFrom(QuerySource source);

    /**
     * Sets the FROM expression with query source and its alias.
     *
     * @param source  the query source
     * @param alias     the alias name
     */
    void setFrom(QuerySource source, String alias);

    /**
     * Adds an expression to the SELECT clause.
     *
     * @param selectClauses the SELECT expression
     */
    void addSelect(Selection selectClauses);

    /**
     * Sets the flag to indicate whether the query requires the use of the <code>DISTINCT</code> keyword.
     *
     * @param distinct <code>true</code> to indicate the query requires <code>DISTINCT</code>;
     *                 <code>false</code> otherwise.
     */
    void setDistinct(boolean distinct);

    /**
     * Adds a JOIN expression.
     *
     * @param joinClauses the JOIN expression
     */
    void addJoin(JoinExpression joinClauses);

    /**
     * Adds a WHERE predicate to the query.
     *
     * @param whereClauses the WHERE predicate
     */
    void addWhere(Predicate whereClauses);

    /**
     * Adds an expression to the GROUP BY clause.
     *
     * @param groupByClauses the GROUP BY expression
     */
    void addGroupBy(Selection groupByClauses);

    /**
     * Adds a predicate to the HAVING clause.
     *
     * @param havingClauses the HAVING predicate
     */
    void addHaving(Predicate havingClauses);

    /**
     * Adds an ORDER BY specifier.
     *
     * @param orderByClauses the ORDER BY specifier
     */
    void addOrderBy(OrderExpression orderByClauses);

    /**
     * Sets query restriction options such as limit and offset.
     *
     * @param restriction the query modifiers
     */
    void setRestriction(Restriction restriction);

    /**
     * Resets all query metadata — clearing all previously added clauses.
     */
    void reset();

    /**
     * Clears all predicates in the WHERE clause.
     */
    void resetWhereClauses();

    /**
     * Clears all expressions in the GROUP BY clause.
     */
    void resetGroupByClauses();

    /**
     * Clears all predicates in the HAVING clause.
     */
    void resetHavingClauses();

    /**
     * Clears all order specifiers in the ORDER BY clause.
     */
    void resetOrderByClauses();

    /**
     * Checks if the query requires the use of the <code>DISTINCT</code> keyword.
     *
     * @return <code>true</code> if the query requires <code>DISTINCT</code>; <code>false</code> otherwise.
     */
    boolean isDistinct();

    /**
     * Returns the list of SELECT expressions.
     *
     * @return the list of SELECT clauses
     */
    List<Selection> getSelectClauses();

    /**
     * Returns the list of JOIN expressions.
     *
     * @return the list of JOIN clauses
     */
    List<JoinExpression> getJoinClauses();

    /**
     * Returns the list of WHERE predicates.
     *
     * @return the list of WHERE clauses
     */
    List<Predicate> getWhereClauses();

    /**
     * Returns the list of GROUP BY expressions.
     *
     * @return the list of GROUP BY clauses
     */
    List<Selection> getGroupByClauses();

    /**
     * Returns the list of HAVING predicates.
     *
     * @return the list of HAVING clauses
     */
    List<Predicate> getHavingClauses();

    /**
     * Returns the list of ORDER BY specifiers.
     *
     * @return the list of ORDER BY clauses
     */
    List<OrderExpression> getOrderByClauses();

    /**
     * Returns the query restrictions (typically contains one item with limit/offset info).
     *
     * @return the query modifiers
     */
    Restriction getRestriction();

}
