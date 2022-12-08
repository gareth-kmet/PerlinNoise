package perlin;

import util.Vector2f;

class PerlinChunk {
	
	final int x,y;
	Vector2f[] invecs;
	
	final PerlinPixel[][] pixels;
	
	final int pixelSize;

	
	PerlinChunk(int x, int y, int pixelSize){
		this.x=x; this.y=y;
		
		this.pixelSize = pixelSize;
		this.pixels = PerlinChunk.initPixels(pixelSize);
	}
	
	void setInfluencevectors(Vector2f[] invecs) {
		this.invecs=invecs;
	}
	
	private static PerlinPixel[][] initPixels(final int pixelSize) {
		PerlinPixel[][] pixels = new PerlinPixel[pixelSize][pixelSize];
		
		for(int x=0; x<pixelSize; x++) {
			for(int y=0; y<pixelSize; y++) {
				pixels[x][y]=new PerlinPixel(x,y,pixelSize);
			}
		}
		
		return pixels;
	}
	
	static Vector2f[] genInfluenceVectorsArray(Vector2f tl, Vector2f tr, Vector2f bl, Vector2f br) {
		Vector2f[] vecs = new Vector2f[PerlinNoise.MASKS];
		vecs[PerlinNoise.TL]=tl;
		vecs[PerlinNoise.TR]=tr;
		vecs[PerlinNoise.BL]=bl;
		vecs[PerlinNoise.BR]=br;
		return vecs;
	}
	
	
}
