package me.ajlane.geo.repast.succession;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * Test suite for {@link AgroSuccessLcsUpdateDecider}, which is responsible for determining the
 * state a landscape cell should be in the next time step given its current physical conditions.
 * Specifically the class under test implements the logical statements specified in Section 3.2 of
 * <a href="https://doi.org/10.1016/j.envsoft.2009.03.013">Millington et al. 2009</a> which
 * determine how land cover should evolve in time. This test suite focuses on checking that these
 * rules are correctly implemented the the {@code LcsUpdateDecider} class for a set of test cases.
 * </p>
 * 
 * <p>
 * Note that we have supplemented the statements specified in Millington et al. 2009 with the
 * additional statements described below. Statements 4.1, 4.2 and 7 have been added to account for a
 * difference in the approach we have taken to accounting for the fact that some combinations of
 * physical attributes result in cells remaining in their current state with respect to Millington
 * et al. 2009.
 * </p>
 * 
 * <p>
 * The former authors dealt with this scenario by specifying 'dummy' transitions in which the end
 * state equals the start state, and the transition time associated with that transition, ΔT, is set
 * at 1 year. This approach makes it necessary to identify and specify all these dummy transitions
 * in advance. It also means that, at a given time, a cell which has been in its current state for 5
 * time steps is indistinguishable from one which has been stuck in its present state for 500 time
 * steps. We argue that these two scenarios are qualitatively different, and that it is
 * diagnostically useful from a modelling perspective to be able to detect when cells are getting
 * stuck in a particular state for a long time. Such a finding might motivate a reconsideration of
 * additional transition rules which were previously dismissed, for example.
 * </p>
 * 
 * <p>
 * Our approach to this issue is to allow the rule specification process to be about declaring
 * <i>possible</i> transitions rather than anticipating problem states in running simulations, and
 * creating unnecessary data in the process. Statements 4.1, 4.2 and 7 augment the simulation logic
 * to handle situations in which a cell is in a state for which no rule has been specified
 * describing how it could leave that state. Central to achieving this is to allow target land cover
 * states, ΔD, and corresponding transition times, ΔT, to have no value &#x2013; represented by the
 * symbol ∅ in the statement descriptions and the Java expression {@code null} in the implementation
 * logic.
 * </p>
 * 
 * <p>
 * <b>Statement 4'</b><br />
 * ΔD(t) != ΔD(t-1) ∧ C(t) = C(t-1) -&gt; ΔT(t) = Round([ΔT(t-1) + ΔT*(t)]/2)<br />
 * where ΔT*(t) denotes the time of state transition implied by the physical state of the cell in
 * the previous time step.
 * </p>
 * 
 * <p>
 * This rule supersedes <b>Statement 4</b> specified in Millington et al. 2009, and differs only in
 * making it explicit that the result of the averaging performed in the consequent is rounded to the
 * nearest integer year.
 * </p>
 * 
 * <p>
 * <b>Statement 4.1</b><br />
 * ΔT(t-1) = ∅ ∧ ΔT*(t) ∈ ℤ -&gt; ΔT(t) = Round([1 + ΔT*(t)]/2)<br />
 * </p>
 * 
 * <p>
 * Encodes the assumption that if there was no target state in the previous time step, but that
 * current environmental conditions facilitate a state change (i.e. ΔT*(t) has an integer value)
 * then the resulting transition time should be calculated as per <b>Statement 4'</b>, but assuming
 * that ΔT(t-1) = 1. This is to mirror the logic implemented in the 'dummy' transitions specified in
 * Millington et al. 2009 (see above).
 * </p>
 * 
 * <p>
 * <b>Statement 4.2</b><br />
 * ΔD(t) = ΔD(t-1) ∧ C(t) = C(t-1)-&gt; ΔT(t) = ΔT(t-1)<br />
 * </p>
 * 
 * <p>
 * Ensures that if neither the current state or the target state have changed since the last time
 * step, the transition time will remain the same. This prevents a situation whereby the transition
 * time calculated by Statement <b>4'</b> or <b>4.1</b> is overwritten by the native transition time
 * of ΔD
 * </p>
 * 
 * <p>
 * <b>Statement 7</b><br />
 * ΔT(t-1) = ∅ ∧ ΔD(t-1) = ∅ -&gt; C(t) = C(t-1)<br />
 * </p>
 * 
 * <p>
 * If the current combination of physical conditions imply a simulation cell won't transition to a
 * different land cover type, its land cover type in this time step must be the same as the land
 * cover type in the previous time step.
 * </p>
 * 
 * <p>
 * <b>Transition pathways used in test scenarios</b><br />
 * <a href="#org4bae411">Table 1</a> shows the transition pathways considered in the test scenarios
 * in this test suite.
 * </p>
 * 
 * <table id="org4bae411" border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">
 * <caption class="t-above"><span class="table-number">Table 1:</span> The example transition
 * pathways considered in these tests. For each variable a meaningful value description is given
 * along with its corresponding numerical value as used in AgroSuccess in brackets.</caption>
 * 
 * <colgroup> <col class="org-left" />
 * 
 * <col class="org-left" />
 * 
 * <col class="org-left" />
 * 
 * <col class="org-left" />
 * 
 * <col class="org-left" /> </colgroup> <thead>
 * <tr>
 * <th scope="col" class="org-left">Pathway no.</th>
 * <th scope="col" class="org-left">1</th>
 * <th scope="col" class="org-left">2</th>
 * <th scope="col" class="org-left">3</th>
 * <th scope="col" class="org-left">4</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td class="org-left">start state, C</td>
 * <td class="org-left">Shrubland (5)</td>
 * <td class="org-left">Pine (6)</td>
 * <td class="org-left">Shrubland (5)</td>
 * <td class="org-left">Pine (6)</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">succession</td>
 * <td class="org-left">regeneration (0)</td>
 * <td class="org-left">regeneration (0)</td>
 * <td class="org-left">regeneration (0)</td>
 * <td class="org-left">regeneration (0)</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">aspect</td>
 * <td class="org-left">south (1)</td>
 * <td class="org-left">south (1)</td>
 * <td class="org-left">south (1)</td>
 * <td class="org-left">south (1)</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">pine</td>
 * <td class="org-left">true (1)</td>
 * <td class="org-left">true (1)</td>
 * <td class="org-left">true (1)</td>
 * <td class="org-left">true (1)</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">oak</td>
 * <td class="org-left">false (0)</td>
 * <td class="org-left">false (0)</td>
 * <td class="org-left">false (0)</td>
 * <td class="org-left">false (0)</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">deciduous</td>
 * <td class="org-left">true (1)</td>
 * <td class="org-left">true (1)</td>
 * <td class="org-left">true (1)</td>
 * <td class="org-left">true (1)</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">water</td>
 * <td class="org-left">xeric (0)</td>
 * <td class="org-left">xeric (0)</td>
 * <td class="org-left">hydric (2)</td>
 * <td class="org-left">hydric (2)</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">end state, ΔD</td>
 * <td class="org-left">Pine (6)</td>
 * <td class="org-left"><code>null</code> (∅)</td>
 * <td class="org-left">Oak (9)</td>
 * <td class="org-left">TransForest (7)</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">ΔT</td>
 * <td class="org-left">15</td>
 * <td class="org-left"><code>null</code> (∅)</td>
 * <td class="org-left">20</td>
 * <td class="org-left">15</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * 
 * 
 * <p>
 * <b>Scenarios considered in tests</b><br />
 * <br />
 * <b>Scenario 1: Transition occurs with no change in physical variables, new state is
 * adsorbing</b><br />
 * Transition from pathway 1 to pathway 2.
 * </p>
 * 
 * <p>
 * Assuming there are no changes in the environmental conditions, and that at time \(t=0\) the cell
 * has been in this state for 10 years, the state sequence shown in <a href="#orgda0d2e6">Table
 * 2</a> will take place.
 * </p>
 * 
 * <table id="orgda0d2e6" border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">
 * <caption class="t-above"><span class="table-number">Table 2:</span> State update sequence for
 * Scenario 1</caption>
 * 
 * <colgroup> <col class="org-left" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" /> </colgroup> <thead>
 * <tr>
 * <th scope="col" class="org-left">t</th>
 * <th scope="col" class="org-right">0</th>
 * <th scope="col" class="org-right">1</th>
 * <th scope="col" class="org-right">2</th>
 * <th scope="col" class="org-right">3</th>
 * <th scope="col" class="org-right">4</th>
 * <th scope="col" class="org-right">5</th>
 * <th scope="col" class="org-right">6</th>
 * <th scope="col" class="org-right">7</th>
 * <th scope="col" class="org-right">8</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td class="org-left">C</td>
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * <td class="org-right">6</td>
 * <td class="org-right">6</td>
 * <td class="org-right">6</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">T<sub>in</sub></td>
 * <td class="org-right">10</td>
 * <td class="org-right">11</td>
 * <td class="org-right">12</td>
 * <td class="org-right">13</td>
 * <td class="org-right">14</td>
 * <td class="org-right">15</td>
 * <td class="org-right">1</td>
 * <td class="org-right">2</td>
 * <td class="org-right">3</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">ΔT</td>
 * <td class="org-right">15</td>
 * <td class="org-right">15</td>
 * <td class="org-right">15</td>
 * <td class="org-right">15</td>
 * <td class="org-right">15</td>
 * <td class="org-right">15</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">∅</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">ΔD</td>
 * <td class="org-right">6</td>
 * <td class="org-right">6</td>
 * <td class="org-right">6</td>
 * <td class="org-right">6</td>
 * <td class="org-right">6</td>
 * <td class="org-right">6</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">∅</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <p>
 * <b>Scenario 2: No state transition occurs, but a change in physical variables results in a change
 * in target state</b><br />
 * Transition from pathway 1 to pathway 3.
 * </p>
 * 
 * <p>
 * A chance increase in soil moisture results in the cell's transition switching from pathway 1 to
 * pathway 3 at timestep 3. Here the transition completion time has increased from 15 years to 20
 * years. Consequently this example provides the opportunity to test the code's ability to calculate
 * the updated transition time.
 * </p>
 * 
 * <table id="orgb016eca" border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">
 * <caption class="t-above"><span class="table-number">Table 3:</span> State update sequence for
 * Scenario 2</caption>
 * 
 * <colgroup> <col class="org-left" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" /> </colgroup> <thead>
 * <tr>
 * <th scope="col" class="org-left">t</th>
 * <th scope="col" class="org-right">0</th>
 * <th scope="col" class="org-right">1</th>
 * <th scope="col" class="org-right">2</th>
 * <th scope="col" class="org-right">3</th>
 * <th scope="col" class="org-right">4</th>
 * <th scope="col" class="org-right">5</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td class="org-left">C</td>
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">T<sub>in</sub></td>
 * <td class="org-right">10</td>
 * <td class="org-right">11</td>
 * <td class="org-right">12</td>
 * <td class="org-right">1</td>
 * <td class="org-right">2</td>
 * <td class="org-right">3</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">ΔT</td>
 * <td class="org-right">15</td>
 * <td class="org-right">15</td>
 * <td class="org-right">15</td>
 * <td class="org-right">18</td>
 * <td class="org-right">18</td>
 * <td class="org-right">18</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">ΔD</td>
 * <td class="org-right">6</td>
 * <td class="org-right">6</td>
 * <td class="org-right">6</td>
 * <td class="org-right">9</td>
 * <td class="org-right">9</td>
 * <td class="org-right">9</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <p>
 * <b>Scenario 3: Initial state is adsorbing, but change in physical variables results in new target
 * state</b><br />
 * Transition from pathway 2 to pathway 4.
 * </p>
 * 
 * <p>
 * Following an increase in soil moisture in timestep 2, a cell which was stuck in the Pine land
 * cover type as a consequence of its other physical variables can now change into transition
 * forest.
 * </p>
 * 
 * <table id="org018e578" border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">
 * <caption class="t-above"><span class="table-number">Table 4:</span> State update sequence for
 * Scenario 3</caption>
 * 
 * <colgroup> <col class="org-left" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" />
 * 
 * <col class="org-right" /> </colgroup> <thead>
 * <tr>
 * <th scope="col" class="org-left">t</th>
 * <th scope="col" class="org-right">0</th>
 * <th scope="col" class="org-right">1</th>
 * <th scope="col" class="org-right">2</th>
 * <th scope="col" class="org-right">3</th>
 * <th scope="col" class="org-right">4</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td class="org-left">C</td>
 * <td class="org-right">6</td>
 * <td class="org-right">6</td>
 * <td class="org-right">6</td>
 * <td class="org-right">6</td>
 * <td class="org-right">6</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">T<sub>in</sub></td>
 * <td class="org-right">50</td>
 * <td class="org-right">51</td>
 * <td class="org-right">1</td>
 * <td class="org-right">2</td>
 * <td class="org-right">3</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">ΔT</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">8</td>
 * <td class="org-right">8</td>
 * <td class="org-right">8</td>
 * </tr>
 * 
 * <tr>
 * <td class="org-left">ΔD</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">7</td>
 * <td class="org-right">7</td>
 * <td class="org-right">7</td>
 * </tr>
 * </tbody>
 * </table>
 * <p>
 * <br />
 * </p>
 * 
 * @author Andrew Lane
 */
public class AgroSuccessLcsUpdateDeciderTest {
  LcsUpdateDecider updateDecider;
  
  /**
   * 
   * Note that no entry is made for pathway 2, as it has no target state so is not expected to be
   * found in the transition map
   * 
   * @return The transition map which will be used for each test case, according to the transition
   *         pathways described in the javadoc for this class.
   */
  private CodedLcsTransitionMap makeTestCodedLcsTransitionMap() {
    CodedLcsTransitionMap transMap = new CodedLcsTransitionMap();

    // Add transition pathway 1 to transition map
    transMap.put(new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 0), new CodedEnvrConsequent(6, 15));

    // Add transition pathway 3 to transition map
    transMap.put(new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 2), new CodedEnvrConsequent(9, 20));

    // Add transition pathway 4 to transition map
    transMap.put(new CodedEnvrAntecedent(6, 0, 1, 1, 0, 1, 2), new CodedEnvrConsequent(7, 15));

    return transMap;
  }

  @Before
  public void setUp() {
    updateDecider = new AgroSuccessLcsUpdateDecider(makeTestCodedLcsTransitionMap());
  }

  @After
  public void tearDown() {
    updateDecider = null;
  }

  @Test
  public void scenario1TimeStep4ShouldBeAsExpected() {
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 0);
    int prevTimeInState = 13;
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(6, 15);

    LcsUpdateMsg msg =
        updateDecider.getLcsUpdateMsg(prevPhysicalState, prevTimeInState, prevTargetTrans);

    assertEquals(5, msg.getCurrentState());
    assertEquals(14, msg.getTimeInState());
    assertEquals((Integer) 15, msg.getTargetStateTransitionTime());
    assertEquals((Integer) 6, msg.getTargetState());
  }
  
  @Test
  public void scenario1TimeStep5ShouldBeAsExpected() {
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 0);
    int prevTimeInState = 14;
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(6, 15);

    LcsUpdateMsg msg =
        updateDecider.getLcsUpdateMsg(prevPhysicalState, prevTimeInState, prevTargetTrans);

    assertEquals(5, msg.getCurrentState());
    assertEquals(15, msg.getTimeInState());
    assertEquals((Integer) 15, msg.getTargetStateTransitionTime());
    assertEquals((Integer) 6, msg.getTargetState());
  }
  
  @Test
  public void scenario1TimeStep6ShouldBeAsExpected() {
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 0);
    int prevTimeInState = 15;
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(6, 15);

    LcsUpdateMsg msg =
        updateDecider.getLcsUpdateMsg(prevPhysicalState, prevTimeInState, prevTargetTrans);

    assertEquals(6, msg.getCurrentState());
    assertEquals(1, msg.getTimeInState());
    assertNull("target state transition time not correct", msg.getTargetStateTransitionTime());
    assertNull("target state not correct", msg.getTargetState());
  }
  
  @Test
  public void scenario1TimeStep7ShouldBeAsExpected() {
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(6, 0, 1, 1, 0, 1, 0);
    int prevTimeInState = 1;
    CodedEnvrConsequent prevTargetTrans = null;

    LcsUpdateMsg msg =
        updateDecider.getLcsUpdateMsg(prevPhysicalState, prevTimeInState, prevTargetTrans);

    assertEquals(6, msg.getCurrentState());
    assertEquals(2, msg.getTimeInState());
    assertNull(msg.getTargetStateTransitionTime());
    assertNull(msg.getTargetState());
  }
  
  @Test
  public void scenario2TimeStep1ShouldBeAsExpected() {
    // transition pathway 1
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 0); 
    int prevTimeInState = 10;
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(6, 15);

    LcsUpdateMsg msg =
        updateDecider.getLcsUpdateMsg(prevPhysicalState, prevTimeInState, prevTargetTrans);

    assertEquals(5, msg.getCurrentState());
    assertEquals(11, msg.getTimeInState());
    assertEquals((Integer) 15, msg.getTargetStateTransitionTime());
    assertEquals((Integer) 6, msg.getTargetState());
  }
  
  @Test
  public void scenario2TimeStep2ShouldBeAsExpected() {
    // transition pathway 1
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 0); 
    int prevTimeInState = 11;
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(6, 15);

    LcsUpdateMsg msg =
        updateDecider.getLcsUpdateMsg(prevPhysicalState, prevTimeInState, prevTargetTrans);

    assertEquals(5, msg.getCurrentState());
    assertEquals(12, msg.getTimeInState());
    assertEquals((Integer) 15, msg.getTargetStateTransitionTime());
    assertEquals((Integer) 6, msg.getTargetState());
  }
  
  @Test
  public void scenario2TimeStep3ShouldBeAsExpected() {
    // transition pathway 3
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 2); 
    int prevTimeInState = 12;
    
    // target state corresponding to pathway 1
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(6, 15);

    LcsUpdateMsg msg =
        updateDecider.getLcsUpdateMsg(prevPhysicalState, prevTimeInState, prevTargetTrans);
    
    assertEquals(5, msg.getCurrentState());
    assertEquals(1, msg.getTimeInState());
    assertEquals((Integer) 18, msg.getTargetStateTransitionTime());
    assertEquals((Integer) 9, msg.getTargetState());
  }
  
  @Test
  public void scenario2TimeStep4ShouldBeAsExpected() {
    // transition pathway 3
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 2); 
    int prevTimeInState = 1;
    
    // target state transition time derived from pathway 1 and 3 
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(9, 18);

    LcsUpdateMsg msg =
        updateDecider.getLcsUpdateMsg(prevPhysicalState, prevTimeInState, prevTargetTrans);
    
    assertEquals(5, msg.getCurrentState());
    assertEquals(2, msg.getTimeInState());
    assertEquals((Integer) 18, msg.getTargetStateTransitionTime());
    assertEquals((Integer) 9, msg.getTargetState());
  }
  
  @Test
  public void scenario3TimeStep1ShouldBeAsExpected() {
    // transition pathway 2
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(6, 0, 1, 1, 0, 1, 0); 
    int prevTimeInState = 50;
    
    // there is no target state for above environmental conditions    
    CodedEnvrConsequent prevTargetTrans = null;

    LcsUpdateMsg msg =
        updateDecider.getLcsUpdateMsg(prevPhysicalState, prevTimeInState, prevTargetTrans);
    
    assertEquals(6, msg.getCurrentState());
    assertEquals(51, msg.getTimeInState());
    assertNull(msg.getTargetStateTransitionTime());
    assertNull(msg.getTargetState());
  }
  
  @Test
  public void scenario3TimeStep2ShouldBeAsExpected() {
    // transition pathway 4
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(6, 0, 1, 1, 0, 1, 2);
    int prevTimeInState = 51;
    
    // target state for (anti-)pathway 2, i.e. before this state update finds a new viable target
    // state
    CodedEnvrConsequent prevTargetTrans = null;

    LcsUpdateMsg msg =
        updateDecider.getLcsUpdateMsg(prevPhysicalState, prevTimeInState, prevTargetTrans);
    
    assertEquals("current state not correct", 6, msg.getCurrentState());
    assertEquals("time in state not correct", 1, msg.getTimeInState());
    assertEquals((Integer) 8, msg.getTargetStateTransitionTime());
    assertEquals((Integer) 7, msg.getTargetState());
  }
  
  @Test
  public void scenario3TimeStep3ShouldBeAsExpected() {
    // transition pathway 4
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(6, 0, 1, 1, 0, 1, 2);
    int prevTimeInState = 1;
    
    // target state transition time derived from pathway 2 and 4
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(7, 8);

    LcsUpdateMsg msg =
        updateDecider.getLcsUpdateMsg(prevPhysicalState, prevTimeInState, prevTargetTrans);
    
    assertEquals(6, msg.getCurrentState());
    assertEquals(2, msg.getTimeInState());
    assertEquals((Integer) 8, msg.getTargetStateTransitionTime());
    assertEquals((Integer) 7, msg.getTargetState());
  }

}
