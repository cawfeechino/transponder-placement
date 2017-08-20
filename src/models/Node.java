package models;

public class Node {

	public static int generateRandomComputationalSpeed() {
		return ((10 + (int)(Math.random() * 51)) / 10) * 10; // Generates a number that is divisible by 10 between 10 and 50
	}

}
