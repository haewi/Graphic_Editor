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

	// 선택한 도구
	public int function = DEFAULT;

	// 현재 굵기와 그 spinner
	int stroke;
	JSlider slider;

	// 현재 색깔
	Color color;

	// 처음으로 선택된 지점
	Point initPoint;

	// 기본 세팅
	JFrame mainFrame;
	JToolBar toolbar;
	Canvas canvas;
	Label mode = new Label("DEFAULT");

	// 현재까지 그려진 도형들
	Stack<ShapeObject> shapes = new Stack<ShapeObject>();
	Stack<ShapeObject> deleteShape = new Stack<ShapeObject>();

	public Frame() {
		mainFrame = new JFrame("그림판");
		mainFrame.setBounds(200, 200, 800, 600);
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
		canvas.setBounds(0, 100, 800, 560);
//		canvas.setBounds(0, 0, 700, 600);
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
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g;


			for(int j=0; j<shapes.size(); j++) {
				ShapeObject so = shapes.get(j);
				g2.setColor(so.penColor);
				g2.setStroke(new BasicStroke(so.stroke));
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
			}
		}

	}

	public class ButtonListen implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
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
			super.mousePressed(e);

			newShape = new ShapeObject();
			initPoint = new Point();
			
			newShape.stroke = stroke;
			newShape.penColor = color;

//			System.out.println("Mouse pressed");

			if(function == LINE) {
				newShape.shape = LINE;
				newShape.start = e.getPoint();
				shapes.add(newShape);
				deleteShape.clear();
			}
			else if(function == RECT) {
				newShape.shape = RECT;
				newShape.start = e.getPoint();
				newShape.end = e.getPoint();
				initPoint = e.getPoint();
				shapes.add(newShape);
				deleteShape.clear();
			}
			else if(function == CIRCLE) {
				newShape.shape = CIRCLE;
				newShape.start = e.getPoint();
				newShape.end = e.getPoint();
				initPoint = e.getPoint();
				shapes.add(newShape);
				deleteShape.clear();
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
				deleteShape.clear();
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
				deleteShape.clear();
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
				deleteShape.clear();
				newShape.penColor = Color.white;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
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
				for(int i=0; i<shapes.size(); i++) {
					ShapeObject so = shapes.get(i);
					if(isClicked(so, e.getPoint())) {
						newShape = so;
					} // 가장 나중에 만든 shape들 중에서 현재 마우스가 클릭한 장소가 자신의 영역인 것
				}
				System.out.println("select: " + newShape.shape);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {

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
	
	public boolean isClicked(ShapeObject so, Point p) {
		if(so.shape == LINE) {
			int min_x = Math.min(so.start.x, so.end.x);
			int min_y = Math.min(so.start.y, so.end.y);
			int max_x = Math.max(so.start.x, so.end.x);
			int max_y = Math.max(so.start.y, so.end.y);
			
			if(p.x > min_x && p.x < max_x && p.y > min_y && p.y < max_y) {
				return true;
			}
		}
		else if(so.shape == RECT) {
			int min_x = Math.min(so.start.x, so.end.x);
			int min_y = Math.min(so.start.y, so.end.y);
			int max_x = Math.max(so.start.x, so.end.x);
			int max_y = Math.max(so.start.y, so.end.y);
			
			if(p.x > min_x && p.x < max_x && p.y > min_y && p.y < max_y) {
				return true;
			}
		}
		else if(so.shape == CIRCLE) {
			int min_x = Math.min(so.start.x, so.end.x);
			int min_y = Math.min(so.start.y, so.end.y);
			int max_x = Math.max(so.start.x, so.end.x);
			int max_y = Math.max(so.start.y, so.end.y);
			
			if(p.x > min_x && p.x < max_x && p.y > min_y && p.y < max_y) {
				return true;
			}
		}
		else if(so.shape == POLYLINE) {
			int min_x = 0;
			int min_y = 0;
			int max_x = 800;
			int max_y = 600;
			
//			System.out.println("x.length: " + so.doing.size());
			
			for(int i=0; i<so.x.length; i++) {
				if(min_x > so.x[i]) min_x = so.x[i];
				if(min_y > so.y[i]) min_y = so.y[i];
				if(max_x < so.x[i]) max_x = so.x[i];
				if(max_y < so.y[i]) max_y = so.y[i];
			}
			
			if(p.x > min_x && p.x < max_x && p.y > min_y && p.y < max_y) {
				return true;
			}
		}
		else if(so.shape == PEN) {
			
		}
		
		return false;
	}

}










