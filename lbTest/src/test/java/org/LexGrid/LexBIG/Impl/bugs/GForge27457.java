package org.LexGrid.LexBIG.Impl.bugs;

import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Impl.function.LexBIGServiceTestCase;
import org.LexGrid.LexBIG.Impl.testUtility.ServiceHolder;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.codingSchemes.CodingScheme;

public class GForge27457 extends LexBIGServiceTestCase{
	final static String testID = "GForge27457";
    
    @Override
    protected String getTestID() {
        return testID;
    }

    public void testEmptyNamespace() {
    	LexBIGService lbs = ServiceHolder.instance().getLexBIGService();
    	CodingSchemeVersionOrTag csvt = Constructors.createCodingSchemeVersionOrTagFromVersion(CAMERA_SCHEME_VERSION);
    	try {
			CodedNodeSet cns = lbs.getNodeSet(CAMERA_SCHEME_NAME, csvt, null);
			NameAndValueList nvList = Constructors.createNameAndValueList("label", null);
			LocalNameList lcList = Constructors.createLocalNameList("label test in data property");
			cns.restrictToProperties(lcList, null, null, null, nvList);
			ResolvedConceptReferencesIterator iterator = cns.resolve(null, null,
					null, null, true);
			if (!iterator.hasNext()){
				fail("no entity found");
			}
			
		} catch (LBException e) {
			e.printStackTrace();
		}
    }
}
