package edu.handong.round2;

import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Stack;

public class ShapeObject {
	
	int shape;
	boolean fill = false;
	boolean visible = true;
	
	Point start, end; // Line, Rect, Circle
	int width, height; // Rect, Circle
	int[] x, y; // Polyline, Pen, Eraser
	ArrayList<Point> doing = new ArrayList<Point>(); // Polyline, Pen, Eraser
	
	Color penColor = Color.black;
	int stroke;
	
	Stack<ShapeObject> clear = new Stack<ShapeObject>(); // 지워진 도형들
	
	// 이동된 거리 및 이동된 도형들 index
	int dif_x, dif_y;
	ArrayList<Integer> select = new ArrayList<Integer>();
	
//	ShapeObject link; // group된 shape object들
	
	
	
	public static ShapeObject copy(ShapeObject from) {
		ShapeObject to = new ShapeObject();
		
//		System.out.println("1" + (to == from));
		
		to.shape = from.shape;
		to.fill = from.fill;
		if(from.start != null) {
			to.start = new Point(from.start.x, from.start.y);
			to.end = new Point(from.end.x, from.end.y);
		}
		to.width = from.width;
		to.height = from.height;
		
//		System.out.println("2" + (to == from));
		
		if(from.x != null) {
			to.x = new int[from.x.length];
			to.y = new int[from.y.length];
			for(int i=0; i<from.x.length; i++) {
				to.x[i] = from.x[i];
				to.y[i] = from.y[i];
			}
		}
		
//		System.out.println("3" + (to == from));
		
		for(int i=0; i<from.doing.size(); i++) {
			to.doing.add(from.doing.get(i));
		}
		
//		System.out.println("4" + (to == from));
		
		to.penColor = from.penColor;
		to.stroke = from.stroke;
		
//		System.out.println("5" + (to == from));
		
		for(int i=0; i<from.clear.size(); i++) {
			to.clear.add(from.clear.get(i));
		}
		
		to.dif_x = from.dif_x;
		to.dif_y = from.dif_y;
		for(int i=0; i<from.select.size(); i++) {
			to.select.add(from.select.get(i));
		}
		
//		System.out.println("6" + (to == from));
		
//		while(from.link!=null) {
//			to.link = copy(from.link);
//		}
		
		return to;
	}
}





