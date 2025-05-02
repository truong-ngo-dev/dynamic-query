package vn.truongngo.lib.dynamicquery.core.builder;

import vn.truongngo.lib.dynamicquery.core.expression.*;
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
 * @version 2.0.0
 */
public class DefaultQueryMetadata implements QueryMetadata {

    private QuerySource from;
    private boolean distinct = false;
    private List<Selection> select = new ArrayList<>();
    private List<JoinExpression> join = new ArrayList<>();
    private List<Predicate> where = new ArrayList<>();
    private List<Selection> groupBy = new ArrayList<>();
    private List<Predicate> having = new ArrayList<>();
    private List<OrderSpecifier> orderBy = new ArrayList<>();
    private Restriction restriction = Restriction.unPaged();

    public DefaultQueryMetadata() {
    }

    public DefaultQueryMetadata(QuerySource expression, String alias) {
        this.from = expression.as(alias);
    }

    public DefaultQueryMetadata(Class<?> entityClass, String alias) {
        this.from = Expressions.entity(entityClass).as(alias);
    }

    public DefaultQueryMetadata(Class<?> entityClass) {
        this.from = Expressions.entity(entityClass);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public QuerySource getFrom() {
        return from;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public String getAlias() {
        return from.getAlias();
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public Class<?> getEntityClass() {
        if (from instanceof EntityReferenceExpression) return ((EntityReferenceExpression) from).getEntityClass();
        throw new IllegalStateException("Query is not from entity class");
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void setFrom(Class<?> entityClass) {
        this.from = Expressions.entity(entityClass);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void setFrom(Class<?> entityClass, String alias) {
        this.from = Expressions.entity(entityClass).as(alias);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void setFrom(QuerySource fromExpression, String alias) {
        this.from = fromExpression.as(alias);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void addSelect(Selection selectClauses) {
        select.add(selectClauses);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void addJoin(JoinExpression joinClauses) {
        join.add(joinClauses);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void addWhere(Predicate whereClauses) {
        where.add(whereClauses);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void addGroupBy(Selection groupByClauses) {
        groupBy.add(groupByClauses);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void addHaving(Predicate havingClauses) {
        having.add(havingClauses);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void addOrderBy(OrderSpecifier orderByClauses) {
        orderBy.add(orderByClauses);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    /**
     * {@inheritDoc}
     * */
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

    /**
     * {@inheritDoc}
     * */
    @Override
    public void resetWhereClauses() {
        where = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void resetGroupByClauses() {
        groupBy = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void resetHavingClauses() {
        having = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void resetOrderByClauses() {
        orderBy = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public List<Selection> getSelectClauses() {
        return select;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public List<JoinExpression> getJoinClauses() {
        return join;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public List<Predicate> getWhereClauses() {
        return where;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public List<Selection> getGroupByClauses() {
        return groupBy;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public List<Predicate> getHavingClauses() {
        return having;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public List<OrderSpecifier> getOrderByClauses() {
        return orderBy;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public Restriction getRestriction() {
        return restriction;
    }
}
