package perlin;

import util.Vector2f;

/**
 * Class representing an individual square chunk used by the PerlinNoise algorithm
 * @author Gareth Kmet
 *
 */
class PerlinChunk {
	/**
	 * Location of the chunk
	 */
	final int x,y;
	/**
	 * Randomly generated influence vectors of size {@link PerlinNoise#MASKS}
	 */
	Vector2f[] invecs;
	/**
	 * The width of the chunk
	 */
	final int pixelSize;

	/**
	 * 
	 * @param x - Location of the chunk
	 * @param y - Location of the chunk
	 * @param pixelSize - The width/height of the chunk
	 */
	PerlinChunk(int x, int y, int pixelSize){
		this.x=x; this.y=y;
		
		this.pixelSize = pixelSize;
	}
	
	/**
	 * Sets the influence vectors of this chunk
	 * @param invecs - the influence vectors of size {@link PerlinNoise#MASKS}
 	 */
	void setInfluencevectors(Vector2f[] invecs) {
		this.invecs=invecs;
	}
	
	/**
	 * Rearranges vectors into an array to match the indices given by {@link PerlinNoise#MASKS}
	 * @param tl - Top left vector
	 * @param tr - Top right vector
	 * @param bl - bottom left vector
	 * @param br - bottom right vector
	 * @return <b><code>Vector2f[]</code></b> - the array of influence vectors
	 */
	static Vector2f[] genInfluenceVectorsArray(Vector2f tl, Vector2f tr, Vector2f bl, Vector2f br) {
		Vector2f[] vecs = new Vector2f[PerlinNoise.MASKS];
		vecs[PerlinNoise.TL]=tl;
		vecs[PerlinNoise.TR]=tr;
		vecs[PerlinNoise.BL]=bl;
		vecs[PerlinNoise.BR]=br;
		return vecs;
	}
	
	
}
