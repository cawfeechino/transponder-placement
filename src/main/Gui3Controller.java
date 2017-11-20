package main;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.MouseEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import models.MapNode;
import netscape.javascript.JSObject;

public class Gui3Controller implements Initializable, MapComponentInitializedListener {

	ObservableList<String> trafficMethodList = FXCollections.observableArrayList("-select a traffic request method--",
			"random", "gaussian", "uniform");
	ObservableList<String> routingMethodList = FXCollections.observableArrayList("--select a routing method--", "SPF",
			"LUF", "MUF", "OPT", "MUX", "Hybrid");

	private String trafficMethod;
	private String routingMethod;
	private StringBuilder console = new StringBuilder();

	List<MapNode> entries = new ArrayList<MapNode>();

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private GoogleMapView gmap;

	@FXML
	private TextField lat;

	@FXML
	private TextField log;

	@FXML
	private TableColumn<MapNode, Integer> nodes;

	@FXML
	private TableColumn<MapNode, Double> lat_list, log_list;

	@FXML
	private TableView<MapNode> nodeList;

	@FXML
	private TitledPane nodeEdit;
	
	private DecimalFormat format = new DecimalFormat("#0.0000");
	
	@Override
	public void mapInitialized() {
		nodeEdit.setExpanded(true);
		
		MapOptions options = new MapOptions();
		options.center(new LatLong(38.873959, -98.517483)).zoomControl(true).zoom(4).overviewMapControl(false)
				.mapType(MapTypeIdEnum.ROADMAP);

		GoogleMap map = gmap.createMap(options);

		ArrayList<LatLong> locations = getLocations();
		
		
		//toolip for the marker optional
		InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
		InfoWindow infoWindow = new InfoWindow();
		// markers location icon
		for (LatLong point : locations) {
			
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.position(point);
			Marker marker = new Marker(markerOptions);
			map.addMarker(marker);
			map.addUIEventHandler(marker, UIEventType.click, new UIEventHandler() {
				
				@Override
				public void handle(JSObject arg0) {
					// TODO when click marker do something
					
					infoWindowOptions.content("description of marker");
					infoWindow.setOptions(infoWindowOptions);
					
					infoWindow.open(map,marker);
					LatLong ll = new LatLong((JSObject) arg0.getMember("latLng"));
					System.out.println(ll);
									
				}
			});
		
			nodeList.getItems().add(new MapNode(point.getLatitude(),point.getLongitude()));
			nodes.setCellValueFactory(new PropertyValueFactory<>("id"));
			lat_list.setCellValueFactory(new PropertyValueFactory<>("lattitude"));
			log_list.setCellValueFactory(new PropertyValueFactory<>("longitude"));
		}	

		ArrayList<LatLong[]> connections = getConnections();
		
		for (LatLong[] pair : connections) {
			MVCArray mvcArray = new MVCArray(pair);
			PolylineOptions polylineOptions = new PolylineOptions().path(mvcArray).strokeColor("black").strokeWeight(2);
			Polyline polyline = new Polyline(polylineOptions);
			map.addMapShape((MapShape) polyline);
		}
		
		//adding a new point
		map.addMouseEventHandler(null, UIEventType.click, new MouseEventHandler() {

			@Override
			public void handle(GMapMouseEvent arg0) {
				LatLong location = arg0.getLatLong();
				Double shortest = Double.MAX_VALUE;
				Double limit = 96560.64; //meters t0 miles(60)
				
				for(LatLong point : locations){
					if(shortest > point.distanceFrom(location)) {
						shortest = point.distanceFrom(location);
					}
				}
				
				// cannot have to markers 60 miles form each other 
				if(shortest > limit){
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setHeaderText("Please confirm your input.");
					
					Optional<ButtonType> result = alert.showAndWait();

					if(result.get() == ButtonType.OK) {
					
						MarkerOptions markerOptions = new MarkerOptions();
						markerOptions.position(location);
						Marker marker = new Marker(markerOptions);
						map.addMarker(marker);

						nodeList.getItems().add(new MapNode(location.getLatitude(), location.getLongitude()));
						nodes.setCellValueFactory(new PropertyValueFactory<>("id"));
						lat_list.setCellValueFactory(new PropertyValueFactory<>("lattitude"));
						log_list.setCellValueFactory(new PropertyValueFactory<>("longitude"));
					}
				}
				
			}
		});
		
	}

	@FXML
	private ComboBox<String> trafficMethodBox;

	@FXML
	private void trafficMethodChoice() {
		if (!trafficMethodBox.getValue().toString().equals("-select a traffic request method--")) {
			trafficMethod = trafficMethodBox.getValue().toString();
		}
		console.append(trafficMethod + "\n");
		consoleText.setText(console.toString());
		System.out.println(trafficMethod);
	}


	@FXML
	public void handleButtonAction() {
		double lattitude, lognitude;

		lattitude = Double.parseDouble(lat.getText().toString());
		lognitude = Double.parseDouble(log.getText().toString());

		MapNode entry = new MapNode(lattitude, lognitude);
		entries.add(entry);
		try {

			PrintWriter out = new PrintWriter(new FileWriter("output.txt"));
			for (MapNode e : entries) {
				out.println(e.getId() + "\t" + e.getLattitude() + "\t" + e.getLongitude());
			}

			out.close();
		} catch (IOException e1) {
			System.out.println("Error during reading/writing");
		}
		nodeList.getItems().add(new MapNode(lattitude, lognitude));
		nodes.setCellValueFactory(new PropertyValueFactory<>("id"));
		lat_list.setCellValueFactory(new PropertyValueFactory<>("lattitude"));
		log_list.setCellValueFactory(new PropertyValueFactory<>("longitude"));

	}

	@FXML
	private ComboBox<String> routingMethodBox;

	@FXML
	private void routingMethodChoice() {
		if (!routingMethodBox.getValue().toString().equals("--select a routing method--")) {
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
	
	public ArrayList<LatLong> getLocations(){
		ArrayList<LatLong> locations = new ArrayList<>();
		String path;
		try {
			path = new File(".").getCanonicalPath().concat("/src/assets/location.txt");
			File locationsFile = new File(path);
			Scanner scanner = new Scanner(locationsFile);
			
			while(scanner.hasNext()) {
				
				String[] line = scanner.nextLine().split(" ");
				locations.add(new LatLong(Double.parseDouble(line[0]), Double.parseDouble(line[1])));
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return locations;
	}
	
	public ArrayList<LatLong[]> getConnections(){
		ArrayList<LatLong[]> connections = new ArrayList<>();
		String path;
		try {
			path = new File(".").getCanonicalPath().concat("/src/assets/locationConnections.txt");
			File locationsFile = new File(path);
			Scanner scanner = new Scanner(locationsFile);
			
			while(scanner.hasNext()) {
				
				String[] line = scanner.nextLine().split(" ");
				String[] f = line[0].split(",");
				String[] s = line[1].split(",");
				double distance = Double.parseDouble(line[2]);
				LatLong[] pairs = new LatLong[]{new LatLong(Double.parseDouble(f[0]),Double.parseDouble(f[1])),new LatLong(Double.parseDouble(s[0]),Double.parseDouble(s[1]))}; 
				connections.add(pairs);
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return connections;
	}
	
	public ArrayList<LatLong> locations() {
		LatLong sd = new LatLong(32.7157, -117.1611);
		LatLong palo = new LatLong(37.4419, -122.143);
		LatLong seattle = new LatLong(47.6062, -122.3321);
		LatLong slc = new LatLong(40.7608, -111.8910);
		LatLong boulder = new LatLong(40.0150, -105.2705);
		LatLong houston = new LatLong(29.7604, -95.3698);
		LatLong lincoln = new LatLong(40.8258, -96.6852);
		LatLong champaign = new LatLong(40.1164, -88.2434);
		LatLong atlanta = new LatLong(33.749, -84.388);
		LatLong pittsburgh = new LatLong(40.4406, -79.9959);
		LatLong annArbor = new LatLong(42.2808, -83.7430);
		LatLong ithaca = new LatLong(43.444, -76.5019);
		LatLong collegePk = new LatLong(38.9897, -76.9378);
		LatLong princeton = new LatLong(40.3487, -74.659);
		
		ArrayList<LatLong> locations = new ArrayList<>();
		locations.add(sd);
		locations.add(palo);
		locations.add(seattle);
		locations.add(slc);
		locations.add(boulder);
		locations.add(houston);
		locations.add(lincoln);
		locations.add(champaign);
		locations.add(atlanta);
		locations.add(pittsburgh);
		locations.add(annArbor);
		locations.add(ithaca);
		locations.add(collegePk);
		locations.add(princeton);
		return locations;
	}
	
	public ArrayList<LatLong[]> locationConnection() {
		LatLong sd = new LatLong(32.7157, -117.1611);
		LatLong palo = new LatLong(37.4419, -122.143);
		LatLong seattle = new LatLong(47.6062, -122.3321);
		LatLong slc = new LatLong(40.7608, -111.8910);
		LatLong boulder = new LatLong(40.0150, -105.2705);
		LatLong houston = new LatLong(29.7604, -95.3698);
		LatLong lincoln = new LatLong(40.8258, -96.6852);
		LatLong champaign = new LatLong(40.1164, -88.2434);
		LatLong atlanta = new LatLong(33.749, -84.388);
		LatLong pittsburgh = new LatLong(40.4406, -79.9959);
		LatLong annArbor = new LatLong(42.2808, -83.7430);
		LatLong ithaca = new LatLong(43.444, -76.5019);
		LatLong collegePk = new LatLong(38.9897, -76.9378);
		LatLong princeton = new LatLong(40.3487, -74.659);
		
		ArrayList<LatLong[]> connections = new ArrayList<>();
		LatLong[] sdANDpalo = new LatLong[] { sd, palo };
		connections.add(sdANDpalo);

		LatLong[] sdANDsea = new LatLong[] { sd, seattle };
		connections.add(sdANDsea);

		LatLong[] paloANDsea = new LatLong[] { seattle, palo };
		connections.add(paloANDsea);

		LatLong[] paloANDslc = new LatLong[] { slc, palo };
		connections.add(paloANDslc);

		LatLong[] seattleANDchampaign = new LatLong[] { seattle, champaign };
		connections.add(seattleANDchampaign);

		LatLong[] sdANDhouston = new LatLong[] { sd, houston };
		connections.add(sdANDhouston);

		LatLong[] slcANDboulder = new LatLong[] { slc, boulder };
		connections.add(slcANDboulder);

		LatLong[] slcANDannArbor = new LatLong[] { slc, annArbor };
		connections.add(slcANDannArbor);

		LatLong[] boulderANDhouston = new LatLong[] { boulder, houston };
		connections.add(boulderANDhouston);

		LatLong[] boulderANDlincoln = new LatLong[] { boulder, lincoln };
		connections.add(boulderANDlincoln);

		LatLong[] lincolnANDchampaign = new LatLong[] { lincoln, champaign };
		connections.add(lincolnANDchampaign);

		LatLong[] champaignANDpittsburgh = new LatLong[] { champaign, pittsburgh };
		connections.add(champaignANDpittsburgh);

		LatLong[] houstonANDatlnta = new LatLong[] { houston, atlanta };
		connections.add(houstonANDatlnta);

		LatLong[] houstonANDcollegePk = new LatLong[] { houston, collegePk };
		connections.add(houstonANDcollegePk);

		LatLong[] atlantaANDpittsburgh = new LatLong[] { atlanta, pittsburgh };
		connections.add(atlantaANDpittsburgh);

		LatLong[] annArborANDprinceton = new LatLong[] { annArbor, princeton };
		connections.add(annArborANDprinceton);

		LatLong[] annArborANDithica = new LatLong[] { annArbor, ithaca };
		connections.add(annArborANDithica);

		LatLong[] pittsburghANDithaca = new LatLong[] { pittsburgh, ithaca };
		connections.add(pittsburghANDithaca);

		LatLong[] pittsburghANDprinceton = new LatLong[] { pittsburgh, princeton };
		connections.add(pittsburghANDprinceton);

		LatLong[] collegePkANDithaca = new LatLong[] { collegePk, ithaca };
		connections.add(collegePkANDithaca);

		LatLong[] collegePkANDprinceton = new LatLong[] { collegePk, princeton };
		connections.add(collegePkANDprinceton);
		
		return connections;
	}
}
