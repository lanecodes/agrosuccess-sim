package me.ajlane.geo.repast.succession.pathway.coded;

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

public class CodedLcsTransitionMapTest {

  private CodedLcsTransitionMap testMap;

  @Rule
  public TemporaryFolder tmpDir = new TemporaryFolder();

  @Before
  public void setUp() {
    this.testMap = generateTestMap();
  }

  private CodedLcsTransitionMap generateTestMap() {
    CodedLcsTransitionMap m = new CodedLcsTransitionMap();
    m.put(new CodedEnvrAntecedent(0, 0, 0, 1, 1, 1, 2), new CodedEnvrConsequent(1, 10));
    m.put(new CodedEnvrAntecedent(0, 1, 1, 0, 0, 0, 1), new CodedEnvrConsequent(2, 30));
    return m;
  }

  @Test
  public void testSerializable() throws IOException, ClassNotFoundException {
    CodedLcsTransitionMap lcsTransMap = this.testMap;
    File lctTransMapFile = this.tmpDir.newFile("AgroSuccess.ecotrans");

    ObjectOutputStream objectOutputStream =
        new ObjectOutputStream(new FileOutputStream(lctTransMapFile));
    objectOutputStream.writeObject(lcsTransMap);
    objectOutputStream.flush();
    objectOutputStream.close();

    ObjectInputStream lctTransMapInput =
        new ObjectInputStream(new FileInputStream(lctTransMapFile));
    CodedLcsTransitionMap newLcsTransMap = (CodedLcsTransitionMap) lctTransMapInput.readObject();
    lctTransMapInput.close();

    assertEquals(lcsTransMap, newLcsTransMap);
  }

}
