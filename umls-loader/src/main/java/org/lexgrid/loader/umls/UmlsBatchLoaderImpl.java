/*
 * Copyright: (c) 2004-2009 Mayo Foundation for Medical Education and 
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
package org.lexgrid.loader.umls;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import org.LexGrid.LexBIG.Extensions.Load.UmlsBatchLoader;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.lexevs.dao.database.spring.DynamicPropertyApplicationContext;
import org.lexevs.system.ResourceManager;
import org.lexgrid.loader.AbstractSpringBatchLoader;
import org.lexgrid.loader.properties.ConnectionPropertiesFactory;
import org.lexgrid.loader.properties.impl.DefaultLexEVSPropertiesFactory;
import org.lexgrid.loader.setup.JobRepositoryManager;
import org.lexgrid.loader.staging.StagingManager;

/**
 * The Class UmlsBatchLoaderImpl.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class UmlsBatchLoaderImpl extends AbstractSpringBatchLoader implements UmlsBatchLoader {

/** The connection properties factory. */
private ConnectionPropertiesFactory connectionPropertiesFactory = new DefaultLexEVSPropertiesFactory();

	/** The UML s_ loade r_ config. */
	private String UMLS_LOADER_CONFIG = "umlsLoader.xml";

	/* (non-Javadoc)
	 * @see org.lexgrid.loader.umls.UmlsBatchLoader#loadUmls(java.lang.String, java.lang.String)
	 */
	public void loadUmls(URI rrfDir, String sab) throws Exception {
		Properties connectionProps = connectionPropertiesFactory.getPropertiesForNewLoad(true);	
		connectionProps.put("sab", sab);
		connectionProps.put("rrfDir", rrfDir.toString());
		connectionProps.put("retry", "false");
		launchJob(connectionProps, UMLS_LOADER_CONFIG, "umlsJob");
	}	
	
	/* (non-Javadoc)
	 * @see org.lexgrid.loader.umls.UmlsBatchLoader#resumeUmls(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void resumeUmls(URI rrfDir, String sab, String uri, String version) throws Exception {
		Properties connectionProps = connectionPropertiesFactory.getPropertiesForExistingLoad(uri, version);
		connectionProps.put("sab", sab);
		connectionProps.put("rrfDir", rrfDir.toString());
		connectionProps.put("retry", "true");
		launchJob(connectionProps, UMLS_LOADER_CONFIG, "umlsJob");
	}
	
	/* (non-Javadoc)
	 * @see org.lexgrid.loader.umls.UmlsBatchLoader#removeLoad(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void removeLoad(URI rrfDir, String sab, String uri, String version) throws Exception {
		Properties connectionProps = connectionPropertiesFactory.getPropertiesForExistingLoad(uri, version);
		connectionProps.put("sab", sab);
		connectionProps.put("rrfDir", rrfDir.toString());
		connectionProps.put("retry", "true");
		
		DynamicPropertyApplicationContext ctx = new DynamicPropertyApplicationContext("umlsLoader.xml", connectionProps);
		
		JobRepositoryManager jobRepositoryManager = (JobRepositoryManager)ctx.getBean("jobRepositoryManager");
		jobRepositoryManager.dropJobRepositoryDatabases();
		
		StagingManager stagingManager = (StagingManager)ctx.getBean("umlsStagingManager");
		stagingManager.dropAllStagingDatabases();		
		
		ResourceManager.instance().removeCodeSystem(
				Constructors.createAbsoluteCodingSchemeVersionReference(uri, version));	
	}
    
    @Override
	public String getName() {
		return UmlsBatchLoader.NAME;
	}

    public static void main(String[] args) throws Exception { 
    	System.setProperty("LG_CONFIG_FILE", "src/test/resources/lbconfig.props");

    	UmlsBatchLoader ubl = new UmlsBatchLoaderImpl();
		 //ubl.loadUmls(new File("/home/LargeStorage/ontologies/rrf/snomed-ct/2009AA").toURI(), "SNOMEDCT");
		// mbl.loadMeta("/home/LargeStorage/ontologies/rrf/LNC/LNC226");
    	ubl.loadUmls(new File("src/test/resources/data/sample-air").toURI(), "AIR");
	 }
}
