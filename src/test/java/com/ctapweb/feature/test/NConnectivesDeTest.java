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

public class NConnectivesDeTest {
	JCas jCas;
	XMLParser pars;
	AnalysisEngineDescription aedSent, aedToken;
	HashMap <String, ArrayList <String>> paramsHashMap;
	ArrayList<String> locationsListForTest;
	String testResourcesFolder = "src/test/resources/org.apache.uima.fit/";
	
	@Before
	public void setUp() throws Exception {
				
		pars = UIMAFramework.getXMLParser();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		
		ArrayList<String> locationsList = new ArrayList<String>();
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/ComplexityFeatureBaseType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/NTokenType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports ("src/main/resources/descriptor/type_system/feature_type/NConnectivesType.xml", testResourcesFolder+"NConnectivesTypeForUIMAFitTest.xml", locationsList);
		String sdSentenceLengthTypeDescr = new String(Files.readAllBytes(Paths.get(testResourcesFolder+"NConnectivesTypeForUIMAFitTest.xml")));
		
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
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "123321"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		locationsListForTest.add("../../src/main/resources/descriptor/type_system/feature_type/NConnectivesType.xml");
	}
	
	
	/*
	 * Checks that the number of additive connectives according to Breindl for META-INF/de-test-text.txt is 14.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NAdditiveConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_Additive_Feature.xml", testResourcesFolder+"NConnectives_Breindl_Additive_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(14.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of adversative connectives according to Breindl for META-INF/de-test-text.txt is 2.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NAdversativeConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_Adversative_Feature.xml", testResourcesFolder+"NConnectives_Breindl_Adversative_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(2.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of adversative and concessive connectives according to Breindl for META-INF/de-test-text.txt is 3.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NAdversativeConcessiveConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_AdversativeConcessive_Feature.xml", testResourcesFolder+"NConnectives_Breindl_AdversativeConcessive_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(3.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of all connectives according to Breindl for META-INF/de-test-text.txt is 21.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NAllConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_All_Feature.xml", testResourcesFolder+"NConnectives_Breindl_All_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(21.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of all multi connectives according to Breindl for META-INF/de-test-text.txt is 1.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NAllMultiConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_AllMulti_Feature.xml", testResourcesFolder+"NConnectives_Breindl_AllMulti_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(1.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of all single connectives according to Breindl for META-INF/de-test-text.txt is 20.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NAllSingleConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_AllSingle_Feature.xml", testResourcesFolder+"NConnectives_Breindl_AllSingle_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(20.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of causal connectives according to Breindl for META-INF/de-test-text.txt is 2.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NCausalConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_Causal_Feature.xml", testResourcesFolder+"NConnectives_Breindl_Causal_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(2.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of concessive connectives according to Breindl for META-INF/de-test-text.txt is 1.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NConcessiveConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_Concessive_Feature.xml", testResourcesFolder+"NConnectives_Breindl_Concessive_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(1.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of other connectives according to Breindl for META-INF/de-test-text.txt is 1.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NOtherConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_Other_Feature.xml", testResourcesFolder+"NConnectives_Breindl_Other_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(1.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of temporal connectives according to Breindl for META-INF/de-test-text.txt is 1.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NTemporalConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_Temporal_Feature.xml", testResourcesFolder+"NConnectives_Breindl_Temporal_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(1.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * *********************************************************************************
	 */
	
	/*
	 * Checks that the number of additive connectives according to Eisenberg for META-INF/de-test-text.txt is 14.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NAdditiveConnectivesEisenbergFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Eisenberg_Additive_Feature.xml", testResourcesFolder+"NConnectives_Eisenberg_Additive_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(14.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of adversative connectives according to Eisenberg for META-INF/de-test-text.txt is 2.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NAdversativeConnectivesEisenbergFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Eisenberg_Adversative_Feature.xml", testResourcesFolder+"NConnectives_Eisenberg_Adversative_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(5.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of adversative and concessive connectives according to Eisenberg for META-INF/de-test-text.txt is 3.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NAdversativeConcessiveConnectivesEisenbergFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Eisenberg_AdversativeConcessive_Feature.xml", testResourcesFolder+"NConnectives_Eisenberg_AdversativeConcessive_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(12.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of causal connectives according to Eisenberg for META-INF/de-test-text.txt is 2.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NCausalConnectivesEisenbergFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Eisenberg_Causal_Feature.xml", testResourcesFolder+"NConnectives_Eisenberg_Causal_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(10.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of concessive connectives according to Eisenberg for META-INF/de-test-text.txt is 1.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NConcessiveConnectivesEisenbergFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Eisenberg_Concessive_Feature.xml", testResourcesFolder+"NConnectives_Eisenberg_Concessive_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(9.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of other connectives according to Eisenberg for META-INF/de-test-text.txt is 1.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NOtherConnectivesEisenbergFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Eisenberg_Other_Feature.xml", testResourcesFolder+"NConnectives_Eisenberg_Other_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(1.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of temporal connectives according to Eisenberg for META-INF/de-test-text.txt is 1.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NTemporalConnectivesEisenbergFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Eisenberg_Temporal_Feature.xml", testResourcesFolder+"NConnectives_Eisenberg_Temporal_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(13.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
}
