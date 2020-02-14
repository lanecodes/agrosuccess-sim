package repast.model.agrosuccess.empirical;

import static org.junit.Assert.*;
import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

public class SiteAllDataFactoryTest {

  @Test
  public void testDummyData() throws ConfigurationException {
    SiteAllDataFactory factory = new SiteAllDataFactory();
    SiteAllData siteData = factory.getSiteAllData(new File("data/test"), true);
    assertEquals("DUMMY", siteData.getSiteName());

  }

  @Test
  public void testNavarresData() throws ConfigurationException {
    SiteAllDataFactory factory = new SiteAllDataFactory();
    SiteAllData siteData = factory.getSiteAllData(new File("data/study-sites/navarres"));
    assertEquals("Navarrés", siteData.getSiteName());
  }

}
