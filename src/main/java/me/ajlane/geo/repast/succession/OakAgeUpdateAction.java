package me.ajlane.geo.repast.succession;

import repast.simphony.engine.schedule.IAction;

public class OakAgeUpdateAction implements IAction {

  private final OakAgeUpdater oakAgeUpdater;

  public OakAgeUpdateAction(OakAgeUpdater oakAgeUpdater) {
    this.oakAgeUpdater = oakAgeUpdater;
  }

  @Override
  public void execute() {
    this.oakAgeUpdater.update();
  }

}
