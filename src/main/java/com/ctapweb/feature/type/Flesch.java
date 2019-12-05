package com.ctapweb.feature.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

public class Flesch extends ComplexityFeatureBase {
	/** @generated
	   * @ordered 
	   */
	  @SuppressWarnings ("hiding")
	  public final static int typeIndexID = JCasRegistry.register(Flesch.class);
	  /** @generated
	   * @ordered 
	   */
	  @SuppressWarnings ("hiding")
	  public final static int type = typeIndexID;
	  /** @generated
	   * @return index of the type  
	   */
	  @Override
	  public              int getTypeIndexID() {return typeIndexID;}
	 
	  /** Never called.  Disable default constructor
	   * @generated */
	  protected Flesch() {/* intentionally empty block */}
	    
	  /** Internal - constructor used by generator 
	   * @generated
	   * @param addr low level Feature Structure reference
	   * @param type the type of this Feature Structure 
	   */
	  public Flesch(int addr, TOP_Type type) {
	    super(addr, type);
	    readObject();
	  }
	  
	  /** @generated
	   * @param jcas JCas to which this Feature Structure belongs 
	   */
	  public Flesch(JCas jcas) {
	    super(jcas);
	    readObject();   
	  } 

	  /** 
	   * <!-- begin-user-doc -->
	   * Write your own initialization here
	   * <!-- end-user-doc -->
	   *
	   * @generated modifiable 
	   */
	  private void readObject() {/*default - does nothing empty block */}
}

