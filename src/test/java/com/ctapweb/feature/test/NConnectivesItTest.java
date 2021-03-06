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

public class NConnectivesItTest {
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
		
	    String contents = new String(Files.readAllBytes(Paths.get("src/test/resources/cani.txt")));
		jCas.setDocumentText(contents);
		
		File fSent = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/SentenceAnnotator.xml", testResourcesFolder+"SentenceAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceSent = new XMLInputSource(fSent);
		aedSent = pars.parseAnalysisEngineDescription(xmlInputSourceSent);
	
		File fToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/TokenAnnotator.xml", testResourcesFolder+"TokenAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceToken = new XMLInputSource(fToken);
		aedToken = pars.parseAnalysisEngineDescription(xmlInputSourceToken);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "123321"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		locationsListForTest.add("../../../../src/main/resources/descriptor/type_system/feature_type/NConnectivesType.xml");
	}
	
	
	/*
	 * Checks that the number of additive connectives according to Lorenzo Zanasi and Nadezda Okinina for META-INF/cani.txt is 9.0, with the precision of 0.0000001.
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
				assertEquals(9.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of adversative connectives according to Breindl for META-INF/cani.txt is 2.0, with the precision of 0.0000001.
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
				assertEquals(3.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of adversative and concessive connectives according to Breindl for META-INF/cani.txt is 3.0, with the precision of 0.0000001.
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
	 * Checks that the number of all connectives according to Breindl for META-INF/cani.txt is 21.0, with the precision of 0.0000001.
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
				assertEquals(15.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of argumentative connectives according to Lorenzo zanasi and Nadezda Okinina for META-INF/cani.txt is 21.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NArgumentativeConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_argumentative_Feature.xml", testResourcesFolder+"NConnectives_argumentative_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of consequence connectives according to Lorenzo zanasi and Nadezda Okinina for META-INF/cani.txt is 21.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NConsequenceConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_consequence_Feature.xml", testResourcesFolder+"NConnectives_consequence_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
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
	 * Checks that the number of causal connectives according to Lorenzo Zanasi and Nadezda Okinina for META-INF/cani.txt is 2.0, with the precision of 0.0000001.
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
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of concessive connectives according to Lorenzo Zanasi and Nadezda Okinina for META-INF/cani.txt is 0.0, with the precision of 0.0000001.
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
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	
	/*
	 * Checks that the number of temporal connectives according to Lorenzo Zanasi and Nadezda Okinina for META-INF/cani.txt is 1.0, with the precision of 0.0000001.
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
	 * Checks that the number of temporal connectives according to Lorenzo Zanasi and Nadezda Okinina for META-INF/cani.txt is 1.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NPurposeConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_purpose_Feature.xml", testResourcesFolder+"NConnectives_purpose_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of hierarchical connectives according to Lorenzo Zanasi and Nadezda Okinina for META-INF/cani.txt is 1.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NHierarchicalConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_hierarchy_Feature.xml", testResourcesFolder+"NConnectives_hierarchy_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
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
	 * Checks that the number of hypothetical and conditional connectives according to Lorenzo Zanasi and Nadezda Okinina for META-INF/cani.txt is 1.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NHypotheticalConditionalConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_hypothetical_conditional_Feature.xml", testResourcesFolder+"NConnectives_hypothetical_conditional_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123321){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of multifunctional connectives according to Lorenzo Zanasi and Nadezda Okinina for META-INF/cani.txt is 1.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void NMultifunctionalConnectivesFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_multifunctional_Feature.xml", testResourcesFolder+"NConnectives_multifunctional_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
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
	
}
