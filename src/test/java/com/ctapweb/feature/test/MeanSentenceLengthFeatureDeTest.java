package com.ctapweb.feature.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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

public class MeanSentenceLengthFeatureDeTest {

	JCas jCas;
	XMLParser pars;
	AnalysisEngineDescription aedSent, aedNSentence,  aedToken, aedNToken, aedSyllable, aedNSyllable, aedLetter, aedNLetter;
	
	@Before
	public void setUp() throws Exception {
		pars = UIMAFramework.getXMLParser();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
				
		ArrayList<String> locationsList = new ArrayList<String>();
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/ComplexityFeatureBaseType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/NSentenceType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/NTokenType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/NSyllableType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/feature_type/NLetterType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports ("src/main/resources/descriptor/type_system/feature_type/MeanSentenceLengthType.xml", "./META-INF/org.apache.uima.fit/MeanSentenceLengthTypeForUIMAFitTest.xml", locationsList);
		String sdSentenceLengthTypeDescr = new String(Files.readAllBytes(Paths.get("./META-INF/org.apache.uima.fit/MeanSentenceLengthTypeForUIMAFitTest.xml")));
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(sdSentenceLengthTypeDescr.getBytes("UTF-8")));
		
		tsd.buildFromXMLElement(doc.getDocumentElement(), pars);
	    jCas = CasCreationUtils.createCas(tsd, null, null).getJCas();
		
	    String contents = new String(Files.readAllBytes(Paths.get("./META-INF/de-test-text.txt")));
		jCas.setDocumentText(contents);
		
		File fSent = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/SentenceAnnotator.xml", "./META-INF/org.apache.uima.fit/SentenceAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceSent = new XMLInputSource(fSent);
		aedSent = pars.parseAnalysisEngineDescription(xmlInputSourceSent);
		
		File fNSentence = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddaeID ("src/main/resources/descriptor/featureAE/NSentenceFeature.xml", "./META-INF/org.apache.uima.fit/NSentenceFeatureForUIMAFitTest.xml", "DE", "22231");
		XMLInputSource xmlInputSourceNSentence = new XMLInputSource(fNSentence);
		aedNSentence = pars.parseAnalysisEngineDescription(xmlInputSourceNSentence);
		
		
		File fToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/TokenAnnotator.xml", "./META-INF/org.apache.uima.fit/TokenAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceToken = new XMLInputSource(fToken);
		aedToken = pars.parseAnalysisEngineDescription(xmlInputSourceToken);
		
		File fNToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddaeID ("src/main/resources/descriptor/featureAE/NTokenFeature.xml", "./META-INF/org.apache.uima.fit/NTokenFeatureForUIMAFitTest.xml", "DE", "123");
		XMLInputSource xmlInputSourceNToken = new XMLInputSource(fNToken);
		aedNToken = pars.parseAnalysisEngineDescription(xmlInputSourceNToken);
		
		File fSyllable = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/SyllableAnnotator.xml", "./META-INF/org.apache.uima.fit/SyllableAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceSyllable = new XMLInputSource(fSyllable);
		aedSyllable = pars.parseAnalysisEngineDescription(xmlInputSourceSyllable);
		
		File fNSyllable = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddaeID ("src/main/resources/descriptor/featureAE/NSyllableFeature.xml", "./META-INF/org.apache.uima.fit/NSyllableFeatureForUIMAFitTest.xml", "DE", "777");
		XMLInputSource xmlInputSourceNSyllable = new XMLInputSource(fNSyllable);
		aedNSyllable = pars.parseAnalysisEngineDescription(xmlInputSourceNSyllable);
		
		File fLetter = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/LetterAnnotator.xml", "./META-INF/org.apache.uima.fit/LetterAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceLetter = new XMLInputSource(fLetter);
		aedLetter = pars.parseAnalysisEngineDescription(xmlInputSourceLetter);
		
		File fNLetter = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddaeID ("src/main/resources/descriptor/featureAE/NLetterFeature.xml", "./META-INF/org.apache.uima.fit/NLetterFeatureForUIMAFitTest.xml", "DE", "777");
		XMLInputSource xmlInputSourceNLetter = new XMLInputSource(fNLetter);
		aedNLetter = pars.parseAnalysisEngineDescription(xmlInputSourceNLetter);
		
	}
	
	
	/*
	 * Checks that the mean sentence length in tokens in META-INF/de-test-text.txt is 16.526315789473685, with the precision of 0.0000001.
	 */
	
	@Test
	public void MeanSentenceLengthInTokenFeatureTest() throws Exception {
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddUnitAddaeID ("src/main/resources/descriptor/featureAE/MeanSentenceLengthInTokenFeature.xml", "./META-INF/org.apache.uima.fit/MeanSentenceLengthInTokenFeatureForUIMAFitTest.xml", "IT", "unit", "token", "5454");
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedNSentence, aedNSentence, aedToken, aedNToken, aedSyllable, aedNSyllable, aedLetter, aedNLetter, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 5454){
				assertEquals(16.526315789473685, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the mean sentence length in letters in META-INF/de-test-text.txt is 90.73684210526316, with the precision of 0.0000001.
	 */
	
	@Test
	public void MeanSentenceLengthInLetterFeatureTest() throws Exception {
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddUnitAddaeID ("src/main/resources/descriptor/featureAE/MeanSentenceLengthInLetterFeature.xml", "./META-INF/org.apache.uima.fit/MeanSentenceLengthInTokenFeatureForUIMAFitTest.xml", "IT", "unit", "letter", "5454");
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedNSentence, aedNSentence, aedToken, aedNToken, aedSyllable, aedNSyllable, aedLetter, aedNLetter, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 5454){
				assertEquals(90.73684210526316, annot.getValue(), 0.0000001);
			}
		}
	}
	
	
	/*
	 * Checks that the mean sentence length in syllables in META-INF/de-test-text.txt is 29.157894736842106, with the precision of 0.0000001.
	 */
	
	@Test
	public void MeanSentenceLengthInSyllableFeatureTest() throws Exception {
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddUnitAddaeID ("src/main/resources/descriptor/featureAE/MeanSentenceLengthInSyllableFeature.xml", "./META-INF/org.apache.uima.fit/MeanSentenceLengthInSyllableFeatureForUIMAFitTest.xml", "IT", "unit", "syllable", "5454");
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedNSentence, aedNSentence, aedToken, aedNToken, aedSyllable, aedNSyllable, aedLetter, aedNLetter, aed);
	
		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 5454){
				assertEquals(29.157894736842106, annot.getValue(), 0.0000001);
			}
		}
	}
	
}