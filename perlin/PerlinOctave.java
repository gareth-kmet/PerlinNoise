package perlin;

import util.Vector2f;

/**
 * Represents a single octave of the algorithm
 * <br> Contains
 * <ul>
 * 	<li><b>n</b> - the octave's number (used for debug)
 * 	<li><b>psize</b> - the width and height of the octave
 * 	<li><b>pixelDistanceVectors</b> - a <code>Vector2f[][][]</code> which contains one {@link Vector2f} for each pixel for each {@link PerlinNoise#MASKS}
 * </ul>
 * @author Gareth Kmet
 *
 */
record PerlinOctave(int n, int psize, Vector2f[][][] pixelDistanceVectors) {
	
	PerlinOctave(int n, int psize){
		this(n, psize, new Vector2f[PerlinNoise.MASKS][psize][psize]);
		calcDistanceVectors();
	}
	
	/**
	 * Calculates the distance of each pixel to the corresponding corner of each {@link PerlinNoise#MASKS}
	 */
	void calcDistanceVectors() {
		for(int x=0; x<psize;x++) {for(int y=0;y<psize;y++) {
			Vector2f pixel = new Vector2f((x)/(float)psize, (y)/(float)psize);
			pixelDistanceVectors[PerlinNoise.TL][x][y] = Vector2f.sub(new Vector2f(0,0),pixel);
			pixelDistanceVectors[PerlinNoise.TR][x][y] = Vector2f.sub(new Vector2f(1,0),pixel);
			pixelDistanceVectors[PerlinNoise.BL][x][y] = Vector2f.sub(new Vector2f(0,1),pixel);
			pixelDistanceVectors[PerlinNoise.BR][x][y] = Vector2f.sub(new Vector2f(1,1),pixel);
		}}
	}
}