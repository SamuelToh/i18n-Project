package qut.localization.service.data.factory;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * A factory class that returns an instance of the Persistence manager factory
 * used to commit data in the data store.
 * @author <a href="mailto:yu.toh@connect.qut.edu.au">Samuel Toh</a>
 * @version $Revision: 1 $
 */
public final class PMF {
    private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    //Default constructor
    private PMF() {}

    /**
     * Singleton method to get our data committer.
     * @return an instance of our PMF
     */
    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }
}