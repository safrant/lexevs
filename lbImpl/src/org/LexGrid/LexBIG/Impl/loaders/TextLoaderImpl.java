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
package org.LexGrid.LexBIG.Impl.loaders;

import java.io.File;
import java.net.URI;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ExtensionDescription;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.types.ProcessState;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Extensions.Load.Loader;
import org.LexGrid.LexBIG.Extensions.Load.Text_Loader;
import org.LexGrid.LexBIG.Extensions.Load.options.OptionHolder;
import org.lexevs.dao.database.service.DatabaseServiceManager;
import org.lexevs.dao.database.service.exception.CodingSchemeAlreadyLoadedException;
import org.lexevs.locator.LexEvsServiceLocator;

import edu.mayo.informatics.lexgrid.convert.directConversions.TextToSQL;
import edu.mayo.informatics.lexgrid.convert.options.BooleanOption;
import edu.mayo.informatics.lexgrid.convert.options.StringOption;
import edu.mayo.informatics.lexgrid.convert.utility.URNVersionPair;

/**
 * Class to load a Text files into the LexBIG API.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @version subversion $Revision: $ checked in on $Date: $
 */
public class TextLoaderImpl extends BaseLoader implements Text_Loader {
    private static final long serialVersionUID = -4164433097047462000L;
    public static final String name = "TextLoader";
    private static final String description = "This loader loads LexGrid Text files into the LexGrid database.";
    
    public static final String DELIMITER_OPTION = "Delimiter";
    public static String FORCE_FORMAT_B_OPTION = "Force Format B";

    public TextLoaderImpl() {
        super();
    }

    public void validate(URI uri, Character delimiter, boolean triplesFormat, int validationLevel)
            throws LBParameterException {
       //
    }

    public void load(URI uri, Character delimiter, boolean readDoublesAsTriples, boolean stopOnErrors, boolean async)
            throws LBParameterException, LBInvocationException {
      //
    }

    @Override
    protected URNVersionPair[] doLoad() throws CodingSchemeAlreadyLoadedException {
  
            TextToSQL loader = new TextToSQL();
            
            URNVersionPair[] schemes =  loader.load(
                    this.getResourceUri(), 
                    this.getOptions().getStringOption(TextLoaderImpl.DELIMITER_OPTION).getOptionValue(), 
                    this.getLoaderPreferences(), 
                    this.getLogger(), 
                    this.getOptions().getBooleanOption(TextLoaderImpl.FORCE_FORMAT_B_OPTION).getOptionValue());
       
            this.getStatus().setState(ProcessState.COMPLETED);
            this.getStatus().setErrorsLogged(false);
            
            return schemes;
        
      
    }

    
    public static void main(String[] args){
        TextLoaderImpl loader = new TextLoaderImpl();
        loader.addBooleanOptionValue(TextLoaderImpl.FORCE_FORMAT_B_OPTION, true);
        
        loader.load(new File("C:\\workspaces\\lexevs60\\lgConverter\\commentedSamples\\textLoad_A.txt").toURI());
    }

    @Override
    protected OptionHolder declareAllowedOptions(OptionHolder holder) {
        BooleanOption forceFormatB = new BooleanOption(TextLoaderImpl.FORCE_FORMAT_B_OPTION, true);
        holder.getBooleanOptions().add(forceFormatB);
        
        StringOption delimeter = new StringOption(TextLoaderImpl.DELIMITER_OPTION);
        holder.getStringOptions().add(delimeter);
        return holder;
    }

    @Override
    protected ExtensionDescription buildExtensionDescription() {
        ExtensionDescription temp = new ExtensionDescription();
        temp.setExtensionBaseClass(TextLoaderImpl.class.getInterfaces()[0].getName());
        temp.setExtensionClass(TextLoaderImpl.class.getName());
        temp.setDescription(description);
        temp.setName(name);
        return temp;
    }
}