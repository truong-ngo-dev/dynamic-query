package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.DefaultQueryMetadata;
import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;
import vn.truongngo.lib.dynamicquery.core.builder.v2.Visitor;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderSpecifier;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.Restriction;

import java.util.function.Consumer;

/**
 * Represents a subquery used as an expression in a larger query context.
 * <p>
 * A subquery can be used within SELECT, WHERE, HAVING, or FROM clauses
 * and is backed by its own {@link QueryMetadata}.
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
public class SubqueryExpression extends AbstractAlias<SubqueryExpression> implements Selection, QuerySource {

    private final QueryMetadata queryMetadata;

    /**
     * Constructs a new {@link SubqueryExpression} with the given query metadata.
     * <p>
     * The metadata represents the underlying query that will be used as the subquery.
     * </p>
     *
     * @param metadata the query metadata for the subquery
     * @throws IllegalArgumentException if the provided metadata is {@code null}
     */
    public SubqueryExpression(QueryMetadata metadata) {
        this.queryMetadata = metadata;
    }

    /**
     * Accepts a visitor to allow processing or transformation of the subquery expression.
     * <p>
     * This method is part of the visitor pattern. The visitor will perform actions based on the type of the expression.
     * </p>
     *
     * @param visitor the visitor that will process the subquery expression
     * @param context additional context to be passed to the visitor
     * @param <R> the return type of the visitor's {@link Visitor#visit(SubqueryExpression, Object)} method
     * @param <C> the type of the context passed to the visitor
     * @return the result of the visitor's processing
     */
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    /**
     * Creates a new builder instance for constructing a {@link SubqueryExpression}.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for constructing a {@link SubqueryExpression} using a fluent API.
     */
    public static class Builder {

        private final QueryMetadata metadata = new DefaultQueryMetadata();

        private String alias;

        /**
         * Sets the alias of the subquery.
         *
         * @param alias the alias to assign
         * @return this builder instance
         */
        public Builder as(String alias) {
            this.alias = alias;
            return this;
        }

        /**
         * Defines the root entity of the subquery.
         *
         * @param entityClass the root entity class
         * @return this builder instance
         */
        public Builder from(Class<?> entityClass) {
            return from(entityClass, null);
        }

        /**
         * Defines the root entity with an alias.
         *
         * @param entityClass the root entity class
         * @param alias the alias for the root entity
         * @return this builder instance
         */
        public Builder from(Class<?> entityClass, String alias) {
            metadata.setFrom(entityClass, alias);
            return this;
        }

        /**
         * Specifies selected columns or expressions.
         *
         * @param expressions expressions to select
         * @return this builder instance
         */
        public SubqueryExpression.Builder select(Expression... expressions) {
            for (Expression expression : expressions) {
                metadata.addSelect(expression);
            }
            return this;
        }

        /**
         * Adds where conditions to the subquery.
         *
         * @param predicates filter conditions
         * @return this builder instance
         */
        public SubqueryExpression.Builder where(Predicate... predicates) {
            for (Predicate predicate : predicates) {
                metadata.addWhere(predicate);
            }
            return this;
        }

        /**
         * Adds a join clause using a consumer builder.
         *
         * @param joinBuilder a consumer that configures the join
         * @return this builder instance
         */
        public SubqueryExpression.Builder join(Consumer<JoinExpression.Builder> joinBuilder) {
            JoinExpression.Builder builder = JoinExpression.builder();
            joinBuilder.accept(builder);
            metadata.addJoin(builder.build());
            return this;
        }

        /**
         * Groups results by the specified expressions.
         *
         * @param expressions expressions to group by
         * @return this builder instance
         */
        public SubqueryExpression.Builder groupBy(Expression... expressions) {
            for (Expression expression : expressions) {
                metadata.addGroupBy(expression);
            }
            return this;
        }

        /**
         * Adds conditions on grouped results (HAVING clause).
         *
         * @param predicates filter conditions on groups
         * @return this builder instance
         */
        public SubqueryExpression.Builder having(Predicate... predicates) {
            for (Predicate predicate : predicates) {
                metadata.addHaving(predicate);
            }
            return this;
        }

        /**
         * Specifies the ordering of subquery results.
         *
         * @param orderSpecifiers one or more order specifiers
         * @return this builder instance
         */
        public SubqueryExpression.Builder orderBy(OrderSpecifier... orderSpecifiers) {
            for (OrderSpecifier orderSpecifier : orderSpecifiers) {
                metadata.addOrderBy(orderSpecifier);
            }
            return this;
        }

        /**
         * Applies a restriction to the subquery (e.g. pagination).
         *
         * @param restriction the restriction
         * @return this builder instance
         */
        public SubqueryExpression.Builder restriction(Restriction restriction) {
            metadata.setRestriction(restriction);
            return this;
        }

        /**
         * Builds and returns the configured {@link SubqueryExpression}.
         *
         * @return the subquery expression instance
         */
        public SubqueryExpression build() {
            SubqueryExpression sq = new SubqueryExpression(metadata);
            if (alias != null) sq = sq.as(alias);
            return sq;
        }
    }
}
