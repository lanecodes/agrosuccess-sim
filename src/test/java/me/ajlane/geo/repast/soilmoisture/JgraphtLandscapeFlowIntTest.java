package me.ajlane.geo.repast.soilmoisture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.ajlane.geo.AsciiGridParams;
import me.ajlane.geo.AsciiGridWriter;
import me.ajlane.geo.CartesianGridDouble2D;
import me.ajlane.geo.DefaultCartesianGridDouble2D;
import me.ajlane.geo.GridLoc;
import me.ajlane.geo.WriteableCartesianGridDouble2D;
import me.ajlane.geo.repast.GeoRasterValueLayer;
import me.ajlane.geo.repast.ValueLayerAdapter;
import repast.model.agrosuccess.LscapeLayer;
import repast.simphony.valueLayer.ValueLayer;

/**
 * <h3>TODO</h3>
 * <ul>
 * <li>Improve performance in construction of {@link JgraphtLandscapeFlow} objects. See notes in
 * that class's source.</li>
 * </ul>
 *
 * @author Andrew Lane
 *
 */
public class JgraphtLandscapeFlowIntTest {

  private static final Set<String> studySiteNames = new HashSet<>(Arrays.asList("algendar",
      "atxuri", "charco_da_candieira", "monte_areo_mire", "navarres", "san_rafael"));
  private static Map<String, File> flowDirFiles;

  /**
   * Ensure landscape flows can be constructed for all study sites
   *
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    flowDirFiles = getFlowDirFiles(new File("data/study-sites"), studySiteNames);
    flowForStudySite("monte_areo_mire");
    // Calculate number of catchments in each study site's landscape
    for (String ssiteName : studySiteNames) {
      System.out.print(ssiteName + ": ");
      LandscapeFlow landscape = flowForStudySite(ssiteName);
      int i = 0;
      Iterator<CatchmentFlow> it = landscape.iterator();
      while (it.hasNext()) {
        i += 1;
        it.next();
      }
      System.out.println(i + " catchments");
    }

    // Output ascii grids showing which cells are in each catchment to facilitate sense check
    AsciiGridParams navarresParams =
        new AsciiGridParams(853441.256, 498414.042, 26.582310676864619, -9999);
    writeCatchmentAllocAsAsciiGrid("navarres", navarresParams);

    AsciiGridParams sanRafaelParams =
        new AsciiGridParams(690885.196, 236540.314, 27.286477847112689, -9999);
    writeCatchmentAllocAsAsciiGrid("san_rafael", sanRafaelParams);
  }

  public static void writeCatchmentAllocAsAsciiGrid(String ssiteName, AsciiGridParams params)
      throws IOException {
    CartesianGridDouble2D grid = getFlowDirMapGrid(flowDirFiles.get(ssiteName));
    LandscapeFlow flow = new JgraphtLandscapeFlow(grid, new DefaultFlowDirectionMap());
    List<CatchmentFlow> catchments = sortedCatchments(flow);
    File outputFile = new File("data/test", ssiteName + "_catchments.asc");
    WriteableCartesianGridDouble2D outputGrid =
        new DefaultCartesianGridDouble2D(-9999, grid.getDimensions());
    for (int i = 0; i < catchments.size(); i++) {
      CatchmentFlow catchment = catchments.get(i);
      for (GridLoc loc : catchment.flowSourceDependencyOrder()) {
        outputGrid.setValue(i, loc);
      }
    }
    AsciiGridWriter writer = new AsciiGridWriter(outputGrid, params);
    writer.write(outputFile);
  }

  /**
   * @param landscape
   * @return Catchments in the landscape sorted in order of largest number of grid cells to
   *         smallest.
   */
  public static List<CatchmentFlow> sortedCatchments(LandscapeFlow landscape) {
    List<CatchmentFlow> catchmentList = new ArrayList<>();
    for (CatchmentFlow catchment : landscape) {
      catchmentList.add(catchment);
    }
    Collections.sort(catchmentList, Collections.reverseOrder(new CatchmentComparator()));
    return catchmentList;
  }

  private static LandscapeFlow flowForStudySite(String ssiteName) {
    CartesianGridDouble2D grid = getFlowDirMapGrid(flowDirFiles.get(ssiteName));
    LandscapeFlow flow = new JgraphtLandscapeFlow(grid, new DefaultFlowDirectionMap());
    return flow;
  }

  private static Map<String, File> getFlowDirFiles(File dataRoot, Set<String> studySiteNames) {
    Map<String, File> files = new HashMap<>();
    for (String studySite : studySiteNames) {
      files.put(studySite, new File(dataRoot, studySite + "/flow_dir.tif"));
    }
    return files;
  }

  public static CartesianGridDouble2D getFlowDirMapGrid(File geoTiff) {
    return new ValueLayerAdapter(getFlowDirMapValueLayer(geoTiff));
  }

  private static ValueLayer getFlowDirMapValueLayer(File geoTiff) {
    GeoRasterValueLayer grvl =
        new GeoRasterValueLayer(geoTiff.getAbsolutePath(), LscapeLayer.FlowDir.name());
    ValueLayer layer = grvl.getValueLayer();
    return layer;
  }

  static class CatchmentComparator implements Comparator<CatchmentFlow> {
    @Override
    public int compare(CatchmentFlow o1, CatchmentFlow o2) {
      int o1Size = o1.flowSourceDependencyOrder().size();
      int o2Size = o2.flowSourceDependencyOrder().size();
      if (o1Size < o2Size) {
        return -1;
      } else if (o1Size == o2Size) {
        return 0;
      }
      return 1;
    }
  }

}
