package models;

import java.util.Random;

public class Link {
	
	private static Random random = new Random();
	
	public static int generateRandomBandwidth() {
		
		int bandwidth = (int)(Math.random() * 4);      // Random number between 0-4
		
		switch(bandwidth){
		case 0: return 10;
		case 1: return 40;
		case 2: return 100;
		case 3: return 200;
		//case 4: return 400;
		//case 5: return 1000;
		default: return -1;
		}
	}
	
	public static int generateRandomBandwidth(int maxBandwidth) {
		return 1 + (int)(Math.random() * (maxBandwidth + 1));  
	}
	
	public static int generateRandomBandwidthUniform(int maxBandwidth){
		return 1 + random.nextInt(maxBandwidth + 1);
	}
	
	public static int generateRandomBandwidthGaussian(int maxBandwidth){
		return (int)(random.nextGaussian() * (maxBandwidth/2) + (maxBandwidth / 2));
	}

}
