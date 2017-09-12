package main;


import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Gui extends Application {

	
	@Override
	public void start(Stage primaryStage) throws Exception {
		//TODO gui maybe add borderPane instead of Vbox as root
		Scene scene = new Scene(new VBox(), 650, 500);
		
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
		
		GridPane body = new GridPane();
			body.setVgap(10);
			body.setHgap(10);
			body.setPadding(new Insets(0, 10, 0, 10));
			
		MenuBar menu = new MenuBar();
		
		//TODO set listeners/eventHandlers for menuItems
		Menu menufile = new Menu("File");
			MenuItem network = new MenuItem("NetWork");
			MenuItem traffic = new MenuItem("Traffic");
			MenuItem link = new MenuItem("Link Failor");
		menufile.getItems().addAll(network,traffic,link);
		
		Menu menuStrategy = new Menu("Strategy");
			MenuItem shortestP = new MenuItem("Shortest Path");
			MenuItem kShortestP = new MenuItem("K-Shortest Path");
		menuStrategy.getItems().addAll(shortestP,kShortestP);
		
		Menu menuProformance = new Menu("Proformance");
			MenuItem bandwidthConsumption = new MenuItem("Bandwidth Consumption");
		menuProformance.getItems().add(bandwidthConsumption);
		
		Menu menuSet = new Menu("Set Request");
			MenuItem source = new MenuItem("Source");
			MenuItem end = new MenuItem("End");
			MenuItem bandwidth = new MenuItem("Bandwidth");
		menuSet.getItems().addAll(source,end,bandwidth);
			
		
		Text test = new Text("test");
		body.add(test, 3, 4);
		Text test2 = new Text("test2");
		body.add(test2, 1, 1);
		
		menu.getMenus().addAll(menufile,menuProformance,menuStrategy,menuSet);
				
		((VBox) scene.getRoot()).getChildren().addAll(menu,body);
		((VBox) scene.getRoot()).setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, null, null)));

	        primaryStage.setTitle("Transponder Placement");
	        primaryStage.setScene(scene);
	        primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
