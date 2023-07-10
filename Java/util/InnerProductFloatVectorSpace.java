/**
 * 
 */
package util;

/**
 * An inner product space of the field of floats
 * 
 * @author Gareth Kmet
 */
public interface InnerProductFloatVectorSpace<T extends InnerProductFloatVectorSpace<T>> {
	/**
	 * Creates a new vector which is the sum of this vector and the other vector
	 * 
	 * @param  b
	 *           The other vector
	 * 
	 * @return   A new vector containing the sum
	 */
	public T add(T b);

	/**
	 * Adds a vector to this vector inplace
	 * 
	 * @param  b
	 *           The other vector
	 * 
	 * @return   This vector after the operation
	 */
	public T iadd(T b);

	/**
	 * Creates a new vector which is the subtraction of the other vector from
	 * this vector
	 * 
	 * @param  b
	 *           The other vector
	 * 
	 * @return   A new vector containing the result
	 */
	public default T sub(T b) {
		return this.add(b.scale(-1));
	}

	/**
	 * Subtractions a vector from this vector inplace
	 * 
	 * @param  b
	 *           The other vector
	 * 
	 * @return   This vector after the operation
	 */
	public default T isub(T b) {
		return this.iadd(b.scale(-1));
	}

	/**
	 * Creates a new vector which is the the scale of this vector by the factor
	 * 
	 * @param  f
	 *           The scaling factor
	 * 
	 * @return   A new vector containing the result
	 */
	public T scale(float f);

	/**
	 * Scales this vector by a factor inplace
	 * 
	 * @param  f
	 *           The scaling factor
	 * 
	 * @return   This vector after the operation
	 */
	public T iscale(float f);

	/**
	 * Returns the inner product of this vector and another vector
	 * 
	 * @param  b
	 *           The other vector
	 * 
	 * @return   The inner product
	 */
	public float dot(T b);

	/**
	 * Returns a new vector representing the lerp between this vector and
	 * another according to a value <em>f</em>
	 * 
	 * @param  b
	 *           The other vector
	 * @param  f
	 *           The lerping factor [0,1]
	 * 
	 * @return
	 */
	public default T lerp(T b, float f) {
		return b.scale(f).iadd(this.scale(1 - f));
	}
}
