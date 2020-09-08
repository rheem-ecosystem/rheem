package io.rheem.tests;

import io.rheem.platforms.PlatformPlugins;
import org.junit.Assert;
import org.junit.Test;
import io.rheem.api.JavaPlanBuilder;
import io.rheem.api.LoadCollectionDataQuantaBuilder;
import io.rheem.api.MapDataQuantaBuilder;
import io.rheem.core.api.RheemContext;
import io.rheem.core.util.RheemArrays;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class hosts and documents some tests for bugs that we encountered. Ultimately, we want to avoid re-introducing
 * already encountered and fixed bugs.
 */
public class RegressionIT {

    /**
     * This plan revealed an issue with the {@link io.rheem.core.optimizer.channels.ChannelConversionGraph} ShortestTreeSearcher.
     */
    @Test
    public void testCollectionToRddAndBroadcast() {
        RheemContext rheemContext = new RheemContext().with(PlatformPlugins.Spark.basicPlugin()).with(PlatformPlugins.Java.basicPlugin());
        JavaPlanBuilder planBuilder = new JavaPlanBuilder(rheemContext, "testCollectionToRddAndBroadcast");

        LoadCollectionDataQuantaBuilder<String> collection = planBuilder
                .loadCollection(Arrays.asList("a", "bc", "def"))
                .withTargetPlatform(PlatformPlugins.Java.platform())
                .withName("collection");

        MapDataQuantaBuilder<String, Integer> map1 = collection
                .map(String::length)
                .withTargetPlatform(PlatformPlugins.Spark.platform());

        MapDataQuantaBuilder<Integer, Integer> map2 = planBuilder
                .loadCollection(RheemArrays.asList(-1))

                .map(i -> i)
                .withBroadcast(collection, "broadcast")
                .withTargetPlatform(PlatformPlugins.Spark.platform());

        ArrayList<Integer> result = new ArrayList<>(map1.union(map2).collect());

        result.sort(Integer::compareTo);
        Assert.assertEquals(
                RheemArrays.asList(-1, 1, 2, 3),
                result
        );
    }


}
