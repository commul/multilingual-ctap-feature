package com.ctapweb.feature.featureAE;


import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
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
import com.ctapweb.feature.type.Gulpease;
import com.ctapweb.feature.type.ComplexityFeatureBase;
import org.apache.uima.fit.util.JCasUtil;
import com.ctapweb.feature.type.NToken;
import com.ctapweb.feature.type.NLetter;
import com.ctapweb.feature.type.NToken;
import com.ctapweb.feature.type.NLetter;
import com.ctapweb.feature.type.NSentence;

/**
 * 
 * @author Nadezda Okinina
 * Source of formula: https://it.wikipedia.org/wiki/Indice_Gulpease
 * L'Indice Gulpease è un indice di leggibilità di un testo tarato sulla lingua italiana[1]. Rispetto ad altri ha il vantaggio di utilizzare la lunghezza delle parole in lettere anziché in sillabe, semplificandone il calcolo automatico.

Definito nel 1988 nell'ambito delle ricerche del GULP (Gruppo Universitario Linguistico Pedagogico) presso il Seminario di Scienze dell'Educazione dell'Università degli studi di Roma "La Sapienza", si basa su rilevazioni raccolte tra il 1986 e il 1987 dalle cattedre di Filosofia del linguaggio e di Pedagogia dell'Istituto di Filosofia.

L'indice di Gulpease considera due variabili linguistiche: la lunghezza della parola e la lunghezza della frase rispetto al numero delle lettere.
La formula per il suo calcolo è la seguente:

89 + ( ((300 * number of sentences) - (10 * number of letters)) / number of tokens )

I risultati sono compresi tra 0 e 100, dove il valore "100" indica la leggibilità più alta e "0" la leggibilità più bassa. In generale risulta che testi con un indice

    inferiore a 80 sono difficili da leggere per chi ha la licenza elementare
    inferiore a 60 sono difficili da leggere per chi ha la licenza media
    inferiore a 40 sono difficili da leggere per chi ha un diploma superiore
 * 
 */
public class GulpeaseAE  extends JCasAnnotator_ImplBase {
	public static final String PARAM_AEID = "aeID";
	private int aeID;

	private static final Logger logger = LogManager.getLogger();

	private static final AEType aeType = AEType.FEATURE_EXTRACTOR;
	private static final String aeName = "Gulpease Feature Extractor";

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

		logger.trace(LogMarker.UIMA_MARKER, new InitializeAECompleteMessage(aeType, aeName));
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
	 */
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		logger.trace(LogMarker.UIMA_MARKER, 
				new ProcessingDocumentMessage(aeType, aeName, aJCas.getDocumentText()));
		
		double nSentences = 0;
		double nTokens = 0;
		double nLetters = 0;
		
		Iterator itLet = aJCas.getAllIndexedFS(NLetter.class);
		if(itLet.hasNext()) {
			NLetter nLet = (NLetter)itLet.next();
			nLetters = nLet.getValue();
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
			if (annot.getType().getName().endsWith("NLetter")){
				nLetters = annot.getValue();
			}else if (annot.getType().getName().endsWith("NSentence")){
				nSentences = annot.getValue();
			}else if (annot.getType().getName().endsWith("NToken")){
				nTokens = annot.getValue();
			}
		}
		*/
		
		// Debugging
		//System.out.println("nSentences: " + nSentences);
		//System.out.println("nLetters: " + nLetters);
		//System.out.println("nTokens: " + nTokens);
		
		double gulpease = 89 + ( ( 300 * nSentences - 10 * nLetters ) / nTokens);
		//output the feature type
		Gulpease annotation = new Gulpease(aJCas);
		//set the feature ID
		annotation.setId(aeID);

		//set feature value
		annotation.setValue(gulpease);
		annotation.addToIndexes();

		logger.info(new PopulatedFeatureValueMessage(aeID, gulpease));
	}
	

	@Override
	public void destroy() {
		logger.trace(LogMarker.UIMA_MARKER, new DestroyingAEMessage(aeType, aeName));

		super.destroy();

		logger.trace(LogMarker.UIMA_MARKER, new DestroyAECompleteMessage(aeType, aeName));
	}
}

