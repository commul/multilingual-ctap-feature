package com.ctapweb.annotator.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XMLParser;
import org.junit.Test;

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
import com.ctapweb.feature.type.POS;

import com.ctapweb.feature.type.ParseTree;

public class ParseTreeAnnotatorItTest {
	JCas jCas;
	XMLParser pars;
	AnalysisEngineDescription aedSent, aedToken, aedPOS;
	HashMap <String, ArrayList <String>> paramsHashMap;
	ArrayList<String> locationsListForTest;
	
	@Before
	public void setUp() throws Exception {
		
		pars = UIMAFramework.getXMLParser();
		
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();		
		
		ArrayList<String> locationsList = new ArrayList<String>();
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/ParseTreeType.xml");
		locationsList.add("src/main/resources/descriptor/type_system/linguistic_type/POSType.xml");
		
		DescriptorModifier.readXMLTypeDescriptorModifyImports ("src/main/resources/descriptor/type_system/linguistic_type/ParseTreeType.xml", "src/test/resources/org.apache.uima.fit/ParseTreeTypeForUIMAFitTest.xml", locationsList);
		String sdSentenceLengthTypeDescr = new String(Files.readAllBytes(Paths.get("src/test/resources/org.apache.uima.fit/ParseTreeTypeForUIMAFitTest.xml")));
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(sdSentenceLengthTypeDescr.getBytes("UTF-8")));
		
		tsd.buildFromXMLElement(doc.getDocumentElement(), pars);
	    jCas = CasCreationUtils.createCas(tsd, null, null).getJCas();
		
	    String contents = new String(Files.readAllBytes(Paths.get("src/test/resources/cani.txt")));
		jCas.setDocumentText(contents);
		
		File fSent = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/SentenceAnnotator.xml", "src/test/resources/org.apache.uima.fit/SentenceAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceSent = new XMLInputSource(fSent);
		aedSent = pars.parseAnalysisEngineDescription(xmlInputSourceSent);
	
		File fToken = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/TokenAnnotator.xml", "src/test/resources/org.apache.uima.fit/TokenAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourceToken = new XMLInputSource(fToken);
		aedToken = pars.parseAnalysisEngineDescription(xmlInputSourceToken);
	
		File fPOS = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/POSAnnotator.xml", "src/test/resources/org.apache.uima.fit/POSAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSourcePOS = new XMLInputSource(fPOS);
		aedPOS = pars.parseAnalysisEngineDescription(xmlInputSourcePOS);
		
		File f = DescriptorModifier.readXMLAnnotatorDescriptorAddLanguage ("src/main/resources/descriptor/annotator/ParseTreeAnnotator.xml", "src/test/resources/org.apache.uima.fit/ParseTreeAnnotatorForUIMAFitTest.xml", "IT");
		XMLInputSource xmlInputSource = new XMLInputSource(f);
		AnalysisEngineDescription aed = pars.parseAnalysisEngineDescription(xmlInputSource);
		
		//Run the analysis pipeline: SentenceAnnotator, then TokenAnnotator, then POSAnnotator, then ParseTreeAnnotator
		SimplePipeline.runPipeline(jCas, aedSent, aedToken, aedPOS, aed);
		
		}
	
	
	/*
	 * Tests that the number of parse trees is equal to 12
	 */
	
	@Test
	public void annotateParseTreeNumberParseTreesItalianTest() throws Exception {		
		int n = 0;
		Iterator it = jCas.getAnnotationIndex(ParseTree.type).iterator();		
		while(it.hasNext()) {
			ParseTree ob = (ParseTree)it.next();
			//System.out.println("ob.toString(): " + ob.toString());
	         n += 1;
	      }

		assertEquals(12, n); 
	}
	
	
	/*
	 * Tests that 12 parse trees start with "(0 (root "
	 */
	/*
	@Test
	public void annotateParseTreeAllParseTreesStartWithZeroRootItalianTest() throws Exception {		
		int n = 0;
		Iterator it = jCas.getAnnotationIndex(ParseTree.type).iterator();		
		while(it.hasNext()) {
			ParseTree ob = (ParseTree)it.next();
			//System.out.println("ob.toString(): " + ob.toString());
			ob.getParseTree();
			if (ob.getParseTree().startsWith("(0 (root ")){
				n += 1;
			}
	      }

		assertEquals(12, n); 
	}
	*/
}
