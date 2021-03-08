package org.elasticsearch.engine;

import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.engine.EngineFactory;
import org.elasticsearch.index.engine.InternalEngineFactory;
import org.elasticsearch.plugins.EnginePlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.Optional;

/**
 * 创建日期 2020/07/30
 *
 * @author pengzhuowei@fybdp.com
 * @since 1.0.0
 */
public class CustomEnginePlugin extends Plugin implements EnginePlugin {


    @Override
    public Optional<EngineFactory> getEngineFactory(IndexSettings indexSettings) {
//        return Optional.of(new InternalEngineFactory().newReadWriteEngine());
    }
}
