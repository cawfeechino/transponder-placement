package draw;

import com.sun.javafx.geom.Point2D;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Ring8 {

	public void drawRing8(BorderPane layout) {
		Pane draw= new Pane();
		Point2D mPoint1 = new Point2D(150,150);
		Point2D mPoint2 = new Point2D(250,50);
		Point2D mPoint3 = new Point2D(400,50);
		Point2D mPoint4 = new Point2D(500,150);
		Point2D mPoint5 = new Point2D(500,250);
		Point2D mPoint6 = new Point2D(400,350);
		Point2D mPoint7 = new Point2D(250,350);
		Point2D mPoint8 = new Point2D(150,250);
		
		Circle point1 = new Circle(mPoint1.x,mPoint1.y,20);
		point1.setFill(Color.TRANSPARENT);
		point1.setStroke(Color.BLACK);
		Circle point2 = new Circle(mPoint2.x,mPoint2.y,20);
		point2.setFill(Color.TRANSPARENT);
		point2.setStroke(Color.BLACK);
		Circle point3 = new Circle(mPoint3.x,mPoint3.y,20);
		point3.setFill(Color.TRANSPARENT);
		point3.setStroke(Color.BLACK);
		Circle point4 = new Circle(mPoint4.x,mPoint4.y,20);
		point4.setFill(Color.TRANSPARENT);
		point4.setStroke(Color.BLACK);
		Circle point5 = new Circle(mPoint5.x,mPoint5.y,20);
		point5.setFill(Color.TRANSPARENT);
		point5.setStroke(Color.BLACK);
		Circle point6 = new Circle(mPoint6.x,mPoint6.y,20);
		point6.setFill(Color.TRANSPARENT);
		point6.setStroke(Color.BLACK);
		Circle point7 = new Circle(mPoint7.x,mPoint7.y,20);
		point7.setFill(Color.TRANSPARENT);
		point7.setStroke(Color.BLACK);
		Circle point8 = new Circle(mPoint8.x,mPoint8.y,20);
		point8.setFill(Color.TRANSPARENT);
		point8.setStroke(Color.BLACK);
		
		Label label1 = new Label("1");
	    label1.setTranslateX(mPoint1.x);
	    label1.setTranslateY(mPoint1.y);
		Label label2 = new Label("2");
	    label2.setTranslateX(mPoint2.x);
	    label2.setTranslateY(mPoint2.y);
		Label label3 = new Label("3");
	    label3.setTranslateX(mPoint3.x);
	    label3.setTranslateY(mPoint3.y);
		Label label4 = new Label("4");
	    label4.setTranslateX(mPoint4.x);
	    label4.setTranslateY(mPoint4.y);
		//1st node 
		Line line1to2 = new Line(mPoint1.x,mPoint1.y,mPoint2.x,mPoint2.y);
		
		//second node
		Line line2to3= new Line(mPoint2.x,mPoint2.y, mPoint3.x,mPoint3.y);
		

		//3rd node
		Line line3to4 = new Line(mPoint3.x, mPoint3.y, mPoint4.x, mPoint4.y);

		//4th
		Line line4to5 = new Line(mPoint4.x,mPoint4.y,mPoint5.x,mPoint5.y);

		
		//5th
		Line line5to6 = new Line(mPoint5.x, mPoint5.y, mPoint6.x, mPoint6.y);
		
		//6th
		Line line6to7 = new Line(mPoint6.x, mPoint6.y, mPoint7.x, mPoint7.y);
		
		//7th
		Line line7to8 = new Line(mPoint7.x, mPoint7.y, mPoint8.x, mPoint8.y);
		
		//8th
		Line line8to1 = new Line(mPoint8.x, mPoint8.y, mPoint1.x, mPoint1.y);
		
		
		draw.getChildren().addAll(point1,point2,point3,point4,point5,point6,point7,point8);	
		draw.getChildren().add(line1to2);
		draw.getChildren().add(line2to3);
		draw.getChildren().add(line3to4);
		draw.getChildren().add(line4to5);
		draw.getChildren().add(line5to6);
		draw.getChildren().add(line6to7);
		draw.getChildren().add(line7to8);
		draw.getChildren().add(line8to1);
		draw.getChildren().addAll(label1, label2, label3, label4);
		layout.setCenter(draw);
				
	}
	
}
