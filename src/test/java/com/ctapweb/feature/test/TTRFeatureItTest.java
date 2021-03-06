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

public class TTRFeatureItTest {
	JCas jCas;
	XMLParser pars;
	AnalysisEngineDescription aedSent, aedToken, aedTokenType, aedNToken, aedNTokenType;
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
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/NTokenTypeType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports ("src/main/resources/descriptor/type_system/feature_type/TypeTokenRatioType.xml", testResourcesFolder+"TypeTokenRatioTypeForUIMAFitTest.xml", locationsList);
		String sdSentenceLengthTypeDescr = new String(Files.readAllBytes(Paths.get(testResourcesFolder+"TypeTokenRatioTypeForUIMAFitTest.xml")));
		
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
	
		File fTokenType = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/TokenTypeAnnotator.xml", testResourcesFolder+"TokenTypeAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceTokenType = new XMLInputSource(fTokenType);
		aedTokenType = pars.parseAnalysisEngineDescription(xmlInputSourceTokenType);
		
		File fNToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddaeID ("src/main/resources/descriptor/featureAE/NTokenFeature.xml", testResourcesFolder+"NTokenFeatureForUIMAFitTest.xml", "IT", "123");
		XMLInputSource xmlInputSourceNToken = new XMLInputSource(fNToken);
		aedNToken = pars.parseAnalysisEngineDescription(xmlInputSourceNToken);
		
		File fNTokenType = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddaeID ("src/main/resources/descriptor/featureAE/NTokenTypeFeature.xml", testResourcesFolder+"NTokenTypeFeatureForUIMAFitTest.xml", "IT", "123");
		XMLInputSource xmlInputSourceNTokenType = new XMLInputSource(fNTokenType);
		aedNTokenType = pars.parseAnalysisEngineDescription(xmlInputSourceNTokenType);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "3456"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		locationsListForTest.add("../../../../src/main/resources/descriptor/type_system/feature_type/TypeTokenRatioType.xml");
	}
	
	
	/*
	 * Checks that the TTR for META-INF/cani.txt is 0.6867924528301886, with the precision of 0.0000001.
	 */
	
	@Test
	public void TTRFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/TypeTokenRatio_TTR_Feature.xml", testResourcesFolder+"TypeTokenRatio_TTR_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedTokenType, aedNToken, aedNTokenType, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 3456){
				assertEquals(0.6867924528301886, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the TTR Uber for META-INF/cani.txt is 82.86257008649488, with the precision of 0.0000001.
	 */
	
	@Test
	public void TTRUberFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/TypeTokenRatio_Uber_Feature.xml", testResourcesFolder+"TypeTokenRatio_Uber_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedTokenType, aedNToken, aedNTokenType, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 3456){
				assertEquals(82.86257008649488, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the RTTR for META-INF/cani.txt is 11.180171126377912, with the precision of 0.0000001.
	 */
	
	@Test
	public void RTTRFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/TypeTokenRatio_RTTR_Feature.xml", testResourcesFolder+"TypeTokenRatio_RTTR_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedTokenType, aedNToken, aedNTokenType, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 3456){
				assertEquals(11.180171126377912, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Log TTR for META-INF/cani.txt is 0.9326628437886744, with the precision of 0.0000001.
	 */
	
	@Test
	public void LogTTRFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/TypeTokenRatio_LogTTR_Feature.xml", testResourcesFolder+"TypeTokenRatio_LogTTR_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedTokenType, aedNToken, aedNTokenType, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 3456){
				assertEquals(0.9326628437886744, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the CTTR for META-INF/cani.txt is 7.905574818287863, with the precision of 0.0000001.
	 */
	
	@Test
	public void CTTRFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/TypeTokenRatio_CTTR_Feature.xml", testResourcesFolder+"TypeTokenRatio_CTTR_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedTokenType, aedNToken, aedNTokenType, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 3456){
				assertEquals(7.905574818287863, annot.getValue(), 0.0000001);
			}
		}
	}
	
}
