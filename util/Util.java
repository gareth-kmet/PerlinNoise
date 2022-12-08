package util;

import java.util.Random;

public final class Util {
	/**
	 * No initializing
	 */
	private Util() {}
	
	/**
	 * Gets the i-th random float of a given seed 
	 * <br>Will reset the seed of the random generator beforehand but not after
	 * @param index - the index to retrieve the float
	 * @param random - the random generator to use
	 * @param seed - the seed to use for the random generator
	 * @param bound - the bound of the random float
	 * @return <b><code>float</code></b> - the i-th random float
	 */
	public static float getRandomFloatAtIndex(int index, Random random, long seed, float bound) {
		random.setSeed(seed);
		for(int i=0; i<=index; i++) {
			float f = random.nextFloat(bound);
			if(i==index) {
				return f;
			}
		}
		return -1; // DEAD CODE, only here to avoid syntax errors
	}
	
	/**
	 * Converts a Cartesian location into an index on a spiral. Each different location will result in a different index
	 * <br>Taken from <a href="https://stackoverflow.com/questions/9970134/get-spiral-index-from-location">here</a>
	 * @param x - The x location
	 * @param y - The y location
	 * @return <b><code>int</code></b> - the index of the location on the spiral
	 */
	public static int pointToSpiral(int x, int y) {
		int p;
		if(y*y >= x*x) {
			p = 4*y*y - y - x;
			if (y<x) {
				p-=2*(y-x);
			}
		}else {
			p = 4*x*x - y - x;
			if(y<x) {
				p+=2*(y-x);
			}
		}
		return p;
	}
	
}
