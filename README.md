# retina-api-java-sdk - A Java Client for the Cortical.io Retina API

Java wrapper library for the [Cortical.io API](http://api.cortical.io/). Register for a [free Cortical.io 
API key](http://www.cortical.io/resources_apikey.html) and include RetinaSDK.js to add language intelligence to your 
application.

## Introduction

Cortical.io's Retina API allows the user to perform semantic operations on text. One can for example:

* measure the semantic similarity between two written entities
* create a semantic classifier based on positive and negative example texts
* extract keywords from a text
* divide a text into sub-sections corresponding to semantic changes
* extract terms from a text based on part of speech tags

The meaning of terms and texts is stored in a sparse binary representation that allows the user to apply logical 
operators to refine the semantic representation of a concept.

You can read more about the technology at the [documentation page](http://documentation.cortical.io/intro.html).

To access the API, you will need to register for an [API key](http://www.cortical.io/resources_apikey.html).


## Installation

If you just need the JAR file, this can be downloaded directly from the <a href="https://search.maven.org/#search|ga|1|io.cortical">Sonatype Central Repository</a> directly or by adding this dependency to a Maven pom:

```java
<dependency>
    <groupId>io.cortical</groupId>
    <artifactId>retina-api-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

For non-Maven projects you need to use the ```retina-api-java-sdk-1.0.0-jar-with-dependencies``` (e.g. when directly calling the java client).

### Building

If you want to build the jar, you will need to: 

* Clone the Github repository
* Install Maven 3.x
* Navigate to the main directory (retina-api-java-sdk) and build the project by typing on the command line: ```mvn clean install -DapiKey=YourAPIKey```
    * The jar file produced by the client api project (found in the target directory) can then be imported into any project. 


### Dependencies

* The client is compatible with Java version: 1.7. 
* It is compatible with all 2.x.x versions of <a href="http://api.cortical.io">cortical.io's api</a>. 
* To use the API you will need to obtain an <a href="http://www.cortical.io/resources_apikey.html">api key</a>.


## Usage

retina-api-java-sdk offers two abstractions of the Cortical.io Retina API, a lightweight module that offers simplified 
access to the most common and useful API functions available and a full version module that gives the user complete 
control over various parameter settings and complete access to all API endpoints.
 
### LiteClient Module

The LiteClient module is sufficient for most applications and offers the ability to quickly and easily 
compute keywords for a text, semantically compare two texts, retrieve similar terms, create category filters for 
semantic filtering and generate semantic fingerprints of a given text. To get started, create an instance of the 
lightweight client by passing your API key as follows:  

```java
/* Create "lightweight" LiteClient instance */
LiteClient lite = new io.cortical.retina.client.LiteClient("your_api_key");
```

Once you've created a client instance, you can start using it to make calls to the Retina API. Here some examples, the 
last comment in each block is showing a print of the last variable assigned:

```java
/* Retrieve similar terms */
List<String> terms = lite.getSimilarTerms("java");
// [java, implementations, api, functionality, compiler, programmers, runtime, object-oriented, perl, javascript, interface, python, unix, interfaces, executable, proprietary, software, server, user, browser]

/* Return keywords of a text */
List<String> keywords = lite.getKeywords("Vienna is the capital and largest city of Austria, and one of the nine states of Austria");
// [austria, vienna]

/* Compute a semantic fingerprint for an input text */
int[] computerFP = lite.getFingerprint("computer");
// [0, 6, 7, 29, 34, 35, 38, 120, 122, 123, 128, 129, 149, 163, 164, 166, 167, 194, 195, 204, 255, ..., 16327, 16379, 16382]

/* Compute the similarity between two texts */
double similarity = lite.compare("apple", "microsoft");
// 0.4024390243902438

/* Compute the similarity between two fingerprints */
int[] appleFP = lite.getFingerprint("apple");
int[] microsoftFP = lite.getFingerprint("microsoft");
double fingerprintSimilarity = lite.compare(appleFP, microsoftFP);
// 0.4024390243902438

/* Compute the similarity between a text and a fingerprint */
double textFingerprintSimilarity = lite.compare("Microsoft Corporation is an American multinational technology company headquartered in Redmond, Washington", lite.getFingerprint("apple"));
// 0.15913190765753138

/* Construct a composite Fingerprint from an array of texts to use for semantic filtering */
int[] neurologyFilter = lite.createCategoryFilter(Arrays.asList("neuron", "synapse", "neocortex"));
// [6, 77, 78, 85, 119, 120, 124, 125, 128, 150, 163, 164, 167, 212, 242, 246, 253, 371, ..., 16376, 16381]

/* Use the neurologyFilter computed above to compare and classify new texts. */ 
double similaritySkylab = lite.compare("skylab", neurologyFilter);
// 0.056544622895956895 -- low semantic similarity -> negative classification
double similarityCorticalColumn = lite.compare("cortical column", neurologyFilter);
// 0.35455851788907006 -- high semantic similarity -> positive classification
```

### FullClient


## TODO: link to 'Uservoice'??

For further documentation about the Retina-API and information on cortical.io's 'Retina' technology please see: 
http://www.cortical.io/resources_tutorials.html

If you have any questions or problems please visit our forum:
http://www.cortical.io/resources_forum.html

## Change Log
**v 1.0.0**
* Initial release.
* Refactoring and migrating project from pervious GitHub repo. 