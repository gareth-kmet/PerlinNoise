package util;

import java.util.Random;

/**
 * 
 * @author Gareth Kmet
 *
 */
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
	 * Gets the i-th random int of a given seed 
	 * <br>Will reset the seed of the random generator beforehand but not after
	 * @param index - the index to retrieve the int
	 * @param random - the random generator to use
	 * @param seed - the seed to use for the random generator
	 * @param bound - the bound of the random int
	 * @return <b><code>float</code></b> - the i-th random float
	 */
	public static int getRandomIntAtIndex(int index, Random random, long seed, int bound) {
		random.setSeed(seed);
		for(int i=0; i<=index; i++) {
			int f = random.nextInt(bound);
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

	/**
	 * Lerps two values together with a smoothing function given a proportional between 0-1.
	 * <br>Uses a smoothing function to result in a non-linear lerp so that the edges of a chunk will not have kinks<ul>
	 * @param val1 - the first value
	 * @param val2 - the second value
	 * @param aProp - the linear proportion of the location between the two values
	 * @return <b><code>Vectornf</code></b> - the resulting value
	 */
	public static Vectornf lerps(Vectornf val1, Vectornf val2, float aProp) {
		float a = (float)(6*Math.pow(aProp, 5)-15*Math.pow(aProp, 4)+10*Math.pow(aProp, 3));
		return Vectornf.lerp(val1, val2, a);
	}
	
}
