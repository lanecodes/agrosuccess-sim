/**
 * 
 */
package me.ajlane.geo.repast.succession;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

public class SeedViabilityMonitorTest {
	
	@Test
	public void shouldFindZeroThenZeroThenTwoDeadSeeds() {
		
		int time;
		Set<Seed> deadSeeds;
		
		// all species have seeds which are viable for 1 year
		SeedViabilityMonitor svm = new SeedViabilityMonitor(1);
		
		time = 0;
		// seeds deposited at some point during 0th year
		svm.addSeed(new Seed("pine", time, 0, 0));
		svm.addSeed(new Seed("oak", time, 0, 1));
		
		deadSeeds = svm.deadSeeds(time);
		assertEquals(0, deadSeeds.size());
		
		time = 1; //tick
		svm.addSeed(new Seed("pine", time, 1, 0));
		svm.addSeed(new Seed("deciduous", time, 1, 2));		
		
		// seeds with lifetime 1 year deposited in 0th year survive start of 1st year
		deadSeeds = svm.deadSeeds(time);
		assertEquals(0, deadSeeds.size());
		
		time = 2; //tick
		svm.addSeed(new Seed("oak", time, 1, 1));
		
		// by start of 2nd year, seeds with lifetime 1 year deposited in 0th year are dead
		deadSeeds = svm.deadSeeds(time);
		assertEquals(2, deadSeeds.size());
		
		System.out.println("Dead seeds at t=" + time);
		for (Seed s : deadSeeds) {
			System.out.println(s.toString());
		}
	}
	
	@Test
	public void shouldFindZeroThenZeroThenOneDeadSeed() {
		
		int time;
		Set<Seed> deadSeeds;
		
		// Pine seeds survive for one year, oak and deciduous for two
		SeedViabilityMonitor svm = new SeedViabilityMonitor(1, 2, 2);
		
		time = 0;
		// seeds deposited at some point during 0th year
		svm.addSeed(new Seed("pine", time, 0, 0));
		svm.addSeed(new Seed("oak", time, 0, 1));
		
		deadSeeds = svm.deadSeeds(time);
		assertEquals(0, deadSeeds.size());
		
		time = 1; //tick
		svm.addSeed(new Seed("pine", time, 1, 0));
		svm.addSeed(new Seed("deciduous", time, 1, 2));		
		
		// seeds with lifetime 1 year deposited in 0th year survive start of 1st year
		deadSeeds = svm.deadSeeds(time);
		assertEquals(0, deadSeeds.size());
		
		time = 2; //tick
		svm.addSeed(new Seed("oak", time, 1, 1));
		
		// by start of 2nd year, pine seed with lifetime 1 year deposited in 0th year is dead
		deadSeeds = svm.deadSeeds(time);
		assertEquals(1, deadSeeds.size());
		
		System.out.println("Dead seeds at t=" + time);
		for (Seed s : deadSeeds) {
			System.out.println(s.toString());
		}
	}
	
	@Test
	public void shouldFindThreePineSeedsTwoOakAndOneDeciduous() {
		
		int time;
		
		// All seed species survive for three years
		SeedViabilityMonitor svm = new SeedViabilityMonitor(2);
		
		time = 0;
		// seeds deposited at some point during 0th year
		svm.addSeed(new Seed("pine", time, 0, 0));
		svm.addSeed(new Seed("oak", time, 0, 1));
		
		svm.deadSeeds(time);
		
		time = 1; //tick
		svm.addSeed(new Seed("pine", time, 1, 0));
		svm.addSeed(new Seed("pine", time, 1, 0));
		svm.addSeed(new Seed("oak", time, 1, 0));
		svm.addSeed(new Seed("deciduous", time, 1, 2));	
		
		assertEquals(3, svm.getNumSeeds("pine"));
		assertEquals(2, svm.getNumSeeds("oak"));
		assertEquals(1, svm.getNumSeeds("deciduous"));
	}

}
