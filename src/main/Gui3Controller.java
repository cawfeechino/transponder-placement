package main;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import metrics.TransponderMetric;

public class Gui3Controller implements Initializable, MapComponentInitializedListener {

	ObservableList<String> trafficMethodList = FXCollections.observableArrayList("-select a traffic request method--",
			"random", "gaussian", "uniform");
	ObservableList<String> routingMethodList = FXCollections.observableArrayList("--select a routing method--", "SPF",
			"LUF", "MUF", "OPT", "MUX", "Hybrid");
	private String trafficMethod;
	private String routingMethod;
	private StringBuilder console = new StringBuilder();

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private GoogleMapView gmap;

	@Override
	public void mapInitialized() {
		MapOptions options = new MapOptions();

		options.center(new LatLong(47.606189, -122.335842)).zoomControl(true).zoom(12).overviewMapControl(false)
				.mapType(MapTypeIdEnum.ROADMAP);
		GoogleMap map = gmap.createMap(options);
	}

	@FXML
	private ComboBox<String> trafficMethodBox;

	@FXML
	private void trafficMethodChoice() {
		if (!trafficMethodBox.getValue().toString().equals("-select a traffic request method--")) {
			trafficMethod = trafficMethodBox.getValue().toString();
			console.append(trafficMethod + "\n");
		}
		consoleText.setText(console.toString());
	}

	@FXML
	private ComboBox<String> routingMethodBox;

	@FXML
	private void routingMethodChoice() {
		if (!routingMethodBox.getValue().toString().equals("--select a routing method--")) {
			routingMethod = routingMethodBox.getValue().toString();
			console.append(routingMethod + "\n");
		}
		consoleText.setText(console.toString());
	}

	@FXML
	private Button runSimulator;

	@FXML
	private void runSimulator(ActionEvent event) {
		console.append("running simulator\n");
		consoleText.setText(console.toString());
		Task<Void> simulator = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				String[] args = { trafficMethod.toUpperCase(), routingMethod };
				TransponderMetric.main(args);
				return null;
			}
		};
		simulator.setOnSucceeded(workerStateEvent -> {
			console.append("simulator finished\n");
			consoleText.setText(console.toString());
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Graph.fxml"));
				Parent root = (Parent) fxmlLoader.load();
				Stage stage = new Stage();
				stage.setTitle("Simulator Results");
				stage.setScene(new Scene(root));
				stage.show();
			} catch (Exception e) {

			}
		});
		simulator.setOnFailed(workerStateEvent -> {
			console.append("Did you set a traffic request method or routing method?\n");
			consoleText.setText(console.toString());
		});
		Thread run = new Thread(simulator);
		run.start();
	}

	@FXML
	private Text consoleText;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		gmap.addMapInializedListener(this);
		trafficMethodBox.setItems(trafficMethodList);
		trafficMethodBox.getSelectionModel().select(0);
		routingMethodBox.setItems(routingMethodList);
		routingMethodBox.getSelectionModel().select(0);
	}

}
