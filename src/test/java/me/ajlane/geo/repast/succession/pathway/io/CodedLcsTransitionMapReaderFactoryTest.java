package me.ajlane.geo.repast.succession.pathway.io;

import static org.easymock.EasyMock.mock;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import java.io.File;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import me.ajlane.geo.repast.succession.pathway.convert.EnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.pathway.io.graph.GraphBasedLcsTransitionMapReader;

public class CodedLcsTransitionMapReaderFactoryTest {

  @Test
  public void testGraphBasedLcsTransitionMapReaderConstructor() {

    GraphDatabaseService graph = mock(GraphDatabaseService.class);
    String modelID = "demo-mock";
    EnvrStateAliasTranslator translator = mock(EnvrStateAliasTranslator.class);


    CodedLcsTransitionMapReaderFactory fac = new CodedLcsTransitionMapReaderFactory();
    CodedLcsTransitionMapReader reader = fac.getCodedLcsTransitionMapReader(graph, modelID, translator);
    assertThat(reader, instanceOf(GraphBasedLcsTransitionMapReader.class));
  }

  @Test
  public void testSerializedLcsTransitionMapReaderConstructor() {

    File testFile = mock(File.class);

    CodedLcsTransitionMapReaderFactory fac = new CodedLcsTransitionMapReaderFactory();
    CodedLcsTransitionMapReader reader = fac.getCodedLcsTransitionMapReader(testFile);
    assertThat(reader, instanceOf(SerializedCodedLcsTransitionMapReader.class));
  }

}
