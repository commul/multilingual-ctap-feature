package com.ctapweb.feature.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import com.ctapweb.feature.test.util.DescriptorModifier;
import com.ctapweb.feature.type.ComplexityFeatureBase;

public class LexicalVariationFeatureDeTest {
	JCas jCas;
	XMLParser pars;
	AnalysisEngineDescription aedSent, aedToken, aedPOS;
	HashMap <String, ArrayList <String>> paramsHashMap;
	ArrayList<String> locationsListForTest;
	
	
	@Before
	public void setUp() throws Exception {
		
		pars = UIMAFramework.getXMLParser();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		
		ArrayList<String> locationsList = new ArrayList<String>();
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/ComplexityFeatureBaseType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/POSType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports ("src/main/resources/descriptor/type_system/feature_type/LexicalVariationType.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LexicalVariationTypeForUIMAFitTest.xml", locationsList);
		String sdSentenceLengthTypeDescr = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LexicalVariationTypeForUIMAFitTest.xml")));
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(sdSentenceLengthTypeDescr.getBytes("UTF-8")));
		
		tsd.buildFromXMLElement(doc.getDocumentElement(), pars);
	    jCas = CasCreationUtils.createCas(tsd, null, null).getJCas();
		
	    String contents = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+"/META-INF/de-test-text.txt")));
		jCas.setDocumentText(contents);
		
		File fSent = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/SentenceAnnotator.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/SentenceAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceSent = new XMLInputSource(fSent);
		aedSent = pars.parseAnalysisEngineDescription(xmlInputSourceSent);
	
		File fToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/TokenAnnotator.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/TokenAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceToken = new XMLInputSource(fToken);
		aedToken = pars.parseAnalysisEngineDescription(xmlInputSourceToken);
	
		File fPOS = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/POSAnnotator.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/POSAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourcePOS = new XMLInputSource(fPOS);
		aedPOS = pars.parseAnalysisEngineDescription(xmlInputSourcePOS);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "2222"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		locationsListForTest.add("../../src/main/resources/descriptor/type_system/feature_type/LexicalVariationType.xml");
	}
	
	
	/*
	 * Checks that the Lexical Variation Feature: Adjective for META-INF/de-test-text.txt is 0.15432098765432098, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalVariationAdjectiveFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalVariation_Adjective_Feature.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LexicalVariation_Adjective_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2222){
				assertEquals(0.15432098765432098, annot.getValue(), 0.0000001); 
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Variation Feature: Adverb for META-INF/de-test-text.txt is 0.08641975308641975, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalVariationAdverbFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalVariation_Adverb_Feature.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LexicalVariation_Adverb_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2222){
				assertEquals(0.08641975308641975, annot.getValue(), 0.0000001); 
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Variation Feature: Lexical for META-INF/de-test-text.txt is 0.7901234567901234, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalVariationLexicalFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalVariation_Lexical_Feature.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LexicalVariation_Lexical_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2222){
				assertEquals(0.7901234567901234, annot.getValue(), 0.0000001); 
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Variation Feature: Modifier for META-INF/de-test-text.txt is 0.24074074074074073, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalVariationModifierFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalVariation_Modifier_Feature.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LexicalVariation_Modifier_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2222){
				assertEquals(0.24074074074074073, annot.getValue(), 0.0000001); 
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Variation Feature: Noun for META-INF/de-test-text.txt is 0.345679012345679, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalVariationNounFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalVariation_Noun_Feature.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LexicalVariation_Noun_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2222){
				assertEquals(0.345679012345679, annot.getValue(), 0.0000001); 
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Variation Feature: Verb for META-INF/de-test-text.txt is 0.1419753086419753, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalVariationVerbFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalVariation_Verb_Feature.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LexicalVariation_Verb_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2222){
				assertEquals(0.1419753086419753, annot.getValue(), 0.0000001); 
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Variation Feature: Corrected Verb Variation 1 for META-INF/de-test-text.txt is 3.3197640478403483, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalVariationVerbCVV1FeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalVariation_Verb_CVV1_Feature.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LexicalVariation_Verb_CVV1_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2222){
				assertEquals(3.3197640478403483, annot.getValue(), 0.0000001); 
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Variation Feature: Squared Verb Variation 1 for META-INF/de-test-text.txt is 22.041666666666668, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalVariationVerbSVV1FeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalVariation_Verb_SVV1_Feature.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LexicalVariation_Verb_SVV1_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2222){
				assertEquals(22.041666666666668, annot.getValue(), 0.0000001); 
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Variation Feature: Verb Variation 1 for META-INF/de-test-text.txt is 0.9583333333333334, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalVariationVerbVV1FeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalVariation_Verb_VV1_Feature.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LexicalVariation_Verb_VV1_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2222){
				assertEquals(0.9583333333333334, annot.getValue(), 0.0000001);
			}
		}
	}
	
}

