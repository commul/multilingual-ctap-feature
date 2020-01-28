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
import com.ctapweb.feature.type.Lemma;

import com.ctapweb.feature.test.util.DescriptorModifier;
import com.ctapweb.feature.type.ComplexityFeatureBase;

public class LexicalSophisticationFeatureItTest {
	
	JCas jCas;
	XMLParser pars;
	AnalysisEngineDescription aedSent, aedToken, aedPOS, aedLemma;
	HashMap <String, ArrayList <String>> paramsHashMap;
	ArrayList<String> locationsListForTest;
	String testResourcesFolder = "src/test/resources/org.apache.uima.fit/";
	
	@Before
	public void setUp() throws Exception {
		
		pars = UIMAFramework.getXMLParser();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		
		ArrayList<String> locationsList = new ArrayList<String>();
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/ComplexityFeatureBaseType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/POSType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/LemmaType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports ("src/main/resources/descriptor/type_system/feature_type/LexicalSophisticationType.xml", testResourcesFolder+"LexicalSophisticationTypeForUIMAFitTest.xml", locationsList);
		String sdSentenceLengthTypeDescr = new String(Files.readAllBytes(Paths.get(testResourcesFolder+"LexicalSophisticationTypeForUIMAFitTest.xml")));
		
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
	
		File fPOS = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/POSAnnotator.xml", testResourcesFolder+"POSAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourcePOS = new XMLInputSource(fPOS);
		aedPOS = pars.parseAnalysisEngineDescription(xmlInputSourcePOS);
		
		File fLemma = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/LemmaAnnotator.xml", testResourcesFolder+"LemmaAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceLemma = new XMLInputSource(fLemma);
		aedLemma = pars.parseAnalysisEngineDescription(xmlInputSourceLemma);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "123"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		locationsListForTest.add("../../../../src/main/resources/descriptor/type_system/linguistic_type/POSType.xml");
	}
	
	
	//---------------Google 2012  Familiarity----------------
	/*
	 * Checks that the Lexical Sophistication Feature: Google Books Word Familiarity Per Million Words (AW Token) for META-INF/cani.txt is 3.759754356295138E8, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalSophisticationGoogle00FamiliarityAWTokenFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Familiarity_AW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Familiarity_AW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(3.759754356295138E8, annot.getValue(), 0.0000001); // 378921747.148007
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Google Books Word Familiarity Per Million Words (AW Type) for META-INF/cani.txt is 1.5535737171640077E8, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalSophisticationGoogle00FamiliarityAWTypeFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Familiarity_AW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Familiarity_AW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(1.5535737171640077E8, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Google Books Word Familiarity Per Million Words (FW Token) for META-INF/cani.txt is 7.374125502753642E8, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalSophisticationGoogle00FamiliarityFWTokenFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Familiarity_FW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Familiarity_FW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(7.374125502753642E8, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Google Books Word Familiarity Per Million Words (FW Type) for META-INF/cani.txt is 3.695195652914551E8, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalSophisticationGoogle00FamiliarityFWTypeFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Familiarity_FW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Familiarity_FW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(3.695195652914551E8, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Google Books Word Familiarity Per Million Words (LW Token) for META-INF/cani.txt is 3.898649267098228E7, with the precision of 0.0000001.
	 */	
	
	@Test
	public void LexicalSophisticationGoogle00FamiliarityLWTokenFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Familiarity_LW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Familiarity_LW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(3.898649267098228E7, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Google Books Word Familiarity Per Million Words (LW Type) for META-INF/cani.txt is 3.252525961405463E7, with the precision of 0.0000001.
	 */	
	
	@Test
	public void LexicalSophisticationGoogle00FamiliarityLWTypeFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Familiarity_LW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Familiarity_LW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(3.252525961405463E7, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	//---------------Google 2012  Informativeness----------------
	/*
	 * Checks that the Lexical Sophistication Feature: Google Books Word Informativeness Per Million Words (AW Token) for META-INF/cani.txt is 90.74215557056517, with the precision of 0.0000001.
	 */	
	
	@Test
	public void LexicalSophisticationGoogle00InformativenessAWTokenFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Informativeness_AW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Informativeness_AW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(90.74215557056517, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Google Books Word Informativeness Per Million Words (AW Type) for META-INF/cani.txt is 116.6286161875089, with the precision of 0.0000001.
	 */	
	
	@Test
	public void LexicalSophisticationGoogle00InformativenessAWTypeFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Informativeness_AW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Informativeness_AW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(116.6286161875089, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Google Books Word Informativeness Per Million Words (FW Token) for META-INF/cani.txt is 11.464919427389937, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalSophisticationGoogle00InformativenessFWTokenFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Informativeness_FW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Informativeness_FW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(11.464919427389937, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Google Books Word Informativeness Per Million Words (FW Type) for META-INF/cani.txt is 20.374627715807016, with the precision of 0.0000001.
	 */	
	
	@Test
	public void LexicalSophisticationGoogle00InformativenessFWTypeFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Informativeness_FW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Informativeness_FW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(20.374627715807016, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Google Books Word Informativeness Per Million Words (LW Token) for META-INF/cani.txt is 163.75211856494926, with the precision of 0.0000001.
	 */	
	
	@Test
	public void LexicalSophisticationGoogle00InformativenessLWTokenFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Informativeness_LW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Informativeness_LW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(163.75211856494926, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Google Books Word Informativeness Per Million Words (LW Type) for META-INF/cani.txt is 170.6587048878943, with the precision of 0.0000001.
	 */
	
	@Test
	public void LexicalSophisticationGoogle00InformativenessLWTypeFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Informativeness_LW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Informativeness_LW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(170.6587048878943, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	//---------------Google 2012  Log10WFInMillion----------------
		/*
		 * Checks that the Lexical Sophistication Feature: Google Books Word Log10 FW In Million Words (AW Token) for META-INF/cani.txt is 7.268666683824464, with the precision of 0.0000001.
		 */
	
		@Test
		public void LexicalSophisticationGoogle00Log10WFInMillionAWTokenFeatureTest() throws Exception {		
		
			File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Log10WFInMillion_AW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Log10WFInMillion_AW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
			XMLInputSource xmlInputSource = new XMLInputSource(f);
			AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
			
			//Run the analysis pipeline
			SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
		
			for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
				if(annot.getId() == 123){
					assertEquals(7.268666683824464, annot.getValue(), 0.0000001); 
				}
			}
		}
		
	
		/*
		 * Checks that the Lexical Sophistication Feature: Google Books Word Log10 FW In Million Words (AW Type) for META-INF/cani.txt is 6.7923314784143125, with the precision of 0.0000001.
		 */	
	
		@Test
		public void LexicalSophisticationGoogle00Log10WFInMillionAWTypeFeatureTest() throws Exception {		
		
			File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Log10WFInMillion_AW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Log10WFInMillion_AW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
			XMLInputSource xmlInputSource = new XMLInputSource(f);
			AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
			
			//Run the analysis pipeline
			SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
		
			for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
				if(annot.getId() == 123){
					assertEquals(6.7923314784143125, annot.getValue(), 0.0000001);
				}
			}
		}
		
	
		/*
		 * Checks that the Lexical Sophistication Feature: Google Books Word Log10 FW In Million Words (FW Token) for META-INF/cani.txt is 8.361300153457918, with the precision of 0.0000001.
		 */	
	
		@Test
		public void LexicalSophisticationGoogle00Log10WFInMillionFWTokenFeatureTest() throws Exception {		
		
			File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Log10WFInMillion_FW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Log10WFInMillion_FW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
			XMLInputSource xmlInputSource = new XMLInputSource(f);
			AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
			
			//Run the analysis pipeline
			SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
		
			for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
				if(annot.getId() == 123){
					assertEquals(8.361300153457918, annot.getValue(), 0.0000001);
				}
			}
		}
		
		
		/*
		 * Checks that the Lexical Sophistication Feature: Google Books Word Log10 FW In Million Words (FW Type) for META-INF/cani.txt is 7.925231615479594, with the precision of 0.0000001.
		 */	
	
		@Test
		public void LexicalSophisticationGoogle00Log10WFInMillionFWTypeFeatureTest() throws Exception {		
		
			File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Log10WFInMillion_FW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Log10WFInMillion_FW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
			XMLInputSource xmlInputSource = new XMLInputSource(f);
			AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
			
			//Run the analysis pipeline
			SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
		
			for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
				if(annot.getId() == 123){
					assertEquals(7.925231615479594, annot.getValue(), 0.0000001);
				}
			}
		}
		
		
		/*
		 * Checks that the Lexical Sophistication Feature: Google Books Word Log10 FW In Million Words (LW Token) for META-INF/cani.txt is 6.243126094902768, with the precision of 0.0000001.
		 */	
	
		@Test
		public void LexicalSophisticationGoogle00Log10WFInMillionLWTokenFeatureTest() throws Exception {		
		
			File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Log10WFInMillion_LW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Log10WFInMillion_LW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
			XMLInputSource xmlInputSource = new XMLInputSource(f);
			AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
			
			//Run the analysis pipeline
			SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
		
			for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
				if(annot.getId() == 123){
					assertEquals(6.243126094902768, annot.getValue(), 0.0000001);
				}
			}
		}
		
		
		/*
		 * Checks that the Lexical Sophistication Feature: Google Books Word Log10 FW In Million Words (LW Type) for META-INF/cani.txt is 6.135580532546088, with the precision of 0.0000001.
		 */	
	
		@Test
		public void LexicalSophisticationGoogle00Log10WFInMillionLWTypeFeatureTest() throws Exception {		
		
			File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00_Log10WFInMillion_LW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00_Log10WFInMillion_LW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
			XMLInputSource xmlInputSource = new XMLInputSource(f);
			AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
			
			//Run the analysis pipeline
			SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
		
			for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
				if(annot.getId() == 123){
					assertEquals(6.135580532546088, annot.getValue(), 0.0000001);
				}
			}
		}
		
	
		//---------------Google 2012  Log10WF----------------
				/*
				 * Checks that the Lexical Sophistication Feature: Google Books Word Log10 WF (AW Token) for META-INF/cani.txt is 7.223194286378245, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationGoogle00Log10WFAWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00Log10WF_AW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00Log10WF_AW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(7.223194286378245, annot.getValue(), 0.0000001); 
						}
					}
				}
				
	
				/*
				 * Checks that the Lexical Sophistication Feature: Google Books Word Log10 WF (AW Type) for META-INF/cani.txt is 6.746859080968096, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationGoogle00Log10WFAWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00Log10WF_AW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00Log10WF_AW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(6.746859080968096, annot.getValue(), 0.0000001);
						}
					}
				}
				
	
				/*
				 * Checks that the Lexical Sophistication Feature: Google Books Word Log10 WF (FW Token) for META-INF/cani.txt is 8.315827756011696, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationGoogle00Log10WFFWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00Log10WF_FW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00Log10WF_FW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(8.315827756011696, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: Google Books Word Log10 WF (FW Type) for META-INF/cani.txt is 7.879759218033367, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationGoogle00Log10WFFWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00Log10WF_FW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00Log10WF_FW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(7.879759218033367, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: Google Books Word Log10 WF (LW Token) for META-INF/cani.txt is 6.197653697456544, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationGoogle00Log10WFLWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00Log10WF_LW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00Log10WF_LW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(6.197653697456544, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: Google Books Word Log10 WF (LW Type) for META-INF/cani.txt is 6.090108135099866, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationGoogle00Log10WFLWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00Log10WF_LW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00Log10WF_LW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(6.090108135099866, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				//---------------Google 2012   WF----------------
				/*
				 * Checks that the Lexical Sophistication Feature: Google Books WF (AW Token) for META-INF/cani.txt is 3.596186041318704E8, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationGoogle00WFAWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00WF_AW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00WF_AW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(3.596186041318704E8, annot.getValue(), 0.0000001); 
						}
					}
				}
				
	
				/*
				 * Checks that the Lexical Sophistication Feature: Google Books WF (AW Type) for META-INF/cani.txt is 1.3311596693765186E8, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationGoogle00WFAWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00WF_AW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00WF_AW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(1.3311596693765186E8, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: Google Books WF (FW Token) for META-INF/cani.txt is 7.191037479697275E8, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationGoogle00WFFWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00WF_FW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00WF_FW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(7.191037479697275E8, annot.getValue(), 0.0000001); // 719103747.969728
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: Google Books Word Log10 WF (FW Type) for META-INF/cani.txt is 3.3556183569967175E8, with the precision of 0.0000001.
				 */
	
				@Test
				public void LexicalSophisticationGoogle00WFFWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00WF_FW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00WF_FW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(3.3556183569967175E8, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: Google Books Word WF (LW Token) for META-INF/cani.txt is 2.4459905413991172E7, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationGoogle00WFLWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00WF_LW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00WF_LW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(2.4459905413991172E7, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: Google Books WF (LW Type) for META-INF/cani.txt is 1.701667565625899E7, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationGoogle00WFLWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Google00WF_LW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_Google00WF_LW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(1.701667565625899E7, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				//---------------SUBTLEX  Familiarity----------------
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Word Familiarity (AW Token) for META-INF/cani.txt is 904383.5534888661, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationSUBTLEXFamiliarityAWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Familiarity_AW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Familiarity_AW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(904383.5534888661, annot.getValue(), 0.0000001); // 911597.132527755
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Word Familiarity (AW Type) for META-INF/cani.txt is 413269.0516186454, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationSUBTLEXFamiliarityAWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Familiarity_AW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Familiarity_AW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(413269.0516186454, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Word Familiarity (FW Token) for META-INF/cani.txt is 1635406.434504271, with the precision of 0.0000001.
				 */
	
				@Test
				public void LexicalSophisticationSUBTLEXFamiliarityFWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Familiarity_FW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Familiarity_FW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(1635406.434504271, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Word Familiarity (FW Type) for META-INF/cani.txt is 861610.9125104927, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationSUBTLEXFamiliarityFWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Familiarity_FW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Familiarity_FW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(861610.9125104927, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Word Familiarity (LW Token) for META-INF/cani.txt is 211939.4259536281, with the precision of 0.0000001.
				 */
	
				@Test
				public void LexicalSophisticationSUBTLEXFamiliarityLWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Familiarity_LW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Familiarity_LW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(211939.4259536281, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Word Familiarity (LW Type) for META-INF/cani.txt is 152365.3041871079, with the precision of 0.0000001.
				 */
	
				@Test
				public void LexicalSophisticationSUBTLEXFamiliarityLWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Familiarity_LW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Familiarity_LW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(152365.3041871079, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				//---------------SUBTLEX  Informativeness----------------
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Word Informativeness (AW Token) for META-INF/cani.txt is 48.551510646461, with the precision of 0.0000001.
				 */
	
				@Test
				public void LexicalSophisticationSUBTLEXInformativenessAWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Informativeness_AW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Informativeness_AW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(48.551510646461, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Word Informativeness (AW Type) for META-INF/cani.txt is 61.9652949532034, with the precision of 0.0000001.
				 */
	
				@Test
				public void LexicalSophisticationSUBTLEXInformativenessAWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Informativeness_AW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Informativeness_AW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(61.9652949532034, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Word Informativeness (FW Token) for META-INF/cani.txt is 6.3233286564121025, with the precision of 0.0000001.
				 */
	
				@Test
				public void LexicalSophisticationSUBTLEXInformativenessFWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Informativeness_FW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Informativeness_FW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(6.3233286564121025, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Word Informativeness(FW Type) for META-INF/cani.txt is 10.283908164326132, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationSUBTLEXInformativenessFWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Informativeness_FW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Informativeness_FW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(10.283908164326132, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Word Informativeness (LW Token) for META-INF/cani.txt is 88.2188767547244, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationSUBTLEXInformativenessLWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Informativeness_LW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Informativeness_LW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(88.2188767547244, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Word Informativeness (LW Type) for META-INF/cani.txt is 91.57929869184902, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationSUBTLEXInformativenessLWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Informativeness_LW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Informativeness_LW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(91.57929869184902, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				//---------------SUBTLEX  Log10WF In Million----------------
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Log10 WF In Million (AW Token) for META-INF/cani.txt is 4.892905306001507, with the precision of 0.0000001.
				 */
	
				@Test
				public void LexicalSophisticationSUBTLEXLog10WFInMillionAWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Log10WFInMillion_AW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Log10WFInMillion_AW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(4.892905306001507, annot.getValue(), 0.0000001); 
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Log10 WF In Million (AW Type) for META-INF/cani.txt is 4.420930613467594, with the precision of 0.0000001.
				 */
	
				@Test
				public void LexicalSophisticationSUBTLEXLog10WFInMillionAWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Log10WFInMillion_AW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Log10WFInMillion_AW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(4.420930613467594, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Log10 WF In Million (FW Token) for META-INF/cani.txt is 5.810062917577086, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationSUBTLEXLog10WFInMillionFWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Log10WFInMillion_FW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Log10WFInMillion_FW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(5.810062917577086, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Log10 WF In Million (FW Type) for META-INF/cani.txt is 5.385605125683745, with the precision of 0.0000001.
				 */
	
				@Test
				public void LexicalSophisticationSUBTLEXLog10WFInMillionFWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Log10WFInMillion_FW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Log10WFInMillion_FW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(5.385605125683745, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Log10 WF In Million (LW Token) for META-INF/cani.txt is 4.015265116495482, with the precision of 0.0000001.
				 */
	
				@Test
				public void LexicalSophisticationSUBTLEXLog10WFInMillionLWTokenFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Log10WFInMillion_LW_Token_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Log10WFInMillion_LW_Token_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(4.015265116495482, annot.getValue(), 0.0000001);
						}
					}
				}
				
				
				/*
				 * Checks that the Lexical Sophistication Feature: SUBTLEX Log10 WF In Million (LW Type) for META-INF/cani.txt is 3.85076988651852, with the precision of 0.0000001.
				 */	
	
				@Test
				public void LexicalSophisticationSUBTLEXLog10WFInMillionLWTypeFeatureTest() throws Exception {		
				
					File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_SUBTLEX_Log10WFInMillion_LW_Type_Feature.xml", testResourcesFolder+"LexicalSophistication_SUBTLEX_Log10WFInMillion_LW_Type_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
					XMLInputSource xmlInputSource = new XMLInputSource(f);
					AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
					
					//Run the analysis pipeline
					SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
				
					for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
						if(annot.getId() == 123){
							assertEquals(3.85076988651852, annot.getValue(), 0.0000001);
						}
					}
				}
				
	
	/*
	 * Checks that the Lexical Sophistication Feature: Concreteness All Lemmas for META-INF/cani.txt is 5.958125000000001, with the precision of 0.0000001.
	 */	

	@Test
	public void LexicalSophisticationConcretenessAllLemmasFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Concreteness_All_Lemmas_Feature.xml", testResourcesFolder+"LexicalSophistication_Concreteness_All_Lemmas_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedLemma, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(5.958125000000001, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Concreteness Unique Lemmas for META-INF/cani.txt is 5.531818181818182, with the precision of 0.0000001.
	 */	

	@Test
	public void LexicalSophisticationConcretenessUniqueLemmasFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Concreteness_Unique_Lemmas_Feature.xml", testResourcesFolder+"LexicalSophistication_Concreteness_Unique_Lemmas_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedLemma, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(5.531818181818182, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Imageability All Lemmas for META-INF/cani.txt is 5.741875, with the precision of 0.0000001.
	 */	

	@Test
	public void LexicalSophisticationImageabilityAllLemmasFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Imageability_All_Lemmas_Feature.xml", testResourcesFolder+"LexicalSophistication_Imageability_All_Lemmas_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedLemma, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(5.741875, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Imageability Unique Lemmas for META-INF/cani.txt is 5.377272727272727, with the precision of 0.0000001.
	 */	

	@Test
	public void LexicalSophisticationImageabilityUniqueLemmasFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_Imageability_Unique_Lemmas_Feature.xml", testResourcesFolder+"LexicalSophistication_Imageability_Unique_Lemmas_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedLemma, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(5.377272727272727, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Age of Acquisition All Lemmas for META-INF/cani.txt is 2.378125, with the precision of 0.0000001.
	 */	

	@Test
	public void LexicalSophisticationAoAAllLemmasFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_AoA_All_Lemmas_Feature.xml", testResourcesFolder+"LexicalSophistication_AoA_All_Lemmas_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedLemma, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(2.378125, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the Lexical Sophistication Feature: Age of Acquisition Unique Lemmas for META-INF/cani.txt is 2.7045454545454546, with the precision of 0.0000001.
	 */	
	
	@Test
	public void LexicalSophisticationAoAUniqueLemmasFeatureTest() throws Exception {		
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/LexicalSophistication_AoA_Unique_Lemmas_Feature.xml", testResourcesFolder+"LexicalSophistication_AoA_Unique_Lemmas_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedLemma, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 123){
				assertEquals(2.7045454545454546, annot.getValue(), 0.0000001);
			}
		}
	}
	
}
