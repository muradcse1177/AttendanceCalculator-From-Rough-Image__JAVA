package com.attendence;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JPanel;

import com.utility.ImageUtility;
import com.utility.Warper;

public class ImagePanel extends JPanel {

	private BufferedImage originalImage;
	private Image visibleImage;
	private ArrayList<Point> markerPosition = new ArrayList<>();
	private Main main;
	private boolean isSetImage = false;
	private static int MARKER_RADIUS = 15;
	public Dimension screenSize = new Dimension(900, 1400);
	private double ratio_x;
	private double ratio_y;
	private boolean isProcessed = false;

	public ImagePanel(Main main, BufferedImage image, int width, int height) {
		this.main = main;
		setLayout(new BorderLayout());
		this.originalImage = image;
		setPreferredSize(new Dimension(width, height));
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		
		//marker making........................
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (isSetImage && !isProcessed) {
					if (markerPosition.size() < 4)
						markerPosition.add(e.getPoint());
					else {
						markerPosition.remove(0);
						markerPosition.add(e.getPoint());
					}
					repaint();
				}

			}
		});

	}
	
	
	//crop image from main image................

	public BufferedImage getCropedImage() {
		if (markerPosition.size() == 4) {
			ArrayList<Point> ordPoint = getOrderMarkerPoint();
			return Warper.getCropedImage(originalImage, ordPoint, Main.OUTPUT_IMAGE_WIDTH, Main.OUTPUT_IMAGE_HEIGHT);
		}
		return null;
	}
	
	
	//screening image from old image....................

	public void setImage(BufferedImage image, BufferedImage vImage) {
		isProcessed = false;
		markerPosition = new ArrayList<>();
		repaint();

		this.originalImage = image;
		this.ratio_x = (double) image.getWidth() / screenSize.width;
		this.ratio_y = (double) image.getHeight() / screenSize.height;
		if(vImage!=null)
		this.visibleImage = ImageUtility.getScaledImage(screenSize.width, screenSize.height, vImage);
		isSetImage = true;
		repaint();
	}
	
	// marker painting for the 4 point......................

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		if (visibleImage != null) {
			g2.drawImage(visibleImage, 0, 0, null);

			for (int i = 0; isSetImage && !isProcessed && i < markerPosition.size() && i < 4; i++) {
				if (markerPosition.get(i).getX() != -1 && markerPosition.get(i).getY() != -1) {

					g2.setColor(Color.RED);
					int X = (int) (markerPosition.get(i).getX() - MARKER_RADIUS / 2);
					int Y = (int) markerPosition.get(i).getY() - MARKER_RADIUS / 2;

					g2.fillOval(X, Y, MARKER_RADIUS, MARKER_RADIUS);
				}

			}
		}

	}
	
	
	public void setProcessed(){
		isProcessed = true;
	}
	
	
	private ArrayList<Point> getOrderMarkerPoint() {
		ArrayList<Point> temp = new ArrayList<>();
		for (int i = 0; i < markerPosition.size(); i++) {
			temp.add(new Point((int) (markerPosition.get(i).getX() * ratio_x),
					(int) (markerPosition.get(i).getY() * ratio_y)));
		}

		Collections.sort(temp, new Comparator<Point>() {

			@Override
			public int compare(Point o1, Point o2) {
				if (o1.getX() > o2.getX())
					return 1;
				return -1;
			}
		});
		ArrayList<Point> result = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			result.add(new Point(-1, -1));
		}
		if (temp.get(0).y > temp.get(1).y) {
			result.set(3, temp.get(0));
			result.set(0, temp.get(1));
		} else {
			result.set(0, temp.get(0));
			result.set(3, temp.get(1));
		}
		if (temp.get(2).y > temp.get(3).y) {
			result.set(1, temp.get(3));
			result.set(2, temp.get(2));
		} else {
			result.set(1, temp.get(2));
			result.set(2, temp.get(3));
		}

		return result;

	}

	public void clearProcessed() {
		isProcessed = false;
		
	}
}
