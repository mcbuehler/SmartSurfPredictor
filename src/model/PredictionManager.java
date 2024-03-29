package model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import util.SSPPaths;
import controller.ForecastController;

/**
 * Uses Controller to execute requests, handles responses and returns classified
 * forcasts
 * 
 * @author marcello
 * 
 */
public class PredictionManager {

	private ForecastController controller = null;

	public PredictionManager(ForecastController controller) {
		super();
		this.controller = controller;
	}

	/**
	 * 
	 * @param spots
	 *            list with all requested spots
	 * @param predictionTime
	 *            can be morning or afternoon, see {@link PredictionTime}
	 * @return list with {@link}{@link Prediction}
	 */
	private ArrayList<Prediction> getPredictions(ArrayList<Spot> spots,
			PredictionTime predictionTime) {
		ArrayList<Prediction> predictions = new ArrayList<Prediction>();
		for (Spot spot : spots) {
			Prediction p = getPrediction(spot, predictionTime);
			predictions.add(p);
			}
		return predictions;
		}
	
	/**
	 * Writes unlabeled predictions to file where it will be loaded from
	 * classifier
	 * 
	 * @param predictions
	 *            list with predictions
	 */
	private void prepareUnlabeledFile(ArrayList<Prediction> predictions) {
		String path = SSPPaths.userDir + "/"
				+ SSPPaths.tmpUnlabeledPredictionFileName;
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		ArrayList<PlainPrediction> list = new ArrayList<PlainPrediction>();
		for (Prediction p : predictions) {
			list.add(new PlainPrediction(p));
		}
		PredictionWriter writer = new PredictionWriter(path);
		writer.writeToFile(list);

	}
	
	/**
	 * Fetches and rates forecasts for the user's favourite spots.
	 * 
	 * @param predictionTime
	 *            MORNING or AFTERNOON
	 * @return HashMap with key: spot-instance and value: PlainPrediction
	 */
	public HashMap<Spot, PlainPrediction> getRatedPredictions(
			PredictionTime predictionTime) {
		System.out.println("> Loading user spots...");
		// Fetch Spots the user is interested in
		ArrayList<Spot> spots = FavouriteSpotFile.getFavouriteSpots();
		System.out.println("> Querying forecasts...");
		// Fetch the predictions for the spots at the right time of the day
		ArrayList<Prediction> predictions = getPredictions(spots,
				predictionTime);
		System.out.println("> Preparing data for classifier...");
		// Write the unlabeled predictions to arff file
		prepareUnlabeledFile(predictions);
		System.out.println("> Classifying...");
		// classify predictions and receive arrayList with scores
		ArrayList<Float> scores = PredictionClassifier.ratePredictions();
		System.out.println("> Aggregating scores...");
		// for further processing, the results have to be packed into a hashmap
		HashMap<Spot, PlainPrediction> map = createMap(spots, predictions,
				scores);
		return map;
		}

	private HashMap<Spot, PlainPrediction> createMap(ArrayList<Spot> spots,
			ArrayList<Prediction> predictions, ArrayList<Float> scores) {
		HashMap<Spot, PlainPrediction> map = new HashMap<Spot, PlainPrediction>();
		// add scores to accepted predictions and fill HashMap
		for (int i = 0; i < predictions.size(); ++i) {
			// declare new variable names for all needed elements -> better
			// readability
			Spot currentSpot = spots.get(i);
			Prediction currentPrediction = predictions.get(i);
			// for debugging. after jar export scores is null
			// System.out.println(scores);
			Float currentScore = scores.get(i);

			// Set score and status for prediction
			currentPrediction.setScore(currentScore);
			if (isAccepted(currentScore)) {
				currentPrediction.setStatus(PredictionStatus.ACCEPTED);
			} else {
				currentPrediction.setStatus(PredictionStatus.REJECTED);
			}
			// Extract only the needed data and pack into a PlainPrediction
			PlainPrediction p = new PlainPrediction(currentPrediction);
			map.put(currentSpot, p);

		}
		return map;
		}

	/**
	 * Makes Call to MSW API and returns Prediction
	 * 
	 * @param spotId
	 * @return Prediction for Spot with id spotId at time predictionTime
	 */
	public Prediction getPrediction(Spot spot, PredictionTime predictionTime) {
		// make request and retrieve list
		ForecastResponse.List list = controller
				.getForecastResponseList(spot.id);
		// create factory and retrieve Prediction
		Prediction prediction = new PredictionFactory(list, predictionTime,
				spot)
				.createPrediction();
		return prediction;
		}

	/**
	 * 
	 * @param predictionRating
	 * @return boolean
	 */
	private boolean isAccepted(float predictionRating) {
		return (predictionRating > 0) ? true : false;
		}
	}
