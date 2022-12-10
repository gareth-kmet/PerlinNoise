package perlin;

import java.util.Arrays;
import java.util.Random;

import perlin.Perlinification.PerlinGenMainInfluenceVectorPosibilityIndex;
import perlin.Perlinification.PerlinGenOctInfluenceVectorPosibilityIndex;
import util.Util;
import util.Vector2f;
import util.Vector2v;
import util.Vectornf;

/**
 * Perlin Noise algorithm built for infinite chunk generation
 * <br>Uses strict octaves and lacunarity as octave chunks must remain within the main chunk's borders with no half pixels
 * <br>Adapted from the algorithm mentioned in <a href="https://www.youtube.com/watch?v=ZsEnnB2wrbI"> this video</a>
 * 
 * <p>This algorithm will take in a size of influence vector, <code>s</code>, and a chunk size, <code>cs</code>.
 * It will then run perlin noise on a given chunk location with each corner given an influence vector equal to <code>(ax,bx)</code>
 * where <code>a,b</code> are two normalized floats and <code>x</code> is a random standard vector of dimension <code>s</code>.
 * <br>This will then return pixel array of square size <code>cs</code> which contains a vector of size <code>s</code> for each pixel.
 * <br><i>Note that the value-vectors are not normalized but can be using the given methods in the returned record</i>
 * @author Gareth Kmet
 *
 */
public final class PerlinNoise implements PerlinGenMainInfluenceVectorPosibilityIndex, PerlinGenOctInfluenceVectorPosibilityIndex{
	/**
	 * Constants representing the index of corner masks used throughout
	 * <p><b>Masks</b> - Total number of masks
	 * <br><b>TL</b> - The top left mask index
	 * <br><b>TR</b> - The top right mask index
	 * <br><b>BL</b> - The bottom left mask index
	 * <br><b>BR</b> - The bottom right mask index
	 */
	public static final int MASKS=4,
					TL=0, TR=1,
					BL=2, BR=3;
	
	/**
	 * The Random instance used by the Perlin Noise to generate chunk influence vectors
	 */
	private final Random random;
	
	/**
	 * The number of recursive algorithms the perlin noise will go throuhg
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
	
	private final PerlinGenMainInfluenceVectorPosibilityIndex mainPosIndex;
	private final PerlinGenOctInfluenceVectorPosibilityIndex octPosIndex;
	
	/**
	 * Generates a new PerlinNoise algorithm instance with a set square pixel size assuming that values can only take on a single float
	 * 
	 * @param psize - the amount of pixels that the chunk is wide and tall
	 */
	public PerlinNoise(int psize) {
		this(psize, Vectornf.genStandardVectors(1));
	}
	
	/**
	 * Generates a new PerlinNoise algorithm instance with a set square pixel size
	 * 
	 * @param psize - the amount of pixels that the chunk is wide and tall
	 * @param possibilities - different {@link Vectornf} that the influence vectors can be
	 */
	public PerlinNoise(int psize, Vectornf[] possibilities) {
		this(psize, possibilities, null, null);
	}
	
	/**
	 * Generates a new PerlinNoise algorithm instance with a set square pixel size
	 * <br> Overrides the methods for influence vector choices
	 * 
	 * @param psize - the amount of pixels that the chunk is wide and tall
	 * @param possibilities - different {@link Vectornf} that the influence vectors can be
	 * @param m - {@link PerlinGenMainInfluenceVectorPosibilityIndex}, uses the default method if <code>null</code>
	 * @param o - {@link PerlinGenOctInfluenceVectorPosibilityIndex}, uses the default method if <code>null</code>
	 */
	public PerlinNoise(int psize, Vectornf[] possibilities, PerlinGenMainInfluenceVectorPosibilityIndex m, PerlinGenOctInfluenceVectorPosibilityIndex o) {
		this.psize=psize;
		random = new Random();
		this.possibilities=possibilities;
		this.runPossibilities = possibilities.length>1;
		setOctaves(1,1);
		
		mainPosIndex = m==null?this:m;
		octPosIndex = o==null?this:o;
	}
	
	/**
	 * Sets the octaves and lacunarity of the algorithm
	 * <br><b>Assertion:</b> the {@link PerlinOctave#psize} must be divisible by <code>lacunarity^octave</code>
	 * @param octaves - the number of octaves to go through
	 * @param lacunarity - the number of subdivisions of chunks per octave
	 */
	public void setOctaves(int octaves, int lacunarity) {
		if(octaves==this.octaves && lacunarity==this.lacunarity) {
			return;
		}else if(lacunarity==this.lacunarity || octaves == 1) {
			setOctaves(octaves, lacunarity);
			return; 
		}
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
		if(octaves==this.octaves) {
			return;
		}else if(octaves<this.octaves) {
			PerlinOctave[] n = new PerlinOctave[octaves];
			for(int i=0;i<octaves;i++) {
				n[i]=octaveDataSets[i];
			}
			octaveDataSets=n;
		}else {
			PerlinOctave[] n=Arrays.copyOf(octaveDataSets, octaves);
			int psize = octaveDataSets[octaveDataSets.length-1].psize();
			for(int i=this.octaves; i<octaves;i++) {
				psize/=lacunarity;
				n[i] = new PerlinOctave(i, psize);
			}
			octaveDataSets=n;
		}
		
		this.octaves=octaves;
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
		
		Vectornf[][] values = new Vectornf[oct.psize()][oct.psize()];
		
		int ocx = cx;
		int ocy = cy;
		
		Vector2v[] invecs = genInfluenceVectors(seed, ocx, ocy, false);
		Vectornf[][] pixs = Perlinification.perlinAChunk(invecs, oct);
		
		Vectornf[][] subOctPixs = null;
		if(octaves>1) {
			subOctPixs = perlinOctave(seed+1, ocx, ocy, 1);
		}
			
		
		float max = Float.NEGATIVE_INFINITY, min=Float.POSITIVE_INFINITY;
		
		for(int x=0;x<oct.psize();x++) {for(int y=0;y<oct.psize();y++) {
			Vectornf p = pixs[x][y];
			if(subOctPixs != null) {
				p = Vectornf.add(p, Vectornf.scale(subOctPixs[x][y], persistence));
			}
			values[x][y]=p;
			for(int i=0; i<p.size(); i++) {
				max = Math.max(max, p.get(i));
				min = Math.min(min, p.get(i));
			}
			
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
	 * @return <code>Vectornf[][]</code> - array with total values of all pixels in the octave. Size of the square array is equal to <code>oct(n-1).psize()</code>
	 */
	private Vectornf[][] perlinOctave(long seed, int cx, int cy, int octn) {
		
		PerlinOctave oct = octaveDataSets[octn];
		
		Vectornf[][] values = new Vectornf[oct.psize()*lacunarity][oct.psize()*lacunarity];
		
		for(int i=0; i<lacunarity; i++) {for(int j=0; j<lacunarity; j++) {
			int ocx = cx*lacunarity+i;
			int ocy = cy*lacunarity+j;
			
			Vector2v[] invecs = genInfluenceVectors(seed, ocx, ocy, true);
			Vectornf[][] pixs = Perlinification.perlinAChunk(invecs, oct);
			
			Vectornf[][] subOctPixs = null;
			if(octn<octaves-1) {
				subOctPixs = perlinOctave(seed+1, ocx, ocy, octn+1);
			}
			
			
			for(int x=0;x<oct.psize();x++) {for(int y=0;y<oct.psize();y++) {
				Vectornf p = pixs[x][y];
				if(subOctPixs != null) {
					p = Vectornf.add(p, Vectornf.scale(subOctPixs[x][y], persistence));
				}
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
	 * @return <code>Vector2v[]</code> - an influence vector with length equal to 1 for each {@link #MASKS}
	 */
	private Vector2v[] genInfluenceVectors(long seed, int cx, int cy, boolean isOct) {
		int[] index = genInfluenceVectorIndecies(cx, cy);
		
		Vector2v[] vecs = new Vector2v[MASKS];
		for(int i=0; i<MASKS; i++) {

//			Generate random float index for the angle of the index vector
			float f = Util.getRandomFloatAtIndex(index[i], random, seed, 2*(float)Math.PI);
			
//			Generate random int index for the influence vector possibility if there is more than one possibility
			int in=0;
			if(runPossibilities) {
				in = isOct? 
							octPosIndex.perlinGenOctInfluenceVectorPosibilityIndex(seed, i, index[i], cx, cy) :
							mainPosIndex.perlinGenMainInfluenceVectorPosibilityIndex(seed, i, index[i], cx, cy);
				if(in==-1) {
					in = isOct? 
							this.perlinGenOctInfluenceVectorPosibilityIndex(seed, i, index[i], cx, cy) :
							this.perlinGenMainInfluenceVectorPosibilityIndex(seed, i, index[i], cx, cy);
				}
			}
			
			Vector2f p = Vector2f.fromPolar(1, f);
			vecs[i] = new Vector2v(
						Vectornf.scale(possibilities[in], p.x),
						Vectornf.scale(possibilities[in], p.y)
					);
		}
		
		return vecs;
	}
	
	@Override
	public int perlinGenOctInfluenceVectorPosibilityIndex(long seed, int mask, int spiralIndex, int cx, int cy) {
		return Util.getRandomIntAtIndex(spiralIndex, random, seed, possibilities.length);
	}

	@Override
	public int perlinGenMainInfluenceVectorPosibilityIndex(long seed, int mask, int spiralIndex, int cx, int cy) {
		return Util.getRandomIntAtIndex(spiralIndex, random, seed, possibilities.length);
	};
	
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
	 * 	<li><b>values</b> - <code>Vectornf[][]</code> of all final <i>non-normalized</i> pixel values
	 * 	<li><b>max</b> - the max value of a pixel component within the chunk
	 * 	<li><b>min</b> - the min value of a pixel component within the chunk
	 * 	<li><b>getNormalizedValue(x,y)</b> - function whcih will return the value of a pixel so that each component is between 0 and 1 depending on the {@link PerlinReturn#min} and {@link PerlinReturn#max} values
	 * 	<li><b>getNormalizedValue(x,y,min,max)</b> - function whcih will normalize the value of a pixel depending on the given min and max values
	 * </ul>
	 *
	 */
	public record PerlinReturn(Vectornf[][] values, float max, float min) {
		public Vectornf getNormalizedValue(int px, int py) {
			return getNormalizedValue(px,py,max,min);
		}
		public Vectornf getNormalizedValue(int px, int py, float max, float min) {
			return Vectornf.scale(Vectornf.sub(values[px][py], Vectornf.Const(values[px][py].size(),min)),1f/(max-min));
		}
	}


	
	
	
	
}