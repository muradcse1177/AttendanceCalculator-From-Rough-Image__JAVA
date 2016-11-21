package com.utility;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;


public class ImageUtility {
	
	//Bi-cubic interpolation///scale

	public static BufferedImage getScaledImage(int width, int height, BufferedImage src) {

		BufferedImage dst = new BufferedImage(width, height, src.getType());
		Graphics2D g = dst.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage(src, 0, 0, width, height, null);
		g.dispose();
		return dst;
	}
	
	//crop image

	public static BufferedImage cropImage(BufferedImage src, Rectangle coords) {

		BufferedImage dest = src.getSubimage(coords.x, coords.y, coords.width, coords.height);
		return dest;
	}

	//Thresholding image
	
	public static BufferedImage getThreshHoldImage(BufferedImage in, int threshold) {
		BufferedImage image = new BufferedImage(in.getWidth(), in.getHeight(), in.getType());
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				Color c = new Color(in.getRGB(i, j));
				int avg = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
				if (avg >= threshold)
					image.setRGB(i, j, new Color(0, 0, 0).getRGB());
				else
					image.setRGB(i, j, new Color(255, 255, 255).getRGB());
			}
		}
		return image;
	}



         //matrix to image

	public static BufferedImage getImage(boolean[][] mat) {
		int w = mat.length;
		if (w <= 0)
			throw new RuntimeException("Zero length matrix doesn't accepted.");
		int h = mat[0].length;
		BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int col = mat[i][j] ? 0 : 255;
				out.setRGB(i, j, new Color(col, col, col).getRGB());
			}
		}
		return out;
	}
	
    
	//THresholding method
	
	public static boolean[][] getThresholdMatrix(BufferedImage image, int threshold) {
		int windowLength = 20;
		int[][] grayscaleMatrix = getGrayscaleMatrix(image);
		boolean[][] thresholdMatrix = new boolean[image.getWidth()][image.getHeight()];
		int ulx, uly, llx, lly, urx, ury, lrx, lry;
		double totalP, avg;
		int sub = windowLength / 2;
		int cs2d[][] = getCumulativeSumOf2DMatrix(grayscaleMatrix);

		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				ulx = Math.max(-1, i - sub - 1);
				uly = Math.max(-1, j - sub - 1);

				llx = Math.max(-1, i - sub - 1);
				lly = Math.min(image.getHeight()- 1, j + sub);

				urx = Math.min(image.getWidth() - 1, i + sub);
				ury = Math.max(-1, j - sub - 1);

				lrx = Math.min(image.getWidth() - 1, i + sub);
				lry = Math.min(image.getHeight() - 1, j + sub);

				totalP = (lrx - ulx) * (lry - uly);

				avg = cs2d[lrx][lry];
				if (ulx >= 0 && uly >= 0)
					avg += cs2d[ulx][uly];
				if (ury >= 0)
					avg -= cs2d[urx][ury];
				if (llx >= 0)
					avg -= cs2d[llx][lly];
				avg /= totalP;

				if ((avg * threshold * .01) <= (double) grayscaleMatrix[i][j])
					thresholdMatrix[i][j] = true;
				else
					thresholdMatrix[i][j] = false;

			}
		}
		return thresholdMatrix;
	}
	
	//gray scaling

	public static int[][] getGrayscaleMatrix(BufferedImage image) {

		int[][] grayscaleMatrix = new int[image.getWidth()][image.getHeight()];
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				grayscaleMatrix[i][j] = (int) (((image.getRGB(i, j)>> 16) & 0x000000FF) * 0.2126
						+ ((image.getRGB(i, j)>> 8) & 0x000000FF) * 0.587
						+ ((image.getRGB(i, j)) & 0x000000FF) * 0.114);
			}
		}
		return grayscaleMatrix;

	}
	
	
	//
	
	private static int[][] getCumulativeSumOf2DMatrix(int[][] grayscaleMatrix)
			throws RuntimeException {

		int w = grayscaleMatrix.length;
		if (w <= 0)
			throw new RuntimeException("Zero length matrix doesn't accepted.");
		int h = grayscaleMatrix[0].length;

		int lr[][] = new int[w][h];
		int ud[][] = new int[w][h];
		for (int i = 0; i < w; i++) {
			int prev = 0;
			for (int j = 0; j < h; j++) {
				lr[i][j] = prev + grayscaleMatrix[i][j];
				prev = lr[i][j];
			}
		}
		for (int i = 0; i < h; i++) {
			int prev = 0;
			for (int j = 0; j < w; j++) {
				ud[j][i] = prev + lr[j][i];
				prev = ud[j][i];
			}
		}
		return ud;
	}
}
