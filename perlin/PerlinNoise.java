package perlin;

import java.util.Random;

import util.Util;
import util.Vector2f;

/**
 * Built for static generation
 * 
 * Perlin Noise algorithm built for a single chunk with infinite generation
 * <br> Can be used multiple times on the same chunk
 * <br> Uses strict octaves and lacunarity as octave chunks must remain within the main chunk's borders with no half pixels
 * <br>
 * <br>Adapted from the algorithm mentioned in <a href="https://www.youtube.com/watch?v=ZsEnnB2wrbI"> this video</a>
 * @author Gareth Kmet
 *
 */
public final class PerlinNoise {
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
	/**
	 * The chunk of this instance
	 */
	private final PerlinChunk chunk;
	
	private int octaves = 1;
	private int lacunarity = 1;
	private float persistence = 1;
	
	/**
	 * Represents all the minor chunks of the objects
	 * <br>Is <code>null</code> if {@link #octaves} is <code>0</code>
	 */
	private PerlinNoise[][] nextOctave = null;
	
	
	/**
	 * Generates a new PerlinNoise instance with a new generated chunk
	 * 
	 * @param cx - the chunk x location
	 * @param cy - the chunk y location
	 * @param psize - the amount of pixels that the chunk is wide
	 */
	public PerlinNoise(int cx, int cy, int psize) {
		random = new Random();
		chunk = new PerlinChunk(cx,cy,psize);
	}
	
	/**
	 * Sets this instance and all octaves' instances' octaves, lacunarity, and persistence
	 * 
	 * @param octaves - the number of octaves to go through, must be greater than 0
	 * @param lacunarity - the number of chunks that go into the width of each minor chunk of an octave, <code>psize</code> of each minor chunk must be divisible by this number
	 * @param persistence - the exponential strength of each octave, should be less than 1
	 */
	public void setOctaves(int octaves, int lacunarity, float persistence) {
		this.setOctaves(octaves, lacunarity);
		this.setPersistence(persistence);
	}
	
	/**
	 * Sets this instance and all octaves' instances' persistence
	 * @param persistence - the exponential strength of each octave, should be less than 1
	 */
	public void setPersistence(float persistence) {
		if(persistence == this.persistence) return;
		
		this.persistence=persistence;
		if(nextOctave!=null) {
			for(PerlinNoise[] is : nextOctave) {for(PerlinNoise i : is) {
				i.setPersistence(persistence);
			}}
		}
	}
	
	/**
	 * Sets this instance and all octaves' instances' octaves and lacunarity
	 * 
	 * @param octaves - the number of octaves to go through, must be greater than 0
	 * @param lacunarity - the number of chunks that go into the width of each minor chunk of an octave, <code>psize</code> of each minor chunk must be divisible by this number
	 */
	public void setOctaves(int octaves, int lacunarity) {
		if(octaves==this.octaves) return;
		
		if(lacunarity==this.lacunarity) {
			setOctaves(octaves);
		}
		
		if(nextOctave != null) {
			for(PerlinNoise[] is : nextOctave) {for(PerlinNoise i:is) {
				i.delete();
			}}
		}
		
		this.octaves=octaves;
		this.lacunarity=lacunarity;
		
		if(octaves==1) {
			nextOctave = null;
			return;
		}
		
		nextOctave = new PerlinNoise[lacunarity][lacunarity];
		
		for(int i=0; i<lacunarity; i++) {for(int j=0; j<lacunarity;j++) {
			int x = chunk.x*lacunarity+i;
			int y = chunk.y*lacunarity+j;
			int psize = chunk.pixelSize/lacunarity;
			nextOctave[i][j] = new PerlinNoise(x,y,psize);
			nextOctave[i][j].setOctaves(octaves-1, lacunarity);
		}}
	}
	
	/**
	 * Sets this instance and all octaves' instances' octaves
	 * @param octaves - the number of octaves to go through, must be greater than 0
	 */
	public void setOctaves(int octaves) {
		//TODO
	}
	
	/**
	 * Runs the perlin algorithm on this chunk
	 * @param seed - the seed to randomly generate influence vectors
	 * @return {@link #PerlinRecord}
	 */
	public PerlinReturn perlin(long seed) {
		genInfluenceVectors(seed);
		
		float[][] pixs = Perlinification.perlinAChunk(chunk);
		float[][] octavePixs = perlinOctave(seed);
		
		float max = Float.NEGATIVE_INFINITY, min=Float.POSITIVE_INFINITY;
		
		for(int i=0; i<chunk.pixelSize; i++) {for(int j=0; j<chunk.pixelSize; j++) {
			pixs[i][j]+=octavePixs[i][j]*persistence;
			float f=pixs[i][j];
			max = Math.max(max, f);
			min = Math.min(min, f);
		}}
		
		return new PerlinReturn(pixs,max,min);
	}
	
	/**
	 * Runs the perlin algorithm on all octave chunks and creates a <code>float[][]</code> containing all results
	 * @param seed - the seed for the octaves' perlin algorithm
	 * @return <b><code>float[][]</code></b> - the square grid of final results of all octaves with a size of <code>chunk.pixelSize</code>
	 */
	private float[][] perlinOctave(long seed){
		seed += 1;
		
		float[][] pixs = new float[chunk.pixelSize][chunk.pixelSize];
		
		if(nextOctave==null) {
			return pixs;
		}
		
		for(int i=0; i<lacunarity; i++) {for(int j=0; j<lacunarity; j++) {
			PerlinNoise p = nextOctave[i][j];
			int psize = p.chunk.pixelSize;
			
			float[][] pr = p.perlin(seed).values;
			
			for(int x=0;x<psize;x++) {for(int y=0; y<psize;y++) {
				pixs[i*psize+x][j*psize+y] = pr[x][y];
			}}
		}}
		
		return pixs;
		
	}
	
	/**
	 * Generates influence vectors for this instance's chunk
	 * <p>Influence vectors will be a normalized vector in a random direction
	 * <br>One influence vector is associated with each {@link PerlinNoise#MASKS}
	 * @param seed - the seed to use for the random generation of the influence vectors
	 */
	private void genInfluenceVectors(long seed) {
		int[] index = genInfluenceVectorIndecies();
		
		Vector2f[] vecs = new Vector2f[4];
		for(int i=0; i<4; i++) {
			float f = Util.getRandomFloatAtIndex(index[i], random, seed, 2*(float)Math.PI);
			vecs[i] = Vector2f.fromPolar(1, f);
		}
		
		chunk.setInfluencevectors(vecs);
	}
	
	/**
	 * Converts the Cartesian location of each corner of the chunk into a location on a spiral.
	 * @return <b><code>int[]</code></b> - an array containing the spiral index of each point of the {@link #MASKS}
	 */
	private int[] genInfluenceVectorIndecies() {
		
		int x = chunk.x, y=chunk.y;
		int[] i = new int[MASKS];
		i[TL] = Util.pointToSpiral(x  , y  );
		i[TR] = Util.pointToSpiral(x+1, y  );
		i[BL] = Util.pointToSpiral(x  , y+1);
		i[BR] = Util.pointToSpiral(x+1, y+1);
		
		return i;
		
	}
	
	/**
	 * Generates the distance vector of a pixel to its corner given by the mask
	 * @param mask - one of the {@link #MASKS}
	 * @param pixel - a {@link Vector2f} representing the pixels location within the chunk
	 * @param pixelSize - equivalent to the {@link PerlinChunk#pixelSize}
	 * @return {@link Vector2f} - the distance vector of the pixel to the corner
	 */
	static final Vector2f getPixelVectors(int mask, Vector2f pixel, int pixelSize){
		switch (mask){
			case TL:
				return Vector2f.sub(new Vector2f(0,0),pixel);
			case TR:
				return Vector2f.sub(new Vector2f(pixelSize,0),pixel);
			case BL:
				return Vector2f.sub(new Vector2f(0,pixelSize),pixel);
			case BR:
				return Vector2f.sub(new Vector2f(pixelSize,pixelSize),pixel);
			default:
				return Vector2f.zero();
		}
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
	
	/**
	 * Deletes the PerlinNoise instance after usage
	 * <br>Does not delete the chunks
	 */
	public void delete() {
		if (nextOctave!=null) {
			for(PerlinNoise[] is : nextOctave) {for(PerlinNoise i:is) {
				i.delete();
			}}
		}
	}
	
}
