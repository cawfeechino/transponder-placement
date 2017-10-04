package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.sun.javafx.geom.Point2D;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import metrics.TransponderMetric;
import utilities.NetworkTopology;

public class Gui2  extends Application{
	
	private Scanner scanner;
	private Thread backgroundThread;
	private Task<Void> mainTask;
	private String[] run;

	BorderPane layout;
	StackPane root;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		//main scene and board
				String project = new File(".").getCanonicalPath();
				root = new StackPane();
				layout = new BorderPane();
				Scene scene = new Scene(root, 650, 500);	
				GridPane body = new GridPane();
				MenuBar menu = new MenuBar();	
					root.getChildren().add(layout);
					body.setVgap(10);
					body.setHgap(10);
					body.setPadding(new Insets(0, 10, 0, 10));
					
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
				
				//action event listener for drawing
				dMesh8.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						// TODO Auto-generated method stub
						drawMesh8();
						
					}
				});
				
				dHyperCube8.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						drawHyperCube8();
						
					}
				});
				
				dNsfnet.setOnAction(new EventHandler<ActionEvent>() {
					//10x 20 y
					@Override
					public void handle(ActionEvent event) {
						
					}
				});
				
				
			
				//
				//action event listener for performance topology 
	
				String path =  project.concat("/src/results/");	
				
				ring8.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						// TODO Auto-generated method stub
						run = new String[2];
						run[0]=NetworkTopology.RING8.name();
						run[1]= "30";
						
						//Thread main = new Thread(runMain);
						//main.start();
						
						mainTask = backgroundTask(run);
						backgroundThread= new Thread(mainTask);
						backgroundThread.start();
						root.getChildren().add(spinner);

						mainTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
							
							@Override
							public void handle(WorkerStateEvent event) {
								// TODO Auto-generated method stub
								root.getChildren().remove(spinner);
								File folder = new File(path);
								File[] csvFiles = folder.listFiles();
								File mostRecent = mostRecentFile(csvFiles);
								try {
									readcsvfile(mostRecent);
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
								
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
								// TODO Auto-generated method stub
								root.getChildren().remove(spinner);
								File folder = new File(path);
								File[] csvFiles = folder.listFiles();
								File mostRecent = mostRecentFile(csvFiles);
								try {
									readcsvfile(mostRecent);
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
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
								// TODO Auto-generated method stub
								root.getChildren().remove(spinner);
								File folder = new File(path);
								File[] csvFiles = folder.listFiles();
								File mostRecent = mostRecentFile(csvFiles);
								try {
									readcsvfile(mostRecent);
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
							}
						});
						
					}
				});

				hyperCube16.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						// TODO Auto-generated method stub
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
								// TODO Auto-generated method stub
								root.getChildren().remove(spinner);
								File folder = new File(path);
								File[] csvFiles = folder.listFiles();
								File mostRecent = mostRecentFile(csvFiles);
								try {
									readcsvfile(mostRecent);
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
							}
						});
					}
				});
				
				

				//test method work before running a topology simulation
				//readcsvfile(mostRecentFile(new File(path).listFiles()));			
				
				
				
				layout.setTop(menu);
				layout.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, null, null)));
					
				System.out.println(Color.ANTIQUEWHITE.toString());
		        primaryStage.setTitle("Transponder Placement");
		        primaryStage.setScene(scene);
		        primaryStage.show();
		
	}
	
	
	//draw mesh8
	//(x,y) X is top left to right Y is from top left to bottom
	public void drawHyperCube8( ) {
		Pane nodes = new Pane();
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
		
		//Circle slc = new Circle() 
		nodes.getChildren().addAll(f1,f2,f3,f4,b1,b2,b3,b4);
		nodes.getChildren().addAll(linef1tob1,linef2tob2,linef3tob3,linef4tob4);
		nodes.getChildren().addAll(linef1tof2,linef2tof4,linef3tof1,linef4tof3);
		nodes.getChildren().addAll(lineb1tob2,lineb2tob4,lineb3tob1,lineb4tob3);
		layout.setCenter(nodes);
	}
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
	
	
//	reading csv files
	
	public void readcsvfile(File read) throws FileNotFoundException {
		System.out.println("most recent: "+ read.lastModified());
		scanner = new Scanner(read);
		ArrayList<String[]> file = new ArrayList<>();
		while(scanner.hasNext()) {
			
			String[] line = scanner.nextLine().split(",");
			file.add(line);
		}
		
		for(String [] line : file) {
			for(String word : line) {
				System.out.print(word +" ");
			}
			System.out.println("");
		}
		
		displayFile(file);
		
	}	
	
	public void displayFile(ArrayList<String[]> file) {
		GridPane left = new GridPane();
		left.setGridLinesVisible(true);
		left.setHgap(10);
		left.setVgap(10);
		left.setPadding(new Insets(10));
		layout.setLeft(left);
		
		for(int i =0; i<file.size();i++) {
			for(int j=0; j< file.get(i).length; j++) {
				VBox cell = new VBox();
				Text text = new Text(file.get(i)[j]);
				text.setFont(new Font(16));
				cell.getChildren().add(text);
				cell.setAlignment(Pos.CENTER);
				left.add(cell, j, i);
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
