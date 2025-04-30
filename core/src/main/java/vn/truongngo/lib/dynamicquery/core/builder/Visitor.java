package vn.truongngo.lib.dynamicquery.core.builder;

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
     * <p>
     * This method will check for type of expression and delegate to concrete visitor method
     *
     * @param expression The expression to visit.
     * @param context The context to pass during the visit.
     * @return The result after processing the expression.
     */
    default R visit(Expression expression, C context) {
        if (expression instanceof ConstantExpression constant) return visit(constant, context);
        if (expression instanceof EntityReferenceExpression entityRef) return visit(entityRef, context);
        if (expression instanceof ColumnReferenceExpression columnRef) return visit(columnRef, context);
        if (expression instanceof FunctionExpression function) return visit(function, context);
        if (expression instanceof CaseWhenExpression caseWhen) return visit(caseWhen, context);
        if (expression instanceof SubqueryExpression subquery) return visit(subquery, context);
        if (expression instanceof ComparisonPredicate comparison) return visit(comparison, context);
        if (expression instanceof LogicalPredicate logical) return visit(logical, context);
        throw new IllegalArgumentException("Unsupported expression type: " + expression.getClass());
    };

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
