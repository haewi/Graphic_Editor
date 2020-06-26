package edu.handong.round2;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class ShapeObject {
	
	int shape;
	
	Point start, end;
	int width, height;
	int[] x, y;
	ArrayList<Point> doing = new ArrayList<Point>();
	Color penColor = Color.black;
	int stroke;
	
}
