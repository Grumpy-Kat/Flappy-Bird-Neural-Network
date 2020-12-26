package main;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Oval implements Shape {
	private double x;
	private double y;
	private double widthR;
	private double heightR;
	
	private double angle;
	
	public Oval(double x, double y, double widthR, double heightR, double angle) {
		this.x = x;
		this.y = y;
		this.widthR = widthR;
		this.heightR = heightR;
		this.angle = angle;
	}

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean contains(Rectangle2D rect) {
		return contains(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}

	@Override
	public boolean contains(double x, double y) {
		double term1 = (Math.pow((x - this.x) * Math.cos(angle) + (y - this.y) * Math.sin(angle), 2)) / (widthR * widthR);
		double term2 = (Math.pow((x - this.x) * Math.sin(angle) - (y - this.y) * Math.cos(angle), 2)) / (heightR * heightR);
		return (term1 + term2 <= 1);
	}

	@Override
	public boolean contains(double x, double y, double width, double height) {
		return (contains(x, y) && contains(x + width, y + height));
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle((int)Math.round(x - widthR), (int)Math.round(y - heightR), (int)Math.round(widthR * 2), (int)Math.round(heightR * 2));
	}

	@Override
	public Rectangle2D getBounds2D() {
		return getBounds();
	}

	@Override
	public PathIterator getPathIterator(AffineTransform transform) { return null; }

	@Override
	public PathIterator getPathIterator(AffineTransform transform, double flatness) { return null; }

	@Override
	public boolean intersects(Rectangle2D rect) {
		return intersects(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}

	@Override
	public boolean intersects(double x, double y, double width, double height) {
		if(!getBounds().intersects(x, y, width, height)) {
			return false;
		}
		int[] quadrants = new int[4];
		quadrants[0] = getQuadrant(x, y);
		quadrants[1] = getQuadrant(x + width, y);
		quadrants[2] = getQuadrant(x, y + height);
		quadrants[3] = getQuadrant(x + width, y + height);
		for(int i = 1; i < 4; i++) {
			if(quadrants[i-1] != quadrants[i]) {
				return true;
			}
		}
		if(contains(x, y) || contains(x + width, y) ||
		   contains(x, y + height) || contains(x + width, y + height)) {
			return true;
		}
		return false;
	}
	
	private int getQuadrant(double x, double y) {
		if(x > this.x && y > this.y) {
			return 1;
		}
		if(x < this.y && y > this.y) {
			return 2;
		}
		if(x < this.x && y < this.y) {
			return 3;
		}
		if(x > this.x && y < this.y) {
			return 4;
		}
		return -1;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getWidth() {
		return widthR;
	}
	
	public double getHeight() {
		return heightR;
	}
}
