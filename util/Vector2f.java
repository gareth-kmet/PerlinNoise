package util;

/**
 * Represents a vector in 2D
 * 
 * @author Gareth Kmet
 *
 */
public final class Vector2f extends Vectornf{
	
	public float x,y;
	
	/**
	 * Vector representing the 2D Cartesian representation of x and y
	 * @param x
	 * @param y
	 */
	public Vector2f(float x, float y) {
		super(x,y);
		this.x=x; this.y=y;
	}
	/**
	 * Vector representing the 2D Cartesian representation with components both equal to c
	 * <br>
	 * Equivalent to <code>new Vector2f(c,c)</code>
	 * @param c
	 */
	public Vector2f(float c) {
		super(c,c);
		this.x=c; this.y=c;
	}
	
	/**
	 * Creates a new {@link Vector2f} with components equal to zero
	 * @return {@link Vector2f} equivalent to <code>new Vector2f(0,0)</code>
	 */
	public static Vector2f zero() {
		return new Vector2f(0);
	}
	
	/**
	 * Creates a new {@link Vector2f} with x-component equal to one and y-component equal to zero
	 * @return {@link Vector2f} equivalent to <code>new Vector2f(1,0)</code>
	 */
	public static Vector2f e1() {
		return new Vector2f(1,0);
	}
	
	/**
	 * Creates a new {@link Vector2f} with x-component equal to one and y-component equal to zero
	 * @return {@link Vector2f} equivalent to <code>new Vector2f(0,1)</code>
	 */
	public static Vector2f e2() {
		return new Vector2f(0,1);
	}
	
	/**
	 * Adds two {@link Vector2f} together
	 * <br>Does not change the contents of <b>a</b> and <b>b</b>
	 * @param a {@link Vector2f}
	 * @param b {@link Vector2f}
	 * @return a new {@link Vector2f} containing the sum
	 */
	public static Vector2f add(Vector2f a, Vector2f b) {
		return new Vector2f(a.x+b.x, a.y+b.y);
	}
	
	/**
	 * Adds the components of a {@link Vector2f} to the components of this instance
	 * <br>Does not modify the contents of <b>b</b>
	 * @param b
	 * @return this instance
	 */
	@Override
	public Vector2f add(Vectornf b) {
		assertCompatable(this, b);
		this.x+=b.get(0); this.y+=b.get(1);
		return this;
	}
	
	/**
	 * Subtracts two {@link Vector2f}
	 * <br>Does not change the contents of <b>a</b> and <b>b</b>
	 * @param a {@link Vector2f}
	 * @param b {@link Vector2f}
	 * @return a new {@link Vector2f} containing the sum
	 */
	public static Vector2f sub(Vector2f a, Vector2f b) {
		return new Vector2f(a.x-b.x, a.y-b.y);
	}
	
	/**
	 * Subtracts the components of a {@link Vector2f} to the components of this instance
	 * <br>Does not modify the contents of <b>b</b>
	 * @param b
	 * @return this instance
	 */
	@Override
	public Vector2f sub(Vectornf b) {
		assertCompatable(this, b);
		this.x-=b.get(0); this.y-=b.get(1);
		return this;
	}
	
	/**
	 * Scales a {@link Vector2f} by a float
	 * <br>Does not change the contents of <b>a</b>
	 * @param a {@link Vector2f}
	 * @param b {@link float}
	 * @return a new {@link Vector2f} representing the scaled contents
	 */
	public static Vector2f scale(Vector2f a, float b) {
		return new Vector2f(a.x*b, a.y*b);
	}
	
	/**
	 * Scales the components of this instance by a float
	 * @param b {@link float}
	 * @return this instance
	 */
	@Override
	public Vector2f scale(float b) {
		this.x*=b; this.y*=b;
		return this;
	}
	
	/**
	 * Calculates the dot product of two {@link Vector2f}
	 * @param a {@link Vector2f}
	 * @param b {@link Vector2f}
	 * @return {@link float} representing the dot product
	 */
	public static float dot(Vector2f a, Vector2f b) {
		return a.x*b.x+a.y*b.y;
	}
	
	/**
	 * Calculates the dot product with a {@link Vector2f}
	 * @param b {@link Vecto2f}
	 * @return {@link float} representing the dot product
	 */
	@Override
	public float dot(Vectornf b) {
		assertCompatable(this, b);
		return x*b.get(0)+y*b.get(1);
	}
	
	@Override
	public String toString() {
		return "Vector2f ["+x+", "+y+"]";
	}
	
	/**
	 * Creates a {@link Vector2f} using polar coordinates
	 * @param r {@link float}
	 * @param theta {@link float}
	 * @return new {@link Vector2f}
	 */
	public static Vector2f fromPolar(float r, float theta) {
		return new Vector2f((float)(r*Math.cos(theta)), (float)(r*Math.sin(theta)));
	}

}
