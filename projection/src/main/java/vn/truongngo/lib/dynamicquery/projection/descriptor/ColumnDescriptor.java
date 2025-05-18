package vn.truongngo.lib.dynamicquery.projection.descriptor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.truongngo.lib.dynamicquery.projection.annotation.Column;

/**
 * Descriptor representing a single column selection in the query's SELECT clause.
 * <p>
 * This class holds metadata about a specific column to be projected, including its name,
 * alias (used for reference in other clauses like GROUP BY or ORDER BY), and the source alias
 * it originates from (typically corresponding to an alias defined in {@code @Projection} or {@code @Join}).
 * </p>
 *
 * <p>
 * If {@code from} is empty or not specified, the column is assumed to come from the main source
 * defined by the {@code @Projection}.
 * </p>
 *
 * @see SelectDescriptor
 * @see Column
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
@Builder
public class ColumnDescriptor implements SelectDescriptor {

   /**
    * The name of the column as it appears in the source (table or view).
    */
    private String name;

    /**
     * The alias used to reference this column in other parts of the query (e.g., ORDER BY, GROUP BY).
     */
    private String alias;

    /**
     * The alias of the source (FROM or JOIN) from which this column originates.
     * If not specified, it defaults to the main source defined in {@code @Projection}.
     */
    private String from;

}
