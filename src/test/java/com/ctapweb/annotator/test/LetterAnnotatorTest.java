package com.ctapweb.annotator.test;

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
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import com.ctapweb.feature.test.util.DescriptorModifier;
import com.ctapweb.feature.type.Letter;



/**
 * Tests the LetterAnnotator.
 * @author Nadezda Okinina
 */
public class LetterAnnotatorTest {
	JCas jCas;
	
	@Before
	public void setUp() throws Exception {
		XMLParser pars = UIMAFramework.getXMLParser();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
		ArrayList<String> locationsList = new ArrayList<String>();
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/TokenType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports ("src/main/resources/descriptor/type_system/linguistic_type/LetterType.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LetterTypeForUIMAFitTest.xml", locationsList);
		String letterTypeDescr = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LetterTypeForUIMAFitTest.xml")));
		
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(letterTypeDescr.getBytes("UTF-8")));
		
		tsd.buildFromXMLElement(doc.getDocumentElement(), pars);
	    jCas = CasCreationUtils.createCas(tsd, null, null).getJCas();
		
	    String contents = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+"/META-INF/cani.txt")));
		jCas.setDocumentText(contents);
		
		File fSent = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/SentenceAnnotator.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/SentenceAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceSent = new XMLInputSource(fSent);
		AnalysisEngineDescription aedSent = pars.parseAnalysisEngineDescription(xmlInputSourceSent);
		
		File fToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/TokenAnnotator.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/TokenAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceToken = new XMLInputSource(fToken);
		AnalysisEngineDescription aedToken = pars.parseAnalysisEngineDescription(xmlInputSourceToken);
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/LetterAnnotator.xml", System.getProperty("user.dir")+"/META-INF/org.apache.uima.fit/LetterAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aed);
	}
	
	
	/*
	 * Checks that there are 1317 letters in the text of the file META-INF/cani.txt.
	 */
	
	@Test
	public void annotateLettersItalianNumberLettersTest() throws Exception {
		int n = 0;
		Iterator it = jCas.getAnnotationIndex(Letter.type).iterator();		
		while(it.hasNext()) {
			Object ob = it.next();
	         n += 1;
	      }

		assertEquals(1317, n); 
	}
	
	
	/*
	 * Checks that the letter number 15 in the text of the file META-INF/cani.txt is ò.
	 */
	
	@Test
	public void annotateLettersItalianTest() throws Exception {
		int n = 0;
		Iterator it = jCas.getAnnotationIndex(Letter.type).iterator();		
		while(it.hasNext()) {
			Letter ob = (Letter) it.next();
	         n += 1;
	         if (n == 15){
	        	 assertEquals('ò', ob.getCoveredText().charAt(0)); 
	         
	         }
	      }
	}
	
}
