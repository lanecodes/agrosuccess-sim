package me.ajlane.geo.repast.fire;

import repast.simphony.engine.schedule.IAction;

public class RunFireSeasonAction implements IAction {

  private final FireManager fireManager;

  public RunFireSeasonAction(FireManager fireManager) {
    this.fireManager = fireManager;
  }

  @Override
  public void execute() {
    this.fireManager.startFires();
  }

}
