package models;

import java.text.DecimalFormat;
import java.util.List;

import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.MouseEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapShape;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.service.geocoding.GeocoderStatus;
import com.lynden.gmapsfx.service.geocoding.GeocodingResult;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import com.lynden.gmapsfx.service.geocoding.GeocodingServiceCallback;
import com.lynden.gmapsfx.shapes.MapShapeOptions;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import netscape.javascript.JSObject;

public class EventHandlers {

	private static DecimalFormat format = new DecimalFormat("#0.0000");	
	
	//adding a listener for the table
		public static EventHandler<MouseEvent> tableListener(TableView<MapNode> nodeList, TextField startNorth, TextField startEast,  TextField endNorth, TextField endEast) {
			EventHandler<MouseEvent> click = new EventHandler<MouseEvent>() {

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
				
			};
			
			return click;
		}	
	
	public static UIEventHandler mapClick(GoogleMap map, GeocodingService geocodingService, InfoWindowOptions infoWindowOptions, 
			InfoWindow infoWindow, Marker marker) {
		UIEventHandler click = new UIEventHandler() {
			
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
		};
		return click;
	}
	
	public static UIEventHandler mapDblCkick(GoogleMap map, TextField startNorth, TextField startEast,  TextField endNorth, TextField endEast) {
		return new UIEventHandler() {
			
			@Override
			public void handle(JSObject arg0) {
				LatLong ll = new LatLong((JSObject) arg0.getMember("latLng"));
				//TODO check if user inputs location manually that will ruin code
				
				//TODO add to connections list
				
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
					PolylineOptions polylineOptions = new PolylineOptions().path(mvcArray).strokeColor("black").strokeWeight(2.0);
					Polyline polyline = new Polyline(polylineOptions);
					map.addMapShape((MapShape) polyline);
					startNorth.clear();
					startEast.clear();
					endEast.clear();
					endNorth.clear();
				}
			}
		};
	}
	
	//manual add. base on user input on textfield
	
	public static EventHandler<ActionEvent> addConnection(GoogleMap map, TableView<MapNode> nodeList, TextField startNorth, TextField startEast,  TextField endNorth, TextField endEast){
		EventHandler<ActionEvent> action = new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				boolean valid = false;
				LatLong[] pair = new LatLong[]{new LatLong(Double.parseDouble(startNorth.getText()), Double.parseDouble(startEast.getText())),
												new LatLong(Double.parseDouble(endNorth.getText()), Double.parseDouble(endEast.getText()))};
				
				
				List<MapNode> items = nodeList.getItems();
				
//this is to see the info get from text fields
				
//				for(int i = 0; i <items.size(); i++) {
//					boolean r = items.get(i).getLatitude()==pair[0].getLatitude();
//					System.out.println(items.get(i).getLatitude() + " = " + pair[0].getLatitude() +" "+ r);
//					r=format.format(items.get(i).getLongitude()).equals(format.format(pair[0].getLongitude()));
//					System.out.println(items.get(i).getLongitude() + " = " + format.format(pair[0].getLongitude()) +" "+ r);
//					r = items.get(i).getLatitude()==pair[1].getLatitude();
//					System.out.println(items.get(i).getLatitude() + " = " + pair[1].getLatitude() +" "+ r);
//					r=format.format(items.get(i).getLongitude()).equals(format.format(pair[1].getLongitude()));
//					System.out.println(items.get(i).getLongitude() + " = " + format.format(pair[1].getLongitude()) +" "+ r);
//					
//				}
				
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
					}else {
						valid=false;
					}
				}
				
				
				if(valid) {
					MVCArray mvcArray = new MVCArray(pair);
					PolylineOptions polylineOptions = new PolylineOptions().path(mvcArray).strokeColor("black").strokeWeight(2.0);
					Polyline polyline = new Polyline(polylineOptions);
					MapShape line = (MapShape) polyline;
					map.addMapShape(line);
					map.addUIEventHandler(line, UIEventType.click, new UIEventHandler() {
						
						@Override
						public void handle(JSObject arg0) {
							map.removeMapShape(line);
						}
					});
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
		};
		
		return action;
	}
	
	
	public MouseEventHandler rightClick() {
		MouseEventHandler rc = new MouseEventHandler() {
			
			@Override
			public void handle(GMapMouseEvent arg0) {
				
			}
		};
		
		return rc;
	}
	
	public static MouseEventHandler mouseMove(MVCArray mvcArray, Polyline polyline, GoogleMap map) {
		return new MouseEventHandler() {
			
			@Override
			public void handle(GMapMouseEvent h) {
				mvcArray.setAt(1, h.getLatLong());
				polyline.setPath(mvcArray);
				map.addMapShape(polyline);	
			}
		};
		
	}
		
	
	public EventHandler<ContextMenuEvent> jsObjectContextMenuEvent(){
		EventHandler<ContextMenuEvent> event = new EventHandler<ContextMenuEvent>() {
			
			@Override
			public void handle(ContextMenuEvent event) {
				
			}
		};
		
		return event;		
	}
	
	
	
		

}
