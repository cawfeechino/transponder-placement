package main;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.MouseEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;

import com.lynden.gmapsfx.service.geocoding.GeocoderStatus;
import com.lynden.gmapsfx.service.geocoding.GeocodingResult;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import com.lynden.gmapsfx.service.geocoding.GeocodingServiceCallback;
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
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewFocusModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
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


	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private GoogleMapView gmap;

	@FXML
	private TableColumn<MapNode, Integer> nodes;

	@FXML
	private TableColumn<MapNode, Double> lat_list, log_list;

	@FXML
	private TableView<MapNode> nodeList;

	@FXML
	private TitledPane nodeEdit;
	
	@FXML
	private TextField lat, log, startNorth, startEast, endNorth, endEast;
	
	@FXML
	private Button addLocation, addConnection;
	
	private DecimalFormat format = new DecimalFormat("#0.0000");
	
	private GeocodingService geocodingService;
	
	private final String us = "United States";
	
	private ArrayList<LatLong> locations;
	
	private ArrayList<LatLong[]> connections;

	
	@Override
	public void mapInitialized() {
		nodeEdit.setExpanded(true);
		geocodingService = new GeocodingService();
		
		MapOptions options = new MapOptions();
		options.center(new LatLong(38.873959, -98.517483)).zoomControl(true).zoom(4).overviewMapControl(false)
				.mapType(MapTypeIdEnum.ROADMAP).mapTypeControl(false).streetViewControl(false);

		GoogleMap map = gmap.createMap(options);
		//toolip for the marker optional
		InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
		InfoWindow infoWindow = new InfoWindow();
		
		
		locations = getLocations();
		
		// markers location icon
		for (LatLong point : locations) {
			addMarker(point, map, infoWindow, infoWindowOptions);
		}	

		connections = getConnections();
		
		for (LatLong[] pair : connections) {
			MVCArray mvcArray = new MVCArray(pair);
			PolylineOptions polylineOptions = new PolylineOptions().path(mvcArray).strokeColor("black").strokeWeight(1.5);
			Polyline polyline = new Polyline(polylineOptions);
			map.addMapShape((MapShape) polyline);
			
		}
		
		nodeList.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(startNorth.getText().equals("")||startEast.getText().equals("")) {
					startNorth.setText(format.format(nodeList.getSelectionModel().getSelectedItem().getLatitude()));
					startEast.setText(format.format(nodeList.getSelectionModel().getSelectedItem().getLongitude()));
				}else {
					endNorth.setText(format.format(nodeList.getSelectionModel().getSelectedItem().getLatitude()));
					endEast.setText(format.format(nodeList.getSelectionModel().getSelectedItem().getLongitude()));
				}
			}
		});
		
		
		
		//adding a new point
		map.addMouseEventHandler(map, UIEventType.rightclick, new MouseEventHandler() {

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
				if(shortest> limit) {
					lat.setText(format.format(location.getLatitude()));
					log.setText(format.format(location.getLongitude()));
				
					geocodingService.reverseGeocode(location.getLatitude(), location.getLongitude(), new GeocodingServiceCallback() {
						
						@Override
						public void geocodedResultsReceived(GeocodingResult[] arg0, GeocoderStatus arg1) {
							if(arg1.equals(GeocoderStatus.OK)) {
								if(arg0[arg0.length-1].getFormattedAddress().equals(us)) {
									addMarker(location, map, infoWindow, infoWindowOptions);
								}  									
							} 
							lat.clear();
							log.clear();
						}
						
					});
				}
			}
		});
		
		
		//manually add stuff
				addLocation.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						String nlat = format.format(Double.parseDouble(lat.getText()));
						String nlng = format.format(Double.parseDouble(log.getText()));
						LatLong newPoint = new LatLong(Double.parseDouble(nlat), Double.parseDouble(nlng));
						
							addMarker(newPoint, map, infoWindow, infoWindowOptions);
					}
				});
				
				addConnection.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent arg0) {
					
						boolean valid = false;
						LatLong[] pair = new LatLong[]{new LatLong(Double.parseDouble(startNorth.getText()), Double.parseDouble(startEast.getText())),
														new LatLong(Double.parseDouble(endNorth.getText()), Double.parseDouble(endEast.getText()))};
						
						
						
						List<MapNode> items = nodeList.getItems();

//						for(int i = 0; i <items.size(); i++) {
//							boolean r = items.get(i).getLatitude()==pair[0].getLatitude();
//							System.out.println(items.get(i).getLatitude() + " = " + pair[0].getLatitude() +" "+ r);
//							r=format.format(items.get(i).getLongitude()).equals(format.format(pair[0].getLongitude()));
//							System.out.println(items.get(i).getLongitude() + " = " + format.format(pair[0].getLongitude()) +" "+ r);
//							r = items.get(i).getLatitude()==pair[1].getLatitude();
//							System.out.println(items.get(i).getLatitude() + " = " + pair[1].getLatitude() +" "+ r);
//							r=format.format(items.get(i).getLongitude()).equals(format.format(pair[1].getLongitude()));
//							System.out.println(items.get(i).getLongitude() + " = " + format.format(pair[1].getLongitude()) +" "+ r);
//							
//						}
						
						//the format of the longitude gives an error so compare the string using the format of above(format) to see results uncomment above code
						for(int i =0; i< items.size(); i++) {
							if(items.get(i).getLatitude()==pair[0].getLatitude()&format.format(items.get(i).getLongitude()).equals(format.format(pair[0].getLongitude()))) {
								valid=true;
								break;
							}
						}
						
						for(int i =0; i< items.size(); i++) {
							if(items.get(i).getLatitude()==pair[1].getLatitude()&format.format(items.get(i).getLongitude()).equals(format.format(pair[1].getLongitude()))) {
								valid=true;
								break;
							}
						}
						
						
						if(valid) {
							MVCArray mvcArray = new MVCArray(pair);
							PolylineOptions polylineOptions = new PolylineOptions().path(mvcArray).strokeColor("black").strokeWeight(1.5);
							Polyline polyline = new Polyline(polylineOptions);
							map.addMapShape((MapShape) polyline);
							startNorth.clear();
							startEast.clear();
							endEast.clear();
							endNorth.clear();
						}else {
							startNorth.clear();
							startEast.clear();
							endEast.clear();
							endNorth.clear();
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
	
	
	//TODO change path when user saves new nsfnet keep default 
	public void writeLocations(ArrayList<LatLong> locations) {
		try {
			PrintWriter out = new PrintWriter(new FileWriter("src/assets/location.txt"));
			for (LatLong location : locations) {
				out.println(location.getLatitude() + " "+location.getLongitude());
			}

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeConnections(ArrayList<LatLong[]> connections) {
		try {
			PrintWriter out = new PrintWriter(new FileWriter("src/assets/location.txt"));
			for (LatLong[] pair : connections) {
				out.println(pair[0].getLatitude() +','+pair[0].getLongitude()+" "
							+ pair[1].getLatitude()+','+pair[1].getLongitude()+" "
							+ format.format(pair[0].distanceFrom(pair[1])));
			}

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addMarker(LatLong point, GoogleMap map, InfoWindow infoWindow, InfoWindowOptions infoWindowOptions) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(point);
		Marker marker = new Marker(markerOptions);
		map.addMarker(marker);
		
		map.addUIEventHandler(marker, UIEventType.click, new UIEventHandler() {
			
			@Override
			public void handle(JSObject arg0) {
				
				LatLong ll = new LatLong((JSObject) arg0.getMember("latLng"));
		
				geocodingService.reverseGeocode(ll.getLatitude(), ll.getLongitude(), new GeocodingServiceCallback() {
					
					@Override
					public void geocodedResultsReceived(GeocodingResult[] arg0, GeocoderStatus arg1) {						
						String[] results = arg0[arg0.length-3].getFormattedAddress().split(",");
						
						String display = results[0] + ", " + results[1].substring(0, 3); 
						
						infoWindowOptions.content(display);
						infoWindow.setOptions(infoWindowOptions);
						infoWindow.open(map,marker);	
						
					}
				});	
			
			}
		});
		
		map.addUIEventHandler(marker, UIEventType.dblclick, new UIEventHandler() {
			
			@Override
			public void handle(JSObject arg0) {
				LatLong ll = new LatLong((JSObject) arg0.getMember("latLng"));
				//TODO check if user inputs location manually that will ruin code
				
				
				//first user double click to set up connection to second dbclick
				if(startNorth.getText().equals("")&startEast.getText().equals("")) {
					startNorth.setText(format.format(ll.getLatitude()));
					startEast.setText(format.format(ll.getLongitude()));
					
				}else {
					endNorth.setText(format.format(ll.getLatitude()));
					endEast.setText(format.format(ll.getLongitude()));
					
					LatLong[] pair = new LatLong[]{new LatLong(Double.parseDouble(startNorth.getText()), Double.parseDouble(startEast.getText())),
													new LatLong(Double.parseDouble(endNorth.getText()), Double.parseDouble(endEast.getText()))};
					MVCArray mvcArray = new MVCArray(pair);
					PolylineOptions polylineOptions = new PolylineOptions().path(mvcArray).strokeColor("black").strokeWeight(1.5);
					Polyline polyline = new Polyline(polylineOptions);
					map.addMapShape((MapShape) polyline);
					startNorth.clear();
					startEast.clear();
					endEast.clear();
					endNorth.clear();
				}
				
			}
		});
		
		nodeList.getItems().add(new MapNode(point.getLatitude(),point.getLongitude()));
		nodes.setCellValueFactory(new PropertyValueFactory<>("id"));
		lat_list.setCellValueFactory(new PropertyValueFactory<>("latitude"));
		log_list.setCellValueFactory(new PropertyValueFactory<>("longitude"));
	}
	
	
	//default locations and pairs for nsfnet
	//i dont use these methods but i dont want to delete it just in case the file get deleted or somthing so i have this as back up
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
