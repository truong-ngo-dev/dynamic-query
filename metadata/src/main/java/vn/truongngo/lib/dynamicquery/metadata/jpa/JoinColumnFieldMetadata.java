package vn.truongngo.lib.dynamicquery.metadata.jpa;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.truongngo.lib.dynamicquery.metadata.entity.DefaultFieldMetadata;

/**
 * Represents metadata for a field that maps to a foreign key column
 * using {@code @JoinColumn} in JPA.
 * <p>
 * This class extends {@link DefaultFieldMetadata} and adds support for
 * referencing the name of the target column in the joined entity.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
@Getter
@Setter
@SuperBuilder
public class JoinColumnFieldMetadata extends DefaultFieldMetadata implements ReferenceColumnMetadata {

    /**
     * The name of the column in the referenced entity that this field points to.
     */
    private String referenceColumnName;

}
