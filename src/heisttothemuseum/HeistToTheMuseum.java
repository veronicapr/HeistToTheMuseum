
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heisttothemuseum;

import heisttothemuseum.threads.MasterThief;
import heisttothemuseum.sharedzones.OrdinaryThievesConcentrationSite;
import heisttothemuseum.sharedzones.Museum;
import heisttothemuseum.sharedzones.MasterThiefControlCollectionSite;
import heisttothemuseum.interfaces.It_masterthief;

/**
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class HeistToTheMuseum {

	// Run Values =================================== //
	// Thieves
	private static final int NUM_THIEVES = 6;
	private static final int TEAM_SIZE = 2;
	private static final int MIN_DISPLACEMENT = 2;
	private static final int MAX_DISPLACEMENT = 6;
	private static final int MAX_GAP = 3;
	// Museum
	private static final int NUM_ROOMS = 3;
	private static final int MIN_DISTANCE = 15;
	private static final int MAX_DISTANCE = 20;

	// Objects ====================================== //
	private static Museum museum;
	private static MasterThiefControlCollectionSite master_thief_control;
	private static OrdinaryThievesConcentrationSite concentration_site;
	private static MasterThief master_thief;

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		inicializeHeist();
		showStructs();
	}

	private static void inicializeHeist() {	
		/* Creating the Shared Memories */
		museum = new Museum(NUM_ROOMS, MIN_DISTANCE, MAX_DISTANCE);
		concentration_site = new OrdinaryThievesConcentrationSite(NUM_THIEVES, MIN_DISPLACEMENT, MAX_DISPLACEMENT);
		master_thief_control = new MasterThiefControlCollectionSite(NUM_ROOMS, NUM_THIEVES, TEAM_SIZE, MAX_GAP, concentration_site, museum);
		
		/* Creating the threads */
		master_thief = new MasterThief((It_masterthief)master_thief_control, "Master Thief", master_thief_control, museum);
		/*
		for (int index = 0; index > NUM_THIEVES; index++) {
			
		}
		*/
		
		/* Starts the threads */
		master_thief.start();
	}
	
	private static void showStructs() {
		System.out.println(museum);
		System.out.println(master_thief_control);
		System.out.println(concentration_site);
	}
}
