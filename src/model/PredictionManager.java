package model;

import java.util.ArrayList;
import java.util.HashMap;

import controller.ForecastController;

public class PredictionManager {

	private ForecastController controller = null;

	public PredictionManager(ForecastController controller) {
		super();
		this.controller = controller;
	}

	public HashMap<Spot, Prediction> getFavouritePredictions(PredictionTime predictionTime) {
		HashMap<Spot, Prediction> acceptedPredictions = new HashMap<Spot, Prediction>();
		
		ArrayList<Spot> spots = getSpots();
		for(Spot spot:spots){
			Prediction prediction = getPrediction(spot.id, predictionTime);
			float rating = getPredictionRating(prediction);
			if (isAccepted(rating)) {
				acceptedPredictions.put(spot, prediction);
			}
		}
		return acceptedPredictions;

	}
	
	private ArrayList<Spot> getSpots() {
		// TODO: implement reading and returning spots
		ArrayList<Spot> spots = new ArrayList<Spot>();
		for (int i = 255; i < 257; ++i) {
			Spot spot = new Spot("testname", "testcountry", "teststate", i);
			spots.add(spot);
		}
		return spots;
	}
	/**
	 * 
	 * @param spotId
	 * @return Prediction for Spot with id spotId at time predictionTime
	 */
	private Prediction getPrediction(long spotId, PredictionTime predictionTime) {
		// make request and retrieve list
		ForecastResponse.List list = controller.getForecastResponseList(spotId);
		// create factory and retrieve Prediction
		Prediction prediction = new PredictionFactory(list, predictionTime)
				.createPrediction();
		return prediction;
	}

	private float getPredictionRating(Prediction prediction) {
		// TODO: implement
		return 1;
	}

	/**
	 * TODO: Adjust parameter
	 * 
	 * @param predictionRating
	 * @return boolean
	 */
	private boolean isAccepted(float predictionRating) {
		return (predictionRating > 0.5) ? true : false;
	}
}
