package vn.truongngo.lib.dynamicquery.projection.parser;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.ParenthesedSelect;
import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;
import vn.truongngo.lib.dynamicquery.core.enumerate.ArithmeticOperator;
import vn.truongngo.lib.dynamicquery.core.enumerate.LogicalOperator;
import vn.truongngo.lib.dynamicquery.core.enumerate.Operator;
import vn.truongngo.lib.dynamicquery.core.expression.*;
import vn.truongngo.lib.dynamicquery.core.support.Predicates;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code DefaultExpressionParser} class provides a default implementation of the
 * {@link ExpressionParser} interface. It is responsible for parsing SQL expressions
 * (represented by JSQLParser's {@link Expression} objects) and converting them into
 * the corresponding internal expression representations used by the dynamic query system.
 *
 * <p>This class supports parsing various types of SQL expressions, including:</p>
 * <ul>
 *   <li>Constants (e.g., numbers, strings, nulls, dates)</li>
 *   <li>Arithmetic expressions (e.g., addition, multiplication)</li>
 *   <li>Column references</li>
 *   <li>Function calls</li>
 *   <li>Case-When expressions</li>
 *   <li>Comparison and logical predicates (e.g., =, &lt;, &gt;, AND, OR, BETWEEN)</li>
 * </ul>
 *
 * <p>Unsupported or unimplemented expressions such as window functions or subqueries
 * will throw an {@link UnsupportedOperationException} or return {@code null} (if not yet implemented).</p>
 *
 * <h2>Example usage:</h2>
 * <blockquote><pre>
 * Expression sqlExpr = CCJSqlParserUtil.parseExpression("age &gt; 18 AND active = true");
 * Predicate predicate = parser.parsePredicate(sqlExpr, queryMetadata);
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 2.0.0
 * @see vn.truongngo.lib.dynamicquery.core.expression.Expression
 * @see QueryMetadata
 * @see ExpressionParser
 */
public class DefaultExpressionParser implements ExpressionParser {

    /**
     * Holder class for the singleton instance of {@link DefaultExpressionParser}.
     * <p>
     * This leverages the initialization-on-demand holder idiom to ensure
     * thread-safe, lazy initialization of the singleton instance.
     */
    private static class Holder {
        private static final DefaultExpressionParser INSTANCE = new DefaultExpressionParser();
    }

    /**
     * Returns the singleton instance of {@link DefaultExpressionParser}.
     * <p>
     * This method uses the initialization-on-demand holder idiom for lazy, thread-safe instantiation.
     *
     * @return the singleton {@code DefaultExpressionParser} instance
     */
    public static DefaultExpressionParser getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Parses a constant SQL expression into a {@link ConstantExpression}.
     *
     * <p>This method supports various literal types including:</p>
     * <ul>
     *   <li>{@link LongValue}, {@link DoubleValue}</li>
     *   <li>{@link StringValue}, {@link BooleanValue}, {@link NullValue}</li>
     *   <li>{@link HexValue}, {@link DateValue}, {@link TimeValue}, {@link TimestampValue}</li>
     * </ul>
     *
     * @param expression the constant expression from JSQLParser
     * @param context the query metadata context
     * @return a {@link ConstantExpression} representing the literal value
     * @throws IllegalArgumentException if the expression type is unsupported
     */
    @Override
    public ConstantExpression parseConstant(Expression expression, QueryMetadata context) {
        if (expression instanceof LongValue longValue) {
            return new ConstantExpression(longValue.getValue());
        }
        if (expression instanceof DoubleValue doubleValue) {
            return new ConstantExpression(doubleValue.getValue());
        }
        if (expression instanceof StringValue stringValue) {
            return new ConstantExpression(stringValue.getValue());
        }
        if (expression instanceof NullValue) {
            return new ConstantExpression(null);
        }
        if (expression instanceof BooleanValue booleanValue) {
            return new ConstantExpression(booleanValue.getValue());
        }
        if (expression instanceof HexValue hexValue) {
            return new ConstantExpression(hexValue.getValue());
        }
        if (expression instanceof DateValue dateValue) {
            return new ConstantExpression(dateValue.getValue());
        }
        if (expression instanceof TimeValue timeValue) {
            return new ConstantExpression(timeValue.getValue());
        }
        if (expression instanceof TimestampValue timestampValue) {
            return new ConstantExpression(timestampValue.getValue());
        }
        throw  new IllegalArgumentException("Unsupported constant expression: " + expression);
    }

    /**
     * Parses an arithmetic SQL expression into an {@link ArithmeticExpression}.
     *
     * <p>This method supports binary operations including addition, subtraction,
     * multiplication, division, and modulo.</p>
     *
     * @param expression the arithmetic expression to parse
     * @param context the query metadata context
     * @return the parsed {@link ArithmeticExpression}
     * @throws IllegalArgumentException if the operator is not supported
     */
    @Override
    public ArithmeticExpression parseArithmetic(Expression expression, QueryMetadata context) {
        ArithmeticOperator operator =
            (expression instanceof Addition) ? ArithmeticOperator.ADD :
            (expression instanceof Subtraction) ? ArithmeticOperator.SUBTRACT :
            (expression instanceof Multiplication) ? ArithmeticOperator.MULTIPLY :
            (expression instanceof Division) ? ArithmeticOperator.DIVIDE :
            (expression instanceof Modulo) ? ArithmeticOperator.MODULO : null;
        if (operator == null) throw new IllegalArgumentException("Unsupported arithmetic expression: " + expression);
        BinaryExpression binaryExpression = (BinaryExpression) expression;
        return new ArithmeticExpression(
                parseExpression(binaryExpression.getLeftExpression(), context),
                operator,
                parseExpression(binaryExpression.getRightExpression(), context));
    }

    /**
     * Parses a column reference into a {@link ColumnReferenceExpression}.
     *
     * <p>The method resolves the table alias using the provided {@link QueryMetadata}
     * and builds a reference to the appropriate query source.</p>
     *
     * @param column the column expression from JSQLParser
     * @param context the query metadata context
     * @return the corresponding {@link ColumnReferenceExpression}
     */
    @Override
    public ColumnReferenceExpression parseExpression(Column column, QueryMetadata context) {
        String columnName = column.getColumnName();
        String tableAlias = column.getTableName();
        QuerySource source = context.getSourceMap().get(tableAlias);
        return new ColumnReferenceExpression(source, columnName);
    }

    /**
     * Parses a {@link CaseExpression} into a {@link CaseWhenExpression}.
     *
     * <p>Each {@link WhenClause} is parsed into a {@code WhenThen} pair.
     * The else expression (if present) is also parsed.</p>
     *
     * @param caseExpression the case expression from JSQLParser
     * @param context the query metadata context
     * @return the corresponding {@link CaseWhenExpression}
     */
    @Override
    public CaseWhenExpression parseExpression(CaseExpression caseExpression, QueryMetadata context) {
        List<CaseWhenExpression.WhenThen> whenThen = new ArrayList<>();
        for (WhenClause whenClause : caseExpression.getWhenClauses()) {
            Predicate whenExpr = parsePredicate(whenClause.getWhenExpression(), context);
            Selection thenExpr = parseExpression(whenClause.getThenExpression(), context);
            whenThen.add(new CaseWhenExpression.WhenThen(whenExpr, thenExpr));
        }
        Selection elseExpr = caseExpression.getElseExpression() != null
                ? parseExpression(caseExpression.getElseExpression(), context)
                : null;
        return new CaseWhenExpression(whenThen, elseExpr);
    }

    /**
     * Parses a SQL function call into a {@link FunctionExpression}.
     *
     * <p>The function name and its arguments are extracted and wrapped into
     * the corresponding expression representation.</p>
     *
     * @param function the SQL function from JSQLParser
     * @param context the query metadata context
     * @return a {@link FunctionExpression} representing the function
     * @throws IllegalArgumentException if the function is {@code null}
     */
    @Override
    public FunctionExpression parseExpression(Function function, QueryMetadata context) {
        if (function == null) {
            throw new IllegalArgumentException("Function must not be null");
        }
        String functionName = function.getName();
        List<Selection> arguments = new ArrayList<>();
        if (function.getParameters() != null) {
            for (Expression expr : function.getParameters()) {
                arguments.add(parseExpression(expr, context));
            }
        }
        return FunctionExpression.builder().name(functionName).parameters(arguments).build();
    }

    /**
     * Parses an analytic (window) expression into a {@link WindowFunctionExpression}.
     *
     * <p>This implementation currently returns {@code null} and is intended to be
     * implemented in the future.</p>
     *
     * @param analyticExpression the window expression to parse
     * @param context the query metadata context
     * @return {@code null} as window function parsing is not yet supported
     */
    @Override
    public WindowFunctionExpression parseExpression(AnalyticExpression analyticExpression, QueryMetadata context) {
        return null;
    }

    /**
     * Parses a subquery expression into a {@link SubqueryExpression}.
     *
     * <p>This implementation currently returns {@code null} and is intended to be
     * implemented in the future.</p>
     *
     * @param subquery the parenthesized subquery
     * @param context the query metadata context
     * @return {@code null} as subquery parsing is not yet supported
     */

    @Override
    public SubqueryExpression parseExpression(ParenthesedSelect subquery, QueryMetadata context) {
        return null;
    }

    /**
     * Parses a comparison expression into a {@link Predicate}.
     *
     * <p>Supported comparison types include:</p>
     * <ul>
     *     <li>{@link EqualsTo}, {@link NotEqualsTo}</li>
     *     <li>{@link GreaterThan}, {@link GreaterThanEquals}</li>
     *     <li>{@link MinorThan}, {@link MinorThanEquals}</li>
     *     <li>{@link IsNullExpression}, {@link LikeExpression}</li>
     *     <li>{@link InExpression}, {@link Between}</li>
     * </ul>
     *
     * @param expression the comparison expression from JSQLParser
     * @param context the query metadata context
     * @return the parsed {@link Predicate}
     * @throws UnsupportedOperationException if the expression type is not supported
     */
    @Override
    public Predicate parseComparison(Expression expression, QueryMetadata context) {
        if (expression instanceof EqualsTo equals) return parse(equals, context);
        if (expression instanceof NotEqualsTo notEqualsTo) return parse(notEqualsTo, context);
        if (expression instanceof GreaterThan greaterThan) return parse(greaterThan, context);
        if (expression instanceof GreaterThanEquals greaterThanEquals) return parse(greaterThanEquals, context);
        if (expression instanceof MinorThan minorThan) return parse(minorThan, context);
        if (expression instanceof MinorThanEquals minorThanEquals) return parse(minorThanEquals, context);
        if (expression instanceof IsNullExpression isNullExpression) return parse(isNullExpression, context);
        if (expression instanceof LikeExpression likeExpression) return parse(likeExpression, context);
        if (expression instanceof InExpression inExpression) return parse(inExpression, context);
        if (expression instanceof Between between) return parse(between, context);
        throw new UnsupportedOperationException("Unsupported comparison expression: " + expression);
    }

    /**
     * Parses a logical binary expression into a {@link LogicalPredicate}.
     *
     * <p>Supports logical {@code AND} and {@code OR} operators.</p>
     *
     * @param expression the logical binary expression
     * @param context the query metadata context
     * @return the parsed {@link LogicalPredicate}
     * @throws UnsupportedOperationException if the expression type is not supported
     */
    @Override
    public Predicate parseLogical(BinaryExpression expression, QueryMetadata context) {
        if (expression instanceof AndExpression and) {
            return new LogicalPredicate(List.of(
                    parsePredicate(and.getLeftExpression(), context),
                    parsePredicate(and.getRightExpression(), context)), LogicalOperator.AND);
        }
        if (expression instanceof OrExpression or) {
            return new LogicalPredicate(List.of(
                    parsePredicate(or.getLeftExpression(), context),
                    parsePredicate(or.getRightExpression(), context)), LogicalOperator.OR);
        }
        throw new UnsupportedOperationException("Unsupported logical expression: " + expression);
    }

    /**
     * Parses an {@link EqualsTo} expression into a {@link ComparisonPredicate}
     * using the {@link Operator#EQUAL} operator.
     *
     * @param equalsTo the equals expression
     * @param context the query metadata context
     * @return the corresponding {@link ComparisonPredicate}
     */
    public Predicate parse(EqualsTo equalsTo, QueryMetadata context) {
        return new ComparisonPredicate(
                parseExpression(equalsTo.getLeftExpression(), context),
                Operator.EQUAL,
                parseExpression(equalsTo.getRightExpression(), context));
    }

    /**
     * Parses a {@link NotEqualsTo} expression by negating an {@link Operator#EQUAL} predicate.
     *
     * @param notEqualsTo the not-equals expression
     * @param context the query metadata context
     * @return the negated {@link ComparisonPredicate}
     */
    public Predicate parse(NotEqualsTo notEqualsTo, QueryMetadata context) {
        return new ComparisonPredicate(
                parseExpression(notEqualsTo.getLeftExpression(), context),
                Operator.NOT_EQUAL,
                parseExpression(notEqualsTo.getRightExpression(), context)).not();
    }

    /**
     * Parses a {@link GreaterThan} expression into a {@link ComparisonPredicate}
     * using the {@link Operator#GREATER_THAN} operator.
     *
     * @param greaterThan the greater-than expression
     * @param context the query metadata context
     * @return the corresponding {@link ComparisonPredicate}
     */
    public Predicate parse(GreaterThan greaterThan, QueryMetadata context) {
        return new ComparisonPredicate(
                parseExpression(greaterThan.getLeftExpression(), context),
                Operator.GREATER_THAN,
                parseExpression(greaterThan.getRightExpression(), context));
    }

    /**
     * Parses a {@link GreaterThanEquals} expression into a {@link ComparisonPredicate}
     * using the {@link Operator#GREATER_THAN_EQUAL} operator.
     *
     * @param greaterThanEquals the greater-than-or-equal expression
     * @param context the query metadata context
     * @return the corresponding {@link ComparisonPredicate}
     */
    public Predicate parse(GreaterThanEquals greaterThanEquals, QueryMetadata context) {
        return new ComparisonPredicate(
                parseExpression(greaterThanEquals.getLeftExpression(), context),
                Operator.GREATER_THAN_EQUAL,
                parseExpression(greaterThanEquals.getRightExpression(), context));
    }

    /**
     * Parses a {@link MinorThan} expression into a {@link ComparisonPredicate}
     * using the {@link Operator#LESS_THAN} operator.
     *
     * @param lessThan the less-than expression
     * @param context the query metadata context
     * @return the corresponding {@link ComparisonPredicate}
     */
    public Predicate parse(MinorThan lessThan, QueryMetadata context) {
        return new ComparisonPredicate(
                parseExpression(lessThan.getLeftExpression(), context),
                Operator.LESS_THAN,
                parseExpression(lessThan.getRightExpression(), context));
    }

    /**
     * Parses a {@link MinorThanEquals} expression into a {@link ComparisonPredicate}
     * using the {@link Operator#LESS_THAN_EQUAL} operator.
     *
     * @param lessThanEquals the less-than-or-equal expression
     * @param context the query metadata context
     * @return the corresponding {@link ComparisonPredicate}
     */
    public Predicate parse(MinorThanEquals lessThanEquals, QueryMetadata context) {
        return new ComparisonPredicate(
                parseExpression(lessThanEquals.getLeftExpression(), context),
                Operator.LESS_THAN_EQUAL,
                parseExpression(lessThanEquals.getRightExpression(), context));

    }

    /**
     * Parses an {@link IsNullExpression} into a {@link ComparisonPredicate}
     * using the {@link Operator#IS_NULL} operator.
     *
     * @param isNullExpression the is-null expression
     * @param context the query metadata context
     * @return the corresponding {@link ComparisonPredicate}
     */
    public Predicate parse(IsNullExpression isNullExpression, QueryMetadata context) {
        return new ComparisonPredicate(
                parseExpression(isNullExpression.getLeftExpression(), context),
                Operator.IS_NULL,
                null);
    }

    /**
     * Parses a {@link LikeExpression} into a {@link ComparisonPredicate}
     * using the {@link Operator#LIKE} operator.
     *
     * @param likeExpression the like expression
     * @param context the query metadata context
     * @return the corresponding {@link ComparisonPredicate}
     */
    public Predicate parse(LikeExpression likeExpression, QueryMetadata context) {
        return new ComparisonPredicate(
                parseExpression(likeExpression.getLeftExpression(), context),
                Operator.LIKE,
                parseExpression(likeExpression.getRightExpression(), context)
        );
    }

    /**
     * Parses an {@link InExpression} into a {@link ComparisonPredicate}
     * using the {@link Operator#IN} operator.
     *
     * @param inExpression the IN expression
     * @param context the query metadata context
     * @return the corresponding {@link ComparisonPredicate}
     */
    public Predicate parse(InExpression inExpression, QueryMetadata context) {
        return new ComparisonPredicate(
                parseExpression(inExpression.getLeftExpression(), context),
                Operator.IN,
                parseExpression(inExpression.getRightExpression(), context)
        );
    }

    /**
     * Parses a {@link Between} expression into a {@link Predicate} using
     * a helper method {@code Predicates.between()}.
     *
     * @param between the BETWEEN expression
     * @param context the query metadata context
     * @return the resulting {@link Predicate} representing a BETWEEN condition
     */
    public Predicate parse(Between between, QueryMetadata context) {
        Selection left = parseExpression(between.getLeftExpression(), context);
        Selection start = parseExpression(between.getBetweenExpressionStart(), context);
        Selection end = parseExpression(between.getBetweenExpressionEnd(), context);
        return Predicates.between(left, start, end);
    }

}
