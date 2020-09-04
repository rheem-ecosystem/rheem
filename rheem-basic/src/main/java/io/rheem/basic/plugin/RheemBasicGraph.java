package io.rheem.basic.plugin;

import io.rheem.basic.mapping.Mappings;
import io.rheem.core.api.Configuration;
import io.rheem.core.mapping.Mapping;
import io.rheem.core.optimizer.channels.ChannelConversion;
import io.rheem.core.platform.Platform;
import io.rheem.core.plugin.Plugin;

import java.util.Collection;
import java.util.Collections;

/**
 * Activator for graph operations being executed with Rheem's basic operators.
 */
@SuppressWarnings("unused") // It's loaded via reflection.
public class RheemBasicGraph implements Plugin {

    @Override
    public void setProperties(Configuration configuration) {
    }

    @Override
    public Collection<Mapping> getMappings() {
        return Mappings.GRAPH_MAPPINGS;
    }

    @Override
    public Collection<Platform> getRequiredPlatforms() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ChannelConversion> getChannelConversions() {
        return Collections.emptyList();
    }

}
