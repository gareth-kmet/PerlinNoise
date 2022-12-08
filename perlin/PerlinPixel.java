package perlin;

import util.Vector2f;

class PerlinPixel {
	
	final int x, y;
	
	final Vector2f 
		TL, TR,
		BL, BR;
	final Vector2f[] vecs = new Vector2f[PerlinNoise.MASKS];

	public PerlinPixel(int x, int y, int tileSize) {
		this.x = x; this.y=y;
		
		float mx = x+.5f; float my = y+.5f;
		Vector2f t = new Vector2f(mx,my);
		
		TL = Vector2f.sub(new Vector2f(0,0),t);
		TR = Vector2f.sub(new Vector2f(tileSize,0),t);
		BL = Vector2f.sub(new Vector2f(0,tileSize),t);
		BR = Vector2f.sub(new Vector2f(tileSize,tileSize),t);
		vecs[PerlinNoise.TL]=TL;
		vecs[PerlinNoise.TR]=TR;
		vecs[PerlinNoise.BL]=BL;
		vecs[PerlinNoise.BR]=BR;
		
	}

}
