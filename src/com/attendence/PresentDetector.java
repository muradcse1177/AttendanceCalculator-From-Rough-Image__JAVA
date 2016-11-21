package com.attendence;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import Jama.Matrix;

public class PresentDetector {
	private static PresentDetector instance;
	private static Matrix matrixA;
	private static Matrix matrixB;

	public static PresentDetector getInstance() {
		if (instance == null)
			instance = new PresentDetector();
		return instance;
	}
	
	//sigmoid function..................

	public static Matrix sigmoid(Matrix m) {
		for (int i = 0; i < m.getRowDimension(); i++) {
			for (int j = 0; j < m.getColumnDimension(); j++) {

				m.set(i, j, (1 / (1 + Math.pow(Math.E, (-1 * m.get(i, j))))));
			}
		}
		return m;
	}

	private PresentDetector() {

		try {
			double matA[][] = new double[784][100];
			double matB[][] = new double[100][10];
			
			//input matA................
			Scanner input = new Scanner(new File("data/matA.txt"));
			for (int i = 0; i < 784; i++) {
				for (int j = 0; j < 100; j++) {
					matA[i][j] = input.nextDouble();
				}
			}
			input.close();
			
			
			//input matB.....................
			input = new Scanner(new File("data/matB.txt"));
			for (int i = 0; i < 100; i++) {
				for (int j = 0; j < 10; j++) {
					matB[i][j] = input.nextDouble();
				}
			}
			input.close();

			matrixA = new Matrix(matA);
			matrixB = new Matrix(matB);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean isPresent(BufferedImage image) {
		
		//making 28*28.............

		double arr[] = new double[28 * 28];
		int k = 0;
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				
				arr[k++] = new Color(image.getRGB(i, j)).getRed();
			}
		}

		Matrix matrix = new Matrix(arr, 1);

		Matrix res = sigmoid(matrix.times(matrixA)); //multiply mat and A

		res = sigmoid(res.times(matrixB));  //multiply mat A and B
		
		//output matrix......................

		double mx = res.get(0, 0);
		int mId = 0;
		for (int i = 0; i < 10; i++) {
			System.out.print(res.get(0, i) + " ");
			if (mx < res.get(0, i)) {
				mx = res.get(0, i);
				mId = i;
			}
		}
		System.out.println("Result: " + mId);
		return res.get(0, 0) < res.get(0, 1);
	}

}
