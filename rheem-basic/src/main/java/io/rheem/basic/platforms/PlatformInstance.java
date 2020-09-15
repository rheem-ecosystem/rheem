package io.rheem.basic.platforms;

import io.rheem.core.plugin.Plugin;
import io.rheem.core.platform.Platform;

/**
 * TODO: check the writting
 * PlatformInstance is the connection with the platform and the components
 * that want to use the Platform. Each Platform have differente repository
 * and because of it is need to have this class to allow the isolation of
 * the code
 */
public abstract class PlatformInstance {

    /**
     * Empty Constructor to be enable to instance.
     */
    public PlatformInstance(){}

    /**
     * Retrieve the name of the platform and it will use key for recovery
     * proposes
     *
     * @return {@link String} with the name of the platform instance
     */
    public abstract String name();

    /**
     * Retrieve the {@link Plugin} with the basic operators
     *
     * @return the {@link Plugin} with the basic operators
     */
    public abstract Plugin basicPlugin();

    /**
     * etrieve the {@link Plugin} with the graph operators
     *
     * @return the {@link Plugin} with graph operators
     */
    public abstract Plugin graphPlugin();

    /**
     * Retrieve the {@link Plugin} with the channel conversions operators
     *
     * @return the {@link Plugin} with the channel conversions operators
     */
    public abstract Plugin channelConversionPlugin();

    /**
     * Retrieve the {@link Platform} where is possible to the instance and
     * all the elements necessary to execute the platform.
     *
     * @return the {@link Platform}
     */
    public abstract Platform platform();
}
