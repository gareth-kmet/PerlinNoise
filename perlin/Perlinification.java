package perlin;

import util.InnerProductFloatVectorSpace;
import util.Util;
import util.Vector2f;
import util.Vector2v;

/**
 * Class of static methods to be used by the {@link PerlinNoise} algorithm
 * 
 * @author Gareth Kmet
 */
public final class Perlinification {
	/**
	 * No initializing
	 */
	private Perlinification() {}

	/**
	 * Generates and stores the pixel masks for the chunk
	 * 
	 * @author     Gareth Kmet
	 *
	 * @param  <E>
	 *             The inner-product vector space that is being used
	 */
	static final class Masks<E extends InnerProductFloatVectorSpace<E>> {
		final E[][] TL, TR, BL, BR;

		final E[][][] m;

		/**
		 * Generates empty masks
		 * 
		 * @param size
		 *             - {@link PerlinChunk#pixelSize}
		 */
		@SuppressWarnings("unchecked")
		private Masks(int size) {
			m = (E[][][]) new InnerProductFloatVectorSpace[PerlinNoise.MASKS][][];
			TL = (E[][]) new InnerProductFloatVectorSpace[size][size];
			m[PerlinNoise.TL] = TL;
			BL = (E[][]) new InnerProductFloatVectorSpace[size][size];
			m[PerlinNoise.BL] = BL;
			TR = (E[][]) new InnerProductFloatVectorSpace[size][size];
			m[PerlinNoise.TR] = TR;
			BR = (E[][]) new InnerProductFloatVectorSpace[size][size];
			m[PerlinNoise.BR] = BR;
		}

	}

	/**
	 * Runs the perlin algorithm on the chunk
	 * <p>
	 * This algorithm first dot-products the individual pixels' distance vector
	 * and influence vector for each {@link PerlinNoise#MASKS}. Then it
	 * horizontally lerps the top and bottom masks respectively and then
	 * vertically lerps those two new masks together
	 * 
	 * @param  <E>
	 *                The inner product space
	 * @param  invecs
	 *                The given influence vectors
	 * @param  oct
	 *                The octave
	 * 
	 * @return        A 2D array of generated vectors for this chunk
	 */
	static <E extends InnerProductFloatVectorSpace<E>> E[][] perlinAChunk(Vector2v<E>[] invecs, PerlinOctave oct) {
		Masks<E> chunkMask = new Masks<E>(oct.psize());
		for (int i = 0; i < PerlinNoise.MASKS; i++) { perlinAMask(i, chunkMask, oct, invecs); }

		E[][] mT = lerpMs(chunkMask.TL, chunkMask.TR, oct.psize(), true);
		E[][] mB = lerpMs(chunkMask.BL, chunkMask.BR, oct.psize(), true);
		E[][] mask = lerpMs(mT, mB, oct.psize(), false);

		return mask;

	}

	/**
	 * Lerps two masks together
	 * 
	 * @param  <E>
	 *              The inner product space
	 * @param  m1
	 *              The first mask
	 * @param  m2
	 *              The second mask
	 * @param  size
	 *              The width of the mask, {@link PerlinChunk#pixelSize}
	 * @param  lr
	 *              The directionality of the lerp (<code>true</code> for
	 *              horizontal and <code>false</code> for vertical)
	 * 
	 * @return      The resulting mask
	 */
	@SuppressWarnings("unchecked")
	private static <E extends InnerProductFloatVectorSpace<E>> E[][] lerpMs(E[][] m1, E[][] m2, int size, boolean lr) {
		float psize = 1f / size;

		E[][] mask = (E[][]) new InnerProductFloatVectorSpace[size][size];

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				float aProp = (lr ? x : y) * psize;
				mask[x][y] = Util.<E>lerps(m1[x][y], m2[x][y], aProp);
			}
		}

		return mask;
	}

	/**
	 * Runs the dot-product of the pixel distance vector and the influence
	 * vector of the chunk respective of a given mask
	 * 
	 * @param <E>
	 *               The inner product space
	 * @param mask
	 *               The index of the current mask to be perlined
	 * @param masks
	 *               The {@link PerlinNoise#MASKS}
	 * @param oct
	 *               The octave data
	 * @param invecs
	 *               The influence vectors
	 */
	private static <E extends InnerProductFloatVectorSpace<E>> void perlinAMask(int mask, Masks<E> masks,
			PerlinOctave oct, Vector2v<E>[] invecs) {
		for (int x = 0; x < oct.psize(); x++) {
			for (int y = 0; y < oct.psize(); y++) {
				Vector2f pixelMaskVector = oct.pixelDistanceVectors()[mask][x][y];
				masks.m[mask][x][y] = Vector2v.<E>dot(invecs[mask], pixelMaskVector);
			}
		}
	}

	/**
	 * Overrides the default methods to find an influence vector at a given
	 * location
	 * <p>
	 * Methods can return <code>null</code> to use the default methods for a
	 * chunk <br>
	 * Methods should return the same value for equal spiral indices
	 * 
	 * @author     Gareth Kmet
	 * 
	 * @param  <E>
	 *             The inner product space
	 */
	public interface PerlinInfluenceGenerator<E extends InnerProductFloatVectorSpace<E>> {
		/**
		 * Returns an influence for a given chunk corner on the first octave
		 * 
		 * @param  seed
		 *                     The seed that would be used to generate the
		 *                     random index
		 * @param  spiralIndex
		 *                     The unique index of the corner
		 * @param  cx
		 *                     The x position of the chunk
		 * @param  cy
		 *                     The y position of a chunk
		 * @param  mask
		 *                     The {@link PerlinNoise#MASKS}
		 * 
		 * @return             The influence vector to be used for this corner
		 *                     or <code>null</code> to use the default methods
		 */
		public E perlinMainInfluenceVector(long seed, int spiralIndex, int cx, int cy, int mask);

		/**
		 * Returns an influence vector for a given chunk corner on the
		 * subsequent octaves
		 * 
		 * @param  seed
		 *                     The seed that would be used to generate the
		 *                     random index
		 * @param  spiralIndex
		 *                     The unique index of the corner
		 * @param  mask
		 *                     The {@link PerlinNoise#MASKS}
		 * @param  octData
		 *                     The {@link PerlinOctaveChunkData} of the octave
		 *                     chunk
		 * 
		 * @return             The influence vector to be used for this corner
		 *                     or <code>null</code> to use the default methods
		 */
		public default E perlinOctInfluenceVector(long seed, int spiralIndex, int mask,
				PerlinNoise.PerlinOctaveChunkData octData) {
			return null;
		}
	}

}
