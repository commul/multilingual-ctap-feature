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

public class DLTDeTest {
	JCas jCas;
	XMLParser pars;
	AnalysisEngineDescription aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse;
	HashMap <String, ArrayList <String>> paramsHashMap;
	ArrayList<String> locationsListForTest;
	String testResourcesFolder = "src/test/resources/org.apache.uima.fit/";
	
	@Before
	public void setUp() throws Exception {
				
		pars = UIMAFramework.getXMLParser();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		
		ArrayList<String> locationsList = new ArrayList<String>();
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/ComplexityFeatureBaseType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/LemmaType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/MorphologicalTagType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/POSType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/DependencyParseType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports ("src/main/resources/descriptor/type_system/feature_type/DLTIntegrationCostType.xml", testResourcesFolder+"DLTIntegrationCostTypeForUIMAFitTest.xml", locationsList);
		//String DLTTypeDescr = new String(Files.readAllBytes(Paths.get(testResourcesFolder+"DLTIntegrationCostTypeForUIMAFitTest.xml")));
		String DLTTypeDescr = new String(Files.readAllBytes(Paths.get(testResourcesFolder+"DLTIntegrationCostTypeForUIMAFitTest.xml")));
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(DLTTypeDescr.getBytes("UTF-8")));
		
		tsd.buildFromXMLElement(doc.getDocumentElement(), pars);
	    jCas = CasCreationUtils.createCas(tsd, null, null).getJCas();
		
	    //String contents = new String(Files.readAllBytes(Paths.get("src/test/resources/de-test-text.txt")));
	    String contents = new String(Files.readAllBytes(Paths.get("src/test/resources/de-test-text.txt")));
		jCas.setDocumentText(contents);
		
		File fSent = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/SentenceAnnotator.xml", testResourcesFolder+"SentenceAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceSent = new XMLInputSource(fSent);
		aedSent = pars.parseAnalysisEngineDescription(xmlInputSourceSent);
	
		File fToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/TokenAnnotator.xml", testResourcesFolder+"TokenAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceToken = new XMLInputSource(fToken);
		aedToken = pars.parseAnalysisEngineDescription(xmlInputSourceToken);
	
		File fLemma = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/LemmaAnnotator.xml", testResourcesFolder+"LemmaAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceLemma = new XMLInputSource(fLemma);
		aedLemma = pars.parseAnalysisEngineDescription(xmlInputSourceLemma);
		
		File fMorph = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/MorphologicalTagAnnotator.xml", testResourcesFolder+"MorphologicalTagAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceMorph = new XMLInputSource(fMorph);
		aedMorph = pars.parseAnalysisEngineDescription(xmlInputSourceMorph);
		
		File fPOS = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/POSAnnotator.xml", testResourcesFolder+"POSAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourcePOS = new XMLInputSource(fPOS);
		aedPOS = pars.parseAnalysisEngineDescription(xmlInputSourcePOS);
		
		File fDepParse = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/DependencyParseAnnotator.xml", testResourcesFolder+"DependencyParseAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceDepParse = new XMLInputSource(fDepParse);
		aedDepParse = pars.parseAnalysisEngineDescription(xmlInputSourceDepParse);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "2506"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "DE"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		locationsListForTest.add("src/main/resources/descriptor/type_system/feature_type/DLTIntegrationCostType.xml");
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_cAdjacentFeature for META-INF/de-test-text.txt is 0.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_cAdjacentFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_cAdjacent_Feature.xml", testResourcesFolder+"DLTIntegrationCost_cAdjacent_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(0.0, annot.getValue(), 0.00);
			}
		}
	}
	
	
	
	/*
	 * Checks that the DLTIntegrationCost_cmAdjacentFeature for META-INF/de-test-text.txt is 0.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_cmAdjacentFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_cmAdjacent_Feature.xml", testResourcesFolder+"DLTIntegrationCost_cmAdjacent_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(0.0, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_cmvAdjacentFeature for META-INF/de-test-text.txt is 0.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_cmvAdjacentFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_cmvAdjacent_Feature.xml", testResourcesFolder+"DLTIntegrationCost_cmvAdjacent_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(0.0, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_cvAdjacentFeature for META-INF/de-test-text.txt is 0.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_cvAdjacentFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_cvAdjacent_Feature.xml", testResourcesFolder+"DLTIntegrationCost_cvAdjacent_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(0.0, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_cvMaxFeature for META-INF/de-test-text.txt is 3.3125, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_cvMaxFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_cvMax_Feature.xml", testResourcesFolder+"DLTIntegrationCost_cvMax_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(3.3125, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_cvTotalFeature for META-INF/de-test-text.txt is 4.625, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_cvTotalFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_cvTotal_Feature.xml", testResourcesFolder+"DLTIntegrationCost_cvTotal_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(4.625, annot.getValue(), 0.00);
			}
		}
	}
	

	
	/*
	 * Checks that the DLTIntegrationCost_mMaxFeature for META-INF/de-test-text.txt is 2.21875, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_mMaxFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_mMax_Feature.xml", testResourcesFolder+"DLTIntegrationCost_mMax_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(2.21875, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_mTotalFeature for META-INF/de-test-text.txt is 2.96875, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_mTotalFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_mTotal_Feature.xml", testResourcesFolder+"DLTIntegrationCost_mTotal_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(2.96875, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_oAdjacentFeature for META-INF/de-test-text.txt is 0.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_oAdjacentFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_oAdjacent_Feature.xml", testResourcesFolder+"DLTIntegrationCost_oAdjacent_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(0.0, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_oTotalFeature for META-INF/de-test-text.txt is 2.96875, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_oTotalFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_oTotal_Feature.xml", testResourcesFolder+"DLTIntegrationCost_oTotal_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(2.96875, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_mAdjacentFeature for META-INF/de-test-text.txt is 0.0, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_mAdjacentFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_vAdjacent_Feature.xml", testResourcesFolder+"DLTIntegrationCost_vAdjacent_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(0.0, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_vMaxFeature for META-INF/de-test-text.txt is 3.4375, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_vMaxFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_vMax_Feature.xml", testResourcesFolder+"DLTIntegrationCost_vMax_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(3.4375, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_vTotalFeature for META-INF/de-test-text.txt is 3.4375, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_vTotalFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_vTotal_Feature.xml", testResourcesFolder+"DLTIntegrationCost_vTotal_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(3.4375, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_cmvMaxFeature for META-INF/de-test-text.txt is 3.3125, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_cmvMaxFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_cmvMax_Feature.xml", testResourcesFolder+"DLTIntegrationCost_cmvMax_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(3.3125, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_cmvTotalFeature for META-INF/de-test-text.txt is 4.625, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_cmvTotalFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_cmvTotal_Feature.xml", testResourcesFolder+"DLTIntegrationCost_cmvTotal_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(4.625, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_cTotalFeature for META-INF/de-test-text.txt is 2.84375, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_cTotalFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_cTotal_Feature.xml", testResourcesFolder+"DLTIntegrationCost_cTotal_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(2.84375, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_cMaxFeature for META-INF/de-test-text.txt is 2.09375, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_cMaxFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_cMax_Feature.xml", testResourcesFolder+"DLTIntegrationCost_cMax_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(2.09375, annot.getValue(), 0.00);
			}
		}
	}
	
	/*
	 * Checks that the DLTIntegrationCost_cmMaxFeature for META-INF/de-test-text.txt is 2.09375, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_cmMaxFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_cmMax_Feature.xml", testResourcesFolder+"DLTIntegrationCost_cmMax_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(2.09375, annot.getValue(), 0.00);
			}
		}
	}
	
	
	/*
	 * Checks that the DLTIntegrationCost_cmTotalFeature for META-INF/de-test-text.txt is 2.84375, with the precision of 0.0000001.
	 */
	
	@Test
	public void DLTIntegrationCost_cmTotalFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/DLTIntegrationCost_cmTotal_Feature.xml", testResourcesFolder+"DLTIntegrationCost_cmTotal_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aedMorph, aedPOS, aedDepParse, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 2506){
				assertEquals(2.84375, annot.getValue(), 0.00);
			}
		}
	}
	
}
