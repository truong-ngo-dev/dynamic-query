package vn.truongngo.lib.dynamicquery.projection.descriptor;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
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
@SuperBuilder
public class ColumnDescriptor extends AbstractSelectDescriptor {

   /**
    * The name of the column as it appears in the source (table or view).
    */
    private String name;

    /**
     * The alias of the source (FROM or JOIN) from which this column originates.
     * If not specified, it defaults to the main source defined in {@code @Projection}.
     */
    private String from;

    /**
     * Returns the effective reference name for this column to be used in clauses
     * like ORDER BY or GROUP BY.
     * <p>
     * This returns the alias if defined; otherwise, it falls back to the column name.
     * This fallback ensures that references to this column always resolve correctly
     * even when no explicit alias is provided.
     * </p>
     *
     * @return the alias if present, or the original column name otherwise
     */
    @Override
    public String getReference() {
        return getAlias() != null ? getAlias() : name;
    }
}
