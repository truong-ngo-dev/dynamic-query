package vn.truongngo.lib.dynamicquery.jooq.converter;

import org.jooq.*;
import org.jooq.impl.DSL;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;

import java.util.Map;

public class JooqVisitor implements Visitor<QueryPart, Map<String, Table<?>>> {

    @Override
    public QueryPart visit(ConstantExpression expression, Map<String, Table<?>> context) {
        return DSL.val(expression.getValue());
    }

    @Override
    public QueryPart visit(EntityReferenceExpression expression, Map<String, Table<?>> context) {
        String key = expression.getAlias() != null ? expression.getAlias() : expression.getEntityClass().getSimpleName();
        return context.get(key);
    }

    @Override
    public QueryPart visit(ColumnReferenceExpression expression, Map<String, Table<?>> context) {
        EntityReferenceExpression entityRef = (EntityReferenceExpression) expression.getSource();
        String entityKey = entityRef.getAlias() != null ? entityRef.getAlias() : entityRef.getEntityClass().getSimpleName();
        Table<?> table = context.get(entityKey);
        Field<?> column = table.field(expression.getColumnName());
        if (column == null) {
            throw new IllegalArgumentException("Column not found: " + expression.getColumnName());
        }
        return (expression.getAlias() != null) ? column.as(expression.getAlias()) : column;
    }

    @Override
    public QueryPart visit(FunctionExpression expression, Map<String, Table<?>> context) {
        Field<?>[] params = expression.getParameters()
                .stream()
                .map(param -> (Field<?>) param.accept(this, context))
                .toArray(Field[]::new);
        return DSL.function(expression.getFunctionName(), Object.class, params);
    }

    @Override
    @SuppressWarnings("all")
    public QueryPart visit(CaseWhenExpression expression, Map<String, Table<?>> context) {

        CaseConditionStep<?> caseExpr = null;

        for (CaseWhenExpression.WhenThen whenThen : expression.getConditions()) {
            Condition when = (Condition) whenThen.when().accept(this, context);
            Expression then = whenThen.then();
            caseExpr = DSL.when(when, then);
        }

        if (expression.getElseExpression() != null) {
            Field elseExpr = (Field) expression.getElseExpression().accept(this, context);
            assert caseExpr != null;
            Field<?> field = caseExpr.otherwise(elseExpr);
        }

        if (expression.getAlias() != null) {
            caseExpr = (CaseConditionStep<?>) caseExpr.as(expression.getAlias());
        }

        return caseExpr;
    }

    @Override
    public QueryPart visit(SubqueryExpression expression, Map<String, Table<?>> context) {
        return null;
    }

    @Override
    public QueryPart visit(ComparisonPredicate expression, Map<String, Table<?>> context) {
        return null;
    }

    @Override
    public QueryPart visit(LogicalPredicate expression, Map<String, Table<?>> context) {
        return null;
    }

}
