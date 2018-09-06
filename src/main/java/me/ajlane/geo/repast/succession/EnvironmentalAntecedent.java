/**
 * 
 */
package me.ajlane.geo.repast.succession;

/**
 * Stores the combination of environmental conditions which might lead to
 * an environmental transition resulting in an EnvironmentalConsequent
 * 
 * @author andrew
 *
 */
public class EnvironmentalAntecedent<T1, T2, T3, T4, T5, T6, T7> {
	
	private T1 startState;
	private T2 successionPathway;
	private T3 aspect;
	private T4 pineSeeds;
	private T5 oakSeeds;
	private T6 deciduousSeeds;
	private T7 water;
	
	public EnvironmentalAntecedent(T1 startState, T2 successionPathway,
			T3 aspect, T4 pineSeeds, T5 oakSeeds, T6 deciduousSeeds, T7 water) {
		
		this.startState = startState;
		this.successionPathway = successionPathway;
		this.aspect = aspect;
		this.pineSeeds = pineSeeds;
		this.oakSeeds = oakSeeds;
		this.deciduousSeeds = deciduousSeeds;
		this.water = water;	
	}

	public T1 getStartState() {
		return startState;
	}

	public T2 getSuccessionPathway() {
		return successionPathway;
	}

	public T3 getAspect() {
		return aspect;
	}

	public T4 getPineSeeds() {
		return pineSeeds;
	}

	public T5 getOakSeeds() {
		return oakSeeds;
	}

	public T6 getDeciduousSeeds() {
		return deciduousSeeds;
	}

	public T7 getWater() {
		return water;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aspect == null) ? 0 : aspect.hashCode());
		result = prime * result
				+ ((deciduousSeeds == null) ? 0 : deciduousSeeds.hashCode());
		result = prime * result
				+ ((oakSeeds == null) ? 0 : oakSeeds.hashCode());
		result = prime * result
				+ ((pineSeeds == null) ? 0 : pineSeeds.hashCode());
		result = prime * result
				+ ((startState == null) ? 0 : startState.hashCode());
		result = prime * result + ((successionPathway == null) ? 0
				: successionPathway.hashCode());
		result = prime * result + ((water == null) ? 0 : water.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnvironmentalAntecedent<T1, T2, T3, T4, T5, T6, T7> other = (EnvironmentalAntecedent<T1, T2, T3, T4, T5, T6, T7>) obj;
		if (aspect == null) {
			if (other.aspect != null)
				return false;
		} else if (!aspect.equals(other.aspect))
			return false;
		if (deciduousSeeds == null) {
			if (other.deciduousSeeds != null)
				return false;
		} else if (!deciduousSeeds.equals(other.deciduousSeeds))
			return false;
		if (oakSeeds == null) {
			if (other.oakSeeds != null)
				return false;
		} else if (!oakSeeds.equals(other.oakSeeds))
			return false;
		if (pineSeeds == null) {
			if (other.pineSeeds != null)
				return false;
		} else if (!pineSeeds.equals(other.pineSeeds))
			return false;
		if (startState == null) {
			if (other.startState != null)
				return false;
		} else if (!startState.equals(other.startState))
			return false;
		if (successionPathway == null) {
			if (other.successionPathway != null)
				return false;
		} else if (!successionPathway.equals(other.successionPathway))
			return false;
		if (water == null) {
			if (other.water != null)
				return false;
		} else if (!water.equals(other.water))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EnvironmentalAntecedent [startState=" + startState
				+ ", successionPathway=" + successionPathway + ", aspect="
				+ aspect + ", pineSeeds=" + pineSeeds + ", oakSeeds=" + oakSeeds
				+ ", deciduousSeeds=" + deciduousSeeds + ", water=" + water
				+ "]";
	}
}
