package vn.truongngo.lib.dynamicquery.core.builder;

import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderSpecifier;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.Restriction;
import vn.truongngo.lib.dynamicquery.core.support.Expressions;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the {@link QueryMetadata} interface.
 * <p>
 * This class stores the components of a dynamic query such as:
 * <ul>
 *     <li>{@code from} - the main entity or subquery the query is executed against</li>
 *     <li>{@code select} - the projection of the query</li>
 *     <li>{@code join} - join clauses with other entities</li>
 *     <li>{@code where} - filter conditions</li>
 *     <li>{@code groupBy} - grouping fields</li>
 *     <li>{@code having} - filtering on grouped records</li>
 *     <li>{@code orderBy} - sorting specifications</li>
 *     <li>{@code restriction} - paging/modifier like limit, offset</li>
 * </ul>
 * <p>
 * This metadata will be consumed by a specific query engine (e.g., QueryDSL, jOOQ)
 * to build an executable query or a SQL string.
 *
 * @author Truong Ngo
 * @version 1.0
 */
public class DefaultQueryMetadata implements QueryMetadata {

    private Expression from;
    private boolean distinct = false;
    private List<Expression> select = new ArrayList<>();
    private List<JoinExpression> join = new ArrayList<>();
    private List<Predicate> where = new ArrayList<>();
    private List<Expression> groupBy = new ArrayList<>();
    private List<Predicate> having = new ArrayList<>();
    private List<OrderSpecifier> orderBy = new ArrayList<>();
    private Restriction restriction = Restriction.unPaged();
    private boolean isUnique = false;

    public DefaultQueryMetadata() {

    }

    public DefaultQueryMetadata(Expression expression, String alias) {
        this.from = expression.as(alias);
    }

    public DefaultQueryMetadata(Class<?> entityClass, String alias) {
        this.from = Expressions.entity(entityClass).as(alias);
    }

    public DefaultQueryMetadata(Class<?> entityClass) {
        this.from = Expressions.entity(entityClass);
    }

    @Override
    public Expression getFrom() {
        return from;
    }

    @Override
    public String getAlias() {
        return from.getAlias();
    }

    @Override
    public Class<?> getEntityClass() {
        if (from instanceof EntityReferenceExpression) return ((EntityReferenceExpression) from).getEntityClass();
        throw new IllegalStateException("Query is not from entity class");
    }

    @Override
    public void setFrom(Class<?> entityClass) {
        this.from = Expressions.entity(entityClass);
    }

    @Override
    public void setFrom(Class<?> entityClass, String alias) {
        this.from = Expressions.entity(entityClass).as(alias);
    }

    @Override
    public void setFrom(Expression fromExpression, String alias) {
        this.from = fromExpression.as(alias);
    }

    @Override
    public void addSelect(Expression selectClauses) {
        select.add(selectClauses);
    }

    @Override
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public void addJoin(JoinExpression joinClauses) {
        join.add(joinClauses);
    }

    @Override
    public void addWhere(Predicate whereClauses) {
        where.add(whereClauses);
    }

    @Override
    public void addGroupBy(Expression groupByClauses) {
        groupBy.add(groupByClauses);
    }

    @Override
    public void addHaving(Predicate havingClauses) {
        having.add(havingClauses);
    }

    @Override
    public void addOrderBy(OrderSpecifier orderByClauses) {
        orderBy.add(orderByClauses);
    }

    @Override
    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    @Override
    public void setUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

    @Override
    public void reset() {
        select = new ArrayList<>();
        join = new ArrayList<>();
        where = new ArrayList<>();
        groupBy = new ArrayList<>();
        having = new ArrayList<>();
        orderBy = new ArrayList<>();
        restriction = Restriction.unPaged();
    }

    @Override
    public void resetWhereClauses() {
        where = new ArrayList<>();
    }

    @Override
    public void resetGroupByClauses() {
        groupBy = new ArrayList<>();
    }

    @Override
    public void resetHavingClauses() {
        having = new ArrayList<>();
    }

    @Override
    public void resetOrderByClauses() {
        orderBy = new ArrayList<>();
    }

    @Override
    public boolean isDistinct() {
        return distinct;
    }

    @Override
    public List<Expression> getSelectClauses() {
        return select;
    }

    @Override
    public List<JoinExpression> getJoinClauses() {
        return join;
    }

    @Override
    public List<Predicate> getWhereClauses() {
        return where;
    }

    @Override
    public List<Expression> getGroupByClauses() {
        return groupBy;
    }

    @Override
    public List<Predicate> getHavingClauses() {
        return having;
    }

    @Override
    public List<OrderSpecifier> getOrderByClauses() {
        return orderBy;
    }

    @Override
    public Restriction getRestriction() {
        return restriction;
    }

    @Override
    public boolean isUnique() {
        return isUnique;
    }
}
