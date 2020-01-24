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

public class CohesiveComplexityItTest {
	JCas jCas;
	XMLParser pars;
	AnalysisEngineDescription aedSent, aedToken, aedNToken, aedACFeature;
	HashMap <String, ArrayList <String>> paramsHashMap, paramsHashMapAC;
	ArrayList<String> locationsListForTest, locationsListForTestAC;
	String testResourcesFolder = "src/test/resources/org.apache.uima.fit/";
	
	@Before
	public void setUp() throws Exception {
				
		pars = UIMAFramework.getXMLParser();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		
		ArrayList<String> locationsList = new ArrayList<String>();
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/ComplexityFeatureBaseType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/NTokenType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/NConnectivesType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports ("src/main/resources/descriptor/type_system/feature_type/CohesiveComplexityType.xml", testResourcesFolder+"CohesiveComplexityTypeForUIMAFitTest.xml", locationsList);
		String sdSentenceLengthTypeDescr = new String(Files.readAllBytes(Paths.get(testResourcesFolder+"CohesiveComplexityTypeForUIMAFitTest.xml")));
		
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
		
		File fNToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddaeID ("src/main/resources/descriptor/featureAE/NTokenFeature.xml", testResourcesFolder+"NTokenFeatureForUIMAFitTest.xml", "IT", "123");
		XMLInputSource xmlInputSourceNToken = new XMLInputSource(fNToken);
		aedNToken = pars.parseAnalysisEngineDescription(xmlInputSourceNToken);
		
		paramsHashMapAC = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArrayAC = new ArrayList(2);
		String[] namesAC = {"integer", "123321"};
		dynamicStringArrayAC.addAll(Arrays.asList(namesAC));
		paramsHashMapAC.put("aeID", dynamicStringArrayAC);

		ArrayList dynamicStringArrayAC2 = new ArrayList(2);
		String[] namesAC2 = {"string", "IT"};
		dynamicStringArrayAC2.addAll(Arrays.asList(namesAC2));
		paramsHashMapAC.put("LanguageCode", dynamicStringArrayAC2);
	
		locationsListForTestAC = new ArrayList <String> ();
		locationsListForTestAC.add("../../src/main/resources/descriptor/type_system/feature_type/NConnectivesType.xml");
    
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Temporal Connectives Per Token_feature for META-INF/cani.txt is 0.0031847133757961785, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityTemporalFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_Temporal_Feature.xml", testResourcesFolder+"NConnectives_Breindl_Temporal_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731547"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Breindl_TempPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Breindl_TempPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731547){
				assertEquals(0.003787878787878788, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Concessive Connectives Per Token_feature for META-INF/cani.txt is 0.0031847133757961785, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityConcessiveFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_Concessive_Feature.xml", testResourcesFolder+"NConnectives_Breindl_Concessive_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731471"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Breindl_ConcessivePerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Breindl_ConcessivePerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731471){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Causal Connectives Per Token_feature for META-INF/cani.txt is 0.006369426751592357, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityCausalFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_Causal_Feature.xml", testResourcesFolder+"NConnectives_Breindl_Causal_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731472"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Breindl_CausalPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Breindl_CausalPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731472){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_All Connectives Per Token_feature for META-INF/cani.txt is 0.06687898089171974, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityAllFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_All_Feature.xml", testResourcesFolder+"NConnectives_Breindl_All_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731473"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Breindl_AllPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Breindl_AllPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731473){
				assertEquals(0.056818181818181816, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Additive Connectives Per Token_feature for META-INF/cani.txt is 0.044585987261146494, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityAdditiveFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_Additive_Feature.xml", testResourcesFolder+"NConnectives_Breindl_Additive_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731474"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Breindl_AddPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Breindl_AddPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731474){
				assertEquals(0.03409090909090909, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Adversative and Concessive Connectives Per Token_feature for META-INF/cani.txt is 0.009554140127388535, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityAdversativeConcessiveFeatureTest() throws Exception {		
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_AdversativeConcessive_Feature.xml", testResourcesFolder+"NConnectives_Breindl_AdversativeConcessive_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731475"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Breindl_ACPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Breindl_ACPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731475){
				assertEquals(0.011363636363636364, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Adversative Connectives Per Token_feature for META-INF/cani.txt is 0.009554140127388535, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityAdversativeFeatureTest() throws Exception {		
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_Adversative_Feature.xml", testResourcesFolder+"NConnectives_Breindl_Adversative_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731475"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_AdversativePerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_AdversativePerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731475){
				assertEquals(0.011363636363636364, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Argumentative Connectives Per Token_feature for META-INF/cani.txt is 0.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityArgumentativeFeatureTest() throws Exception {		
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_argumentative_Feature.xml", testResourcesFolder+"NConnectives_argumentative_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731475"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_ArgumentativePerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_ArgumentativePerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731475){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Consequence Connectives Per Token_feature for META-INF/cani.txt is 0.009554140127388535, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityConsequenceFeatureTest() throws Exception {		
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_consequence_Feature.xml", testResourcesFolder+"NConnectives_consequence_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731475"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_ConsequencePerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_ConsequencePerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731475){
				assertEquals(0.003787878787878788, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Hierarchy Connectives Per Token_feature for META-INF/cani.txt is 0.009554140127388535, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityHierarchyFeatureTest() throws Exception {		
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_hierarchy_Feature.xml", testResourcesFolder+"NConnectives_hierarchy_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731475"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_HierarchyPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_HierarchyPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731475){
				assertEquals(0.007575757575757576, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Hypothetical and Conditional Connectives Per Token_feature for META-INF/cani.txt is 0.009554140127388535, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityHypotheticalConditionalFeatureTest() throws Exception {		
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_hypothetical_conditional_Feature.xml", testResourcesFolder+"NConnectives_hypothetical_conditional_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731475"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_HypotheticalConditionalPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_HypotheticalConditionalPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731475){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Multifunctional Connectives Per Token_feature for META-INF/cani.txt is 0.009554140127388535, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityMultifunctionalFeatureTest() throws Exception {		
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_multifunctional_Feature.xml", testResourcesFolder+"NConnectives_multifunctional_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731475"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_MultifunctionalPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_MultifunctionalPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731475){
				assertEquals(0.011363636363636364, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Purpose Connectives Per Token_feature for META-INF/cani.txt is 0.009554140127388535, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityPurposeFeatureTest() throws Exception {		
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_purpose_Feature.xml", testResourcesFolder+"NConnectives_purpose_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731475"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_PurposePerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_PurposePerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731475){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Explicative Connectives Per Token_feature for META-INF/cani.txt is 0.009554140127388535, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityExplicativeFeatureTest() throws Exception {		
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_explicative_Feature.xml", testResourcesFolder+"NConnectives_explicative_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731475"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_ExplicativePerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_ExplicativePerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731475){
				assertEquals(0.007575757575757576, annot.getValue(), 0.0000001);
			}
		}
	}
	
}
