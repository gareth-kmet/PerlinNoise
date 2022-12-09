package perlin;

import util.Vector2f;
import util.Vector2v;
import util.Vectornf;

/**
 * Class of static methods to be used by the {@link PerlinNoise} algorithm
 * @author Gareth Kmet
 *
 */
final class Perlinification {
	/**
	 * No initializing
	 */
	private Perlinification() {}
	
	/**
	 * Generates and stores the pixel masks for the chunk
	 * @author Gareth Kmet
	 *
	 */
	static final class Masks{
		final Vectornf[][]
				TL, TR,
				BL, BR;
		
		final Vectornf[][][] m = new Vectornf[PerlinNoise.MASKS][][];
		
		/**
		 * Generates empty masks
		 * @param size - {@link PerlinChunk#pixelSize}
		 */
		private Masks(int size){
			TL = new Vectornf[size][size]; m[PerlinNoise.TL]=TL;
			BL = new Vectornf[size][size]; m[PerlinNoise.BL]=BL;
			TR = new Vectornf[size][size]; m[PerlinNoise.TR]=TR;
			BR = new Vectornf[size][size]; m[PerlinNoise.BR]=BR;
		}
		
	}
	
	/**
	 * Runs the perlin algorithm on the chunk
	 * 
	 * <p>This algorithm first dot-products the individual pixels' distance vector and influence vector for each {@link PerlinNoise#MASKS}. 
	 * Then it horizontally lerps the top and bottom masks respectively and then vertically lerps those two new masks together
	 * @param chunk - {@link PerlinChunk}
	 * @return <b><code>float[][]</code></b> - the float array of the final pixel values
	 */
	static Vectornf[][] perlinAChunk(Vector2v[] invecs, PerlinOctave oct) {
		Masks chunkMask = new Masks(oct.psize());
		for(int i=0; i<PerlinNoise.MASKS; i++) {
			perlinAMask(i, chunkMask, oct, invecs);
		}
		
		Vectornf[][] mT = lerpMs(chunkMask.TL, chunkMask.TR, oct.psize(), true);
		Vectornf[][] mB = lerpMs(chunkMask.BL, chunkMask.BR, oct.psize(), true);
		Vectornf[][] mask = lerpMs(mT, mB, oct.psize(), false);
		
		return mask;
		
	}
	
	/**
	 * Lerps two <code>float[][]</code> masks together
	 * @param m1 - the first mask
	 * @param m2 - the second mask
	 * @param size - the width of the mask, {@link PerlinChunk#pixelSize}
	 * @param lr - the directionality of the lerp (<code>true</code> for horizontal and <code>false</code> for vertical)
	 * @return <code><b>float[][]</code></b> - the resulting mask
	 */
	private static Vectornf[][] lerpMs(Vectornf[][] m1, Vectornf[][] m2, int size, boolean lr){
		float psize = 1f/size;
		
		Vectornf[][] mask = new Vectornf[size][size];
		
		for(int x=0;x<size;x++) {
			for(int y=0;y<size;y++) {
				float aProp = (lr?x:y)*psize;
				mask[x][y] = lerp(m1[x][y],m2[x][y], aProp);
			}
		}
		
		return mask;
	}
	
	/**
	 * Lerps two values together given a proportional between 0-1.
	 * <br>Uses a smoothing function to result in a non-linear lerp so that the edges of a chunk will not have kinks<ul>
	 * @param val1 - the first value
	 * @param val2 - the second value
	 * @param aProp - the linear proportion of the location between the two values
	 * @return <b><code>float</code></b> - the resulting value
	 */
	private static Vectornf lerp(Vectornf val1, Vectornf val2, float aProp) {
		float a = (float)(6*Math.pow(aProp, 5)-15*Math.pow(aProp, 4)+10*Math.pow(aProp, 3));
		return Vectornf.add(val1, Vectornf.scale(Vectornf.sub(val2, val1), a));
	}
	
	/**
	 * Runs the dot-product of the pixel distance vector and the influence vector of the chunk respective of a given mask
	 * @param mask - {@link PerlinNoise#MASKS}
	 * @param masks - the chunk's pixel masks
	 * @param chunk - the chunk
	 */
	private static void perlinAMask(int mask, Masks masks, PerlinOctave oct, Vector2v[] invecs) {
		for(int x=0; x<oct.psize(); x++) {
			for(int y=0; y<oct.psize(); y++) {
				Vector2f pixelMaskVector = oct.pixelDistanceVectors()[mask][x][y];
				masks.m[mask][x][y] = Vector2v.dot(invecs[mask], pixelMaskVector);
			}
		}
	}
}
