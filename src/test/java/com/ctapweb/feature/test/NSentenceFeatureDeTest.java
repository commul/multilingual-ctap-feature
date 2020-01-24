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

public class NSentenceFeatureDeTest {
	JCas jCas;
	String testResourcesFolder = "src/test/resources/org.apache.uima.fit/";
	
	@Before
	public void setUp() throws Exception {
		XMLParser pars = UIMAFramework.getXMLParser();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
				
		ArrayList<String> locationsList = new ArrayList<String>();
		locationsList.add("../../../main/resources/descriptor/type_system/feature_type/ComplexityFeatureBaseType.xml");
		locationsList.add("../../../main/resources/descriptor/type_system/linguistic_type/SentenceType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports ("src/main/resources/descriptor/type_system/feature_type/NSentenceType.xml", testResourcesFolder+"NSentenceTypeForUIMAFitTest.xml", locationsList);
		String sdSentenceLengthTypeDescr = new String(Files.readAllBytes(Paths.get(testResourcesFolder+"NSentenceTypeForUIMAFitTest.xml")));
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(sdSentenceLengthTypeDescr.getBytes("UTF-8")));
		
		tsd.buildFromXMLElement(doc.getDocumentElement(), pars);
	    jCas = CasCreationUtils.createCas(tsd, null, null).getJCas();
		
	    String contents = new String(Files.readAllBytes(Paths.get("src/test/resources/de-test-text.txt")));
		jCas.setDocumentText(contents);
		
		File fSent = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/SentenceAnnotator.xml", testResourcesFolder+"SentenceAnnotatorForUIMAFitTest.xml", "DE");
		XMLInputSource xmlInputSourceSent = new XMLInputSource(fSent);
		AnalysisEngineDescription aedSent = pars.parseAnalysisEngineDescription(xmlInputSourceSent);
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguageAddaeID ("src/main/resources/descriptor/featureAE/NSentenceFeature.xml", testResourcesFolder+"NSentenceFeatureForUIMAFitTest.xml", "DE", "22231");
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then SyllableAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aed);
	}
	
	
	/*
	 * Checks that the number of sentences in META-INF/de-test-text.txt is 19.0, with the precision of 0.001.
	 */
	
	@Test
	public void NSentenceFeatureTest() throws Exception {

		for(ComplexityFeatureBase annot : JCasUtil.select(jCas, ComplexityFeatureBase.class)){
			if(annot.getId() == 22231){
				assertEquals(19.0, annot.getValue(), 0.0000001);
			}
		}
	}
	
}
