package util;

import java.util.Random;

public abstract class Util {
	
	public static float getRandomFloatAtIndex(int index, Random random, long seed, float bound) {
		random.setSeed(seed);
		for(int i=0; i<=index; i++) {
			float f = random.nextFloat(bound);
			if(i==index) {
				return f;
			}
		}
		return -1;
	}
	
	/**
	 * Adapted from <a href="https://stackoverflow.com/questions/9970134/get-spiral-index-from-location">here</a>
	 * @param x
	 * @param y
	 * @return
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
