package util;

public final class Vector2v {
	
	private final int size;
	
	public Vectornf a, b;
	
	public Vector2v(Vectornf a, Vectornf b) {
		Vectornf.assertCompatable(a, b);
		size = a.size();
		this.a=a;
		this.b=b;
	}
	
	public int size() {
		return this.size;
	}
	
	public static Vectornf dot(Vector2v a, Vector2f b) {
		return Vectornf.add(Vectornf.scale(a.a, b.x), Vectornf.scale(a.b, b.y));
	}
	
	public Vectornf dot(Vector2f b) {
		return dot(this, b);
	}

}
