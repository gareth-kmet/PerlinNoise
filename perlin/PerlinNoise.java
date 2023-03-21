package perlin;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Predicate;

import perlin.Perlinification.PerlinInfluenceGenerator;
import util.Util;
import util.Vector2f;
import util.Vector2v;
import util.Vectornf;

/**
 * Perlin Noise algorithm built for infinite chunk generation <br>
 * Uses strict octaves and lacunarity as octave chunks must remain within the
 * main chunk's borders with no half pixels <br>
 * Adapted from the algorithm mentioned in
 * <a href="https://www.youtube.com/watch?v=ZsEnnB2wrbI"> this video</a>
 * <p>
 * This algorithm will take in a size of influence vector, <code>s</code>, and a
 * chunk size, <code>cs</code>. It will then run perlin noise on a given chunk
 * location with each corner given an influence vector equal to
 * <code>(ax,bx)</code> where <code>a,b</code> are two normalized floats and
 * <code>x</code> is a random possible influence vector of dimension
 * <code>s</code>. <br>
 * This will then return pixel array of square size <code>cs</code> which
 * contains a vector of size <code>s</code> for each pixel. <br>
 * <i>Note that the value-vectors are not normalized but can be using the given
 * methods in the returned record</i>
 * <p>
 * This algorithm can guarantee that (if the possibility set is linearly
 * independent) an linear independent vector which is missing for the
 * possibility set will not be present in the results <br>
 * Although, this is not true if the normalization method is used from the
 * results
 * <p>
 * This algorithm can only guarantee that the absolute value of all components
 * of the resulting vectors are <i>strictly</i> less than
 * <p>
 * <code><u><center>		
 *    sqrt(1/2)x</u><br>
 *      1-p</center></code> <br>
 * Where <code>p</code> is the persistence of the algorithm and <code>x</code>
 * is the maximal length of the influence vectors <br>
 * More precisely, all absolute values are <i>weakly</i> less than
 * <p>
 * <code><u><center>sqrt(1/2)(1-p<sup>c</sup>)x</u><br>
 * 							1-p</center></code> <br>
 * Where <code>c</code> is the octave count of the algorithm
 * <p>
 * It can also be noted that this algorithm approximately normally distributes
 * the values within the interval, thus for normalized influence vectors, the
 * results will be generally within 1 and -1 <br>
 * Also, it can be noted that this algorithm is continuous and differentiable at
 * all values
 * 
 * @author Gareth Kmet
 */
public final class PerlinNoise implements PerlinInfluenceGenerator {
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
	private final Vectornf[] possibilities;

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

	private final PerlinInfluenceGenerator influenceGenerator;

	/**
	 * Generates a new PerlinNoise algorithm instance with a set square pixel
	 * size assuming that values can only take on a single float
	 * 
	 * @param psize
	 *              - the amount of pixels that the chunk is wide and tall
	 */
	public PerlinNoise(int psize) {
		this(psize, Vectornf.genStandardVectors(1));
	}

	/**
	 * Generates a new PerlinNoise algorithm instance with a set square pixel
	 * size
	 * 
	 * @param psize
	 *                      - the amount of pixels that the chunk is wide and
	 *                      tall
	 * @param possibilities
	 *                      - different {@link Vectornf} that the influence
	 *                      vectors can be
	 */
	public PerlinNoise(int psize, Vectornf[] possibilities) {
		this(psize, possibilities, null);
	}

	/**
	 * Generates a new PerlinNoise algorithm instance with a set square pixel
	 * size
	 * 
	 * @param psize
	 *                      - the amount of pixels that the chunk is wide and
	 *                      tall
	 * @param possibilities
	 *                      - different {@link Vectornf} that the influence
	 *                      vectors can be
	 * @param influence
	 *                      - a {@link PerlinInfluenceGenerator}, uses the
	 *                      default methods if null
	 */
	public PerlinNoise(int psize, Vectornf[] possibilities, PerlinInfluenceGenerator influence) {
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
	 *                   - the number of octaves to go through
	 * @param lacunarity
	 *                   - the number of subdivisions of chunks per octave
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
	 *                - the number of octaves to go through
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
	 *                   - the number of subdivisions of chunks per octave
	 */
	public void setLacunarity(int lacunarity) {
		setOctaves(octaves, lacunarity);
	}

	/**
	 * Sets the persistence of the algorithm
	 * 
	 * @param persistence
	 *                    - the exponential strength of each octave, should be
	 *                    less than 1
	 */
	public void setPersistence(float persistence) { this.persistence = persistence; }

	/**
	 * Sets the octaves, lacunarity, and persistence of the algorithm <br>
	 * <b>Assertion:</b> the {@link PerlinOctave#psize} must be divisible by
	 * <code>lacunarity^octave</code>
	 * 
	 * @param octaves
	 *                    - the number of octaves to go through
	 * @param lacunarity
	 *                    - the number of subdivisions of chunks per octave
	 * @param persistence
	 *                    - the exponential strength of each octave, should be
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
	 *              - the seed to randomly generate influence vectors
	 * @param  cx
	 *              - the x position of the chunk
	 * @param  cy
	 *              - the y position of the chunk
	 * 
	 * @return      {@link PerlinReturn} - the values and relative min/max of
	 *              the result
	 */
	public PerlinReturn perlin(long seed, int cx, int cy) {
		PerlinOctave oct = octaveDataSets[0];
		PerlinOctaveChunkData d = new PerlinOctaveChunkData(0, cx, cy, cx, cy, null);

		Vectornf[][] values = new Vectornf[oct.psize()][oct.psize()];

		Vector2v[] invecs = genInfluenceVectors(seed, d);
		Vectornf[][] pixs = Perlinification.perlinAChunk(invecs, oct);

		Vectornf[][] subOctPixs = null;
		if (octaves > 1) { subOctPixs = perlinOctave(seed + 1, d); }

		float max = Float.NEGATIVE_INFINITY, min = Float.POSITIVE_INFINITY;

		for (int x = 0; x < oct.psize(); x++) {
			for (int y = 0; y < oct.psize(); y++) {
				Vectornf p = pixs[x][y];
				if (subOctPixs != null) { p = Vectornf.add(p, Vectornf.scale(subOctPixs[x][y], persistence)); }
				values[x][y] = p;
				for (int i = 0; i < p.size(); i++) {
					max = Math.max(max, p.get(i));
					min = Math.min(min, p.get(i));
				}

			}
		}

		return new PerlinReturn(values, max, min);
	}

	/**
	 * Runs the perlin algorithm recursively on the suboctaves until there are
	 * no more octaves
	 * 
	 * @param  seed
	 *                - the seed to generate random influence vectors
	 * @param  parent
	 *                - the octave chunk data of the previous octave
	 * 
	 * @return        <code>Vectornf[][]</code> - array with total values of all
	 *                pixels in the octave. Size of the square array is equal to
	 *                <code>oct(n-1).psize()</code>
	 */
	private Vectornf[][] perlinOctave(long seed, PerlinOctaveChunkData parent) {
		int octn = parent.octLevel + 1;

		PerlinOctave oct = octaveDataSets[octn];

		Vectornf[][] values = new Vectornf[oct.psize() * lacunarity][oct.psize() * lacunarity];

		for (int i = 0; i < lacunarity; i++) {
			for (int j = 0; j < lacunarity; j++) {
				int ocx = parent.cx * lacunarity + i;
				int ocy = parent.cy * lacunarity + j;
				PerlinOctaveChunkData thisC = new PerlinOctaveChunkData(octn, i, j, ocx, ocy, parent);

				Vector2v[] invecs = genInfluenceVectors(seed, thisC);
				Vectornf[][] pixs = Perlinification.perlinAChunk(invecs, oct);

				Vectornf[][] subOctPixs = null;
				if (octn < octaves - 1) { subOctPixs = perlinOctave(seed + 1, thisC); }

				for (int x = 0; x < oct.psize(); x++) {
					for (int y = 0; y < oct.psize(); y++) {
						Vectornf p = pixs[x][y];
						if (subOctPixs != null) { p = Vectornf.add(p, Vectornf.scale(subOctPixs[x][y], persistence)); }
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
	 *              - the seed to randomly generate the influence vectors
	 * @param  c
	 *              - the octave chunk data of the chunk calling this method
	 * 
	 * @return      <code>Vector2v[]</code> - an influence vector with length
	 *              equal to 1 for each {@link #MASKS}
	 */
	private Vector2v[] genInfluenceVectors(long seed, PerlinOctaveChunkData c) {
		int[] index = genInfluenceVectorIndecies(c.cx, c.cy);

		Vector2v[] vecs = new Vector2v[MASKS];
		for (int i = 0; i < MASKS; i++) {

			// Generate random float index for the angle of the index vector
			float f = Util.getRandomFloatAtIndex(index[i], random, seed, 2 * (float) Math.PI);

			Vectornf influence;
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
			vecs[i] = new Vector2v(Vectornf.scale(influence, p.x), Vectornf.scale(influence, p.y));
		}

		return vecs;
	}

	@Override
	public Vectornf perlinMainInfluenceVector(long seed, int spiralIndex, int cx, int cy, int mask) {
		int in = 0;
		if (runPossibilities)
			in = Util.getRandomIntAtIndex(spiralIndex, random, seed, possibilities.length);
		return possibilities[in];
	}

	@Override
	public Vectornf perlinOctInfluenceVector(long seed, int spiralIndex, int mask, PerlinOctaveChunkData octData) {
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
	 * The return value for the Perlin algorithm
	 * <p>
	 * Contains
	 * <ul>
	 * <li><b>values</b> - <code>Vectornf[][]</code> of all final
	 * <i>non-normalized</i> pixel values
	 * <li><b>max</b> - the max value of a pixel component within the chunk
	 * <li><b>min</b> - the min value of a pixel component within the chunk
	 * <li><b>getNormalizedValue(x,y)</b> - function whcih will return the value
	 * of a pixel so that each component is between 0 and 1 depending on the
	 * {@link PerlinReturn#min} and {@link PerlinReturn#max} values
	 * <li><b>getNormalizedValue(x,y,min,max)</b> - function whcih will
	 * normalize the value of a pixel depending on the given min and max values
	 * </ul>
	 */
	public record PerlinReturn(Vectornf[][] values, float max, float min) {
		public Vectornf getNormalizedValue(int px, int py) {
			return getNormalizedValue(px, py, max, min);
		}

		public Vectornf getNormalizedValue(int px, int py, float max, float min) {
			return Vectornf.scale(Vectornf.sub(values[px][py], Vectornf.Const(values[px][py].size(), min)),
					1f / (max - min));
		}
	}

	/**
	 * A data class which stores information about a current chunk in a current
	 * octave
	 * <p>
	 * Contains
	 * <ul>
	 * <li><b>octLevel</b> - the octave number where <code>0</code> is the main
	 * level</li>
	 * <li><b>rx</b> - the relative x position of this chunk within the parent
	 * chunk</li>
	 * <li><b>ry</b> - the relative y position of this chunk within the parent
	 * chunk</li>
	 * <li><b>cx</b> - the chunk x coordinate used for the generation of its
	 * unique index<br>
	 * <i>This is the same as <code>rx</code> for the main level</i></li>
	 * <li><b>cy</b> - the chunk y coordinate used for the generation of its
	 * unique index<br>
	 * <i>This is the same as <code>ry</code> for the main level</i></li>
	 * <li><b>parent</b> - the previous octave<br>
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
		 *                  - the condition
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