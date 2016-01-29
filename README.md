cortical.io
===========
Welcome to the cortical.io Retina API Java SDK page.

Release Version: 1.0.0

This page contains
<UL>
<LI><B>Introduction</B></LI>
<LI><B>Dependencies</B></LI>
<LI><B>Maven Repository</B></LI>
<LI><B>How to use</B></LI>
<LI><B>Change Log</B></LI>
</UL>


### Introduction
cortical.io's Retina API Java SDK is a simple Maven project that builds a Java HTTP client which simplifies communication between any Java application and the Retina API server using the Retina's REST API.


### Dependencies
cortical.io's Retina API Java SDK is compatible with Java version: 1.7. See the respective pom files for each project for details regarding dependencies.

Compatible with all 2.x.x versions of <a href="http://api.cortical.io">cortical.io's api</a>.

To use the API you will need to obtain an <a href="http://www.cortical.io/resources_apikey.html">api key</a>.

### Maven Repository
If you just need the JAR file, this can be downloaded directly from the <a href="https://search.maven.org/#search|ga|1|io.cortical">Sonatype Central Repository</a> directly or by adding the dependency to a Maven pom:

```
<dependency>
    <groupId>io.cortical</groupId>
    <artifactId>retina-api-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

### How to use/build
* You will need to have Maven 3.x installed.
* Clone all the sources from our Github repository.
* Navigate to the main directory (retina-api-java-sdk) and build the project by typing on the command line: ```mvn clean install -DapiKey=YourAPIKey```
   * The jar file produced by the client api project (found in the target directory) can then be imported into any project (e.g. the example project).
   * To import the JAR into an existing Maven project, see the below code example:

```
<dependency>
    <groupId>io.cortical</groupId>
    <artifactId>retina-api-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

   * For non-Maven projects you need to use the ```retina-api-java-sdk-1.0.0-jar-with-dependencies``` (e.g. when directly calling the java client).

* For examples of usage please see:
   * The unit tests included with the project.
    
For further documentation about the Retina-API and information on cortical.io's 'Retina' technology please see: 
http://www.cortical.io/resources_tutorials.html

If you have any questions or problems please visit our forum:
http://www.cortical.io/resources_forum.html

### Change Log
<B>v 1.0.0</B>
* Initial release.
* Refactoring and migrating project from pervious GitHub repo. 