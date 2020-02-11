package me.ajlane.geo.repast.fire;

import static org.junit.Assert.*;
import org.junit.Test;
import me.ajlane.geo.Direction;

public class WindRiskCalculatorTest {

  private static final double TOLERANCE = 0.00001;

  @Test
  public void testAllTargetCellDirsWithNortherlyWind() {

    assertEquals(WindOrientation.Parallel,
        WindRiskCalculator.getWindOrientation(Direction.N, Direction.N));

    assertEquals(WindOrientation.Acute,
        WindRiskCalculator.getWindOrientation(Direction.N, Direction.NE));
    assertEquals(WindOrientation.Acute,
        WindRiskCalculator.getWindOrientation(Direction.NE, Direction.N));
    assertEquals(WindOrientation.Acute,
        WindRiskCalculator.getWindOrientation(Direction.N, Direction.NW));

    assertEquals(WindOrientation.Perpendicular,
        WindRiskCalculator.getWindOrientation(Direction.N, Direction.W));
    assertEquals(WindOrientation.Perpendicular,
        WindRiskCalculator.getWindOrientation(Direction.N, Direction.E));

    assertEquals(WindOrientation.Obtuse,
        WindRiskCalculator.getWindOrientation(Direction.N, Direction.SE));
    assertEquals(WindOrientation.Obtuse,
        WindRiskCalculator.getWindOrientation(Direction.N, Direction.SW));

    assertEquals(WindOrientation.Antiparallel,
        WindRiskCalculator.getWindOrientation(Direction.N, Direction.S));

  }

  @Test
  public void testVariousTargetCellDirsWithVariousWindDirs() {

    assertEquals(WindOrientation.Acute,
        WindRiskCalculator.getWindOrientation(Direction.W, Direction.SW));
    assertEquals(WindOrientation.Obtuse,
        WindRiskCalculator.getWindOrientation(Direction.E, Direction.SW));
    assertEquals(WindOrientation.Antiparallel,
        WindRiskCalculator.getWindOrientation(Direction.NW, Direction.SE));
    assertEquals(WindOrientation.Parallel,
        WindRiskCalculator.getWindOrientation(Direction.SW, Direction.SW));
    assertEquals(WindOrientation.Perpendicular,
        WindRiskCalculator.getWindOrientation(Direction.NE, Direction.SE));

  }

  @Test
  public void testWindRiskCalc() {
    WindRiskCalculator wrCalc = new WindRiskCalculator();

    assertEquals(1.10, wrCalc.getRisk(WindSpeed.Low, Direction.SW, Direction.SW), TOLERANCE);
    assertEquals(1.20, wrCalc.getRisk(WindSpeed.Medium, Direction.W, Direction.W), TOLERANCE);
    assertEquals(1.50, wrCalc.getRisk(WindSpeed.High, Direction.NW, Direction.NW), TOLERANCE);

    assertEquals(1.05, wrCalc.getRisk(WindSpeed.Low, Direction.SW, Direction.W), TOLERANCE);
    assertEquals(0.80, wrCalc.getRisk(WindSpeed.Medium, Direction.W, Direction.E), TOLERANCE);
    assertEquals(0.80, wrCalc.getRisk(WindSpeed.High, Direction.N, Direction.SE), TOLERANCE);
  }

}
