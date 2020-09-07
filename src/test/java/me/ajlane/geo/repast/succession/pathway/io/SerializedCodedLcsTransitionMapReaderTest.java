package me.ajlane.geo.repast.succession.pathway.io;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import me.ajlane.geo.repast.succession.pathway.coded.CodedEnvrAntecedent;
import me.ajlane.geo.repast.succession.pathway.coded.CodedEnvrConsequent;
import me.ajlane.geo.repast.succession.pathway.coded.CodedLcsTransitionMap;

public class SerializedCodedLcsTransitionMapReaderTest {

  private File testFile;
  private CodedLcsTransitionMap testMap;

  @Rule
  public TemporaryFolder tmpDir = new TemporaryFolder();

  @Before
  public void setUp() throws IOException {
    this.testMap = generateTestMap();
    this.testFile = writeTestMap(testMap);
  }

  private static CodedLcsTransitionMap generateTestMap() {
    CodedLcsTransitionMap m = new CodedLcsTransitionMap();
    m.put(new CodedEnvrAntecedent(0, 0, 0, 1, 1, 1, 2), new CodedEnvrConsequent(1, 10));
    m.put(new CodedEnvrAntecedent(0, 1, 1, 0, 0, 0, 1), new CodedEnvrConsequent(2, 30));
    return m;
  }

  private File writeTestMap(CodedLcsTransitionMap testMap) throws IOException {
    File lctTransMapFile = this.tmpDir.newFile("AgroSuccess.ecotrans");

    ObjectOutputStream objectOutputStream =
        new ObjectOutputStream(new FileOutputStream(lctTransMapFile));
    objectOutputStream.writeObject(testMap);
    objectOutputStream.flush();
    objectOutputStream.close();

    return lctTransMapFile;
  }

  @Test
  public void testMapSuccessfullyRead() {
    CodedLcsTransitionMapReader reader = new SerializedCodedLcsTransitionMapReader(this.testFile);
    CodedLcsTransitionMap map = reader.getCodedLcsTransitionMap();
    assertEquals(this.testMap, map);
  }

}
