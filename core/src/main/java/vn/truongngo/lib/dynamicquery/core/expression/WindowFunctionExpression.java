package vn.truongngo.lib.dynamicquery.core.expression;

import lombok.Getter;
import vn.truongngo.lib.dynamicquery.core.builder.Visitor;
import vn.truongngo.lib.dynamicquery.core.expression.modifier.OrderSpecifier;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WindowFunctionExpression extends AbstractAlias<WindowFunctionExpression> implements Selection {

    private final String functionName;
    private final List<Selection> arguments;
    private final List<Selection> partitionBy;
    private final List<OrderSpecifier> orderBy;

    public WindowFunctionExpression(String functionName, List<Selection> arguments, List<Selection> partitionBy, List<OrderSpecifier> orderBy) {
        this.functionName = functionName;
        this.arguments = arguments;
        this.partitionBy = partitionBy;
        this.orderBy = orderBy;
    }

    @Override
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String functionName;
        private final List<Selection> arguments = new ArrayList<>();
        private final List<Selection> partitionBy = new ArrayList<>();
        private final List<OrderSpecifier> orderBy = new ArrayList<>();
        private String as;

        public Builder name(String functionName) {
            this.functionName = functionName;
            return this;
        }

        public Builder argument(Selection argument) {
            this.arguments.add(argument);
            return this;
        }

        public Builder arguments(List<Selection> arguments) {
            this.arguments.addAll(arguments);
            return this;
        }

        public Builder partitionBy(Selection expression) {
            this.partitionBy.add(expression);
            return this;
        }

        public Builder partitionBy(List<Selection> expressions) {
            this.partitionBy.addAll(expressions);
            return this;
        }

        public Builder orderBy(OrderSpecifier... orderSpecifier) {
            this.orderBy.addAll(List.of(orderSpecifier));
            return this;
        }

        public Builder orderBy(List<OrderSpecifier> orderSpecifiers) {
            this.orderBy.addAll(orderSpecifiers);
            return this;
        }

        public Builder as(String as) {
            this.as = as;
            return this;
        }

        public WindowFunctionExpression build() {
            WindowFunctionExpression wdf = new WindowFunctionExpression(functionName, arguments, partitionBy, orderBy);
            if (as != null) wdf = wdf.as(as);
            return wdf;
        }
    }
}
