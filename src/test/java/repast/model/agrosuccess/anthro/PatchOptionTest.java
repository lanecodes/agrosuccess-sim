package repast.model.agrosuccess.anthro;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import repast.simphony.space.grid.GridPoint;

public class PatchOptionTest {

  @Test
  public void testEqualsObject() {
    PatchOption po1 = new PatchOption(new GridPoint(0, 0), 0, 0, 0, 0);
    PatchOption po2 = new PatchOption(new GridPoint(0, 1), 0, 0, 0, 0);
    PatchOption po3 = new PatchOption(new GridPoint(0, 1), 0, 1, 0, 0);
    PatchOption po4 = new PatchOption(new GridPoint(0, 0), 0, 0, 0, 0);

    assertFalse(po1.equals(po2));
    assertFalse(po2.equals(po3));
    assertTrue(po1.equals(po4));
  }

  @Test
  public void testHashCode() {
    PatchOption po1 = new PatchOption(new GridPoint(0, 0), 0, 0, 0, 0);
    PatchOption po2 = new PatchOption(new GridPoint(0, 1), 0, 0, 0, 0);
    PatchOption po3 = new PatchOption(new GridPoint(0, 1), 0, 1, 0, 0);
    PatchOption po4 = new PatchOption(new GridPoint(0, 0), 0, 0, 0, 0);

    assertTrue(po1.hashCode() != po2.hashCode());
    assertTrue(po2.hashCode() != po3.hashCode());
    assertTrue(po1.hashCode() == po4.hashCode());
  }

}
