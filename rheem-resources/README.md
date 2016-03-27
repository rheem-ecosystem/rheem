Rheem
=====

### What is Rheem?

Rheem is an efficient and scalable distributed data processing framework developed by the data analytic group at [Qatar Computing Research Institute](http://da.qcri.org). It has three distinctive features:

1. it allows users to easily specify their jobs with easy-to-use interfaces,
2. it provides developers with opportunities to optimize performance in different ways, and
3. it can run on any execution platform, such as Spark or MapReduce and combinations of those.

### Prerequisites

To be able to run a Rheem application, the following software is needed.
- [Java 1.8](http://www.java.com/en/download/faq/develop.xml)
- [Apache Maven](http://maven.apache.org)
- Include the rheem jar files into your project.
- In case spark is needed; Spark(v1.6 and over), hadoop (v2.2 to v2.6.2)

### Platforms support
- Java
- [Apache Spark](https://spark.apache.org/)
- Coming soon
    - [Graphchi](https://github.com/GraphChi/graphchi-java)
    - [Postgres](www.postgresql.org)
    - [Alluxio](http://www.alluxio.org/)

### Download and Usage
- You can download [the latest jar from here](http://da.qcri.org/rheem/download.html).
- Include the rheem jar as a library in your application.
- Steps for writing a rheem application:
    1. Define a rheem plan using rheem operators. For a list of all currently supported rheem operators check the [api documentation](operators-api-url)
    2. Create a rheem context.
    3. Register required platforms with rheem context. You might want to include an "app.properties" file in the root directory of your application to set the platform specific properties. 
    4. Execute rheemplan.


### Example

``` 
    # app.properties file
    spark.master = local
    spark.appName= myapp
```


```java
       // Build the RheemPlan that reads from a text file as source, 
       // performs an uppercase on all characters and output to a localcallback sink
       
       // Create a plan
        RheemPlan rheemPlan = new RheemPlan();
        // Define the operators.
        TextFileSource textFileSource = new TextFileSource("file.txt");
        MapOperator<String, String> upperOperator = new MapOperator<>(
            String::toUpperCase, String.class, String.class
        );
        LocalCallbackSink<String> stdoutSink =  LocalCallbackSink.createStdoutSink(String.class);
        
        // Connect the operators together.
        textFileSource.connectTo(0, upperOperator, 0);
        reverseOperator.connectTo(0, stdoutSink, 0);
        
        // Add a sink to the rheem plan.
        rheemPlan.addSink(stdoutSink);
        
        // Instantiate Rheem context and register the backends.
        RheemContext rheemContext = new RheemContext();
        rheemContext.register(JavaPlatform.getInstance());
        rheemContext.register(SparkPlatform.getInstance());
        
        //Execute the plan
        rheemContext.execute(rheemPlan);
```
    
