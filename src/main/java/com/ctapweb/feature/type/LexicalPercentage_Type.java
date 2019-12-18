package com.ctapweb.feature.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

public class LexicalPercentage_Type extends ComplexityFeatureBase_Type {
	/** @generated */
	  @SuppressWarnings ("hiding")
	  public final static int typeIndexID = LexicalPercentage.typeIndexID;
	  /** @generated 
	     @modifiable */
	  @SuppressWarnings ("hiding")
	  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("com.ctapweb.feature.type.LexicalPercentage");



	  /** initialize variables to correspond with Cas Type and Features
		 * @generated
		 * @param jcas JCas
		 * @param casType Type 
		 */
	  public LexicalPercentage_Type(JCas jcas, Type casType) {
	    super(jcas, casType);
	    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

	  }
}
