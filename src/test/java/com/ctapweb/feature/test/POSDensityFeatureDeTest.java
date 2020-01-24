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

public class POSDensityFeatureDeTest {
	JCas jCas;
	XMLParser pars;
	AnalysisEngineDescription aedSent, aedToken, aedPOS, aedNToken;
	HashMap <String, ArrayList <String>> paramsHashMap;
	ArrayList<String> locationsListForTest;
	String testResourcesFolder = "src/test/resources/org.apache.uima.fit/";
	
	@Before
	public void setUp() throws Exception {
		pars = UIMAFramework.getXMLParser();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		
		ArrayList<String> locationsList = new ArrayList<String>();
		locationsList.add("../../../main/resources/descriptor/type_system/feature_type/ComplexityFeatureBaseType.xml");
		locationsList.add("../../../main/resources/descriptor/type_system/feature_type/POSDensityType.xml");
		locationsList.add("../../../main/resources/descriptor/type_system/feature_type/NTokenType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports ("src/main/resources/descriptor/type_system/feature_type/POSDensityType.xml", testResourcesFolder+"POSDensityTypeForUIMAFitTest.xml", locationsList);
		String sdSentenceLengthTypeDescr = new String(Files.readAllBytes(Paths.get(testResourcesFolder+"POSDensityTypeForUIMAFitTest.xml")));
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(sdSentenceLengthTypeDescr.getBytes("UTF-8")));
		
		tsd.buildFromXMLElement(doc.getDocumentElement(), pars);
	    jCas = CasCreationUtils.createCas(tsd, null, null).getJCas();
		
	    String contents = new String(Files.readAllBytes(Paths.get("src/test/resources/de-test-text.txt")));
		jCas.setDocumentText(contents);
		
		File fSent = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/SentenceAnnotator.xml", testResourcesFolder+"SentenceAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceSent = new XMLInputSource(fSent);
		aedSent = pars.parseAnalysisEngineDescription(xmlInputSourceSent);
		
		File fToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/TokenAnnotator.xml", testResourcesFolder+"TokenAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceToken = new XMLInputSource(fToken);
		aedToken = pars.parseAnalysisEngineDescription(xmlInputSourceToken);
		
		File fPOS = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/POSAnnotator.xml", testResourcesFolder+"POSAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourcePOS = new XMLInputSource(fPOS);
		aedPOS = pars.parseAnalysisEngineDescription(xmlInputSourcePOS);
		
		File fNToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddaeID ("src/main/resources/descriptor/featureAE/NTokenFeature.xml", testResourcesFolder+"NTokenFeatureForUIMAFitTest.xml", "DE", "123");
		XMLInputSource xmlInputSourceNToken = new XMLInputSource(fNToken);
		aedNToken = pars.parseAnalysisEngineDescription(xmlInputSourceNToken);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "333"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		locationsListForTest.add("../../src/main/resources/descriptor/type_system/feature_type/POSDensityType.xml");
	
	}
	
	
	/*
	 * Checks that the adjective density in META-INF/de-test-text.txt is 0.08280254777070063, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityAdjectiveFeatureTest() throws Exception {		
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Adjective_Feature.xml", testResourcesFolder+"POSDensity_Adjective_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.08280254777070063, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the adverb density in META-INF/de-test-text.txt is 0.07006369426751592, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityAdverbFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Adverb_Feature.xml", testResourcesFolder+"POSDensity_Adverb_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.07006369426751592, annot.getValue(), 0.0000001);
			}
		}
	}
		
	
	/*
	 * Checks that the article density in META-INF/de-test-text.txt is 0.11146496815286625, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityArticleFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Article_Feature.xml", testResourcesFolder+"POSDensity_Article_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.11146496815286625, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the auxiliary verb density in META-INF/de-test-text.txt is 0.041401273885350316, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityAuxiliaryVerbFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_AuxiliaryVerb_Feature.xml", testResourcesFolder+"POSDensity_AuxiliaryVerb_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.041401273885350316, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the cardinal number density in META-INF/de-test-text.txt is 0.025477707006369428, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityCardinalNumberFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_CardinalNumber_Feature.xml", testResourcesFolder+"POSDensity_CardinalNumber_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.025477707006369428, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the conjunction density in META-INF/de-test-text.txt is 0.07006369426751592, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityConjunctionFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Conjunction_Feature.xml", testResourcesFolder+"POSDensity_Conjunction_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.07006369426751592, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the coordinating conjunction density in META-INF/de-test-text.txt is 0.03184713375796178, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityCoordinatingConjunctionFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_CoordinatingConjunction_Feature.xml", testResourcesFolder+"POSDensity_CoordinatingConjunction_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.03184713375796178, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the coordinating conjunction density in META-INF/de-test-text.txt is 0.006369426751592357, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityDemonstrativePronounFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_DemonstrativePronoun_Feature.xml", testResourcesFolder+"POSDensity_DemonstrativePronoun_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.006369426751592357, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the determiner density in META-INF/de-test-text.txt is 0.13694267515923567, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityDeterminerFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Determiner_Feature.xml", testResourcesFolder+"POSDensity_Determiner_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.13694267515923567, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	
	/*
	 * Checks that the finite verb density in META-INF/de-test-text.txt is 0.10191082802547771, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityFiniteVerbFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_FiniteVerb_Feature.xml", testResourcesFolder+"POSDensity_FiniteVerb_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.10191082802547771, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the finite verb density in META-INF/de-test-text.txt is 0.0, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityForeignWordFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_ForeignWord_Feature.xml", testResourcesFolder+"POSDensity_ForeignWord_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the functional density in META-INF/de-test-text.txt is 0.25477707006369427, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityFunctionalFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Functional_Feature.xml", testResourcesFolder+"POSDensity_Functional_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.25477707006369427, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the indefinite pronoun density in META-INF/de-test-text.txt is 0.012738853503184714, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityIndefinitePronounFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_IndefinitePronoun_Feature.xml", testResourcesFolder+"POSDensity_IndefinitePronoun_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.012738853503184714, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the infinite verb density in META-INF/de-test-text.txt is 0.01592356687898089, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityInfiniteVerbFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_InfiniteVerb_Feature.xml", testResourcesFolder+"POSDensity_InfiniteVerb_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.01592356687898089, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the interjection density in META-INF/de-test-text.txt is , with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityInterjectionFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Interjection_Feature.xml", testResourcesFolder+"POSDensity_Interjection_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the interrogative pronoun density in META-INF/de-test-text.txt is 0.006369426751592357, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityInterrogativePronounFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_InterrogativePronoun_Feature.xml", testResourcesFolder+"POSDensity_InterrogativePronoun_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.006369426751592357, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the lexical density in META-INF/de-test-text.txt is 0.5159235668789809, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityLexicalFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Lexical_Feature.xml", testResourcesFolder+"POSDensity_Lexical_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.5159235668789809, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the main verb density in META-INF/de-test-text.txt is 0.10509554140127389, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityMainVerbFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_MainVerb_Feature.xml", testResourcesFolder+"POSDensity_MainVerb_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.10509554140127389, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the modal density in META-INF/de-test-text.txt is 0.006369426751592357, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityModalFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Modal_Feature.xml", testResourcesFolder+"POSDensity_Modal_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.006369426751592357, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the modal verb density in META-INF/de-test-text.txt is 0.0, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityModalVerbFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_ModalVerb_Feature.xml", testResourcesFolder+"POSDensity_ModalVerb_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the modifier density in META-INF/de-test-text.txt is 0.15286624203821655, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityModifierFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Modifier_Feature.xml", testResourcesFolder+"POSDensity_Modifier_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.15286624203821655, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the modifier density in META-INF/de-test-text.txt is 0.050955414012738856, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityNonFiniteVerbFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_NonFiniteVerb_Feature.xml", testResourcesFolder+"POSDensity_NonFiniteVerb_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.050955414012738856, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the noun density in META-INF/de-test-text.txt is 0.2515923566878981, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityNounFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Noun_Feature.xml", testResourcesFolder+"POSDensity_Noun_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.2515923566878981, annot.getValue(), 0.0000001);
			}
		}
	}
	
		
	/*
	 * Checks that the past participle verb density in META-INF/de-test-text.txt is 0.03503184713375796, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityPastParticipleVerbFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_PastParticipleVerb_Feature.xml", testResourcesFolder+"POSDensity_PastParticipleVerb_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.03503184713375796, annot.getValue(), 0.0000001);
			}
		}
	}
	

	/*
	 * Checks that the personal pronoun density in META-INF/de-test-text.txt is 0.03503184713375796, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityPersonalPronounFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_PersonalPronoun_Feature.xml", testResourcesFolder+"POSDensity_PersonalPronoun_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.03503184713375796, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	
	/*
	 * Checks that the possessive pronoun density in META-INF/de-test-text.txt is 0.009554140127388535, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityPossessivePronounFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_PossesivePronoun_Feature.xml", testResourcesFolder+"POSDensity_PossesivePronoun_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.009554140127388535, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	
	/*
	 * Checks that the preposition density in META-INF/de-test-text.txt is 0.10191082802547771, with the precision of 0.0000001.
	 */
	
	@Test
	public void POSDensityPrepositionFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Preposition_Feature.xml", testResourcesFolder+"POSDensity_Preposition_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.10191082802547771, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the pronoun density in META-INF/de-test-text.txt is 0.09554140127388536, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensityPronounFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Pronoun_Feature.xml", testResourcesFolder+"POSDensity_Pronoun_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.09554140127388536, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the proper noun density in META-INF/de-test-text.txt is 0.044585987261146494, with the precision of 0.0000001.
	 */
	
	@Test
	public void POSDensityProperNounFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_ProperNoun_Feature.xml", testResourcesFolder+"POSDensity_ProperNoun_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.044585987261146494, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the punctuation density in META-INF/de-test-text.txt is 0.12738853503184713, with the precision of 0.0000001.
	 */
	
	@Test
	public void POSDensityPunctuationFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Punctuation_Feature.xml", testResourcesFolder+"POSDensity_Punctuation_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.12738853503184713, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the relative pronoun density in META-INF/de-test-text.txt is 0.009554140127388535, with the precision of 0.0000001.
	 */
	
	@Test
	public void POSDensityRelativePronounFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_RelativePronoun_Feature.xml", testResourcesFolder+"POSDensity_RelativePronoun_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.009554140127388535, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the subordinating conjunction density in META-INF/de-test-text.txt is 0.028662420382165606, with the precision of 0.0000001.
	 */	
	
	@Test
	public void POSDensitySubordinatingConjunctionFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_SubordinatingConjunction_Feature.xml", testResourcesFolder+"POSDensity_SubordinatingConjunction_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.028662420382165606, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	
	/*
	 * Checks that the verb density in META-INF/de-test-text.txt is 0.1178343949044586, with the precision of 0.0000001.
	 */
	
	@Test
	public void POSDensityVerbFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/POSDensity_Verb_Feature.xml", testResourcesFolder+"POSDensity_Verb_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedNToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 333){
				assertEquals(0.1178343949044586, annot.getValue(), 0.0000001);
			}
		}
	}
		
}
