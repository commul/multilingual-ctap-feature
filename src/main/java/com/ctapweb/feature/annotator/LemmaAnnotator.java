package com.ctapweb.feature.annotator;

import static java.util.Arrays.asList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.FileSystems;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.attribute.FileAttribute;
//import java.nio.file.attribute.PosixFilePermission;
//import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.annolab.tt4j.TokenHandler;
//import org.annolab.tt4j.TreeTaggerWrapper;
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
//import com.ctapweb.feature.util.EnglishWordCategories;
//import com.ctapweb.feature.util.GermanWordCategories;
//import com.ctapweb.feature.util.ItalianWordCategories;
//import com.ctapweb.feature.util.TintReadableStringTransformer;

import is2.data.SentenceData09;
import is2.lemmatizer.Lemmatizer;

import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;

//import com.sun.jna.Library;
//import com.sun.jna.Native;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
//import edu.stanford.nlp.trees.LabeledScoredTreeNode;
import edu.stanford.nlp.util.CoreMap;
import eu.fbk.dh.tint.runner.TintPipeline;
//import eu.fbk.dh.tint.runner.TintRunner;

/**
 * Annotates text with lemmas for each word in the input text
 * Requires the following annotations: sentences, tokens (see LemmaAnnotatorTAE.xml)
 * 
 * Lemmatization is done using the CTAPLemmatizer interface. 
 * To add a new lemmatizer, make sure to implement the CTAPLemmatizer interface.
 * 
 * @author zweiss
 *
 */
public class LemmaAnnotator extends JCasAnnotator_ImplBase {

	//for pos tagger
	private CTAPLemmatizer lemmatizer;
	private CTAPLemmatizer lemmatizer2; // The second lemmatizer is needed for the processing of complex Italian texts where the first lemmatizer fails. This way there is more chance to get a lemma.
	
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
		
		switch (lCode) {
		case "DE":
			String languageSpecificResourceKey = LEMMA_RESOURCE_KEY+lCode;
			try {
				lemmaModelFilePath = getContext().getResourceFilePath(languageSpecificResourceKey);

				logger.trace(LogMarker.UIMA_MARKER, 
					new LoadLangModelMessage(languageSpecificResourceKey, lemmaModelFilePath));
				
				// The second lemmatizer is needed for the processing of complex Italian texts where the first lemmatizer fails. This way there is more chance to get a lemma.
				// But I also have to instantiate the second lemmatizer for German for code coherence
				lemmatizer = new MateLemmatizer(lemmaModelFilePath);
				lemmatizer2 = new MateLemmatizer(lemmaModelFilePath);
				
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
			break;
		case "IT":
			try{							
				lemmatizer = new TintLemmatizer ();
				
				//The Tint lemmatizer fails sometimes. In such cases I use the TreeTagger lemmatizer. If it fails, too, I fill the lemmas with "NULL" strings.
				String treetaggerPath = getContext().getResourceFilePath("treetagger");
				logger.trace(LogMarker.UIMA_MARKER, new LoadLangModelMessage("it", "treetaggerPath : " + treetaggerPath));
				lemmatizer2 = new TreeTaggerLemmatizer(treetaggerPath);
				
			}catch(Exception e){
				logger.throwing(e);
			}
			break;
		case "EN":
		default:
			break;
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
			//The Italian Tint lemmatizer fails sometimes. In such cases I use the TreeTagger lemmatizer. If it fails, too, I fill the lemmas with "NULL" strings.
			try{
				lemmas = lemmatizer.lemmatize(sentTokens);
			}catch(Exception e){
				try{
					lemmas = lemmatizer2.lemmatize(sentTokens);
				}catch(Exception ex){
					Arrays.fill(lemmas, "NULL");
				}
			}
						
			//populate the CAS
			for(int i = 0; i < lemmas.length; i++) {  
				Token token = sentTokens.get(i);
				//logger.trace(LogMarker.UIMA_MARKER, "Adding Lemma: "+token.getCoveredText()+" "+lemmas[i]);  // debugging
				//System.out.println("Adding Lemma: "+token.getCoveredText()+" "+lemmas[i]);  // debugging
				
				Lemma annotation = new Lemma(aJCas);
				annotation.setBegin(token.getBegin()); 
				annotation.setEnd(token.getEnd());
				annotation.setLemma(lemmas[i]);
				annotation.addToIndexes();
			}
		}

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
		//abstract String[] lemmatizeString(String sentenceString);
	}

	/**
	 * Wrapper for use of Mate lemmatizer
	 * which is part of the Mate tools (https://www.ims.uni-stuttgart.de/forschung/ressourcen/werkzeuge/matetools.en.html)
	 * @author zweiss
	 */
	private class MateLemmatizer implements CTAPLemmatizer {
		
		private Lemmatizer mateLemmatizer;
		
		public MateLemmatizer(String modelInFile) throws IOException {
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
	
	/**
	 * Wrapper for use of Tint
	 * 
	 * @author nokinina
	 */
	private class TintLemmatizer implements CTAPLemmatizer {		
		private TintPipeline pipelineTint;
		private InputStream stream;
		private ByteArrayOutputStream baos;
		
		public TintLemmatizer() throws IOException {
			pipelineTint = new TintPipeline();
			// Load the default properties
			// see https://github.com/dhfbk/tint/blob/master/tint-runner/src/main/resources/default-config.properties
			try {
				pipelineTint.loadDefaultProperties();
			}
			catch (IOException e) { //TODO Auto-generated catch block
				e.printStackTrace();
			}

			pipelineTint.setProperty("annotators","ita_toksent, pos, ita_morpho, ita_lemma");
			
			// Load the models
			pipelineTint.load();
		}
		
		
		@Override
		public String[] lemmatize(List<Token> sentTokens) {
			ArrayList<String> lemmaList = new ArrayList();

			String tokenString;
			StringBuilder strB = new StringBuilder();

			for(Token token : sentTokens){
				tokenString = token.getCoveredText();
				strB.append(" " + tokenString);
			}

			String sentenceString = strB.toString();
			//System.out.println(sentenceString);  // debugging
			
			Annotation annotationTint = pipelineTint.runRaw(sentenceString);
			for (CoreMap sentence : annotationTint.get(CoreAnnotations.SentencesAnnotation.class)) {
	            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {		                
	                lemmaList.add(token.get(CoreAnnotations.LemmaAnnotation.class));
	                //System.out.println(token.get(CoreAnnotations.LemmaAnnotation.class));  // debugging
	            }
	        }
				
			String[] arrayResult = getStringArray(lemmaList);
			return arrayResult;
		}
		
		// Function to convert ArrayList<String> to String[] 
	    private String[] getStringArray(ArrayList<String> arr){	  
	        // declaration and initialise String Array 
	        String str[] = new String[arr.size()]; 
	  
	        // ArrayList to Array Conversion 
	        for (int j = 0; j < arr.size(); j++) { 	  
	            // Assign each value to String array 
	            str[j] = arr.get(j); 
	        }	  
	        return str; 
	    }
	}
	
	/**
	 * Wrapper for use of Tree Tagger lemmatizer
	 * 
	 * @author nokinina
	 */
	
	private class TreeTaggerLemmatizer implements CTAPLemmatizer {

		private TreeTaggerWrapper tt;

		public TreeTaggerLemmatizer(String treetaggerPath) throws IOException {

			String fileSep = File.separator;
			String newFilePath = treetaggerPath + fileSep + "bin" + fileSep + "tree-tagger";
			File fileObject = new File(newFilePath);				
			// Change permission as below.
			fileObject.setReadable(true);
			fileObject.setWritable(false);
			fileObject.setExecutable(true);

			System.setProperty("treetagger.home", treetaggerPath);
			tt = new TreeTaggerWrapper();

			//String fileSep = File.separator;
			String italianUtf8FilePath = treetaggerPath + fileSep + "italian-utf8.par:utf8";
			tt.setModel(italianUtf8FilePath);


		}

		@Override
		public String[] lemmatize(List<Token> sentTokens) {
			ArrayList<String> ttResultArrayL = new ArrayList<String>();
	        try {

	        	TokenHandler tokenHandler = new TokenHandler<String>() {
	                public void token(String token, String pos, String lemma) {
	                    ttResultArrayL.add(lemma);
	                }
	            };

	            tt.setHandler(tokenHandler);
	            String[] sentTokensToTreeTag = new String[sentTokens.size()];
	            int tokNumber = 0;
	            for (Token tok : sentTokens){
	            	//logger.trace(LogMarker.UIMA_MARKER, new LoadLangModelMessage("it", "tok line 189: " + tok.getCoveredText()));
	            	//System.out.println("tok line 189: " + tok.getCoveredText());
	            	sentTokensToTreeTag[tokNumber] = tok.getCoveredText();
	            	tokNumber += 1;
	            }

	            tt.process(asList(sentTokensToTreeTag));
	            tt.destroy();

	        }
	        catch (TreeTaggerException e){
	        	logger.throwing(e);
	        }
	        catch (IOException e){
	        	logger.throwing(e);
	        }

	        String[] arrayResult = ttResultArrayL.toArray(new String[ttResultArrayL.size()]);

	        return arrayResult;
		}
	}
	
}
