package repast.model.agrosuccess.empirical;

import java.io.File;
import org.apache.commons.configuration.ConfigurationException;

public class SiteAllDataFactory {

  /**
   * @see #getSiteAllData(File, boolean)
   *
   * @param file Data directory for study site.
   * @return Site-specific data for use in the simulation model.
   * @throws ConfigurationException
   */
  public SiteAllData getSiteAllData(File file) throws ConfigurationException {
    return getSiteAllData(file, false);
  }

  /**
   * @param dataDir Directory in which to look for data files. If {@code isDummy == false} this should be
   * the data directory for a study site. Otherwise it should be the test data directory.
   * @param isDummy Set to {@code true} if we want to load dummy data.
   * @return Site-specific data for use in the simulation model.
   * @throws ConfigurationException
   */
  public SiteAllData getSiteAllData(File dataDir, boolean isDummy) throws ConfigurationException {
    SiteAllData output = null;
    if (isDummy) {
      output = new DummySiteData(dataDir);
    } else {
      output = new SiteDataLoader(dataDir);
    }
    return output;
  }

}
