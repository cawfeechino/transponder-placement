package main;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.MapReadyListener;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.MouseEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;



import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import metrics.TransponderMetric;
import models.EventHandlers;
import models.MapNode;
import netscape.javascript.JSObject;
import sun.net.www.MeteredStream;

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
	private Button addLocation, addConnection;
	
	private DecimalFormat format = new DecimalFormat("#0.0000");
	
	private GeocodingService geocodingService;
	
	private final String us = "United States";
	
	private ArrayList<LatLong> locations;
	
	private ArrayList<MapNode> connections;
	
	private HashMap<LatLong[], MapShape> mapShapes;
			
	private GoogleMap map;
	
	private ContextMenu markerMenu , mapMenu;
	
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
		
		locations = getLocations();
		
		mapShapes = new HashMap<>();
		
		//to add the location where user right clicks so can use on the menu item action events. 
		//TODO Double click is disable so need to add connection another way
		
		mapMenu = new ContextMenu();
		MenuItem addMarker = new MenuItem("Add Marker");
		MenuItem addEarthQuake = new MenuItem("Earth Quake");
		mapMenu.getItems().addAll(addMarker,addEarthQuake);
		
		markerMenu =new ContextMenu();
		MenuItem remove = new MenuItem("Remove");
		MenuItem connection = new MenuItem("Start Connection");
		markerMenu.getItems().addAll(remove, connection);

		
		// markers location icon
		for (LatLong point : locations) {
			//TODO fix the issue of the longitude having bigger decimal probably add the two decimal in file for more accurate location
			addMarker(point, map, infoWindow, infoWindowOptions);
		}	

		connections = getConnections();
		
		for(MapNode links : connections) {
			LatLong[] pair = new LatLong[] {links.getStart(), links.getEnd()};
			MVCArray mvcArray = new MVCArray(pair);
			
			PolylineOptions polylineOptions = new PolylineOptions().path(mvcArray).strokeColor("black").strokeWeight(2.5);
			Polyline polyline = new Polyline(polylineOptions);
			MapShape line = (MapShape) polyline;
					
			map.addMapShape(line);
			mapShapes.put(pair, line);
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
								addMarker(arg0.getLatLong(), map, infoWindow, infoWindowOptions);
							}
						});
						
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
		saveNodes.getItems().addAll(nodeList.getItems());
		columnNode.setCellValueFactory(new PropertyValueFactory<>("id"));
		columnlat.setCellValueFactory(new PropertyValueFactory<>("latitude"));
		columnlng.setCellValueFactory(new PropertyValueFactory<>("longitude"));
	
		saveLinks.getItems().addAll(connections);
		start.setCellValueFactory(new PropertyValueFactory<>("start"));
		end.setCellValueFactory(new PropertyValueFactory<>("end"));
		distance.setCellValueFactory(new PropertyValueFactory<>("distance"));

	}
	
	@FXML
	public void saveFile() {
		
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
	
	public ArrayList<LatLong> getLocations(){
		ArrayList<LatLong> locations = new ArrayList<>();
		String path;
		try {
			path = new File(".").getCanonicalPath().concat("/src/assets/location.txt");
			File locationsFile = new File(path);
			Scanner scanner = new Scanner(locationsFile);
			
			while(scanner.hasNext()) {
				
				String[] line = scanner.nextLine().split(" ");
				LatLong point = new LatLong(Double.parseDouble(line[0]), Double.parseDouble(line[1]));
				locations.add(point);
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return locations;
	}
	
	
	public ArrayList<MapNode> getConnections(){
		ArrayList<MapNode> connections = new ArrayList<>();
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
				LatLong start = new LatLong(Double.parseDouble(f[0]),Double.parseDouble(f[1]));
				LatLong end = new LatLong(Double.parseDouble(s[0]),Double.parseDouble(s[1]));
				connections.add(new MapNode(start, end, distance));
			}	
		} catch (IOException e) {
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
		marker.setTitle(format.format(point.getLatitude()) +  ", "+format.format(point.getLongitude()));
		map.addMarker(marker);

		map.addUIEventHandler(marker, UIEventType.click, EventHandlers.mapClick(map, geocodingService, infoWindowOptions, infoWindow, marker));
		
		map.addUIEventHandler(marker, UIEventType.rightclick, new UIEventHandler() {
			
			@Override
			public void handle(JSObject arg0) {
				
				gmap.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

					@Override
					public void handle(ContextMenuEvent event) {
						mapMenu.hide();
						markerMenu.setAnchorX(event.getSceneX());
						markerMenu.setAnchorY(event.getSceneY());
						markerMenu.getItems().get(0).setOnAction(new EventHandler<ActionEvent>() {
							
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
						
						markerMenu.getItems().get(1).setOnAction(value ->{
							MVCArray mvcArray = new MVCArray();
							mvcArray.setAt(0, point);
							PolylineOptions polylineOptions = new PolylineOptions().strokeColor("black").strokeWeight(2.5);
							Polyline polyline = new Polyline(polylineOptions);
							//map.addMouseEventHandler(marker, UIEventType.mousemove, EventHandlers.mouseMove(mvcArray, polyline, map));
						});
						
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
