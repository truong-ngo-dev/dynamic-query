package vn.truongngo.lib.dynamicquery.core.support;

import vn.truongngo.lib.dynamicquery.core.enumerate.LogicalOperator;
import vn.truongngo.lib.dynamicquery.core.enumerate.Operator;
import vn.truongngo.lib.dynamicquery.core.expression.Expression;
import vn.truongngo.lib.dynamicquery.core.expression.SubqueryExpression;
import vn.truongngo.lib.dynamicquery.core.expression.predicate.ComparisonPredicate;
import vn.truongngo.lib.dynamicquery.core.expression.predicate.LogicalPredicate;
import vn.truongngo.lib.dynamicquery.core.expression.predicate.Predicate;

import java.util.List;

/**
 * Utility class for building {@link Predicate} expressions in a fluent style.
 * <p>
 * Provides factory methods for comparison, logical, nullity, and subquery-based predicates.
 * </p>
 *
 * <p>Example usage:
 * <blockquote><pre>
 * Predicate predicate = Predicates.and(
 *     Predicates.equal(Expressions.column("age", User.class), 30),
 *     Predicates.isNotNull(Expressions.column("email", User.class))
 * );
 * </pre></blockquote>
 * </p>
 *
 * @author Truong Ngo
 * @version 1.0
 */
public class Predicates {

    /**
     * Builds a binary comparison predicate between an expression and a constant.
     *
     * @param lhs the left-hand side expression
     * @param operator the comparison operator
     * @param rhs the right-hand side constant value
     * @return the resulting {@link Predicate}
     */
    private static Predicate binaryPredicate(Expression lhs, Operator operator, Object rhs) {
        return new ComparisonPredicate(lhs, operator, Expressions.constant(rhs));
    }

    /**
     * Builds a binary comparison predicate between two expressions.
     *
     * @param lhs the left-hand side expression
     * @param operator the comparison operator
     * @param rhs the right-hand side expression
     * @return the resulting {@link Predicate}
     */
    private static Predicate binaryPredicate(Expression lhs, Operator operator, Expression rhs) {
        return new ComparisonPredicate(lhs, operator, rhs);
    }

    /**
     * Negates a given predicate.
     *
     * @param predicate the predicate to negate
     * @return the negated predicate
     */
    public static Predicate not(Predicate predicate) {
        predicate.setNegated(true);
        return predicate;
    }

    /**
     * Combines two predicates using logical AND.
     *
     * @param lhs the left-hand side predicate
     * @param rhs the right-hand side predicate
     * @return a new predicate representing (lhs AND rhs)
     */
    public static Predicate and(Predicate lhs, Predicate rhs) {
        return new LogicalPredicate(List.of(lhs, rhs), LogicalOperator.AND);
    }

    /**
     * Combines two predicates using logical OR.
     *
     * @param lhs the left-hand side predicate
     * @param rhs the right-hand side predicate
     * @return a new predicate representing (lhs OR rhs)
     */
    public static Predicate or(Predicate lhs, Predicate rhs) {
        return new LogicalPredicate(List.of(lhs, rhs), LogicalOperator.OR);
    }

    /**
     * Combines multiple predicates using logical OR (any match).
     *
     * @param predicates the predicates to combine
     * @return a new predicate representing (p1 OR p2 OR ...)
     */
    public static Predicate anyOf(Predicate... predicates) {
        return new LogicalPredicate(List.of(predicates), LogicalOperator.OR);
    }

    /**
     * Combines multiple predicates using logical AND (all must match).
     *
     * @param predicates the predicates to combine
     * @return a new predicate representing (p1 AND p2 AND ...)
     */
    public static Predicate allOf(Predicate... predicates) {
        return new LogicalPredicate(List.of(predicates), LogicalOperator.AND);
    }

    /**
     * Returns a predicate that always evaluates to true (1 = 1).
     *
     * @return the always-true predicate
     */
    public static Predicate conjunction() {
        return equal(Expressions.constant(1), Expressions.constant(1));
    }

    /**
     * Returns a predicate that always evaluates to false (1 != 1).
     *
     * @return the always-false predicate
     */
    public static Predicate disjunction() {
        return notEqual(Expressions.constant(1), Expressions.constant(1));
    }


    /**
     * Creates an equality predicate comparing an expression to a constant.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side constant value
     * @return a predicate representing (left = right)
     */
    public static Predicate equal(Expression left, Object right) {
        return binaryPredicate(left, Operator.EQUAL, right);
    }

    /**
     * Creates an equality predicate comparing two expressions.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side expression
     * @return a predicate representing (left = right)
     */
    public static Predicate equal(Expression left, Expression right) {
        return binaryPredicate(left, Operator.EQUAL, right);
    }

    /**
     * Creates a not-equal predicate comparing an expression to a constant.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side constant value
     * @return a predicate representing (left != right)
     */
    public static Predicate notEqual(Expression left, Object right) {
        return binaryPredicate(left, Operator.NOT_EQUAL, right);
    }

    /**
     * Creates a not-equal predicate comparing two expressions.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side expression
     * @return a predicate representing (left != right)
     */
    public static Predicate notEqual(Expression left, Expression right) {
        return binaryPredicate(left, Operator.NOT_EQUAL, right);
    }

    /**
     * Creates a greater-than predicate comparing an expression to a constant.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side constant value
     * @return a predicate representing (left > right)
     */
    public static Predicate greaterThan(Expression left, Object right) {
        return binaryPredicate(left, Operator.GREATER_THAN, right);
    }

    /**
     * Creates a greater-than predicate comparing two expressions.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side expression
     * @return a predicate representing (left > right)
     */
    public static Predicate greaterThan(Expression left, Expression right) {
        return binaryPredicate(left, Operator.GREATER_THAN, right);
    }

    /**
     * Creates a less-than predicate comparing an expression to a constant.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side constant value
     * @return a predicate representing (left < right)
     */
    public static Predicate lessThan(Expression left, Object right) {
        return binaryPredicate(left, Operator.LESS_THAN, right);
    }

    /**
     * Creates a less-than predicate comparing two expressions.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side expression
     * @return a predicate representing (left < right)
     */
    public static Predicate lessThan(Expression left, Expression right) {
        return binaryPredicate(left, Operator.LESS_THAN, right);
    }

    /**
     * Creates a greater-than-or-equal predicate comparing an expression to a constant.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side constant value
     * @return a predicate representing (left >= right)
     */
    public static Predicate greaterThanOrEqual(Expression left, Object right) {
        return binaryPredicate(left, Operator.GREATER_THAN_EQUAL, right);
    }

    /**
     * Creates a greater-than-or-equal predicate comparing two expressions.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side expression
     * @return a predicate representing (left >= right)
     */
    public static Predicate greaterThanOrEqual(Expression left, Expression right) {
        return binaryPredicate(left, Operator.GREATER_THAN_EQUAL, right);
    }

    /**
     * Creates a less-than-or-equal predicate comparing an expression to a constant.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side constant value
     * @return a predicate representing (left <= right)
     */
    public static Predicate lessThanOrEqual(Expression left, Object right) {
        return binaryPredicate(left, Operator.LESS_THAN_EQUAL, right);
    }

    /**
     * Creates a less-than-or-equal predicate comparing two expressions.
     *
     * @param left the left-hand side expression
     * @param right the right-hand side expression
     * @return a predicate representing (left <= right)
     */
    public static Predicate lessThanOrEqual(Expression left, Expression right) {
        return binaryPredicate(left, Operator.LESS_THAN_EQUAL, right);
    }

    /**
     * Creates a LIKE predicate for matching patterns.
     *
     * @param left the expression to be matched
     * @param right the pattern string
     * @return a predicate representing (left LIKE right)
     */
    public static Predicate like(Expression left, String right) {
        return binaryPredicate(left, Operator.LIKE, right);
    }

    /**
     * Creates a NOT LIKE predicate for excluding patterns.
     *
     * @param left the expression to be matched
     * @param right the pattern string
     * @return a predicate representing (left NOT LIKE right)
     */
    public static Predicate notLike(Expression left, String right) {
        return binaryPredicate(left, Operator.NOT_LIKE, right);
    }

    /**
     * Creates an IS NULL predicate.
     *
     * @param left the expression to be checked
     * @return a predicate representing (left IS NULL)
     */
    public static Predicate isNull(Expression left) {
        return new ComparisonPredicate(left, Operator.IS_NULL, null);
    }

    /**
     * Creates an IS NOT NULL predicate.
     *
     * @param left the expression to be checked
     * @return a predicate representing (left IS NOT NULL)
     */
    public static Predicate isNotNull(Expression left) {
        return new ComparisonPredicate(left, Operator.IS_NOT_NULL, null);
    }

    /**
     * Creates an EXISTS predicate.
     *
     * @param left the subquery or expression to be checked
     * @return a predicate representing (EXISTS left)
     */
    public static Predicate exists(Expression left) {
        return new ComparisonPredicate(left, Operator.EXISTS, null);
    }

    /**
     * Creates a NOT EXISTS predicate.
     *
     * @param left the subquery or expression to be checked
     * @return a predicate representing (NOT EXISTS left)
     */
    public static Predicate notExists(Expression left) {
        return new ComparisonPredicate(left, Operator.NOT_EXISTS, null);
    }

    /**
     * Creates an IN predicate using a subquery.
     *
     * @param left the expression to check for membership
     * @param subquery the subquery representing the target set
     * @return a predicate representing (left IN subquery)
     */
    public static Predicate in(Expression left, SubqueryExpression subquery) {
        return new ComparisonPredicate(left, Operator.IN, subquery);
    }

    /**
     * Creates an IN predicate using a list of constant values.
     *
     * @param left the expression to check for membership
     * @param values the constant list to compare with
     * @return a predicate representing (left IN values)
     */
    public static Predicate in(Expression left, List<Object> values) {
        Expression right = Expressions.constant(values);
        return new ComparisonPredicate(left, Operator.IN, right);
    }

    /**
     * Creates a NOT IN predicate using a subquery.
     *
     * @param left the expression to check for exclusion
     * @param subquery the subquery representing the target set
     * @return a predicate representing (left NOT IN subquery)
     */
    public static Predicate notIn(Expression left, SubqueryExpression subquery) {
        return new ComparisonPredicate(left, Operator.NOT_IN, subquery);
    }

    /**
     * Creates a NOT IN predicate using a list of constant values.
     *
     * @param left the expression to check for exclusion
     * @param values the constant list to compare with
     * @return a predicate representing (left NOT IN values)
     */
    public static Predicate notIn(Expression left, List<Object> values) {
        Expression right = Expressions.constant(values);
        return new ComparisonPredicate(left, Operator.NOT_IN, right);
    }

    /**
     * Creates a BETWEEN predicate with expression boundaries.
     *
     * @param expression the expression to compare
     * @param min the minimum value as expression
     * @param max the maximum value as expression
     * @return a predicate representing (expression BETWEEN min AND max)
     */
    public static Predicate between(Expression expression, Expression min, Expression max) {
        return and(greaterThanOrEqual(expression, min), lessThanOrEqual(expression, max));
    }

    /**
     * Creates a BETWEEN predicate with constant boundaries.
     *
     * @param expression the expression to compare
     * @param min the minimum constant value
     * @param max the maximum constant value
     * @return a predicate representing (expression BETWEEN min AND max)
     */
    public static Predicate between(Expression expression, Object min, Object max) {
        return and(greaterThanOrEqual(expression, min), lessThanOrEqual(expression, max));
    }

}
