package util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a vector of <code>n</code> dimensions containing floats
 * @author Gareth Kmet
 *
 */
public sealed class Vectornf permits Vector2f{
	
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
	 * @param vec - the components of the vector
	 */
	public Vectornf(ArrayList<Float> vec) {
		this.size = vec.size();
		this.vec = vec.toArray(new Float[size]);
	}
	
	/**
	 * Generates a vector of dimension <code>vec.length</code>
	 * @param vec - the components of the vector
	 */
	public Vectornf(Float ... vec) {
		this.size = vec.length;
		this.vec = vec;
	}
	
	/**
	 * Generates a vector with all equal components
	 * @param size - the dimension of the vector
	 * @param f - the value of each component
	 * @return the new vector
	 */
	public static Vectornf Const(int size, float f) {
		Float[] fs = new Float[size];
		Arrays.fill(fs, f);
		return new Vectornf(fs);
	}
	
	/**
	 * Create a vector of dimension 3 using a {@link Color}
	 * @param c - the color
	 * @return the new vector
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
	 * @param i - the index
	 * @return the component of the vector at the <code>i</code>-th index
	 */
	public float get(int i) {
		return vec[i];
	}
	
	/**
	 * Sets the <code>i</code>-th component of the vector to a new element
	 * @param i - the index
	 * @param a - the new element
	 */
	public void set(int i, float a) {
		vec[i]=a;
	}
	
	/**
	 * Ensures vector operations can be performed between two vectors
	 * @param a - a vector
	 * @param b - a vector
	 */
	protected static void assertCompatable(Vectornf a, Vectornf b) {
		if(a.size!=b.size) {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Adds two {@link Vectornf} together
	 * <br>Does not change the contents of <b>a</b> and <b>b</b>
	 * @param a {@link Vectornf}
	 * @param b {@link Vectornf}
	 * @return a new {@link Vectornf} containing the sum
	 */
	public static Vectornf add(Vectornf a, Vectornf b) {
		assertCompatable(a,b);
		
		Float[] c = new Float[a.size];
		for(int i=0; i<a.size; i++) {
			c[i]= a.get(i)+b.get(i);
		}
		return new Vectornf(c);
	}
	
	/**
	 * Adds the components of a {@link Vectornf} to the components of this instance
	 * <br>Does not modify the contents of <b>b</b>
	 * @param b
	 * @return this instance
	 */
	public Vectornf add(Vectornf b) {
		assertCompatable(this,b);
		
		for(int i=0; i<size; i++) {
			vec[i] += b.get(i);
		}
		return this;
	}
	
	/**
	 * Subtracts two {@link Vectornf}
	 * <br>Does not change the contents of <b>a</b> and <b>b</b>
	 * @param a {@link Vectornf}
	 * @param b {@link Vectornf}
	 * @return a new {@link Vectornf} containing the sum
	 */
	public static Vectornf sub(Vectornf a, Vectornf b) {
		assertCompatable(a,b);
		
		Float[] c = new Float[a.size];
		for(int i=0; i<a.size; i++) {
			c[i]= a.get(i)-b.get(i);
		}
		return new Vectornf(c);
	}
	
	/**
	 * Subtracts the components of a {@link Vectornf} to the components of this instance
	 * <br>Does not modify the contents of <b>b</b>
	 * @param b
	 * @return this instance
	 */
	public Vectornf sub(Vectornf b) {
		assertCompatable(this,b);
		
		for(int i=0; i<size; i++) {
			vec[i] -= -b.get(i);
		}
		return this;
	}
	
	/**
	 * Scales a {@link Vectornf} by a float
	 * <br>Does not change the contents of <b>a</b>
	 * @param a {@link Vectornf}
	 * @param b {@link float}
	 * @return a new {@link Vectornf} representing the scaled contents
	 */
	public static Vectornf scale(Vectornf a, float b) {
		Float[] c = new Float[a.size];
		for(int i=0; i<a.size; i++) {
			c[i]= a.get(i)*b;
		}
		return new Vectornf(c);
	}
	
	/**
	 * Scales the components of this instance by a float
	 * @param b {@link float}
	 * @return this instance
	 */
	public Vectornf scale(float b) {
		for(int i=0; i<size; i++) {
			vec[i] *= b;
		}
		return this;
	}
	
	/**
	 * Calculates the dot product of two {@link Vectornf}
	 * @param a {@link Vectornf}
	 * @param b {@link Vectornf}
	 * @return {@link float} representing the dot product
	 */
	public static float dot(Vectornf a, Vectornf b) {
		assertCompatable(a, b);
		float f = 0;
		
		for(int i=0; i<a.size; i++) {
			f += a.get(i)*b.get(i);
		}
		return f;
	}
	
	/**
	 * Calculates the dot product with a {@link Vectornf}
	 * @param b {@link Vectornf}
	 * @return {@link float} representing the dot product
	 */
	public float dot(Vectornf b) {
		return dot(this, b);
	}
	
	/**
	 * Generates all the standard vectors for a given dimension
	 * @param size - the dimension
	 * @return an array containing all standard vectors (the <code>i</code>-th index will contain the <code>i</code>-th standard vector)
	 */
	public static Vectornf[] genStandardVectors(int size) {
		Vectornf[] s = new Vectornf[size];
		for(int i=0; i<size; i++) {
			Vectornf c = Vectornf.Const(size, 0);
			c.set(i, 1f);
			s[i]=c;
		}
		return s;
	}
	
	@Override
	public String toString() {
		return "Vector"+size+"f "+Arrays.deepToString(vec);
	}

	/**
	 * Lerps two values together given a proportional between 0-1.
	 * @param val1 - the first value
	 * @param val2 - the second value
	 * @param aProp - the linear proportion of the location between the two values
	 * @return <b><code>Vectornf</code></b> - the resulting value
	 */
	public static Vectornf lerp(Vectornf val1, Vectornf val2, float aProp) {
		assertCompatable(val1, val2);
		
		Float[] newVector = new Float[val1.size];
		for(int i=0; i<val1.size; i++) {
			newVector[i] = aProp*val2.get(i) + (1-aProp)*val1.get(1);
		}
		return new Vectornf(newVector);
	}
	
	
	
	
	
}
