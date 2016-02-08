/*******************************************************************************
 * Copyright (c) cortical.io GmbH. All rights reserved.
 *
 * This software is confidential and proprietary information.
 * You shall use it only in accordance with the terms of the
 * license agreement you entered into with cortical.io GmbH.
 ******************************************************************************/
package io.cortical.retina.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cortical.retina.core.Compare.CompareModel;
import io.cortical.retina.core.ImagePlotShape;
import io.cortical.retina.core.PosTag;
import io.cortical.retina.core.PosType;
import io.cortical.retina.model.CategoryFilter;
import io.cortical.retina.model.Context;
import io.cortical.retina.model.Fingerprint;
import io.cortical.retina.model.Image;
import io.cortical.retina.model.Language;
import io.cortical.retina.model.Metric;
import io.cortical.retina.model.Model;
import io.cortical.retina.model.Retina;
import io.cortical.retina.model.Term;
import io.cortical.retina.model.Text;
import io.cortical.retina.rest.ApiException;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class FullClientIntegrationTest {
    
    private FullClient fullClient;
    private String apiKey;
    
    String javaText =
            "Java is a general-purpose programming language that is concurrent, class-based, object-oriented.";
    String javascriptText = "JavaScript is a dynamically typed object-oriented programming language.";
    
    String longText =
            "George L. Fox was fruit born March 15, 1900, in Lewistown, Pennsylvania, computer eight children. When he was 17, he"
                    + "left school and lied about his age in order to join the Army to  serve in World War I. He joined the ambulance corps"
                    + "in 1917, assigned to Camp Newton D. Baker in Texas. On December 3, 1917, George embarked from Camp Merritt, New Jersey,"
                    + "and boarded the USS Huron en route to France. As a medical corps fruit, he fruit highly decorated for bravery and was"
                    + "awarded the Silver Star, Purple Heart and the French Croix de Guerre.  Upon his fruit, he returned home to Altoona,"
                    + "where he completed high school. He entered Moody Bible Institute in Illinois in 1923. He and Isadora G. Hurlbut of"
                    + "Vermont were married in 1923, when he began his religious career as an itinerant preacher in the Methodist banana. He"
                    + "later graduated from Illinois University in Bloomington, served as a student pupil in Rye, New Hampshire, and then"
                    + "studied at the Boston University School of Theology, where he was ordained a Methodist minister on June 10, 1934. He"
                    + "served parishes in Union Village and Gilman, Vermont, and was appointed state chaplain and historian for the American"
                    + "Legion in Vermont. In 1942, Fox fruit to serve as an Army chaplain, accepting his appointment July 24, 1942. He began"
                    + "active duty on August 8, 1942, the same day his son Wyatt enlisted in the Marine Corps. After Army Chaplains school at"
                    + "Harvard, apple he reported to the 411th Coast Artillery Battalion at Camp Davis. He computer then united with banana"
                    + "Chaplains Goode, Poling and Washington at Camp Myles Standish in Taunton.";
                    
                    
    private Set<PosTag> nounTags;
    
    /**
     * initialization.
     */
    @Before
    public void before() {
        apiKey = System.getProperty("apiKey");
        
        nounTags = new HashSet<PosTag>();
        nounTags.add(PosTag.NN);
        nounTags.add(PosTag.NNP);
        
        fullClient = new FullClient(apiKey);
    }
    
    @Test
    public void testConstructor() throws JsonProcessingException, ApiException {
        FullClient as = new FullClient(apiKey);
        FullClient sy = new FullClient(apiKey, "en_synonymous");
        Assert.assertNotEquals(as.getFingerprintForExpression(new Term("java")),
                sy.getFingerprintForExpression(new Term("java")));
    }
    
    @Test
    public void testRetinas() throws ApiException {
        // Retrieve an array of all available Retinas
        List<Retina> retinas = fullClient.getRetinas();
        for (Retina retina : retinas) {
            Assert.assertTrue(retina.getNumberOfTermsInRetina() > 500000);
        }
    }
    
    @Test
    public void testTerms() throws ApiException {
        // Retrieve information about a specific term
        List<Term> term1 = fullClient.getTerms("java");
        Assert.assertEquals(1, term1.size());
        
        List<Term> term2 = fullClient.getTerms("java", 0, 5, true);
        Assert.assertEquals(1, term2.size());
        
        // contexts
        List<Context> contextsForTerm1 = fullClient.getContextsForTerm("java");
        Assert.assertTrue(contextsForTerm1.size() > 3);
        for (Context context : contextsForTerm1) {
            Assert.assertTrue(context.getContextLabel().length() > 1);
            Assert.assertEquals(0, context.getFingerprint().getPositions().length);
        }
        
        List<Context> contextsForTerm2 = fullClient.getContextsForTerm("java", 0, 3, true);
        Assert.assertEquals(3, contextsForTerm2.size());
        for (Context context : contextsForTerm2) {
            Assert.assertTrue(context.getContextLabel().length() > 1);
            Assert.assertTrue(context.getFingerprint().getPositions().length > 50);
        }
        
        // similar terms
        List<Term> similarTermsForTerm1 = fullClient.getSimilarTermsForTerm("java");
        Assert.assertEquals(10, similarTermsForTerm1.size());
        Assert.assertEquals("java", similarTermsForTerm1.get(0).getTerm());
        Assert.assertEquals(0, similarTermsForTerm1.get(0).getFingerprint().getPositions().length);
        
        List<Term> similarTermsForTerm2 = fullClient.getSimilarTermsForTerm("java", -1, null, 0, 12, true);
        Assert.assertEquals(12, similarTermsForTerm2.size());
        Assert.assertEquals("java", similarTermsForTerm2.get(0).getTerm());
        Assert.assertEquals(328, similarTermsForTerm2.get(0).getFingerprint().getPositions().length);
    }
    
    @Test
    public void testText() throws ApiException, JsonProcessingException {
        // Encode a text into a Semantic Fingerprint
        Fingerprint fingerprintForText = fullClient.getFingerprintForText(javaText);
        Assert.assertTrue(fingerprintForText.getPositions().length > 300);
        
        // Return keywords from a text
        List<String> keywordsForText = fullClient.getKeywordsForText(javaText);
        Assert.assertEquals(2, keywordsForText.size());
        
        
        // Returns tokens from an input text
        List<String> tokens1 = fullClient.getTokensForText(javaText + " " + javascriptText);
        Assert.assertEquals(2, tokens1.size());
        Assert.assertEquals(11, tokens1.get(0).split(",").length);
        
        List<String> tokens2 = fullClient.getTokensForText(javaText, nounTags);
        Assert.assertEquals(tokens2.get(0), "java,programming,language");
        
        // slices
        List<Text> slices1 = fullClient.getSlicesForText(longText);
        Assert.assertEquals(3, slices1.size());
        Assert.assertEquals(0, slices1.get(0).getFingerprint().getPositions().length);
        Assert.assertTrue(slices1.get(0).getText().length() > 200);
        
        List<Text> slices2 = fullClient.getSlicesForText(longText, 0, 2, true);
        Assert.assertEquals(2, slices2.size());
        Assert.assertTrue(slices2.get(0).getFingerprint().getPositions().length > 50);
        Assert.assertTrue(slices2.get(0).getText().length() > 200);
        
        // bulk
        List<String> texts = new ArrayList<String>();
        texts.add(javaText);
        texts.add(javascriptText);
        List<Fingerprint> fingerprintsForTexts1 = fullClient.getFingerprintsForTexts(texts);
        Assert.assertEquals(2, fingerprintsForTexts1.size());
        for (Fingerprint fp : fingerprintsForTexts1) {
            Assert.assertTrue(fp.getPositions().length > 50);
        }
        
        List<Fingerprint> fingerprintsForTexts2 = fullClient.getFingerprintsForTexts(texts, 1.0);
        Assert.assertEquals(2, fingerprintsForTexts2.size());
        for (Fingerprint fp : fingerprintsForTexts2) {
            Assert.assertTrue(fp.getPositions().length > 50);
        }
        
        // Detect the language for an input text
        Language languageForText1 = fullClient.getLanguageForText("I have a dream!");
        Assert.assertEquals("English", languageForText1.getLanguage());
        
        Language languageForText2 = fullClient.getLanguageForText("Ich bin ein");
        Assert.assertEquals("http://en.wikipedia.org/wiki/German_language", languageForText2.getWiki_url());
        
        Language languageForText3 = fullClient.getLanguageForText("Der var så dejligt ude på landet.");
        Assert.assertEquals("da", languageForText3.getIso_tag());
    }
    
    
    @Test
    public void testExpressions() throws JsonProcessingException, ApiException {
        // Return the Fingerprint for an input expression
        Fingerprint fingerprintForExpression1 = fullClient.getFingerprintForExpression(new Text(javaText));
        Assert.assertTrue(fingerprintForExpression1.getPositions().length > 300);
        Fingerprint fingerprintForExpression2 = fullClient.getFingerprintForExpression(new Text(javaText), 1.0);
        Assert.assertTrue(fingerprintForExpression2.getPositions().length > 300);
        
        
        // Return contexts for an input expression
        List<Context> contextsForExpression1 = fullClient.getContextsForExpression(new Text(javaText));
        Assert.assertTrue(contextsForExpression1.size() > 3);
        for (Context context : contextsForExpression1) {
            Assert.assertTrue(context.getContextLabel().length() > 1);
            Assert.assertEquals(0, context.getFingerprint().getPositions().length);
        }
        
        List<Context> contextsForExpression2 = fullClient.getContextsForExpression(new Text(javaText), 0, 3, 1.0, true);
        Assert.assertEquals(3, contextsForExpression2.size());
        for (Context context : contextsForExpression2) {
            Assert.assertTrue(context.getContextLabel().length() > 1);
            Assert.assertTrue(context.getFingerprint().getPositions().length > 50);
        }
        
        // Return similar terms for an input expression
        List<Term> similarTermsForExpression1 = fullClient.getSimilarTermsForExpression(new Text(javaText));
        Assert.assertEquals(10, similarTermsForExpression1.size());
        Assert.assertEquals(0, similarTermsForExpression1.get(0).getFingerprint().getPositions().length);
        
        List<Term> similarTermsForExpression2 =
                fullClient.getSimilarTermsForExpression(new Text(javaText), 0, 12, -1, PosType.NOUN, true, 1.0);
        Assert.assertEquals(12, similarTermsForExpression2.size());
        for (Term t : similarTermsForExpression2) {
            Assert.assertEquals(328, t.getFingerprint().getPositions().length);
            Assert.assertTrue(Arrays.asList(t.getPosTypes()).contains("NOUN"));
        }
        
        // bulk
        List<Model> expressions = new ArrayList<Model>();
        expressions.add(new Text(javaText));
        expressions.add(new Text(javascriptText));
        expressions.add(new Term("java"));
        
        List<Fingerprint> fingerprintsForExpressions1 = fullClient.getFingerprintsForExpressions(expressions);
        Assert.assertEquals(3, fingerprintsForExpressions1.size());
        for (Fingerprint fp : fingerprintsForExpressions1) {
            Assert.assertTrue(fp.getPositions().length > 50);
        }
        
        List<Fingerprint> fingerprintsForExpressions2 = fullClient.getFingerprintsForExpressions(expressions, 1.0);
        Assert.assertEquals(3, fingerprintsForExpressions2.size());
        for (Fingerprint fp : fingerprintsForExpressions2) {
            Assert.assertTrue(fp.getPositions().length > 50);
        }
        
        // bulk contexts
        List<List<Context>> contextsForExpressions1 = fullClient.getContextsForExpressions(expressions);
        Assert.assertEquals(3, contextsForExpressions1.size());
        for (List<Context> contexts : contextsForExpressions1) {
            Assert.assertTrue(contexts.size() > 4);
            Assert.assertEquals(0, contexts.get(0).getFingerprint().getPositions().length);
        }
        
        List<List<Context>> contextsForExpressions2 =
                fullClient.getContextsForExpressions(expressions, 0, 3, true, 1.0);
        Assert.assertEquals(3, contextsForExpressions2.size());
        for (List<Context> contexts : contextsForExpressions2) {
            Assert.assertEquals(3, contexts.size());
            Assert.assertTrue(contexts.get(0).getFingerprint().getPositions().length > 50);
            Assert.assertEquals(0, contexts.get(0).getContextId());
        }
        
        
        // bulk similar terms
        List<List<Term>> similarTermsForExpressions1 = fullClient.getSimilarTermsForExpressions(expressions);
        Assert.assertEquals(3, similarTermsForExpressions1.size());
        for (List<Term> terms : similarTermsForExpressions1) {
            Assert.assertEquals(10, terms.size());
            Assert.assertEquals(0, terms.get(0).getFingerprint().getPositions().length);
        }
        
        List<List<Term>> similarTermsForExpressions2 =
                fullClient.getSimilarTermsForExpressions(expressions, 0, 5, -1, PosType.NOUN, true, 1.0);
        Assert.assertEquals(3, similarTermsForExpressions2.size());
        for (List<Term> terms : similarTermsForExpressions2) {
            Assert.assertEquals(5, terms.size());
            Assert.assertTrue(terms.get(0).getFingerprint().getPositions().length > 50);
            for (Term t : terms) {
                Assert.assertEquals(328, t.getFingerprint().getPositions().length);
                Assert.assertTrue(Arrays.asList(t.getPosTypes()).contains("NOUN"));
            }
        }
    }
    
    @Test
    public void testCompare() throws JsonProcessingException, ApiException {
        // Compute the semantic similarity of two input expressions
        Metric resultMetric = fullClient.compare(new Term("apple"), new Text("banana is a kind of fruit"));
        Assert.assertTrue(resultMetric.getCosineSimilarity() > 0.1);
        Assert.assertTrue(resultMetric.getEuclideanDistance() > 0.1);
        Assert.assertTrue(resultMetric.getJaccardDistance() > 0.1);
        Assert.assertTrue(resultMetric.getWeightedScoring() > 0.1);
        Assert.assertTrue(resultMetric.getSizeRight() > 10);
        Assert.assertTrue(resultMetric.getSizeLeft() > 10);
        Assert.assertTrue(resultMetric.getOverlappingLeftRight() > 0.1);
        Assert.assertTrue(resultMetric.getOverlappingAll() > 10);
        Assert.assertTrue(resultMetric.getOverlappingRightLeft() > 0.1);

        // Make multiple comparisons in a single call
        List<CompareModel> compareModels = new ArrayList<CompareModel>();
        CompareModel comparison1 = new CompareModel(new Term("synapse"), new Term("skylab"));
        CompareModel comparison2 = new CompareModel(new Term("mir"), new Text("skylab was a space station"));
        compareModels.add(comparison1);
        compareModels.add(comparison2);
        Metric[] compareBulk = fullClient.compareBulk(compareModels);
        Assert.assertEquals(2, compareBulk.length);
        for (Metric metric: compareBulk) {
            Assert.assertTrue(metric.getCosineSimilarity() > 0.01);
            Assert.assertTrue(metric.getEuclideanDistance() > 0.01);
            Assert.assertTrue(metric.getJaccardDistance() > 0.01);
            Assert.assertTrue(metric.getWeightedScoring() > 0.01);
            Assert.assertTrue(metric.getSizeRight() > 1);
            Assert.assertTrue(metric.getSizeLeft() > 1);
            Assert.assertTrue(metric.getOverlappingLeftRight() > 0.01);
            Assert.assertTrue(metric.getOverlappingAll() > 1);
            Assert.assertTrue(metric.getOverlappingRightLeft() > 0.01);
        }
    }

    @Test
    public void testImage() throws JsonProcessingException, ApiException {
        // Create an image from an expression
        ByteArrayInputStream image = fullClient.getImage(new Term("term"));
        System.out.println(image);
        Assert.assertNotNull(image);
        
        // Create a composite image showing the visual overlap between two expressions
        List<Model> twoItems = new ArrayList<Model>();
        twoItems.add(new Term("java"));
        twoItems.add(new Text(javaText));
        ByteArrayInputStream compareImage = fullClient.compareImage(twoItems);
        Assert.assertNotNull(compareImage);        
        
        // Create multiple images from multiple expressions in a single call
        List<Model> expressions = new ArrayList<Model>();
        expressions.add(new Text(javaText));
        expressions.add(new Text(javascriptText));
        expressions.add(new Term("java"));
        
        List<Image> images1 = fullClient.getImages(expressions);
        Assert.assertEquals(3, images1.size());
        for (Image img : images1) {
            System.out.println(img);
            Assert.assertNotNull(img);
            Assert.assertEquals(0, img.getFingerprint().getPositions().length);
            Assert.assertNotNull(img.getImageData());
        }

        List<Image> images2 = fullClient.getImages(expressions, true, 2, ImagePlotShape.SQUARE, 1.0);
        for (Image img : images2) {
            Assert.assertNotNull(img);
            System.out.println(img);
            Assert.assertTrue(img.getFingerprint().getPositions().length > 50);
            Assert.assertNotNull(img.getImageData());
        }
    }
    
    
    @Test
    public void testClassify() throws ApiException, JsonProcessingException {
        // Create a filter Fingerprint from example texts that should "pass through" the filter
        List<String> positiveExamples = new ArrayList<String>();
        positiveExamples.add(javaText);
        positiveExamples.add(javascriptText);
        CategoryFilter categoryFilter = fullClient.createCategoryFilter("test_filter", positiveExamples, null);
        Assert.assertEquals("test_filter", categoryFilter.getCategoryName());
        Assert.assertTrue(categoryFilter.getPositions().length > 50);
    }
}
