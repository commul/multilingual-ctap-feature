package com.ctapweb.annotator.test;

import com.ctapweb.feature.test.util.DescriptorModifier;
import com.ctapweb.feature.type.Lemma;
import com.ctapweb.feature.type.TokenType;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.AnnotationBase;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

public class TokenTypeAnnotatorItTest {
	JCas jCas;
	
	@Before
	public void setUp() throws Exception {
		XMLParser pars = UIMAFramework.getXMLParser();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();		
		
		ArrayList<String> locationsList = new ArrayList<String>();
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/TokenType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports("src/main/resources/descriptor/type_system/linguistic_type/TokenTypeType.xml", "src/test/resources/org.apache.uima.fit/TokenTypeTypeForUIMAFitTest.xml", locationsList);
		String lemmaTypeDescr = new String(Files.readAllBytes(Paths.get("src/test/resources/org.apache.uima.fit/TokenTypeTypeForUIMAFitTest.xml")));
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(lemmaTypeDescr.getBytes("UTF-8")));
		
		tsd.buildFromXMLElement(doc.getDocumentElement(), pars);
	    jCas = CasCreationUtils.createCas(tsd, null, null).getJCas();
		
	    String contents = new String(Files.readAllBytes(Paths.get("src/test/resources/cani.txt")));
		jCas.setDocumentText(contents);
		
		File fSent = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/SentenceAnnotator.xml", "src/test/resources/org.apache.uima.fit/SentenceAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceSent = new XMLInputSource(fSent);
		AnalysisEngineDescription aedSent = pars.parseAnalysisEngineDescription(xmlInputSourceSent);
		
		File fToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/TokenAnnotator.xml", "src/test/resources/org.apache.uima.fit/TokenAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceToken = new XMLInputSource(fToken);
		AnalysisEngineDescription aedToken = pars.parseAnalysisEngineDescription(xmlInputSourceToken);
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/TokenTypeAnnotator.xml", "src/test/resources/org.apache.uima.fit/TokenTypeAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	}
	
	
	/*
	 * Checks that the number of token types in the file META-INF/cani.txt is 182.
	 */
	
	@Test
	public void annotateLemmasItalianNumberLemmasTest() throws Exception {		
		int n = 0;
		
		for(AnnotationBase annot : JCasUtil.select(jCas, AnnotationBase.class)){
			if(annot.getType().toString().equals("com.ctapweb.feature.type.TokenType") ){
				n += 1;
			}
		}
		
		assertEquals(182, n); 
	}
	
}
