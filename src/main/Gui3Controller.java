package main;


import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import models.NodeEditor;
public class Gui3Controller implements Initializable, MapComponentInitializedListener {
	
	ObservableList<String> trafficMethodList = FXCollections.observableArrayList("-select a traffic request method--", "random", "gaussian", "uniform");
	ObservableList<String> routingMethodList = FXCollections.observableArrayList("--select a routing method--", "SPF", "LUF", "MUF", "OPT", "MUX", "Hybrid");
	private String trafficMethod;
	private String routingMethod;
	private StringBuilder console = new StringBuilder();
	List<NodeEditor> newNode = new ArrayList<NodeEditor>();

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
    	if(!trafficMethodBox.getValue().toString().equals("-select a traffic request method--")) {
    		trafficMethod = trafficMethodBox.getValue().toString();
    	}
    	console.append(trafficMethod + "\n");
    	consoleText.setText(console.toString());
    	System.out.println(trafficMethod);
    }

    @FXML
    private ComboBox<String> routingMethodBox;
    
    @FXML
    private void routingMethodChoice() {
    	if(!routingMethodBox.getValue().toString().equals("--select a routing method--")) {
    		routingMethod = routingMethodBox.getValue().toString();
    	}
    	System.out.println(routingMethod);
    }
    
    @FXML
    private Text consoleText;
    
    @FXML 
    private Button addButton;
    
    @FXML
    private TextField lat;
    
    @FXML
    private TextField log;
    
    @FXML
    public void handleButtonAction() {
    		String lattitude, lognitude;
    		lattitude = lat.getText();
    		lognitude = log.getText().toString();
    		System.out.println(lattitude); 
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		gmap.addMapInializedListener(this);
		trafficMethodBox.setItems(trafficMethodList);
		trafficMethodBox.getSelectionModel().select(0);
		routingMethodBox.setItems(routingMethodList);
		routingMethodBox.getSelectionModel().select(0);
	}
}
