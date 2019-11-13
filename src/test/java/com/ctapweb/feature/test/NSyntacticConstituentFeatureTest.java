package com.ctapweb.feature.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XMLParser;

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

public class NSyntacticConstituentFeatureTest {
	JCas jCas;
	XMLParser pars;
	AnalysisEngineDescription aedSent, aedToken, aedPOS, aedParseTree, aed;
	HashMap <String, ArrayList <String>> paramsHashMap;
	ArrayList<String> locationsListForTest;
	
	@Before
	public void setUp() throws Exception {
		pars = UIMAFramework.getXMLParser();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		
		ArrayList<String> locationsList = new ArrayList<String>();
		
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/ComplexityFeatureBaseType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/ParseTreeType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/POSType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports ("src/main/resources/descriptor/type_system/feature_type/NSyntacticConstituentType.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituentTypeForUIMAFitTest.xml", locationsList);
		String sdSentenceLengthTypeDescr = new String(Files.readAllBytes(Paths.get("./META-INF/org.apache.uima.fit/NSyntacticConstituentTypeForUIMAFitTest.xml")));
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(sdSentenceLengthTypeDescr.getBytes("UTF-8")));
		
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
		
		File fPOS = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/POSAnnotator.xml", "./META-INF/org.apache.uima.fit/POSAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourcePOS = new XMLInputSource(fPOS);
		aedPOS = pars.parseAnalysisEngineDescription(xmlInputSourcePOS);
		
		File fParseTree = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/ParseTreeAnnotator.xml", "./META-INF/org.apache.uima.fit/ParseTreeAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceParseTree = new XMLInputSource(fParseTree);
		aedParseTree = pars.parseAnalysisEngineDescription(xmlInputSourceParseTree);
		
		paramsHashMap = new HashMap <String, ArrayList <String>> ();		
		ArrayList dynamicStringArray = new ArrayList(2);
		String[] names = {"integer", "44442"};
		dynamicStringArray.addAll(Arrays.asList(names));
		paramsHashMap.put("aeID", dynamicStringArray);
		
		ArrayList dynamicStringArray2 = new ArrayList(2);
		String[] names2 = {"string", "IT"};
		dynamicStringArray2.addAll(Arrays.asList(names2));
		paramsHashMap.put("LanguageCode", dynamicStringArray2);
		
		locationsListForTest = new ArrayList <String> ();
		locationsListForTest.add("../../src/main/resources/descriptor/type_system/feature_type/NSyntacticConstituentType.xml");
	
	}
	
	
	/*
	 * Checks that the number of attributes in META-INF/cani.txt is 0.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentAttributiveFeatureTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_Attributive_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_Attributive_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of auxiliary verbs in META-INF/cani.txt is 15.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentAuxiliaryFeatureTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_Auxiliary_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_Auxiliary_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(15.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of passive auxiliaries in META-INF/cani.txt is 2.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentAuxiliaryPassiveTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_AuxiliaryPassive_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_AuxiliaryPassive_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(2.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of cases in META-INF/cani.txt is 29.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentCaseTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_Case_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_Case_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(29.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of clausal complements in META-INF/cani.txt is 3.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentClausalComplementTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_ClausalComplement_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_ClausalComplement_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(3.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of clausal passive subjects in META-INF/cani.txt is 0.0, with the precision of 0.0000001.
	 */
	/*
	@Test
	public void NSyntacticConstituentClausalPassiveSubjectTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_ClausalPassiveSubject_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_ClausalPassiveSubject_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of clausal subjects in META-INF/cani.txt is 0.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentClausalSubjectTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_ClausalSubject_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_ClausalSubject_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of coordinations in META-INF/cani.txt is 12.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentCoordinationTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_Coordination_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_Coordination_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(12.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of copulae in META-INF/cani.txt is 5.0, with the precision of 0.0000001.
	 */
	/*
	@Test
	public void NSyntacticConstituentCopulaTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_Copula_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_Copula_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(5.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the number of dependent clauses in META-INF/cani.txt is 23.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentDCTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_DC_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_DC_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(23.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of direct objects in META-INF/cani.txt is 20.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentDirectObjectTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_DirectObject_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_DirectObject_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(20.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of discourse elements in META-INF/cani.txt is 0.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentDiscourseElementTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_DiscourseElement_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_DiscourseElement_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of expletive elements in META-INF/cani.txt is 1.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentExpletiveTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_Expletive_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_Expletive_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(1.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of indirect objects in META-INF/cani.txt is 1.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentIndirectObjectTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_IndirectObject_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_IndirectObject_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(1.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of emarkers in META-INF/cani.txt is 9.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentMarkerTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_Marker_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_Marker_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(9.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of multi word expressions in META-INF/cani.txt is 0.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentMultiWordExpressionTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_MultiWordExpression_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_MultiWordExpression_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of negation modifiers in META-INF/cani.txt is 2.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentNegationModifierTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_NegationModifier_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_NegationModifier_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(2.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of nominal modifiers in META-INF/cani.txt is 33.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentNominalModifierTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_NominalModifier_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_NominalModifier_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(33.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of nominal subjects in META-INF/cani.txt is 19.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentNominalSubjectTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_NominalSubject_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_NominalSubject_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(19.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of passive nominal subjects in META-INF/cani.txt is 1.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentNominalSubjectPassiveTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_NominalSubjectPassive_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_NominalSubjectPassive_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(1.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of noun compounds in META-INF/cani.txt is 3.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentNounCompoundModifierTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_NounCompoundModifier_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_NounCompoundModifier_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(3.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of noun phrases as adverbial modifiers in META-INF/cani.txt is 0.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentNounPhraseAsAdverbialModifierTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_NounPhraseAsAdverbialModifier_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_NounPhraseAsAdverbialModifier_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	
	/*
	 * Checks that the number of noun phrases in META-INF/cani.txt is 0.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentNumberTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_Number_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_Number_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of numeric modifiers in META-INF/cani.txt is 4.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentNumericModifierTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_NumericModifier_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_NumericModifier_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(4.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of open clausal complements in META-INF/cani.txt is 6.0, with the precision of 0.0000001.
	 */
	/*
	@Test
	public void NSyntacticConstituentOpenClausalComplementTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_OpenClausalCompliment_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_OpenClausalComplement_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(6.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of parataxis complements in META-INF/cani.txt is 0.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentParataxisTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_Parataxis_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_Parataxis_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of phrasal verb particles in META-INF/cani.txt is 0.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentPhrasalVerbParticleTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_PhrasalVerbParticle_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_PhrasalVerbParticle_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of possession modifiers in META-INF/cani.txt is 8.0, with the precision of 0.0000001.
	 */
	/*
	@Test
	public void NSyntacticConstituentPossessionModifierTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_PossessionModifier_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_PossessionModifier_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(8.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of preconjuncts in META-INF/cani.txt is 0.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentPreconjunctTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_Preconjunct_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_Preconjunct_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of predeterminers in META-INF/cani.txt is 1.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentPredeterminerTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_Predeterminer_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_Predeterminer_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(1.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of prepositional complements in META-INF/cani.txt is 0.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentPrepositionalComplementTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_PrepositionalComplement_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_PrepositionalComplement_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of proper noun modifiers in META-INF/cani.txt is 2.0, with the precision of 0.0000001.
	 */
	/*
	@Test
	public void NSyntacticConstituentProperNounModifierTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_ProperNounModifier_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_ProperNounModifier_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(2.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of quantifier phrase modifiers in META-INF/cani.txt is 0.0, with the precision of 0.0000001.
	 */	
	/*
	@Test
	public void NSyntacticConstituentQuantifierPhraseModifierTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_QuantifierPhraseModifier_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_QuantifierPhraseModifier_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of quantifier phrase modifiers in META-INF/cani.txt is 8.0, with the precision of 0.0000001.
	 */
	/*
	@Test
	public void NSyntacticConstituentRelativeClauseModifierTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_RelativeClauseModifier_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_RelativeClauseModifier_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(8.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of sentences in META-INF/cani.txt is 14.0, with the precision of 0.0000001.
	 * Tint splits some sentences into multiple sentences. In the text cani.txt, 2 sentences were split by colon ':' : the second and the 8th (that starts with "Scrive su Facebook:").
	 */
	/*
	@Test
	public void NSyntacticConstituentSentencesTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_S_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_S_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(14.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
	
	/*
	 * Checks that the number of temporal modifiers in META-INF/cani.txt is 0.0, with the precision of 0.001.
	 */
	/*
	@Test
	public void NSyntacticConstituentTemporalModifierTest() throws Exception {
	
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddParamsFromHashModifyImports ("src/main/resources/descriptor/featureAE/NSyntacticConstituent_TemporalModifier_Feature.xml", "./META-INF/org.apache.uima.fit/NSyntacticConstituent_TemporalModifier_FeatureForUIMAFitTest.xml", paramsHashMap, locationsListForTest);
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aedParseTree, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 44442){
				assertEquals(0.0, annot.getValue(), 0.0000001);
			}
		}
	}
	*/
}
