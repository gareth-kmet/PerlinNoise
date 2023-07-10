package main_path;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import perlin.PerlinNoise;
import util.Vectornf;

class Main {
	static final int CHUNK_SIZE = 3, PIXEL_SIZE = 256, MULTI = 1, R = 1;
	static Vectornf[][][][] pixs;
	static Vectornf[][] ps;
	static Frame f = new Frame();
	static {
		f.setLayout(new GridLayout(R, R));
	}
	static long seed = /* new Random().nextInt(); */ 1646419626;
	static {
//		System.out.println(seed);
	}
	static PerlinNoise<Vectornf> p = new PerlinNoise<Vectornf>(Vectornf.class, PIXEL_SIZE,
			Vectornf.genStandardVectors(3));
	
	static PerlinNoise<Vectornf> p2 = new PerlinNoise<Vectornf>(Vectornf.class, PIXEL_SIZE,
			Vectornf.genStandardVectors(1));

	static float max, min, mmax;

	static float per = 0.5f;
	static int oct = 2;
	static int lac = 2;

	public static void main(String[] args) {

		f.setSize(new Dimension(CHUNK_SIZE * PIXEL_SIZE * MULTI * R, CHUNK_SIZE * PIXEL_SIZE * MULTI * R + 40));

		/*
		 * for(oct=1; oct<=3; oct++) { for(lac=1; lac<=3; lac++) {
		 * if(lac==3)lac=4; mainy(); } }
		 */
		mainy();
		f.setVisible(true);

		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				f.dispose();
			}
		});
	}

	static public void mainy() {
		System.out.println("X,Y,Z,c,");
		System.out.println("0,0,1000,1,");
		p.setOctaves(oct, lac, per);
		p2.setOctaves(oct, lac, per);
		Main.perl();

		Canvas c = new MainCanvas(mmax, ps);
		f.add(c);

	}

	static void perl() {
		pixs = new Vectornf[CHUNK_SIZE][CHUNK_SIZE][][];
		max = Float.NEGATIVE_INFINITY;
		min = Float.POSITIVE_INFINITY;
		long time = System.currentTimeMillis();
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int y = 0; y < CHUNK_SIZE; y++) {
				Vectornf[][] pr = p.perlin(seed, x, y, v -> {
					for (int i = 0; i < v.size(); i++) {
						max = Math.max(v.get(i), max);
						min = Math.min(v.get(i), min);
					}
				});
				Vectornf[][] pr2 = p2.perlin(seed, x, y, v->{});
				pixs[x][y] = pr;
				for(int i=0; i<pr.length; i++) {
					for(int j=0;j<pr[i].length; j++) {
						pr[i][j].iscale(pr2[i][j].get(0));
					}
				}
			}
		}
		mmax = Math.max(Math.abs(max), Math.abs(min));
//		System.out.println(max + " " + min + " " + (Math.sqrt(1 / 2) * (1f - Math.pow(per, oct)) / (1f - per)));
		long t = System.currentTimeMillis() - time;
//		System.out.println(t);

		ps = new Vectornf[Main.CHUNK_SIZE * Main.PIXEL_SIZE][Main.CHUNK_SIZE * Main.PIXEL_SIZE];
		for (int i = 0; i < Main.CHUNK_SIZE * Main.PIXEL_SIZE; i++)
			for (int j = 0; j < Main.CHUNK_SIZE * Main.PIXEL_SIZE; j++)
				ps[i][j] = new Vectornf(0f);

		for (int cx = 0; cx < Main.CHUNK_SIZE; cx++) {
			for (int cy = 0; cy < Main.CHUNK_SIZE; cy++) {
				for (int px = 0; px < Main.PIXEL_SIZE; px++) {
					for (int py = 0; py < Main.PIXEL_SIZE; py++) {
						
						

						int x = Main.PIXEL_SIZE * cx + px;
						int y = Main.PIXEL_SIZE * cy + py;
						
						int csf = x+y;

						if (px%64==0 || py%64==0) {
							csf=-1000;
						}

						Vectornf ct = pixs[cx][cy][px][py];

						// int x2 = x+Math.round(mmax/ct.get(0));
						// int y2 = y+Math.round(mmax/ct.get(1));

						float x2 = x + 1000 * ct.get(0);
						float y2 = y + 1000 * ct.get(1);
						float z2 = 1000*ct.get(2);
						
//						System.out.println("::"+x2+"::"+y2+"::"+z2+"::0::A::1::0::0::0::0;");
						System.out.println(x2+","+y2+","+z2+","+csf);
						
//						int x2=x; int y2=y;

//						float R = Math.abs(ct.get(0)) / mmax;
//						float G = Math.abs(ct.get(1)) / mmax;
//						float B = Math.abs(ct.get(2)) / mmax;
						
						if(0<=x2-1 && x2+1<ps.length && 0<=y2-1 && y2+1<ps[0].length)
//							ps[x2][y2] = new Vectornf(R, G, B);
							ps[Math.round(x2)][Math.round(y2)] = new Vectornf(0f, 0f, 1f);

						/*
						 * float r = Math.abs(ct.get(0))/mmax; float g_=
						 * Math.abs(ct.get(1))/mmax; float b =
						 * Math.abs(ct.get(2))/mmax;
						 */
						/*
						 * float r = ct.get(0) > 0 ? ct.get(0)/mmax : 0; float
						 * g_= ct.get(1) > 0 ? ct.get(1)/mmax : 0; float b =
						 * ct.get(2) > 0 ? ct.get(2)/mmax : 0;
						 */
						/*
						 * float f = ct.get(0); float r = f>B?f:0; float
						 * g_=Math.abs(f)<B?1:0; float b = f<-B?-f:0;
						 */
						/*
						 * float r = Math.abs(ct.get(0)); float g_ =
						 * Math.abs(ct.get(1)); float b = Math.abs(ct.get(2));
						 */

						//
						// Vectornf f = new
						// Vectornf((ct.get(2)/mmax+1)/2,(ct.get(3)/mmax+1)/2,(ct.get(4)/mmax+1)/2);
						//
						// if(!(x2<0 || x2>= Main.CHUNK_SIZE*Main.PIXEL_SIZE ||
						// y2<0 || y2>= Main.CHUNK_SIZE*Main.PIXEL_SIZE)) {
						// int len = ps[x2][y2].length;
						// ps[x2][y2] = Arrays.copyOf(ps[x2][y2], len+1);
						// ps[x2][y2][len] = f;
						// }
						// float f = (ct.get(2)/mmax+1)/2+1;
						// if (!(x2 < 0 || x2 >= Main.CHUNK_SIZE *
						// Main.PIXEL_SIZE || y2 < 0
						// || y2 >= Main.CHUNK_SIZE * Main.PIXEL_SIZE))
						// ps[x2][y2] = new Vectornf((ct.get(2) / mmax + 1) / 2
						// + 1);
						// ps[x][y] = new Vectornf((ct.get(0)/mmax+1)/2,
						// (ct.get(1)/mmax+1)/2, 0f);
						// g.setColor(new
						// Color((ct.get(0)/mmax+1)/2,f,(ct.get(1)/mmax+1)/2));
						// g.setColor(new Color(0f,0f,f));
						// g.setColor(new Color(r,g_,b));

						// g.fillRect(x*Main.MULTI, y*Main.MULTI, 1*Main.MULTI,
						// 1*Main.MULTI);
						// g.fillRect(x*Main.MULTI+x2, y*Main.MULTI+y2, 1, 1);
					}
				}
			}
		}
	}
}

class MainCanvas extends Canvas {

	static final float B = 0.007f;
	static final int S = 2;

	Vectornf[][] ps;
	float mmax;

	public MainCanvas(float mmax, Vectornf[][] pixs) {
		this.mmax = mmax;
		this.ps = pixs;
	}

	@Override
	public void paint(Graphics g) {

		//
		// Vectornf[][] pss = new
		// Vectornf[Main.CHUNK_SIZE*Main.PIXEL_SIZE][Main.CHUNK_SIZE*Main.PIXEL_SIZE];
		// for(int x=0; x<ps.length; x++)for(int y=0; y<ps.length; y++) {
		// Vectornf[] fs = ps[x][y];
		// Vectornf f = new Vectornf(0f,0f,0f);
		// for(int i=0; i<fs.length; i++) {
		// f.add(fs[i]);
		// }
		// if(fs.length!=0)f.scale(1f/fs.length);
		// pss[x][y]=f;
		// }

		for (int x = 0; x < ps.length; x++) {
			for (int y = 0; y < ps.length; y++) {
				/*
				 * float r1 = ps[x][y].get(0) - 1; float r2 = ps[x +
				 * 1][y].get(0) - 1; float r3 = ps[x][y + 1].get(0) - 1; float
				 * r4 = ps[x + 1][y + 1].get(0) - 1; int c = 4; if (r1 < 0) { r1
				 * = 0; c -= 1; } if (r2 < 0) { r2 = 0; c -= 1; } if (r3 < 0) {
				 * r3 = 0; c -= 1; } if (r4 < 0) { r4 = 0; c -= 1; } float r =
				 * 0; if (c > 0) { r = (r1 + r2 + r3 + r4) / c; }
				 */
				//// if(r<0) {
				//// int c=0;
				//// float f=0;
				//// for(int x2=x-S;x2<=x+S;x2++)for(int y2=y-S;y2<=y+S;y2++) {
				//// if(x2==x && y2==y)continue;
				//// if(!(x2<0 || x2>= Main.CHUNK_SIZE*Main.PIXEL_SIZE || y2<0
				//// || y2>= Main.CHUNK_SIZE*Main.PIXEL_SIZE)) {
				//// float b = ps[x2][y2]-1;
				//// if(b>=0) {
				//// c+=1; f+=b;
				//// }
				//// }
				//// }
				//// if(c>0) {
				//// r=f/c;
				//// }else {
				//// r=0;
				//// }
				//// }
				// g.setColor(new Color(ps[x][y].get(0), ps[x][y].get(1),
				//// ps[x][y].get(2)));
				// Vectornf a= Util.lerps(new Vectornf(1f,0f), new
				//// Vectornf(0f,1f), r);
				// g.setColor(new Color(a.get(0),0f,a.get(1)));
				
				if(ps[x][y].size()==1) {
					Vectornf a = new Vectornf(0f,0f,0f);
					int c=0;
					
					for(int i=-1; i<=1; i++) {
						if(0<=x+i && x+i<ps.length)
							for(int j=-1; j<=1; j++) {
								if(0<=y+j && y+j<ps[x+i].length) {
									if(ps[x+i][y+j].size()!=1) {
										a.iadd(ps[x+i][y+j]);
										c++;
									}
									
								}
							}
					}
					if(c!=0) {
						a.iscale(1f/c);
					}
					
					
					
//					g.setColor(new Color(a.get(0), a.get(1), a.get(2)));
					g.setColor(Color.BLACK);
				}else {
					g.setColor(new Color(ps[x][y].get(0), ps[x][y].get(1), ps[x][y].get(2)));
				}
				
				
				g.fillRect(x, y, 1, 1);

			}
		}

	}
}