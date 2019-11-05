package com.ctapweb.feature.test.util;

import java.io.File;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Helper class for modifying XML descriptors for the use of UIMA tests.
 * @author Nadezda Okinina
 */
public class DescriptorModifier {
	/*
	 * Reads an existing annotator descriptor (XML file) and makes some modifications in it:
	 * - sets the LanguageCode parameter to "IT"
	 * - deletes the typeSystemDescription
	 * 
	 * Parameters:
	 * - filePath: the path to the existing annotator descriptor (XML file)
	 * - outputFilePath: the path to the file that will be created and will contain the modified annotator descriptor
	 * 
	 * Returns a file object containing the new XML annotator descriptor
	 */	
	public static File readXMLAnnotatorDescriptorAddLanguage (String filePath, String outputFilePath) throws Exception {
		//File fToReturn = new File(filePath);
		File fXmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		
		try {
			doc.getDocumentElement().normalize();
			//Get the already existing node "analysisEngineMetaData"
			NodeList nList = doc.getElementsByTagName("analysisEngineMetaData");
			
			//Create a new node "configurationParameterSettings" with the indication of the Italian language in the LanguageCode parameter
			Element newConfParamSettingsNode = doc.createElement("configurationParameterSettings");
	        Element nameValuePairNode = doc.createElement("nameValuePair");
	        newConfParamSettingsNode.appendChild(nameValuePairNode);	        
	        Element nameNode = doc.createElement("name");
	        nameValuePairNode.appendChild(nameNode);
	        Text nametextNode = doc.createTextNode("LanguageCode");
	        nameNode.appendChild(nametextNode);	        
	        Element valueNode = doc.createElement("value");
	        nameValuePairNode.appendChild(valueNode);
	        Element stringNode = doc.createElement("string");
	        valueNode.appendChild(stringNode);
	        Text langTextNode = doc.createTextNode("IT");
	        stringNode.appendChild(langTextNode);
	        
	        // Loop through all possible "analysisEngineMetaData" nodes: in reality only one
	        int temp;
	        int t;
			for (temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);						
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;					
					NodeList childrenList = eElement.getChildNodes();
					//Loop through all the children of "analysisEngineMetaData"
					for (t = 0; t < childrenList.getLength(); t++) {
						Node childNode = childrenList.item(t);						
						if (childNode.getNodeName().equals("configurationParameterSettings") || childNode.getNodeName().equals("typeSystemDescription") ){
							eElement.removeChild(childNode);
						}
						eElement.appendChild(newConfParamSettingsNode);
					}
				}
			}

		    } catch (Exception e) {
			e.printStackTrace();
		    }
		//Write the new XML to a new annotator descriptor in the META-INF folder only for the use of the test function
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		File fToReturn = new File(outputFilePath);
		FileWriter writer = new FileWriter(fToReturn);
		StreamResult result = new StreamResult(writer);
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
		//Return the file object
		return fToReturn;
	}
	
	/*
	 * Reads an existing type system descriptor (XML file) and modifies its imports section
	 * by providing the new location of the imported file.
	 * Writes the new type system descriptor to the specified location.
	 * Parameters:
	 * - filePath: the path to the existing type system descriptor (XML file)
	 * - outputFilePath: the path to the file that will be created and will contain the modified type system descriptor
	 * - location: the location of the file  that has to be imported in the imports section of the newly created type system descriptor
	 */
	public static void readXMLTypeDescriptorModifyImports (String filePath, String outputFilePath, String location) throws Exception {
		//File fToReturn = new File(filePath);
		File fXmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		
		try {
			doc.getDocumentElement().normalize();
			//Get the already existing node "typeSystemDescription"
			NodeList nList = doc.getElementsByTagName("typeSystemDescription");
			
			//Create a new node "imports" with the full path to the file location
			Element importsNode = doc.createElement("imports");
	        Element importNode = doc.createElement("import");
	        importNode.setAttribute("location", location);
	        importsNode.appendChild(importNode);       

	        // Loop through all possible "typeSystemDescription" nodes: in reality only one
	        int temp;
	        int t;
			for (temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);						
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;					
					NodeList childrenList = eElement.getChildNodes();
					//Loop through all the children of "analysisEngineMetaData"
					for (t = 0; t < childrenList.getLength(); t++) {
						Node childNode = childrenList.item(t);						
						if (childNode.getNodeName().equals("imports") ){
							eElement.removeChild(childNode);
						}
						eElement.appendChild(importsNode);
					}
				}
			}

		    } catch (Exception e) {
			e.printStackTrace();
		    }
		//Write the new XML to a new annotator descriptor in the META-INF folder only for the use of the test function
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		File fToReturn = new File(outputFilePath);
		FileWriter writer = new FileWriter(fToReturn);
		StreamResult result = new StreamResult(writer);
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
		
	}
}