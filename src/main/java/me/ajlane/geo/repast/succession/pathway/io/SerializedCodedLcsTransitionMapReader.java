package me.ajlane.geo.repast.succession.pathway.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.apache.log4j.Logger;
import me.ajlane.geo.repast.succession.pathway.coded.CodedLcsTransitionMap;

/**
 * Reads a serialised instance of a {@link CodedLcsTransitionMap} object
 *
 * @author Andrew Lane
 */
public class SerializedCodedLcsTransitionMapReader implements CodedLcsTransitionMapReader {

  final static Logger logger = Logger.getLogger(SerializedCodedLcsTransitionMapReader.class);
  private final File serializedLcsTransitionMap;

  /**
   * @param serializedLcsTransitionMap File containing a serialised instance of a
   *        {@link CodedLcsTransitionMap} object
   */
  public SerializedCodedLcsTransitionMapReader(File serializedLcsTransitionMap) {
    this.serializedLcsTransitionMap = serializedLcsTransitionMap;
  }

  /**
   * @throws IllegalArgumentException if the file used to configure this reader in the constructor
   *         does not exist, or if it contains a serialized object of a class not on the classpath
   * @throws IllegalStateException is the file used to configure this reader in the constructor
   *         exists but cannot be read
   */
  @Override
  public CodedLcsTransitionMap getCodedLcsTransitionMap() {
    FileInputStream lcsTransitionMapStream;
    try {
      lcsTransitionMapStream = new FileInputStream(this.serializedLcsTransitionMap);
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("No file called " + this.serializedLcsTransitionMap, e);
    }
    CodedLcsTransitionMap lcsTransMap = getMapFromStream(lcsTransitionMapStream);
    if (lcsTransMap.size() == 0) {
      logger.warn("0 ecological succession rules were found in "
          + this.serializedLcsTransitionMap);

    } else {
      logger.debug(lcsTransMap.size() + " ecological succession rules loaded from "
          + this.serializedLcsTransitionMap);
    }

    return lcsTransMap;
  }

  private CodedLcsTransitionMap getMapFromStream(FileInputStream inputStream) {
    CodedLcsTransitionMap lcsTransMap;
    try (ObjectInputStream lctTransMapInput = new ObjectInputStream(inputStream)) {
      lcsTransMap = (CodedLcsTransitionMap) lctTransMapInput.readObject();
    } catch (IOException e) {
      throw new IllegalStateException("The file " + this.serializedLcsTransitionMap
          + " was found but could be read");
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("The serialized object in "
          + this.serializedLcsTransitionMap + " is an instance of a class whose definition could "
          + "not becould not be found on the classpath", e);
    }
    return lcsTransMap;
  }

}
