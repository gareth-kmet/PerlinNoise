package perlin;

import util.Vector2f;

class PerlinChunk {
	
	final int x,y;
	
	static class InfluenceVectors{
		final Vector2f 
			TL, TR,
			BL, BR;
		
		final Vector2f[] vecs = new Vector2f[PerlinNoise.MASKS];
		
		InfluenceVectors(Vector2f tl, Vector2f tr, Vector2f bl, Vector2f br) {
			TL= tl;vecs[PerlinNoise.TL]=TL;
			TR= tr;vecs[PerlinNoise.TR]=TR;
			BL= bl;vecs[PerlinNoise.BL]=BL;
			BR= br;vecs[PerlinNoise.BR]=BR;
		}
	}
	
	final InfluenceVectors invecs;
	
	final PerlinPixel[][] pixels;
	
	final int pixelSize;

	
	PerlinChunk(int x, int y, InfluenceVectors vecs, int pixelSize){
		this.x=x; this.y=y;
		invecs = vecs;
		
		this.pixelSize = pixelSize;
		this.pixels = PerlinChunk.initPixels(pixelSize);
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
	
	
}
