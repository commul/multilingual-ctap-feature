package com.ctapweb.feature.featureAE;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;

import com.ctapweb.feature.logging.LogMarker;
import com.ctapweb.feature.logging.message.AEType;
import com.ctapweb.feature.logging.message.DestroyAECompleteMessage;
import com.ctapweb.feature.logging.message.DestroyingAEMessage;
import com.ctapweb.feature.logging.message.InitializeAECompleteMessage;
import com.ctapweb.feature.logging.message.InitializingAEMessage;
import com.ctapweb.feature.logging.message.PopulatedFeatureValueMessage;
import com.ctapweb.feature.logging.message.ProcessingDocumentMessage;
import com.ctapweb.feature.type.LexicalPercentage;
import com.ctapweb.feature.type.NToken;
import com.ctapweb.feature.type.Sentence;
import com.ctapweb.feature.type.Token;
import com.ctapweb.feature.type.Lemma;
import com.ctapweb.feature.util.LookUpListResource;

/**
 * 
 * @author nokinina
 * 
 * Calculates the percentage of lemmas listed in a resource.
 * 
 * For each token of a text, excluding punctuations, takes its lemma and checks if it is present in the resource (a set of lemmas).
 * Devides the number of lemmas present in the resource by the whole number of lemmas and multiplies by 100.
 * 
 * By default counts all lemmas. If the type parameter is set to true, only counts unique lemmas.
 *
 */
public class LexicalPercentageAE extends JCasAnnotator_ImplBase {

	//the analysis engine's id from the database
	//this value needs to be set when initiating the analysis engine
	public static final String PARAM_AEID = "aeID";
	public static final String PARAM_TYPE = "type";
	public static final String RESOURCE_KEY = "lookUpList";
	public static final String PARAM_LANGUAGE_CODE = "LanguageCode";

	private int aeID;
	private boolean type = false; //whether to count unique lemmas or all lemmas: "true" means to calculate unique lemmas instead of all lemmas
	private LookUpListResource lemmaResourceList;

	private static final Logger logger = LogManager.getLogger();

	private static final AEType aeType = AEType.FEATURE_EXTRACTOR;
	private static final String aeName = "Lexical Percentage Feature Extractor";

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

		// get the optional parameter of "type" (boolean)
		if(aContext.getConfigParameterValue(PARAM_TYPE) != null) {
			type = (Boolean) aContext.getConfigParameterValue(PARAM_TYPE);
		}

		// obtain mandatory language parameter and access language dependent resources
		String lCode = "";
		if(aContext.getConfigParameterValue(PARAM_LANGUAGE_CODE) == null) {
			ResourceInitializationException e = new ResourceInitializationException("mandatory_value_missing", 
					new Object[] {PARAM_LANGUAGE_CODE});
			logger.throwing(e);
			throw e;
		} else {
			lCode = ((String) aContext.getConfigParameterValue(PARAM_LANGUAGE_CODE)).toUpperCase();
		}
		String languageSpecificResourceKey = RESOURCE_KEY + lCode;

		//get the list of connectives
		try {
			lemmaResourceList = (LookUpListResource) aContext.getResourceObject(languageSpecificResourceKey);
		} catch (ResourceAccessException e) {
			logger.throwing(e);
			throw new ResourceInitializationException(e);
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

		// get annotation indexes and iterator
		Iterator lemmaIter = aJCas.getAnnotationIndex(Lemma.type).iterator();
		int numberLemmasInResource = 0;
		int numberLemmas = 0;

		Set <String> deMauroDic = lemmaResourceList.getKeys();

		//for storing unique lemmas
		Set<String> uniqueLemmas = new HashSet<>();

		while (lemmaIter.hasNext()) {
			Lemma lemma = (Lemma) lemmaIter.next();
			String lemmaString = lemma.getLemma();

			if(lemmaString.matches("\\p{Punct}")) {
				continue; //Ignore punctuations
			}else if(type && uniqueLemmas.contains(lemmaString)) {
				continue; //skip repeated lemmas
			}else {
				uniqueLemmas.add(lemmaString);
				numberLemmas += 1;
				if ( deMauroDic.contains(lemmaString)) {
					numberLemmasInResource += 1;
				}
			}
		}

		double percentage = ( (double)numberLemmasInResource / (double)numberLemmas ) * 100;

		LexicalPercentage annotation = new LexicalPercentage(aJCas);
		//set the feature ID and feature value
		annotation.setId(aeID);
		annotation.setValue(percentage);
		annotation.addToIndexes();

		logger.info(new PopulatedFeatureValueMessage(aeID, percentage));
	}


	@Override
	public void destroy() {
		logger.trace(LogMarker.UIMA_MARKER, new DestroyingAEMessage(aeType, aeName));

		super.destroy();

		logger.trace(LogMarker.UIMA_MARKER, new DestroyAECompleteMessage(aeType, aeName));
	}
}
