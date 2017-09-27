package utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import models.CustomRequest;

public class RequestsUtil {
	public static ArrayList<CustomRequest> createCustomRequests(String fileName) {
		ArrayList<CustomRequest> requests = new ArrayList<CustomRequest>();
		try {
			//FileChooser fileChooser = new FileChooser();
			//File selectedFile = fileChooser.showOpenDialog(null);
			//String fileName = selectedFile.getAbsolutePath();
			Scanner scanner = new Scanner(new File(fileName));
			String strLine;
			while (scanner.hasNext()) {
				strLine = scanner.nextLine();
				String[] tokens = strLine.split("\\s+");
				requests.add(new CustomRequest(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
			}
			scanner.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return requests;
	}
}
