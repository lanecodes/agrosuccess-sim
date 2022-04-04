package repast.model.agrosuccess.anthro;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.valueLayer.ValueLayer;

public class DefaultPatchOptionGenerator implements PatchOptionGenerator {

  private final LctValueGetter lctValueGetter = new LctValueGetterHardCoded();
  private Map<Integer, Lct> codeToLctMap;
  private ValueLayer lctMap, slopeMap;

  public DefaultPatchOptionGenerator(ValueLayer lctMap, ValueLayer slopeMap) {
    this.codeToLctMap = buildCodeToLctMap();
    this.lctMap = lctMap;
    this.slopeMap = slopeMap;
  }

  private Map<Integer, Lct> buildCodeToLctMap() {
    Map<Integer, Lct> m = new HashMap<>();
    for (Lct lct : Lct.values()) {
      m.put(lct.getCode(), lct);
    }
    return m;
  }

  @Override
  public Set<PatchOption> generateAllPatchOptions() {
    Set<PatchOption> patchOptions = new HashSet<>();
    int width = (int) this.lctMap.getDimensions().getWidth();
    int height = (int) this.lctMap.getDimensions().getHeight();
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        Lct patchLct = this.codeToLctMap.get((int) this.lctMap.get(x, y));
        PatchOption patchOption = new PatchOption(new GridPoint(x, y),
            this.lctValueGetter.getFertility(patchLct), this.lctValueGetter.getWoodValue(patchLct),
            this.lctValueGetter.getSlopeModificationValue(this.slopeMap.get(x, y)),
            this.lctValueGetter.getLandCoverConversionCost(patchLct));
        patchOptions.add(patchOption);
      }
    }
    return patchOptions;
  }

}
