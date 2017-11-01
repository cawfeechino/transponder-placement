package main;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.service.directions.Distance;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;

public class Gui3Controller implements Initializable, MapComponentInitializedListener {
	
	ObservableList<String> trafficMethodList = FXCollections.observableArrayList("-select a traffic request method--", "random", "gaussian", "uniform");
	ObservableList<String> routingMethodList = FXCollections.observableArrayList("--select a routing method--", "SPF", "LUF", "MUF", "OPT", "MUX", "Hybrid");
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
		
		//lat: 38.873959 lng: -98.517483 zoom:4
		options.center(new LatLong(38.873959, -98.517483)).zoomControl(true).zoom(4).overviewMapControl(false)
				.mapType(MapTypeIdEnum.ROADMAP);
		
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
		
		
		LatLong[] sdANDpalo = new LatLong[] {sd,palo};
		MVCArray mvcSdANDpalo = new MVCArray(sdANDpalo);
		
		LatLong[] sdANDsea = new LatLong[] {sd,seattle};
		MVCArray mvcsdANDsea = new MVCArray(sdANDsea);
		
		LatLong[] paloANDsea = new LatLong[] {seattle,palo};
		MVCArray mvcPaloANDSea = new MVCArray(paloANDsea);
		
		LatLong[] paloANDslc = new LatLong[] {slc,palo};
		MVCArray mvcPaloANDslc = new MVCArray(paloANDslc);
		
		LatLong[] seattleANDchampaign = new LatLong[] {seattle,champaign};
		MVCArray mvcSeattleANDchampaign = new MVCArray(seattleANDchampaign);
		
		LatLong[] sdANDhouston = new LatLong[] {sd,houston};
		MVCArray mvcSdANDhouston = new MVCArray(sdANDhouston);
		
		LatLong[] slcANDboulder = new LatLong[] {slc,boulder};
		MVCArray mvcSlcANDboulder = new MVCArray(slcANDboulder);
		
		LatLong[] slcANDannArbor = new LatLong[] {slc,annArbor};
		MVCArray mvcSlcANDannArbor = new MVCArray(slcANDannArbor);
		
		LatLong[] boulderANDhouston = new LatLong[] {boulder,houston};
		MVCArray mvcBoulderANDhouston = new MVCArray(boulderANDhouston);
		
		LatLong[] boulderANDlincoln = new LatLong[] {boulder,lincoln};
		MVCArray mvcBoulderANDlincoln = new MVCArray(boulderANDlincoln);
		
		LatLong[] lincolnANDchampaign = new LatLong[] {lincoln,champaign};
		MVCArray mvcLincolnANDchampaign = new MVCArray(lincolnANDchampaign);
		
		LatLong[] champaignANDpittsburgh = new LatLong[] {champaign,pittsburgh};
		MVCArray mvcChampaignANDpittsburgh = new MVCArray(champaignANDpittsburgh);
		
		LatLong[] houstonANDatlnta = new LatLong[] {houston,atlanta};
		MVCArray mvcHoustonANDatlnta = new MVCArray(houstonANDatlnta);

		LatLong[] houstonANDcollegePk = new LatLong[] {houston,collegePk};
		MVCArray mvcHoustonANDcollegePk = new MVCArray(houstonANDcollegePk);

		LatLong[] atlantaANDpittsburgh = new LatLong[] {atlanta,pittsburgh};
		MVCArray mvcAtlantaANDpittsburgh = new MVCArray(atlantaANDpittsburgh);
		
		LatLong[] annArborANDprinceton = new LatLong[] {annArbor,princeton};
		MVCArray mvcAnnArborANDprinceton = new MVCArray(annArborANDprinceton);

		LatLong[] annArborANDithica = new LatLong[] {annArbor,ithaca};
		MVCArray mvcAnnArborANDithica = new MVCArray(annArborANDithica);

		LatLong[] pittsburghANDithaca = new LatLong[] {pittsburgh,ithaca};
		MVCArray mvcPittsburghANDithaca = new MVCArray(pittsburghANDithaca);

		LatLong[] pittsburghANDprinceton = new LatLong[] {pittsburgh,princeton};
		MVCArray mvcPittsburghANDprinceton= new MVCArray(pittsburghANDprinceton);
		
		LatLong[] collegePkANDithaca = new LatLong[] {collegePk,ithaca};
		MVCArray mvcCollegePkANDithaca= new MVCArray(collegePkANDithaca);
		
		LatLong[] collegePkANDprinceton = new LatLong[] {collegePk,princeton};
		MVCArray mvcCollegePkANDprinceton= new MVCArray(collegePkANDprinceton);
		
		
		//markers location icon
		
		MarkerOptions sdOption = new MarkerOptions();
		sdOption.position(sd);
		Marker sdMarker = new Marker(sdOption);
		
		MarkerOptions paloOption = new MarkerOptions();
		paloOption.position(palo);
		Marker paloMarker = new Marker(paloOption);
		
		MarkerOptions seattleOption = new MarkerOptions();
		seattleOption.position(seattle);
		Marker seattleMarker = new Marker(seattleOption);
		
		MarkerOptions slcOption = new MarkerOptions();
		slcOption.position(slc);
		Marker slcMarker = new Marker(slcOption);
		
		MarkerOptions boulderOption = new MarkerOptions();
		boulderOption.position(boulder);
		Marker boulderMarker = new Marker(boulderOption);
		
		MarkerOptions lincolnOption = new MarkerOptions();
		lincolnOption.position(lincoln);
		Marker lincolnMarker = new Marker(lincolnOption);
		
		MarkerOptions houstonOption = new MarkerOptions();
		houstonOption.position(houston);
		Marker houstonMarker = new Marker(houstonOption);
		
		MarkerOptions champaignOption = new MarkerOptions();
		champaignOption.position(champaign);
		Marker champaignMarker = new Marker(champaignOption);
		
		MarkerOptions annArborOption = new MarkerOptions();
		annArborOption.position(annArbor);
		Marker annArborMarker = new Marker(annArborOption);
		
		MarkerOptions pittsburghOption = new MarkerOptions();
		pittsburghOption.position(pittsburgh);
		Marker pittsburghMarker = new Marker(pittsburghOption);
		
		MarkerOptions ithacaOption = new MarkerOptions();
		ithacaOption.position(ithaca);
		Marker ithacaMarker = new Marker(ithacaOption);
		
		MarkerOptions collegePkOption = new MarkerOptions();
		collegePkOption.position(collegePk);
		Marker collegePkMarker = new Marker(collegePkOption);
		
		MarkerOptions princetonOption = new MarkerOptions();
		princetonOption.position(princeton);
		Marker princetonMarker = new Marker(princetonOption);
		
		MarkerOptions atlantaOption = new MarkerOptions();
		atlantaOption.position(atlanta);
		Marker atlantaMarker = new Marker(atlantaOption);
		
//		ArrayList<Marker> markers = new ArrayList<>();
//		markers.add(Collections.addAll(c, elements)l(sdMarker,paloMarker));
		
		
		
		
		PolylineOptions sdTOpaloOpts = new PolylineOptions().path(mvcSdANDpalo).strokeColor("black").strokeWeight(2);
		Polyline sdTOPalo = new Polyline(sdTOpaloOpts);
		
		PolylineOptions sdTOseattleOpts = new PolylineOptions().path(mvcsdANDsea).strokeColor("black").strokeWeight(2);
		Polyline sdTOseattle = new Polyline(sdTOseattleOpts);
		
		PolylineOptions sdTOhoustonOpts = new PolylineOptions().path(mvcSdANDhouston).strokeColor("black").strokeWeight(2);
		Polyline sdTOhouston = new Polyline(sdTOhoustonOpts);
		
		PolylineOptions paloTOSeattleOpts = new PolylineOptions().path(mvcPaloANDSea).strokeColor("black").strokeWeight(2);
		Polyline paloTOseattle = new Polyline(paloTOSeattleOpts);
		
		PolylineOptions paloTOslcOpts = new PolylineOptions().path(mvcPaloANDslc).strokeColor("black").strokeWeight(2);
		Polyline paloTOslc = new Polyline(paloTOslcOpts);
		
		PolylineOptions seattleTOchampaignOpts = new PolylineOptions().path(mvcSeattleANDchampaign).strokeColor("black").strokeWeight(2);
		Polyline seattleTOchampaign = new Polyline(seattleTOchampaignOpts);

		PolylineOptions slcTOboulderOpts = new PolylineOptions().path(mvcSlcANDboulder).strokeColor("black").strokeWeight(2);
		Polyline slcTOboulder = new Polyline(slcTOboulderOpts);
		
		PolylineOptions slcTOannArborOpts = new PolylineOptions().path(mvcSlcANDannArbor).strokeColor("black").strokeWeight(2);
		Polyline slcTOannArbor = new Polyline(slcTOannArborOpts);
		
		PolylineOptions boulderTOhoustonOpts = new PolylineOptions().path(mvcBoulderANDhouston).strokeColor("black").strokeWeight(2);
		Polyline boulderTOhouston = new Polyline(boulderTOhoustonOpts);
		
		PolylineOptions boulderTOlincolnOpts = new PolylineOptions().path(mvcBoulderANDlincoln).strokeColor("black").strokeWeight(2);
		Polyline boulderTOlincoln = new Polyline(boulderTOlincolnOpts);
		
		PolylineOptions houstonTOcollegePkOpts = new PolylineOptions().path(mvcHoustonANDcollegePk).strokeColor("black").strokeWeight(2);
		Polyline houstonTOcollegePk = new Polyline(houstonTOcollegePkOpts);

		PolylineOptions houstonTOAtlantaOpts = new PolylineOptions().path(mvcHoustonANDatlnta).strokeColor("black").strokeWeight(2);
		Polyline houstonTOAtlanta = new Polyline(houstonTOAtlantaOpts);
		
		PolylineOptions lincolnTOchampaignOpts = new PolylineOptions().path(mvcLincolnANDchampaign).strokeColor("black").strokeWeight(2);
		Polyline lincolnTOchampaign = new Polyline(lincolnTOchampaignOpts);
		
		PolylineOptions champaignTOpittsburghOpts = new PolylineOptions().path(mvcChampaignANDpittsburgh).strokeColor("black").strokeWeight(2);
		Polyline champaignTOpittsburgh = new Polyline(champaignTOpittsburghOpts);
		
		PolylineOptions annArborTOithacaOpts = new PolylineOptions().path(mvcAnnArborANDithica).strokeColor("black").strokeWeight(2);
		Polyline annArborTOithaca = new Polyline(annArborTOithacaOpts);
		
		PolylineOptions annArborTOprincetonOpts = new PolylineOptions().path(mvcAnnArborANDprinceton).strokeColor("black").strokeWeight(2);
		Polyline annArborTOprinceton = new Polyline(annArborTOprincetonOpts);

		PolylineOptions atlantaTOpittsburgOpts = new PolylineOptions().path(mvcAtlantaANDpittsburgh).strokeColor("black").strokeWeight(2);
		Polyline atlantaTOpittsburg = new Polyline(atlantaTOpittsburgOpts);
		
		PolylineOptions pittsburghTOithacaOpts = new PolylineOptions().path(mvcPittsburghANDithaca).strokeColor("black").strokeWeight(2);
		Polyline pittsburghTOithaca = new Polyline(pittsburghTOithacaOpts);
		
		PolylineOptions pittsburghTOprincetonOpts = new PolylineOptions().path(mvcPittsburghANDprinceton).strokeColor("black").strokeWeight(2);
		Polyline pittsburghTOprinceton = new Polyline(pittsburghTOprincetonOpts);
		
		PolylineOptions collegePKTOithacaOpts = new PolylineOptions().path(mvcCollegePkANDithaca).strokeColor("black").strokeWeight(2);
		Polyline collegePKTOithaca = new Polyline(collegePKTOithacaOpts);
		
		PolylineOptions collegePKTOprincetonOpts = new PolylineOptions().path(mvcCollegePkANDprinceton).strokeColor("black").strokeWeight(2);
		Polyline collegePKTOprinceton = new Polyline(collegePKTOprincetonOpts);

		
		
		
		GoogleMap map = gmap.createMap(options);
				
		
		map.addMarker(sdMarker);
		map.addMarker(paloMarker);
		map.addMarker(seattleMarker);
		map.addMarker(slcMarker);
		map.addMarker(lincolnMarker);
		map.addMarker(boulderMarker);
		map.addMarker(houstonMarker);
		map.addMarker(champaignMarker);
		map.addMarker(atlantaMarker);
		map.addMarker(pittsburghMarker);
		map.addMarker(annArborMarker);
		map.addMarker(collegePkMarker);
		map.addMarker(ithacaMarker);
		map.addMarker(princetonMarker);
		
		map.addMapShape((MapShape) sdTOPalo);
		map.addMapShape((MapShape) sdTOseattle);
		map.addMapShape((MapShape) sdTOhouston);
		map.addMapShape((MapShape) paloTOseattle);
		map.addMapShape((MapShape) paloTOslc);
		map.addMapShape((MapShape) seattleTOchampaign);
		map.addMapShape((MapShape) slcTOannArbor);
		map.addMapShape((MapShape) slcTOboulder);
		map.addMapShape((MapShape) boulderTOhouston);
		map.addMapShape((MapShape) boulderTOlincoln);
		map.addMapShape((MapShape) houstonTOAtlanta);
		map.addMapShape((MapShape) houstonTOcollegePk);
		map.addMapShape((MapShape) lincolnTOchampaign);
		map.addMapShape((MapShape) champaignTOpittsburgh);
		map.addMapShape((MapShape) atlantaTOpittsburg);
		map.addMapShape((MapShape) annArborTOithaca);
		map.addMapShape((MapShape) annArborTOprinceton);
		map.addMapShape((MapShape) pittsburghTOithaca);
		map.addMapShape((MapShape) pittsburghTOprinceton);
		map.addMapShape((MapShape) collegePKTOithaca);
		map.addMapShape((MapShape) collegePKTOprinceton);
		
			
		
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		gmap.addMapInializedListener(this);
		trafficMethodBox.setItems(trafficMethodList);
		trafficMethodBox.getSelectionModel().select(0);
		routingMethodBox.setItems(routingMethodList);
		routingMethodBox.getSelectionModel().select(0);
	}
}
