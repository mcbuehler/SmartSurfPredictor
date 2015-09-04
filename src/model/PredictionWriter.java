package model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Writes data files (arff) for weka Classifiers. This is a specialized Writer
 * for the SmartSurfPredictor Classifier. Check out constructor javadoc for more
 * information.
 * 
 * @author marcello
 * 
 */
public class PredictionWriter extends PrintWriter {
	boolean labeled;
	
	/**
	 * <p>
	 * Creates an arff file for Predictions.
	 * </p>
	 * set flag labeled:<li>true = write labeled data (= write training set) <li>
	 * false = write unlabeled data
	 * 
	 * @param path
	 *            path to file, e.g. "data/predictions_unlabeled.arff"
	 * @param labeled
	 *            true (write training set) or false (write unlabeled
	 *            predictions)
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public PredictionWriter(String path, boolean labeled)
			throws FileNotFoundException,
			UnsupportedEncodingException {
		super(path, "UTF-8");
		this.labeled = labeled;
	}

	private void writeRelationName(String name) {
		this.write("@relation " + name);
	}

	private void writeSinglePrediction(Prediction p) {
		StringBuilder builder = new StringBuilder();

		int minBreakHeight = p.getSwellForecast().surf.minBreakingHeight;
		builder.append(minBreakHeight);
		builder.append(",");
		int maxBreakHeight = p.getSwellForecast().surf.maxBreakingHeight;
		builder.append(maxBreakHeight);
		builder.append(",");
		int fadedRating = p.getSwellForecast().fadedRating;
		builder.append(fadedRating);
		builder.append(",");
		int solidRating = p.getSwellForecast().solidRating;
		builder.append(solidRating);
		builder.append(",");
		float primarySwellHeight = p.getSwellForecast().primarySwell.height;
		builder.append(primarySwellHeight);
		builder.append(",");
		int primarySwellPeriod = p.getSwellForecast().primarySwell.period;
		builder.append(primarySwellPeriod);
		builder.append(",");
		CompassDirection primarySwellDirection = p.getSwellForecast().primarySwell.compassDirection;
		builder.append(primarySwellDirection);
		builder.append(",");
		int windSpeed = p.getWeatherForecast().wind.speed;
		builder.append(windSpeed);
		builder.append(",");
		CompassDirection windDirection = p.getWeatherForecast().wind.compassDirection;
		builder.append(windDirection);
		builder.append(",");
		int weather = p.getWeatherForecast().weather.weather;
		builder.append(weather);
		builder.append(",");
		float temperature = p.getWeatherForecast().weather.temperature;
		builder.append(temperature);
		// for target class
		builder.append(",");
		if (labeled) {
			if (p.status == PredictionStatus.ACCEPTED) {
				builder.append("yes");
			}
 else if (p.status == PredictionStatus.REJECTED) {
				builder.append("no");
			} else {
				System.err.println("Error: Invalid PredictionStatus: "
						+ p.status);
				// builder.append("?");
			}
		} else {
		builder.append("?");
		}
		this.println(builder.toString());
	}

	/**
	 * Writes list with predictions to arff file. <li>header: e.g. "@relation X"
	 * <li>attributes: e.g. "@attribute speed numeric" <li>data: data with all
	 * data from Prediction list
	 * 
	 * @param list
	 */
	public void writePredictions(ArrayList<Prediction> list) {
		writeRelationName("predictions");
		this.println("");
		this.println("");
		writeAttributeList();
		this.println("");
		this.println("");
		this.println("@data");
		for (Prediction p : list) {
			writeSinglePrediction(p);
		}

	}

	private void addNumeric(String attributeName) {
		this.println("@attribute " + attributeName + " numeric");
	}

	private void addAttribute(String attributeName, String values) {
		this.println("@attribute " + attributeName + " {"
				+ values.substring(1, values.length() - 1) + "}");
	}

	private void writeAttributeList() {
		StringBuilder builder = new StringBuilder();

		addNumeric("minBreakHeight");
		addNumeric("maxBreakHeight");
		addNumeric("fadedRating");
		addNumeric("solidRating");
		addNumeric("primarySwellHeight");
		addNumeric("primarySwellPeriod");
		addAttribute("compassDirection",
				Arrays.toString(CompassDirection.values()));
		addNumeric("windSpeed");
		addAttribute("windDirection",
				Arrays.toString(CompassDirection.values()));
		addNumeric("weather");
		addNumeric("temperature");
		builder.append("@attribute show {yes,no}");
		
		this.print(builder.toString());
		
	}



}
