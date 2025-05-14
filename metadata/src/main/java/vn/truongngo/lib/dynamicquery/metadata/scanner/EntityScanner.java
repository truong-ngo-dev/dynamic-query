package vn.truongngo.lib.dynamicquery.metadata.scanner;

import vn.truongngo.lib.dynamicquery.metadata.entity.EntityMetadata;

public interface EntityScanner<T> {
    EntityMetadata scan(T source);
}
