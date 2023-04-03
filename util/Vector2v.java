package util;

/**
 * Represents a vector of 2 dimensions which contains two {@link Vectornf} of
 * equal dimension
 * 
 * @author Gareth Kmet
 */
public final class Vector2v<E extends InnerProductFloatVectorSpace<E>> {

	/**
	 * The component vectors
	 */
	public E a, b;

	/**
	 * Generates a new <code>Vector2v</code> using two {@link Vectornf} of equal
	 * dimension
	 * 
	 * @param a
	 *          - the first vector
	 * @param b
	 *          - the second vector
	 */
	public Vector2v(E a, E b) {
		// Vectornf.assertCompatable(a, b);
		// size = a.size();
		this.a = a;
		this.b = b;
	}

	/**
	 * Performs a dot product operation on a <code>Vector2v</code> and a
	 * {@link Vector2f}
	 * 
	 * @param  a
	 *           - the <code>Vector2v</code>
	 * @param  b
	 *           - the <code>Vector2f</code>
	 * 
	 * @return   the resulting <code>Vectornf</code>
	 */
	public static <E extends InnerProductFloatVectorSpace<E>> E dot(Vector2v<E> a, Vector2f b) {
		return a.a.scale(b.x).iadd(a.b.scale(b.y));
	}

	/**
	 * Performs a dot product operation on this vector and a {@link Vector2f}
	 * 
	 * @param  b
	 *           - the <code>Vector2f</code>
	 * 
	 * @return   the resulting <code>Vectornf</code>
	 */
	public E dot(Vector2f b) {
		return dot(this, b);
	}

	@Override
	public String toString() {
		return "Vector2v{\n         " + a.toString() + "\n         " + b.toString() + "\n}";
	}

}
