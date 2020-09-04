package io.rheem.iejoin.mapping;

import io.rheem.core.mapping.Mapping;
import io.rheem.iejoin.mapping.spark.IEJoinMapping;
import io.rheem.iejoin.mapping.spark.IESelfJoinMapping;
import io.rheem.iejoin.operators.IEJoinOperator;
import io.rheem.java.platform.JavaPlatform;
import io.rheem.spark.platform.SparkPlatform;

import java.util.Arrays;
import java.util.Collection;

/**
 * {@link Mapping}s for the {@link IEJoinOperator}.
 */
public class Mappings {

    /**
     * {@link Mapping}s towards the {@link JavaPlatform}.
     */
    public static Collection<Mapping> javaMappings = Arrays.asList(
            new io.rheem.iejoin.mapping.java.IEJoinMapping(), new io.rheem.iejoin.mapping.java.IESelfJoinMapping()
    );

    /**
     * {@link Mapping}s towards the {@link SparkPlatform}.
     */
    public static Collection<Mapping> sparkMappings = Arrays.asList(
            new IEJoinMapping(), new IESelfJoinMapping()
    );

}
