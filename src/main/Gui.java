package main;


import java.util.ArrayList;

import com.sun.javafx.geom.Point2D;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Gui extends Application{

	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//root is for the spinner when loading in the background
		StackPane root = new StackPane();
		Scene scene = new Scene(root,800,600);	
		
		ScrollPane scrollPane = new ScrollPane();
		BorderPane borderPane = new BorderPane(scrollPane);
		
		root.getChildren().add(borderPane);
		
		TabPane tabs = new TabPane();
		Tab network = new Tab("Network");
		network.setClosable(false);
		//change
		Tab analyzer = new Tab("Algorithim");
		analyzer.setClosable(false);
		
		tabs.getTabs().addAll(network,analyzer);
		borderPane.setTop(tabs);
			
		//main body will be switching content when different tabs are clicked
		BorderPane body1 = new BorderPane();
		//body1.setPrefWidth(scene.getWidth());
		scrollPane.setContent(body1);
	
		VBox top = new VBox();
		
		HBox selector = new HBox();
		selector.setPadding(new Insets(15));
		Label topolgyLabel = new Label("Name of Topolgy: ");
		ComboBox<String> topology = new ComboBox<>();
		topology.setPromptText("Select Topology");
		topology.getItems().addAll("Ring 8", "Mesh 8", "HyperCube 8", "HyperCube 16", "NsfNet");
		topology.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				switch(newValue) {
				case"Ring 8":
					drawRing8(body1);
					break;
				case "Mesh 8":
					drawMesh8(body1);
					break;
				
				case "HyperCube 8":
					drawHyperCube8(body1);
					break;
					
				case "HyperCube 16":
					break;
					
				case "NsfNet":
					drawNsfNet(body1);
					break;
				}
			}
		});
		  
		selector.getChildren().addAll(topolgyLabel,topology);
		selector.setAlignment(Pos.TOP_CENTER);
		
		Line border = new Line(20,0,750,0);
		top.getChildren().addAll(selector,border);
		top.setPadding(new Insets(15));
		body1.setTop(top);		
		
		
		
		
		//side nav with closable button
				
		VBox sideNav = new VBox();
			VBox topologyNav = new VBox();
				HBox topologyForm = new HBox();
					Label lTopology = new Label("Topology");
					Button bTopology = new Button("-");
					topologyForm.getChildren().addAll(lTopology,bTopology);
				Label ring = new Label("Ring 8");
				Label mesh = new Label("Mesh 8");
			topologyNav.getChildren().addAll(topologyForm);
		sideNav.getChildren().addAll(topologyNav);
		sideNav.setPadding(new Insets(15));
		topologyNav.setPadding(new Insets(15));
		topologyNav.setAlignment(Pos.TOP_CENTER);
		topologyNav.setSpacing(10);
		topologyForm.setSpacing(10);
	
		
		
		
		bTopology.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				//add remove stuff
			
				if(topologyNav.getChildren().contains(ring)) {
					topologyNav.getChildren().removeAll(ring,mesh);
				}else {
					topologyNav.getChildren().addAll(ring,mesh);
				}				
			}
		});

		body1.setLeft(sideNav);		
		
		primaryStage.setTitle("Transponder Placement");
        primaryStage.setScene(scene);
        primaryStage.show();
		
	}
	
	//draw mesh8
		//(x,y) X is top left to right Y is from top left to bottom
		public void drawHyperCube8(BorderPane layout) {
			VBox box = new VBox();
			Pane nodes = new Pane();
			box.getChildren().add(nodes);
			box.setAlignment(Pos.CENTER_LEFT);
			Point2D mPoint1 = new Point2D(100,150);
			Point2D mPoint2 = new Point2D(400,150);
			Point2D mPoint3 = new Point2D(100,400);
			Point2D mPoint4 = new Point2D(400,400);
			
			Point2D mPoint5 = new Point2D(250,50);
			Point2D mPoint6 = new Point2D(550,50);
			Point2D mPoint7 = new Point2D(250,300);
			Point2D mPoint8 = new Point2D(550,300);
			
			Circle f1 = new Circle(mPoint1.x,mPoint1.y,8);
			Circle f2 = new Circle(mPoint2.x,mPoint2.y,8);
			Circle f3= new Circle(mPoint3.x,mPoint3.y,8);
			Circle f4= new Circle(mPoint4.y,mPoint4.y,8);
			
			Circle b1 = new Circle(mPoint5.x,mPoint5.y,8);
			Circle b2 = new Circle(mPoint6.x,mPoint6.y,8);
			Circle b3= new Circle(mPoint7.x,mPoint7.y,8);
			Circle b4= new Circle(mPoint8.x,mPoint8.y,8);
			
			Line linef1tob1 = new Line(100,150,250,50);
			Line linef2tob2 = new Line(400,150,550,50);
			Line linef3tob3 = new Line(100,400,250,300);
			Line linef4tob4 = new Line(400,400,550,300);
			
			Line linef1tof2 = new Line(100,150,400,150);
			Line linef2tof4 = new Line(400,150,400,400);
			Line linef3tof1 = new Line(100,400,100,150);
			Line linef4tof3 = new Line(400,400,100,400);
			
			Line lineb1tob2 = new Line(250,50,550,50);
			Line lineb2tob4 = new Line(550,50,550,300);
			Line lineb3tob1 = new Line(250,300,250,50);
			Line lineb4tob3 = new Line(550,300,250,300);
			
			nodes.getChildren().addAll(f1,f2,f3,f4,b1,b2,b3,b4);
			nodes.getChildren().addAll(linef1tob1,linef2tob2,linef3tob3,linef4tob4);
			nodes.getChildren().addAll(linef1tof2,linef2tof4,linef3tof1,linef4tof3);
			nodes.getChildren().addAll(lineb1tob2,lineb2tob4,lineb3tob1,lineb4tob3);
			layout.setCenter(box);
		}
		public void drawMesh8(BorderPane layout) {
			
			Pane draw= new Pane();
			
			Point2D mPoint1 = new Point2D(150,150);
			Point2D mPoint2 = new Point2D(250,50);
			Point2D mPoint3 = new Point2D(400,50);
			Point2D mPoint4 = new Point2D(500,150);
			Point2D mPoint5 = new Point2D(500,250);
			Point2D mPoint6 = new Point2D(400,350);
			Point2D mPoint7 = new Point2D(250,350);
			Point2D mPoint8 = new Point2D(150,250);
			
			Circle point1 = new Circle(mPoint1.x,mPoint1.y,10);
			Circle point2 = new Circle(mPoint2.x,mPoint2.y,10);
			Circle point3 = new Circle(mPoint3.x,mPoint3.y,10);
			Circle point4 = new Circle(mPoint4.x,mPoint4.y,10);
			Circle point5 = new Circle(mPoint5.x,mPoint5.y,10);
			Circle point6 = new Circle(mPoint6.x,mPoint6.y,10);
			Circle point7 = new Circle(mPoint7.x,mPoint7.y,10);
			Circle point8 = new Circle(mPoint8.x,mPoint8.y,10);
			
			//connections for point one mesh8
			//1st node 
			Line line1to3 = new Line(mPoint1.x,mPoint1.y,mPoint3.x,mPoint3.y);
			//line1to3.setStrokeWidth(2.5);
			Line line1to4 = new Line(mPoint1.x,mPoint1.y, mPoint4.x,mPoint4.y);
			//line1to4.setStrokeWidth(2.5);
			Line line1to5 = new Line(mPoint1.x,mPoint1.y, mPoint5.x,mPoint5.y);
			Line line1to6 = new Line(mPoint1.x,mPoint1.y, mPoint6.x,mPoint6.y);
			Line line1to8 = new Line(mPoint1.x,mPoint1.y, mPoint8.x,mPoint8.y);
			
			//second node
			Line line2to3= new Line(mPoint2.x,mPoint2.y, mPoint3.x,mPoint3.y);
			Line line2to4= new Line(mPoint2.x,mPoint2.y, mPoint4.x,mPoint4.y);
			Line line2to5= new Line(mPoint2.x,mPoint2.y, mPoint5.x,mPoint5.y);
			Line line2to6= new Line(mPoint2.x,mPoint2.y, mPoint6.x,mPoint6.y);
			Line line2to8= new Line(mPoint2.x,mPoint2.y, mPoint8.x,mPoint8.y);

			//3rd node
			Line line3to5 = new Line(mPoint3.x, mPoint3.y, mPoint5.x, mPoint5.y);
			Line line3to7 = new Line(mPoint3.x, mPoint3.y, mPoint7.x, mPoint7.y);
			Line line3to8 = new Line(mPoint3.x, mPoint3.y, mPoint8.x, mPoint8.y);
			
			//4th
			Line line4to6 = new Line(mPoint4.x,mPoint4.y,mPoint6.x,mPoint6.y);
			Line line4to7 = new Line(mPoint4.x,mPoint4.y,mPoint7.x,mPoint7.y);
			Line line4to8 = new Line(mPoint4.x,mPoint4.y,mPoint8.x,mPoint8.y);
			
			//5th
			Line line5to6 = new Line(mPoint5.x, mPoint5.y, mPoint6.x, mPoint6.y);
			Line line5to7 = new Line(mPoint5.x, mPoint5.y, mPoint7.x, mPoint7.y);
			Line line5to8 = new Line(mPoint5.x, mPoint5.y, mPoint8.x, mPoint8.y);
			
			//6th
			Line line6to7 = new Line(mPoint6.x, mPoint6.y, mPoint7.x, mPoint7.y);
			
			draw.getChildren().addAll(point1,point2,point3,point4,point5,point6,point7,point8);	
			draw.getChildren().addAll(line1to3,line1to4,line1to5,line1to6,line1to8);
			draw.getChildren().addAll(line2to3,line2to4,line2to5,line2to6, line2to8);
			draw.getChildren().addAll(line3to5,line3to7,line3to8);
			draw.getChildren().addAll(line4to6,line4to7,line4to8);
			draw.getChildren().addAll(line5to6,line5to7,line5to8);
			draw.getChildren().add(line6to7);
			layout.setCenter(draw);
			
			
		}
		
		public void drawNsfNet(BorderPane layout) {
			StackPane stackPane = new StackPane();	
			layout.setCenter(stackPane);
			
			HBox ivHolder = new HBox();
			Image map = new Image(getClass().getResourceAsStream("../img/usaMap.png"));
			ImageView iv = new ImageView(map);
			iv.setBlendMode(BlendMode.MULTIPLY);
			ivHolder.getChildren().add(iv);
			ivHolder.setPadding(new Insets(36,0,0,35));
		
			Point2D pCal1 = new Point2D(75, 292);
			Point2D pCal2 = new Point2D(25, 210);
			Point2D pWa = new Point2D(67, 55);
			Point2D pUt = new Point2D(148, 190);
			Point2D pCo = new Point2D(223, 206);
			Point2D pNe = new Point2D(315, 196);
			Point2D pTx = new Point2D(350, 350);
			Point2D pIl = new Point2D(417, 180);
			Point2D pMi = new Point2D(452, 138);
			Point2D pGa = new Point2D(490, 285);
			Point2D pPa = new Point2D(530, 157);
			Point2D pNy = new Point2D(547, 110);
			Point2D pDc = new Point2D(550, 186);
			Point2D pNj = new Point2D(572, 155);	
			
			ArrayList<Point2D> points = new ArrayList<>();
			points.add(pCal1);
			points.add(pCal2);
			points.add(pWa);
			points.add(pUt);
			points.add(pCo);
			points.add(pNe);
			points.add(pTx);
			points.add(pIl);
			points.add(pMi);
			points.add(pGa);
			points.add(pPa);
			points.add(pNy);
			points.add(pDc);
			points.add(pNj);
			
			for( Point2D point : points) {
				point.setLocation(point.x+35, point.y+20);
			}
			
			Pane nodes = new Pane();
			Circle ca1 = new Circle(pCal1.x,pCal1.y,6);
			Circle ca2 = new Circle(pCal2.x,pCal2.y,6);
			Circle wa= new Circle(pWa.x,pWa.y,6);
			Circle ut = new Circle(pUt.x,pUt.y,6);
			Circle co = new Circle(pCo.x,pCo.y,6);
			Circle ne = new Circle(pNe.x,pNe.y,6);
			Circle tx = new Circle(pTx.x,pTx.y,6);
			Circle il = new Circle(pIl.x, pIl.y, 6);
			Circle mi = new Circle(pMi.x,pMi.y,6);
			Circle ga = new Circle(pGa.x,pGa.y,6);
			Circle pa = new Circle(pPa.x,pPa.y,6);
			Circle ny = new Circle(pNy.x,pNy.y,6);
			Circle dc = new Circle(pDc.x,pDc.y,6);
			Circle nj= new Circle(pNj.x,pNj.y,6);
			
			Line lCal1toCal2 = new Line(pCal1.x, pCal1.y, pCal2.x, pCal2.y);
			Line lcal1toWa = new Line(pCal1.x,pCal1.y,pWa.x,pWa.y);
			Line lcal1toTx = new Line(pCal1.x,pCal1.y,pTx.x,pTx.y);
			Line lCal2toWa = new Line(pCal2.x,pCal2.y,pWa.x,pWa.y);
			Line lCal2toUt = new Line(pCal2.x,pCal2.y,pUt.x,pUt.y);
			Line lWatoIl = new Line(pWa.x,pWa.y,pIl.x,pIl.y);
			Line lUttoCo = new Line(pUt.x,pUt.y,pCo.x,pCo.y);
			Line lUttoMi = new Line(pUt.x,pUt.y,pMi.x,pMi.y);
			Line lCotoNe = new Line(pCo.x,pCo.y,pNe.x,pNe.y);
			Line lCotoTx = new Line(pCo.x,pCo.y,pTx.x,pTx.y);
			Line lNetoIl = new Line(pNe.x,pNe.y,pIl.x,pIl.y);
			Line lTxtoDc = new Line(pTx.x,pTx.y,pDc.x,pDc.y);
			Line lTxtoGa = new Line(pTx.x,pTx.y,pGa.x,pGa.y);
			Line lIltoPa = new Line(pIl.x,pIl.y, pPa.x,pPa.y); 
			Line lPatoGa = new Line(pPa.x,pPa.y,pGa.x,pGa.y);
			Line lPatoNj = new Line(pPa.x,pPa.y,pNj.x,pNj.y);
			Line lPatoNy = new Line(pPa.x,pPa.y,pNy.x,pNy.y);
			Line lMitoNy = new Line(pMi.x,pMi.y,pNy.x,pNy.y);
			Line lMitoNj = new Line(pMi.x,pMi.y,pNj.x,pNj.y);
			Line lDctoNy = new Line(pDc.x,pDc.y,pNy.x,pNy.y);
			Line lDctoNj = new Line(pDc.x,pDc.y,pNj.x,pNj.y);
			
			
			nodes.getChildren().addAll(ca1,ca2,wa,ut,co,ne,tx,il,mi,ga,pa,ny,dc,nj);
			nodes.getChildren().addAll(lCal1toCal2,lCal2toWa,lcal1toWa,lcal1toTx,lCal2toUt,lWatoIl,lUttoCo,lUttoMi,lCotoNe,lCotoTx);
			nodes.getChildren().addAll(lNetoIl,lTxtoDc,lTxtoGa,lIltoPa,lPatoGa,lPatoNj,lPatoNy,lMitoNj,lMitoNy,lDctoNj,lDctoNy);
			stackPane.getChildren().addAll(nodes,ivHolder);
			
			
			
		}
		
		public void drawRing8(BorderPane layout) {
			Pane draw= new Pane();
			Point2D mPoint1 = new Point2D(150,200);
			Point2D mPoint2 = new Point2D(201,77);
			Point2D mPoint3 = new Point2D(325,25);
			Point2D mPoint4 = new Point2D(452,80);
			Point2D mPoint5 = new Point2D(500,200);
			Point2D mPoint6 = new Point2D(448,325);
			Point2D mPoint7 = new Point2D(325,375);
			Point2D mPoint8 = new Point2D(198,322);
			
			Circle ring = new Circle(325,200,175);
			Circle center = new Circle(325,200,10);
			ring.setFill(null);
			ring.setStroke(Color.BLACK);
			
			Circle point1 = new Circle(mPoint1.x,mPoint1.y,10);
			Circle point2 = new Circle(mPoint2.x,mPoint2.y,10);
			Circle point3 = new Circle(mPoint3.x,mPoint3.y,10);
			Circle point4 = new Circle(mPoint4.x,mPoint4.y,10);
			Circle point5 = new Circle(mPoint5.x,mPoint5.y,10);
			Circle point6 = new Circle(mPoint6.x,mPoint6.y,10);
			Circle point7 = new Circle(mPoint7.x,mPoint7.y,10);
			Circle point8 = new Circle(mPoint8.x,mPoint8.y,10);
			

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
			draw.getChildren().addAll(ring,center);
			layout.setCenter(draw);
					
		}

}
