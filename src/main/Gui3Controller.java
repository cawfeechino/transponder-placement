package main;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.MapStateEventType;
import com.lynden.gmapsfx.javascript.event.MouseEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import models.nodeEditor;

public class Gui3Controller implements Initializable, MapComponentInitializedListener {
	
	ObservableList<String> trafficMethodList = FXCollections.observableArrayList("-select a traffic request method--", "random", "gaussian", "uniform");
	ObservableList<String> routingMethodList = FXCollections.observableArrayList("--select a routing method--", "SPF", "LUF", "MUF", "OPT", "MUX", "Hybrid");
	
	private String trafficMethod;
	private String routingMethod;
	private StringBuilder console = new StringBuilder();
	List<nodeEditor> entries = new ArrayList<nodeEditor>();
	private TableView<nodeEditor> table = new TableView<nodeEditor>();
	
	

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private GoogleMapView gmap;
	

	@Override
	public void mapInitialized() {
		MapOptions options = new MapOptions();
		//lat: 38.873959 lng: -98.517483 zoom:4
		options.center(new LatLong(38.873959, -98.517483)).zoomControl(true).zoom(4).overviewMapControl(false)
				.mapType(MapTypeIdEnum.ROADMAP);
		
		GoogleMap map = gmap.createMap(options);
		
		map.addMouseEventHandler(null, UIEventType.click, new MouseEventHandler() {
			
			@Override
			public void handle(GMapMouseEvent arg0) {
				// TODO Auto-generated method stub
				MarkerOptions markerOptions = new MarkerOptions();
				markerOptions.position(arg0.getLatLong());
				Marker marker = new Marker(markerOptions);
				map.addMarker(marker);				
			}
		});
		
		LatLong sd = new LatLong(32.7157,-117.1611);
		LatLong palo = new LatLong(37.4419, -122.143);
		LatLong seattle = new LatLong(47.6062, -122.3321);
		LatLong slc = new LatLong(40.7608,-111.8910);
		LatLong boulder= new LatLong(40.0150, -105.2705);
		LatLong houston= new LatLong(29.7604, -95.3698);
		LatLong lincoln= new LatLong(40.8258, -96.6852); 
		LatLong champaign= new LatLong(40.1164, -88.2434);
		LatLong atlanta= new LatLong(33.749, -84.388);
		LatLong pittsburgh= new LatLong(40.4406, -79.9959);
		LatLong annArbor= new LatLong(42.2808, -83.7430);
		LatLong ithaca= new LatLong(43.444,-76.5019);
		LatLong collegePk= new LatLong(38.9897, -76.9378);
		LatLong princeton= new LatLong(40.3487, -74.659);
		
		HashMap<Integer, LatLong> locations = new HashMap<>();
		locations.put(0,sd);
		locations.put(1, palo);
		locations.put(2, seattle);
		locations.put(3, slc);
		locations.put(4, boulder);
		locations.put(5, houston);
		locations.put(6, lincoln);
		locations.put(7, champaign);
		locations.put(8, atlanta);
		locations.put(9, pittsburgh);
		locations.put(10, annArbor);
		locations.put(11, ithaca);
		locations.put(12, collegePk);
		locations.put(13, princeton);
		
		
		//markers location icon
		for(int i =0; i < locations.size(); i++) {
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.position(locations.get(i));
			Marker marker = new Marker(markerOptions);
			map.addMarker(marker);
		}		
		
		ArrayList<LatLong[]> connections = new ArrayList<>();
		
		LatLong[] sdANDpalo = new LatLong[] {sd,palo};
		connections.add(sdANDpalo);
				
		LatLong[] sdANDsea = new LatLong[] {sd,seattle};
		connections.add(sdANDsea);
		
		LatLong[] paloANDsea = new LatLong[] {seattle,palo};
		connections.add(paloANDsea);
		
		LatLong[] paloANDslc = new LatLong[] {slc,palo};
		connections.add(paloANDslc);
		
		LatLong[] seattleANDchampaign = new LatLong[] {seattle,champaign};
		connections.add(seattleANDchampaign);
		
		LatLong[] sdANDhouston = new LatLong[] {sd,houston};
		connections.add(sdANDhouston);
		
		LatLong[] slcANDboulder = new LatLong[] {slc,boulder};
		connections.add(slcANDboulder);
		
		LatLong[] slcANDannArbor = new LatLong[] {slc,annArbor};
		connections.add(slcANDannArbor);
		
		LatLong[] boulderANDhouston = new LatLong[] {boulder,houston};
		connections.add(boulderANDhouston);
		
		LatLong[] boulderANDlincoln = new LatLong[] {boulder,lincoln};
		connections.add(boulderANDlincoln);
		
		LatLong[] lincolnANDchampaign = new LatLong[] {lincoln,champaign};
		connections.add(lincolnANDchampaign);
		
		LatLong[] champaignANDpittsburgh = new LatLong[] {champaign,pittsburgh};
		connections.add(champaignANDpittsburgh);
		
		LatLong[] houstonANDatlnta = new LatLong[] {houston,atlanta};
		connections.add(houstonANDatlnta);
		
		LatLong[] houstonANDcollegePk = new LatLong[] {houston,collegePk};
		connections.add(houstonANDcollegePk);
		
		LatLong[] atlantaANDpittsburgh = new LatLong[] {atlanta,pittsburgh};
		connections.add(atlantaANDpittsburgh);
		
		LatLong[] annArborANDprinceton = new LatLong[] {annArbor,princeton};
		connections.add(annArborANDprinceton);
		
		LatLong[] annArborANDithica = new LatLong[] {annArbor,ithaca};
		connections.add(annArborANDithica);
		
		LatLong[] pittsburghANDithaca = new LatLong[] {pittsburgh,ithaca};
		connections.add(pittsburghANDithaca);
		
		LatLong[] pittsburghANDprinceton = new LatLong[] {pittsburgh,princeton};
		connections.add(pittsburghANDprinceton);
		
		LatLong[] collegePkANDithaca = new LatLong[] {collegePk,ithaca};
		connections.add(collegePkANDithaca);
		
		LatLong[] collegePkANDprinceton = new LatLong[] {collegePk,princeton};
		connections.add(collegePkANDprinceton);

		for(LatLong[] pair : connections) {
			MVCArray mvcArray = new MVCArray(pair);
			PolylineOptions polylineOptions = new PolylineOptions().path(mvcArray).strokeColor("black").strokeWeight(2);
			Polyline polyline = new Polyline(polylineOptions);
			map.addMapShape((MapShape) polyline);
		}			
		
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
    private TextField lat;
    
    @FXML
    private TextField log;
    @FXML
    private TableColumn<nodeEditor, Integer> nodes; 
    @FXML
    private TableColumn<nodeEditor, Double> lat_list, log_list; 
    @FXML
    private TableView<nodeEditor> nodeList;
    

    @FXML
    public void handleButtonAction() {
    		double lattitude, lognitude;
    		
    		lattitude = Double.parseDouble(lat.getText().toString());
    		lognitude = Double.parseDouble(log.getText().toString());
    		
    		nodeEditor entry = new nodeEditor(lattitude,lognitude);
    		entries.add(entry);
    		try {
    			
    			PrintWriter out = new PrintWriter(new FileWriter("output.txt"));
    			for(nodeEditor e : entries) {
    				out.println(e.getId() + "\t" + e.getLattitude() + "\t" + e.getLongitude());
    				
    				
    			}
    			
    			out.close();
    		}catch(IOException e1) {
    	        System.out.println("Error during reading/writing");
    		   }
	    		nodeList.getItems().add(new nodeEditor(lattitude, lognitude));
	    		nodes.setCellValueFactory(new PropertyValueFactory<>("id"));
	    		lat_list.setCellValueFactory(new PropertyValueFactory<>("lattitude"));
	    		log_list.setCellValueFactory(new PropertyValueFactory<>("longitude"));
    		
    		
    		
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		gmap.addMapInializedListener(this);
		trafficMethodBox.setItems(trafficMethodList);
		trafficMethodBox.getSelectionModel().select(0);
		routingMethodBox.setItems(routingMethodList);
		routingMethodBox.getSelectionModel().select(0);
		
	    
	}
}
