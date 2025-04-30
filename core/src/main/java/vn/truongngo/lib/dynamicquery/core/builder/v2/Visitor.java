package vn.truongngo.lib.dynamicquery.core.builder.v2;

import vn.truongngo.lib.dynamicquery.core.expression.v2.*;

/**
 * Visitor interface for traversing and processing different types of expressions.
 * <p>
 * The visitor pattern allows you to implement custom behavior for each type of expression
 * without modifying the expression classes themselves. This is useful for extending and maintaining
 * your expression system in a flexible way.
 * </p>
 *
 * @param <R> the result type of the visitor's operations
 * @param <C> the context type used during visitation (e.g., a context object that is passed to each visit method)
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface Visitor<R, C> {

    /**
     * Default method to handle expressions by delegating to the specific visit method
     * for each type of expression.
     * <p>
     * This method is invoked when a new expression is encountered, and it checks the type
     * of the expression. If the expression is of a known type, it delegates to the corresponding
     * visit method for that type.
     * </p>
     *
     * @param expression the expression to visit
     * @param context the context object passed to each visit method
     * @return the result of the visitation
     * @throws IllegalArgumentException if the expression type is not supported
     */
    default R visit(Expression expression, C context) {
        if (expression instanceof ConstantExpression constant) return visit(constant, context);
        if (expression instanceof ArithmeticExpression arithmetic) return visit(arithmetic, context);
        if (expression instanceof ColumnReferenceExpression columnRef) return visit(columnRef, context);
        if (expression instanceof FunctionExpression function) return visit(function, context);
        if (expression instanceof CaseWhenExpression caseWhen) return visit(caseWhen, context);
        if (expression instanceof SubqueryExpression subquery) return visit(subquery, context);
        if (expression instanceof WindowFunctionExpression windowFunction) return visit(windowFunction, context);
        if (expression instanceof EntityReferenceExpression entityRef) return visit(entityRef, context);
        if (expression instanceof CommonTableExpression cte) return visit(cte, context);
        if (expression instanceof SetOperationExpression setOperation) return visit(setOperation, context);
        if (expression instanceof ComparisonPredicate comparison) return visit(comparison, context);
        if (expression instanceof LogicalPredicate logical) return visit(logical, context);
        if (expression instanceof ExtendedExpression extended) return visit(extended, context);
        throw new IllegalArgumentException("Unsupported expression: " + expression.getClass().getName());
    }

    /**
     * Visit method for handling {@link ConstantExpression}.
     *
     * @param expression the constant expression to process
     * @param context the context for visiting
     * @return the result of the visitation
     */
    R visit(ConstantExpression expression, C context);

    /**
     * Visit method for handling {@link ArithmeticExpression}.
     *
     * @param expression the arithmetic expression to process
     * @param context the context for visiting
     * @return the result of the visitation
     */
    R visit(ArithmeticExpression expression, C context);

    /**
     * Visit method for handling {@link ColumnReferenceExpression}.
     *
     * @param expression the column reference expression to process
     * @param context the context for visiting
     * @return the result of the visitation
     */
    R visit(ColumnReferenceExpression expression, C context);

    /**
     * Visit method for handling {@link FunctionExpression}.
     *
     * @param expression the function expression to process
     * @param context the context for visiting
     * @return the result of the visitation
     */
    R visit(FunctionExpression expression, C context);

    /**
     * Visit method for handling {@link CaseWhenExpression}.
     *
     * @param expression the case-when expression to process
     * @param context the context for visiting
     * @return the result of the visitation
     */
    R visit(CaseWhenExpression expression, C context);

    /**
     * Visit method for handling {@link SubqueryExpression}.
     *
     * @param expression the subquery expression to process
     * @param context the context for visiting
     * @return the result of the visitation
     */
    R visit(SubqueryExpression expression, C context);

    /**
     * Visit method for handling {@link WindowFunctionExpression}.
     *
     * @param expression the window function expression to process
     * @param context the context for visiting
     * @return the result of the visitation
     */
    R visit(WindowFunctionExpression expression, C context);

    /**
     * Visit method for handling {@link EntityReferenceExpression}.
     *
     * @param expression the entity reference expression to process
     * @param context the context for visiting
     * @return the result of the visitation
     */
    R visit(EntityReferenceExpression expression, C context);

    /**
     * Visit method for handling {@link CommonTableExpression}.
     *
     * @param expression the common table expression to process
     * @param context the context for visiting
     * @return the result of the visitation
     */
    R visit(CommonTableExpression expression, C context);

    /**
     * Visit method for handling {@link SetOperationExpression}.
     *
     * @param expression the set operation expression to process
     * @param context the context for visiting
     * @return the result of the visitation
     */
    R visit(SetOperationExpression expression, C context);

    /**
     * Visit method for handling {@link ComparisonPredicate}.
     *
     * @param expression the comparison predicate to process
     * @param context the context for visiting
     * @return the result of the visitation
     */
    R visit(ComparisonPredicate expression, C context);

    /**
     * Visit method for handling {@link LogicalPredicate}.
     *
     * @param expression the logical predicate to process
     * @param context the context for visiting
     * @return the result of the visitation
     */
    R visit(LogicalPredicate expression, C context);

    /**
     * Visit method for handling {@link ExtendedExpression}.
     *
     * @param expression the extended expression to process
     * @param context the context for visiting
     * @return the result of the visitation
     */
    R visit(ExtendedExpression expression, C context);

}

