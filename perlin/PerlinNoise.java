package perlin;

import java.util.Random;

import util.Util;
import util.Vector2f;

/**
 * Perlin Noise algorithm built for infinite chunk generation
 * <br>Uses strict octaves and lacunarity as octave chunks must remain within the main chunk's borders with no half pixels
 * <br>Adapted from the algorithm mentioned in <a href="https://www.youtube.com/watch?v=ZsEnnB2wrbI"> this video</a>
 * @author Gareth Kmet
 *
 */
public final class PerlinNoise{
	/**
	 * Constants representing the index of corner masks used throughout
	 * <p><b>Masks</b> - Total number of masks
	 * <br><b>TL</b> - The top left mask index
	 * <br><b>TR</b> - The top right mask index
	 * <br><b>BL</b> - The bottom left mask index
	 * <br><b>BR</b> - The bottom right mask index
	 */
	static final int MASKS=4,
					TL=0, TR=1,
					BL=2, BR=3;
	
	/**
	 * The Random instance used by the Perlin Noise to generate chunk influence vectors
	 */
	private final Random random;
	
	private int octaves = 1;
	private int lacunarity = 1;
	private float persistence = 0.5f;
	
	private final int psize;
	
	private PerlinOctave[] octaveDataSets = {};
	
	/**
	 * Generates a new PerlinNoise algorithm instance with a set square pixel size
	 * @param psize - the amount of pixels that the chunk is wide and tall
	 */
	public PerlinNoise(int psize) {
		this.psize=psize;
		random = new Random();
		setOctaves(1,1);
	}
	
	/**
	 * Sets the octaves and lacunarity of the algorithm
	 * <br><b>Assertion:</b> the {@link PerlinOctave#psize} must be divisible by <code>lacunarity^octave</code>
	 * @param octaves - the number of octaves to go through
	 * @param lacunarity - the number of subdivisions of chunks per octave
	 */
	public void setOctaves(int octaves, int lacunarity) {
		octaveDataSets = new PerlinOctave[octaves];
		
		this.octaves = octaves;
		this.lacunarity=lacunarity;
		int psize = this.psize;
		for(int i=0; i<octaves;i++) {
			octaveDataSets[i] = new PerlinOctave(i, psize);
			psize/=lacunarity;
		}
		
	}
	
	/**
	 * Sets the octaves of the algorithm
	 * <br><b>Assertion:</b> the {@link PerlinOctave#psize} must be divisible by <code>lacunarity^octave</code>
	 * @param octaves - the number of octaves to go through
	 */
	public void setOctaves(int octaves) {
		setOctaves(octaves, lacunarity);
		//TODO optimize;
	}
	
	/**
	 * Sets the lacunarity of the algorithm
	 * <br><b>Assertion:</b> the {@link PerlinOctave#psize} must be divisible by <code>lacunarity^octave</code>
	 * @param lacunarity - the number of subdivisions of chunks per octave
	 */
	public void setLacunarity(int lacunarity) {
		setOctaves(octaves, lacunarity);
	}
	
	/**
	 * Sets the persistence of the algorithm
	 * @param persistence - the exponential strength of each octave, should be less than 1
	 */
	public void setPersistence(float persistence) {
		this.persistence=persistence;
	}
	/**
	 * Sets the octaves, lacunarity, and persistence of the algorithm
	 * <br><b>Assertion:</b> the {@link PerlinOctave#psize} must be divisible by <code>lacunarity^octave</code>
	 * @param octaves - the number of octaves to go through
	 * @param lacunarity - the number of subdivisions of chunks per octave
	 * @param persistence - the exponential strength of each octave, should be less than 1
	 */
	public void setOctaves(int octaves, int lacunarity, float persistence) {
		setOctaves(octaves, lacunarity);
		setPersistence(persistence);
	}
	
	/**
	 * Runs the perlin noise algorithm for a chunk located at position <code>(cx,cy)</code>
	 * @param seed - the seed to randomly generate influence vectors
	 * @param cx - the x position of the chunk
	 * @param cy - the y position of the chunk
	 * @return {@link PerlinReturn} - the values and relative min/max of the result
	 */
	public PerlinReturn perlin(long seed, int cx, int cy){
		PerlinOctave oct = octaveDataSets[0];
		
		float[][] values = new float[oct.psize()][oct.psize()];
		
		int ocx = cx;
		int ocy = cy;
		
		Vector2f[] invecs = genInfluenceVectors(seed, ocx, ocy);
		float[][] pixs = Perlinification.perlinAChunk(invecs, oct);
		
		float[][] subOctPixs;
		if(octaves>1) {
			subOctPixs = perlinOctave(seed+1, ocx, ocy, 1);
		}else {
			subOctPixs = new float[oct.psize()][oct.psize()];
		}
			
		
		float max = Float.NEGATIVE_INFINITY, min=Float.POSITIVE_INFINITY;
		
		for(int x=0;x<oct.psize();x++) {for(int y=0;y<oct.psize();y++) {
			float p = pixs[x][y] + subOctPixs[x][y] * persistence;
			values[x][y]=p;
			max = Math.max(max, p);
			min = Math.min(min, p);
		}}
		
		return new PerlinReturn(values,max,min);
	}
	
	/**
	 * Runs the perlin algorithm recursively on the suboctaves until there are no more octaves
	 * 
	 * @param seed - the seed to generate random influence vectors
	 * @param cx - the x position of the previous octave's chunk
	 * @param cy - the y position of the previous octave's chunk
	 * @param octn - the number of the current octave
	 * @return <code>float[][]</code> - array with total values of all pixels in the octave. Size of the square array is equal to <code>oct(n-1).psize()</code>
	 */
	private float[][] perlinOctave(long seed, int cx, int cy, int octn) {
		
		PerlinOctave oct = octaveDataSets[octn];
		
		float[][] values = new float[oct.psize()*lacunarity][oct.psize()*lacunarity];
		
		for(int i=0; i<lacunarity; i++) {for(int j=0; j<lacunarity; j++) {
			int ocx = cx*lacunarity+i;
			int ocy = cy*lacunarity+j;
			
			Vector2f[] invecs = genInfluenceVectors(seed, ocx, ocy);
			float[][] pixs = Perlinification.perlinAChunk(invecs, oct);
			
			float[][] subOctPixs;
			if(octn<octaves-1) {
				subOctPixs = perlinOctave(seed+1, ocx, ocy, octn+1);
			}else {
				subOctPixs = new float[oct.psize()][oct.psize()];
			}
			
			
			for(int x=0;x<oct.psize();x++) {for(int y=0;y<oct.psize();y++) {
				float p = pixs[x][y] + subOctPixs[x][y] * persistence;
				values[i*oct.psize()+x][j*oct.psize()+y]=p;
			}}
		}}
		
		return values;
	}
	
	/**
	 * Generates random normalized influence vectors for a chunk at a given position
	 * @param seed - the seed to randomly generate the influence vectors
	 * @param cx - the x position of the chunk
	 * @param cy - the y position of the chunk
	 * @return <code>Vector2f[]</code> - an influence vector with length equal to 1 for each {@link #MASKS}
	 */
	private Vector2f[] genInfluenceVectors(long seed, int cx, int cy) {
		int[] index = genInfluenceVectorIndecies(cx, cy);
		
		Vector2f[] vecs = new Vector2f[4];
		for(int i=0; i<4; i++) {
			float f = Util.getRandomFloatAtIndex(index[i], random, seed, 2*(float)Math.PI);
			vecs[i] = Vector2f.fromPolar(1, f);
		}
		
		return vecs;
	}
	
	/**
	 * Converts the Cartesian location of each corner of the chunk into a location on a spiral.
	 * @return <b><code>int[]</code></b> - an array containing the spiral index of each point of the {@link #MASKS}
	 */
	private int[] genInfluenceVectorIndecies(int x, int y) {
		
		int[] i = new int[MASKS];
		i[TL] = Util.pointToSpiral(x  , y  );
		i[TR] = Util.pointToSpiral(x+1, y  );
		i[BL] = Util.pointToSpiral(x  , y+1);
		i[BR] = Util.pointToSpiral(x+1, y+1);
		
		return i;
		
	}
	
	
	
	/**
	 * The return value for the Perlin algorithm
	 * <p>Contains
	 * <ul>
	 * 	<li><b>values</b> - <code>float[][]</code> of all final <i>non-normalized</i> pixel values
	 * 	<li><b>max</b> - the max value of a pixel within the chunk
	 * 	<li><b>min</b> - the min value of a pixel within the chunk
	 * 	<li><b>getNormalizedValue(x,y)</b> - function whcih will return the value of a pixel between 0 and 1 depending on the {@link PerlinReturn#min} and {@link PerlinReturn#max} values
	 * 	<li><b>getNormalizedValue(x,y,min,max)</b> - function whcih will normalize the value of a pixel depending on the given min and max values
	 * </ul>
	 *
	 */
	public record PerlinReturn(float[][] values, float max, float min) {
		public float getNormalizedValue(int px, int py) {
			return getNormalizedValue(px,py,max,min);
		}
		public float getNormalizedValue(int px, int py, float max, float min) {
			return (values[px][py]-min)/(max-min);
		}
	};
	
	
	
}