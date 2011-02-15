/*
 * Copyright: (c) 2004-2011 Mayo Foundation for Medical Education and 
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
package org.cts2.internal.uri;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.cts2.core.Directory;
import org.cts2.internal.mapper.BeanMapper;
import org.cts2.uri.DirectoryURI;

/**
 * The Class AbstractLexEvsDirectoryURI.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public abstract class AbstractLexEvsDirectoryURI<T extends Directory> implements DirectoryURI<T> {
	
	/** The lex big service. */
	private LexBIGService lexBIGService;
	
	/** The bean mapper. */
	private BeanMapper beanMapper;
	
	/**
	 * Instantiates a new abstract lex evs directory uri.
	 *
	 * @param lexBIGService the lex big service
	 * @param beanMapper the bean mapper
	 */
	protected AbstractLexEvsDirectoryURI(LexBIGService lexBIGService, BeanMapper beanMapper){
		this.lexBIGService = lexBIGService;
		this.beanMapper = beanMapper;
	}

	/**
	 * Gets the lex big service.
	 *
	 * @return the lex big service
	 */
	protected LexBIGService getLexBIGService(){
		return lexBIGService;
	}

	/**
	 * Sets the lex big service.
	 *
	 * @param lexBIGService the new lex big service
	 */
	public void setLexBIGService(LexBIGService lexBIGService) {
		this.lexBIGService = lexBIGService;
	}

	/**
	 * Sets the bean mapper.
	 *
	 * @param beanMapper the new bean mapper
	 */
	public void setBeanMapper(BeanMapper beanMapper) {
		this.beanMapper = beanMapper;
	}

	/**
	 * Gets the bean mapper.
	 *
	 * @return the bean mapper
	 */
	public BeanMapper getBeanMapper() {
		return beanMapper;
	}
}