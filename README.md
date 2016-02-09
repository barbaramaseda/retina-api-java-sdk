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

If you just need the JAR file, this can be downloaded directly from the <a href="https://search.maven.org/#search|ga|1|io.cortical">Sonatype Central Repository</a> directly or by adding this dependency to a Maven pom.xml:

```java
<dependency>
    <groupId>io.cortical</groupId>
    <artifactId>retina-api-java-sdk</artifactId>
    <version>1.0.1</version>
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

* The client is compatible with Java version: 1.7 and above. 
* It is compatible with all 2.x.x versions of <a href="http://api.cortical.io">cortical.io's API</a>. 
* To use the API you will need to obtain an <a href="http://www.cortical.io/resources_apikey.html">API key</a>.


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
// Create "lightweight" LiteClient instance
LiteClient lite = new io.cortical.retina.client.LiteClient("your_api_key");
```

Once you've created a client instance, you can start using it to make calls to the Retina API. Here some examples, the 
last comment in each block shows a print of the last variable assigned:

```java
// Retrieve similar terms
List<String> terms = lite.getSimilarTerms("java");
// [java, implementations, api, functionality, compiler, programmers, runtime, object-oriented, perl, javascript, interface, python, unix, interfaces, executable, proprietary, software, server, user, browser]

// Return keywords of a text
List<String> keywords = lite.getKeywords("Vienna is the capital and largest city of Austria, and one of the nine states of Austria");
// [austria, vienna]

// Compute a semantic fingerprint for an input text
int[] computerFP = lite.getFingerprint("computer");
// [0, 6, 7, 29, 34, 35, 38, 120, 122, 123, 128, 129, 149, 163, 164, 166, 167, 194, 195, 204, 255, ..., 16327, 16379, 16382]

// Compute the similarity between two texts
double similarity = lite.compare("apple", "microsoft");
// 0.4024390243902438

// Compute the similarity between two fingerprints
int[] appleFP = lite.getFingerprint("apple");
int[] microsoftFP = lite.getFingerprint("microsoft");
double fingerprintSimilarity = lite.compare(appleFP, microsoftFP);
// 0.4024390243902438

// Compute the similarity between a text and a fingerprint
double textFingerprintSimilarity = lite.compare("Microsoft Corporation is an American multinational technology company headquartered in Redmond, Washington", lite.getFingerprint("apple"));
// 0.15913190765753138

// Construct a composite Fingerprint from an array of texts to use for semantic filtering
int[] neurologyFilter = lite.createCategoryFilter(Arrays.asList("neuron", "synapse", "neocortex"));
// [6, 77, 78, 85, 119, 120, 124, 125, 128, 150, 163, 164, 167, 212, 242, 246, 253, 371, ..., 16376, 16381]

// Use the neurologyFilter computed above to compare and classify new texts.
double similaritySkylab = lite.compare("skylab", neurologyFilter);
// 0.056544622895956895 -- low semantic similarity -> negative classification
double similarityCorticalColumn = lite.compare("cortical column", neurologyFilter);
// 0.35455851788907006 -- high semantic similarity -> positive classification
```

### FullClient

The FullClient module provides complete access to the entire Retina API and allows for more flexibility in configuring request parameters than the LiteClient module. Some functionality included with the FullClient not available in the LiteClient are operations on expressions, images and bulk requests.

As with the LiteClient, the FullClient must be instantiated with a valid Cortical.io API key:

```java
// Create FullClient instance
FullClient fullClient = new FullClient("your_api_key");
```

Additional parameters can also be passed when creating a FullClient instance to specify the host address (in case you have access to your own Retina API service, for example by running your own AWS or Azure instance) and Retina name, so you can configure a specific Retina for subsequent calls.

// Create FullClient instance with explicit server address and Retina name
FullClient fullClient = new FullClient("your_api_key", "http://api.cortical.io/rest/", "en_associative");

#### Semantic Expressions

The semantic fingerprint is the basic unit within the Retina API. A text or a term can be resolved into a fingerprint using the API. Fingerprints can also be combined in expressions, and a number of methods expect input in our expression language. This is explained in more detail [here](http://documentation.cortical.io/the_power_of_expressions.html).

Expressions are represented internally as json strings with reserved keys: term, text, and positions. In the Java client, this formatting is abstracted away, by letting the user work with Term and Text objects. The client offers a static helper class io.cortical.retina.model.ExpressionFactory which has convenience methods for creating Term and Text objects as well as methods (and, or, sub, and, xor) to assist in building expressions.

For instance, to create a Term object for the term brain you can use the static method term:

```java
Term term = ExpressionFactory.term("brain");
```

And to AND two terms together you can use the ExpressionFactory.and static method. This method returns an Expression (an object of type ExpressionModel).

```java
ExpressionModel andExpression = ExpressionFactory.and(ExpressionFactory.term("brain"), ExpressionFactory.term("cortex"));
```

Expression operators can take multiple input types. For instance, you can OR a Term object with a Text object. We can now get the most similar terms for such an expression:

```java
ExpressionModel orExpression = ExpressionFactory.or(ExpressionFactory.term("brain"), ExpressionFactory.text("a region of the cerebral cortex"));
List<Term> terms = fullClient.getSimilarTermsForExpression(orExpression);
```

(In reality the Expression operation is performed on the semantic fingerprints of the respective input types).

#### FullClient Examples

```java
// Create FullClient instance
FullClient fullClient = new FullClient("c109d280-73ec-11e4-a595-532580ca217d");

// Retrieve an array of all available Retinas
List<Retina> retinas = fullClient.getRetinas();

// Retrieve information about a specific term
Term term = fullClient.getTerm("java");

// Get contexts for a given term
List<Context> contextsForTerm = fullClient.getContextsForTerm("java");

// Get similar terms and their Fingerprints for a given term
List<Term> similarTermsForTerm = fullClient.getSimilarTermsForTerm("java", 0, null, 0, 10, true);

// Encode a text into a Semantic Fingerprint
Fingerprint fingerprintForText = fullClient.getFingerprintForText(javaText);

// Return keywords from a text
List<String> keywordsForText = fullClient.getKeywordsForText(javaText);

// Returns tokens from an input text
List<String> tokens = fullClient.getTokensForText(javaText);

// Slice the input text according to semantic changes (works best on
// larger texts of at least several sentences)
fullClient.getSlicesForText(javaText);

// Return Semantic Fingerprints for numerous texts in a single call
List<String> texts = new ArrayList<String>();
texts.add(javaText);
texts.add(javascriptText);
List<Fingerprint> fingerprintsForTexts = fullClient.getFingerprintsForTexts(texts);

// Detect the language for an input text
Language languageForText = fullClient.getLanguageForText(javaText);

// Return the Fingerprint for an input expression
Fingerprint fingerprintForExpression = fullClient.getFingerprintForExpression(new Text(javaText));

// Return contexts for an input expression
List<Context> contextsForExpression = fullClient.getContextsForExpression(new Text(javaText));

// Return similar terms for an input expression
List<Term> similarTermsForExpression = fullClient.getSimilarTermsForExpression(new Text(javaText));

List<Model> expressions = new ArrayList<Model>();
expressions.add(new Text(javaText));
expressions.add(new Text(javascriptText));

// Return Fingerprints for multiple semantic expressions
List<Fingerprint> fingerprintsForExpressions = fullClient.getFingerprintsForExpressions(expressions);

// Return contexts for multiple semantic expressions
List<List<Context>> contextsForExpressions = fullClient.getContextsForExpressions(expressions);

// Return similar terms for multiple semantic expressions
List<List<Term>> similarTermsForExpressions = fullClient.getSimilarTermsForExpressions(expressions);

// Compute the semantic similarity of two input expressions
Metric compare = fullClient.compare(new Term("synapse"), new Term("skylab"));

// Make multiple comparisons in a single call
List<CompareModel> compareModels = new ArrayList<CompareModel>();
CompareModel comparison1 = new CompareModel(new Term("synapse"), new Term("skylab"));
CompareModel comparison2 = new CompareModel(new Term("mir"), new Text("skylab was a space station"));
compareModels.add(comparison1);
compareModels.add(comparison2);
Metric[] compareBulk = fullClient.compareBulk(compareModels);

// Create an image from an expression
ByteArrayInputStream image = fullClient.getImage(new Term("synapse"));

// Create multiple images from multiple expressions in a single call
List<Image> images = fullClient.getImages(expressions);

// Create a composite image showing the visual overlap between two expressions
ByteArrayInputStream compareImage = fullClient.compareImage(expressions);

// Create a filter Fingerprint from example texts that should "pass through" the filter
CategoryFilter createCategoryFilter = fullClient.createCategoryFilter("test_filter", texts, null);
```

## Support

For further documentation about the Retina API and information about Cortical.io's 'Retina' technology please see our 
[Knowledge Base](http://www.cortical.io/resources_tutorials.html). Also the `src/test/java` folder contains more examples of how to use the 
clients modules. 

If you have any questions or problems please visit our [forum](http://www.cortical.io/resources_forum.html).

## Change Log
**v 1.0.1**
* Improved image handling.

**v 1.0.0**
* Initial release.
* Refactoring and migrating project from pervious GitHub repo. 