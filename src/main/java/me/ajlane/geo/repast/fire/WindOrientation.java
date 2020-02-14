package me.ajlane.geo.repast.fire;

/**
 * Categories of angles which can be formed between the direction of the wind and a neighbouring cell.
 * Used to evaluate wind spread risk during a fire.
 *
 * See Fig. 3 in <a href="https://doi.org/10.1016/j.envsoft.2009.03.013">Millington et al. 2009</a>.
 *
 * @author Andrew Lane
 *
 */
public enum WindOrientation {
  Parallel, Acute, Perpendicular, Obtuse, Antiparallel;
}
