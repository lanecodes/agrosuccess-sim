package me.ajlane.geo.repast.colonisation;

import repast.simphony.engine.schedule.IAction;

public class LandCoverColonisationAction implements IAction {

  private final LandCoverColoniser landCoverColoniser;

  public LandCoverColonisationAction(LandCoverColoniser landCoverColoniser) {
      this.landCoverColoniser = landCoverColoniser;
  }

  @Override
  public void execute() {
    this.landCoverColoniser.updateJuvenilePresenceLayers();
  }

}
