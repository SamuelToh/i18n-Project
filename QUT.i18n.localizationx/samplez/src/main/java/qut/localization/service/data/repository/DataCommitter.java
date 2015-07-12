package qut.localization.service.data.repository;

import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import qut.localization.service.data.factory.PMF;
import qut.localization.service.datastructure.Annotation;
import qut.localization.service.datastructure.Pattern;


/**
 * DataCommitter class facilitates saving of our pattern entity into
 * google big table.
 * @author <a href="mailto:yu.toh@connect.qut.edu.au">Samuel Toh</a>
 * @version $Revision: 1 $
 */
public class DataCommitter extends DataManager {

	
	/**
	 * Commit our data into the server.
	 * 
	 * @param pattern to be saved into the data repository.
	 * @throws Exception thrown when data commit fails.
	 */
	public static int Create(PatternEntity pattern) throws Exception{

		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		try {
			//try commit the data
			pm.makePersistent(pattern); 
			
		}catch(Exception e){ 
			pm.close(); //close this before re throw the exception
			return PATTERN_UPDATE_FAIL;

		}
		pm.close();
		return PATTERN_CREATED;
	}
	
	public static int Update(PatternEntity newPattern){

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(PatternEntity.class);
		query.setFilter(getNameFilter(newPattern.getName()));
		
		try {
			List<PatternEntity> results = (List<PatternEntity>) query.execute();

			if(results.size() < 1)
				return PATTERN_UPDATE_FAIL_PAT_NOT_FOUND;
			
			results.get(0).setName(newPattern.getName());
			results.get(0).setLocaleCode(newPattern.getLocaleCode());
			results.get(0).setRuleType(newPattern.getRuleType());
			results.get(0).setStandard(newPattern.getStandard());
			results.get(0).setBeforeRegExpression(newPattern.getBeforeRegExpression());
			results.get(0).setAfterRegExpression(newPattern.getAfterRegExpression());
			results.get(0).setLastUpdate(newPattern.getLastUpdate());
			
			List<AnnotationEntity> patternAnn = new LinkedList<AnnotationEntity>();
			for(AnnotationEntity ann : newPattern.getAnnotations())
			{
				patternAnn.add(new AnnotationEntity(results.get(0),ann.getAnnotationKey(), ann.getAnnotationValue()));
			}
			
			if(patternAnn.size() > 0 )
				results.get(0).setAnnotations(patternAnn);
			
			
			
		}catch(Exception e){ 
			pm.close(); //close this before re throw the exception
			return PATTERN_UPDATE_FAIL;
		}
		pm.close();
		return PATTERN_UPDATED;
	}
				
}
