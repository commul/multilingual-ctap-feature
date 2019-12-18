package com.ctapweb.feature.featureAE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;

import com.ctapweb.feature.logging.LogMarker;
import com.ctapweb.feature.logging.message.AEType;
import com.ctapweb.feature.logging.message.DestroyAECompleteMessage;
import com.ctapweb.feature.logging.message.DestroyingAEMessage;
import com.ctapweb.feature.logging.message.InitializeAECompleteMessage;
import com.ctapweb.feature.logging.message.InitializingAEMessage;
import com.ctapweb.feature.logging.message.PopulatedFeatureValueMessage;
import com.ctapweb.feature.logging.message.ProcessingDocumentMessage;
import com.ctapweb.feature.type.NToken;
import com.ctapweb.feature.type.NSentence;
import com.ctapweb.feature.type.NSyllable;
import com.ctapweb.feature.type.ComplexityFeatureBase;
import com.ctapweb.feature.type.Flesch;
import com.ctapweb.feature.type.Token;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

/**
 * 
 * @author Nadezda Okinina
 * 
 * Flesch Reading Ease and Flesch-Kincaid Grade Level
 * Source of formula: https://en.wikipedia.org/wiki/Flesch%E2%80%93Kincaid_readability_tests
 * In the Flesch reading-ease test, higher scores indicate material that is easier to read; lower numbers mark passages that are more difficult to read.
 * 
 * The formula for the Flesch reading-ease score (FRES) test is:
 *  * 
 * 206.835 - (84.6 * mean_token_length_in_syllables) - (1.015 * mean_sentence_length_in_tokens)
 * 
 * Scores can be interpreted as shown in the table below:
		
		100.00–90.00 	5th grade 	Very easy to read. Easily understood by an average 11-year-old student.
		90.0–80.0 	6th grade 	Easy to read. Conversational English for consumers.
		80.0–70.0 	7th grade 	Fairly easy to read.
		70.0–60.0 	8th & 9th grade 	Plain English. Easily understood by 13- to 15-year-old students.
		60.0–50.0 	10th to 12th grade 	Fairly difficult to read.
		50.0–30.0 	College 	Difficult to read.
		30.0–0.0 	College graduate 	Very difficult to read. Best understood by university graduates.
 * 
 * The formula for the Flesch-Kincaid Grade Level test is:
 * 
 * ( 0.39 * mean_sentence_length_in_tokens ) + ( 11.8 * mean_token_length_in_syllables ) - 15.59
 * 
 * Kincaid JP, Fishburne RP Jr, Rogers RL, Chissom BS (February 1975). "Derivation of new readability formulas (Automated Readability Index, Fog Count and Flesch Reading Ease Formula) for Navy enlisted personnel" (PDF). Research Branch Report 8-75, Millington, TN: Naval Technical Training, U. S. Naval Air Station, Memphis, TN.
 * 
 */
public class FleschAE  extends JCasAnnotator_ImplBase {
	public static final String PARAM_AEID = "aeID";
	public static final String PARAM_readingEaseOrKincaid = "readingEaseOrKincaid";
	private String readingEaseOrKincaid;
	private int aeID;

	private static final Logger logger = LogManager.getLogger();

	private static final AEType aeType = AEType.FEATURE_EXTRACTOR;
	private static final String aeName = "Flesch Feature Extractor";

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		logger.trace(LogMarker.UIMA_MARKER, new InitializingAEMessage(aeType, aeName));

		super.initialize(aContext);

		//get the parameter value of analysis id
		if(aContext.getConfigParameterValue(PARAM_AEID) == null) {
			ResourceInitializationException e = new ResourceInitializationException("mandatory_value_missing", 
					new Object[] {PARAM_AEID});
			logger.throwing(e);
			throw e;
		} else {
			aeID = (Integer) aContext.getConfigParameterValue(PARAM_AEID);
		}

		//get the parameter value for the formular : Reading Ease or Kincaid Grade level
		if(aContext.getConfigParameterValue(PARAM_readingEaseOrKincaid) == null) {
			ResourceInitializationException e = new ResourceInitializationException("mandatory_value_missing", 
					new Object[] {PARAM_readingEaseOrKincaid});
			logger.throwing(e);
			throw e;
		} else {
			readingEaseOrKincaid = (String) aContext.getConfigParameterValue(PARAM_readingEaseOrKincaid);

			//check if the value is correct
			switch(readingEaseOrKincaid) {
			case "re":
			case "kc":
				break;
			default: 
				throw new ResourceInitializationException("annotator_parameter_not_valid", 
						new Object[] {readingEaseOrKincaid, PARAM_readingEaseOrKincaid});
			}
		}

		logger.trace(LogMarker.UIMA_MARKER, new InitializeAECompleteMessage(aeType, aeName));
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		logger.trace(LogMarker.UIMA_MARKER, 
				new ProcessingDocumentMessage(aeType, aeName, aJCas.getDocumentText()));

		double nSyllables = 0;
		double nSentences = 0;
		double nTokens = 0;
		
		
		Iterator itSyll = aJCas.getAllIndexedFS(NSyllable.class);
		if(itSyll.hasNext()) {
			NSyllable nSyll = (NSyllable)itSyll.next();
			nSyllables = nSyll.getValue();
		}
		
		Iterator itSent = aJCas.getAllIndexedFS(NSentence.class);
		if(itSent.hasNext()) {
			NSentence nSent = (NSentence)itSent.next();
			nSentences = nSent.getValue();
		}
		
		Iterator itTok = aJCas.getAllIndexedFS(NToken.class);
		if(itTok.hasNext()) {
			NToken nTok = (NToken)itTok.next();
			nTokens = nTok.getValue();
		}
		
		/*
		for(ComplexityFeatureBase annot : JCasUtil.select(aJCas, ComplexityFeatureBase.class)){
			if (annot.getType().getName().endsWith("NSyllable")){
				nSyllables = annot.getValue();
			}else if (annot.getType().getName().endsWith("NSentence")){
				nSentences = annot.getValue();
			}else if (annot.getType().getName().endsWith("NToken")){
				nTokens = annot.getValue();
			}
		}
		*/

		double flesch = 0;		
		switch (readingEaseOrKincaid){
		case "re":
			flesch = 206.835 - ( 84.6 * ( nSyllables / nTokens )) - ( 1.015 * ( nTokens / nSentences ));
			break;
		case "kc":
			flesch = ( 0.39 * ( nTokens / nSentences ) ) + ( 11.8 * ( nSyllables / nTokens ) ) - 15.59;
			break;
		}
		//output the feature type
		Flesch annotation = new Flesch(aJCas);
		//set the feature ID
		annotation.setId(aeID);

		//set feature value
		annotation.setValue(flesch);
		annotation.addToIndexes();

		logger.info(new PopulatedFeatureValueMessage(aeID, flesch));
	}
	

	@Override
	public void destroy() {
		logger.trace(LogMarker.UIMA_MARKER, new DestroyingAEMessage(aeType, aeName));

		super.destroy();

		logger.trace(LogMarker.UIMA_MARKER, new DestroyAECompleteMessage(aeType, aeName));
	}
}

