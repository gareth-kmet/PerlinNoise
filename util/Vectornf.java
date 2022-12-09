package util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public final class Vectornf {
	
	private final int size;
	private final ArrayList<Float> vec;
	
	public Vectornf(int size, ArrayList<Float> vec) {
		this.size = size;
		this.vec = vec;
	}
	
	public Vectornf(int size, Float ... vec) {
		this.size = size;
		this.vec = new ArrayList<Float>(size);
		Collections.addAll(this.vec, vec);
	}
	
	public static Vectornf Const(int size, float f) {
		Float[] fs = new Float[size];
		Arrays.fill(fs, f);
		return new Vectornf(size, fs);
	}
	
	public static Vectornf color(Color c) {
		float[] f = c.getRGBColorComponents(null);
		return new Vectornf(3, f[0], f[1], f[2]);
	}
	
	public static Color toColor(Vectornf v) {
		return new Color(v.get(0), v.get(1), v.get(2));
	}
	
	public Color toColor() {
		return toColor(this);
	}
	
	public int size() {
		return size;
	}
	
	public float get(int i) {
		return vec.get(i);
	}
	
	public void set(int i, float a) {
		vec.set(i, a);
	}
	
	public static void assertCompatable(Vectornf a, Vectornf b) {
		if(a.size!=b.size) {
			throw new IllegalArgumentException();
		}
	}
	
	public static Vectornf add(Vectornf a, Vectornf b) {
		assertCompatable(a,b);
		
		Float[] c = new Float[a.size];
		for(int i=0; i<a.size; i++) {
			c[i]= a.get(i)+b.get(i);
		}
		return new Vectornf(a.size, c);
	}
	
	public Vectornf add(Vectornf b) {
		assertCompatable(this,b);
		
		for(int i=0; i<size; i++) {
			set(i, get(i)+b.get(i));
		}
		return this;
	}
	
	public static Vectornf sub(Vectornf a, Vectornf b) {
		assertCompatable(a,b);
		
		Float[] c = new Float[a.size];
		for(int i=0; i<a.size; i++) {
			c[i]= a.get(i)-b.get(i);
		}
		return new Vectornf(a.size, c);
	}
	
	public Vectornf sub(Vectornf b) {
		assertCompatable(this,b);
		
		for(int i=0; i<size; i++) {
			set(i, get(i)-b.get(i));
		}
		return this;
	}
	
	public static Vectornf scale(Vectornf a, float b) {
		Float[] c = new Float[a.size];
		for(int i=0; i<a.size; i++) {
			c[i]= a.get(i)*b;
		}
		return new Vectornf(a.size, c);
	}
	
	public Vectornf scale(float b) {
		for(int i=0; i<size; i++) {
			set(i, get(i)*b);
		}
		return this;
	}
	
	public static float dot(Vectornf a, Vectornf b) {
		assertCompatable(a, b);
		float f = 0;
		
		for(int i=0; i<a.size; i++) {
			f += a.get(i)*b.get(i);
		}
		return f;
	}
	
	public float dot(Vectornf b) {
		return dot(this, b);
	}
	
	@Override
	public String toString() {
		return "Vector"+size+"f "+vec.toString();
	}
	
	
	
	
	
}
