package main;


import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.sun.jmx.snmp.tasks.Task;

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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import metrics.TransponderMetric;
import utilities.NetworkTopology;

public class Gui extends Application {
	private String[] firstRun = new String[2];
		
	@Override
	public void start(Stage primaryStage) throws Exception {

		//main scene and board
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 650, 500);	
		
		//main scene 
		
		GridPane body = new GridPane();
			body.setVgap(10);
			body.setHgap(10);
			body.setPadding(new Insets(0, 10, 0, 10));
			
		MenuBar menu = new MenuBar();
		
		//TODO set listeners/eventHandlers for menuItems
		Menu menufile = new Menu("File");
		MenuItem setRequest = new MenuItem("Set Request");
		menufile.getItems().addAll(setRequest);		
		
		VBox newRun = new VBox();
		VBox newThreshold = new VBox();
		GridPane left = new GridPane();
		left.setGridLinesVisible(true);
		Button run = new Button("Run");
		run.setFont(new Font(16));
		left.setHgap(10);
		left.setVgap(10);
		left.setPadding(new Insets(10));
		left.add(newRun, 0, 0);
		left.add(newThreshold, 0, 1);
		left.add(run, 0, 2);
		root.setLeft(left);
		newRun.setSpacing(10);
		newRun.setPadding(new Insets(15));
		newThreshold.setSpacing(10);
		newThreshold.setPadding(new Insets(15));
		
		Label topology = new Label("NetworkTopology");
		topology.setFont(new Font(12));
		
		ToggleGroup topologyGroup = new ToggleGroup();
		RadioButton mRing8 = new RadioButton("Ring 8");
		mRing8.setToggleGroup(topologyGroup);
		mRing8.setFont(new Font(12));
		mRing8.setId(NetworkTopology.RING8.name());
		RadioButton mHypercube8 = new RadioButton("HyperCube 8");
		mHypercube8.setToggleGroup(topologyGroup);
		mHypercube8.setFont(new Font(12));
		mHypercube8.setId(NetworkTopology.HYPERCUBE8.name());
		RadioButton mMesh8 = new RadioButton("Mesh 8");
		mMesh8.setToggleGroup(topologyGroup);
		mMesh8.setFont(new Font(12));
		mMesh8.setId(NetworkTopology.MESH8.name());
		RadioButton mHypercube16 = new RadioButton("HyperCube 16");
		mHypercube16.setToggleGroup(topologyGroup);
		mHypercube16.setFont(new Font(12));
		mHypercube16.setId(NetworkTopology.HYPERCUBE16.name());
		
		newRun.getChildren().addAll(topology,mRing8,mMesh8,mHypercube8,mHypercube16);
		
		Label mThreshold = new Label("Threshold");
		mThreshold.setFont(new Font(12));
		
		ComboBox<String> numbers = new ComboBox<String>();
		numbers.getItems().addAll("30","40","50","60","70","80","90","110");
		
		newThreshold.getChildren().addAll(mThreshold,numbers);
		
		run.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				String[] newRun = new String[2];
				RadioButton selectedRadio = (RadioButton) topologyGroup.getSelectedToggle();
				newRun[0]=selectedRadio.getId();
				newRun[1]=numbers.getValue();
				
				Task runMain = new Task() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							TransponderMetric.main(newRun);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}
					@Override
					public void cancel() {
						
					}
				};
				Thread main = new Thread(runMain);
				main.start();
			}
		});
		
		menu.getMenus().addAll(menufile);
		root.setTop(menu);
		root.setCenter(body);		
		root.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, null, null)));
		
		//listener to find the ideal size for the gui
		scene.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					System.out.println("Width: " + newValue);	
			}
			
		});
		
		scene.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				System.out.println("Height: " + newValue);
			}
		});
		

		//startup scene
		BorderPane startUp = new BorderPane();		
		Scene first = new Scene(startUp,650,500);
		
		HBox bottom = new HBox();
		bottom.setPadding(new Insets(0,100,75,0));
		bottom.setAlignment(Pos.BASELINE_RIGHT);
		startUp.setBottom(bottom);
		
		HBox top = new HBox();
		top.setPadding(new Insets(50,25,0,25));
		top.setAlignment(Pos.CENTER);
		startUp.setTop(top);
	
		Text networkTopology = new Text("NetworkTopology");
		networkTopology.setFont(new Font(22));
		top.getChildren().add(networkTopology);
		
		VBox center = new VBox();
		GridPane centerBox = new GridPane();
		ToggleGroup selection1 = new ToggleGroup();
		RadioButton ring8 = new RadioButton("RING 8");
		ring8.setToggleGroup(selection1);
		ring8.setFont(new Font(18));
		RadioButton hypercube8 = new RadioButton("HYPERCUBE 8");
		hypercube8.setToggleGroup(selection1);
		hypercube8.setFont(new Font(18));
		RadioButton mesh8 = new RadioButton("MESH 8");
		mesh8.setToggleGroup(selection1);
		mesh8.setFont(new Font(18));
		RadioButton hypercube16 = new RadioButton("HYPERCUBE 16");
		hypercube16.setToggleGroup(selection1);
		hypercube16.setFont(new Font(18));
		

		center.getChildren().addAll(ring8,hypercube8,mesh8,hypercube16);
		center.setSpacing(10);
		centerBox.add(center, 0,0);
		centerBox.setHgap(10);
		centerBox.setVgap(20);
		centerBox.setPadding(new Insets(0, 10, 0, 10));
		centerBox.setAlignment(Pos.CENTER);
		startUp.setCenter(centerBox);
	
		
		Button next = new Button("Next >");
		next.setFont(new Font(16));
		bottom.getChildren().add(next);
		next.setDisable(true);
		
		EventHandler<ActionEvent> enableNext= new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				next.setDisable(false);
				
			}
		};
		
		ring8.setOnAction(enableNext);
		hypercube8.setOnAction(enableNext);
		mesh8.setOnAction(enableNext);
		hypercube16.setOnAction(enableNext);
		
		
		//second
		//scene
				BorderPane secondB = new BorderPane();		
				Scene second = new Scene(secondB,650,500);
				
				HBox bottom2= new HBox();
				bottom2.setPadding(new Insets(0,100,75,0));
				bottom2.setAlignment(Pos.BASELINE_RIGHT);
				secondB.setBottom(bottom2);
				
				HBox top2 = new HBox();
				top2.setPadding(new Insets(50,25,0,25));
				top2.setAlignment(Pos.CENTER);
				secondB.setTop(top2);
			
				Text threshold = new Text("Threshold");
				threshold.setFont(new Font(22));
				top2.getChildren().add(threshold);
				
				VBox center2 = new VBox();
				VBox center2B = new VBox();
				GridPane centerBox2 = new GridPane();
				ToggleGroup selection2 = new ToggleGroup();
				RadioButton threshold30 = new RadioButton("30");
				threshold30.setToggleGroup(selection2);
				threshold30.setFont(new Font(18));
				RadioButton threshold40 = new RadioButton("40");
				threshold40.setToggleGroup(selection2);
				threshold40.setFont(new Font(18));
				RadioButton threshold50 = new RadioButton("50");
				threshold50.setToggleGroup(selection2);
				threshold50.setFont(new Font(18));
				RadioButton threshold60 = new RadioButton("60");
				threshold60.setToggleGroup(selection2);
				threshold60.setFont(new Font(18));
				RadioButton threshold70 = new RadioButton("70");
				threshold70.setToggleGroup(selection2);
				threshold70.setFont(new Font(18));
				RadioButton threshold80 = new RadioButton("80");
				threshold80.setToggleGroup(selection2);
				threshold80.setFont(new Font(18));
				RadioButton threshold90 = new RadioButton("90");
				threshold90.setToggleGroup(selection2);
				threshold90.setFont(new Font(18));
				RadioButton threshold110 = new RadioButton("110");
				threshold110.setToggleGroup(selection2);
				threshold110.setFont(new Font(18));
				

				center2.getChildren().addAll(threshold30,threshold40,threshold50,threshold60);
				center2B.getChildren().addAll(threshold70,threshold80,threshold90,threshold110);
				center2.setSpacing(10);
				center2B.setSpacing(10);
				centerBox2.add(center2, 0,0);
				centerBox2.add(center2B, 1, 0);
				centerBox2.setHgap(30);
				centerBox2.setVgap(20);
				centerBox2.setPadding(new Insets(0, 10, 0, 10));
				centerBox2.setAlignment(Pos.CENTER);
				secondB.setCenter(centerBox2);
				
				
				Button finish = new Button("Finish->");
				finish.setFont(new Font(16));
				bottom2.getChildren().add(finish);
				finish.setDisable(true);
				
				
				EventHandler<ActionEvent> enablefinish= new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						finish.setDisable(false);
						
					}
				};
				
				threshold30.setOnAction(enablefinish);
				threshold40.setOnAction(enablefinish);
				threshold50.setOnAction(enablefinish);
				threshold60.setOnAction(enablefinish);
				threshold70.setOnAction(enablefinish);
				threshold80.setOnAction(enablefinish);
				threshold90.setOnAction(enablefinish);
				threshold110.setOnAction(enablefinish);
				
				finish.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						if(threshold30.isSelected()) {
							firstRun[1]="30";
						}else if(threshold40.isSelected()) {
							firstRun[1]="40";
						}else if(threshold50.isSelected()) {
							firstRun[1]="50";
						}else if(threshold60.isSelected()) {
							firstRun[1]="60";
						}else if(threshold70.isSelected()) {
							firstRun[1]="70";
						}else if(threshold80.isSelected()) {
							firstRun[1]="80";
						}else if(threshold90.isSelected()) {
							firstRun[1]="90";
						}else if(threshold110.isSelected()) {
							firstRun[1]="110";
						}
						
						if(firstRun[0].equals(NetworkTopology.RING8.name())) {
							topologyGroup.selectToggle(mRing8);
						}else if(firstRun[0].equals(NetworkTopology.MESH8.name())) {
							topologyGroup.selectToggle(mMesh8);
						}else if(firstRun[0].equals(NetworkTopology.HYPERCUBE8.name())) {
							topologyGroup.selectToggle(mHypercube8);
						}else if(firstRun[0].equals(NetworkTopology.HYPERCUBE16.name())) {
							topologyGroup.selectToggle(mHypercube16);
						}
						
						numbers.setValue(firstRun[1]);	
						
						Task runMain = new Task() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									TransponderMetric.main(firstRun);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
							
							@Override
							public void cancel() {
								// TODO Auto-generated method stub
								
							}
						};
						Thread main = new Thread(runMain);
						main.start();
						
					primaryStage.setScene(scene);	
					}
				});
				
				next.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
					
						if(ring8.isSelected()) {
							firstRun[0]=NetworkTopology.RING8.name();							
						}
						else if(mesh8.isSelected()) {
							firstRun[0]=NetworkTopology.MESH8.name();
							
						}else if(hypercube8.isSelected()) {
							firstRun[0]=NetworkTopology.HYPERCUBE8.name();
							
						}else if(hypercube16.isSelected()) {
							firstRun[0]=NetworkTopology.HYPERCUBE16.name();
						}
					primaryStage.setScene(second);	
					}
				});	
				
				
			//reading csv files body is the root middle gridPane
			String path =  new File(".").getCanonicalPath().concat("/src/results/");			
			File folder = new File(path);
			File[] csvFiles = folder.listFiles();
 			
			Scanner scanner = new Scanner(csvFiles[0]);
			while(scanner.hasNext()) {
				String[] line = scanner.nextLine().split(",");
				System.out.println(line[0]+' '+line[1]+" "+line[2]);
			}
			
			
	
			

				
				
	        primaryStage.setTitle("Transponder Placement");
	        primaryStage.setScene(first);
	        primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	

}
