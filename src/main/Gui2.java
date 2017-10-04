package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.sun.javafx.geom.Point2D;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import metrics.TransponderMetric;
import utilities.NetworkTopology;
import draw.Ring8;

public class Gui2  extends Application{
	
	private Scanner scanner;
	private Thread backgroundThread;
	private Task<Void> mainTask;
	private String[] run;


	BorderPane layout;
	StackPane root;
	String project;

	private Ring8 drawRing8;

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//main scene and board
				project = new File(".").getCanonicalPath();
				layout = new BorderPane();
				root = new StackPane();
				Scene scene = new Scene(root, 800, 600);	
				root.getChildren().add(layout);
				
				scene.widthProperty().addListener(new ChangeListener<Number>() {

					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						System.out.println("W: " +newValue);
						
					}
				});
				
				scene.heightProperty().addListener(new ChangeListener<Number>() {

					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						System.out.println("H: " + newValue);
					}
				});
				
				MenuBar menu = new MenuBar();

				Menu menufile = new Menu("File");
					MenuItem setRequest = new MenuItem("Set Request");
					Menu topolodyDiagram = new Menu("Topology Diagram");
						MenuItem dRing8 = new MenuItem("Ring 8");
						MenuItem dMesh8 = new MenuItem("Mesh 8");
						MenuItem dHyperCube8 = new MenuItem("HyperCube 8");
						MenuItem dHyperCube16  = new MenuItem("HyperCube 16");
						MenuItem dNsfnet = new MenuItem("NsfNet");
						MenuItem upload = new MenuItem("Upload");
					Menu trafficRequest = new Menu("Traffic Request");
						MenuItem random = new MenuItem("Random");
						MenuItem uniform = new MenuItem("Uniform");
						MenuItem gaussian = new MenuItem("Gaussian");
					MenuItem failure = new MenuItem("Failure");
				
					topolodyDiagram.getItems().addAll(dRing8,dMesh8,dHyperCube8,dHyperCube16,dNsfnet, new SeparatorMenuItem(), upload);
					trafficRequest.getItems().addAll(random,uniform,gaussian);
				menufile.getItems().addAll(setRequest,topolodyDiagram,trafficRequest,failure);
						
				
				Menu menuPerformance = new Menu("Performance");
					Menu results = new Menu("Results");
						MenuItem ring8 = new MenuItem("Ring 8");
						MenuItem mesh8 = new MenuItem("Mesh 8");
						MenuItem hyperCube8 = new MenuItem("HyperCube 8");
						MenuItem hyperCube16  = new MenuItem("HyperCube 16");
				
					results.getItems().addAll(ring8,mesh8,hyperCube8,hyperCube16);
					menuPerformance.getItems().add(results);
				menu.getMenus().addAll(menufile,menuPerformance);
				
				ProgressIndicator spinner  = new ProgressIndicator();
				spinner.setMaxWidth(350);
				//upload topology 
				upload.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						FileChooser fileChooser= new FileChooser();
						fileChooser.setTitle("Topology file");
						fileChooser.showOpenDialog(primaryStage);
					}
				});
				
				//action event listener for drawing
				dMesh8.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						// TODO Auto-generated method stub
						drawMesh8();
						
					}
				});
				
				dNsfnet.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						drawNsfNet();
					}
				});						
				

				dRing8.setOnAction(new EventHandler<ActionEvent>(){
					@Override
					public void handle(ActionEvent event) {
						drawRing8();
					}
				});
				
				//action event listener for performance topology 
	
				String path =  project.concat("/src/results/");	
				
				ring8.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						run = new String[2];
						run[0]=NetworkTopology.RING8.name();
						run[1]= "30";
						
						mainTask = backgroundTask(run);
						backgroundThread= new Thread(mainTask);
						backgroundThread.start();

						root.getChildren().add(spinner);
						
						StackPane stack = new StackPane();
						stack.getChildren().add(spinner);
						layout.setCenter(stack);
						
						mainTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
							
							@Override
							public void handle(WorkerStateEvent event) {

								
								File folder = new File(path);
								File[] csvFiles = folder.listFiles();
								File mostRecent = mostRecentFile(csvFiles);
								try {
									readcsvfile(mostRecent);
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
								root.getChildren().remove(spinner);
								
							}
							
								
							
						});
		 					
					}
				});
				
				mesh8.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						// TODO Auto-generated method stub
						run = new String[2];
						run[0]=NetworkTopology.MESH8.name();
						run[1]= "30";

						mainTask = backgroundTask(run);
						backgroundThread= new Thread(mainTask);
						backgroundThread.start();
						root.getChildren().add(spinner);


						mainTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
							
							@Override
							public void handle(WorkerStateEvent event) {

								File folder = new File(path);
								File[] csvFiles = folder.listFiles();
								File mostRecent = mostRecentFile(csvFiles);
								try {
									readcsvfile(mostRecent);
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
								root.getChildren().remove(spinner);

							}
						});
						
					}
				});
				
				hyperCube8.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {   
						// TODO Auto-generated method stub
						run = new String[2];
						run[0]=NetworkTopology.HYPERCUBE8.name();
						run[1]= "30";
						
						mainTask = backgroundTask(run);
						backgroundThread= new Thread(mainTask);
						backgroundThread.start();
						root.getChildren().add(spinner);


						mainTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
							
							@Override
							public void handle(WorkerStateEvent event) {
								File folder = new File(path);
								File[] csvFiles = folder.listFiles();
								File mostRecent = mostRecentFile(csvFiles);
								try {
									readcsvfile(mostRecent);
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
								root.getChildren().remove(spinner);

							}
						});
						
					}
				});

				hyperCube16.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {

						run = new String[2];
						run[0]=NetworkTopology.HYPERCUBE16.name();
						run[1]= "30";
						
						mainTask = backgroundTask(run);
						backgroundThread= new Thread(mainTask);
						backgroundThread.start();
						root.getChildren().add(spinner);


						mainTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
							
							@Override
							public void handle(WorkerStateEvent event) {
								File folder = new File(path);
								File[] csvFiles = folder.listFiles();
								File mostRecent = mostRecentFile(csvFiles);
								try {
									readcsvfile(mostRecent);
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
								root.getChildren().remove(spinner);
							}
						});
					}
				});
				
				

				//test method work before running a topology simulation
//				readcsvfile(mostRecentFile(new File(path).listFiles()));
//				readcsvfile(mostRecentFile(new File(path).listFiles()));			
				
				
				layout.setTop(menu);
				layout.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, null, null)));
					
		        primaryStage.setTitle("Transponder Placement");
		        primaryStage.setScene(scene);
		        primaryStage.show();
		
	}
	
	//draw mesh8
	//(x,y) X is top left to right Y is from top left to bottom
	public void drawMesh8() {
		
		Pane draw= new Pane();
		Point2D mPoint1 = new Point2D(150,150);
		Point2D mPoint2 = new Point2D(250,50);
		Point2D mPoint3 = new Point2D(400,50);
		Point2D mPoint4 = new Point2D(500,150);
		Point2D mPoint5 = new Point2D(500,250);
		Point2D mPoint6 = new Point2D(400,350);
		Point2D mPoint7 = new Point2D(250,350);
		Point2D mPoint8 = new Point2D(150,250);
		
		Circle point1 = new Circle(mPoint1.x,mPoint1.y, 10);
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
	
	public void drawNsfNet() {
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
	
	
	public void drawSimple() {
		
	}
	
	public void drawRing8() {
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
	
//	reading csv files
	
	public void readcsvfile(File read) throws FileNotFoundException {
		scanner = new Scanner(read);
		ArrayList<String[]> file = new ArrayList<>();
		while(scanner.hasNext()) {
			
			String[] line = scanner.nextLine().split(",");
			file.add(line);
		}	
		
		displayFile(file);
		
	}	
	
	public void displayFile(ArrayList<String[]> file) {
		GridPane right = new GridPane();
		right.setGridLinesVisible(true);
		right.setHgap(10);
		right.setVgap(10);
		right.setPadding(new Insets(10));
		layout.setRight(right);
		right.setAlignment(Pos.CENTER);
		
		for(int i =0; i<file.size();i++) {
			for(int j=0; j< file.get(i).length; j++) {
				VBox cell = new VBox();
				Text text = new Text(file.get(i)[j]);
				text.setFont(new Font(16));
				cell.getChildren().add(text);
				cell.setAlignment(Pos.CENTER);
				right.add(cell, j, i);
			}
		}	
		
		
		
	}
	
	public File mostRecentFile(File[] csvFiles) {
		File mostRecent  = null;
		
		if(csvFiles.length==1) {
				mostRecent=csvFiles[0];
		}
		else if(csvFiles.length>1) {
			
			for(int i =0; i <csvFiles.length-1; i++) {
				if(csvFiles[i].lastModified() > csvFiles[i+1].lastModified()) {
					mostRecent = csvFiles[i];
				}
				else {
					mostRecent = csvFiles[i+1];
				}
			}
		}
		
		return mostRecent;
	}

	public Task<Void> backgroundTask(String[] args) {
		Task<Void> run = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				// TODO Auto-generated method stub
				try {
					TransponderMetric.main(args);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				return null;
			}
			
			
		};
		return run;
	
	}
}
