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

	// 선택한 기능
	public int function = DEFAULT;

	// 현재 굵기와 그 spinner
	int stroke;
	JSlider slider;

	// 현재 색깔
	Color color = Color.black;

	// 처음으로 선택된 지점, 마지막으로 선택된 지점
	Point initPoint; //, lastPoint;

	// 기본 세팅
	JFrame mainFrame;
	JToolBar toolbar;
	Canvas canvas;
	Label mode = new Label("DEFAULT");

	// 현재까지 그려진 도형들
	Stack<ShapeObject> shapes = new Stack<ShapeObject>();
	Stack<ShapeObject> deleteShape = new Stack<ShapeObject>();
	
	// 선택된 도형들의 index, select
	ArrayList<Integer> select;
	boolean move=false;
	

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
		toolbarLabels = new String[] {"Pen", "Color", "Eraser", "Select"};
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
				g2.setColor(so.penColor);
				g2.setStroke(new BasicStroke(so.stroke));
//				g2.setStroke(new BasicStroke(so.stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {9}, 3));
				g2.setColor(so.penColor);

				if(so.shape==LINE) {
					g2.drawLine(so.start.x, so.start.y, so.end.x, so.end.y);
				}
				else if(so.shape==RECT) {

					g2.drawRect(so.start.x, so.start.y, so.width, so.height);

				}
				else if(so.shape == CIRCLE) {
					g2.drawOval(so.start.x, so.start.y, so.width, so.height);
				}
				else if(so.shape == POLYLINE) {

//					System.out.println("doing size: " + so.doing.size());
					
					if(so.doing.isEmpty()) {
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
				else if(function==SELECT) {
					g2.setColor(Color.LIGHT_GRAY);
					g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {9}, 3));
					g2.drawRect(Math.min(so.start.x, so.end.x), Math.min(so.start.y, so.end.y),Math.abs(so.start.x-so.end.x), Math.abs(so.start.y-so.end.y));
				}
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
				if(deleteShape.get(deleteShape.size()-1).shape == CLEAR) {
					int size = deleteShape.get(deleteShape.size()-1).clear.size();
					for(int i=size-1; i>=0; i--) {
						shapes.add(deleteShape.get(deleteShape.size()-1).clear.get(i));
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
				shapes.add(deleteShape.pop());
				canvas.repaint();
			}
			else if(str.equals("Clear")) {
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
			
			if(function != POLYLINE) {
				for(ShapeObject s: shapes) {
					if(s.shape == POLYLINE) {
						arrayListToArray(s);
						s.doing.clear();
					}
				}
			}
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
			deleteShape.clear();
			
			if(function == LINE) {
				newShape.shape = LINE;
				newShape.start = e.getPoint();
				shapes.add(newShape);
//				deleteShape.clear();
			}
			else if(function == RECT) {
				newShape.shape = RECT;
				newShape.start = e.getPoint();
				newShape.end = e.getPoint();
				initPoint = e.getPoint();
				shapes.add(newShape);
//				deleteShape.clear();
			}
			else if(function == CIRCLE) {
				newShape.shape = CIRCLE;
				newShape.start = e.getPoint();
				newShape.end = e.getPoint();
				initPoint = e.getPoint();
				shapes.add(newShape);
//				deleteShape.clear();
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
//				deleteShape.clear();
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
//				deleteShape.clear();
				newShape.penColor = Color.white;
			}
			else if(function == SELECT) {
				initPoint = e.getPoint();
				if(move) {
					if(is_inBoundary(e.getPoint())) { // 영역 안이면
						for(int i=0; i<select.size(); i++) {
							shapes.get(shapes.size()-1).doing.add(e.getPoint()); // 이동 위치 저장
						}
					}
					else { // 영역 밖이면 (도형 다시 선택)
						select.clear();
						newShape.shape = SELECT;
						newShape.start = e.getPoint();
						newShape.end = e.getPoint();
						shapes.add(newShape);
//						System.out.println("select.size(): " + select.size());
						move=false;
					}
				}
				else {
					// 영역 안의 도형들 선택
					newShape.shape = SELECT;
					newShape.start = e.getPoint();
					newShape.end = e.getPoint();
					shapes.add(newShape);
				}
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
			else if(function==RECT){
				shapes.get(shapes.size()-1).start.x = Math.min(initPoint.x, e.getPoint().x);
				shapes.get(shapes.size()-1).start.y = Math.min(initPoint.y, e.getPoint().y);

				shapes.get(shapes.size()-1).end.x = Math.max(initPoint.x, e.getPoint().x);
				shapes.get(shapes.size()-1).end.y = Math.max(initPoint.y, e.getPoint().y);

				shapes.get(shapes.size()-1).width = Math.abs(shapes.get(shapes.size()-1).start.x - shapes.get(shapes.size()-1).end.x);
				shapes.get(shapes.size()-1).height = Math.abs(shapes.get(shapes.size()-1).start.y - shapes.get(shapes.size()-1).end.y);

				canvas.repaint();
			}
			else if(function == CIRCLE) {
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
						moveShape(shapes.get(select.get(i)), e.getPoint());
					}
					select.clear();
					move = false;
				}
				else { // 이동시킬 도형 저장
					select = selected();
					if(!select.isEmpty()) move = true;
					shapes.pop();
				}
				
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
			else if(function == RECT) {
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
			else if(function == CIRCLE) {

				shapes.get(shapes.size()-1).start.x = Math.min(initPoint.x, e.getPoint().x);
				shapes.get(shapes.size()-1).start.y = Math.min(initPoint.y, e.getPoint().y);

				shapes.get(shapes.size()-1).end.x = Math.max(initPoint.x, e.getPoint().x);
				shapes.get(shapes.size()-1).end.y = Math.max(initPoint.y, e.getPoint().y);

				shapes.get(shapes.size()-1).width = Math.abs(shapes.get(shapes.size()-1).start.x - shapes.get(shapes.size()-1).end.x);
				shapes.get(shapes.size()-1).height = Math.abs(shapes.get(shapes.size()-1).start.y - shapes.get(shapes.size()-1).end.y);

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
					for(int i=0; i<select.size(); i++) {
						shapes.get(shapes.size()-1).doing.add(e.getPoint()); // 이동하는 위치 저장
						moveShape(shapes.get(select.get(i)), e.getPoint());
//						System.out.println("moved shape: " + shapes.get(select.get(i)).shape);
					}
				}
				else {
					// 선택될 영역을 보여줄 사각형
					shapes.get(shapes.size()-1).start.x = Math.min(initPoint.x, e.getPoint().x);
					shapes.get(shapes.size()-1).start.y = Math.min(initPoint.y, e.getPoint().y);
					
					shapes.get(shapes.size()-1).end.x = Math.max(initPoint.x, e.getPoint().x);
					shapes.get(shapes.size()-1).end.y = Math.max(initPoint.y, e.getPoint().y);
						
					shapes.get(shapes.size()-1).width = Math.abs(shapes.get(shapes.size()-1).start.x - shapes.get(shapes.size()-1).end.x);
					shapes.get(shapes.size()-1).height = Math.abs(shapes.get(shapes.size()-1).start.y - shapes.get(shapes.size()-1).end.y);
					
					
					// 선택된 영역 지정 (첫 선택 지정)
					select = selected();
					
					System.out.println("select: " + select.size());
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
		ShapeObject so = shapes.get(shapes.size()-1);
		
		// 선택된 영역의 x, y 좌표들
		int min_x = Math.min(so.start.x, so.end.x);
		int min_y = Math.min(so.start.y, so.end.y);
		int max_x = Math.max(so.start.x, so.end.x);
		int max_y = Math.max(so.start.y, so.end.y);
		
		
		for(int i=0; i<shapes.size()-1; i++) {
			so = shapes.get(i);
			if(so.shape == LINE) {
				int x1 = Math.min(so.start.x, so.end.x);
				int y1 = Math.min(so.start.y, so.end.y);
				int x2 = Math.max(so.start.x, so.end.x);
				int y2 = Math.max(so.start.y, so.end.y);
				
				if(x1>min_x && x2<max_x && y1>min_y && y2<max_y) {
					a.add(i);
				}
			}
			else if(so.shape == RECT) {
				int x1 = Math.min(so.start.x, so.end.x);
				int y1 = Math.min(so.start.y, so.end.y);
				int x2 = Math.max(so.start.x, so.end.x);
				int y2 = Math.max(so.start.y, so.end.y);
				
				if(x1>min_x && x2<max_x && y1>min_y && y2<max_y) {
					a.add(i);
				}
			}
			else if(so.shape == CIRCLE) {
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
			else if(so.shape == RECT) {
				min_x = (so.start.x<min_x) ? so.start.x:min_x;
				min_y = (so.start.y<min_y) ? so.start.y:min_y;
				max_x = (so.end.x>max_x) ? so.end.x:max_x;
				max_y = (so.end.y>max_y) ? so.end.y:max_y;
			}
		}
		
		if(now.x>min_x && now.x<max_x && now.y>min_y && now.y<max_y) {
			return true;
		}
		
		
		return false;
	}
	
	public void moveShape(ShapeObject so, Point now) { // so: 이동해야할 shape, now: 현재 위치
		ShapeObject s = shapes.get(shapes.size()-1); // 그간 이동했던 위치 저장된 object
		int x_dif = now.x - s.doing.get(s.doing.size()-2).x;
		int y_dif = now.y - s.doing.get(s.doing.size()-2).y;
		
		if(so.shape == LINE) {

			so.start.x += x_dif;
			so.start.y += y_dif;
			so.end.x += x_dif;
			so.end.y += y_dif;
			
//			so.start.x += now.x - initPoint.x;
//			so.start.y += now.y - initPoint.y;
//			so.end.x += now.x - initPoint.x;
//			so.end.y += now.y - initPoint.y;
//			System.out.println("now.x: " + now.x + " now.y: " + now.y);
//			System.out.println("initPoint.x: " + initPoint.x + " initPoint.y: " + initPoint.y);
//			System.out.println("x_dif: " + x_dif + " y_dif: " + y_dif);
//			System.out.println("so.start.x: " + so.start.x + " so.start.y: " + so.start.y + " so.end.x: " + so.end.x + " so.end.y: " + so.end.y);
		}
		else if(so.shape == RECT) {
			
			so.start.x += x_dif;
			so.start.y += y_dif;
			so.end.x += x_dif;
			so.end.y += y_dif;
			System.out.println("moved shape: " + so.shape);
		}
//		System.out.println("moved shape: " + so.shape);
	}
}









