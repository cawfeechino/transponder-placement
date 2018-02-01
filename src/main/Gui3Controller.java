package main;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.MapReadyListener;
import com.lynden.gmapsfx.javascript.JavascriptObject;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.MouseEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.geocoding.GeocoderStatus;
import com.lynden.gmapsfx.service.geocoding.GeocodingResult;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import com.lynden.gmapsfx.service.geocoding.GeocodingServiceCallback;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import metrics.TransponderMetric;
import models.EventHandlers;
import models.MapNode;
import netscape.javascript.JSObject;

// all form of distance are in meters NOTE*
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
	private TableColumn<MapNode, Integer> nodes,columnNode;

	@FXML
	private TableColumn<MapNode, Double> lat_list, log_list, columnlat, columnlng, distance;
	
	@FXML
	private TableColumn<MapNode, LatLong> start, end;
	
	@FXML
	private TableView<MapNode> nodeList, saveNodes, saveLinks;

	@FXML
	private TitledPane nodeEdit;
	
	@FXML
	private TextField lat, log, startNorth, startEast, endNorth, endEast;
	
	@FXML
	private Button addLocation, addConnection, clear;
	
	private DecimalFormat format = new DecimalFormat("#0.0000");
	
	private GeocodingService geocodingService;
	
	private final String us = "United States";
	
	//for default location
	private ArrayList<LatLong> locations;
	
	//for default connections
	private ArrayList<MapNode> connections;
	
	
	//when changes are made they will be added here
	
	//polylines
	private HashMap<LatLong[], MapShape> mapShapes;
	
	//markers
	private HashMap<LatLong, Marker> markers;
	
	//circles
	private HashMap<LatLong, Circle> earthquakes;
	
	private LatLong[] pair;
			
	private GoogleMap map;
	
	//one drop down when user right clicks on map another when click on marker
	private ContextMenu markerMenu , mapMenu;
	
	//default path for nsfnet topology
	private String path;
	
	HashMap <LatLong, Polyline> connection;
	
	@Override
	public void mapInitialized() {
		nodeEdit.setExpanded(true);
		geocodingService = new GeocodingService();
	
		MapOptions options = new MapOptions();
		options.center(new LatLong(38.873959, -98.517483)).zoomControl(true).zoom(4).overviewMapControl(false)
				.mapType(MapTypeIdEnum.ROADMAP).mapTypeControl(false).streetViewControl(false);
		

		gmap.setDisableDoubleClick(true);
		
		map = gmap.createMap(options);
		
		gmap.addMapReadyListener(new MapReadyListener() {
			
			@Override
			public void mapReady() {
				onMapReady();
			}
		});
		
	}
	
	
	
	public void onMapReady() {
		//toolip for the marker optional
		InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
		InfoWindow infoWindow = new InfoWindow();
		
		locations = new ArrayList<>();
		
		mapShapes = new HashMap<>();
		markers = new HashMap<>();
		earthquakes = new HashMap<>();
		connection = new HashMap<>();
		
		try {
			path = new File(".").getCanonicalPath().concat("/src/assets/locationConnections.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//to add the location where user right clicks so can use on the menu item action events. 
		//TODO Double click is disable so need to add connection another way
		
		mapMenu = new ContextMenu();
		
		MenuItem addMarker = new MenuItem("Add Marker");
		MenuItem addEarthQuake = new MenuItem("Earth Quake");
		mapMenu.getItems().addAll(addMarker,addEarthQuake);
		
		markerMenu =new ContextMenu();
		MenuItem addEarthQuake0 = new MenuItem("Earth Quake");
		MenuItem remove = new MenuItem("Remove");
		MenuItem connection = new MenuItem("Start Connection");
		markerMenu.getItems().addAll(addEarthQuake0, remove, connection);

		connections = getConnections(path);
		
		
		for(MapNode links : connections) {
			LatLong[] pair = new LatLong[] {links.getStart(), links.getEnd()};
			MVCArray mvcArray = new MVCArray(pair);
		
			locations.add(links.getStart());
			locations.add(links.getEnd());		
			
			PolylineOptions polylineOptions = new PolylineOptions().path(mvcArray).strokeColor("black").strokeWeight(2.5);
			Polyline polyline = new Polyline(polylineOptions);
			MapShape line = (MapShape) polyline;
					
			map.addMapShape(line);
			mapShapes.put(pair, line);	
			
		}	
			
		
		//make distinct list
		for(int i =0; i<locations.size()-1; i++) {
			for(int j=1; j<locations.size(); j++) {
				if(i!=j) {
					if(locations.get(i).getLatitude() == locations.get(j).getLatitude() & locations.get(i).getLongitude() == locations.get(j).getLongitude()) {
						locations.remove(j);
					}
				}
			}
		}
		
		//for some reason it doesnt compair the last two elements in the above for loop
		//TODO need to find a better solution 
		if(locations.get(locations.size()-2).getLatitude() == locations.get(locations.size()-1).getLatitude() & locations.get(locations.size()-2).getLongitude() ==locations.get(locations.size()-1).getLongitude()) {
			locations.remove(locations.size()-1);
		}
		
		for(LatLong point : locations) {
			addMarker(point, map, infoWindow, infoWindowOptions);
		}
		
		//add listener on table. once user click on locations on table. it adds to the location to the addition section
		nodeList.setOnMouseClicked(EventHandlers.tableListener(nodeList, startNorth, startEast, endNorth, endEast));		
		
		
		//add location on the map of where use rightclick 
		map.addMouseEventHandler(map,UIEventType.rightclick, new MouseEventHandler() {
			
			@Override
			public void handle(GMapMouseEvent arg0) {
				markerMenu.hide();
			
				gmap.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
	
					@Override
					public void handle(ContextMenuEvent event) {
						mapMenu.setAnchorX(event.getSceneX());
						mapMenu.setAnchorY(event.getSceneY());
						
						addMarker.setOnAction(new EventHandler<ActionEvent>() {
							
							@Override
							public void handle(ActionEvent event) {
								locations.add(arg0.getLatLong());
								addMarker(arg0.getLatLong(), map, infoWindow, infoWindowOptions);
							}
						});
						
						//add earthquake
						//to see if the marker falls in the earthquake data grab the center of the circle and find the distance between the marker and the center
						//Compare that to the radius
						//user is clicking on the marker so by default maker will be disabled. 
						
						addEarthQuake.setOnAction(new EventHandler<ActionEvent>() {
							
							@Override
							public void handle(ActionEvent event) {
								TextInputDialog dialog = new TextInputDialog("EarthQuake Info");
								dialog.setContentText("Please enter radius in kilometer: ");
								dialog.setTitle("EarthQuake Info");
								dialog.setHeaderText("Please Fill Out");
								
								CircleOptions co = new CircleOptions();
								co.strokeColor("brown")
								.fillColor("red")
								.center(arg0.getLatLong())
								.strokeWeight(2.0);
								Optional<String> results = dialog.showAndWait();
								results.ifPresent(distance ->  co.radius(Double.parseDouble(distance) * 1000));
								Circle cir = new Circle(co);
								
								map.addMapShape(cir);
								
							}
						});
						
						mapMenu.show(gmap, event.getSceneX(), event.getScreenY());
					}
				});
			}
		});		
	
		map.addMouseEventHandler(map, UIEventType.click, new MouseEventHandler() {
			
			@Override
			public void handle(GMapMouseEvent arg) {
				mapMenu.hide();
				markerMenu.hide();
				infoWindow.close();	
				
			}
		});
		
		clear.setOnAction(value->{
			map.clearMarkers();
			Iterator<MapShape> it = mapShapes.values().iterator();
			while(it.hasNext()) {
				map.removeMapShape(it.next());
			}
			
			nodeList.getItems().clear();
			saveNodes.getItems().clear();
			saveLinks.getItems().clear();
			connections.clear();
			
		});
		
					
		//manually add stuff
		//TODO make sure it is a valid entry
		addLocation.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				String nlat = format.format(Double.parseDouble(lat.getText()));
				String nlng = format.format(Double.parseDouble(log.getText()));
				LatLong newPoint = new LatLong(Double.parseDouble(nlat), Double.parseDouble(nlng));
				
					addMarker(newPoint, map, infoWindow, infoWindowOptions);
			}
		});
		
		addConnection.setOnAction(EventHandlers.addConnection(map, nodeList, startNorth, startEast, endNorth, endEast));

					
	}
	

	@FXML
	public void displaySave() {
		saveNodes.getItems().clear();
		saveNodes.getItems().addAll(nodeList.getItems());
		columnNode.setCellValueFactory(new PropertyValueFactory<>("id"));
		columnlat.setCellValueFactory(new PropertyValueFactory<>("latitude"));
		columnlng.setCellValueFactory(new PropertyValueFactory<>("longitude"));
	
		saveLinks.getItems().clear();
		saveLinks.getItems().addAll(connections);
		start.setCellValueFactory(new PropertyValueFactory<>("start"));
		end.setCellValueFactory(new PropertyValueFactory<>("end"));
		distance.setCellValueFactory(new PropertyValueFactory<>("distance"));

	}
	
	@FXML
	public void saveFile() throws IOException {
		FileChooser chooser = new FileChooser();
		ExtensionFilter filter = new ExtensionFilter("TXT files (*.txt)", "*.txt");
		chooser.setTitle("NSFNet Data");
		chooser.getExtensionFilters().add(filter);
		File file = chooser.showSaveDialog(Gui3Main.getPrimaryStage());
		writeConnections(connections, file);
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
	
	
	public ArrayList<MapNode> getConnections(String location){
		ArrayList<MapNode> connections = new ArrayList<>();
		try {
			File locationsFile = new File(location);
			Scanner scanner = new Scanner(locationsFile);
			
			while(scanner.hasNext()) {
				
				String[] line = scanner.nextLine().split(" ");
				String[] f = line[0].split(",");
				String[] s = line[1].split(",");
				double distance = Double.parseDouble(line[2]);
				LatLong start = new LatLong(Double.parseDouble(f[0]),Double.parseDouble(f[1]));
				LatLong end = new LatLong(Double.parseDouble(s[0]),Double.parseDouble(s[1]));
				connections.add(new MapNode(start, end, distance));
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return connections;
	}
		
	public void writeConnections(ArrayList<MapNode> connections, File file) {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(file));
			for (MapNode pair : connections) {
				LatLong start = pair.getStart();
				LatLong end = pair.getEnd();
				double distance = pair.getDistance();
				out.println(start.getLatitude() +','+start.getLongitude()+" "
							+ end.getLatitude()+','+end.getLongitude()+" "
							+ distance);
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
		marker.setTitle(format.format(point.getLatitude()) +  ", "+format.format(point.getLongitude()));
		map.addMarker(marker);
		markers.put(point, marker);
		
		
		
		map.addUIEventHandler(marker, UIEventType.click, h ->{
			geocodingService.reverseGeocode(point.getLatitude(), point.getLongitude(), new GeocodingServiceCallback() {
				
				@Override
				public void geocodedResultsReceived(GeocodingResult[] arg0, GeocoderStatus arg1) {		
					
					String[] results = arg0[arg0.length-3].getFormattedAddress().split(",");
					
					String display = results[0] + ", " + results[1].substring(0, 3); 
					
					infoWindowOptions.content(display);
					infoWindow.setOptions(infoWindowOptions);
					infoWindow.open(map,marker);	
					
				}
			});	
		});
		
//		map.addUIEventHandler(marker, UIEventType.click, EventHandlers.mapClick(map, geocodingService, infoWindowOptions, infoWindow, marker));
		
		
		map.addUIEventHandler(marker, UIEventType.rightclick, new UIEventHandler() {
			
			@Override
			public void handle(JSObject arg0) {
				
				
				gmap.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

					@Override
					public void handle(ContextMenuEvent event) {
						mapMenu.hide();
						markerMenu.setAnchorX(event.getSceneX());
						markerMenu.setAnchorY(event.getSceneY());
						marker.setAnimation(Animation.DROP);		
						
						
						//add earthquake
						//to see if the marker falls in the earthquake data grab the center of the circle and find the distance between the marker and the center
						//Compare that to the radius
						//user is clicking on the marker so by default maker will be disabled. 
						markerMenu.getItems().get(0).setOnAction(value -> {
							TextInputDialog dialog = new TextInputDialog("EarthQuake Info");
							dialog.setContentText("Please enter radius in kilometer: ");
							dialog.setTitle("EarthQuake Info");
							dialog.setHeaderText("Please Fill Out");
							
							CircleOptions co = new CircleOptions();
							co.strokeColor("brown")
							.fillColor("red")
							.center(point)
							.strokeWeight(2.0);
							Optional<String> results = dialog.showAndWait();
							results.ifPresent(distance ->  co.radius(Double.parseDouble(distance) * 1000));
							Circle cir = new Circle(co);
							
							map.addMapShape(cir);
							earthquakes.put(point, cir);
						});
						
						
						//remove marker
						markerMenu.getItems().get(1).setOnAction(new EventHandler<ActionEvent>() {
							
							@Override
							public void handle(ActionEvent event) {
								map.removeMarker(marker);
								//if a marker is deleted so does its links 
								for(LatLong[] search : mapShapes.keySet()) {
									for(LatLong distory : search) {
										if(distory.getLatitude() == point.getLatitude() && distory.getLongitude() == point.getLongitude()) {
											map.removeMapShape(mapShapes.get(search));
										}
									}
								}
							}
						});
						
						
						//connections
						if(markerMenu.getItems().get(2).getText().equals("Start Connection")) {
							markerMenu.getItems().get(2).setOnAction(value ->{
								MVCArray mvcArray = new MVCArray();
								PolylineOptions polylineOptions = new PolylineOptions().strokeColor("black").strokeWeight(2.5);
								Polyline polyline = new Polyline(polylineOptions);
								connection.put(point, polyline);
								mvcArray.setAt(0, point);
								map.addMapShape(polyline);	
								markerMenu.getItems().get(2).setText("End Connection");
								
								map.addUIEventHandler(UIEventType.mousemove, h->{
									mvcArray.setAt(1, new LatLong((JSObject) h.getMember("latLng")));
									polyline.setPath(mvcArray);
								});
								
							});	
						}else {
							markerMenu.getItems().get(2).removeEventHandler(ActionEvent.ACTION, markerMenu.getItems().get(2).getOnAction());
							
							markerMenu.getItems().get(2).setOnAction(value->{
								map.removeMapShape((MapShape) connection.values().toArray()[0]);
								markerMenu.getItems().get(2).setText("Start Connection");
								
								LatLong[] key = new LatLong[] {connection.keySet().iterator().next(), point};
								MVCArray mvcArray = new MVCArray(key);
								PolylineOptions polylineOptions = new PolylineOptions().strokeColor("black").strokeWeight(2.5).path(mvcArray);
								Polyline polyline = new Polyline(polylineOptions);
								map.addMapShape(polyline);
								
								 
								mapShapes.put(key, (MapShape) connection.values().toArray()[0]);
								System.out.println(mapShapes.get(key));
								connection.clear();
							});
						}
						
						markerMenu.show(gmap, event.getSceneX()+40, event.getSceneY()+100);
					}
				});
			}
		});
		
//		map.addUIEventHandler(marker, UIEventType.dblclick, EventHandlers.mapDblCkick(map, startNorth, startEast, endNorth, endEast));
		
		nodeList.getItems().add(new MapNode(point.getLatitude(),point.getLongitude()));
		nodes.setCellValueFactory(new PropertyValueFactory<>("id"));
		lat_list.setCellValueFactory(new PropertyValueFactory<>("latitude"));
		log_list.setCellValueFactory(new PropertyValueFactory<>("longitude"));
	}	

}
