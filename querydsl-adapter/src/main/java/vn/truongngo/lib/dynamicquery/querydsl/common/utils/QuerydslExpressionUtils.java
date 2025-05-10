package vn.truongngo.lib.dynamicquery.querydsl.common.utils;

import com.querydsl.core.types.*;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.WindowFunction;
import com.querydsl.sql.WindowOver;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.enumerate.LogicalOperator;
import vn.truongngo.lib.dynamicquery.core.enumerate.Operator;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderExpression;

import java.util.List;

public class QuerydslExpressionUtils {

    /**
     * Builds a Querydsl window function expression from a custom {@link WindowFunctionExpression}.
     * Supports standard SQL window functions such as ROW_NUMBER, RANK, DENSE_RANK, SUM, AVG, COUNT, MAX, MIN.
     *
     * @param expression the window function expression
     * @param visitor the expression visitor
     * @param context the visitor context
     * @return the Querydsl expression representing the window function
     * @param <C> the type of visitor context
     */
    @SuppressWarnings("all")
    public static <C> Expression<?> windowFunction(WindowFunctionExpression expression, Visitor<Expression<?>, C> visitor, C context) {

        Expression[] args = expression.getArguments().stream()
                .map(arg -> arg.accept(visitor, context))
                .toArray(Expression[]::new);

        WindowOver<?> baseFunction = switch (expression.getFunctionName()) {
            case "ROW_NUMBER" -> SQLExpressions.rowNumber();
            case "RANK" -> SQLExpressions.rank();
            case "DENSE_RANK" -> SQLExpressions.denseRank();
            case "SUM" -> SQLExpressions.sum(args[0]);
            case "AVG" -> SQLExpressions.avg(args[0]);
            case "COUNT" -> SQLExpressions.count((args[0]));
            case "MAX" -> SQLExpressions.max(args[0]);
            case "MIN" -> SQLExpressions.min(args[0]);
            default -> throw new UnsupportedOperationException("Unsupported window function: " + expression.getFunctionName());
        };

        WindowFunction<?> window = baseFunction.over();

        if (!expression.getPartitionBy().isEmpty()) {
            Expression<?>[] partitionExprs = expression.getPartitionBy().stream()
                    .map(p -> p.accept(visitor, context))
                    .toArray(Expression[]::new);
            window = window.partitionBy(partitionExprs);
        }

        if (!expression.getOrderBy().isEmpty()) {
            OrderSpecifier[] orderSpecifiers = expression.getOrderBy()
                    .stream()
                    .map(op -> order(op, visitor, context))
                    .toArray(OrderSpecifier[]::new);
            window = window.orderBy(orderSpecifiers);
        }

        return window;
    }

    /**
     * Builds a Querydsl {@link OrderSpecifier} from a custom {@link OrderExpression}.
     *
     * @param orderExpression the custom order specifier
     * @param visitor the expression visitor
     * @param context the visitor context
     * @return the Querydsl order specifier
     * @param <C> the type of visitor context
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <C> OrderSpecifier<?> order(OrderExpression orderExpression, Visitor<Expression<?>, C> visitor, C context) {
        Expression target = orderExpression.getTarget().accept(visitor, context);
        Order order = orderExpression.getOrder().equals(vn.truongngo.lib.dynamicquery.core.enumerate.Order.ASC) ? Order.ASC : Order.DESC;
        return new OrderSpecifier<>(order, target);
    }

    /**
     * Builds a Querydsl case-when-then-else expression from a custom {@link CaseWhenExpression}.
     *
     * @param expression the case-when expression
     * @param visitor the expression visitor
     * @param context the visitor context
     * @return the Querydsl expression representing the case-when-else structure
     * @param <C> the type of visitor context
     */
    @SuppressWarnings("all")
    public static <C> Expression<?> caseWhen(CaseWhenExpression expression, Visitor<Expression<?>, C> visitor, C context) {
        CaseBuilder builder = new CaseBuilder();
        CaseBuilder.Cases<?, ?> caseExpr = null;

        for (CaseWhenExpression.WhenThen whenThen : expression.getConditions()) {
            Predicate when = (Predicate) whenThen.when().accept(visitor, context);
            Expression then = whenThen.then().accept(visitor, context);
            if (caseExpr == null) {
                caseExpr = builder.when(when).then(then);
            } else {
                caseExpr = caseExpr.when(when).then(then);
            }
        }

        Expression otherwise = expression.getElseExpression().accept(visitor, context);
        return caseExpr.otherwise(otherwise);
    }

    /**
     * Builds a custom SQL function expression using Querydsl's {@link Expressions#template} method.
     *
     * @param expression the function expression
     * @param visitor the expression visitor
     * @param context the visitor context
     * @return the Querydsl expression representing the SQL function
     * @param <C> the type of visitor context
     */
    public static <C> Expression<?> function(FunctionExpression expression, Visitor<Expression<?>, C> visitor, C context) {
        List<? extends Expression<?>> args = expression.getParameters().stream()
                .map(param -> param.accept(visitor, context))
                .toList();

        StringBuilder templateBuilder = new StringBuilder(expression.getFunctionName()).append("(");
        for (int i = 0; i < args.size(); i++) {
            templateBuilder.append("{").append(i).append("}");
            if (i < args.size() - 1) {
                templateBuilder.append(", ");
            }
        }
        templateBuilder.append(")");
        String template = templateBuilder.toString();

        Expression<?> result = Expressions.template(Object.class, template, args.toArray());

        return expression.getAlias() != null
                ? Expressions.template(Object.class, "{0} as " + expression.getAlias(), result)
                : result;
    }

    /**
     * Builds a Querydsl arithmetic expression (add, subtract, multiply, divide, modulo).
     *
     * @param expression the arithmetic expression
     * @param visitor the expression visitor
     * @param context the visitor context
     * @return the Querydsl arithmetic expression
     * @throws IllegalArgumentException if operands are not {@link NumberExpression}
     * @param <C> the type of visitor context
     */
    @SuppressWarnings("all")
    public static <C> Expression<?> arithmetic(ArithmeticExpression expression, Visitor<Expression<?>, C> visitor, C context) {
        Expression<?> left = expression.getLeft().accept(visitor, context);
        Expression right = expression.getRight().accept(visitor, context);

        if (!(left instanceof NumberExpression<?> leftNum) || !(right instanceof NumberExpression<?> rightNum)) {
            throw new IllegalArgumentException("Arithmetic operations require NumberExpression types");
        }

        return switch (expression.getOperator()) {
            case ADD -> leftNum.add(rightNum);
            case SUBTRACT -> leftNum.subtract(rightNum);
            case MULTIPLY -> leftNum.multiply(rightNum);
            case DIVIDE -> leftNum.divide(rightNum);
            case MODULO -> leftNum.mod(right);
        };
    }

    /**
     * Builds a Querydsl {@link Predicate} from a comparison predicate expression.
     * Supported operators include EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, BETWEEN, IS_NULL, EXISTS, etc.
     *
     * @param expression the comparison predicate
     * @param visitor the expression visitor
     * @param context the visitor context
     * @return the Querydsl predicate
     * @param <C> the type of visitor context
     */
    public static <C> Predicate comparison(ComparisonPredicate expression, Visitor<Expression<?>, C> visitor, C context) {
        Expression<?> left = expression.getLeft().accept(visitor, context);
        Expression<?> right = expression.getRight() != null ? expression.getRight().accept(visitor, context) : null;
        Operator operator = expression.getOperator();
        return switch (operator) {
            case EQUAL -> Expressions.predicate(Ops.EQ, left, right);
            case NOT_EQUAL -> Expressions.predicate(Ops.NE, left, right);
            case GREATER_THAN -> Expressions.predicate(Ops.GT, left, right);
            case LESS_THAN -> Expressions.predicate(Ops.LT, left, right);
            case GREATER_THAN_EQUAL -> Expressions.predicate(Ops.GOE, left, right);
            case LESS_THAN_EQUAL -> Expressions.predicate(Ops.LOE, left, right);
            case LIKE -> Expressions.predicate(Ops.LIKE, left, right);
            case NOT_LIKE -> Expressions.predicate(Ops.LIKE, left, right).not();
            case BETWEEN -> Expressions.predicate(Ops.BETWEEN, left, right);
            case NOT_BETWEEN -> Expressions.predicate(Ops.BETWEEN, left, right).not();
            case IS_NULL -> Expressions.predicate(Ops.IS_NULL, left);
            case IS_NOT_NULL -> Expressions.predicate(Ops.IS_NOT_NULL, left);
            case EXISTS -> Expressions.predicate(Ops.EXISTS, left);
            case NOT_EXISTS -> Expressions.predicate(Ops.EXISTS, left).not();
            case IN -> Expressions.predicate(Ops.IN, left, right);
            case NOT_IN -> Expressions.predicate(Ops.NOT_IN, left, right);
        };
    }

    /**
     * Builds a Querydsl {@link Predicate} from a logical predicate (AND/OR).
     *
     * @param expression the logical predicate expression
     * @param visitor the expression visitor
     * @param context the visitor context
     * @return the Querydsl predicate
     * @param <C> the type of visitor context
     */
    public static <C> Predicate logical(LogicalPredicate expression, Visitor<Expression<?>, C> visitor, C context) {
        LogicalOperator operator = expression.getOperator();
        List<? extends Expression<?>> predicates = expression.getPredicates().stream()
                .map(p -> p.accept(visitor, context))
                .toList();

        return switch (operator) {
            case AND -> Expressions.allOf((BooleanExpression) predicates);
            case OR -> Expressions.anyOf((BooleanExpression) predicates);
        };
    }

}
