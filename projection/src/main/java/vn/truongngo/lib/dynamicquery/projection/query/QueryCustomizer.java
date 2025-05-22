package vn.truongngo.lib.dynamicquery.projection.query;

import vn.truongngo.lib.dynamicquery.core.builder.QueryMetadata;

public interface QueryCustomizer {

    <C> void customize(QueryMetadata queryMetadata, C context);
}
