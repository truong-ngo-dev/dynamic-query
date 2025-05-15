package vn.truongngo.lib.dynamicquery.metadata.scanner;

import vn.truongngo.lib.dynamicquery.metadata.entity.EntityMetadata;

/**
 * A generic interface for scanning and extracting metadata from a source object (typically a class, annotation,
 * or configuration object) to produce an {@link EntityMetadata} representation.
 *
 * <p>
 * This abstraction allows for pluggable metadata scanning strategies. For example,
 * implementations can scan JPA entities, XML configuration, or even custom metadata annotations.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * EntityScanner<Class<?>> jpaScanner = new JpaEntityScanner();
 * EntityMetadata metadata = jpaScanner.scan(MyEntity.class);
 * }</pre>
 * </p>
 *
 * @param <T> the source type to scan (e.g., {@code Class<?>} for JPA, or a configuration file/descriptor)
 * @author Truong Ngo
 * @version 2.0.0
 */
public interface EntityScanner<T> {

    /**
     * Scans the given source and returns the corresponding {@link EntityMetadata}.
     *
     * @param source the input source to scan (e.g., a Java class)
     * @return the entity metadata extracted from the source
     */
    EntityMetadata scan(T source);

}
