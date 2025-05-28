package vn.truongngo.lib.dynamicquery.querydsl.projection;

import com.querydsl.core.Tuple;
import vn.truongngo.lib.dynamicquery.projection.descriptor.ProjectionDescriptor;
import vn.truongngo.lib.dynamicquery.projection.descriptor.SelectDescriptor;
import vn.truongngo.lib.dynamicquery.projection.processor.ProjectionDescriptorProvider;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Utility class for mapping QueryDSL {@link Tuple} results to projection instances.
 * <p>
 * This class provides methods to convert QueryDSL tuples into instances of projection classes
 * based on the metadata defined in {@link ProjectionDescriptor}.
 * </p>
 *
 * @author Truong Ngo
 * @version 2.0.0
 */
public class QuerydslProjectionMapper {

    /**
     * Maps a {@link Tuple} result from QueryDSL to a projection instance based on the given {@link ProjectionDescriptor}.
     *
     * @param tuple      the QueryDSL tuple containing query results
     * @param descriptor the descriptor containing projection metadata
     * @param <P>        the type of the projection class
     * @return an instance of the projection class with fields populated from the tuple
     */
    public static <P> P mapTupleToProjection(Tuple tuple, Class<P> projectionClass) {
        ProjectionDescriptor descriptor = ProjectionDescriptorProvider.getInstance().getProjectionDescriptor(projectionClass);
        try {
            P instance = projectionClass.getDeclaredConstructor().newInstance();
            List<SelectDescriptor> selects = descriptor.getSelects();
            for (SelectDescriptor select : selects) {
                Field field = select.getField();
                if (field == null) continue;
                field.setAccessible(true);
                Object value = tuple.get(select.getIndex(), field.getType());
                field.set(instance, value);
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map tuple to projection: " + descriptor.getTarget().getSimpleName(), e);
        }
    }
}
