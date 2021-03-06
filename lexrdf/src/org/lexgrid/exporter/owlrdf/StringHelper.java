/*
 * Copyright: (c) 2004-2010 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 * 
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * 		http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package org.lexgrid.exporter.owlrdf;

import java.util.Map;

import org.LexGrid.LexBIG.Extensions.Load.OntologyFormat;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * We need the StringHelp only because some property is not well format.
 * This helper class is use to extract the owl property types or uri
 * from a given string
 * Now it can handle the cases: 1) owl:xxx;
 * 2) DefaultOWLObjectProperty(http://www.co-ode.org/ontologies/pizza/2005/05/
 * 16/pizza.owl#hasIngredient)
 * 
 * 
 * @author m077995
 *
 */
public class StringHelper {
	final private OntologyFormat ontFormat = OntologyFormat.OWLRDF;
	private String str;
	private Map<String, String> supportedNamespace;
	
	private Resource type;
	private String value;
	
	public enum StrFormat {TYPE_VALUE, PREFIX_TYPE};
	
	private StrFormat strFormat;

	public StringHelper(String input, Map<String, String> supportedNamespace) {
		str = input.trim();
		this.supportedNamespace = supportedNamespace;
		processStr();
	}
	
	private void processStr() {
		// Rule #1, we assume that the thing in () is the value, the thing before ( is the type
		if (str.contains("(") && str.contains(")")) {
			int startIndex = str.indexOf("(") + 1, endIndex = str.indexOf(")");
			value = str.substring(startIndex, endIndex);
			
			type = LexRdfMap.get(str.substring(0, startIndex-1), ontFormat);
			
			if (value != null && type != null)
				strFormat = StrFormat.TYPE_VALUE;

		}
		// Rule #2, we assume that the string can owl:type
		else { 
			String[] lines = str.split(":");
			if(lines.length == 2 && supportedNamespace.containsKey(lines[0])) {
				type = LexRdfMap.get(lines[1], ontFormat);
				strFormat = StrFormat.PREFIX_TYPE;
			}
		}
	}
	
	public StrFormat getStrFormat() {
		return strFormat;
	}
	
	public String getStr() {
		return str;
	}
	
	public void setStr(String str) {
		this.str = str;
	}
	
	public Resource getType() {
		return type;
	}
	
	public String getValue() {
		return value;
		
	}
	
	
	public static void main(String[] args) {
		StringHelper sh = new StringHelper("DefaultOWLObjectProperty(http://www.co-ode.org/ontologies/pizza/2005/05/16/pizza.owl#isIngredientOf)", null);
		System.out.println("type: " + sh.getType());
		System.out.println("value: " + sh.getValue());
	}
}
