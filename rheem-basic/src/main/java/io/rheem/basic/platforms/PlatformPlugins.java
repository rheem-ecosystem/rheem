package io.rheem.basic.platforms;

import io.rheem.core.platform.Platform;
import io.rheem.core.plugin.Plugin;

public enum PlatformPlugins {

    Java("java"),
    Spark("spark"),
    Flink("flink"),
    Postgres("postgres"),
    Giraph("giraph"),
    GraphChi("graphchi"),
    SQLite("sqlite3");

    private String name;
    private PlatformPlugins(String name){
        this.name = name;
    }

    public PlatformInstance getInstance(){
        return Platforms.getInstanceOf(this.name);
    }

    public Plugin basicPlugin(){
        return this.getInstance().basicPlugin();
    }

    public Plugin graphPlugin(){
        return this.getInstance().graphPlugin();
    }

    public Plugin channelConversionPlugin(){
        return this.getInstance().channelConversionPlugin();
    }

    public Platform platform(){
        return this.getInstance().platform();
    }
}
