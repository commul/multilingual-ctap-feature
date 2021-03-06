/**
 * 
 */
package com.ctapweb.feature.annotator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.ctapweb.feature.annotator.LemmaAnnotator.CTAPLemmatizer;
import com.ctapweb.feature.logging.LogMarker;
import com.ctapweb.feature.logging.message.AEType;
import com.ctapweb.feature.logging.message.DestroyAECompleteMessage;
import com.ctapweb.feature.logging.message.DestroyingAEMessage;
import com.ctapweb.feature.logging.message.InitializeAECompleteMessage;
import com.ctapweb.feature.logging.message.InitializingAEMessage;
import com.ctapweb.feature.logging.message.ProcessingDocumentMessage;
import com.ctapweb.feature.logging.message.SelectingLanguageSpecificResource;
import com.ctapweb.feature.type.Syllable;
import com.ctapweb.feature.type.Token;
import com.ctapweb.feature.util.SupportedLanguages;

import is2.lemmatizer.Lemmatizer;


/**
 * Annotates text with syllables for each token in the input text
 * Requires the following annotations: sentences, tokens (see SyllableAnnotatorTAE.xml)
 * 
 * Syllable annotation is done using SyllablePatterns. 
 * To add a new syllable annotation logic, create a new SyllablePattern.
 * 
 * @author xiaobin
 * 
 * zweiss 20/12/18 : added new syllable structures
 * Nadezda Okinina 16/09/19, 19/11/19 : added the annotator for Italian, added the SyllableAnnotator wrapper
 */
public class SyllableAnnotator extends JCasAnnotator_ImplBase {

	private JCas aJCas;
	private Token token;	
	private String lCode;
	private CTAPSyllableAnnotator syllableAnnotator;	
	
	private static final String PARAM_LANGUAGE_CODE = "LanguageCode";
	private static final Logger logger = LogManager.getLogger();
	private static final AEType aeType = AEType.ANNOTATOR;
	private static final String aeName = "Syllable Annotator";	

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		logger.trace(LogMarker.UIMA_MARKER, new InitializingAEMessage(aeType, aeName));
		super.initialize(aContext);

		// obtain language parameter and access language dependent resources
		lCode = "";
		if(aContext.getConfigParameterValue(PARAM_LANGUAGE_CODE) == null) {
			ResourceInitializationException e = new ResourceInitializationException("mandatory_value_missing", 
					new Object[] {PARAM_LANGUAGE_CODE});
			logger.throwing(e);
			throw e;
		} else {
			lCode = ((String) aContext.getConfigParameterValue(PARAM_LANGUAGE_CODE)).toUpperCase();
		}

		logger.trace(LogMarker.UIMA_MARKER, new SelectingLanguageSpecificResource(aeName, lCode));
		switch (lCode) {
		case SupportedLanguages.GERMAN:
			syllableAnnotator = new GermanSyllableAnnotator();
			break;
		case SupportedLanguages.ENGLISH:
			syllableAnnotator = new EnglishSyllableAnnotator();
			break;
		case SupportedLanguages.ITALIAN:
			syllableAnnotator = new ItalianSyllableAnnotator();
			break;
			// add new language here
		default:   // TODO reconsider default
			syllableAnnotator = new EnglishSyllableAnnotator();
			break;
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

		this.aJCas = aJCas;

		// get annotation indexes and iterator
		Iterator it = aJCas.getAnnotationIndex(Token.type).iterator();
		syllableAnnotator.annotateSyllables(it);
	}
	
	/**
	 * Wrapper for syllable annotators
	 * @author nokinina
	 */
	interface CTAPSyllableAnnotator {
		/*
		 * Takes as argument an iterator over tokens, because in some languages (like Italian) syllables may be distributed over two tokens:
		 * syllable margins do not correspond to token margins.
		 * Example: dell'acqua consists of 2 tokens: dell' and acqua and of 3 syllables: del-l'ac-qua
		 */
		abstract void annotateSyllables(Iterator it);
	}

	/**
	 * Wrapper for annotating Italian syllables
	 * @author nokinina
	 */
	private class ItalianSyllableAnnotator implements CTAPSyllableAnnotator {
		private String regexListOfWordsEndingInCiaGia;
		private String regexListOfWordsWithUi;
		private String V;
		private String C;
		private Pattern yup1;
		private Pattern yup2;
		private Pattern yup3;
		private Pattern yup4;
			
		
		public ItalianSyllableAnnotator() {
			V = "[aeiouàèéìòùáíóú]";
			C = "[b-df-hj-np-tv-z\'’]";
			yup1 = Pattern.compile("("+V+"*"+C+"+"+V+"+)("+C+V+")");
			yup2 = Pattern.compile("("+V+"*"+C+"+"+V+"+"+C+")("+C+")");
			yup3 = Pattern.compile("("+V+")([iìí][uùúeèé]?[aàáoòóuùúiìí])");
			yup4 = Pattern.compile("[^=]+");
			
			/*
			arrayListOfWordsEndingInCiaGia = new ArrayList<>(Arrays.asList( "macia", "lucia", "malacia","eutocia","farmacia",
					"distocia",	"alopecia","ossitocia","mascalcia","miomalacia","parafarmacia","osteomalacia",
					"mielomalacia","fitofarmacia","regia","magia","bugia","algia","gaggia","elegia","alogia",
					"teurgia","otalgia","omalgia","mialgia","energia","dilogia","coregia",
					"albagia","tenalgia","sinergia","rinalgia","pubalgia","notalgia","liturgia","letargia",
					"ialurgia","gonalgia","disergia","disergia","ulorragia","tarsalgia","strategia","sacralgia",
					"pedagogia","otorragia","ostealgia","nostalgia","nevralgia","nefralgia","metralgia","mastalgia",
					"lombalgia","geragogia","epatargia","epatalgia","emorragia","dorsalgia","dermalgia","demagogia",
					"cistalgia","chirurgia","artralgia","achiurgia","sternalgia","splenalgia","siderurgia","seborragia",
					"sciatalgia","scenopegia","rinorragia",	"rachialgia","psicagogia","proctalgia","ovarialgia",
					"orchialgia","odontalgia","normoergia",	"mistagogia","menorragia","meiopragia","isteralgia",
					"ischialgia","gastralgia","enteralgia",	"cefalalgia","cardialgia","bioenergia","uterorragia",
					"taumaturgia","steatopigia","onomaturgia","nefrorragia","metrorragia","metallurgia","ipofalangia",
					"epatorragia","elioenergia","brachialgia","splenorragia","pneumorragia","onfalorragia","iperfalangia",
					"fotoallergia","enterorragia","drammaturgia","autoallergia"));
			*/
			regexListOfWordsEndingInCiaGia = "^(a(log|lopec|utoallerg|lbagi[ae]|lg|rtralg|chiurg)|b(rachialg|ioenerg|ug)|"+
					"c(oreg|istalg|hirurg|efalalg|ardialg)|d(ilog|iserg|iserg|orsalg|ermalg|emagog|istoc|rammaturg)|"+
					"e(nerg|patarg|nteralg|utoc|leg|patorrag|lioenerg|patalg|morrag|nterorrag)|"+
					"f(armac|itofarmac|otoallerg)|"+
					"g(astralg|onalg|agg|eragog)|"+
					"i(perfalang|schialg|steralg|alurg|pofalang)|"+
					"l(uc|iturg|etarg|ombalg)|"+
					"m(ialg|ag|etralg|ielomalac|ascalc|iomalac|astalg|ac|istagog|enorrag|eioprag|alac|etrorrag|etallurg)|" +
					"n(ostalg|evralg|efralg|otalg|efrorrag|ormoerg)|"+
					"o(talg|ssitoc|steomalac|torrag|stealg|varialg|nomaturg|rchialg|dontalg|nfalorrag|malg)|" +
					"p(neumorrag|ubalg|edagog|sicagog|roctalg|arafarmac)|"+
					"r(eg|inorrag|achialg|inalg)|" +
					"t(eurg|enalg|arsalg|aumaturg)|" +
					"u(lorrag|terorrag)|" +
					"s(ternalg|plenalg|iderurg|eborrag|ciatalg|cenopeg|teatopig|plenorrag|trateg|acralg|inerg)" +
					")[iìí][ae]$";
			
			/*
			arrayListOfWordsWithUi = new ArrayList<>(Arrays.asList("lui","cui","luigi","suidi","pruina",
					"druida","costui","altrui","pituita","intuito","visnuita","pattuito","gratuito",
					"fortuito","fluidica","cotestui","circuito","nottuidi","toluidina",
					"costruito","suindicato","retribuito","destituito","costituito","contiguità",
					"superfluido","diluibilità","semigratuito","ricostituito","microcircuito",
					"gratuitamente","fortuitamente","cortocircuito","contribuimento"));
			*/
			regexListOfWordsWithUi = "^(altrui|c(ui|ostui|otestui|ircuito|ostruit[aeio]|ostituit[aeio]|ontiguità|ortocircuito|ontribuimento)|"+
					"d(ruida|estituit[aeio]|iluibilit[aà])|"+
					"f(ortuit([aeio]|amente)|luidica)|"+
					"gratuit([aeio]|amente)|"+
					"intuito|"+
					"lui(gi)?|"+
					"microcircuit[oi]|"+
					"nottuidi|"+
					"p(attuito|ruina|ituita)|"+
					"r(etribuit[aeio]|icostituit[aeio])|"+
					"s(uperfluid[aeio]|uidi|uindicat[aeio]|emigratuit([aeio]|amente))|"+
					"toluidina|"+
					"visnuita"+
					")$";
		}
		
		@Override
		public void annotateSyllables(Iterator it) {
			StringBuilder tokenStringBuilder = new StringBuilder();
			int tokenGetbegin = -1;
			String tokenStr;
			
			//annotate syllables for each token 
			while(it.hasNext()) {			
				//get the token
				token = (Token)it.next();
				tokenStr = token.getCoveredText().toLowerCase();
				//annotate syllables
				// if a token ends with l', it is not a separate tonic word and has to be analyzed together with the following word
				if(Pattern.compile("l'$").matcher(tokenStr).find()){ 
					tokenStringBuilder.append(tokenStr);
					tokenGetbegin = token.getBegin();
				}else{
					tokenStringBuilder.append(tokenStr);
					annotateSyllablesItalian(tokenStringBuilder.toString(), tokenGetbegin);
					tokenStringBuilder.setLength(0);
					tokenGetbegin = -1;
				}
			}			
		}
		
		/**
		 * Annotates the Italian syllables in the Italian token string. 
		 * 
		 * Based on the code of Lingua::IT:Hyphenate written by Aldo Calpini and found on https://it.comp.programmare.narkive.com/TExPlcuC/programma-di-sillabazione
		 * First '=' sign is inserted between syllables with the help of different rules based on regular expressions, then the syllables are registered.
		 * Nadezda Okinina added some extra rules to this code
		 * Italian syllabation depends on the accent and accent is not marked in writing.
		 * For that reason in many cases word lists should be used in order to divide words and word groups into syllables correctly.
		 * Some such lists were used, but they are not exhaustive.
		 * The division into syllables is correct in most cases, but not in every single case. There may be some mistakes.
		 * 
		 * if a word ends with an l followed by an apostrophe, it can’t be a tonic entity of its own and has to be analysed together with the following word.
		 * Example: dall’alto: dal-lal-to In such cases the hyphen is removed.
		 * 
		 * In certain cases allows for variation with accents. (Accents are compulsory in Italian only in certain cases (città).)
		 
		 * Single vowels: aeiouyàèòùìéáíóúÁÉÍÓÚÀÈÌÒÙ
		 * 
		 * Dittonghi:
		 * ià, iè, iò, iù :	piàtto, fièno, fiòre, fiùme
		 * uà, uè, uì, uò :	guàsto, guèrra, guìda, fuòri
		 * ài, àu :	dirài, càusa
		 * èi, èu :	nèi, nèutro
		 * òi :	vòi
		 * 
		 * Trittonghi:
		 * iài :	soffiài
		 * ièi :	mièi
		 * uài :	guài
		 * uòi :	buòi
		 * iuò :	aiuòla
		 *  
		 * Numbers written as digits are counted as 1 syllable.
		 * 
		 * @param tokenStr the string to be split into syllables, may contain more than 1 token (a noun and its article, for example)
		 * @param tokenGetbegin the index of the start of the current token compared to the current string (which may contain the previous token: an article, for example)
		 * @return void
		 */
		private void annotateSyllablesItalian(String tokenStr, int tokenGetbegin) {
			String str = tokenStr.toLowerCase();
			int offset = 0;
			// If the word is composed of 2 words with a hyphen, split it into separate words
			if (Pattern.compile("\\p{Pd}").matcher(str).find()){
				String[] parts = str.split("\\p{Pd}");
				for(String part : parts){
					annotateSyllablesItalianWord(part, offset, tokenGetbegin);
					offset += part.length()+1;
				}
			}else{
				annotateSyllablesItalianWord(str, offset, tokenGetbegin);
			}
		}
		
		/*
		 * @param str the string to be split into syllables. May be a part of a token (in case of compound words written with a hyphen)
		 * @param offset the index of the start of the current string compared to the start of the current token (in case of a second word of a compound written with a hyphen will be different from zero)
		 * @param tokenGetbegin the index of the start of the current token compared to the current string (which may contain the previous token: an article, for example)
		 * @return void
		 */
		private void annotateSyllablesItalianWord(String str, int offset, int tokenGetbegin) {
			// If the word is part of the list of words ending with -cia / -gia with accentuated -i-, we separate the final -a as a separate syllable
			if (str.matches(regexListOfWordsEndingInCiaGia)){
				str = str.replaceAll("([aàá])$", "=$1");
			}else if( str.matches(regexListOfWordsWithUi)){
				str = str.replaceAll("([uùú])([iìí])", "$1=$2");
			}

			str = str.replaceAll("("+V+")([bcfgptv][lr])", "$1=$2");		
			str = str.replaceAll("("+V+")([cg]h)", "$1=$2");		
			str = str.replaceAll("("+V+")(gn)", "$1=$2");		
			str = str.replaceAll("("+C+")\\1", "$1=$1");		
			str = str.replaceAll("(s"+C+")", "=$1");			

			Matcher m = yup1.matcher(str);

			while (m.find()) {
				str = str.replaceAll("("+V+"*"+C+"+"+V+"+)("+C+V+")", "$1=$2");
				m = yup1.matcher(str);
			}			
			
			m = yup2.matcher(str);

			while (m.find()) {
				str = str.replaceAll("("+V+"*"+C+"+"+V+"+"+C+")("+C+")", "$1=$2");
				m = yup2.matcher(str);
			}

			str = str.replaceAll("^("+V+"+"+C+")("+C+")", "$1=$2");		
			str = str.replaceAll("^("+V+"+)("+C+V+")", "$1=$2");			
			str = str.replaceAll("^=", "");
			str = str.replaceAll("=$", "");
			str = str.replaceAll("=+", "=");

			// Short 2 syllable word starting with consonants and ending with ua, ue, uo  (suo, mia ecc.) Non sono sicura di questa regola!!!
			if(!str.equals("quo") || !str.equals("quò") || !str.equals("quó")){
				str = str.replaceAll("^("+C+"+)([uùúiìí])([aàáeèéoòó])$", "$1$2=$3");
			}

			// Prefixes bi-, tri-, ri- ecc.
			str = str.replaceAll("^(b[iìí]|r[eèéiiìí]|tr[iìí]|c[oòó]|[aàá]nt[eèéiìí]|c[oòó]n=?tr[aàáoòó]|[iìí]=?p[oòó]|m[eèé]=?t[aàá]|m[iìíaàá]=?cro|[tf]r[aàá]|d[eèé]|str[aàá]|s[oòó]t=?t[oòó]|s[oòó][pv]=?r[aàá]|s[eèé]=?m[iìí]|[eèé]=?m[iìí]|r[eèé]t=?ro|pr[oòó]|[iìí]n=?fr[aàá]|v[iìí]=?c[eèé])("+V+")", "$1=$2");

			//La finale dei verbi in ire
			str = str.replaceAll("[^gqc](" + V + ")([iìí]=?r[eèé]|[iìí]=?r=?s[iìí])$", "$1=$2");
			
			// Words that finish with -logia, -plegia and -fagia - scientific words - have the accent on i di logia -> lo-gi-a
			str = str.replaceAll("((lo|fa|ple|al=?ler)=?g[iìí])([aàá])$", "$1=$3");		
			
			// Words that finish with -ismo, -ista  on i -> i-smo, i-sta
			str = str.replaceAll("([iìí])(sm[oòó]|st[aàá]|t[iìí]=?c[oòóaàá]|b[iìí]=?l[eèéiìí])$", "$1=$2");		
					
			// Vocaboli con il maggior numero di vocali consecutive
			/*
							ghiaiaiuolo
							cuoiaiuolo
							cuoiaio
							stuoiaio
							ghiaiaio
							troiaio
			 */			
			
			m = yup3.matcher(str);

			while (m.find()) {
				str = str.replaceAll("("+V+")([iìí][uùúeèé]?[aàáoòóuùúiìí])", "$1=$2");
				m = yup3.matcher(str);
			}

			// aiu _> a=iu (aiuola)
			str = str.replaceAll("([aàá])([iìí][uùú])", "$1=$2");

			str = str.replaceAll("([aàáeèéo])([aàáeèéoòó])", "$1=$2");
			str = str.replaceAll("([iìí])([iìí])", "$1=$2");

			yup4 = Pattern.compile("[^=]+");
			m = yup4.matcher(str);
			
			if (tokenGetbegin == -1){
				tokenGetbegin = token.getBegin();
			}
			
			
			int numberOfEquals = -1;
			while (m.find()) {
				numberOfEquals += 1;
				//finds a syllable
				Syllable annotation = new Syllable(aJCas);
				
				annotation.setBegin(m.start() + offset - numberOfEquals + tokenGetbegin);
				annotation.setEnd(m.end() + offset - numberOfEquals + tokenGetbegin);
				
				//If the syllable is not one consonant or punctuation
				if(!annotation.getCoveredText().matches("^[b-df-hj-np-tv-z]$") && !annotation.getCoveredText().matches("^\\p{Punct}$")){
					annotation.addToIndexes();			
					//logger.info("syllable: " + annotation.getBegin() + ", " + annotation.getEnd() + " \'"  + annotation.getCoveredText()+"\'");
				}
			}
		}
	}
	
	private class GermanSyllableAnnotator implements CTAPSyllableAnnotator {
		private String syllablePattern;
		private boolean considerSilentE;
		private Pattern splitter;
		
		public GermanSyllableAnnotator() {
			syllablePattern = SyllablePatterns.GERMAN;
			splitter = Pattern.compile(syllablePattern);
			considerSilentE = false;
		}
		
		@Override
		public void annotateSyllables(Iterator it) {
			String tokenStr;
			while(it.hasNext()) {			
				//get the token
				token = (Token)it.next();
				tokenStr = token.getCoveredText().toLowerCase();
				//annotate syllables
				Matcher m = splitter.matcher(tokenStr);

				while (m.find()) {
					//finds a syllable
					Syllable annotation = new Syllable(aJCas);
					annotation.setBegin(m.start() + token.getBegin());
					annotation.setEnd(m.end() + token.getBegin());
					annotation.addToIndexes();
					//logger.info("syllable: " + annotation.getBegin() + ", " + annotation.getEnd() + " \'"  + annotation.getCoveredText()+"\'");
				}
			}
		}
	}
	
	private class EnglishSyllableAnnotator implements CTAPSyllableAnnotator {
		private String syllablePattern;
		private boolean considerSilentE;
		
		public EnglishSyllableAnnotator() {
			syllablePattern = SyllablePatterns.ENGLISH;
			considerSilentE = true;
		}
		
		@Override
		public void annotateSyllables(Iterator it) {
			Token token;
			String tokenStr;
			//annotate syllables for each token 
			while(it.hasNext()) {			
				//get the token
				token = (Token)it.next();
				tokenStr = token.getCoveredText().toLowerCase();
				//annotate syllables
				if (tokenStr.charAt(tokenStr.length()-1) == 'e' && silente(tokenStr)){
					String newToken= tokenStr.substring(0, tokenStr.length()-1); //deal with the rest of word
					annotateSyllablesEnglish(token, newToken);
				}else{
					annotateSyllablesEnglish(token, tokenStr);				
				}
			}			
		}
		
		/**
		 * Annotates the syllables in the token string. 
		 * 
		 * @param token the token to be annotated
		 * @param tokenStr the token string to be annotated
		 * @return
		 */
		private void annotateSyllablesEnglish(Token token, String tokenStr){
			Pattern splitter = Pattern.compile(syllablePattern);
			Matcher m = splitter.matcher(tokenStr);

			while (m.find()) {
				//finds a syllable
				Syllable annotation = new Syllable(aJCas);
				annotation.setBegin(m.start() + token.getBegin());
				annotation.setEnd(m.end() + token.getBegin());
				annotation.addToIndexes();
				//logger.info("syllable: " + annotation.getBegin() + ", " + annotation.getEnd() + " \'"  + annotation.getCoveredText()+"\'");
			}
		}
	}


	private boolean silente(String word) {
		word = word.substring(0, word.length()-1);

		Pattern yup = Pattern.compile("[aeiouy]");
		Matcher m = yup.matcher(word);

		return m.find() ? true: false;
	}

	@Override
	public void destroy() {
		logger.trace(LogMarker.UIMA_MARKER, new DestroyingAEMessage(aeType, aeName));
		super.destroy();
		logger.trace(LogMarker.UIMA_MARKER, new DestroyAECompleteMessage(aeType, aeName));
	}

	/*
	 * Defines syllable patterns which may be chosen upon initialization based on the language parameter transported in the UimaContext
	 * @author zweiss
	 * TODO reconsider usage
	 */
	public class SyllablePatterns {
		public static final String ENGLISH = "[^aeiouy]*[aeiouy]+";
		// German syllables: each vowel indicates its own syllable unless it is followed by a) itself or b) e i u y
		public static final String GERMAN = "[^aeiouöüäAEIOUÖÜÄ]*([aeiouöüäyAEIOUÖÜÄY])([eiuy]|\1)?[^aeiouöüäAEIOUÖÜÄ]*";  
		// default
		public static final String DEFAULT = SyllablePatterns.ENGLISH;
	}
}

