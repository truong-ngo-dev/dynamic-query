package vn.truongngo.lib.dynamicquery.core.builder;

import vn.truongngo.lib.dynamicquery.core.enumerate.*;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderSpecifier;
import vn.truongngo.lib.dynamicquery.core.expression.predicate.Predicate;
import vn.truongngo.lib.dynamicquery.core.support.Expressions;
import vn.truongngo.lib.dynamicquery.core.support.Predicates;

import java.util.List;
import java.util.function.Consumer;

public interface QueryBuilderMixIn {

    /**
     * Creates a constant expression with a given value.
     *
     * @param value the constant value
     * @return a constant expression
     */
    default Expression constant(Object value) {
        return Expressions.constant(value);
    }

    /**
     * Creates an entity reference expression for the given class.
     *
     * @param entityClass the entity class
     * @return an entity reference expression
     */
    default Expression entity(Class<?> entityClass) {
        return Expressions.entity(entityClass);
    }

    /**
     * Creates an entity reference expression with an alias.
     *
     * @param entityClass the entity class
     * @param alias the alias for the entity
     * @return an entity reference expression with alias
     */
    default Expression entity(Class<?> entityClass, String alias) {
        return Expressions.entity(entityClass, alias);
    }

    /**
     * Creates a column expression for the given column name and entity.
     *
     * @param columnName the name of the column
     * @param entityClass the owning entity class
     * @return a column expression
     */
    default Expression column(String columnName, Class<?> entityClass) {
        return Expressions.column(columnName, entityClass);
    }

    /**
     * Creates a column expression with alias for an entity.
     *
     * @param columnName the column name
     * @param alias the alias for the column
     * @param entityClass the entity class
     * @return a column reference expression
     */
    default Expression column(String columnName, String alias, Class<?> entityClass) {
        return Expressions.column(columnName, alias, entityClass);
    }

    /**
     * Creates a column expression from a subquery with alias.
     *
     * @param columnName the column name
     * @param alias the alias for the subquery
     * @param target the subquery expression
     * @return a column reference expression
     */
    default Expression column(String columnName, String alias, SubqueryExpression target) {
        return Expressions.column(columnName, alias, target);
    }

    /**
     * Creates a function expression with custom function name and alias.
     *
     * @param functionName the function name
     * @param alias the alias for the function result
     * @param args the arguments to the function
     * @return a function expression
     */
    default Expression function(String functionName, String alias, Expression... args) {
        return Expressions.function(functionName, alias, args);
    }

    /**
     * Creates a COUNT aggregate function.
     *
     * @param alias the alias for the result
     * @param args the expressions to count
     * @return a COUNT expression
     */
    default Expression count(String alias, Expression... args) {
        return Expressions.count(alias, args);
    }

    /**
     * Creates a SUM aggregate function.
     *
     * @param alias the alias for the result
     * @param args the expressions to sum
     * @return a SUM expression
     */
    default Expression sum(String alias, Expression... args) {
        return Expressions.sum(alias, args);
    }

    /**
     * Creates an AVG aggregate function.
     *
     * @param alias the alias for the result
     * @param args the expressions to average
     * @return an AVG expression
     */
    default Expression avg(String alias, Expression... args) {
        return Expressions.avg(alias, args);
    }

    /**
     * Creates a MAX aggregate function.
     *
     * @param alias the alias for the result
     * @param args the expressions to evaluate
     * @return a MAX expression
     */
    default Expression max(String alias, Expression... args) {
        return Expressions.max(alias, args);
    }

    /**
     * Creates a MIN aggregate function.
     *
     * @param alias the alias for the result
     * @param args the expressions to evaluate
     * @return a MIN expression
     */
    default Expression min(String alias, Expression... args) {
        return Expressions.min(alias, args);
    }

    /**
     * Creates a LOWER string function expression.
     *
     * @param alias the alias for the result
     * @param args the string expression
     * @return a LOWER function expression
     */
    default Expression lower(String alias, Expression args) {
        return Expressions.lower(alias, args);
    }

    /**
     * Creates an UPPER string function expression.
     *
     * @param alias the alias for the result
     * @param args the string expression
     * @return an UPPER function expression
     */
    default Expression upper(String alias, Expression args) {
        return Expressions.upper(alias, args);
    }

    /**
     * Creates a join expression with a given type, target, join condition, and alias.
     *
     * @param joinType the type of join (e.g., INNER, LEFT)
     * @param target the target entity or subquery
     * @param condition the join condition
     * @param alias the alias for the joined table
     * @return a join expression
     */
    default JoinExpression joins(JoinType joinType, Expression target, Predicate condition, String alias) {
        return Expressions.join(joinType, target, condition, alias);
    }

    /**
     * Creates a join expression using a builder for fluent customization.
     *
     * @param builder the consumer to configure the join builder
     * @return a join expression
     */
    default JoinExpression joins(Consumer<JoinExpression.Builder> builder) {
        return Expressions.join(builder);
    }

    /**
     * Creates an order specifier with a given expression and direction.
     *
     * @param expression the expression to order by
     * @param order the sort direction (ASC or DESC)
     * @return an order specifier
     */
    default OrderSpecifier order(Expression expression, Order order) {
        return Expressions.orderBy(expression, order);
    }

    /**
     * Creates an ascending order specifier by default.
     *
     * @param expression the expression to order by
     * @return an order specifier (default ASC)
     */
    default OrderSpecifier order(Expression expression) {
        return Expressions.orderBy(expression);
    }

    /**
     * Creates a CASE WHEN expression using a builder.
     *
     * @param builder the consumer to configure the case expression
     * @return a case when expression
     */
    default Expression caseWhen(Consumer<CaseWhenExpression.Builder> builder) {
        return Expressions.caseWhen(builder);
    }

    /**
     * Creates a subquery expression using a builder.
     *
     * @param builder the consumer to configure the subquery
     * @return a subquery expression
     */
    default Expression subquery(Consumer<SubqueryExpression.Builder> builder) {
        return Expressions.subquery(builder);
    }

    /**
     * Negates a given predicate.
     *
     * @param predicate the predicate to negate
     * @return the negated predicate
     */
    default Predicate not(Predicate predicate) {
        return Predicates.not(predicate);
    }

    /**
     * Combines two predicates using logical AND.
     *
     * @param lhs the left-hand side predicate
     * @param rhs the right-hand side predicate
     * @return a new predicate representing (lhs AND rhs)
     */
    default Predicate and(Predicate lhs, Predicate rhs) {
        return Predicates.and(lhs, rhs);
    }

    /**
     * Combines two predicates using logical OR.
     *
     * @param lhs the left-hand side predicate
     * @param rhs the right-hand side predicate
     * @return a new predicate representing (lhs OR rhs)
     */
    default Predicate or(Predicate lhs, Predicate rhs) {
        return Predicates.or(lhs, rhs);
    }

    /**
     * Combines multiple predicates using logical OR (any match).
     *
     * @param predicates the predicates to combine
     * @return a new predicate representing (p1 OR p2 OR ...)
     */
    default Predicate anyOf(Predicate... predicates) {
        return Predicates.anyOf(predicates);
    }

    /**
     * Combines multiple predicates using logical AND (all must match).
     *
     * @param predicates the predicates to combine
     * @return a new predicate representing (p1 AND p2 AND ...)
     */
    default Predicate allOf(Predicate... predicates) {
        return Predicates.allOf(predicates);
    }

    /**
     * Returns a predicate that always evaluates to true (1 = 1).
     *
     * @return the always-true predicate
     */
    default Predicate conjunction() {
        return Predicates.conjunction();
    }

    /**
     * Returns a predicate that always evaluates to false (1 != 1).
     *
     * @return the always-false predicate
     */
    default Predicate disjunction() {
        return Predicates.disjunction();
    }


    /**
     * Creates an equality predicate comparing an expression to a constant.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side constant value
     * @return a predicate representing (left = right)
     */
    default Predicate equal(Expression left, Object right) {
        return Predicates.equal(left, right);
    }

    /**
     * Creates an equality predicate comparing two expressions.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side expression
     * @return a predicate representing (left = right)
     */
    default Predicate equal(Expression left, Expression right) {
        return Predicates.equal(left, right);
    }

    /**
     * Creates a not-equal predicate comparing an expression to a constant.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side constant value
     * @return a predicate representing (left != right)
     */
    default Predicate notEqual(Expression left, Object right) {
        return Predicates.notEqual(left, right);
    }

    /**
     * Creates a not-equal predicate comparing two expressions.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side expression
     * @return a predicate representing (left != right)
     */
    default Predicate notEqual(Expression left, Expression right) {
        return Predicates.notEqual(left, right);
    }

    /**
     * Creates a greater-than predicate comparing an expression to a constant.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side constant value
     * @return a predicate representing (left > right)
     */
    default Predicate greaterThan(Expression left, Object right) {
        return Predicates.greaterThan(left, right);
    }

    /**
     * Creates a greater-than predicate comparing two expressions.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side expression
     * @return a predicate representing (left > right)
     */
    default Predicate greaterThan(Expression left, Expression right) {
        return Predicates.greaterThan(left, right);
    }

    /**
     * Creates a less-than predicate comparing an expression to a constant.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side constant value
     * @return a predicate representing (left < right)
     */
    default Predicate lessThan(Expression left, Object right) {
        return Predicates.lessThan(left, right);
    }

    /**
     * Creates a less-than predicate comparing two expressions.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side expression
     * @return a predicate representing (left < right)
     */
    default Predicate lessThan(Expression left, Expression right) {
        return Predicates.lessThan(left, right);
    }

    /**
     * Creates a greater-than-or-equal predicate comparing an expression to a constant.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side constant value
     * @return a predicate representing (left >= right)
     */
    default Predicate greaterThanOrEqual(Expression left, Object right) {
        return Predicates.greaterThanOrEqual(left, right);
    }

    /**
     * Creates a greater-than-or-equal predicate comparing two expressions.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side expression
     * @return a predicate representing (left >= right)
     */
    default Predicate greaterThanOrEqual(Expression left, Expression right) {
        return Predicates.greaterThanOrEqual(left, right);
    }

    /**
     * Creates a less-than-or-equal predicate comparing an expression to a constant.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side constant value
     * @return a predicate representing (left <= right)
     */
    default Predicate lessThanOrEqual(Expression left, Object right) {
        return Predicates.lessThanOrEqual(left, right);
    }

    /**
     * Creates a less-than-or-equal predicate comparing two expressions.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side expression
     * @return a predicate representing (left <= right)
     */
    default Predicate lessThanOrEqual(Expression left, Expression right) {
        return Predicates.lessThanOrEqual(left, right);
    }

    /**
     * Creates a LIKE predicate for matching patterns.
     *
     * @param left the expression to be matched
     * @param right the pattern string
     * @return a predicate representing (left LIKE right)
     */
    default Predicate like(Expression left, String right) {
        return Predicates.like(left, right);
    }

    /**
     * Creates a NOT LIKE predicate for excluding patterns.
     *
     * @param left the expression to be matched
     * @param right the pattern string
     * @return a predicate representing (left NOT LIKE right)
     */
    default Predicate notLike(Expression left, String right) {
        return Predicates.notLike(left, right);
    }

    /**
     * Creates an IS NULL predicate.
     *
     * @param left the expression to be checked
     * @return a predicate representing (left IS NULL)
     */
    default Predicate isNull(Expression left) {
        return Predicates.isNull(left);
    }

    /**
     * Creates an IS NOT NULL predicate.
     *
     * @param left the expression to be checked
     * @return a predicate representing (left IS NOT NULL)
     */
    default Predicate isNotNull(Expression left) {
        return Predicates.isNotNull(left);
    }

    /**
     * Creates an EXISTS predicate.
     *
     * @param left the subquery or expression to be checked
     * @return a predicate representing (EXISTS left)
     */
    default Predicate exists(Expression left) {
        return Predicates.exists(left);
    }

    /**
     * Creates a NOT EXISTS predicate.
     *
     * @param left the subquery or expression to be checked
     * @return a predicate representing (NOT EXISTS left)
     */
    default Predicate notExists(Expression left) {
        return Predicates.notExists(left);
    }

    /**
     * Creates an IN predicate using a subquery.
     *
     * @param left the expression to check for membership
     * @param subquery the subquery representing the target set
     * @return a predicate representing (left IN subquery)
     */
    default Predicate in(Expression left, SubqueryExpression subquery) {
        return Predicates.in(left, subquery);
    }

    /**
     * Creates an IN predicate using a list of constant values.
     *
     * @param left the expression to check for membership
     * @param values the constant list to compare with
     * @return a predicate representing (left IN values)
     */
    default Predicate in(Expression left, List<Object> values) {
        return Predicates.in(left, values);
    }

    /**
     * Creates a NOT IN predicate using a subquery.
     *
     * @param left the expression to check for exclusion
     * @param subquery the subquery representing the target set
     * @return a predicate representing (left NOT IN subquery)
     */
    default Predicate notIn(Expression left, SubqueryExpression subquery) {
        return Predicates.notIn(left, subquery);
    }

    /**
     * Creates a NOT IN predicate using a list of constant values.
     *
     * @param left the expression to check for exclusion
     * @param values the constant list to compare with
     * @return a predicate representing (left NOT IN values)
     */
    default Predicate notIn(Expression left, List<Object> values) {
        return Predicates.notIn(left, values);
    }

    /**
     * Creates a BETWEEN predicate with expression boundaries.
     *
     * @param expression the expression to compare
     * @param min the minimum value as expression
     * @param max the maximum value as expression
     * @return a predicate representing (expression BETWEEN min AND max)
     */
    default Predicate between(Expression expression, Expression min, Expression max) {
        return Predicates.between(expression, min, max);
    }

    /**
     * Creates a BETWEEN predicate with constant boundaries.
     *
     * @param expression the expression to compare
     * @param min the minimum constant value
     * @param max the maximum constant value
     * @return a predicate representing (expression BETWEEN min AND max)
     */
    default Predicate between(Expression expression, Object min, Object max) {
        return Predicates.between(expression, min, max);
    }
}
