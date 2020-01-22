package com.ctapweb.feature.featureAE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;


import com.ctapweb.feature.annotator.SyllableAnnotator.SyllablePatterns;
import com.ctapweb.feature.logging.LogMarker;
import com.ctapweb.feature.logging.message.AEType;
import com.ctapweb.feature.logging.message.DestroyAECompleteMessage;
import com.ctapweb.feature.logging.message.DestroyingAEMessage;
import com.ctapweb.feature.logging.message.InitializeAECompleteMessage;
import com.ctapweb.feature.logging.message.InitializingAEMessage;
import com.ctapweb.feature.logging.message.PopulatedFeatureValueMessage;
import com.ctapweb.feature.logging.message.ProcessingDocumentMessage;
import com.ctapweb.feature.logging.message.SelectingLanguageSpecificResource;
import com.ctapweb.feature.type.NConnectives;
import com.ctapweb.feature.type.Sentence;
import com.ctapweb.feature.type.Syllable;
import com.ctapweb.feature.type.Token;
import com.ctapweb.feature.util.LookUpListResource;
import com.ctapweb.feature.util.SupportedLanguages;

/**
 * Counts the pccurrence of multi- and single word connectives based on lists of connectives
 * @author zweiss
 *
 */
public class NConnectivesAE extends JCasAnnotator_ImplBase {

	//the analysis engine's id from the database
	//this value needs to be set when initiating the analysis engine
	public static final String PARAM_AEID = "aeID";
	public static final String PARAM_CONNECTIVE_SCOPE = "Scope";
	public static final String PARAM_CONNECTIVE_TYPE = "connectiveType";
	public static final String RESOURCE_KEY = "lookUpList";
	public static final String PARAM_LANGUAGE_CODE = "LanguageCode";

	private CTAPConnectiveCounter connectiveCounter;

	private int aeID;
	private LookUpListResource listOfConnectives;
	private String countingScope;
	private String connectiveType;

	private static final Logger logger = LogManager.getLogger();

	private static final AEType aeType = AEType.FEATURE_EXTRACTOR;
	private static final String aeName = "Use of Connectives Feature Extractor";

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
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

		// see if only multi word connectives are relevant
		if(aContext.getConfigParameterValue(PARAM_CONNECTIVE_SCOPE) == null) {
			ResourceInitializationException e = new ResourceInitializationException("mandatory_value_missing", 
					new Object[] {PARAM_CONNECTIVE_SCOPE});
			logger.throwing(e);
			throw e;
		} else {
			countingScope = (String) aContext.getConfigParameterValue(PARAM_CONNECTIVE_SCOPE);
		}

		// get semantic type
		if(aContext.getConfigParameterValue(PARAM_CONNECTIVE_TYPE) == null) {
			ResourceInitializationException e = new ResourceInitializationException("mandatory_value_missing", 
					new Object[] {PARAM_CONNECTIVE_TYPE});
			logger.throwing(e);
			throw e;
		} else {
			connectiveType = (String) aContext.getConfigParameterValue(PARAM_CONNECTIVE_TYPE);
		}

		// might become relevant later on, when we add POS tagging to refine results
		// obtain mandatory language parameter and access language dependent resources
		String lCode = "";
		if(aContext.getConfigParameterValue(PARAM_LANGUAGE_CODE) == null) {
			ResourceInitializationException e = new ResourceInitializationException("mandatory_value_missing", 
					new Object[] {PARAM_LANGUAGE_CODE});
			logger.throwing(e);
			throw e;
		} else {
			lCode = ((String) aContext.getConfigParameterValue(PARAM_LANGUAGE_CODE)).toUpperCase();
			logger.trace(LogMarker.UIMA_MARKER, new SelectingLanguageSpecificResource(aeName, lCode));
			switch (lCode) {
			case SupportedLanguages.GERMAN:
				connectiveCounter = new GermanConnectiveCounter();
				break;
			case SupportedLanguages.ITALIAN:
				connectiveCounter = new ItalianConnectiveCounter();
				break;
				// add new language here
			default:   // TODO reconsider default
				connectiveCounter = new GermanConnectiveCounter();
				break;
			}
		}
		String languageSpecificResourceKey = RESOURCE_KEY + lCode;

		//get the list of connectives
		try {
			listOfConnectives = (LookUpListResource) aContext.getResourceObject(languageSpecificResourceKey);
		} catch (ResourceAccessException e) {
			logger.throwing(e);
			throw new ResourceInitializationException(e);
		}

		logger.trace(LogMarker.UIMA_MARKER, new InitializeAECompleteMessage(aeType, aeName));

		//logger.trace(LogMarker.UIMA_MARKER, "connectiveType: "+connectiveType);
	}

	/* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		logger.trace(LogMarker.UIMA_MARKER, 
				new ProcessingDocumentMessage(aeType, aeName, aJCas.getDocumentText()));
		//  Debugging
		//logger.trace(LogMarker.UIMA_MARKER, "connectiveType: "+connectiveType);

		// get annotation indexes and iterator
		Iterator<Annotation> sentIter = aJCas.getAnnotationIndex(Sentence.type).iterator();		
		Iterator<Annotation> tokenIter = aJCas.getAnnotationIndex(Token.type).iterator();
		double occurrence = connectiveCounter.countConnectives(sentIter, tokenIter, aJCas);

		NConnectives annotation = new NConnectives(aJCas);
		//set the feature ID and feature value
		annotation.setId(aeID);
		annotation.setValue(occurrence);
		annotation.setConnectiveType(connectiveType);
		annotation.addToIndexes();

		logger.info(new PopulatedFeatureValueMessage(aeID, occurrence));
	}

	/**
	 * Wrapper for connective counters
	 * @author nokinina
	 */
	interface CTAPConnectiveCounter {
		/*
		 * Takes as argument an iterator over sentences and an iterator over tokens
		 */
		abstract double countConnectives(Iterator<Annotation> sentIter, Iterator<Annotation> tokenIter, JCas aJCas);
	}
	
	/**
	 * Counts Italian connectives
	 * Implements the interface CTAPConnectiveCounter
	 * @author nokinina
	 */
	private class ItalianConnectiveCounter implements CTAPConnectiveCounter {

		public ItalianConnectiveCounter() {
		}

		@Override
		public double countConnectives(Iterator<Annotation> sentIter, Iterator<Annotation> tokenIter, JCas aJCas) {
			double occurrence = 0.0;
			while (sentIter.hasNext()) {
				Sentence sent = (Sentence) sentIter.next();
				int sentStart = sent.getBegin();
				int sentEnd = sent.getEnd();
				//iterate through all tokens in the sentence
				List<String> sentTokens = new ArrayList<String>();
				while(tokenIter.hasNext()) {
					Token token = (Token) tokenIter.next();
					if(token.getBegin() >= sentStart && token.getEnd() <= sentEnd) {
						sentTokens.add(token.getCoveredText().trim().toLowerCase());
					}
				}

				for (String connective : listOfConnectives.getKeys()) {
					occurrence = occurrence + countOccurrencesConnectives(sent.getCoveredText().toLowerCase(), sentTokens, connective);
				}
			}

			return occurrence;		
		}

		//TODO Add check for syntactic structure
		private int countOccurrencesConnectives(String sentence, List<String> tokenizedSentence, String connID) {
			//logger.trace(LogMarker.UIMA_MARKER, "in countOccurrencesConnectives connID: "+connID);
			// Base case 1: connective does not occur in sentence
			if (!sentence.contains(connID)) {
				//logger.trace(LogMarker.UIMA_MARKER, "Exit 1: ID not in sentence");
				return 0;
			}
			int nOccurrence = 0;
			Pattern p;
						
			if(connID.equals("se")){
				p = Pattern.compile("(^|\\P{L})" + connID+"(\\P{L}+(?!(stess|medesim)[oaei])|$)");
			}else{
				p = Pattern.compile("(^|\\P{L})" + connID+"(\\P{L}|$)");
			}
			Matcher m = p.matcher(sentence);
			while (m.find()){
				nOccurrence++;
				//  Debugging
				//logger.trace(LogMarker.UIMA_MARKER, "Found: "+connID);
				//logger.trace(LogMarker.UIMA_MARKER, "In sentence: "+sentence);
			}

			//  Debugging
			//logger.trace(LogMarker.UIMA_MARKER, "nOccurrence: "+nOccurrence);
			
			return nOccurrence;
		}
	}
	
	/**
	 * Counts German connectives
	 * Implements the interface CTAPConnectiveCounter
	 * @author zarah weiss
	 */
	private class GermanConnectiveCounter implements CTAPConnectiveCounter {

		public GermanConnectiveCounter() {
		}

		@Override
		public double countConnectives(Iterator<Annotation> sentIter, Iterator<Annotation> tokenIter1, JCas aJCas) {
			double occurrence = 0.0;
			while (sentIter.hasNext()) {
				Sentence sent = (Sentence) sentIter.next();
				int sentStart = sent.getBegin();
				int sentEnd = sent.getEnd();				
				//iterate through all tokens in the sentence
				List<String> sentTokens = new ArrayList<String>();
				Iterator tokenIter = aJCas.getAnnotationIndex(Token.type).iterator(false);
				while(tokenIter.hasNext()) {
					Token token = (Token) tokenIter.next();					
					if(token.getBegin() >= sentStart && token.getEnd() <= sentEnd) {
						sentTokens.add(token.getCoveredText().trim().toLowerCase());
					}
				}

				for (String connective : listOfConnectives.getKeys()) {
					occurrence = occurrence + countOccurrencesIncludingMultiConnectives(sent.getCoveredText().toLowerCase(), sentTokens, connective);
				}
			}

			return occurrence;		
		}

		//TODO Add check for syntactic structure
		private int countOccurrencesIncludingMultiConnectives(String sentence, List<String> tokenizedSentence, String connID) {
			// Base case 1: connective does not occur in sentence
			if (!sentence.contains(connID)) {
				return 0;
			}
			int nOccurrence = 0;
			// Base case 2: single-word connective without multi word variants occurs in the sentence
			if (listOfConnectives.lookup(connID).length == 0) {
				// if we are not interested in single word connectives, just return 0
				if (countingScope.equals("MULTI")) {
					return 0;
				}
				// if we are interested in single word connectives, count each occurrence
				for (String token : tokenizedSentence) {
					if(token.equals(connID)) {
						nOccurrence++;
					}
				}
				//  Debugging 
				//logger.trace(LogMarker.UIMA_MARKER, "Found: "+connID);
				//logger.trace(LogMarker.UIMA_MARKER, "In sentence: "+sentence);
				//logger.trace(LogMarker.UIMA_MARKER, "Counting 1: non-multi connective: "+nOccurrence);
				return nOccurrence;
			}

			boolean letsCountSingleWordConn = false;
			// Complex case: we have multi word connectives
			for (String multiConnective : listOfConnectives.lookup(connID)) {
				// Multi-word connectives with long distance compounds are separated by "-"
				// Multi-word connectives with adjacent compounds are separated by " "
				// Combinations are possible, e.g., "sowohl: sowohl-als auch"
				String[] multiConnectiveSplit = multiConnective.split("-");  // Length one in case of connectives without long distance components 
				int curPosition = 0;  // keep track of sentence position
				boolean foundFullMultiWordConnective = false;
				for (int curConn = 0; curConn < multiConnectiveSplit.length; curConn++) {
					// current component may contain multiple words
					int sizeCurrentComponent = multiConnectiveSplit[curConn].trim().split(" ").length;  // between 1 and n
					boolean foundComponent = false;

					// otherwise we iterate over the sentence to find our mathc
					for (int curTok = curPosition; curTok < tokenizedSentence.size(); curTok++) {
						// trivial case: remainder of sentence is too short to contain the rest of the connective
						if (curTok+sizeCurrentComponent > tokenizedSentence.size()) {
							foundComponent = false;
							break;
						}
						// build the area of interest (in case of single word connective just wone token
						StringBuilder sb = new StringBuilder();
						for (int i = 0; i < sizeCurrentComponent; i++) {
							sb.append(tokenizedSentence.get(curTok+i));
							sb.append(" ");
						}
						if (sb.toString().trim().equals(multiConnectiveSplit[curConn].trim())) {
							foundComponent = true;  // ... the sentence might contain the full pattern
							if (curConn==multiConnectiveSplit.length-1) {
								// if we successfully found the last component, we found the full match
								foundFullMultiWordConnective = true;
							}
							curPosition = curTok+sizeCurrentComponent;  // ... we continue our search for the next component in the remainder of the sentence
							break;  // .. we stop searching for the current component 
						}
					}
					// if you iterates through the sentence and did not find the current component, the connective is not in there
					if (!foundComponent) {
						break;
					}
				}

				// if we found a multi word connective and we are not excluding multi word connectives, increase the occurrence count
				if (foundFullMultiWordConnective && !countingScope.equals("SINGLE")) {
					nOccurrence++;
					// Debugging 
					//logger.trace(LogMarker.UIMA_MARKER, "Found: "+connID);
					//logger.trace(LogMarker.UIMA_MARKER, "In sentence: "+sentence);
					//logger.trace(LogMarker.UIMA_MARKER, "Counting 2: multi connective: "+nOccurrence);
				}
				// if we have a single word connective (with multi word variants) and we are not excluding single word connectives, 
				// increase the occurrence count
				else if (!foundFullMultiWordConnective && !countingScope.equals("MULTI")) {
					letsCountSingleWordConn = true;
					//The following 5 lines were commented by Nadia, because were erroneous: they counted several times the same 1-word connective that in theory has multi-word variants, but not in this text
					//nOccurrence++;
					// Debugging 
					//logger.trace(LogMarker.UIMA_MARKER, "Found: "+connID);
					//logger.trace(LogMarker.UIMA_MARKER, "In sentence: "+sentence);
					//logger.trace(LogMarker.UIMA_MARKER, "Counting 3: single connective: "+nOccurrence);
				}
			}

			// Loop added by Nadia for counting one-word connectives that have multi-word variants, but not in this text
			if (letsCountSingleWordConn){
				// if we are interested in single word connectives, count each occurrence
				for (String token : tokenizedSentence) {
					if(token.equals(connID)) {
						nOccurrence++;
						//logger.trace(LogMarker.UIMA_MARKER, "Found: "+connID);
						//logger.trace(LogMarker.UIMA_MARKER, "In sentence: "+sentence);
						//logger.trace(LogMarker.UIMA_MARKER, "Counting 4: single from multi connective: "+nOccurrence);
					}
				}
			}

			return nOccurrence;
		}
	}


	@Override
	public void destroy() {
		logger.trace(LogMarker.UIMA_MARKER, new DestroyingAEMessage(aeType, aeName));

		super.destroy();

		logger.trace(LogMarker.UIMA_MARKER, new DestroyAECompleteMessage(aeType, aeName));
	}
}
