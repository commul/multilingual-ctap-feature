package com.ctapweb.feature.annotator;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;

import com.ctapweb.feature.exception.CTAPException;
import com.ctapweb.feature.logging.LogMarker;
import com.ctapweb.feature.logging.message.AEType;
import com.ctapweb.feature.logging.message.DestroyAECompleteMessage;
import com.ctapweb.feature.logging.message.DestroyingAEMessage;
import com.ctapweb.feature.logging.message.InitializeAECompleteMessage;
import com.ctapweb.feature.logging.message.InitializingAEMessage;
import com.ctapweb.feature.logging.message.LoadLangModelMessage;
import com.ctapweb.feature.logging.message.ProcessingDocumentMessage;
import com.ctapweb.feature.type.Lemma;
import com.ctapweb.feature.type.Sentence;
import com.ctapweb.feature.type.Token;

import is2.data.SentenceData09;
import is2.lemmatizer.Lemmatizer;

import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerException;
//import de.unihd.dbs.heideltime.standalone.components.impl.TreeTaggerWrapper;
import org.annolab.tt4j.TreeTaggerWrapper;

public class LemmaAnnotator extends JCasAnnotator_ImplBase {

	//for pos tagger
	private CTAPLemmatizer lemmatizer;
	public static String LEMMA_RESOURCE_KEY = "LemmaModel";

	private static final String PARAM_LANGUAGE_CODE = "LanguageCode";
	private static String lCode = "";
	private static final Logger logger = LogManager.getLogger();
	private static final AEType aeType = AEType.ANNOTATOR;
	private static final String aeName = "Lemma Annotator";

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		logger.trace(LogMarker.UIMA_MARKER, new InitializingAEMessage(aeType, aeName));
		super.initialize(aContext);

		String lemmaModelFilePath = null;
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
		
		if (lCode.equals("DE")){

			//init lemmatizer
			String languageSpecificResourceKey = LEMMA_RESOURCE_KEY+lCode;
			try {
				lemmaModelFilePath = getContext().getResourceFilePath(languageSpecificResourceKey);

				logger.trace(LogMarker.UIMA_MARKER, 
					new LoadLangModelMessage(languageSpecificResourceKey, lemmaModelFilePath));

				lemmatizer = new MateLemmatizer(lemmaModelFilePath);
				// add switch statement here to allow for different instantiations; see example in ParseTreeAnnotator.java

			} catch (ResourceAccessException e) {
				logger.throwing(e);
				throw new ResourceInitializationException("could_not_access_data",
					new Object[] {lemmaModelFilePath}, e);
			} catch (IOException e) {
				logger.throwing(e);
				throw new ResourceInitializationException(CTAPException.EXCEPTION_DIGEST, 
					"file_io_error",
					new Object[] {lemmaModelFilePath}, e);
			}
		
		}else if (lCode.equals("IT")){
			
		}

		logger.trace(LogMarker.UIMA_MARKER, new InitializeAECompleteMessage(aeType, aeName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.
	 * apache.uima.jcas.JCas)
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		logger.trace(LogMarker.UIMA_MARKER, 
				new ProcessingDocumentMessage(aeType, aeName, aJCas.getDocumentText()));

		//iterate through all sentences
		Iterator sentIter = aJCas.getAnnotationIndex(Sentence.type).iterator();
		while (sentIter.hasNext()) {
			Sentence sent = (Sentence) sentIter.next();
			int sentStart = sent.getBegin();
			int sentEnd = sent.getEnd();
			List<Token> sentTokens = new ArrayList<>();
			
			//iterate through all tokens
			Iterator tokenIter = aJCas.getAnnotationIndex(Token.type).iterator(false);
			while(tokenIter.hasNext()) {
				Token token = (Token) tokenIter.next();
				if(token.getBegin() >= sentStart && token.getEnd() <= sentEnd) {
					sentTokens.add(token);
				}
			}
			
			String[] lemmas = new String[sentTokens.size()];
			
			if (lCode.equals("DE")){
				//get lemmas
				lemmas = lemmatizer.lemmatize(sentTokens);
			}else if (lCode.equals("IT")){
				lemmas = lemmatizeItalian(sentTokens);
			}
			
			//populate the CAS
			for(int i = 0; i < lemmas.length; i++) {  
				Token token = sentTokens.get(i);
				Lemma annotation = new Lemma(aJCas);
//				logger.trace(LogMarker.UIMA_MARKER, "Adding Lemma: "+token.getCoveredText()+" "+lemmas[i]);  // debugging
				annotation.setBegin(token.getBegin()); 
				annotation.setEnd(token.getEnd());
				annotation.setLemma(lemmas[i]);
				annotation.addToIndexes();
			}
		}

	}
	
	private String[] lemmatizeItalian(List<Token> sentTokens){
		System.setProperty("treetagger.home", "/home/nadiushka/treetagger");     
        
		ArrayList<String> ttResultArrayL = new ArrayList<String>();
		
        TreeTaggerWrapper tt = new TreeTaggerWrapper();
        try {
            tt.setModel("/home/nadiushka/treetagger/italian-utf8.par:utf8");
            
            TokenHandler tokenHandler = new TokenHandler<String>() {
                public void token(String token, String pos, String lemma) {
                    //System.out.println(token+"\t"+pos+"\t"+lemma);
                    //String[] tokenResultArray = {token,pos,lemma};
                    //ttResultArrayL.add(tokenResultArray);
                    ttResultArrayL.add(lemma);
                    //return tokenResultArray;
                }
            };
            
            tt.setHandler(tokenHandler);
            
            //System.out.println("has set TreeTagger");
            //tt.process(asList(new String[] {"Quattro", "gattini", "giocavano", "a", "calcetto", "."}));
            
            String[] sentTokensToTreeTag = sentTokens.toArray(new String[sentTokens.size()]);
            tt.process(asList(sentTokensToTreeTag));
        }
        catch (TreeTaggerException e){
        	logger.throwing(e);
        }
        catch (IOException e){
        	logger.throwing(e);
        }
        
        finally {
            tt.destroy();
        }
        
        //String[] list2 = new String[ttResultArrayL.size()];
        //list2 = ttResultArrayL.toArray(list2);
        
        String[] arrayResult = ttResultArrayL.toArray(new String[ttResultArrayL.size()]);
        
        return arrayResult;
	}

	@Override
	public void destroy() {
		logger.trace(LogMarker.UIMA_MARKER, new DestroyingAEMessage(aeType, aeName));
		super.destroy();
		logger.trace(LogMarker.UIMA_MARKER, new DestroyAECompleteMessage(aeType, aeName));
	}

	/**
	 * Interface for lemmatizer; acts as wrapper for any lemmatizer that may be 
	 * added to support new languages or to change existing lemmatizing components.
	 * @author zweiss
	 */
	interface CTAPLemmatizer {
		abstract String[] lemmatize(List<Token> tokenizedSentence);
	}

	/**
	 * Wrapper for use of Mate lemmatizer
	 * @author zweiss
	 */
	private class MateLemmatizer implements CTAPLemmatizer {
		
		private Lemmatizer mateLemmatizer;
		
		public MateLemmatizer(String modelInFile) throws IOException {
//			is2.lemmatizer.Options optsLemmatizer = new is2.lemmatizer.Options(new String[]{"-model", modelInFile}); 
//			mateLemmatizer = new Lemmatizer(optsLemmatizer);
			mateLemmatizer = new Lemmatizer(modelInFile);
		}
		
		@Override
		public String[] lemmatize(List<Token> sentTokens) {
			String[] tokens = new String[sentTokens.size()+1];
			tokens[0] = "<root>";  // Mate tools expect root token
			for (int i = 0; i < sentTokens.size(); i++) {
				tokens[i+1] = sentTokens.get(i).getCoveredText();
			}
			return lemmatize(tokens);
		}

		/**
		 * Takes input with root element at initial position
		 */
		public String[] lemmatize(String[] tokens) {
			SentenceData09 inputSentenceData = new SentenceData09();
			inputSentenceData.init(tokens);
			return lemmatize(inputSentenceData);
		}

		public String[] lemmatize(SentenceData09 inputSentenceData) {
			SentenceData09 lemmatizedSentence = mateLemmatizer.apply(inputSentenceData);
			return lemmatizedSentence.plemmas;
		}
	}
}
