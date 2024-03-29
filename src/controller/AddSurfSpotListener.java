package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import model.Notifier;
import view.AddSpotView;
import view.FeedbackView;
import view.PredictorView;

public class AddSurfSpotListener implements ActionListener 
{
	private PredictorView view;
	private Notifier model;
	private AddSpotView spotView;
	
	//Check if all the JList have been selected,returns true(empty), if users didn't select anything
	//if all selected, writes to favorite file
	
	public AddSurfSpotListener(PredictorView v, Notifier m, AddSpotView s) 
	{
		this.view = v;
		this.model = m;
		this.spotView = s;
	}
	

	public void actionPerformed(ActionEvent e) 
	{
		//check if country selected
		if(spotView.getSelectedCountry().isEmpty())
		{
			JOptionPane.showMessageDialog(view, " Country not selected !","Select Country", JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		//check is state selected
		if(spotView.getSelectedState().isEmpty())
		{
			JOptionPane.showMessageDialog(view, " State not selected !","Select State", JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		//check if location selected
		if(spotView.getSelectedLocations().isEmpty())
		{
			JOptionPane.showMessageDialog(view, " Surf Location not selected !","Select Surf Location", JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		//write favorite surf details to file
		model.getFavSpot().writeToFavFile(spotView.findUsersfavouriteSpots());
		
		spotView.setVisible(false);
		spotView.dispose();
		 //check if user is justing setting up the app for the first time
		//if yes - make them provide feedback
		if(view.isStartSetupDone() == true)
		{
			new FeedbackView(view,model).setVisible(true);
		}
	}

}
