package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
				
				//action event listener for performance topology 
	
				String path =  new File(".").getCanonicalPath().concat("/src/results/");	
				
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
				
				

				//readcsvfile(mostRecentFile(new File(path).listFiles()));
				
				layout.setTop(menu);
				layout.setCenter(body);
				layout.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, null, null)));
						
		        primaryStage.setTitle("Transponder Placement");
		        primaryStage.setScene(scene);
		        primaryStage.show();
		
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
