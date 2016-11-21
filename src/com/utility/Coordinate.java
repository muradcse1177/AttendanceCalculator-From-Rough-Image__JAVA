package com.utility;

import java.awt.Point;

//coordinate declaration

public class Coordinate {
	private double x, y;

	public Coordinate(double xx, double yy) {
		this.x = xx;
		this.y = yy;
	}
	public Coordinate(Point p){
		this.x = p.getX();
		this.y = p.getY();
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getIntX() {
		return (int) x;
	}

	public int getIntY() {
		return (int) y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return super.toString() + "@Cordinate value: "
				+ String.format("(x,y)=(%f,%f)", x, y);
	}

}
