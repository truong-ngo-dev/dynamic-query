package vn.truongngo.lib.dynamicquery.core.builder;

import vn.truongngo.lib.dynamicquery.core.expression.ConstantExpression;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.core.expression.predicate.ComparisonPredicate;
import vn.truongngo.lib.dynamicquery.core.expression.predicate.LogicalPredicate;

/**
 * The Visitor interface is part of the Visitor design pattern used to traverse and
 * process different types of query expressions. This interface defines methods to
 * visit various expression types, allowing for operations such as transformation
 * or evaluation.
 * <p>
 * This allows different types of expressions (e.g., constants, columns, functions)
 * to be processed in a uniform way, providing flexibility to handle these expressions
 * without modifying their concrete classes.
 * </p>
 *
 * @param <R> The result type that the visitor returns after visiting an expression.
 * @param <C> The context type that may be passed along with each visit.
 *
 * @see Expression
 * @see ConstantExpression
 * @see EntityReferenceExpression
 * @see ColumnReferenceExpression
 * @see FunctionExpression
 * @see CaseWhenExpression
 * @see SubqueryExpression
 * @see ComparisonPredicate
 * @see LogicalPredicate
 *
 * @author Truong Ngo
 * @version 1.0
 */
public interface Visitor<R, C> {

    /**
     * Visits a generic expression.
     *
     * @param expression The expression to visit.
     * @param context The context to pass during the visit.
     * @return The result after processing the expression.
     */
    R visit(Expression expression, C context);

    /**
     * Visits a constant expression.
     *
     * @param expression The constant expression to visit.
     * @param context The context to pass during the visit.
     * @return The result after processing the constant expression.
     */
    R visit(ConstantExpression expression, C context);

    /**
     * Visits an entity reference expression.
     *
     * @param expression The entity reference expression to visit.
     * @param context The context to pass during the visit.
     * @return The result after processing the entity reference expression.
     */
    R visit(EntityReferenceExpression expression, C context);

    /**
     * Visits a column reference expression.
     *
     * @param expression The column reference expression to visit.
     * @param context The context to pass during the visit.
     * @return The result after processing the column reference expression.
     */
    R visit(ColumnReferenceExpression expression, C context);

    /**
     * Visits a function expression.
     *
     * @param expression The function expression to visit.
     * @param context The context to pass during the visit.
     * @return The result after processing the function expression.
     */
    R visit(FunctionExpression expression, C context);

    /**
     * Visits a case-when expression.
     *
     * @param expression The case-when expression to visit.
     * @param context The context to pass during the visit.
     * @return The result after processing the case-when expression.
     */
    R visit(CaseWhenExpression expression, C context);

    /**
     * Visits a subquery expression.
     *
     * @param expression The subquery expression to visit.
     * @param context The context to pass during the visit.
     * @return The result after processing the subquery expression.
     */
    R visit(SubqueryExpression expression, C context);

    /**
     * Visits a comparison predicate.
     *
     * @param expression The comparison predicate to visit.
     * @param context The context to pass during the visit.
     * @return The result after processing the comparison predicate.
     */
    R visit(ComparisonPredicate expression, C context);

    /**
     * Visits a logical predicate.
     *
     * @param expression The logical predicate to visit.
     * @param context The context to pass during the visit.
     * @return The result after processing the logical predicate.
     */
    R visit(LogicalPredicate expression, C context);
}
