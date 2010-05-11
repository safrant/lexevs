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
package org.LexGrid.LexBIG.Impl.pagedgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Impl.CodedNodeSetImpl;
import org.LexGrid.LexBIG.Impl.pagedgraph.query.DefaultGraphQueryBuilder;
import org.LexGrid.LexBIG.Impl.pagedgraph.query.GraphQueryBuilder;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ServiceUtility;
import org.LexGrid.naming.SupportedContainerName;
import org.LexGrid.naming.SupportedProperty;
import org.lexevs.dao.database.service.codednodegraph.CodedNodeGraphService;
import org.lexevs.dao.database.service.codednodegraph.model.GraphQuery;
import org.lexevs.locator.LexEvsServiceLocator;

/**
 * The Class AbstractQueryBuildingCodedNodeGraph.
 * 
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public abstract class AbstractQueryBuildingCodedNodeGraph extends AbstractCodedNodeGraph {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1153282485482789848L;
    
    /** The builder. */
    private GraphQueryBuilder graphQueryBuilder;
    
    /** The coding scheme uri. */
    private String codingSchemeUri;
    
    /** The version. */
    private String version;
    
    /** The relations container name. */
    private String relationsContainerName;
    
    /**
     * Instantiates a new paging coded node graph impl.
     * 
     * @param codingSchemeUri the coding scheme uri
     * @param version the version
     * @param relationsContainerName the relations container name
     * @throws LBParameterException 
     */
    public AbstractQueryBuildingCodedNodeGraph(
            String codingSchemeUri, 
            String version,
            String relationsContainerName) throws LBParameterException {
        ServiceUtility.validateParameter(codingSchemeUri, version, relationsContainerName, SupportedContainerName.class);
        
        this.codingSchemeUri = codingSchemeUri;
        this.version = version;
        this.relationsContainerName = relationsContainerName;
  
        graphQueryBuilder = new DefaultGraphQueryBuilder(codingSchemeUri, version);
    }
    
    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph#areCodesRelated(org.LexGrid.LexBIG.DataModel.Core.NameAndValue, org.LexGrid.LexBIG.DataModel.Core.ConceptReference, org.LexGrid.LexBIG.DataModel.Core.ConceptReference, boolean)
     */
    @Override
    public Boolean areCodesRelated(NameAndValue association, ConceptReference sourceCode, ConceptReference targetCode,
            boolean directOnly) throws LBInvocationException, LBParameterException {
       boolean areRelatedForward = this.doGetAreCodesRelated(sourceCode, targetCode, association);
       
       if(areRelatedForward) {return true;}
       
       boolean areRelatedBackward = this.doGetAreCodesRelated(targetCode, sourceCode, association);
       
       if(areRelatedBackward) {return true;}
       
       return false;  
    }
    
    
    
    @Override
    public ResolvedConceptReferenceList doResolveAsList(ConceptReference graphFocus, boolean resolveForward,
            boolean resolveBackward, int resolveCodedEntryDepth, int resolveAssociationDepth,
            LocalNameList propertyNames, PropertyType[] propertyTypes, SortOptionList sortOptions,
            LocalNameList filterOptions, int maxToReturn, boolean keepLastAssociationLevelUnresolved)
            throws LBInvocationException, LBParameterException {
        
        ServiceUtility.validateParameter(this.getCodingSchemeUri(), this.getVersion(), propertyNames, SupportedProperty.class);
        
        return this.doResolveAsValidatedParameterList(
                graphFocus, resolveForward, resolveBackward, 
                resolveCodedEntryDepth, resolveAssociationDepth, 
                propertyNames, propertyTypes, sortOptions, 
                filterOptions, maxToReturn, keepLastAssociationLevelUnresolved);
    }
    
    protected abstract ResolvedConceptReferenceList doResolveAsValidatedParameterList(ConceptReference graphFocus, boolean resolveForward,
            boolean resolveBackward, int resolveCodedEntryDepth, int resolveAssociationDepth,
            LocalNameList propertyNames, PropertyType[] propertyTypes, SortOptionList sortOptions,
            LocalNameList filterOptions, int maxToReturn, boolean keepLastAssociationLevelUnresolved)
            throws LBInvocationException, LBParameterException;

    protected boolean doGetAreCodesRelated(
            ConceptReference sourceCode, 
            ConceptReference targetCode, 
            NameAndValue association) throws LBParameterException, LBInvocationException {

        GraphQueryBuilder builder = new DefaultGraphQueryBuilder(this.codingSchemeUri, this.version, getClonedGraphQuery());
        
        NameAndValueList nvl = new NameAndValueList();
        nvl.addNameAndValue(association);
        
        builder.restrictToAssociations(nvl, null);
        builder.getQuery().getRestrictToTargetCodes().add(Constructors.createConceptReference(targetCode.getCode(), targetCode.getCodeNamespace(), null));
        
        CodedNodeGraphService service = LexEvsServiceLocator.getInstance().
            getDatabaseServiceManager().
            getCodedNodeGraphService();
        
        Map<String,Integer> subjectCount = service.getTripleUidsContainingSubjectCount(
                codingSchemeUri, 
                version, 
                relationsContainerName, 
                sourceCode.getCode(), 
                sourceCode.getCodeNamespace(), 
                builder.getQuery());
        
        builder = new DefaultGraphQueryBuilder(this.codingSchemeUri, this.version, getClonedGraphQuery());

        builder.restrictToAssociations(nvl, null);
        builder.getQuery().getRestrictToSourceCodes().add(Constructors.createConceptReference(sourceCode.getCode(), sourceCode.getCodeNamespace(), null));
    
        Map<String,Integer> objectCount = service.getTripleUidsContainingObjectCount(
                codingSchemeUri, 
                version, 
                relationsContainerName, 
                targetCode.getCode(), 
                targetCode.getCodeNamespace(), 
                builder.getQuery());
        
        return !subjectCount.isEmpty() || objectCount.isEmpty();
    }
 
    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph#restrictToAssociations(org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList, org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList)
     */
    @Override
    public CodedNodeGraph restrictToAssociations(NameAndValueList association, NameAndValueList associationQualifiers)
            throws LBInvocationException, LBParameterException {
        this.graphQueryBuilder.restrictToAssociations(association, associationQualifiers);
        return this;
    }

    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph#restrictToCodeSystem(java.lang.String)
     */
    @Override
    public CodedNodeGraph restrictToCodeSystem(String codingScheme) throws LBInvocationException, LBParameterException {
        this.graphQueryBuilder.restrictToCodeSystem(codingScheme);
        return this;
    }

    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph#restrictToCodes(org.LexGrid.LexBIG.LexBIGService.CodedNodeSet)
     */
    @Override
    public CodedNodeGraph restrictToCodes(CodedNodeSet codes) throws LBInvocationException, LBParameterException {
        this.graphQueryBuilder.restrictToCodes(codes);
        return this;
    }

    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph#restrictToDirectionalNames(org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList, org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList)
     */
    @Override
    public CodedNodeGraph restrictToDirectionalNames(NameAndValueList directionalNames,
            NameAndValueList associationQualifiers) throws LBInvocationException, LBParameterException {
       this.graphQueryBuilder.restrictToDirectionalNames(directionalNames, associationQualifiers);
       return this;
    }

    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph#restrictToSourceCodeSystem(java.lang.String)
     */
    @Override
    public CodedNodeGraph restrictToSourceCodeSystem(String codingScheme) throws LBInvocationException,
            LBParameterException {
        this.graphQueryBuilder.restrictToSourceCodeSystem(codingScheme);
        return this;
    }

    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph#restrictToSourceCodes(org.LexGrid.LexBIG.LexBIGService.CodedNodeSet)
     */
    @Override
    public CodedNodeGraph restrictToSourceCodes(CodedNodeSet codes) throws LBInvocationException, LBParameterException {
        this.graphQueryBuilder.restrictToSourceCodes(codes);
        return this;
    }

    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph#restrictToTargetCodeSystem(java.lang.String)
     */
    @Override
    public CodedNodeGraph restrictToTargetCodeSystem(String codingScheme) throws LBInvocationException,
            LBParameterException {
        this.graphQueryBuilder.restrictToTargetCodeSystem(codingScheme);
        return this;
    }

    /* (non-Javadoc)
     * @see org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph#restrictToTargetCodes(org.LexGrid.LexBIG.LexBIGService.CodedNodeSet)
     */
    @Override
    public CodedNodeGraph restrictToTargetCodes(CodedNodeSet codes) throws LBInvocationException, LBParameterException {
        this.graphQueryBuilder.restrictToTargetCodes(codes);
        return this;
    }
    
    @Override
    public CodedNodeSet toNodeList(ConceptReference graphFocus, boolean resolveForward, boolean resolveBackward,
            int resolveAssociationDepth, int maxToReturn) throws LBInvocationException, LBParameterException {
        ResolvedConceptReferenceList list = 
            this.doResolveAsList(
                graphFocus, 
                resolveForward, 
                resolveBackward, 
                0, 
                resolveAssociationDepth,
                null, null, null, null, maxToReturn, false);
        
        ConceptReferenceList codeList = this.traverseGraph(list, resolveForward, resolveBackward);

        try {
            CodedNodeSet cns = new CodedNodeSetImpl(
                    this.getCodingSchemeUri(), 
                    Constructors.createCodingSchemeVersionOrTagFromVersion(this.getVersion()), null);
            
            cns = cns.restrictToCodes(codeList);
            
            return cns;
        } catch (LBResourceUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
    
    private ConceptReferenceList traverseGraph(ResolvedConceptReferenceList list, boolean resolveForward, boolean resolveBackward){
        List<ConceptReference> returnList = new ArrayList<ConceptReference>();
        
        for(ResolvedConceptReference ref : list.getResolvedConceptReference()) {
            returnList.addAll(traverseGraph(ref, resolveForward, resolveBackward));
        }
        
        ConceptReferenceList refList = new ConceptReferenceList();
        refList.setConceptReference(returnList.toArray(new ConceptReference[returnList.size()] ));
        
        return refList;
    }
    
    private List<ConceptReference> traverseGraph(ResolvedConceptReference ref, boolean resolveForward, boolean resolveBackward){
        List<ConceptReference> returnList = new ArrayList<ConceptReference>();
        returnList.add(ref);
        
        if(resolveForward) {
            if(ref.getSourceOf() != null) {
                for(Association assoc : ref.getSourceOf().getAssociation()) {
                    for(AssociatedConcept ac : assoc.getAssociatedConcepts().getAssociatedConcept()) {
                        returnList.addAll(
                                this.traverseGraph(ac, resolveForward, resolveBackward));
                    }
                }
            }
        }
        
        if(resolveBackward) {
            if(ref.getTargetOf() != null) {
                for(Association assoc : ref.getTargetOf().getAssociation()) {
                    for(AssociatedConcept ac : assoc.getAssociatedConcepts().getAssociatedConcept()) {
                        returnList.addAll(
                                this.traverseGraph(ac, resolveForward, resolveBackward));
                    }
                }
            }
        }
        
        return returnList;
    }

    /**
     * Gets the graph query builder.
     * 
     * @return the graph query builder
     */
    public GraphQueryBuilder getGraphQueryBuilder() {
        return graphQueryBuilder;
    }

    /**
     * Sets the graph query builder.
     * 
     * @param graphQueryBuilder the new graph query builder
     */
    public void setGraphQueryBuilder(GraphQueryBuilder graphQueryBuilder) {
        this.graphQueryBuilder = graphQueryBuilder;
    }
    
    /**
     * Gets the coding scheme uri.
     * 
     * @return the coding scheme uri
     */
    public String getCodingSchemeUri() {
        return codingSchemeUri;
    }

    /**
     * Sets the coding scheme uri.
     * 
     * @param codingSchemeUri the new coding scheme uri
     */
    public void setCodingSchemeUri(String codingSchemeUri) {
        this.codingSchemeUri = codingSchemeUri;
    }

    /**
     * Gets the version.
     * 
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     * 
     * @param version the new version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the relations container name.
     * 
     * @return the relations container name
     */
    public String getRelationsContainerName() {
        return relationsContainerName;
    }

    /**
     * Sets the relations container name.
     * 
     * @param relationsContainerName the new relations container name
     */
    public void setRelationsContainerName(String relationsContainerName) {
        this.relationsContainerName = relationsContainerName;
    }
    
    private GraphQuery getClonedGraphQuery() {
        try {
            return this.graphQueryBuilder.getQuery().clone();
        } catch (CloneNotSupportedException e) {
           throw new RuntimeException(e);
        }
    }
}
