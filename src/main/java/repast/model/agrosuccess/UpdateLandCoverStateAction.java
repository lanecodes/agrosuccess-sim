package repast.model.agrosuccess;

import me.ajlane.geo.repast.succession.LcsUpdater;
import repast.simphony.engine.schedule.IAction;

public class UpdateLandCoverStateAction implements IAction {

  private final LcsUpdater lcsUpdater;

  public UpdateLandCoverStateAction(LcsUpdater lcsUpdater) {
    this.lcsUpdater = lcsUpdater;
  }

  @Override
  public void execute() {
    lcsUpdater.updateLandscapeLcs();
  }

}
