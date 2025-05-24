package vn.truongngo.lib.dynamicquery.projection.processor;

import vn.truongngo.lib.dynamicquery.projection.descriptor.PredicateDescriptor;
import vn.truongngo.lib.dynamicquery.projection.descriptor.ProjectionDescriptor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides and caches {@link ProjectionDescriptor} instances for projection classes.
 * <p>
 * This class uses a singleton pattern to ensure a single cache instance is used throughout the application.
 * It maintains a cache of projection descriptors, scanning and storing them as needed.
 * </p>
 *
 * <h2>Usage:</h2>
 * <blockquote><pre>
 * ProjectionDescriptorProvider provider = ProjectionDescriptorProvider.getInstance();
 * ProjectionDescriptor descriptor = provider.getProjectionDescriptor(MyProjection.class);
 * </pre></blockquote>
 * </p>
 *
 * <p>
 * The cache is implemented as a {@link LinkedHashMap} to preserve insertion order.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public class ProjectionDescriptorProvider {

    /**
     * Returns the singleton instance of {@code ProjectionDescriptorProvider}.
     *
     * @return the singleton instance
     */
    public static ProjectionDescriptorProvider getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Holder class for lazy initialization of the singleton instance.
     * Utilizes the initialization-on-demand holder idiom.
     */
    private static class Holder {
        private static final ProjectionDescriptorProvider INSTANCE = new ProjectionDescriptorProvider();
    }

    /**
     * Cache for projection descriptors.
     * <p>
     * The cache is a LinkedHashMap to maintain insertion order.
     * The key is the projection class, and the value is its {@link ProjectionDescriptor}.
     * </p>
     */
    private final Map<Class<?>, ProjectionDescriptor> PROJECTION_CACHE = new LinkedHashMap<>();

    /**
    * Cache for predicate descriptors.
    * <p>
    * The cache is a LinkedHashMap to maintain insertion order.
    * The key is the projection class, and the value is its {@link PredicateDescriptor}.
    * </p>
    */
    private final Map<Class<?>, PredicateDescriptor> CRITERIA_CACHE = new LinkedHashMap<>();

    /**
     * Retrieves the {@link ProjectionDescriptor} for the given projection class.
     * <p>
     * If the descriptor is already cached, it returns the cached instance.
     * Otherwise, it scans the class to create a new descriptor, caches it, and returns it.
     * </p>
     *
     * @param clazz the projection class to scan
     * @return the corresponding {@link ProjectionDescriptor}
     * @throws NullPointerException if {@code clazz} is {@code null}
     * @throws IllegalArgumentException if the class is not properly annotated
     */
    public ProjectionDescriptor getProjectionDescriptor(final Class<?> clazz) {
        if (PROJECTION_CACHE.containsKey(clazz)) {
            return PROJECTION_CACHE.get(clazz);
        }

        ProjectionDescriptor projectionDescriptor = ProjectionScanner.scanProjection(clazz);
        PROJECTION_CACHE.put(clazz, projectionDescriptor);

        return projectionDescriptor;
    }



    /**
     * Retrieves the {@link PredicateDescriptor} for the given projection class.
     * <p>
     * If the descriptor is already cached, it returns the cached instance.
     * Otherwise, it scans the class to create a new descriptor, caches it, and returns it.
     * </p>
     *
     * @param clazz the projection class to scan
     * @return the corresponding {@link PredicateDescriptor}
     * @throws NullPointerException if {@code clazz} is {@code null}
     * @throws IllegalArgumentException if the class is not properly annotated
     */
    public PredicateDescriptor getPredicateDescriptor(final Class<?> clazz, Class<?> projectionType) {
        if (CRITERIA_CACHE.containsKey(clazz)) {
            return CRITERIA_CACHE.get(clazz);
        }

        ProjectionDescriptor projectionDescriptor = getProjectionDescriptor(projectionType);
        PredicateDescriptor predicateDescriptor = ProjectionScanner.scanPredicate(clazz, projectionDescriptor);
        CRITERIA_CACHE.put(clazz, predicateDescriptor);

        return predicateDescriptor;
    }
}
