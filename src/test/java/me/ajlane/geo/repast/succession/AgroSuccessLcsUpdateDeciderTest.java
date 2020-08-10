package me.ajlane.geo.repast.succession;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * <p>
 * Test suite for {@link AgroSuccessLcsUpdateDecider}, which is responsible for determining the
 * state a landscape cell should be in the next time step given its current physical conditions.
 * Specifically the class under test implements the logical statements specified in Chapter 5 of
 * Andrew Lane's PhD thesis. These specify how land cover should evolve in time. This test suite
 * focuses on checking that these rules are correctly implemented the the {@code LcsUpdateDecider}
 * class for a set of test cases.
 * </p>
 *
 * <p>
 * Note that the rules tested here build on those specified in Section 3.2 of
 * <a href="https://doi.org/10.1016/j.envsoft.2009.03.013">Millington et al. 2009</a>. See the PhD
 * thesis for discussion of differences between the logical statements.
 * </p>
 *
 *
 * <h3>S2a</h3>
 * <p>
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
 *
 * <h3>S2b</h3>
 * <p>
 * ΔT(t-1) = ∅ ∧ ΔT*(t) ∈ ℤ -&gt; ΔT(t) = Round([1 + ΔT*(t)]/2)
 * </p>
 *
 * <p>
 * Encodes the assumption that if there was no target state in the previous time step, but that
 * current environmental conditions facilitate a state change (i.e. ΔT*(t) has an integer value)
 * then the resulting transition time should be calculated as per <b>S2a</b>, but assuming that
 * ΔT(t-1) = 1. This is to mirror the logic implemented in the 'dummy' transitions specified in
 * Millington et al. 2009 (see above).
 * </p>
 *
 * <h3>S2c</h3>
 * <p>
 * ΔD(t) = ΔD(t-1) ∧ C(t) = C(t-1)-&gt; ΔT(t) = ΔT(t-1)
 * </p>
 *
 * <p>
 * Ensures that if neither the current state or the target state have changed since the last time
 * step, the transition time will remain the same. This prevents a situation whereby the transition
 * time calculated by <b>S2a</b> or <b>S2b</b> is overwritten by the native transition time of ΔD
 * </p>
 *
 * <h3>S3b</h3>
 * <p>
 * ΔT(t-1) = ∅ ∧ ΔD(t-1) = ∅ -&gt; C(t) = C(t-1)<br />
 * </p>
 *
 * <p>
 * If the current combination of physical conditions imply a simulation cell won't transition to a
 * different land cover type, its land cover type in this time step must be the same as the land
 * cover type in the previous time step.
 * </p>
 *
 * <h3>Transition pathways used in test scenarios</h3>
 * <p>
 * <a href="#org4bae411">Table 1</a> shows the transition pathways considered in the test scenarios
 * in this test suite.
 * </p>
 *
 * <table id="org76c4946" border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">
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
 * <th scope="col" class="org-left">5</th>
 * <th scope="col" class="org-left">6</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td class="org-left">start state, C</td>
 * <td class="org-left">Shrubland (5)</td>
 * <td class="org-left">Pine (6)</td>
 * <td class="org-left">Shrubland (5)</td>
 * <td class="org-left">Pine (6)</td>
 * <td class="org-left">Shrubland (5)</td>
 * <td class="org-left">TransForest (7)</td>
 * </tr>
 *
 * <tr>
 * <td class="org-left">succession</td>
 * <td class="org-left">regeneration (0)</td>
 * <td class="org-left">regeneration (0)</td>
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
 * <td class="org-left">mesic (1)</td>
 * <td class="org-left">hydric (2)</td>
 * </tr>
 *
 * <tr>
 * <td class="org-left">end state, ΔD</td>
 * <td class="org-left">Pine (6)</td>
 * <td class="org-left"><code>null</code> (∅)</td>
 * <td class="org-left">Oak (9)</td>
 * <td class="org-left">TransForest (7)</td>
 * <td class="org-left"><code>null</code> (∅)</td>
 * <td class="org-left">Oak (9)</td>
 * </tr>
 *
 * <tr>
 * <td class="org-left">ΔT</td>
 * <td class="org-left">15</td>
 * <td class="org-left"><code>null</code> (∅)</td>
 * <td class="org-left">20</td>
 * <td class="org-left">15</td>
 * <td class="org-left"><code>null</code> (∅)</td>
 * <td class="org-left">20</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * <h3>Scenarios considered in tests</h3>
 *
 * <h4>Scenario 1</h4>
 * <ul>
 * <li>Transition occurs with no change in physical variables, new state is adsorbing</li>
 * <li>Transition from pathway 1 to pathway 2</li>
 * </ul>
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
 * <h4>Scenario 2</h4>
 * <ul>
 * <li>No state transition occurs, but a change in physical variables results in a change in target
 * state</li>
 * <li>Transition from pathway 1 to pathway 3</li>
 * </ul>
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
 * <h4>Scenario 3</h4>
 *
 * <ul>
 * <li>Initial state is adsorbing, but change in physical variables results in new target state</li>
 * <li>Transition from pathway 2 to pathway 4</li>
 * </ul>
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
 *
 * <h4>Scenario 4</h4>
 * <ul>
 * <li>No land cover state change occurs, but change in physical variables eliminates target
 * state</li>
 * </ul>
 *
 * <p>
 * No state change occurs, but as a result of a slight increase in soil moisture (xeric to mesic) at
 * time step 0, there is no longer any target state specified
 * </p>
 *
 * <table id="org2e7b675" border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">
 * <caption class="t-above"><span class="table-number">Table 5:</span> State update sequence for
 * Scenario 4</caption>
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
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * <td class="org-right">5</td>
 * </tr>
 *
 * <tr>
 * <td class="org-left">T<sub>in</sub></td>
 * <td class="org-right">14</td>
 * <td class="org-right">1</td>
 * <td class="org-right">2</td>
 * <td class="org-right">3</td>
 * <td class="org-right">4</td>
 * </tr>
 *
 * <tr>
 * <td class="org-left">ΔT</td>
 * <td class="org-right">15</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">∅</td>
 * </tr>
 *
 * <tr>
 * <td class="org-left">ΔD</td>
 * <td class="org-right">6</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">∅</td>
 * <td class="org-right">∅</td>
 * </tr>
 *
 * <tr>
 * <td class="org-left">&#xa0;</td>
 * <td class="org-right">&#xa0;</td>
 * <td class="org-right">&#xa0;</td>
 * <td class="org-right">&#xa0;</td>
 * <td class="org-right">&#xa0;</td>
 * <td class="org-right">&#xa0;</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * <h4>Scenario 5</h4>
 * <ul>
 * <li>Land-cover state transition to transition forest occurs</li>
 * </ul>
 *
 * <p>
 * This enables us to test that a transition to a non-mature vegetation type doesn't produce the
 * side effect of killing juvenile vegetation in the cell.
 * </p>
 *
 * <table id="org2e7b677" border="2" cellspacing="0" cellpadding="6" rules="groups" frame="hsides">
 * <caption class="t-above"><span class="table-number">Table 6:</span> State update sequence for
 * Scenario 5</caption>
 *
 * <colgroup> <col class="org-left" />
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
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td class="org-left">C</td>
 * <td class="org-right">6</td>
 * <td class="org-right">7</td>
 * <td class="org-right">7</td>
 * </tr>
 *
 * <tr>
 * <td class="org-left">T<sub>in</sub></td>
 * <td class="org-right">15</td>
 * <td class="org-right">1</td>
 * <td class="org-right">2</td>
 * </tr>
 *
 * <tr>
 * <td class="org-left">ΔT</td>
 * <td class="org-right">15</td>
 * <td class="org-right">20</td>
 * <td class="org-right">20</td>
 * </tr>
 *
 * <tr>
 * <td class="org-left">ΔD</td>
 * <td class="org-right">6</td>
 * <td class="org-right">9</td>
 * <td class="org-right">9</td>
 * </tr>
 *
 * <tr>
 * <td class="org-left">&#xa0;</td>
 * <td class="org-right">&#xa0;</td>
 * <td class="org-right">&#xa0;</td>
 * <td class="org-right">&#xa0;</td>
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

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock
  private SuccessionPathwayUpdater successionUpdater;

  @Mock
  private SeedStateUpdater seedUpdater;

  @Before
  public void setUp() {
    updateDecider = new AgroSuccessLcsUpdateDecider(makeTestCodedLcsTransitionMap(),
        this.successionUpdater, this.seedUpdater);
  }

  @After
  public void tearDown() {
    updateDecider = null;
  }

  @Test
  public void scenario1TimeStep4ShouldBeAsExpected() {
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 0);
    SeedState sameSeedState = seedStateFromEnvrAntecedent(prevPhysicalState);
    int prevTimeInState = 13;
    EnvrSimState simState = new EnvrSimState(prevTimeInState, null, null);
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(6, 15);

    expect(successionUpdater.updatedSuccessionPathway(simState, prevPhysicalState
        .getSuccessionPathway()))
            .andReturn(0);
    expect(seedUpdater.updatedSeedState(sameSeedState, 5, 5))
        .andReturn(sameSeedState);
    replay(successionUpdater, seedUpdater);

    LcsUpdateMsg msg = updateDecider.getLcsUpdateMsg(prevPhysicalState, simState, prevTargetTrans);

    assertEquals(5, (int) msg.getCurrentState().getStartState());
    assertEquals(14, msg.getTimeInState());
    assertEquals(15, (int) msg.getTargetTransition().getTransitionTime());
    assertEquals(6, (int) msg.getTargetTransition().getTargetState());

    // Pine and deciduous seeds present before transition to pine occurs
    assertEquals(1, (int) msg.getCurrentState().getPineSeeds());
    assertEquals(0, (int) msg.getCurrentState().getOakSeeds());
    assertEquals(1, (int) msg.getCurrentState().getDeciduousSeeds());

    verify(successionUpdater, seedUpdater);
  }

  @Test
  public void scenario1TimeStep5ShouldBeAsExpected() {
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 0);
    SeedState sameSeedState = seedStateFromEnvrAntecedent(prevPhysicalState);
    int prevTimeInState = 14;
    EnvrSimState simState = new EnvrSimState(prevTimeInState, null, null);
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(6, 15);

    expect(successionUpdater.updatedSuccessionPathway(simState, prevPhysicalState
        .getSuccessionPathway()))
            .andReturn(0);
    expect(seedUpdater.updatedSeedState(sameSeedState, 5, 5))
        .andReturn(sameSeedState);
    replay(successionUpdater, seedUpdater);

    LcsUpdateMsg msg = updateDecider.getLcsUpdateMsg(prevPhysicalState, simState, prevTargetTrans);

    assertEquals(5, (int) msg.getCurrentState().getStartState());
    assertEquals(15, msg.getTimeInState());
    assertEquals(15, (int) msg.getTargetTransition().getTransitionTime());
    assertEquals(6, (int) msg.getTargetTransition().getTargetState());

    // Pine and deciduous seeds present before transition to pine occurs
    assertEquals(1, (int) msg.getCurrentState().getPineSeeds());
    assertEquals(0, (int) msg.getCurrentState().getOakSeeds());
    assertEquals(1, (int) msg.getCurrentState().getDeciduousSeeds());

    verify(successionUpdater, seedUpdater);
  }

  @Test
  public void scenario1TimeStep6ShouldBeAsExpected() {
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 0);
    SeedState initialSeedState = seedStateFromEnvrAntecedent(prevPhysicalState);
    int prevTimeInState = 15;
    EnvrSimState simState = new EnvrSimState(prevTimeInState, null, null);
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(6, 15);

    expect(successionUpdater.updatedSuccessionPathway(simState, prevPhysicalState
        .getSuccessionPathway()))
            .andReturn(0);
    expect(seedUpdater.updatedSeedState(initialSeedState, 5, 6))
        .andReturn(new SeedState(0, 0, 0));
    replay(successionUpdater, seedUpdater);

    LcsUpdateMsg msg = updateDecider.getLcsUpdateMsg(prevPhysicalState, simState, prevTargetTrans);

    assertEquals(6, (int) msg.getCurrentState().getStartState());
    assertEquals(1, msg.getTimeInState());
    assertNull("target state not correct", msg.getTargetTransition());

    // Pine and deciduous seeds killed off following transition to pine
    assertEquals(0, (int) msg.getCurrentState().getPineSeeds());
    assertEquals(0, (int) msg.getCurrentState().getOakSeeds());
    assertEquals(0, (int) msg.getCurrentState().getDeciduousSeeds());

    verify(successionUpdater, seedUpdater);
  }

  @Test
  public void scenario1TimeStep7ShouldBeAsExpected() {
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(6, 0, 1, 1, 0, 0, 0);
    SeedState sameSeedState = seedStateFromEnvrAntecedent(prevPhysicalState);
    int prevTimeInState = 1;
    EnvrSimState simState = new EnvrSimState(prevTimeInState, null, null);
    CodedEnvrConsequent prevTargetTrans = null;

    expect(successionUpdater.updatedSuccessionPathway(simState, prevPhysicalState
        .getSuccessionPathway()))
            .andReturn(0);
    expect(seedUpdater.updatedSeedState(sameSeedState, 6, 6))
        .andReturn(sameSeedState);
    replay(successionUpdater, seedUpdater);

    LcsUpdateMsg msg = updateDecider.getLcsUpdateMsg(prevPhysicalState, simState, prevTargetTrans);

    assertEquals(6, (int) msg.getCurrentState().getStartState());
    assertEquals(2, msg.getTimeInState());
    assertNull(msg.getTargetTransition());

    verify(successionUpdater, seedUpdater);
  }

  @Test
  public void scenario2TimeStep1ShouldBeAsExpected() {
    // transition pathway 1
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 0);
    SeedState sameSeedState = seedStateFromEnvrAntecedent(prevPhysicalState);
    int prevTimeInState = 10;
    EnvrSimState simState = new EnvrSimState(prevTimeInState, null, null);
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(6, 15);

    expect(successionUpdater.updatedSuccessionPathway(simState, prevPhysicalState
        .getSuccessionPathway()))
            .andReturn(0);
    expect(seedUpdater.updatedSeedState(sameSeedState, 5, 5))
        .andReturn(sameSeedState);
    replay(successionUpdater, seedUpdater);

    LcsUpdateMsg msg = updateDecider.getLcsUpdateMsg(prevPhysicalState, simState, prevTargetTrans);

    assertEquals(5, (int) msg.getCurrentState().getStartState());
    assertEquals(11, msg.getTimeInState());
    assertEquals(15, (int) msg.getTargetTransition().getTransitionTime());
    assertEquals(6, (int) msg.getTargetTransition().getTargetState());

    verify(successionUpdater, seedUpdater);
  }

  @Test
  public void scenario2TimeStep2ShouldBeAsExpected() {
    // transition pathway 1
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 0);
    SeedState sameSeedState = seedStateFromEnvrAntecedent(prevPhysicalState);
    int prevTimeInState = 11;
    EnvrSimState simState = new EnvrSimState(prevTimeInState, null, null);
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(6, 15);

    expect(successionUpdater.updatedSuccessionPathway(simState, prevPhysicalState
        .getSuccessionPathway()))
            .andReturn(0);
    expect(seedUpdater.updatedSeedState(sameSeedState, 5, 5))
        .andReturn(sameSeedState);
    replay(successionUpdater, seedUpdater);

    LcsUpdateMsg msg =
        updateDecider.getLcsUpdateMsg(prevPhysicalState, simState, prevTargetTrans);

    assertEquals(5, (int) msg.getCurrentState().getStartState());
    assertEquals(12, msg.getTimeInState());
    assertEquals(15, (int) msg.getTargetTransition().getTransitionTime());
    assertEquals(6, (int) msg.getTargetTransition().getTargetState());

    verify(successionUpdater, seedUpdater);
  }

  @Test
  public void scenario2TimeStep3ShouldBeAsExpected() {
    // transition pathway 3
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 2);
    SeedState sameSeedState = seedStateFromEnvrAntecedent(prevPhysicalState);
    int prevTimeInState = 12;
    EnvrSimState simState = new EnvrSimState(prevTimeInState, null, null);

    // target state corresponding to pathway 1
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(6, 15);

    expect(successionUpdater.updatedSuccessionPathway(simState, prevPhysicalState
        .getSuccessionPathway()))
            .andReturn(0);
    expect(seedUpdater.updatedSeedState(sameSeedState, 5, 5))
        .andReturn(sameSeedState);
    replay(successionUpdater, seedUpdater);

    LcsUpdateMsg msg = updateDecider.getLcsUpdateMsg(prevPhysicalState, simState, prevTargetTrans);

    assertEquals(5, (int) msg.getCurrentState().getStartState());
    assertEquals(1, msg.getTimeInState());
    assertEquals(18, (int) msg.getTargetTransition().getTransitionTime());
    assertEquals(9, (int) msg.getTargetTransition().getTargetState());

    verify(successionUpdater, seedUpdater);
  }

  @Test
  public void scenario2TimeStep4ShouldBeAsExpected() {
    // transition pathway 3
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 2);
    SeedState sameSeedState = seedStateFromEnvrAntecedent(prevPhysicalState);
    int prevTimeInState = 1;
    EnvrSimState simState = new EnvrSimState(prevTimeInState, null, null);

    // target state transition time derived from pathway 1 and 3
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(9, 18);

    expect(successionUpdater.updatedSuccessionPathway(simState, prevPhysicalState
        .getSuccessionPathway()))
            .andReturn(0);
    expect(seedUpdater.updatedSeedState(sameSeedState, 5, 5))
        .andReturn(sameSeedState);
    replay(successionUpdater, seedUpdater);

    LcsUpdateMsg msg = updateDecider.getLcsUpdateMsg(prevPhysicalState, simState, prevTargetTrans);

    assertEquals(5, (int) msg.getCurrentState().getStartState());
    assertEquals(2, msg.getTimeInState());
    assertEquals(18, (int) msg.getTargetTransition().getTransitionTime());
    assertEquals(9, (int) msg.getTargetTransition().getTargetState());

    verify(successionUpdater, seedUpdater);
  }

  @Test
  public void scenario3TimeStep1ShouldBeAsExpected() {
    // transition pathway 2
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(6, 0, 1, 1, 0, 1, 0);
    SeedState sameSeedState = seedStateFromEnvrAntecedent(prevPhysicalState);
    int prevTimeInState = 50;
    EnvrSimState simState = new EnvrSimState(prevTimeInState, null, null);

    // there is no target state for above environmental conditions
    CodedEnvrConsequent prevTargetTrans = null;

    expect(successionUpdater.updatedSuccessionPathway(simState, prevPhysicalState
        .getSuccessionPathway()))
            .andReturn(0);
    expect(seedUpdater.updatedSeedState(sameSeedState, 6, 6))
        .andReturn(sameSeedState);
    replay(successionUpdater, seedUpdater);

    LcsUpdateMsg msg = updateDecider.getLcsUpdateMsg(prevPhysicalState, simState, prevTargetTrans);

    assertEquals(6, (int) msg.getCurrentState().getStartState());
    assertEquals(51, msg.getTimeInState());
    assertNull(msg.getTargetTransition());

    verify(successionUpdater, seedUpdater);
  }

  @Test
  public void scenario3TimeStep2ShouldBeAsExpected() {
    // transition pathway 4
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(6, 0, 1, 1, 0, 1, 2);
    SeedState sameSeedState = seedStateFromEnvrAntecedent(prevPhysicalState);
    int prevTimeInState = 51;
    EnvrSimState simState = new EnvrSimState(prevTimeInState, null, null);

    // target state for (anti-)pathway 2, i.e. before this state update finds a new viable target
    // state
    CodedEnvrConsequent prevTargetTrans = null;

    expect(successionUpdater.updatedSuccessionPathway(simState, prevPhysicalState
        .getSuccessionPathway()))
            .andReturn(0);
    expect(seedUpdater.updatedSeedState(sameSeedState, 6, 6))
        .andReturn(sameSeedState);
    replay(successionUpdater, seedUpdater);

    LcsUpdateMsg msg = updateDecider.getLcsUpdateMsg(prevPhysicalState, simState, prevTargetTrans);

    assertEquals("current state not correct", 6, (int) msg.getCurrentState().getStartState());
    assertEquals("time in state not correct", 1, msg.getTimeInState());
    assertEquals(8, (int) msg.getTargetTransition().getTransitionTime());
    assertEquals(7, (int) msg.getTargetTransition().getTargetState());

    verify(successionUpdater, seedUpdater);
  }

  @Test
  public void scenario3TimeStep3ShouldBeAsExpected() {
    // transition pathway 4
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(6, 0, 1, 1, 0, 1, 2);
    SeedState sameSeedState = seedStateFromEnvrAntecedent(prevPhysicalState);
    int prevTimeInState = 1;
    EnvrSimState simState = new EnvrSimState(prevTimeInState, null, null);

    // target state transition time derived from pathway 2 and 4
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(7, 8);

    expect(successionUpdater.updatedSuccessionPathway(simState, prevPhysicalState
        .getSuccessionPathway()))
            .andReturn(0);
    expect(seedUpdater.updatedSeedState(sameSeedState, 6, 6))
        .andReturn(sameSeedState);
    replay(successionUpdater, seedUpdater);

    LcsUpdateMsg msg = updateDecider.getLcsUpdateMsg(prevPhysicalState, simState, prevTargetTrans);

    assertEquals(6, (int) msg.getCurrentState().getStartState());
    assertEquals(2, msg.getTimeInState());
    assertEquals(8, (int) msg.getTargetTransition().getTransitionTime());
    assertEquals(7, (int) msg.getTargetTransition().getTargetState());

    verify(successionUpdater, seedUpdater);
  }

  @Test
  public void scenario4TimeStep1ShouldBeAsExpected() {
    // transition pathway 5
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 1);
    SeedState sameSeedState = seedStateFromEnvrAntecedent(prevPhysicalState);
    int prevTimeInState = 14;
    EnvrSimState simState = new EnvrSimState(prevTimeInState, null, null);

    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(6, 15);

    expect(successionUpdater.updatedSuccessionPathway(simState, prevPhysicalState
        .getSuccessionPathway()))
            .andReturn(0);
    expect(seedUpdater.updatedSeedState(sameSeedState, 5, 5))
        .andReturn(sameSeedState);
    replay(successionUpdater, seedUpdater);

    LcsUpdateMsg msg = updateDecider.getLcsUpdateMsg(prevPhysicalState, simState, prevTargetTrans);

    assertEquals(5, (int) msg.getCurrentState().getStartState());
    assertEquals(1, msg.getTimeInState());
    assertNull(msg.getTargetTransition());

    verify(successionUpdater, seedUpdater);
  }

  /**
   * TODO Investigate whether this should be tested in {@link SeedStateUpdater}'s tests rather than
   * here
   */
  @Test
  public void scenario5TimeStep1ShouldBeAsExpected() {
    // transition pathway 4
    CodedEnvrAntecedent prevPhysicalState = new CodedEnvrAntecedent(6, 0, 1, 1, 0, 1, 2);
    SeedState sameSeedState = seedStateFromEnvrAntecedent(prevPhysicalState);
    int prevTimeInState = 15;
    CodedEnvrConsequent prevTargetTrans = new CodedEnvrConsequent(7, 15);
    EnvrSimState simState = new EnvrSimState(prevTimeInState, null, null);

    expect(successionUpdater.updatedSuccessionPathway(simState, prevPhysicalState
        .getSuccessionPathway()))
            .andReturn(0);
    expect(seedUpdater.updatedSeedState(sameSeedState, 6, 7))
        .andReturn(sameSeedState);
    replay(successionUpdater, seedUpdater);

    LcsUpdateMsg msg = updateDecider.getLcsUpdateMsg(prevPhysicalState, simState, prevTargetTrans);

    assertEquals(7, (int) msg.getCurrentState().getStartState());
    assertEquals(1, msg.getTimeInState());
    assertEquals(20, (int) msg.getTargetTransition().getTransitionTime());
    assertEquals(9, (int) msg.getTargetTransition().getTargetState());

    // Trans forest not mature vegetation so juvenile seeds remain
    // Compare to scenario 1 where pine and deciduous juveniles are killed off
    // following transition to mature pine forest
    assertEquals(1, (int) msg.getCurrentState().getPineSeeds());
    assertEquals(0, (int) msg.getCurrentState().getOakSeeds());
    assertEquals(1, (int) msg.getCurrentState().getDeciduousSeeds());

    verify(successionUpdater, seedUpdater);
  }

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

    // Add transition pathway 6 to transition map
    transMap.put(new CodedEnvrAntecedent(7, 0, 1, 1, 0, 1, 2), new CodedEnvrConsequent(9, 20));

    return transMap;
  }

  private static SeedState seedStateFromEnvrAntecedent(CodedEnvrAntecedent envrAntecedent) {
    return new SeedState(envrAntecedent.getPineSeeds(), envrAntecedent.getOakSeeds(),
        envrAntecedent.getDeciduousSeeds());
  }

}
