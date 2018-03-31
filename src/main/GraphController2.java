package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

public class GraphController2 implements Initializable {
	private StringBuilder resultsText = new StringBuilder();
	private Scanner scanner;
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    
    @FXML
    private Text results;
    
    @FXML
    private LineChart<?,?> resultsGraph;
    
    @FXML
    private NumberAxis x;

    @FXML
    private NumberAxis y;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			String path = new File(".").getCanonicalPath().concat("/src/results/");
			File mostRecentFile = getMostRecentFile(new File(path).listFiles());
			setResultsData(mostRecentFile);
			setResultsGraph(mostRecentFile);
		} catch (Exception e) {
		}
    }
    
    private File getMostRecentFile(File[] csvFiles) {
		File mostRecent = csvFiles[0];
		if(csvFiles.length==1) 
			return mostRecent;
		
		else {
			
			for(int i =0; i < csvFiles.length; i++) {
				if(mostRecent.lastModified() < csvFiles[i].lastModified()) {
					if (!csvFiles[i].getName().contains("utilization")) {
						mostRecent = csvFiles[i];
					}
				}
			}
		}
		return mostRecent;
	}
	
	private ArrayList<String[]> readableFile(File read) throws FileNotFoundException {
		scanner = new Scanner(read);
		ArrayList<String[]> file = new ArrayList<>();
		while(scanner.hasNext()) {
			
			String[] line = scanner.nextLine().split(",");
			file.add(line);
		}	
		return file;
	}
	
	private void setResultsData(File file) throws IOException {
		ArrayList<String[]> fileArray = readableFile(file);
		for(int i =0; i<fileArray.size();i++) {
			for(int j=0; j< fileArray.get(i).length; j++) {
				if(i==0)
					resultsText.append(fileArray.get(i)[j] + "	");
				else 
					resultsText.append(fileArray.get(i)[j] + "				");
			}
			resultsText.append("\n");
		}
		results.setText(resultsText.toString());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setResultsGraph(File file) throws IOException {
		ArrayList<String[]> fileArray = readableFile(file);
		x.setLabel("Bandwidth");
		y.setLabel("Drop Percentage");
		XYChart.Series series = new XYChart.Series();
		series.setName(fileArray.get(0)[5]);
		for(int i = 1; i < fileArray.size(); i++) {
			series.getData().add(new XYChart.Data(Integer.parseInt(fileArray.get(i)[0]), Integer.parseInt(fileArray.get(i)[5])));
			
		}
		resultsGraph.getData().addAll(series);
	}
}
