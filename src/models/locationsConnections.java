package models;
import java.util.ArrayList;

import com.lynden.gmapsfx.javascript.object.LatLong;

public class locationsConnections {
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
