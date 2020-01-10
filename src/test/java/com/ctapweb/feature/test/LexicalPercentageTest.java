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

public class LexicalPercentageTest {
	
	JCas jCas;
	XMLParser pars;
	AnalysisEngineDescription aedSent, aedToken, aedLemma;
	HashMap <String, ArrayList <String>> paramsHashMap;
	ArrayList<String> locationsListForTest;
	
	@Before
	public void setUp() throws Exception {
		
		pars = UIMAFramework.getXMLParser();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		
		ArrayList<String> locationsList = new ArrayList<String>();
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/ComplexityFeatureBaseType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/LemmaType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports ("src/main/resources/descriptor/type_system/feature_type/LexicalPercentageType.xml", "./META-INF/org.apache.uima.fit/LexicalPercentageTypeForUIMAFitTest.xml", locationsList);
		String lexPercTypeDescr = new String(Files.readAllBytes(Paths.get("./META-INF/org.apache.uima.fit/LexicalPercentageTypeForUIMAFitTest.xml")));
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(lexPercTypeDescr.getBytes("UTF-8")));
		
		tsd.buildFromXMLElement(doc.getDocumentElement(), pars);
	    jCas = CasCreationUtils.createCas(tsd, null, null).getJCas();
		
	    String contents = new String(Files.readAllBytes(Paths.get("./META-INF/cani.txt")));
		jCas.setDocumentText(contents);
		
		File fSent = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/SentenceAnnotator.xml", "./META-INF/org.apache.uima.fit/SentenceAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceSent = new XMLInputSource(fSent);
		aedSent = pars.parseAnalysisEngineDescription(xmlInputSourceSent);
	
		File fToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/TokenAnnotator.xml", "./META-INF/org.apache.uima.fit/TokenAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceToken = new XMLInputSource(fToken);
		aedToken = pars.parseAnalysisEngineDescription(xmlInputSourceToken);
	
		File fLemma = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/LemmaAnnotator.xml", "./META-INF/org.apache.uima.fit/LemmaAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceLemma = new XMLInputSource(fLemma);
		aedLemma = pars.parseAnalysisEngineDescription(xmlInputSourceLemma);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "9020"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		locationsListForTest.add("../../src/main/resources/descriptor/type_system/linguistic_type/LemmaType.xml");
	}
	
	
	/*
	 * Checks that the percentage of lemmas that are listed in the De mauro dictionary for META-INF/cani.txt is 76.13636363636364, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalPercentageDeMauroAllLemmasFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalPercentage_All_Lemmas_Feature.xml", "./META-INF/org.apache.uima.fit/LexicalPercentage_All_Lemmas_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 9020){
				assertEquals(76.13636363636364, annot.getValue(), 0.0000001); // 76.13636363636364
			}
		}
	}
	
	/*
	 * Checks that the percentage of unique lemmas that are listed in the De mauro dictionary for META-INF/cani.txt is 74.67532467532467, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalPercentageDeMauroUniqueLemmasFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalPercentage_Unique_Lemmas_Feature.xml", "./META-INF/org.apache.uima.fit/LexicalPercentage_Unique_Lemmas_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedLemma, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 9020){
				assertEquals(74.67532467532467, annot.getValue(), 0.0000001); // 74.67532467532467
			}
		}
	}
	
	
}
