/**
 * 
 */
package me.ajlane.geo.repast.colonisation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Keeps track of how long seeds have existed for and provides lists of dead 
 * seeds at a given time. 
 * 
 * @author Andrew Lane
 *
 */
public class SeedViabilityMonitor {
	
	int pineSeedLifetime;
	int oakSeedLifetime;
	int deciduousSeedLifetime;
	
	Queue<Seed> pineQueue = new LinkedList<Seed>();
	Queue<Seed> oakQueue = new LinkedList<Seed>();
	Queue<Seed> deciduousQueue = new LinkedList<Seed>();
	
	/**
	 * @param seedViabilityParams Parameters specifying how long different types of seeds survive
	 *         in the seed bank after being deposited.
	 */
	SeedViabilityMonitor(SeedViabilityParams seedViabilityParams) {
		this.pineSeedLifetime = seedViabilityParams.getPineSeedLifetime();
		this.oakSeedLifetime = seedViabilityParams.getOakSeedLifetime();
		this.deciduousSeedLifetime = seedViabilityParams.getDeciduousSeedLifetime();	
	}
	
	/**
	 * Make the SeedViabilityMonitor aware of the given Seed object. Note that if a seed
	 * is created and the SeedViabilityMonitor isn't made aware, there is no way of knowing
	 * whether or not that seed should be considered dead. 
	 *   
	 * @param seed
	 * 		Seed object the SeedViabilityMonitor is being made aware of. 
	 */
	public void addSeed(Seed seed) {
		
		if (seed.getType()=="pine") {
			pineQueue.add(seed);			
		} else if (seed.getType()=="oak") {
			oakQueue.add(seed);
		} else if (seed.getType()=="deciduous") {
			deciduousQueue.add(seed);
		} else {
			throw new IllegalArgumentException(seed.getType() + 
					" is not a recognised seed type.");
		}		
	}
	
	/**
	 * @param seedType
	 * 		The species for which we want to determine the number of seeds
	 * 		currently in the model
	 * @return
	 * 		Number of seeds in the model of type seedType
	 */
	public int getNumSeeds(String seedType) {
		if (seedType.equals("pine")) {
			return pineQueue.size();
		} else if (seedType.equals("oak")) {
			return oakQueue.size();
		} else if (seedType.equals("deciduous")) {
			return deciduousQueue.size();
		} else {
			throw new IllegalArgumentException("Seed type must be one of 'pine', " + 
							"'deciduous' or 'oak'");
		}
	}
	
	/**
	 * @param currentTime
	 * 		Present model time, used to calculate the age of seeds in specified queue
	 * @param seedLifeTime
	 * 		Lifetime of seeds in queue (in simulation time units) used to calculate if 
	 * 		seeds in queue are old enough to be dead 
	 * @param queue
	 * 		queue containing seeds from which we will remove the dead ones
	 * @return
	 * 		Set of dead seeds which have been removed from the given queue
	 */
	private Set<Seed> getDeadSeedsFromQueue(int currentTime, int seedLifeTime, Queue<Seed> queue) {
		boolean moreDeadSeeds = true; // track whether we expect more dead seeds from queue
		Set<Seed> deadSeeds = new HashSet<Seed>();
		
		while (moreDeadSeeds) {
			Seed thisSeed = queue.peek(); // store a reference to Seed at front of queue without removing
			
			if (thisSeed==null) { // queue is empty
				moreDeadSeeds = false;				
			} else if (thisSeed.getAge(currentTime)>seedLifeTime) { // Seed at front of queue is dead
				deadSeeds.add(queue.poll());				
			} else { // Seed at front of queue isn't dead yet, so neither will be those behind it, exit while
				moreDeadSeeds = false;
			}			
		}
		
		return deadSeeds;
	}
	
	/**
	 * Get a set containing all the seeds which have died <em>since the last time
	 * this method was called</em>. Note that calling this method removes the dead 
	 * seeds from the queues held by instances of the SeedViabilityMonitor class. 
	 * This means we don't store seeds which have died unnecessarily, but does mean
	 * that code calling this method should ensure it does whatever it needs to do 
	 * with the knowledge that the seeds have died, i.e. remove them from ValueLayer-s
	 * if necessary etc.
	 * 
	 * @param currentTime
	 * 		Present model time, used to calculate the age of seeds in each of the seed
	 * 		queues (pine, deciduous and oak) internal to SeedViabilityMonitor instances.		
	 * @return
	 * 		A set of seeds which have died since the last time this method was called
	 */
	public Set<Seed> deadSeeds(int currentTime) {
		Set<Seed> seeds = new HashSet<Seed>();
		seeds.addAll(getDeadSeedsFromQueue(currentTime, pineSeedLifetime, pineQueue));
		seeds.addAll(getDeadSeedsFromQueue(currentTime, oakSeedLifetime, oakQueue));
		seeds.addAll(getDeadSeedsFromQueue(currentTime, deciduousSeedLifetime, deciduousQueue));
		return seeds;		
	}
}
