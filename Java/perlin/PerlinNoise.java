package perlin;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

import perlin.Perlinification.PerlinInfluenceGenerator;
import util.InnerProductFloatVectorSpace;
import util.Util;
import util.Vector2f;
import util.Vector2v;

/**
 * Perlin Noise algorithm built for infinite chunk generation <br>
 * Uses strict octaves and lacunarity as octave chunks must remain within the
 * main chunk's borders with no half pixels <br>
 * Adapted from the algorithm mentioned in
 * <a href="https://www.youtube.com/watch?v=ZsEnnB2wrbI"> this video</a>
 * <p>
 * This algorithm will take in a set of influence vectors and a chunk size,
 * <code>cs</code>. It will then run perlin noise on a given chunk location with
 * each corner given an influence vector equal to <code>(ax,bx)</code> where
 * <code>a,b</code> are two normalized floats and <code>x</code> is a random
 * possible influence vector of the given set. <br>
 * This will then return a pixel array of square size <code>cs</code> which
 * contains a vector from the same space as the influence vectors for each
 * pixel. <br>
 * <p>
 * This algorithm can guarantee that the output vectors will be contained within
 * the span of the influence vectors.
 * <p>
 * This algorithm can only guarantee that the norm of all components of the
 * resulting vectors are <i>strictly</i> less than
 * <p>
 * <code><u><center>		
 *    sqrt(1/2)x</u><br>
 *      1-p</center></code> <br>
 * Where <code>p</code> is the persistence of the algorithm and <code>x</code>
 * is the maximal norm of the influence vectors. <br>
 * More precisely, all norms are <i>weakly</i> less than
 * <p>
 * <code><u><center>sqrt(1/2)(1-p<sup>c</sup>)x</u><br>
 * 							1-p</center></code> <br>
 * Where <code>c</code> is the octave count of the algorithm
 * <p>
 * It can also be noted that this algorithm approximately normally distributes
 * the values within the interval, thus for normalized influence vectors, the
 * results will have a norm be generally (but not always) within 1.
 * <p>
 * Also, it can be noted that this algorithm is continuous and differentiable at
 * all values
 * 
 * @author     Gareth Kmet
 * 
 * @param  <E>
 *             The inner product space corresponding to the influence vectors
 *             and the output vectors
 */
public final class PerlinNoise<E extends InnerProductFloatVectorSpace<E>> implements PerlinInfluenceGenerator<E> {
	/**
	 * Constants representing the index of corner masks used throughout
	 * <p>
	 * <b>Masks</b> - Total number of masks <br>
	 * <b>TL</b> - The top left mask index <br>
	 * <b>TR</b> - The top right mask index <br>
	 * <b>BL</b> - The bottom left mask index <br>
	 * <b>BR</b> - The bottom right mask index
	 */
	public static final int MASKS = 4, TL = 0, TR = 1, BL = 2, BR = 3;

	/**
	 * The Random instance used by the Perlin Noise to generate chunk influence
	 * vectors
	 */
	private final Random random;

	/**
	 * The class of the inner product space
	 */
	private final Class<? extends E> cls;

	/**
	 * The number of recursive algorithms the perlin noise will go through
	 */
	private int octaves = 1;
	/**
	 * The number of subdivisions of chunks per octave (width and height)
	 */
	private int lacunarity = 1;
	/**
	 * The exponential strength of each octave on the final result
	 */
	private float persistence = 0.5f;

	/**
	 * Different {@link Vectornf} that the influence vectors can be
	 */
	private final E[] possibilities;

	/**
	 * The amount of pixels that the chunk is wide and tall
	 */
	private final int psize;

	/**
	 * The stored octave data
	 */
	private PerlinOctave[] octaveDataSets = {};

	/**
	 * Represents if it is necessary to run a random possibility search
	 */
	private final boolean runPossibilities;

	/**
	 * The generator for Influence Vectors for a given location
	 */
	private final PerlinInfluenceGenerator<E> influenceGenerator;

	/**
	 * Generates a new PerlinNoise algorithm instance with a set square pixel
	 * size
	 * 
	 * @param cls
	 *                      The class of the inner product space
	 * @param psize
	 *                      The amount of pixels that the chunk is wide and tall
	 * @param possibilities
	 *                      The set of influence vectors
	 */
	public PerlinNoise(Class<? extends E> cls, int psize, E[] possibilities) {
		this(cls, psize, possibilities, null);
	}

	/**
	 * Generates a new PerlinNoise algorithm instance with a set square pixel
	 * size
	 * 
	 * @param cls
	 *                      The class of the inner product space
	 * @param psize
	 *                      Tthe amount of pixels that the chunk is wide and
	 *                      tall
	 * @param possibilities
	 *                      The set of influence vectors
	 * @param influence
	 *                      A {@link PerlinInfluenceGenerator}, uses the default
	 *                      methods if null
	 */
	public PerlinNoise(Class<? extends E> cls, int psize, E[] possibilities, PerlinInfluenceGenerator<E> influence) {
		this.cls = cls;
		this.psize = psize;
		random = new Random();
		this.possibilities = possibilities;
		this.runPossibilities = possibilities.length > 1;
		setOctaves(1, 1);
		influenceGenerator = influence == null ? this : influence;
	}

	/**
	 * Sets the octaves and lacunarity of the algorithm <br>
	 * <b>Assertion:</b> the {@link PerlinOctave#psize} must be divisible by
	 * <code>lacunarity^octave</code>
	 * 
	 * @param octaves
	 *                   The number of octaves to go through
	 * @param lacunarity
	 *                   The number of subdivisions of chunks per octave
	 */
	public void setOctaves(int octaves, int lacunarity) {
		/*
		 * if(octaves==this.octaves && lacunarity==this.lacunarity) { return;
		 * }else if(lacunarity==this.lacunarity || octaves == 1) {
		 * setOctaves(octaves); return; }
		 */
		octaveDataSets = new PerlinOctave[octaves];

		this.octaves = octaves;
		this.lacunarity = lacunarity;
		int psize = this.psize;
		for (int i = 0; i < octaves; i++) {
			octaveDataSets[i] = new PerlinOctave(i, psize);
			psize /= lacunarity;
		}

	}

	/**
	 * Sets the octaves of the algorithm <br>
	 * <b>Assertion:</b> the {@link PerlinOctave#psize} must be divisible by
	 * <code>lacunarity^octave</code>
	 * 
	 * @param octaves
	 *                The number of octaves to go through
	 */
	@Deprecated
	public void setOctaves(int octaves) {
		if (octaves == this.octaves) {
			return;
		} else if (octaves < this.octaves) {
			PerlinOctave[] n = new PerlinOctave[octaves];
			for (int i = 0; i < octaves; i++) { n[i] = octaveDataSets[i]; }
			octaveDataSets = n;
		} else {
			PerlinOctave[] n = Arrays.copyOf(octaveDataSets, octaves);
			int psize = octaveDataSets[octaveDataSets.length - 1].psize();
			for (int i = this.octaves; i < octaves; i++) {
				psize /= lacunarity;
				n[i] = new PerlinOctave(i, psize);
			}
			octaveDataSets = n;
		}

		this.octaves = octaves;
	}

	/**
	 * Sets the lacunarity of the algorithm <br>
	 * <b>Assertion:</b> the {@link PerlinOctave#psize} must be divisible by
	 * <code>lacunarity^octave</code>
	 * 
	 * @param lacunarity
	 *                   The number of subdivisions of chunks per octave
	 */
	public void setLacunarity(int lacunarity) {
		setOctaves(octaves, lacunarity);
	}

	/**
	 * Sets the persistence of the algorithm
	 * 
	 * @param persistence
	 *                    The exponential strength of each octave, should be
	 *                    less than 1
	 */
	public void setPersistence(float persistence) { this.persistence = persistence; }

	/**
	 * Sets the octaves, lacunarity, and persistence of the algorithm <br>
	 * <b>Assertion:</b> the {@link PerlinOctave#psize} must be divisible by
	 * <code>lacunarity^octave</code>
	 * 
	 * @param octaves
	 *                    The number of octaves to go through
	 * @param lacunarity
	 *                    The number of subdivisions of chunks per octave
	 * @param persistence
	 *                    The exponential strength of each octave, should be
	 *                    less than 1
	 */
	public void setOctaves(int octaves, int lacunarity, float persistence) {
		setOctaves(octaves, lacunarity);
		setPersistence(persistence);
	}

	/**
	 * Runs the perlin noise algorithm for a chunk located at position
	 * <code>(cx,cy)</code>
	 * 
	 * @param  seed
	 *                  The seed to randomly generate influence vectors
	 * @param  cx
	 *                  The x position of the chunk
	 * @param  cy
	 *                  The y position of the chunk
	 * @param  consumer
	 *                  A consumer for each output vector. This will be called
	 *                  once for each vector that is found in the output grid
	 * 
	 * @return          {@link PerlinReturn} - a 2D grid of the output vectors
	 *                  of the result
	 */
	public E[][] perlin(long seed, int cx, int cy, Consumer<E> consumer) {
		PerlinOctave oct = octaveDataSets[0];
		PerlinOctaveChunkData d = new PerlinOctaveChunkData(0, cx, cy, cx, cy, null);

		@SuppressWarnings("unchecked")
		E[][] values = (E[][]) Array.newInstance(cls, oct.psize(), oct.psize());

		Vector2v<E>[] invecs = genInfluenceVectors(seed, d);
		E[][] pixs = Perlinification.perlinAChunk(invecs, oct);

		E[][] subOctPixs = null;
		if (octaves > 1) { subOctPixs = perlinOctave(seed + 1, d); }

		for (int x = 0; x < oct.psize(); x++) {
			for (int y = 0; y < oct.psize(); y++) {
				E p = pixs[x][y];
				if (subOctPixs != null) { p = p.add(subOctPixs[x][y].scale(persistence)); }
				values[x][y] = p;
				consumer.accept(p);
			}
		}

		return values;
	}

	/**
	 * Runs the perlin algorithm recursively on the suboctaves until there are
	 * no more octaves
	 * 
	 * @param  seed
	 *                The seed to generate random influence vectors
	 * @param  parent
	 *                The octave chunk data of the previous octave
	 * 
	 * @return        A 2D array with the output vectors for this octave. Size
	 *                of the square array is equal to
	 *                <code>oct(n-1).psize()</code>
	 */
	private E[][] perlinOctave(long seed, PerlinOctaveChunkData parent) {
		int octn = parent.octLevel + 1;

		PerlinOctave oct = octaveDataSets[octn];

		@SuppressWarnings("unchecked")
		E[][] values = (E[][]) new InnerProductFloatVectorSpace[oct.psize() * lacunarity][oct.psize() * lacunarity];

		for (int i = 0; i < lacunarity; i++) {
			for (int j = 0; j < lacunarity; j++) {
				int ocx = parent.cx * lacunarity + i;
				int ocy = parent.cy * lacunarity + j;
				PerlinOctaveChunkData thisC = new PerlinOctaveChunkData(octn, i, j, ocx, ocy, parent);

				Vector2v<E>[] invecs = genInfluenceVectors(seed, thisC);
				E[][] pixs = Perlinification.<E>perlinAChunk(invecs, oct);

				E[][] subOctPixs = null;
				if (octn < octaves - 1) { subOctPixs = perlinOctave(seed + 1, thisC); }

				for (int x = 0; x < oct.psize(); x++) {
					for (int y = 0; y < oct.psize(); y++) {
						E p = pixs[x][y];
						if (subOctPixs != null) { p = p.add(subOctPixs[x][y].scale(persistence)); }
						values[i * oct.psize() + x][j * oct.psize() + y] = p;
					}
				}
			}
		}

		return values;
	}

	/**
	 * Generates random normalized influence vectors for a chunk at a given
	 * position
	 * 
	 * @param  seed
	 *              The seed to randomly generate the influence vectors
	 * @param  c
	 *              The octave chunk data of the chunk calling this method
	 * 
	 * @return      An influence vector for each {@link #MASKS}
	 */
	private Vector2v<E>[] genInfluenceVectors(long seed, PerlinOctaveChunkData c) {
		int[] index = genInfluenceVectorIndecies(c.cx, c.cy);

		@SuppressWarnings("unchecked")
		Vector2v<E>[] vecs = new Vector2v[MASKS];
		for (int i = 0; i < MASKS; i++) {

			// Generate random float index for the angle of the index vector
			float f = Util.getRandomFloatAtIndex(index[i], random, seed, 2 * (float) Math.PI);

			E influence;
			if (!c.isMain()) {
				influence = influenceGenerator.perlinOctInfluenceVector(seed, index[i], i, c);
				if (influence == null)
					influence = this.perlinOctInfluenceVector(seed, index[i], i, c);
			} else {
				influence = influenceGenerator.perlinMainInfluenceVector(seed, index[i], c.cx, c.cy, i);
				if (influence == null)
					influence = this.perlinMainInfluenceVector(seed, index[i], c.cx, c.cy, i);
			}
			Vector2f p = Vector2f.fromPolar(1, f);
			vecs[i] = new Vector2v<E>(influence.scale(p.x), influence.scale(p.y));
		}

		return vecs;
	}

	@Override
	public E perlinMainInfluenceVector(long seed, int spiralIndex, int cx, int cy, int mask) {
		int in = 0;
		if (runPossibilities)
			in = Util.getRandomIntAtIndex(spiralIndex, random, seed, possibilities.length);
		return possibilities[in];
	}

	@Override
	public E perlinOctInfluenceVector(long seed, int spiralIndex, int mask, PerlinOctaveChunkData octData) {
		return perlinMainInfluenceVector(seed, spiralIndex, octData.cx, octData.cy, mask);
	}

	/**
	 * Converts the Cartesian location of each corner of the chunk into a
	 * location on a spiral.
	 * 
	 * @return <b><code>int[]</code></b> - an array containing the spiral index
	 *         of each point of the {@link #MASKS}
	 */
	private int[] genInfluenceVectorIndecies(int x, int y) {

		int[] i = new int[MASKS];
		i[TL] = Util.pointToSpiral(x, y);
		i[TR] = Util.pointToSpiral(x + 1, y);
		i[BL] = Util.pointToSpiral(x, y + 1);
		i[BR] = Util.pointToSpiral(x + 1, y + 1);

		return i;

	}

	/**
	 * A data class which stores information about a current chunk in a current
	 * octave
	 * <p>
	 * Contains
	 * <ul>
	 * <li><b>octLevel</b> The octave number where <code>0</code> is the main
	 * level</li>
	 * <li><b>rx</b> The relative x position of this chunk within the parent
	 * chunk</li>
	 * <li><b>ry</b> The relative y position of this chunk within the parent
	 * chunk</li>
	 * <li><b>cx</b> The chunk x coordinate used for the generation of its
	 * unique index<br>
	 * <i>This is the same as <code>rx</code> for the main level</i></li>
	 * <li><b>cy</b> The chunk y coordinate used for the generation of its
	 * unique index<br>
	 * <i>This is the same as <code>ry</code> for the main level</i></li>
	 * <li><b>parent</b> The previous octave<br>
	 * <i>This is <code>null</code> for the main level</i></li>
	 * </ul>
	 * 
	 * @author Gareth Kmet
	 */
	public record PerlinOctaveChunkData(int octLevel, int rx, int ry, int cx, int cy, PerlinOctaveChunkData parent) {
		/**
		 * Returns if this is the main level
		 * 
		 * @return <b><code>true</code></b> if this is the main level and
		 *         <b><code>false</code></b> if this is a subsequent octave
		 */
		public boolean isMain() { return parent == null; }

		/**
		 * Retrieves the main octave chunk data
		 * 
		 * @return The main level octave chunk data
		 */
		public PerlinOctaveChunkData getMain() {
			if (isMain())
				return this;
			return parent.getMain();
		}

		/**
		 * Runs a boolean <code>AND</code> operation on each octave. This starts
		 * from this instance and performs the operation on each parent octave
		 * 
		 * @param  consumer
		 *                  The condition
		 * 
		 * @return          <b><code>true</code></b> if <u>all</u> previous
		 *                  octaves and this match the given condition <br>
		 *                  <b><code>false</code></b> if at least one octave
		 *                  fails
		 */
		public boolean forEach(Predicate<PerlinOctaveChunkData> consumer) {
			boolean thisTest = consumer.test(this);
			if (!isMain()) { return thisTest && parent.forEach(consumer); }
			return thisTest;
		}
	}

}