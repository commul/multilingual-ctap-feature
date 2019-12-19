/**
 * 
 */
package com.ctapweb.feature.featureAE;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.ctapweb.feature.featureAE.NConnectivesAE.CTAPConnectiveCounter;
import com.ctapweb.feature.logging.LogMarker;
import com.ctapweb.feature.logging.message.AEType;
import com.ctapweb.feature.logging.message.DestroyAECompleteMessage;
import com.ctapweb.feature.logging.message.DestroyingAEMessage;
import com.ctapweb.feature.logging.message.InitializeAECompleteMessage;
import com.ctapweb.feature.logging.message.InitializingAEMessage;
import com.ctapweb.feature.logging.message.PopulatedFeatureValueMessage;
import com.ctapweb.feature.logging.message.ProcessingDocumentMessage;
import com.ctapweb.feature.type.NSyntacticConstituent;
import com.ctapweb.feature.type.ParseTree;
import com.ctapweb.feature.util.SupportedLanguages;

import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeReader;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NSyntacticConstituentAE extends JCasAnnotator_ImplBase {

	//the analysis engine's id from the database
	//this value needs to be set when initiating the analysis engine
	public static final String PARAM_AEID = "aeID";
	public static final String PARAM_CONSTITUENT_TYPE = "constituentType";
	public static final String PARAM_TREGEX_PATTERNS = "tregexPatterns";

	public static String lCode;

	private CTAPSyntacticConstituentCounter syntacticConstituentCounter;

	private int aeID;
	private String constituentType;
	private String[] tregexPatterns;

	//Tregex
	//private List<TregexPattern> patternList;
	//private List<Pattern> patternListNotTregex;

	private static final Logger logger = LogManager.getLogger();

	private static final String PARAM_LANGUAGE_CODE = "LanguageCode";
	private static final AEType aeType = AEType.FEATURE_EXTRACTOR;
	private static final String aeName = "NSyntacticConstituent Feature Extractor";

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		logger.trace(LogMarker.UIMA_MARKER, new InitializingAEMessage(aeType, aeName));
		super.initialize(aContext);

		// get the parameters
		// define the model to be loaded based on the mandatory LanguageCode config parameter
		//String lCode = "";
		if(aContext.getConfigParameterValue(PARAM_LANGUAGE_CODE) == null) { 
			ResourceInitializationException e = new ResourceInitializationException("mandatory_value_missing", 
					new Object[] {PARAM_LANGUAGE_CODE});
			logger.throwing(e);
			throw e;
		} else {
			lCode = ((String) aContext.getConfigParameterValue(PARAM_LANGUAGE_CODE)).toUpperCase();
		}

		// get the parameter value of analysis id
		if(aContext.getConfigParameterValue(PARAM_AEID) == null) {
			ResourceInitializationException e = new ResourceInitializationException("mandatory_value_missing", 
					new Object[] {PARAM_AEID});
			logger.throwing(e);
			throw e;
		} else {
			aeID = (Integer) aContext.getConfigParameterValue(PARAM_AEID);
		}

		// get constituent name
		if(aContext.getConfigParameterValue(PARAM_CONSTITUENT_TYPE) == null) {
			ResourceInitializationException e = new ResourceInitializationException("mandatory_value_missing", 
					new Object[] {PARAM_CONSTITUENT_TYPE});
			logger.throwing(e);
			throw e;
		} else {
			constituentType = (String) aContext.getConfigParameterValue(PARAM_CONSTITUENT_TYPE);
		}

		// get tregex patterns
		String curTregExPattern = PARAM_TREGEX_PATTERNS+lCode;
		logger.trace(LogMarker.UIMA_MARKER, "Attempt to obtain TregEx patterns for: "+curTregExPattern);
		if(aContext.getConfigParameterValue(curTregExPattern) == null) {
			ResourceInitializationException e = new ResourceInitializationException("mandatory_value_missing", 
					new Object[] {curTregExPattern});
			logger.throwing(e);
			throw e;
		} else {
			tregexPatterns = (String []) aContext.getConfigParameterValue(curTregExPattern);
			switch (lCode) {
			case SupportedLanguages.ENGLISH:
				syntacticConstituentCounter = new EnglishSyntacticConstituentCounter();
				break;
			case SupportedLanguages.GERMAN:
				syntacticConstituentCounter = new GermanSyntacticConstituentCounter();
				break;
			case SupportedLanguages.ITALIAN:
				syntacticConstituentCounter = new ItalianSyntacticConstituentCounter();
				break;
				// add new language here
			default:   // TODO reconsider default
				syntacticConstituentCounter = new EnglishSyntacticConstituentCounter();
				break;
			}
		}

		logger.trace(LogMarker.UIMA_MARKER, "Constituent type: {}; tregexPatterns: {} ", 
				constituentType, curTregExPattern);
		logger.trace(LogMarker.UIMA_MARKER, new InitializeAECompleteMessage(aeType, aeName));
	}
	
	/* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		logger.trace(LogMarker.UIMA_MARKER, 
				new ProcessingDocumentMessage(aeType, aeName, aJCas.getDocumentText()));

		int occurrence = syntacticConstituentCounter.countSyntacticConstituents(aJCas);

		//output the feature type
		NSyntacticConstituent annotation = new NSyntacticConstituent(aJCas);

		//set the feature ID and feature value
		annotation.setId(aeID);
		annotation.setValue(occurrence);
		annotation.setContituentType(constituentType);
		annotation.addToIndexes();

		logger.info(new PopulatedFeatureValueMessage(aeID, occurrence));
	}

	/**
	 * Wrapper for syntactic constituent counters
	 * @author nokinina
	 */
	interface CTAPSyntacticConstituentCounter {
		/*
		 * Takes as argument an iterator over sentences and an iterator over tokens
		 */
		abstract int countSyntacticConstituents(JCas aJCas);
	}

	/**
	 * Counts Italian syntactic constituents
	 * Implements the interface CTAPSyntacticConstituentCounter
	 * @author nokinina
	 */
	private class ItalianSyntacticConstituentCounter implements CTAPSyntacticConstituentCounter {

		private List<Pattern> patternListNotTregex;

		public ItalianSyntacticConstituentCounter() {
			//initialize regex patterns
			patternListNotTregex = new ArrayList<>();
			for(String pattern: tregexPatterns) {
				patternListNotTregex.add(Pattern.compile("[\\(\\)\\: ]"+pattern.replace(":", "\\:")+"[\\(\\)\\: ]"));	
			}
		}

		@Override
		public int countSyntacticConstituents(JCas aJCas){
			// get annotation indexes and iterator
			//iterate through all parse trees (sentences).
			Iterator it = aJCas.getAnnotationIndex(ParseTree.type).iterator();
			String previousMatch;
			//count number of occurrences of the tree that matches the tregex patterns
			int occurrence = 0;
			while(it.hasNext()) {
				previousMatch = "";
				
				ParseTree parseTree = (ParseTree) it.next();
				for(Pattern pattern: patternListNotTregex) {
					Matcher matcher = pattern.matcher(parseTree.getParseTree());
					while(matcher.find()) {
						if (!previousMatch.equals(matcher.group())){
							occurrence++;
						}
					}
				}
			}
			return occurrence;
		}
	}

	/**
	 * Counts German syntactic constituents
	 * Implements the interface CTAPSyntacticConstituentCounter
	 * @author nokinina
	 */
	private class GermanSyntacticConstituentCounter implements CTAPSyntacticConstituentCounter {
		private List<TregexPattern> patternList;

		public GermanSyntacticConstituentCounter() {
			StringBuilder sb = new StringBuilder();
			for (String p : tregexPatterns) {
				sb.append(p);
				sb.append(" ");	
			}
			logger.trace(LogMarker.UIMA_MARKER, "Obtained the TregEx patterns: "+sb.toString().trim());

			//initialize tregex patterns
			patternList = new ArrayList<>();
			for(String pattern: tregexPatterns) {
				patternList.add(TregexPattern.compile(pattern));
			}
		}

		@Override
		public int countSyntacticConstituents(JCas aJCas){
			// get annotation indexes and iterator
			//iterate through all parse trees (sentences).
			Iterator it = aJCas.getAnnotationIndex(ParseTree.type).iterator();
			String previousMatch;
			//count number of occurrences of the tree that matches the tregex patterns
			int occurrence = 0;
			while(it.hasNext()) {
				ParseTree parseTree = (ParseTree) it.next();
				//logger.trace(LogMarker.UIMA_MARKER, "Parse tree: {}", parseTree.getParseTree()); // debugging

				//the matcher requires a tree object, so create a tree object from the 
				//parse tree string
				TreeReader treeReader =  new PennTreeReader(new StringReader(parseTree.getParseTree()));
				Tree tree;
				try {
					tree = treeReader.readTree();
					if (tree == null) {
						logger.warn(LogMarker.UIMA_MARKER, "ParseTree could not be converted to Tree and is skipped line 165: "+parseTree.getParseTree().toString());

					} else {
						previousMatch = "";
						
						for(TregexPattern pattern: patternList) {
							//logger.trace(LogMarker.UIMA_MARKER, "pattern: " , pattern);
							//logger.trace(LogMarker.UIMA_MARKER, "pattern: " + pattern.toString()); // debugging
							
							TregexMatcher matcher = pattern.matcher(tree);
							while(matcher.find()) {
								matcher.getMatch().pennPrint();  // debugging
								
								if (!previousMatch.equals(matcher.getMatch().toString())){
									//logger.trace(LogMarker.UIMA_MARKER, "matcher.getMatch().toString(): " + matcher.getMatch().toString()); // debugging
									occurrence++;
									previousMatch = matcher.getMatch().toString();
									//logger.trace(LogMarker.UIMA_MARKER, "occurrence tregex: "+ Integer.toString(occurrence)); // debugging
								}
							}
						}

					}
					treeReader.close();
				} catch (IOException e) {
					logger.throwing(e);
				}
			}
			return occurrence;
		}
	}

	/**
	 * Counts English syntactic constituents
	 * Implements the interface CTAPSyntacticConstituentCounter
	 * @author nokinina
	 */
	private class EnglishSyntacticConstituentCounter implements CTAPSyntacticConstituentCounter {
		private List<TregexPattern> patternList;

		public EnglishSyntacticConstituentCounter() {
			StringBuilder sb = new StringBuilder();
			for (String p : tregexPatterns) {
				sb.append(p);
				sb.append(" ");	
			}
			logger.trace(LogMarker.UIMA_MARKER, "Obtained the TregEx patterns: "+sb.toString().trim());

			//initialize tregex patterns
			patternList = new ArrayList<>();
			for(String pattern: tregexPatterns) {
				patternList.add(TregexPattern.compile(pattern));
			}
		}

		@Override
		public int countSyntacticConstituents(JCas aJCas){
			// get annotation indexes and iterator
			//iterate through all parse trees (sentences).
			Iterator it = aJCas.getAnnotationIndex(ParseTree.type).iterator();
			String previousMatch;
			//count number of occurrences of the tree that matches the tregex patterns
			int occurrence = 0;
			while(it.hasNext()) {
				ParseTree parseTree = (ParseTree) it.next();
				//logger.trace(LogMarker.UIMA_MARKER, "Parse tree: {}", parseTree.getParseTree()); // debugging

				//the matcher requires a tree object, so create a tree object from the 
				//parse tree string
				TreeReader treeReader =  new PennTreeReader(new StringReader(parseTree.getParseTree()));
				Tree tree;
				try {
					tree = treeReader.readTree();
					if (tree == null) {
						logger.warn(LogMarker.UIMA_MARKER, "ParseTree could not be converted to Tree and is skipped line 165: "+parseTree.getParseTree().toString());
					} else {
						previousMatch = "";
						//for each pattern create a matcher
						for(TregexPattern pattern: patternList) {
							//logger.trace(LogMarker.UIMA_MARKER, "pattern: ", pattern); // debugging

							TregexMatcher matcher = pattern.matcher(tree);
							while(matcher.find()) {
								if (!previousMatch.equals(matcher.getMatch().toString())){
									occurrence++;
								}
							}
						}

					}
					treeReader.close();
				} catch (IOException e) {
					logger.throwing(e);
				}
			}
			return occurrence;
		}
	}


	@Override
	public void destroy() {
		logger.trace(LogMarker.UIMA_MARKER, new DestroyingAEMessage(aeType, aeName));

		super.destroy();

		logger.trace(LogMarker.UIMA_MARKER, new DestroyAECompleteMessage(aeType, aeName));
	}
}
