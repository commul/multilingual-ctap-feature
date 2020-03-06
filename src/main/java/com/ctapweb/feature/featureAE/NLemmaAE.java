/**
 * 
 */
package com.ctapweb.feature.featureAE;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.ctapweb.feature.logging.LogMarker;
import com.ctapweb.feature.logging.message.AEType;
import com.ctapweb.feature.logging.message.DestroyAECompleteMessage;
import com.ctapweb.feature.logging.message.DestroyingAEMessage;
import com.ctapweb.feature.logging.message.InitializeAECompleteMessage;
import com.ctapweb.feature.logging.message.InitializingAEMessage;
import com.ctapweb.feature.logging.message.PopulatedFeatureValueMessage;
import com.ctapweb.feature.logging.message.ProcessingDocumentMessage;
import com.ctapweb.feature.type.NLemma;
import com.ctapweb.feature.type.NToken;
import com.ctapweb.feature.type.Lemma;
import com.ctapweb.feature.type.Token;

/**
 * @author nokinina
 * Counts the number of lemmas.
 */
public class NLemmaAE extends JCasAnnotator_ImplBase {

	//the analysis engine's id from the database
	//this value needs to be set when initiating the analysis engine
	public static final String PARAM_AEID = "aeID";
	private int aeID;
	public static final String PARAM_EXCLUDE_PUNCTUATIONS = "excludePunctuations";
	private Boolean excludePunct = true; //true by default
	
	private static final Logger logger = LogManager.getLogger();

	private static final AEType aeType = AEType.FEATURE_EXTRACTOR;
	private static final String aeName = "NLemma Feature Extractor";

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		logger.trace(LogMarker.UIMA_MARKER, new InitializingAEMessage(aeType, aeName));

		super.initialize(aContext);
		
		// get the optional pamameter
		Boolean paramValue = (Boolean) aContext.getConfigParameterValue(PARAM_EXCLUDE_PUNCTUATIONS);
		if(paramValue != null) {
			excludePunct = paramValue;
		}
				
		//get the parameter value of analysis id
		if(aContext.getConfigParameterValue(PARAM_AEID) == null) {
			ResourceInitializationException e = new ResourceInitializationException("mandatory_value_missing", 
					new Object[] {PARAM_AEID});
			logger.throwing(e);
			throw e;
		} else {
			aeID = (Integer) aContext.getConfigParameterValue(PARAM_AEID);
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

		// count the number of lemmas
		// get annotation indexes and iterator
		Iterator it = aJCas.getAnnotationIndex(Lemma.type).iterator();
		
		//for storing unique lemmas
		Set<String> uniqueLemmas = new HashSet<>();
		
		int occurrence = 0;
		//count number of occurrences 
		while(it.hasNext()) {
			Lemma lemma = (Lemma) it.next();
			String lemmaString = lemma.getLemma();
			
			if((excludePunct && lemma.getCoveredText().matches("\\p{Punct}")) || uniqueLemmas.contains(lemmaString)) {
				continue;
			} else {
				uniqueLemmas.add(lemmaString);
				occurrence++;
			}
		}

		//output the feature type
		NLemma annotation = new NLemma(aJCas);

		//set the feature ID
		annotation.setId(aeID);

		//set feature value
		annotation.setValue(occurrence);
		annotation.addToIndexes();

		logger.info(new PopulatedFeatureValueMessage(aeID, occurrence));
	}

	@Override
	public void destroy() {
		logger.trace(LogMarker.UIMA_MARKER, new DestroyingAEMessage(aeType, aeName));

		super.destroy();

		logger.trace(LogMarker.UIMA_MARKER, new DestroyAECompleteMessage(aeType, aeName));
	}

}
