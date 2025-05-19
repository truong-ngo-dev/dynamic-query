package vn.truongngo.lib.dynamicquery.projection.descriptor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.truongngo.lib.dynamicquery.core.enumerate.Order;

/**
 * Descriptor representing an ORDER BY clause element in a query projection.
 * <p>
 * This class is used to capture metadata about how a particular column or expression
 * should be sorted in the query result, including the sort direction.
 * </p>
 *
 * <h2>Usage example:</h2>
 * <pre>{@code
 * @OrderBy(reference = "createdDate", order = Order.DESC)
 * public class UserProjection {
 *     @Column(name = "created_at", alias = "createdDate")
 *     private LocalDateTime createdAt;
 * }
 * }</pre>
 *
 * <p>
 * The {@code selection} field references a {@link SelectDescriptor} that describes
 * the column or expression being ordered. This may originate from annotations like
 * {@code @Column}, {@code @Aggregate}, or {@code @Expression}.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 *
 */
@Getter
@Setter
@Builder
public class OrderByDescriptor {

    /**
     * The selection (column or expression) being ordered in the result set.
     * This typically corresponds to a field annotated in the projection class.
     */
    private SelectDescriptor selection;

    /**
     * The direction of the ordering: ascending or descending.
     */
    private Order order;
}
