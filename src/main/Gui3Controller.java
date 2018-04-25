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
import com.lynden.gmapsfx.shapes.MapShapeOptions;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import com.lynden.gmapsfx.util.MarkerImageFactory;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
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
	ObservableList<String> dynSimConfirm = FXCollections.observableArrayList("Yes", "No");

	private String trafficMethod;
	private String routingMethod;
	private String dynSimMaxTime;
	private String dynSimChoice;
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
	
	@FXML
	private MenuItem upload;
	
	private DecimalFormat format = new DecimalFormat("#0.0000");
	
	private GeocodingService geocodingService;
	
	private final String us = "US";
	
	//for default location
	private ArrayList<LatLong> locations, connection;
	
	//for default connections
	private ArrayList<MapNode> connections;
	
	//when changes are made they will be added here
	
	//polylines
	private HashMap<LatLong[], Polyline> polylines;
	
	//markers
	private HashMap<LatLong, Marker> markers;
	
	//circles
	private HashMap<LatLong, Circle> earthquakes;
				
	private GoogleMap map;
	
	//one drop down when user right clicks on map another when click on marker
	private ContextMenu markerMenu , mapMenu,earthquakmenu;
	
	
	//default path for nsfnet topology
	private String path;
	
		
	@Override
	public void mapInitialized() {
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
		
		nodeEdit.setExpanded(true);
		
		locations = new ArrayList<>();			
		polylines = new HashMap<>();
		markers = new HashMap<>();
		earthquakes = new HashMap<>();
		connection = new ArrayList<>();
		
		
		try {
			if(path == null) {
				System.out.println(this.getClass().getResource("/resources/enableNode.png"));
				System.out.println(this.getClass().getResource("/assets/locationConnections.txt"));
				path = new File(".").getCanonicalPath().concat("/src/assets/locationConnections.txt");
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//to add the location where user right clicks so can use on the menu item action events. 		
		mapMenu = new ContextMenu();
		
		MenuItem addMarker = new MenuItem("Add Marker");
		MenuItem addEarthQuake = new MenuItem("Earth Quake");
		mapMenu.getItems().addAll(addMarker,addEarthQuake);
		
		markerMenu =new ContextMenu();
		MenuItem addEarthQuake0 = new MenuItem("Earth Quake");
		MenuItem remove = new MenuItem("Remove");
		MenuItem connection = new MenuItem("Start Connection");
		markerMenu.getItems().addAll(addEarthQuake0, remove, connection);
		
		earthquakmenu = new ContextMenu();
		MenuItem removeE = new MenuItem("Remove");
		earthquakmenu.getItems().add(removeE);
		
		
		upload.setOnAction(e->{
			FileChooser chooser = new FileChooser();
			ExtensionFilter filter = new ExtensionFilter("TXT files (*.txt)", "*.txt");
			chooser.setTitle("NSFNet Data");
			chooser.getExtensionFilters().add(filter);
			File f = chooser.showOpenDialog(Gui3Main.getPrimaryStage());
			if(f != null) {
				clear.fire();
				path = f.getAbsolutePath();
				onMapReady();
			}
		});
		
		connections = getConnections(path);
		
		
		//TODO if it is already in list dont add
		for(MapNode links : connections) {
			LatLong[] pair = new LatLong[] {links.getStart(), links.getEnd()};
			MVCArray mvcArray = new MVCArray(pair);
		
			locations.add(links.getStart());
			locations.add(links.getEnd());		
			
			PolylineOptions polylineOptions = new PolylineOptions().path(mvcArray).strokeColor("black").strokeWeight(2.5);
			Polyline polyline = new Polyline(polylineOptions);
			
			polyline.getJSObject().setMember("getPolylineOptions", polylineOptions);
					
			map.addMapShape(polyline);
			polylines.put(pair, polyline);	
			
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
		
		//for some reason it doesnt compare the last two elements in the above for loop
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
				
				LatLong ll = arg0.getLatLong();
				
				markerMenu.hide();
				earthquakmenu.hide();
				gmap.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
	
					@Override
					public void handle(ContextMenuEvent event) {
						
						addMarker.setOnAction(new EventHandler<ActionEvent>() {
							
							@Override
							public void handle(ActionEvent event) {
								locations.add(ll);
								addMarker(ll, map, infoWindow, infoWindowOptions);
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
								.center(ll)
								.strokeWeight(2.0);
								Optional<String> results = dialog.showAndWait();
								results.ifPresent(distance -> {
									co.radius(Double.parseDouble(distance) * 1000.0);
								});
								
								Circle cir = new Circle(co);
								
								earthquakes.put(ll, cir);
							
								map.addMapShape(cir);
								
								map.addUIEventHandler(cir, UIEventType.rightclick, h->{
									
									mapMenu.hide();
									markerMenu.hide();
									
									
									gmap.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

										@Override
										public void handle(ContextMenuEvent event) {
											
											earthquakmenu.getItems().get(0).setOnAction(action ->{
												map.removeMapShape(cir);
												earthquakes.remove(ll);
											});
											earthquakmenu.show(gmap, event.getScreenX(), event.getScreenY());
										}
										
									});
								
									
								});
								
								//if marker falls in radius of earthquake
								
								for(LatLong ll : markers.keySet()) {
									if(ll.distanceFrom(cir.getCenter()) <= (int) cir.getJSObject().getMember("radius") ) {
										for(LatLong[] search : polylines.keySet()) {
											for(LatLong distory : search) {
												if(distory.getLatitude() == ll.getLatitude() && distory.getLongitude() == ll.getLongitude()) {
													PolylineOptions options = new PolylineOptions().strokeColor("red").strokeWeight(2.5).path(polylines.get(search).getPath());
													Polyline p = new Polyline(options);
													
													 map.removeMapShape(polylines.get(search));		
													 map.addMapShape(p);
													 polylines.replace(search, p);
												}
											}
										}
										
									}
								}								
							}
						});
						
						
						mapMenu.show(gmap, event.getScreenX(), event.getScreenY());
					}
				});
				  
				geocodingService.reverseGeocode(ll.getLatitude(), ll.getLongitude(), new GeocodingServiceCallback() {
					
					@Override
					public void geocodedResultsReceived(GeocodingResult[] arg0, GeocoderStatus arg1) {
						if(!arg0[arg0.length-1].getAddressComponents().get(0).getShortName().equals(us)) {
							mapMenu.hide();
						}
						
					}
				});
			}
		});		
	
		map.addMouseEventHandler(map, UIEventType.click, new MouseEventHandler() {
			
			@Override
			public void handle(GMapMouseEvent arg) {
				mapMenu.hide();
				markerMenu.hide();
				earthquakmenu.hide();
				infoWindow.close();	
			}
		});
		
		clear.setOnAction(value->{
			map.clearMarkers();
			Iterator<Polyline> it = polylines.values().iterator();
			while(it.hasNext()) {
				map.removeMapShape(it.next());
			}
			Iterator<Circle> circles = earthquakes.values().iterator();
			while(circles.hasNext()) {
				map.removeMapShape(circles.next());
			}
			
			//delete all save data
			nodeList.getItems().clear();
			saveNodes.getItems().clear();
			saveLinks.getItems().clear();
			connections.clear();
			locations.clear();
			polylines.clear();
			markers.clear();
			earthquakes.clear();
			MapNode.reset();
			
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
    private ComboBox<String> dynSimConfBox;
	
	@FXML
	private void dynSimConfChoice() {
		dynSimChoice = dynSimConfBox.getValue().toString().equals("Yes") ? "yes" : "no";
		System.out.println(dynSimChoice);
	}

    @FXML
    private Label dynSimConfLabel;

    @FXML
    private Label dynSimTimeLabel;

    @FXML
    private TextField dynSimTimeText;
    
    @FXML
    private void dynSimTimeRetrieve() {
    	dynSimMaxTime = dynSimTimeText.getText();
    }
	
    @FXML
	private ComboBox<String> routingMethodBox;

	@FXML
	private void routingMethodChoice() {
		if (!routingMethodBox.getValue().toString().equals("--select a routing method--")) {
			routingMethod = routingMethodBox.getValue().toString();
		}
		if(routingMethodBox.getValue().toString().equals("SPF") || routingMethodBox.getValue().toString().equals("LUF") || routingMethodBox.getValue().toString().equals("MUF")) {
			dynSimConfBox.setVisible(true);
			dynSimConfLabel.setVisible(true);
			dynSimTimeLabel.setVisible(true);
			dynSimTimeText.setVisible(true);
			dynSimChoice = dynSimConfBox.getValue().toString().toLowerCase();
		}
		if(!routingMethodBox.getValue().toString().equals("SPF") && !routingMethodBox.getValue().toString().equals("LUF") && !routingMethodBox.getValue().toString().equals("MUF")) {
			dynSimConfBox.setVisible(false);
			dynSimConfLabel.setVisible(false);
			dynSimTimeLabel.setVisible(false);
			dynSimTimeText.setVisible(false);
			dynSimChoice = "no";
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
				
				String[] args = { trafficMethod.toUpperCase(), routingMethod, dynSimChoice, dynSimMaxTime };
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
				stage.setTitle("Simulator Results - Transponder");
				stage.setScene(new Scene(root));
				stage.show();
				
				FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("Graph2.fxml"));
				Parent root2 = (Parent) fxmlLoader2.load();
				Stage stage2 = new Stage();
				stage2.setTitle("Simulator Results - Drop");
				stage2.setScene(new Scene(root2));
				stage2.show();
				
				FXMLLoader fxmlLoader3 = new FXMLLoader(getClass().getResource("Graph3.fxml"));
				Parent root3 = (Parent) fxmlLoader3.load();
				Stage stage3 = new Stage();
				stage3.setTitle("Simulator Results - Utilization");
				stage3.setScene(new Scene(root3));
				stage3.show();
				
				FXMLLoader fxmlLoader4 = new FXMLLoader(getClass().getResource("Graph4.fxml"));
				Parent root4 = (Parent) fxmlLoader4.load();
				Stage stage4 = new Stage();
				stage4.setTitle("Simulator Results - Drops per Second");
				stage4.setScene(new Scene(root4));
				stage4.show();
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
		dynSimConfBox.setItems(dynSimConfirm);
		dynSimConfBox.getSelectionModel().select(0);
		dynSimConfBox.setVisible(false);
		dynSimConfLabel.setVisible(false);
		dynSimTimeLabel.setVisible(false);
		dynSimTimeText.setText("1000");
		dynSimTimeText.setVisible(false);
		dynSimMaxTime = "1000";
		dynSimChoice = "yes";
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
				out.println(format.format(start.getLatitude()) +','+ format.format(start.getLongitude())+" "
							+ format.format(end.getLatitude())+','+format.format(end.getLongitude())+" "
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
		
		map.addUIEventHandler(marker, UIEventType.rightclick, new UIEventHandler() {
			
			@Override
			public void handle(JSObject arg0) {
				
				
				gmap.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

					@Override
					public void handle(ContextMenuEvent event) {
						mapMenu.hide();
						earthquakmenu.hide();
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
							
							for(LatLong ll : markers.keySet()) {
								if(ll.distanceFrom(cir.getCenter()) <= (int) cir.getJSObject().getMember("radius") ) {
									System.out.println("hit");
									for(LatLong[] search : polylines.keySet()) {
										for(LatLong distory : search) {
											if(distory.getLatitude() == ll.getLatitude() && distory.getLongitude() == ll.getLongitude()) {
												PolylineOptions po = ((PolylineOptions) polylines.get(search).getJSObject().getMember("getPolylineOptions")).strokeColor("red") ;
												Polyline p = new Polyline(po);
												 map.removeMapShape(polylines.get(search));		
												 map.addMapShape(p);
												 polylines.replace(search, p);
											}
										}
									}
									
								}
							}
										
						});
						
						
						//remove marker
						markerMenu.getItems().get(1).setOnAction(new EventHandler<ActionEvent>() {
							
							@Override
							public void handle(ActionEvent event) {
								map.removeMarker(marker);
								markers.remove(point);
								//if a marker is deleted so does its links 
								
								ArrayList<LatLong[]> toRemove = new ArrayList<>();
								for(LatLong[] search : polylines.keySet()) {
									for(LatLong distory : search) {
										if(distory.getLatitude() == point.getLatitude() && distory.getLongitude() == point.getLongitude()) {
											 map.removeMapShape(polylines.get(search));	
											 toRemove.add(search);
										}
									}
								}
								
								for(LatLong[] line : toRemove) {
									polylines.remove(line);
								}
								
								
							}
						});
						
						//connections							
							markerMenu.getItems().get(2).setOnAction(value ->{
								
								if(markerMenu.getItems().get(2).getText().equals("Start Connection")) {
									connection.add(point);
									markerMenu.getItems().get(2).setText("End Connection");
									
								}else {
									markerMenu.getItems().get(2).setText("Start Connection");
									LatLong start = connection.get(0);
									LatLong[] key = new LatLong[] {start, point};
									
									if(!connection.get(0).equals(point)) {
										PolylineOptions polylineOptions = new PolylineOptions().strokeColor("black").strokeWeight(2.5);
										Polyline polyline = new Polyline(polylineOptions);
										MVCArray pair = new MVCArray();
										pair.setAt(0, key[0]);
										pair.setAt(1, key[1]);
						
										polyline.setPath(pair);
										
										map.addMapShape(polyline);
										polyline.getJSObject().setMember("getPolylineOptions", polylineOptions);
										
										polylines.put(key, polyline);
										connections.add(new MapNode(start, point, start.distanceFrom(point)/1000));
										connection.clear();
									}else {
										connection.clear();
									}
									
								}
						
							});	
						
						markerMenu.show(gmap, event.getScreenX(), event.getScreenY());
					}
				});
			}
		});
				
		nodeList.getItems().add(new MapNode(point.getLatitude(),point.getLongitude()));
		nodes.setCellValueFactory(new PropertyValueFactory<>("id"));
		lat_list.setCellValueFactory(new PropertyValueFactory<>("latitude"));
		log_list.setCellValueFactory(new PropertyValueFactory<>("longitude"));
	}	

}
