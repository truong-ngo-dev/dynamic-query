package vn.truongngo.lib.dynamicquery.core.expression;

import vn.truongngo.lib.dynamicquery.core.enumerate.JoinType;

/**
 * Represents a SQL JOIN expression, including the join type, target expression, condition, and alias.
 * <p>
 * This class is used to model SQL JOIN clauses, such as INNER JOIN, LEFT JOIN, etc., along with
 * the corresponding condition and an optional alias for the joined table.
 * </p>
 *
 * <blockquote><pre>
 *     // Example usage:
 *     new JoinExpression.Builder()
 *         .join(new ColumnReferenceExpression(User.class, "address"), JoinType.LEFT_JOIN)
 *         .on(new Predicate(...))
 *         .as("u")
 *         .build();
 * </pre></blockquote>
 *
 * @author Truong Ngo
 * @version 1.0
 */
public record JoinExpression(JoinType joinType, QuerySource target, Predicate condition) {

    /**
     * Creates a new builder for constructing a JoinExpression.
     *
     * @return a new instance of the Builder class
     */
    public static Builder builder() {
        return new Builder();
    }


    /**
     * Builder class for constructing a JoinExpression with flexible parameters.
     * <p>
     * The builder allows specifying the join type, target expression, join condition, and alias.
     * </p>
     */
    public static class Builder {
        private JoinType joinType;
        private QuerySource target;
        private Predicate condition;


        /**
         * Specifies the target expression for the join and the join type.
         *
         * @param target   the target expression to join with
         * @param joinType the type of join (e.g., INNER_JOIN, LEFT_JOIN)
         * @return the current Builder instance
         */
        public Builder join(QuerySource target, JoinType joinType) {
            this.target = target;
            this.joinType = joinType;
            return this;
        }


        /**
         * Specifies the target expression for the join with the default INNER_JOIN type.
         *
         * @param target the target expression to join with
         * @return the current Builder instance
         */
        public Builder join(QuerySource target) {
            this.target = target;
            this.joinType = JoinType.INNER_JOIN;
            return this;
        }


        /**
         * Specifies the ON condition for the JOIN expression.
         *
         * @param condition the join condition (predicate)
         * @return the current Builder instance
         */
        public Builder on(Predicate condition) {
            this.condition = condition;
            return this;
        }


        /**
         * Builds the final JoinExpression instance.
         *
         * @return a new JoinExpression with the specified parameters
         */
        public JoinExpression build() {
            return new JoinExpression(joinType, target, condition);
        }
    }
}
