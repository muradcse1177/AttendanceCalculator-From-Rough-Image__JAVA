package com.utility;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import Jama.Matrix;

public class Warper {
	
	public static BufferedImage getCropedImage(BufferedImage image,
			ArrayList<Point> cornersCordinate, int width, int height) {
		
		
		// image to rgb value..........
		
		int mat[][] = new int[image.getWidth()][image.getHeight()];
		for(int i=0; i<image.getWidth(); i++){
			for (int j = 0; j <image.getHeight(); j++) {
				mat[i][j] = image.getRGB(i, j);
			}
		}
		
		// avg coordinate calculate................
		
		
		int avg_width = (int)(((cornersCordinate.get(1).x-cornersCordinate.get(0).x)+
				(cornersCordinate.get(2).x-cornersCordinate.get(3).x))/2f);
		int avg_height = (int)(((cornersCordinate.get(3).y-cornersCordinate.get(0).y)+
				(cornersCordinate.get(2).y-cornersCordinate.get(1).y))/2f);
		
		double X1 = Math.abs(cornersCordinate.get(0).getX());
		double Y1 = Math.abs(cornersCordinate.get(0).getY());
		double X2 = Math.abs(cornersCordinate.get(1).getX());
		double Y2 = Math.abs(cornersCordinate.get(1).getY());
		double X3 = Math.abs(cornersCordinate.get(2).getX());
		double Y3 = Math.abs(cornersCordinate.get(2).getY());
		double X4 = Math.abs(cornersCordinate.get(3).getX());
		double Y4 = Math.abs(cornersCordinate.get(3).getY());
		double x1 = 0;
		double y1 = 0;
		double x2 = avg_width - 1;
		double y2 = 0;
		double x3 = avg_width - 1;
		double y3 = avg_height - 1;
		double x4 = 0;
		double y4 = avg_height - 1;
		
		
		//transformation matrix..................

		double M_a[][] = { { x1, y1, 1, 0, 0, 0, -x1 * X1, -y1 * X1 },
				{ x2, y2, 1, 0, 0, 0, -x2 * X2, -y2 * X2 },
				{ x3, y3, 1, 0, 0, 0, -x3 * X3, -y3 * X3 },
				{ x4, y4, 1, 0, 0, 0, -x4 * X4, -y4 * X4 },
				{ 0, 0, 0, x1, y1, 1, -x1 * Y1, -y1 * Y1 },
				{ 0, 0, 0, x2, y2, 1, -x2 * Y2, -y2 * Y2 },
				{ 0, 0, 0, x3, y3, 1, -x3 * Y3, -y3 * Y3 },
				{ 0, 0, 0, x4, y4, 1, -x4 * Y4, -y4 * Y4 }, };

		double M_b[][] = { { X1 }, { X2 }, { X3 }, { X4 }, { Y1 }, { Y2 },
				{ Y3 }, { Y4 }, };

		Matrix A = new Matrix(M_a);
		Matrix B = new Matrix(M_b);		

		Matrix C = A.solve(B);
		double a = C.get(0, 0);
		double b = C.get(1, 0);
		double c = C.get(2, 0);
		double d = C.get(3, 0);
		double e = C.get(4, 0);
		double f = C.get(5, 0);
		double g = C.get(6, 0);
		double h = C.get(7, 0);

		int output[][] = new int[avg_width][avg_height];
		for (int i = 0; i < avg_width; i++) {
			for (int j = 0; j < avg_height; j++) {
				
				
				//law of transformation................
				
				int x = (int) (((a * i) + (b * j) + c) / ((g * i) + (h * j) + 1));
				int y = (int) (((d * i) + (e * j) + f) / ((g * i) + (h * j) + 1));
				output[i][j] = mat[x][y];
			}
		}
		
		//new rgb value of matrix from image...............
		
		BufferedImage outputImage = new BufferedImage(avg_width, avg_height, image.getType());
		for(int i=0; i<avg_width; i++){
			for(int j=0; j<avg_height; j++){
				outputImage.setRGB(i, j, output[i][j]);
			}
		}
		

		return ImageUtility.getScaledImage(width, height, outputImage);
	}
}
