/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heisttothemuseum.sharedzones;

import heisttothemuseum.threads.Thief;
import heisttothemuseum.enums.ThiefState;
import heisttothemuseum.interfaces.It_AssaultParty;
import heisttothemuseum.interfaces.It_thief;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class OrdinaryThievesConcentrationSite implements It_thief{

	private final int num_thieves;			// total number of thieves
	private final Thief[] free_thieves;		// free thieves
	private int num_free;					// number of free thieves

	/**
	 * Creates the concentration site and all <i>num_thieves</i> in it awaiting to be assigned to the rooms, it requires the minimum and maximum displacement of the thieves to
	 * be provided
	 *
	 * @param num_thieves number of thieves in the heist excluding the master thief
	 * @param min_displacement minimum thief displacement
	 * @param max_displacement maximum thief displacement
	 */
	public OrdinaryThievesConcentrationSite(int num_thieves, int min_displacement, int max_displacement) {
		this.num_thieves = num_thieves;
		this.num_free = num_thieves;

		this.free_thieves = new Thief[num_thieves];
		//for (int i = 0; i < num_thieves; i++) {
		//	this.free_thieves[i] = new Thief (min_displacement, max_displacement);
		//}
	}
	
	public synchronized ThiefState prepareExcursion() {
		
		return ThiefState.CRAWLING_INWARDS;
	}
	
	@Override
	public synchronized ThiefState amINeeded(Thief thief) {
		boolean notNeeded = true;
		while(notNeeded){
			for( int i = 0; i < num_free; i ++){
				if(free_thieves[i] == thief){
					notNeeded = false;
					break;
				}
			}
			try {
				wait();
			} catch (InterruptedException ex) {
				Logger.getLogger(OrdinaryThievesConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return ThiefState.OUTSIDE;
	}

	/**
	 * Assigns the next <i>team_size</i> members to the new team, and places the rest of the members to the beginning of the waiting list
	 *
	 * @param team_size size of the team
	 * @return thieves to be assigned to the team
	 */
	public Thief[] prepareAssaultParty(int team_size) {
		Thief[] thieves = new Thief[team_size];

		/* Assigns the first team_size members to the team */
		System.arraycopy(free_thieves, 0, thieves, 0, team_size);
		/* Moves the rest of the free members to the beginning of the array */
		for (int index = team_size; index < num_free; index++) {
			free_thieves[index - team_size] = free_thieves[index];
			free_thieves[index] = null;
		}
		/* Reduces the number of free thieves by team_size */
		num_free -= team_size;

		return thieves;
	}
	
	public void dismissAssaultParty(Thief[] thieves, int team_size){
		/* Places the team membes on the concetration site */
		System.arraycopy(thieves, 0, free_thieves, num_free, team_size);
		/* Increments the number of free thieves by team_size */
		num_free += team_size;
	}

	/**
	 * Returns the current free thieves on the concentration site
	 *
	 * @return a string representation of the free thieves
	 */
	@Override
	public String toString() {
		String table_1 = "| Thief |";
		String table_2 = "| Name  |";
		String site = "================================== Concentration Site ==================================\n";
		for (int index = 0; index < this.num_thieves; index++) {
			table_1 += String.format("   Thief %1$4s    |", index);
			if (this.free_thieves[index] == null) {
				table_2 += "        -        |";
			} else {
				table_2 += String.format("%1$17s|", this.free_thieves[index].getName());
			}
		}
		return site += table_1 + "\n" + table_2 + "\n";
	}
}
