package org.lexevs.dao.index.lucene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermsFilter;
import org.compass.core.lucene.support.ChainedFilter;
import org.lexevs.dao.database.utility.DaoUtility;
import org.lexevs.dao.index.access.AbstractBaseIndexDao;
import org.lexevs.dao.index.indexer.LuceneLoaderCode;
import org.lexevs.dao.index.lucenesupport.LuceneIndexTemplate;

public abstract class AbstractBaseLuceneIndexTemplateDao extends AbstractBaseIndexDao {
	
	private Map<String,Filter> codingSchemeFilterMap = new HashMap<String,Filter>();
	private Map<String,Filter> boundaryDocFilterMap = new HashMap<String,Filter>();
	
	protected Filter getBoundaryDocFilterForCodingScheme(List<AbsoluteCodingSchemeVersionReference> codingSchemes) {
		String key = getFilterMapKey(codingSchemes);
		if(!this.boundaryDocFilterMap.containsKey(key)) {
			Filter[] filters = new Filter[codingSchemes.size()];
			for(int i=0;i<codingSchemes.size();i++) {
				AbsoluteCodingSchemeVersionReference ref = codingSchemes.get(i);
				
				filters[i] = this.getBoundaryDocFilterForCodingScheme(
						ref.getCodingSchemeURN(), 
						ref.getCodingSchemeVersion());
			}
			
			Filter chainedFilter = new ChainedFilter(filters, ChainedFilter.OR);
			boundaryDocFilterMap.put(key, new CachingWrapperFilter(chainedFilter));
		}
		return boundaryDocFilterMap.get(key);
	}
	
	protected Filter getBoundaryDocFilterForCodingScheme(String codingSchemeUri, String codingSchemeVersion) {
		String key = getFilterMapKey(codingSchemeUri, codingSchemeUri);
		if(!this.boundaryDocFilterMap.containsKey(key)) {
			Filter filter = createBoundaryDocFilter();
			boundaryDocFilterMap.put(key, new CachingWrapperFilter(filter));
		}
		return new ChainedFilter(
				new Filter[] {boundaryDocFilterMap.get(key), 
				this.getCodingSchemeFilterForCodingScheme(codingSchemeUri, codingSchemeVersion)}, 
				ChainedFilter.AND);
	}
	
	protected Filter createBoundaryDocFilter() {
		TermsFilter filter = new TermsFilter();
		filter.addTerm(new Term("codeBoundry", "T"));
		
		return filter;
	}
	
	protected Filter getCodingSchemeFilterForCodingScheme(List<AbsoluteCodingSchemeVersionReference> codingSchemes) {
		String key = getFilterMapKey(codingSchemes);
		if(!this.codingSchemeFilterMap.containsKey(key)) {
			Filter[] filters = new Filter[codingSchemes.size()];
			for(int i=0;i<codingSchemes.size();i++) {
				AbsoluteCodingSchemeVersionReference ref = codingSchemes.get(i);
				
				filters[i] = this.getCodingSchemeFilterForCodingScheme(
						ref.getCodingSchemeURN(), 
						ref.getCodingSchemeVersion());
			}
			
			Filter chainedFilter = new ChainedFilter(filters, ChainedFilter.OR);
			codingSchemeFilterMap.put(key, new CachingWrapperFilter(chainedFilter));
		}
		return codingSchemeFilterMap.get(key);
	}
	
	protected Filter getCodingSchemeFilterForCodingScheme(String codingSchemeUri, String codingSchemeVersion) {
		String key = getFilterMapKey(codingSchemeUri, codingSchemeVersion);
		if(!this.codingSchemeFilterMap.containsKey(key)) {
			Term term = new Term(
					LuceneLoaderCode.CODING_SCHEME_URI_VERSION_KEY_FIELD,
					LuceneLoaderCode.createCodingSchemeUriVersionKey(
							codingSchemeUri, codingSchemeVersion));
			TermsFilter filter = new TermsFilter();
			filter.addTerm(term);
			codingSchemeFilterMap.put(key, new CachingWrapperFilter(filter));
		}
		return codingSchemeFilterMap.get(key);
	}
	
	private String getFilterMapKey(List<AbsoluteCodingSchemeVersionReference> refs) {
		return DaoUtility.createKey(refs);
	}
	
	private String getFilterMapKey(String codingSchemeUri, String codingSchemeVersion) {
		return DaoUtility.createKey(codingSchemeUri, codingSchemeUri);
	}

	protected abstract LuceneIndexTemplate getLuceneIndexTemplate(String codingSchemeUri, String version);
}
