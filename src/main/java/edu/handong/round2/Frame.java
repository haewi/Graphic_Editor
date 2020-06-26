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
	
	// 현재까지 그려진 도형들
	ArrayList<ShapeObject> shapes = new ArrayList<ShapeObject>();
	
	Stack<ShapeObject> shapeStack = new Stack<ShapeObject>();
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
		
		canvas = new Canvas();

		// Toolbar 기본 설정
		toolbar = new JToolBar();
		toolbar.setBounds(0, 0, 800, 80);
//		toolbar.setBounds(700, 0, 100, 600);
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
//		toolbar.setLayout(new FlowLayout());
		toolbar.setBackground(new Color(218, 207, 251));

		mainFrame.add(toolbar);

		// toolbar용 버튼 만들기 
		ArrayList<Button> toolButton = new ArrayList<Button>();
		String[] toolbarLabels = new String[] {"Line", "Rect", "Circle", "Polyline", "Color"};
		ButtonListen listener = new ButtonListen();
		for(int i=0; i<toolbarLabels.length; i++) {
			Button tempBtn;
			tempBtn = new Button(toolbarLabels[i]);
			tempBtn.setSize(80, 60);
			tempBtn.setLocation(10+i*80, 10);
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
		slider = new JSlider(0, 40, 3);
		slider.addChangeListener(new ChangeListen());
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		slider.setBounds(10+80*5+30, 10, 100, 60);
//		slider.setBounds(0, 5+50*5+20, 100, 60);
		stroke = (int) slider.getValue();
		
		Label label = new Label("굵기");
		label.setFont(new Font("나눔손글씨 펜", Font.PLAIN, 15));
		label.setBounds(10+80*5, 20, 30, 30);
//		label.setBounds(40, 8+50*5+10, 80, 30);
		toolbar.add(label);
		toolbar.add(slider);
		
		// canvas
		canvas.addMouseListener(new MouseListen());
		canvas.addMouseMotionListener(new MouseListen());
		canvas.setBounds(0, 100, 800, 560);
//		canvas.setBounds(0, 0, 700, 600);
		canvas.setBackground(Color.white);
		mainFrame.add(canvas);
	}

	public class Canvas extends JPanel{

		// repaint() 실행시 실행됨
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g;


			for(ShapeObject so: shapes) {
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
					if(so.doing.isEmpty()) {
						g2.drawPolyline(so.x, so.y, so.x.length);
					} // polyline이 완성되었을 때
					else {
						
						int[] x = new int[so.doing.size()];
						int[] y = new int[so.doing.size()];
						
						for(int i=0; i<so.doing.size(); i++) {
							x[i] = so.doing.get(i).x;
							y[i] = so.doing.get(i).y;
						}
						
						g2.drawPolyline(x, y, x.length);
						
					} // polyline이 완성되기 전 
				}
			}
		}

	}

	public class ButtonListen implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String str = e.getActionCommand();
			
			if(function==DEFAULT) {
				for(ShapeObject s: shapes) {
					if(s.shape == POLYLINE) {
						arrayListToArray(s);
						s.doing.clear();
					}
				}
			}
			
			if(str.equals("Line")) {
				if(function==LINE) {
					function = DEFAULT;
				}
				else function = LINE;
				
			}
			else if(str.equals("Rect")) {
				if(function==RECT) {
					function = DEFAULT;
				}
				else function = RECT;
				
			}
			else if(str.equals("Circle")) {
				if(function==CIRCLE) {
					function = DEFAULT;
				}
				else function = CIRCLE;
			}
			else if(str.equals("Polyline")) {
				if(function==POLYLINE) {
					function = DEFAULT;
				}
				else {
					function = POLYLINE;
				}
			}
			else if(str.equals("Color")) {
				color = JColorChooser.showDialog(null, "Choose Color", Color.black);
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
			
//			System.out.println("Mouse pressed");
			
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
				} // 첫 도형이면
				else if(shapes.get(shapes.size()-1).shape != POLYLINE) {
					newShape.shape = POLYLINE;
				} // Polyline의 첫번째 점이면
				else {
					if(shapes.get(shapes.size()-1).doing.isEmpty()) {
						newShape.shape = POLYLINE;
					}
					else {
						newShape = shapes.get(shapes.size()-1);
					}
					
				} // n번째 점이면
			}
			newShape.stroke = stroke;
			newShape.penColor = color;
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
				
				newShape.doing.add(e.getPoint());
				
				shapes.add(newShape);
				canvas.repaint();
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
			else if(function == POLYLINE) {

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


}










