package util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a vector of <code>n</code> dimensions containing floats
 * 
 * @author Gareth Kmet
 */
public sealed class Vectornf implements InnerProductFloatVectorSpace<Vectornf> permits Vector2f {

	/**
	 * The dimension of the vector
	 */
	private final int size;
	/**
	 * The components of the vector
	 */
	private final Float[] vec;

	/**
	 * Generates a vector of dimension <code>vec.size()</code>
	 * 
	 * @param vec
	 *            - the components of the vector
	 */
	public Vectornf(ArrayList<Float> vec) {
		this.size = vec.size();
		this.vec = vec.toArray(new Float[size]);
	}

	/**
	 * Generates a vector of dimension <code>vec.length</code>
	 * 
	 * @param vec
	 *            - the components of the vector
	 */
	public Vectornf(Float... vec) {
		this.size = vec.length;
		this.vec = vec;
	}

	/**
	 * Generates a vector with all equal components
	 * 
	 * @param  size
	 *              - the dimension of the vector
	 * @param  f
	 *              - the value of each component
	 * 
	 * @return      the new vector
	 */
	public static Vectornf Const(int size, float f) {
		Float[] fs = new Float[size];
		Arrays.fill(fs, f);
		return new Vectornf(fs);
	}

	/**
	 * Create a vector of dimension 3 using a {@link Color}
	 * 
	 * @param  c
	 *           - the color
	 * 
	 * @return   the new vector
	 */
	public static Vectornf color(Color c) {
		float[] f = c.getColorComponents(new float[3]);
		return new Vectornf(f[0], f[1], f[2]);
	}

	/**
	 * @return the dimension of the vector
	 */
	public int size() {
		return size;
	}

	/**
	 * @param  i
	 *           - the index
	 * 
	 * @return   the component of the vector at the <code>i</code>-th index
	 */
	public float get(int i) {
		return vec[i];
	}

	/**
	 * Sets the <code>i</code>-th component of the vector to a new element
	 * 
	 * @param i
	 *          - the index
	 * @param a
	 *          - the new element
	 */
	public void set(int i, float a) {
		vec[i] = a;
	}

	/**
	 * Ensures vector operations can be performed between two vectors
	 * 
	 * @param a
	 *          - a vector
	 * @param b
	 *          - a vector
	 */
	protected static void assertCompatable(Vectornf a, Vectornf b) {
		if (a.size != b.size) { throw new IllegalArgumentException(); }
	}

	@Override
	public Vectornf add(Vectornf b) {
		assertCompatable(this, b);

		Float[] c = new Float[b.size];
		for (int i = 0; i < b.size; i++) { c[i] = this.get(i) + b.get(i); }
		return new Vectornf(c);
	}

	@Override
	public Vectornf iadd(Vectornf b) {
		assertCompatable(this, b);

		for (int i = 0; i < size; i++) { vec[i] += b.get(i); }
		return this;
	}

	@Override
	public Vectornf sub(Vectornf b) {
		assertCompatable(this, b);

		Float[] c = new Float[b.size];
		for (int i = 0; i < b.size; i++) { c[i] = this.get(i) - b.get(i); }
		return new Vectornf(c);
	}

	@Override
	public Vectornf isub(Vectornf b) {
		assertCompatable(this, b);

		for (int i = 0; i < size; i++) { vec[i] -= b.get(i); }
		return this;
	}

	@Override
	public Vectornf scale(float b) {
		Float[] c = new Float[this.size];
		for (int i = 0; i < this.size; i++) { c[i] = this.get(i) * b; }
		return new Vectornf(c);
	}

	@Override
	public Vectornf iscale(float b) {
		for (int i = 0; i < size; i++) { vec[i] *= b; }
		return this;
	}

	@Override
	public float dot(Vectornf b) {
		assertCompatable(this, b);
		float f = 0;

		for (int i = 0; i < b.size; i++) { f += this.get(i) * b.get(i); }
		return f;
	}

	/**
	 * Generates all the standard vectors for a given dimension
	 * 
	 * @param  size
	 *              - the dimension
	 * 
	 * @return      an array containing all standard vectors (the
	 *              <code>i</code>-th index will contain the <code>i</code>-th
	 *              standard vector)
	 */
	public static Vectornf[] genStandardVectors(int size) {
		Vectornf[] s = new Vectornf[size];
		for (int i = 0; i < size; i++) {
			Vectornf c = Vectornf.Const(size, 0);
			c.set(i, 1f);
			s[i] = c;
		}
		return s;
	}

	@Override
	public String toString() {
		return "Vector" + size + "f " + Arrays.deepToString(vec);
	}

	@Override
	public Vectornf lerp(Vectornf b, float f) {
		// TODO Auto-generated method stub
		Float[] newVector = new Float[this.size];
		for (int i = 0; i < this.size; i++) { newVector[i] = f * b.get(i) + (1 - f) * this.get(i); }
		return new Vectornf(newVector);
	}

}
