package edu.handong.round2;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

public class ShapeObject {
	
	int shape;
	
	Point start, end; // Line, Rect, Circle
	int width, height; // Rect, Circle
	int[] x, y; // Polyline, Pen, Eraser
	ArrayList<Point> doing = new ArrayList<Point>(); // Polyline, Pen, Eraser
	
	Color penColor = Color.black;
	int stroke;
	
	Stack<ShapeObject> clear = new Stack<ShapeObject>(); // 지워진 도형들
	
	ShapeObject link; // group된 shape object들
}
