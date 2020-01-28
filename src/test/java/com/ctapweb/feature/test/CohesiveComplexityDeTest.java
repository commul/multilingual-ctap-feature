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

public class CohesiveComplexityDeTest {
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
		
	    String contents = new String(Files.readAllBytes(Paths.get("src/test/resources/de-test-text.txt")));
		jCas.setDocumentText(contents);
		
		File fSent = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/SentenceAnnotator.xml", testResourcesFolder+"SentenceAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceSent = new XMLInputSource(fSent);
		aedSent = pars.parseAnalysisEngineDescription(xmlInputSourceSent);
	
		File fToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/TokenAnnotator.xml", testResourcesFolder+"TokenAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceToken = new XMLInputSource(fToken);
		aedToken = pars.parseAnalysisEngineDescription(xmlInputSourceToken);
		
		File fNToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddaeID ("src/main/resources/descriptor/featureAE/NTokenFeature.xml", testResourcesFolder+"NTokenFeatureForUIMAFitTest.xml", "DE", "123");
		XMLInputSource xmlInputSourceNToken = new XMLInputSource(fNToken);
		aedNToken = pars.parseAnalysisEngineDescription(xmlInputSourceNToken);
		
		paramsHashMapAC = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArrayAC = new ArrayList(2);
		String[] namesAC = {"integer", "123321"};
		dynamicStringArrayAC.addAll(Arrays.asList(namesAC));
		paramsHashMapAC.put("aeID", dynamicStringArrayAC);

		ArrayList dynamicStringArrayAC2 = new ArrayList(2);
		String[] namesAC2 = {"string", "DE"};
		dynamicStringArrayAC2.addAll(Arrays.asList(namesAC2));
		paramsHashMapAC.put("LanguageCode", dynamicStringArrayAC2);
	
		locationsListForTestAC = new ArrayList <String> ();
		locationsListForTestAC.add("../../../../src/main/resources/descriptor/type_system/feature_type/NConnectivesType.xml");
    
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Breindl_Temporal Connectives Per Token_feature for META-INF/de-test-text.txt is 0.0031847133757961785, with the precision of 0.0000001.
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
		String[] names2 = {"string", "DE"};
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
				assertEquals(0.0031847133757961785, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Eisenberg_Temporal Connectives Per Token_feature for META-INF/de-test-text.txt is 0.041401273885350316, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityTemporalEisenbergFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Eisenberg_Temporal_Feature.xml", testResourcesFolder+"NConnectives_Eisenberg_Temporal_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731547"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Eisenberg_TempPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Eisenberg_TempPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731547){
				assertEquals(0.041401273885350316, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Breindl_Other Connectives Per Token_feature for META-INF/de-test-text.txt is 0.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityOtherFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_Other_Feature.xml", testResourcesFolder+"NConnectives_Breindl_Other_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731479"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Breindl_OtherPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Breindl_OtherPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731479){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Eisenberg_Other Connectives Per Token_feature for META-INF/de-test-text.txt is 0.0031847133757961785, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityOtherEisenbergFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Eisenberg_Other_Feature.xml", testResourcesFolder+"NConnectives_Eisenberg_Other_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731479"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Eisenberg_OtherPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Eisenberg_OtherPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731479){
				assertEquals(0.0031847133757961785, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Breindl_Multi Connectives Per Connective_feature for META-INF/de-test-text.txt is 0.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityMultiPerConnFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_AllMulti_Feature.xml", testResourcesFolder+"NConnectives_Breindl_AllMulti_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "962398794"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Breindl_MultiConnPerConn_Feature.xml", testResourcesFolder+"CohesiveComplexity_Breindl_MultiConnPerConn_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 962398794){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Breindl_Single Connectives Per Connective_feature for META-INF/de-test-text.txt is 0.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexitySinglePerConnFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_AllSingle_Feature.xml", testResourcesFolder+"NConnectives_Breindl_AllSingle_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25471547"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Breindl_SingleConnPerConn_Feature.xml", testResourcesFolder+"CohesiveComplexity_Breindl_SingleConnPerConn_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25471547){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Breindl_Concessive Connectives Per Token_feature for META-INF/de-test-text.txt is 0.0031847133757961785, with the precision of 0.0000001.
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
		String[] names2 = {"string", "DE"};
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
				assertEquals(0.0031847133757961785, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Breindl_Single Connectives Per Token_feature for META-INF/de-test-text.txt is 0.06369426751592357, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexitySinglePerTokenFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Breindl_AllSingle_Feature.xml", testResourcesFolder+"NConnectives_Breindl_AllSingle_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731471"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Breindl_SingleConnPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Breindl_SingleConnPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731471){
				assertEquals(0.06369426751592357, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Eisenberg_Concessive Connectives Per Token_feature for META-INF/de-test-text.txt is 0.028662420382165606, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityConcessiveEisenbergFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Eisenberg_Concessive_Feature.xml", testResourcesFolder+"NConnectives_Eisenberg_Concessive_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731471"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Eisenberg_ConcessivePerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Eisenberg_ConcessivePerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731471){
				assertEquals(0.028662420382165606, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Breindl_Causal Connectives Per Token_feature for META-INF/de-test-text.txt is 0.006369426751592357, with the precision of 0.0000001.
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
		String[] names2 = {"string", "DE"};
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
				assertEquals(0.006369426751592357, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Eisenberg_Causal Connectives Per Token_feature for META-INF/de-test-text.txt is 0.03184713375796178, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityCausalEisenbergFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Eisenberg_Causal_Feature.xml", testResourcesFolder+"NConnectives_Eisenberg_Causal_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731472"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Eisenberg_CausalPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Eisenberg_CausalPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731472){
				assertEquals(0.03184713375796178, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Breindl_All Connectives Per Token_feature for META-INF/de-test-text.txt is 0.06687898089171974, with the precision of 0.0000001.
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
		String[] names2 = {"string", "DE"};
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
				assertEquals(0.06687898089171974, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Breindl_Additive Connectives Per Token_feature for META-INF/de-test-text.txt is 0.044585987261146494, with the precision of 0.0000001.
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
		String[] names2 = {"string", "DE"};
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
				assertEquals(0.044585987261146494, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Eisenberg_Additive Connectives Per Token_feature for META-INF/de-test-text.txt is 0.044585987261146494, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityAdditiveEisenbergFeatureTest() throws Exception {
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Eisenberg_Additive_Feature.xml", testResourcesFolder+"NConnectives_Eisenberg_Additive_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731474"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Eisenberg_AddPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Eisenberg_AddPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731474){
				assertEquals(0.044585987261146494, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Breindl_Adversative and Concessive Connectives Per Token_feature for META-INF/de-test-text.txt is 0.009554140127388535, with the precision of 0.0000001.
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
		String[] names2 = {"string", "DE"};
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
				assertEquals(0.009554140127388535, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the value of the Cohesive Complexity_Eisenberg_Adversative and Concessive Connectives Per Token_feature for META-INF/de-test-text.txt is 0.03821656050955414, with the precision of 0.0000001.
	 */
	
	@Test
	public void CohesionComplexityAdversativeConcessiveEisenbergFeatureTest() throws Exception {		
    	File fACFeature = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NConnectives_Eisenberg_AdversativeConcessive_Feature.xml", testResourcesFolder+"NConnectives_Eisenberg_AdversativeConcessive_FeatureForUIMAFitTest.xml", paramsHashMapAC, locationsListForTestAC);
		XMLInputSource xmlInputSourceACFeature = new XMLInputSource(fACFeature);
		aedACFeature = pars.parseAnalysisEngineDescription(xmlInputSourceACFeature);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "25731475"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/CohesiveComplexity_Eisenberg_ACPerToken_Feature.xml", testResourcesFolder+"CohesiveComplexity_Eisenberg_ACPerToken_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedNToken, aedACFeature, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 25731475){
				assertEquals(0.03821656050955414, annot.getValue(), 0.0000001);
			}
		}
	}
	
}
