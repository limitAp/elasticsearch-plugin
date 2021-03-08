package org.elasticsearch.plugin;

import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.SearchPlugin;
import org.elasticsearch.query.CustomQueryStringBuilder;

import java.util.List;

import static java.util.Collections.singletonList;


public class CustomQueryPlugin extends Plugin implements SearchPlugin {

    public CustomQueryPlugin() {

    }

    @Override
    public List<QuerySpec<?>> getQueries() {
        return singletonList(new QuerySpec<>(CustomQueryStringBuilder.NAME, CustomQueryStringBuilder::new, CustomQueryStringBuilder::fromXContent));
    }
}
