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
package org.cts2.internal.model.uri.factory;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.cts2.internal.model.uri.DefaultCodeSystemVersionDirectoryURI;
import org.cts2.internal.model.uri.restrict.IterableBasedResolvingRestrictionHandler;
import org.cts2.uri.CodeSystemVersionDirectoryURI;

/**
 * A factory for creating CodeSystemVersionDirectoryURIFactory objects.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class CodeSystemVersionDirectoryURIFactory extends AbstractCompositeDirectoryURIFactory<CodeSystemVersionDirectoryURI> {
	
	private IterableBasedResolvingRestrictionHandler<CodingSchemeRendering> restrictionHandler;

	/* (non-Javadoc)
	 * @see org.cts2.internal.uri.factory.AbstractDirectoryURIFactory#doGetDirectoryURI()
	 */
	@Override
	protected CodeSystemVersionDirectoryURI doBuildDirectoryURI() {
		CodingSchemeRenderingList codingSchemeRenderingList;
		
		try {
			codingSchemeRenderingList = this.getLexBigService().getSupportedCodingSchemes();
		} catch (LBInvocationException e) {
			throw new RuntimeException(e);
		}
		
		return new DefaultCodeSystemVersionDirectoryURI(codingSchemeRenderingList, this.restrictionHandler, this.getBeanMapper());
	}

	public void setRestrictionHandler(IterableBasedResolvingRestrictionHandler<CodingSchemeRendering> restrictionHandler) {
		this.restrictionHandler = restrictionHandler;
	}

	public IterableBasedResolvingRestrictionHandler<CodingSchemeRendering> getRestrictionHandler() {
		return restrictionHandler;
	}
}
