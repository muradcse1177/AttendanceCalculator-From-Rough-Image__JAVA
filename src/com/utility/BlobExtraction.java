package com.utility;

import java.util.Stack;

public class BlobExtraction {
	private Point[][] par;
	private boolean visited[][];
	// private final int MOVE_X[] = { -1, -1, -1, 0, 0, 1, 1, 1 };
	// private final int MOVE_Y[] = { -1, 0, 1, -1, 1, -1, 0, +1 };
	private final int MOVE_X[] = { -2, -2, -2, -2, -2, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2,
			2 };
	private final int MOVE_Y[] = { -2, -1, 0, 1, 2, -2, -1, 0, 1, 2, -2, -1, 0, 1, 2, -2, -1, 0, 1, 2, -2, -1, 0, 1,
			2 };

	private boolean mat[][];
	private Point max, sMax, tMax;
	private int width, height;

	
	public boolean[][] getExtractedMatrix(int minBlobSize) {
		boolean outputMat[][] = new boolean[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Point pn = findPar(i, j);

				if ((!mat[i][j]) && pn.size <= minBlobSize && pn.size >= 12) {
					outputMat[i][j] = true;
				} else
					outputMat[i][j] = false;
			}
		}

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (!outputMat[i][j]) {
					int tot = 0, cnt = 0;
					for (int k = 0; k < MOVE_X.length; k++) {
						int tempI = i + MOVE_X[k];
						int tempJ = j + MOVE_Y[k];
						tot++;
						if (isValid(tempI, tempJ)) {
							if(outputMat[tempI][tempJ]){
								cnt++;
							}
						}
					}
					double perc = (double)tot*100.0/cnt;
					if(perc>=60) outputMat[i][j] = false;

				}
			}
		}
		return outputMat;

	}

	/**
	 * 
	 * @return boolean[][] matrix, biggest blob is only taken care.
	 */
	public boolean[][] getExtractedMatrixByOrder(int order) throws RuntimeException {
		Point px = getMaxByOrder(order);
		Point p = findPar(px.i, px.j);

		boolean outputMat[][] = new boolean[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Point pn = findPar(i, j);
				if (p.i == pn.i && p.j == pn.j) {
					outputMat[i][j] = false;
				} else
					outputMat[i][j] = true;
			}
		}
		return outputMat;
	}

	boolean isMaxFound(Point p) {
		if (p.i == max.i && p.j == max.j && p.size == max.size)
			return true;
		if (p.i == sMax.i && p.j == sMax.j && p.size == sMax.size)
			return true;
		if (p.i == tMax.i && p.j == tMax.j && p.size == tMax.size)
			return true;
		return false;
	}

	private void maxGen() {
		max = new Point(0, 0, 0);
		sMax = new Point(0, 0, 0);
		tMax = new Point(0, 0, 0);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Point p = findPar(par[i][j].i, par[i][j].j);
				if (isMaxFound(p))
					continue;

				int sz = p.size;
				if (sz > max.size) {
					tMax = sMax;
					sMax = max;
					max = p;
				} else if (sz > sMax.size) {
					tMax = sMax;
					sMax = p;

				} else if (sz > tMax.size) {
					tMax = p;
				}
			}
		}
	}

	public Point getMaxByOrder(int order) throws RuntimeException {
		if (order == 1)
			return max;
		else if (order == 2)
			return sMax;
		else if (order == 3)
			return tMax;
		else
			throw new RuntimeException("Only 1, 2 and 3 are valid");
	}

	public BlobExtraction(boolean mat[][]) throws RuntimeException {
		this.mat = mat;
		// TODO Change width and height
		width = mat.length;
		if (width <= 0)
			throw new RuntimeException("Matrix size can not be 0.");
		height = mat[0].length;
		par = new Point[width][height];
		visited = new boolean[width][height];

		init();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				process(i, j);
			}
		}
		maxGen();
	}

	private boolean isValid(int x, int y) {
		if (x < width && x >= 0 && y < height && y >= 0 && !mat[x][y])
			return true;
		return false;
	}

	private void process(int ii, int jj) {
		if (!isValid(ii, jj) || visited[ii][jj])
			return;

		Stack<Point> st = new Stack<Point>();
		st.push(new Point(ii, jj, 0));

		while (!st.empty()) {
			Point first = st.pop();
			if (mat[first.i][first.j] || visited[first.i][first.j])
				continue;
			visited[first.i][first.j] = true;
			for (int i = 0; i < MOVE_X.length; i++) {
				int tempI = first.i + MOVE_X[i];
				int tempJ = first.j + MOVE_Y[i];

				if (!isValid(tempI, tempJ))
					continue;

				if (!visited[tempI][tempJ]) {
					st.push(new Point(tempI, tempJ, 0));
				}
				union(first.i, first.j, tempI, tempJ);
			}

		}

	}

	private Point findPar(int i, int j) {
		int tempI, tempJ;
		int oi = i;
		int oj = j;
		while (!(par[i][j].i == i && par[i][j].j == j)) {
			tempI = i;
			tempJ = j;
			i = par[tempI][tempJ].i;
			j = par[tempI][tempJ].j;
		}
		while (!(par[oi][oj].i == oi && par[oi][oj].j == oj)) {
			tempI = oi;
			tempJ = oj;
			par[tempI][tempJ] = par[i][j];
			oi = par[tempI][tempJ].i;
			oj = par[tempI][tempJ].j;
		}
		return par[i][j];
	}

	private void init() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				par[i][j] = new Point(i, j, 1);
			}
		}
	}

	private void union(int i, int j, int l, int m) {
		Point p1 = findPar(i, j);
		Point p2 = findPar(l, m);
		if (p1.i == p2.i && p1.j == p2.j)
			return;

		int sz = p1.size + p2.size;
		par[p1.i][p1.j] = par[p2.i][p2.j];
		p2.size = sz;

	}

}

class Point {
	int size, i, j;

	public Point(int i, int j, int sz) {
		this.size = sz;
		this.i = i;
		this.j = j;
	}

	public Point(Point p) {
		this.i = p.i;
		this.j = p.j;
		this.size = p.size;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("(%-2d %-2d %-2d)", i, j, size);
	}
}
