package util;

/**
 * Represents a vector of 2 dimensions which contains two {@link Vectornf} of equal dimension
 * @author Gareth Kmet
 *
 */
public final class Vector2v {
	
	/**
	 * The dimension of the component-vectors
	 */
	private final int size;
	
	/**
	 * The component vectors
	 */
	public Vectornf a, b;
	
	/**
	 * Generates a new <code>Vector2v</code> using two {@link Vectornf} of equal dimension
	 * @param a - the first vector
	 * @param b - the second vector
	 */
	public Vector2v(Vectornf a, Vectornf b) {
		Vectornf.assertCompatable(a, b);
		size = a.size();
		this.a=a;
		this.b=b;
	}
	
	/**
	 * @return the dimension of the component-vectors
	 */
	public int size() {
		return this.size;
	}
	
	/**
	 * Performs a dot product operation on a <code>Vector2v</code> and a {@link Vector2f}
	 * @param a - the <code>Vector2v</code>
	 * @param b - the <code>Vector2f</code>
	 * @return the resulting <code>Vectornf</code>
	 */
	public static Vectornf dot(Vector2v a, Vector2f b) {
		return Vectornf.add(Vectornf.scale(a.a, b.x), Vectornf.scale(a.b, b.y));
	}
	
	/**
	 * Performs a dot product operation on this vector and a {@link Vector2f}
	 * @param b - the <code>Vector2f</code>
	 * @return the resulting <code>Vectornf</code>
	 */
	public Vectornf dot(Vector2f b) {
		return dot(this, b);
	}
	
	@Override
	public String toString() {
		return "Vector2v{\n         "+a.toString()+"\n         "+b.toString()+"\n}";
	}

}
