package vn.truongngo.lib.dynamicquery.projection.parser;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

/**
 * Utility class for analyzing JSQLParser {@link Expression} types.
 * <p>
 * Provides helpers to determine the nature of a given SQL expression,
 * such as whether it is a constant, arithmetic operation, or logical condition.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public class JSqlUtils {

    /**
     * Checks whether the given expression is a constant value.
     *
     * <p>Supported constant types include:</p>
     * <ul>
     *     <li>{@link StringValue}, {@link LongValue}, {@link DoubleValue}</li>
     *     <li>{@link NullValue}, {@link DateValue}, {@link TimeValue}, {@link TimestampValue}</li>
     *     <li>{@link HexValue}, {@link ExpressionList}</li>
     *     <li>{@link SignedExpression} wrapping a constant</li>
     * </ul>
     *
     * @param expr the expression to check
     * @return {@code true} if the expression is a constant, {@code false} otherwise
     */
    public static boolean isConstant(Expression expr) {
        if (expr instanceof StringValue ||
                expr instanceof LongValue ||
                expr instanceof DoubleValue ||
                expr instanceof NullValue ||
                expr instanceof DateValue ||
                expr instanceof TimeValue ||
                expr instanceof TimestampValue ||
                expr instanceof HexValue ||
                expr instanceof ExpressionList) {
            return true;
        }
        if (expr instanceof SignedExpression signedExpression) {
            return isConstant(signedExpression.getExpression());
        }
        return false;
    }

    /**
     * Checks whether the given expression is an arithmetic operation.
     *
     * <p>Supported arithmetic operators include:</p>
     * <ul>
     *     <li>{@link Addition}, {@link Subtraction}</li>
     *     <li>{@link Multiplication}, {@link Division}, {@link Modulo}</li>
     * </ul>
     *
     * @param expr the expression to check
     * @return {@code true} if the expression is an arithmetic operation, {@code false} otherwise
     */
    public static boolean isArithmetic(Expression expr) {
        return expr instanceof Addition ||
                expr instanceof Subtraction ||
                expr instanceof Multiplication ||
                expr instanceof Division ||
                expr instanceof Modulo;
    }

    /**
     * Checks whether the given binary expression is a logical condition.
     *
     * <p>Supported logical expressions include:</p>
     * <ul>
     *     <li>{@link AndExpression}</li>
     *     <li>{@link OrExpression}</li>
     * </ul>
     *
     * @param expr the binary expression to check
     * @return {@code true} if the expression is a logical condition, {@code false} otherwise
     */
    public static boolean isLogical(BinaryExpression expr) {
        return expr instanceof AndExpression ||
               expr instanceof OrExpression;
    }

}
