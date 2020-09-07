package me.ajlane.geo.repast.succession.pathway.aliased;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class AliasedLcsTransitionMapTest {

  private AliasedLcsTransitionMap testMap;

  @Rule
  public TemporaryFolder tmpDir = new TemporaryFolder();

  @Before
  public void setUp() {
    this.testMap = generateTestMap();
  }

  private AliasedLcsTransitionMap generateTestMap() {
    AliasedLcsTransitionMap m = new AliasedLcsTransitionMap();
    m.put(new AliasedEnvrAntecedent("Shrubland", "regeneration", "south",
        "true", "true", "false", "xeric"),
        new AliasedEnvrConsequent("Pine", 10));
    m.put(new AliasedEnvrAntecedent("Shrubland", "regeneration", "north",
        "false", "false", "false", "hydric"),
        new AliasedEnvrConsequent("Oak", 30));
    return m;
  }

  @Test
  public void testSerializable() throws IOException, ClassNotFoundException {
    AliasedLcsTransitionMap lcsTransMap = this.testMap;
    File lctTransMapFile = this.tmpDir.newFile("AgroSuccess.ecotrans");

    ObjectOutputStream objectOutputStream =
        new ObjectOutputStream(new FileOutputStream(lctTransMapFile));
    objectOutputStream.writeObject(lcsTransMap);
    objectOutputStream.flush();
    objectOutputStream.close();

    ObjectInputStream lctTransMapInput =
        new ObjectInputStream(new FileInputStream(lctTransMapFile));
    AliasedLcsTransitionMap newLcsTransMap = (AliasedLcsTransitionMap) lctTransMapInput.readObject();
    lctTransMapInput.close();

    assertEquals(lcsTransMap, newLcsTransMap);
  }

}
