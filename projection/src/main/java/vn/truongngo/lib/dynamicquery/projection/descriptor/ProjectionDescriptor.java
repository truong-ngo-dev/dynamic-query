package vn.truongngo.lib.dynamicquery.projection.descriptor;

import lombok.*;

import java.util.LinkedList;
import java.util.List;

/**
 * A descriptor class representing a projection model used in dynamic query building.
 * <p>
 * This descriptor includes metadata about the projection target class,
 * the base entity it maps to, and query-related components such as selected columns,
 * joins, group-by fields, and order-by fields.
 * </p>
 *
 * <p><b>Fields:</b></p>
 * <ul>
 *     <li>{@code target} – The projection class type.</li>
 *     <li>{@code entity} – The root entity class being queried.</li>
 *     <li>{@code alias} – Alias name for the root entity (used in SQL).</li>
 *     <li>{@code distinct} – Whether to apply the {@code DISTINCT} clause in the SQL query.</li>
 *     <li>{@code selects} – List of select expressions.</li>
 *     <li>{@code joins} – List of join definitions.</li>
 *     <li>{@code orderBys} – List of order by expressions.</li>
 *     <li>{@code groupBys} – List of group by expressions.</li>
 * </ul>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectionDescriptor {

    /**
     * The projection class (DTO or custom result class).
     */
    private Class<?> target;

    /**
     * The root entity class from which the projection is built.
     */
    private Class<?> entity;

    /**
     * Alias used for the root entity in the SQL query.
     */
    private String alias;

    /**
     * Indicates whether the projection should use DISTINCT.
     */
    private boolean distinct;

    /**
     * List of selected expressions (columns, aggregates, subqueries, etc.).
     */
    private List<SelectDescriptor> selects;

    /**
     * List of join descriptors used to join other entities.
     */
    private List<JoinDescriptor> joins;

    /**
     * List of expressions used in the GROUP BY clause.
     */
    private List<SelectDescriptor> groupBys;

    /**
     * List of order-by descriptors used to sort the result.
     */
    private List<OrderByDescriptor> orderBys;

    /**
     * Adds a join descriptor to the list of joins.
     *
     * @param joinDescriptor the {@link JoinDescriptor} to add
     */
    public void addJoin(JoinDescriptor joinDescriptor) {
        if (joins == null) joins = new LinkedList<>();
        joins.add(joinDescriptor);
    }

    /**
     * Adds a select descriptor to the list of selections.
     *
     * @param columnDescriptor the {@link SelectDescriptor} to add
     */
    public void addSelect(SelectDescriptor columnDescriptor) {
        if (selects == null) selects = new LinkedList<>();
        selects.add(columnDescriptor);
    }

    /**
     * Adds a group-by expression to the list of group-by descriptors.
     *
     * @param selectDescriptor the {@link SelectDescriptor} to group by
     */
    public void addGroupBy(SelectDescriptor selectDescriptor) {
        if (groupBys == null) groupBys = new LinkedList<>();
        groupBys.add(selectDescriptor);
    }

    /**
     * Adds an order-by descriptor to the list of ordering expressions.
     *
     * @param orderByDescriptor the {@link OrderByDescriptor} to add
     */
    public void addOrderBy(OrderByDescriptor orderByDescriptor) {
        if (orderBys == null) orderBys = new LinkedList<>();
        orderBys.add(orderByDescriptor);
    }

    /**
     * Gets the target entity class that matches the given join alias.
     *
     * @param sourceAlias the alias of the joined entity
     * @return the target entity class, or {@code null} if not found
     */
    public Class<?> getJoinTargetByAlias(String sourceAlias) {
        return joins
                .stream()
                .filter(j -> j.getTargetAlias().equals(sourceAlias))
                .map(JoinDescriptor::getTargetEntity)
                .findFirst()
                .orElse(null);
    }

}
