package me.ajlane.geo.repast.seeddispersal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import me.ajlane.geo.repast.seeddispersal.Seed;

public class SeedTest {
	
	@Test
	public void ageShouldBe10() {
		Seed seed = new Seed("pine", 5, 2, 4);
		assertEquals(10, seed.getAge(15));		
	}
}
