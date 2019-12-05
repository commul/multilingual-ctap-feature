package com.ctapweb.feature.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;

public class Gulpease_Type extends ComplexityFeatureBase_Type {
	/** @generated */
	  @SuppressWarnings ("hiding")
	  public final static int typeIndexID = Gulpease.typeIndexID;
	  /** @generated 
	     @modifiable */
	  @SuppressWarnings ("hiding")
	  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("com.ctapweb.feature.type.Gulpease");



	  /** initialize variables to correspond with Cas Type and Features
		 * @generated
		 * @param jcas JCas
		 * @param casType Type 
		 */
	  public Gulpease_Type(JCas jcas, Type casType) {
	    super(jcas, casType);
	    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

	  }
}
