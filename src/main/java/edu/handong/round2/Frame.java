package edu.handong.round2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.*;
import javax.swing.event.*;


public class Frame {

	// 위치 찾는 변수들
//	j;
	int one, two, three, four, five, six, seven;

	// 선택 가능한 도구들
	public static final int DEFAULT = 0;
	public static final int LINE = 1;
	public static final int RECT = 2;
	public static final int CIRCLE = 3;
	public static final int POLYLINE = 4;
	public static final int CLEAR = 5;
	public static final int PEN = 6;
	public static final int ERASER = 7;
	public static final int SELECT = 8;
	public static final int DRAG = 9;
	public static final int COPY = 10;
	public static final int PASTE = 11;
	public static final int DELETE = 12;

	// 선택한 기능
	public int function = DEFAULT;

	// 현재 굵기와 그 slider, 채우기 상태, 색깔
	int stroke=3;
	JSlider slider;
	boolean fill=false;
	Color color = Color.black;

	// 처음으로 선택된 지점, 마지막으로 선택된 지점
	Point initPoint; //, lastPoint;

	// 기본 세팅
	JFrame mainFrame;
	JToolBar toolbar;
	Canvas canvas;
	Label mode = new Label("DEFAULT");
	Label fillMode = new Label("Fill: No");

	// 현재까지 그려진 도형들
	Stack<ShapeObject> shapes = new Stack<ShapeObject>();
	Stack<ShapeObject> deleteShape = new Stack<ShapeObject>();
	
	// 선택된 도형들의 index, select
	ArrayList<Integer> select = new ArrayList<Integer>();//null;
	boolean move=false;
	ShapeObject drag = new ShapeObject();
	
	// 복사한 도형들
	ArrayList<ShapeObject> copied = new ArrayList<ShapeObject>();
	
	// 한번에 삭제할 도형들
	ArrayList<ShapeObject> delete = new ArrayList<ShapeObject>();
	

	public Frame() {
		mainFrame = new JFrame("그림판");
		mainFrame.setBounds(0, 200, 800, 600);
		mainFrame.setVisible(true);
		mainFrame.getContentPane().setBackground(Color.white);
		mainFrame.setBackground(new Color(87, 153, 245));
		mainFrame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});

		// canvas 기본 설정
		canvas = new Canvas();
		canvas.addMouseListener(new MouseListen());
		canvas.addMouseMotionListener(new MouseListen());
		canvas.setBounds(0, 100, 800, 600);
//		canvas.setBounds(0, 0, 800, 600);
		canvas.setBackground(Color.white);
		mainFrame.add(canvas);

		// Toolbar 기본 설정
		toolbar = new JToolBar();
		toolbar.setBounds(0, 0, 800, 97);
//		toolbar.setBounds(700, 0, 100, 600);
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
//		toolbar.setLayout(new FlowLayout());
		toolbar.setBackground(new Color(218, 207, 251));

		mainFrame.add(toolbar);

		// toolbar용 버튼 - 첫번째 줄
		ArrayList<Button> toolButton = new ArrayList<Button>();
		String[] toolbarLabels = new String[] {"Line", "Rect", "Circle", "Polyline", "<--", "-->"};
		ButtonListen listener = new ButtonListen();
		for(int i=0; i<toolbarLabels.length; i++) {
			Button tempBtn;
			tempBtn = new Button(toolbarLabels[i]);
			tempBtn.setSize(80, 40);
			tempBtn.setLocation(10+i*80, 7);
//			tempBtn.setLocation(14, 5+50*i);
			tempBtn.addActionListener(listener);
			tempBtn.setFont(new Font("Chalkboard", Font.PLAIN, 15));
//			tempBtn.setForeground(Color.black);
			toolButton.add(tempBtn);
		}

		// toolbar에 button 넣기
		for(Button b: toolButton)
			toolbar.add(b);

		// slider 넣기
		slider = new JSlider(0, 50, 3);
		slider.addChangeListener(new ChangeListen());
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		slider.setBounds(10+80*6+50, 7, 170, 40);
//		slider.setBounds(0, 5+50*7+20, 100, 60);
		stroke = (int) slider.getValue();

		Label label = new Label("Stroke:");
		label.setFont(new Font("Chalkboard", Font.PLAIN, 15));
		label.setBounds(10+80*6, 12, 50, 30);
//		label.setBounds(40, 8+50*7+10, 80, 30);
		toolbar.add(label);
		toolbar.add(slider);
		
		// clear button
		Button btn = new Button("Clear");
		btn.setBounds(10+80*7+140, 7, 80, 40);
		btn.addActionListener(listener);
		btn.setFont(new Font("Chalkboard", Font.PLAIN, 15));
		
		toolbar.add(btn);
		
		// toolbar용 버튼 - 2번째 줄
		toolbarLabels = new String[] {"Pen", "Color", "Fill", "Eraser", "Delete", "Select", "Copy", "Paste"};
		for(int i=0; i<toolbarLabels.length; i++) {
			Button tempBtn;
			tempBtn = new Button(toolbarLabels[i]);
			tempBtn.setSize(80, 40);
			tempBtn.setLocation(10+i*80, 50);
//			tempBtn.setLocation(14, 5+50*i);
			tempBtn.addActionListener(listener);
			tempBtn.setFont(new Font("Chalkboard", Font.PLAIN, 15));
//			tempBtn.setForeground(Color.black);
			toolButton.add(tempBtn);
		}
		
		// toolbar에 button 넣기
		for(Button b: toolButton)
			toolbar.add(b);
		
		// fill 상태 알려주기
		fillMode.setBounds(10+80*7+90, 50, 50, 40);
		toolbar.add(fillMode);
		
		// mode 알려주기
		mode.setBounds(10+80*7+150, 50, 80, 40);
		toolbar.add(mode);
	}
	
	
	public class Canvas extends JPanel{
		// repaint() 실행시 실행됨
		@Override
		protected void paintComponent(Graphics g) {
			one=1;
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g;

			for(int j=0; j<shapes.size(); j++) {
				ShapeObject so = shapes.get(j);
				if(!so.visible) continue;
				if(!select.isEmpty() && select.contains(j)) {
					g2.setColor(new Color(68, 178, 250));
				}
				else {
					g2.setColor(so.penColor);
				}
				g2.setStroke(new BasicStroke(so.stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//				g2.setStroke(new BasicStroke(so.stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {9}, 3));
				if(so.shape==LINE) {
					g2.drawLine(so.start.x, so.start.y, so.end.x, so.end.y);
				}
				else if(so.shape==RECT) {
					if(so.fill) {
						g2.fillRect(so.start.x, so.start.y, so.width, so.height);
					}
					else {
						g2.drawRect(so.start.x, so.start.y, so.width, so.height);
					}
				}
				else if(so.shape == CIRCLE) {
					if(so.fill) {
						g2.fillOval(so.start.x, so.start.y, so.width, so.height);
					}
					else {
						g2.drawOval(so.start.x, so.start.y, so.width, so.height);
					}
				}
				else if(so.shape == POLYLINE) {

//					System.out.println("doing size: " + so.doing.size());
					
					if(so.doing.isEmpty()) {
//						System.out.println("Polyline x.length: " + so.x.length);
						g2.drawPolyline(so.x, so.y, so.x.length);
					} // polyline이 완성되었을 때
					else {	
						
//						System.out.println("drawing polyline");
//						System.out.println("shapes size: " + shapes.size());
						
						int[] x = new int[so.doing.size()];
						int[] y = new int[so.doing.size()];

						for(int i=0; i<so.doing.size(); i++) {
							x[i] = so.doing.get(i).x;
							y[i] = so.doing.get(i).y;
						}

						g2.drawPolyline(x, y, x.length);

					} // polyline이 완성되기 전 
				}
				else if(so.shape == PEN) {
					if(so.doing.isEmpty()) {
						g2.drawPolyline(so.x, so.y, so.x.length);
					} // pen이 완성되었을 때
					else {
						
//						System.out.println("drawing pen");
//						System.out.println("shapes size: " + shapes.size());
						
						int[] x = new int[so.doing.size()];
						int[] y = new int[so.doing.size()];

						for(int i=0; i<so.doing.size(); i++) {
//							g2.drawLine(x[i], y[i], x[i+1], y[i+1]);
							x[i] = so.doing.get(i).x;
							y[i] = so.doing.get(i).y;
						}

						g2.drawPolyline(x, y, x.length);

					} // pen이 완성되기 전 
				}
				else if(so.shape == ERASER) {
					g2.setColor(Color.white);
					if(so.doing.isEmpty()) {
						g2.drawPolyline(so.x, so.y, so.x.length);
					} // 지우개가 완성되었을 때
					else {
						
//						System.out.println("drawing eraser");
//						System.out.println("shapes size: " + shapes.size());
						
						int[] x = new int[so.doing.size()];
						int[] y = new int[so.doing.size()];

						for(int i=0; i<so.doing.size(); i++) {
							x[i] = so.doing.get(i).x;
							y[i] = so.doing.get(i).y;
						}
						
						g2.drawPolyline(x, y, x.length);
					} // 지우개가 완성되기 전 
				}
			}
			
			if(drag.shape == DRAG) {
				g2.setColor(Color.LIGHT_GRAY);
				g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {9}, 3));
				g2.drawRect(Math.min(drag.start.x, drag.end.x), Math.min(drag.start.y, drag.end.y),Math.abs(drag.start.x-drag.end.x), Math.abs(drag.start.y-drag.end.y));
			}
		}

	}

	
	public class ButtonListen implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			two=1;
			
			String str = e.getActionCommand();

			if(str.equals("Line")) {
				if(function==LINE) {
					function = DEFAULT;
					mode.setText("DEFAULT");
				}
				else{
					function = LINE;
					mode.setText("LINE");
				}
				
			}
			else if(str.equals("Rect")) {
				if(function==RECT) {
					function = DEFAULT;
					mode.setText("DEFAULT");
				}
				else {
					function = RECT;
					mode.setText("RECT");
				}
				
			}
			else if(str.equals("Circle")) {
				if(function==CIRCLE) {
					function = DEFAULT;
					mode.setText("DEFAULT");
				}
				else {
					function = CIRCLE;
					mode.setText("CIRCLE");
				}
				
			}
			else if(str.equals("Polyline")) {
				if(function==POLYLINE) {
					function = DEFAULT;
					mode.setText("DEFAULT");
				}
				else {
					function = POLYLINE;
					mode.setText("POLYLINE");
				}
				
			}
			else if(str.equals("Color")) {
				color = JColorChooser.showDialog(null, "Choose Color", Color.black);
			}
			else if(str.equals("<--")) {
				if(shapes.isEmpty()) return;
				deleteShape.add(shapes.pop());
				
				
				if(deleteShape.peek().shape == CLEAR) {
					int size = deleteShape.peek().clear.size();
					for(int i=size-1; i>=0; i--) {
						shapes.add(deleteShape.peek().clear.get(i));
					}
				}
				else if(deleteShape.peek().shape == DRAG) {
					ShapeObject so = deleteShape.peek();
					for(int i=0; i<so.select.size(); i++) {
						relocate(shapes.get(so.select.get(i)), -so.dif_x, -so.dif_y);
					}
				}
				else if(deleteShape.peek().shape == DELETE) {
					for(int i=0; i<deleteShape.peek().clear.size(); i++) {
						deleteShape.peek().clear.get(i).visible = true;
					}
				}
				
				canvas.repaint();
			}
			else if(str.equals("-->")) {
				if(deleteShape.isEmpty()) return;
				
				if(deleteShape.peek().shape == CLEAR) {
					int size = deleteShape.peek().clear.size();
					for(int i=0; i<size; i++) {
						shapes.pop();
					}
				}
				else if(deleteShape.peek().shape == DRAG) {
					ShapeObject so = deleteShape.peek();
					for(int i=0; i<so.select.size(); i++) {
						relocate(shapes.get(so.select.get(i)), so.dif_x, so.dif_y);
					}
				}
				else if(deleteShape.peek().shape == DELETE) {
					for(int i=0; i<deleteShape.peek().clear.size(); i++) {
						deleteShape.peek().clear.get(i).visible = false;
					}
				}
				
				shapes.add(deleteShape.pop());
				canvas.repaint();
			}
			else if(str.equals("Clear")) {
				if(function == CLEAR) {
					fill = false;
					fillMode.setText("Fill: No");
					color = Color.black;
					shapes.clear();
					deleteShape.clear();
					select.clear();
					copied.clear();
					delete.clear();
					move = false;
					drag.doing.clear();
					mode.setText("DEFAULT");
				}
				else {
					function = CLEAR;
					mode.setText("CLEAR");
					
					int size =  shapes.size();
					ShapeObject newShape = new ShapeObject();
					newShape.shape = CLEAR;
					for(int i=0; i<size; i++) {
						newShape.clear.add(shapes.pop());
					}
					shapes.add(newShape);
					deleteShape.clear();
					canvas.repaint();
				}
			}
			else if(str.equals("Pen")) {
				if(function==PEN) {
					function = DEFAULT;
					mode.setText("DEFAULT");
				}
				else {
					function = PEN;
					mode.setText("PEN");
				}
				
			}
			else if(str.equals("Eraser")) {
				if(function==ERASER) {
					function = DEFAULT;
					mode.setText("DEFAULT");
				}
				else {
					function = ERASER;
					mode.setText("ERASER");
				}
				
			}
			else if(str.equals("Select")) {
				if(function == SELECT) {
					function = DEFAULT;
					mode.setText("DEFAULT");
				}
				else {
					function = SELECT;
					mode.setText("SELECT");
				}
			}
			else if(str.equals("Copy")) {
				function = DEFAULT;
				mode.setText("DEFAULT");
				if(select.isEmpty()) {
					function = SELECT;
					mode.setText("SELECT");
					return;
				}
				shapes.pop();
				copied.clear(); // 원상 복귀 후,
				for(int i=0; i<select.size(); i++) {
//					System.out.println("select.size(): " + select.size());
					copied.add(ShapeObject.copy(shapes.get(select.get(i)))); // 복사할 것 추가하기
//					System.out.println("copied.size(): " + copied.size());
				}
			}
			else if(str.equals("Paste")) {
				if(function == PASTE) {
					function = DEFAULT;
					mode.setText("DEFAULT");
				}
				else {
					function = PASTE;
					mode.setText("PASTE");
					
					for(int i=0; i<copied.size(); i++) {
						ShapeObject so = ShapeObject.copy(copied.get(i));
						copied.set(i, so);
					}
				}
			}
			else if(str.equals("Fill")) {
				fill = !fill;
				if(fill) {
					fillMode.setText("Fill: Yes");
				}
				else {
					fillMode.setText("Fill: No");
				}
			}
			else if(str.equals("Delete")) {
				function = DEFAULT;
				mode.setText("DEFAULT");
				
				if(function != POLYLINE) {
					for(ShapeObject s: shapes) {
						if(s.shape == POLYLINE) {
							arrayListToArray(s);
							s.doing.clear();
						}
					}
				}
				
				if(select.isEmpty()) {
					function = SELECT;
					mode.setText("SELECT");
					return;
				}
				
				ShapeObject so = new ShapeObject();
				so.shape = DELETE;
				
				for(int i=0; i<select.size(); i++) {
//					System.out.println("select.size(): " + select.size());
					so.clear.add(shapes.get(select.get(i))); // 삭제할 것 추가하기
//					System.out.println("copied.size(): " + copied.size());
				}
				
				for(int i=0; i<select.size(); i++) {
					shapes.get((int)select.get(i)).visible = false;
//					System.out.println("select.size(): " + select.size() + shapes.get((int)select.get(i)).visible);
				}
				shapes.pop();
				shapes.add(so);
//				System.out.println("clear size: " + shapes.peek().clear.size());
//				System.out.println(shapes.size());
//				for(int i=0; i<shapes.size(); i++) {
//					System.out.println("shape: " + shapes.get(i).shape);
//				}
			}
			
			if(function != POLYLINE) {
				for(ShapeObject s: shapes) {
					if(s.shape == POLYLINE) {
						arrayListToArray(s);
						s.doing.clear();
					}
				}
			}
			if(function != SELECT || function != PASTE) {
				select.clear();
				canvas.repaint();
			}
			

//			System.out.println(shapes.size());
//			for(int i=0; i<shapes.size(); i++) {
//				System.out.println("shape: " + shapes.get(i).shape);
//			}
		}

	}

	public class MouseListen extends MouseAdapter implements MouseListener {
		
		ShapeObject newShape;
		
		@Override
		public void mousePressed(MouseEvent e) {
			three =1;
			super.mousePressed(e);

			newShape = new ShapeObject();
			initPoint = new Point();
			
			newShape.stroke = stroke;
			newShape.penColor = color;
			newShape.fill = fill;
			deleteShape.clear();
			
			if(function == LINE) {
				newShape.shape = LINE;
				newShape.start = e.getPoint();
				shapes.add(newShape);
			}
			else if(function == RECT) {
				newShape.shape = RECT;
				newShape.start = e.getPoint();
				newShape.end = e.getPoint();
				initPoint = e.getPoint();
				shapes.add(newShape);
			}
			else if(function == CIRCLE) {
				newShape.shape = CIRCLE;
				newShape.start = e.getPoint();
				newShape.end = e.getPoint();
				initPoint = e.getPoint();
				shapes.add(newShape);
			}
			else if(function == POLYLINE) {
				if(shapes.isEmpty()) {
					newShape.shape = POLYLINE;
					shapes.add(newShape);
				} // 첫 도형이면
				else if(shapes.get(shapes.size()-1).shape != POLYLINE) {
					newShape.shape = POLYLINE;
					shapes.add(newShape);
				} // Polyline의 첫번째 점이면
				else {
					if(shapes.get(shapes.size()-1).doing.isEmpty()) {
						newShape.shape = POLYLINE;
						shapes.add(newShape);
					}
					else {
						newShape = shapes.get(shapes.size()-1);
					}
				} // n번째 점이면
			}
			else if(function == PEN) {
				if(shapes.isEmpty()) {
					newShape.shape = PEN;
					shapes.add(newShape);
				} // 첫 도형이면
				else if(shapes.get(shapes.size()-1).shape != PEN) {
					newShape.shape = PEN;
					shapes.add(newShape);
				} // Polyline의 첫번째 점이면
				else {
					if(shapes.get(shapes.size()-1).doing.isEmpty()) {
						newShape.shape = PEN;
						shapes.add(newShape);
					}
					else {
						newShape = shapes.get(shapes.size()-1);
					}

				} // n번째 점이면
			}
			else if(function == ERASER) {
				if(shapes.isEmpty()) {
					newShape.shape = ERASER;
					shapes.add(newShape);
				} // 첫 도형이면
				else if(shapes.get(shapes.size()-1).shape != ERASER) {
					newShape.shape = ERASER;
					shapes.add(newShape);
				} // Polyline의 첫번째 점이면
				else {
					if(shapes.get(shapes.size()-1).doing.isEmpty()) {
						newShape.shape = ERASER;
						shapes.add(newShape);
					}
					else {
						newShape = shapes.get(shapes.size()-1);
					}
				} // n번째 점이면
				newShape.penColor = Color.white;
			}
			else if(function == SELECT) {
				initPoint = e.getPoint();
				if(move) {
					if(is_inBoundary(e.getPoint())) { // 영역 안이면
						for(int i=0; i<select.size(); i++) {
							drag.doing.add(e.getPoint()); // 이동 위치 저장
						}
					}
					else { // 영역 밖이면 (도형 다시 선택)
						select.clear();
						if(!shapes.peek().clear.isEmpty()) shapes.pop();
						
//						System.out.println("size " + shapes.size());
						drag.shape = DRAG;
						drag.start = e.getPoint();
						drag.end = e.getPoint();
						
						newShape.shape = DRAG;
						shapes.add(newShape);
//						System.out.println("select.size(): " + select.size());
						move=false;
					}
				}
				else {
					// 영역 안의 도형들 선택
					drag.shape = DRAG;
					drag.start = e.getPoint();
					drag.end = e.getPoint();
					
					newShape.shape = DRAG;
					shapes.add(newShape);
				}
			}
			else if(function == PASTE) {
//				System.out.println("3: " + copied.size());
				if(copied.isEmpty()) return;
				
				function = DEFAULT;
				mode.setText("DEFAULT");
				canvas.repaint();
				drag.doing.clear();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			four=0;
			
			super.mouseReleased(e);

//			System.out.println("Mouse released");

			if(function==LINE) {
				shapes.get(shapes.size()-1).end = e.getPoint();
				canvas.repaint();

			}
			else if(function==RECT || function == CIRCLE){
				shapes.get(shapes.size()-1).start.x = Math.min(initPoint.x, e.getPoint().x);
				shapes.get(shapes.size()-1).start.y = Math.min(initPoint.y, e.getPoint().y);

				shapes.get(shapes.size()-1).end.x = Math.max(initPoint.x, e.getPoint().x);
				shapes.get(shapes.size()-1).end.y = Math.max(initPoint.y, e.getPoint().y);

				shapes.get(shapes.size()-1).width = Math.abs(shapes.get(shapes.size()-1).start.x - shapes.get(shapes.size()-1).end.x);
				shapes.get(shapes.size()-1).height = Math.abs(shapes.get(shapes.size()-1).start.y - shapes.get(shapes.size()-1).end.y);

				canvas.repaint();
			}
			else if(function == POLYLINE) {
				shapes.get(shapes.size()-1).doing.add(e.getPoint());
				canvas.repaint();
				
			}
			else if(function == PEN) {
				shapes.get(shapes.size()-1).doing.add(e.getPoint());
				arrayListToArray(shapes.get(shapes.size()-1));
				shapes.get(shapes.size()-1).doing.clear();
				canvas.repaint();
				
			}
			else if(function == ERASER) {
				shapes.get(shapes.size()-1).doing.add(e.getPoint());
				arrayListToArray(shapes.get(shapes.size()-1));
				shapes.get(shapes.size()-1).doing.clear();
				canvas.repaint();
				
			}
			else if(function == SELECT) {
				if(move) { // 도형 이동시키기
					for(int i=0; i<select.size(); i++) {
//						System.out.println("Select2: ");
						moveShape(shapes.get(select.get(i)), e.getPoint());
					}
					shapes.get(shapes.size()-1).dif_x = e.getPoint().x-initPoint.x;
					shapes.get(shapes.size()-1).dif_y = e.getPoint().y-initPoint.y;
					select.clear();
					move = false;
				}
				else { // 이동시킬 도형 저장
					select = selected();
					newShape.select = selected();
					if(!select.isEmpty()) move = true;
					else shapes.pop();
				}
				
				drag.doing.clear();
				drag.shape =0;
				canvas.repaint();
//				System.out.println("final select: " + select.size());
			}
		}

		
		@Override
		public void mouseDragged(MouseEvent e) {
			five=0;
			
			super.mouseDragged(e);

			if(function == LINE) {
				shapes.get(shapes.size()-1).end = e.getPoint();
				canvas.repaint();
			}
			else if(function == RECT || function == CIRCLE) {
				shapes.get(shapes.size()-1).start.x = Math.min(initPoint.x, e.getPoint().x);
				shapes.get(shapes.size()-1).start.y = Math.min(initPoint.y, e.getPoint().y);

				shapes.get(shapes.size()-1).end.x = Math.max(initPoint.x, e.getPoint().x);
				shapes.get(shapes.size()-1).end.y = Math.max(initPoint.y, e.getPoint().y);

				shapes.get(shapes.size()-1).width = Math.abs(shapes.get(shapes.size()-1).start.x - shapes.get(shapes.size()-1).end.x);
				shapes.get(shapes.size()-1).height = Math.abs(shapes.get(shapes.size()-1).start.y - shapes.get(shapes.size()-1).end.y);

//				System.out.println(initPoint.x + " " + initPoint.y + " " + e.getPoint().x + " " + e.getPoint().y);
//				System.out.println(shapes.get(shapes.size()-1).start.x + " " + shapes.get(shapes.size()-1).start.y + " " + shapes.get(shapes.size()-1).end.x + " " + shapes.get(shapes.size()-1).end.y);

				canvas.repaint();
			}
			else if(function == PEN) {
				shapes.get(shapes.size()-1).doing.add(e.getPoint());
				canvas.repaint();
			}
			else if(function == ERASER) {
				shapes.get(shapes.size()-1).doing.add(e.getPoint());
				canvas.repaint();
			}
			else if(function == SELECT) {
				if(move) { 
					drag.doing.add(e.getPoint()); // 이동하는 위치 저장
					for(int i=0; i<select.size(); i++) {
//						System.out.println("Select1: " + select.get(i));
						moveShape(shapes.get(select.get(i)), e.getPoint());
//						System.out.println("moved shape: " + shapes.get(select.get(i)).shape);
					}
				}
				else {
					// 선택될 영역을 보여줄 사각형
					drag.start.x = Math.min(initPoint.x, e.getPoint().x);
					drag.start.y = Math.min(initPoint.y, e.getPoint().y);
					
					drag.end.x = Math.max(initPoint.x, e.getPoint().x);
					drag.end.y = Math.max(initPoint.y, e.getPoint().y);
						
					drag.width = Math.abs(drag.start.x - drag.end.x);
					drag.height = Math.abs(drag.start.y - drag.end.y);
					
					
					// 선택된 영역 지정 (첫 선택 지정)
					select = selected();
					
//					System.out.println("select: " + select.size());
				}
				
				canvas.repaint();
			}
			
		}
		

		@Override
		public void mouseEntered(MouseEvent e) {
			super.mouseEntered(e);
			
			if(function == PASTE) {
				if(copied.isEmpty()) return;
				drag.doing.add(e.getPoint()); // 이동하는 위치 저장
				adjustLocation(copied, e.getPoint());
				for(int i=0; i<copied.size(); i++) {
					shapes.add(copied.get(i));
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			six=0;
			super.mouseEntered(e);
			
			
			if(function == PASTE) {
				drag.doing.add(e.getPoint()); // 이동하는 위치 저장
				
				for(int i=0; i<copied.size(); i++) {
//					System.out.println("Paste: ");
//					System.out.println("Copied: " + copied.size());
					moveShape(copied.get(i), e.getPoint());
				}
				
				canvas.repaint();
			}
		}

	}

	public class ChangeListen implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent e) {
			stroke = (int) slider.getValue();
		}

	}


	public void arrayListToArray(ShapeObject so) {
		if(so.doing.size()==0) return;
		so.x = new int[so.doing.size()];
		so.y = new int[so.doing.size()];
		for(int i=0; i<so.doing.size(); i++) {
			so.x[i] = so.doing.get(i).x;
			so.y[i] = so.doing.get(i).y;
		}
	}
	
	public ArrayList<Integer> selected() {
		ArrayList<Integer> a = new ArrayList<Integer>();
		
		// 선택된 영역의 x, y 좌표들
		int min_x = Math.min(drag.start.x, drag.end.x);
		int min_y = Math.min(drag.start.y, drag.end.y);
		int max_x = Math.max(drag.start.x, drag.end.x);
		int max_y = Math.max(drag.start.y, drag.end.y);
		
		
		for(int i=0; i<shapes.size(); i++) {
			ShapeObject so = shapes.get(i);
			if(so.shape == LINE) {
				int x1 = Math.min(so.start.x, so.end.x);
				int y1 = Math.min(so.start.y, so.end.y);
				int x2 = Math.max(so.start.x, so.end.x);
				int y2 = Math.max(so.start.y, so.end.y);
				
				if(x1>min_x && x2<max_x && y1>min_y && y2<max_y) {
					a.add(i);
				}
			}
			else if(so.shape == RECT || so.shape == CIRCLE) {
				int x1 = Math.min(so.start.x, so.end.x);
				int y1 = Math.min(so.start.y, so.end.y);
				int x2 = Math.max(so.start.x, so.end.x);
				int y2 = Math.max(so.start.y, so.end.y);
				
				if(x1>min_x && x2<max_x && y1>min_y && y2<max_y) {
					a.add(i);
				}
			}
			else if(so.shape == POLYLINE) {
				int x1 = 801;
				int y1 = 801;
				int x2 = 0;
				int y2 = 0;
				
				// polyline을 이루는 점들의 좌표 들로 이루어진 점들 중에 왼쪽 위와 오른쪽 아래의 점 구하기
				for(int j=0; j<so.x.length; j++) {
					if(x1>so.x[j]) x1 = so.x[j];
					else if(x2<so.x[j]) x2 = so.x[j];
					
					if(y1>so.y[j]) y1 = so.y[j];
					else if(y2<so.y[j]) y2 = so.y[j];
					
//					System.out.println("x, y: " + so.x[j] +"," + so.y[j] );
				}
//				System.out.println("x1, y1, x2, y2: " + x1 + "," + y1 + "," + x2 + "," + y2);
				
				if(x1>min_x && x2<max_x && y1>min_y && y2<max_y) {
					a.add(i);
				}
			}
			else if(so.shape == PEN) {
				int x1 = 801;
				int y1 = 801;
				int x2 = 0;
				int y2 = 0;
				
				// pen을 이루는 점들의 좌표 들로 이루어진 점들 중에 왼쪽 위와 오른쪽 아래의 점 구하기
				for(int j=0; j<so.x.length; j++) {
					if(x1>so.x[j]) x1 = so.x[j];
					else if(x2<so.x[j]) x2 = so.x[j];
					
					if(y1>so.y[j]) y1 = so.y[j];
					else if(y2<so.y[j]) y2 = so.y[j];
					
//					System.out.println("x, y: " + so.x[j] +"," + so.y[j] );
				}
//				System.out.println("x1, y1, x2, y2: " + x1 + "," + y1 + "," + x2 + "," + y2);
				
				if(x1>min_x && x2<max_x && y1>min_y && y2<max_y) {
					a.add(i);
				}
			}
			else if(so.shape == ERASER) {
				int x1 = 801;
				int y1 = 801;
				int x2 = 0;
				int y2 = 0;
				
				// pen을 이루는 점들의 좌표 들로 이루어진 점들 중에 왼쪽 위와 오른쪽 아래의 점 구하기
				for(int j=0; j<so.x.length; j++) {
					if(x1>so.x[j]) x1 = so.x[j];
					else if(x2<so.x[j]) x2 = so.x[j];
					
					if(y1>so.y[j]) y1 = so.y[j];
					else if(y2<so.y[j]) y2 = so.y[j];
					
//					System.out.println("x, y: " + so.x[j] +"," + so.y[j] );
				}
//				System.out.println("x1, y1, x2, y2: " + x1 + "," + y1 + "," + x2 + "," + y2);
				
				if(x1>min_x && x2<max_x && y1>min_y && y2<max_y) {
					a.add(i);
				}
			}
		}
		
		return a;
	}
	
	public boolean is_inBoundary(Point now) {
		int min_x = 800;
		int max_x = 0;
		int min_y = 600;
		int max_y = 0;
		
		for(int i=0; i<select.size(); i++) {
			ShapeObject so = shapes.get(select.get(i));
			
			// 가장 작은 x, y값과 가장 큰 x, y값 구하기
			if(so.shape == LINE) {
				min_x = (Math.min(so.start.x, so.end.x)<min_x) ? Math.min(so.start.x, so.end.x):min_x;
				min_y = (Math.min(so.start.y, so.end.y)<min_y) ? Math.min(so.start.y, so.end.y):min_y;
				max_x = (Math.max(so.start.x, so.end.x)>max_x) ? Math.max(so.start.x, so.end.x):max_x;
				max_y = (Math.max(so.start.y, so.end.y)>max_y) ? Math.max(so.start.y, so.end.y):max_y;
			}
			else if(so.shape == RECT || so.shape == CIRCLE) {
				min_x = (so.start.x<min_x) ? so.start.x:min_x;
				min_y = (so.start.y<min_y) ? so.start.y:min_y;
				max_x = (so.end.x>max_x) ? so.end.x:max_x;
				max_y = (so.end.y>max_y) ? so.end.y:max_y;
			}
			else if(so.shape == POLYLINE) {
				// polyline을 이루는 점들의 좌표 들로 이루어진 점들 중에 왼쪽 위와 오른쪽 아래의 점 구하기
				for(int j=0; j<so.x.length; j++) {
					if(min_x>so.x[j]) min_x = so.x[j];
					else if(max_x<so.x[j]) max_x = so.x[j];
					
					if(min_y>so.y[j]) min_y = so.y[j];
					else if(max_y<so.y[j]) max_y = so.y[j];
					
				}
			}
			else if(so.shape == PEN) {
				for(int j=0; j<so.x.length; j++) {
					if(min_x>so.x[j]) min_x = so.x[j];
					else if(max_x<so.x[j]) max_x = so.x[j];
					
					if(min_y>so.y[j]) min_y = so.y[j];
					else if(max_y<so.y[j]) max_y = so.y[j];
					
				}
			}
		}
		
		if(now.x>min_x && now.x<max_x && now.y>min_y && now.y<max_y) {
			return true;
		}
		return false;
	}
	
	public void moveShape(ShapeObject so, Point now) { // so: 이동해야할 shape, now: 현재 위치
		int x_dif = now.x - drag.doing.get(drag.doing.size()-2).x;
		int y_dif = now.y - drag.doing.get(drag.doing.size()-2).y;
		
		
		if(so.shape == LINE) {
			so.start.x += x_dif;
			so.start.y += y_dif;
			so.end.x += x_dif;
			so.end.y += y_dif;
			
//			so.end.y += now.y - initPoint.y;
//			System.out.println("now.x: " + now.x + " now.y: " + now.y);
//			System.out.println("initPoint.x: " + initPoint.x + " initPoint.y: " + initPoint.y);
//			System.out.println("x_dif: " + x_dif + " y_dif: " + y_dif);
//			System.out.println("so.start.x: " + so.start.x + " so.start.y: " + so.start.y + " so.end.x: " + so.end.x + " so.end.y: " + so.end.y);
		}
		else if(so.shape == RECT || so.shape == CIRCLE) {
			so.start.x += x_dif;
			so.start.y += y_dif;
			so.end.x += x_dif;
			so.end.y += y_dif;
//			System.out.println("x_dif: " + x_dif + " y_dif: " + y_dif);
//			System.out.println("Rect start, end: " + so.start.x + " " + so.start.y + " " + so.end.x + " " + so.end.y);
		}
		else if(so.shape == POLYLINE) {
			for(int j=0; j<so.x.length; j++) {
//				System.out.println("x[" + j + "]: " + so.x[j]);
				so.x[j] += x_dif;
				so.y[j] += y_dif;
			}
			
		}
		else if(so.shape == PEN) {
			for(int j=0; j<so.x.length; j++) {
				so.x[j] += x_dif;
				so.y[j] += y_dif;
			}
		}
		else if(so.shape == ERASER) {
			for(int j=0; j<so.x.length; j++) {
				so.x[j] += x_dif;
				so.y[j] += y_dif;
			}
		}
	}
	
	private void adjustLocation(ArrayList<ShapeObject> relocate, Point mouse) {
		
		Point diff;
		
		Point p = findPoint(relocate.get(0));
		
		diff = new Point(mouse.x-p.x, mouse.y-p.y);
		
		for(int i=0; i<relocate.size(); i++) {
			ShapeObject so = relocate.get(i);
			
			if(so.shape == LINE) {
				so.start.x += diff.x;
				so.start.y += diff.y;
				so.end.x += diff.x;
				so.end.y += diff.y;
				
//				so.end.y += now.y - initPoint.y;
//				System.out.println("now.x: " + now.x + " now.y: " + now.y);
//				System.out.println("initPoint.x: " + initPoint.x + " initPoint.y: " + initPoint.y);
//				System.out.println("diff.x: " + diff.x + " diff.y: " + diff.y);
//				System.out.println("so.start.x: " + so.start.x + " so.start.y: " + so.start.y + " so.end.x: " + so.end.x + " so.end.y: " + so.end.y);
			}
			else if(so.shape == RECT || so.shape == CIRCLE) {
				so.start.x += diff.x;
				so.start.y += diff.y;
				so.end.x += diff.x;
				so.end.y += diff.y;
//				System.out.println("diff.x: " + diff.x + " diff.y: " + diff.y);
//				System.out.println("Rect start, end: " + so.start.x + " " + so.start.y + " " + so.end.x + " " + so.end.y);
			}
			else if(so.shape == POLYLINE) {
				for(int j=0; j<so.x.length; j++) {
//					System.out.println("x[" + j + "]: " + so.x[j]);
					so.x[j] += diff.x;
					so.y[j] += diff.y;
				}
				
			}
			else if(so.shape == PEN) {
				for(int j=0; j<so.x.length; j++) {
					so.x[j] += diff.x;
					so.y[j] += diff.y;
				}
			}
			
		}
		
	}

	private Point findPoint(ShapeObject so) {
		
		int min_x = 800;
		int min_y = 600;
		
		// 가장 작은 x, y값과 가장 큰 x, y값 구하기
		if(so.shape == LINE) {
			min_x = (Math.min(so.start.x, so.end.x)<min_x) ? Math.min(so.start.x, so.end.x):min_x;
			min_y = (Math.min(so.start.y, so.end.y)<min_y) ? Math.min(so.start.y, so.end.y):min_y;
		}
		else if(so.shape == RECT || so.shape == CIRCLE) {
			min_x = (so.start.x<min_x) ? so.start.x:min_x;
			min_y = (so.start.y<min_y) ? so.start.y:min_y;
		}
		else if(so.shape == POLYLINE) {
			// polyline을 이루는 점들의 좌표 들로 이루어진 점들 중에 왼쪽 위와 오른쪽 아래의 점 구하기
			for(int j=0; j<so.x.length; j++) {
				if(min_x>so.x[j]) min_x = so.x[j];

				if(min_y>so.y[j]) min_y = so.y[j];

			}
		}
		else if(so.shape == PEN) {
			for(int j=0; j<so.x.length; j++) {
				if(min_x>so.x[j]) min_x = so.x[j];

				if(min_y>so.y[j]) min_y = so.y[j];

			}
		}
		
		return new Point(min_x, min_y);
	}
	
	private void relocate(ShapeObject so, int dif_x, int dif_y) { 
		
		if(so.shape == LINE) {
			so.start.x += dif_x;
			so.start.y += dif_y;
			so.end.x += dif_x;
			so.end.y += dif_y;
			
//			so.end.y += now.y - initPoint.y;
//			System.out.println("now.x: " + now.x + " now.y: " + now.y);
//			System.out.println("initPoint.x: " + initPoint.x + " initPoint.y: " + initPoint.y);
//			System.out.println("dif_x: " + dif_x + " dif_y: " + dif_y);
//			System.out.println("so.start.x: " + so.start.x + " so.start.y: " + so.start.y + " so.end.x: " + so.end.x + " so.end.y: " + so.end.y);
		}
		else if(so.shape == RECT || so.shape == CIRCLE) {
			so.start.x += dif_x;
			so.start.y += dif_y;
			so.end.x += dif_x;
			so.end.y += dif_y;
//			System.out.println("dif_x: " + dif_x + " dif_y: " + dif_y);
//			System.out.println("Rect start, end: " + so.start.x + " " + so.start.y + " " + so.end.x + " " + so.end.y);
		}
		else if(so.shape == POLYLINE) {
			for(int j=0; j<so.x.length; j++) {
//				System.out.println("x[" + j + "]: " + so.x[j]);
				so.x[j] += dif_x;
				so.y[j] += dif_y;
			}
			
		}
		else if(so.shape == PEN) {
			for(int j=0; j<so.x.length; j++) {
				so.x[j] += dif_x;
				so.y[j] += dif_y;
			}
		}
	}
}









