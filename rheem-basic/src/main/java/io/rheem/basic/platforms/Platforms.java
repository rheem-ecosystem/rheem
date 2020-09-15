package io.rheem.basic.platforms;

import java.util.HashMap;
import java.util.Map;

//TODO: add the documentation
public class Platforms {

    private static Platforms PLATFORMS;

    private final Map<String, PlatformInstance> platforms_register;

    public Platforms(){
        this.platforms_register = new HashMap<>();
        loadPlatforms();
    }

    public static PlatformInstance getInstanceOf(String name){
        if(PLATFORMS == null){
            PLATFORMS = new Platforms();
        }
        return PLATFORMS.getInstance(name);
    }

    public PlatformInstance getInstance(String name) throws PlatformNotFound {
        PlatformInstance instance = this.platforms_register.get(name);
        if(instance == null){
            throw new PlatformNotFound(
                String.format(
                    "The platform with name \'%s\' is not found, please check the configuration and " +
                        "the dependencies of the execution",
                    name
                )
            );
        }
        return instance;
    }

    private void loadPlatforms(){
        //TODO: add the code that read some path all load all the extension and everything from configuration files
    }
}
