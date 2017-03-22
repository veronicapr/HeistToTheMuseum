/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heisttothemuseum.sharedzones;

import heisttothemuseum.threads.Thief;
import heisttothemuseum.sharedzones.AssaultParty;
import heisttothemuseum.enums.MasterThiefState;
import heisttothemuseum.interfaces.It_masterthief;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class MasterThiefControlCollectionSite implements It_masterthief {

	private final int num_rooms;		// number of rooms
	private final int team_size;		// total number of elements in team
	private final int max_thieves;		// total number of thieves
	private final int max_teams;		// max number of teams
	private final int max_gap;			// max gap between members in team

	private final Museum museum;
	private final OrdinaryThievesConcentrationSite site;

	private int num_teams;				// number of teams created
	private int canvas;					// number of stolen canvas
	private boolean[] empty_rooms;		// state of the rooms (with/without canvas)
	private boolean[] assigned_rooms;	// rooms with team assigned
	private AssaultParty[] teams;		// teams created

	public MasterThiefControlCollectionSite(int num_rooms, int max_thieves, int team_size, int max_gap, OrdinaryThievesConcentrationSite site, Museum museum) {
		this.num_rooms = num_rooms;
		this.max_thieves = max_thieves;
		this.team_size = team_size;
		this.max_gap = max_gap;

		this.site = site;
		this.museum = museum;

		this.num_teams = 0;
		this.canvas = 0;

		this.empty_rooms = new boolean[num_rooms];
		this.assigned_rooms = new boolean[num_rooms];
		for (int i = 0; i < num_rooms; i++) {
			this.empty_rooms[i] = false;
			this.assigned_rooms[i] = false;
		}

		int total_teams = max_thieves / team_size;
		if (num_rooms <= total_teams) {
			this.max_teams = num_rooms;
		} else {
			this.max_teams = total_teams;
		}
		this.teams = new AssaultParty[max_teams];
	}

	/**
	 * Checks for current room situation, if a non empty, non assigned room is found returns its number. If all rooms are empty or already assigned
	 *
	 * @return the room index, -1 if there is all rooms are assigned, -2 if all rooms are empty
	 */
	@Override
	public synchronized int checkRooms() {
		boolean all_empty = true;

		for (int index = 0; index < num_rooms; index++) {
			if (!empty_rooms[index]) {
				all_empty = false;
				if (!assigned_rooms[index]) {
					return index;
				}
			}
		}
		if (all_empty) {
			return -2;
		}
		return -1;
	}

	/**
	 * Changes state of the master thief to DECIDING_WHAT_TO_DO
	 *
	 * @return master thief next state as DECIDING_WHAT_TO_DO
	 */
	@Override
	public synchronized MasterThiefState startOperations() {
		return MasterThiefState.DECIDING_WHAT_TO_DO;
	}

	/**
	 * TODO
	 *
	 * @return
	 */
	@Override
	public synchronized MasterThiefState sendAssaultParty() {
		/*==> Acordar todos os ladrões da equipa em questão para iniciar o despacho */
		return MasterThiefState.DECIDING_WHAT_TO_DO;
	}

	/**
	 * Prepares a new assault party for the given target room. Uses the functions with the same name on the Ordinary Thieves Concentration Site and Museum for the team members
	 * and the target room distance respectively. Changes the target room to assigned
	 *
	 * @param target_room Target room number to be assigned to the new team
	 * @return next master thief state as ASSEMBLING_A_GROUP
	 */
	@Override
	public synchronized MasterThiefState prepareAssaultParty(int target_room) {
		int target_distance;
		Thief[] thieves;

		/* Get thieves from concentration site */
		thieves = site.prepareAssaultParty(team_size);
		/* Get target_distance form museum */
		target_distance = museum.prepareAssaultParty(target_room);
		/* Create assault party */
		AssaultParty assault_party = new AssaultParty(team_size, target_room, target_distance, max_gap, thieves, museum);
		/* Changes the target room to assigned */
		this.assigned_rooms[target_room] = true;
		/* Adds team to array and increments team number */
		this.teams[num_teams] = assault_party;
		this.num_teams++;

		return MasterThiefState.ASSEMBLING_A_GROUP;
	}

	public MasterThiefState collectCanvas(int target_team) {
		int stolen_canvas = teams[target_team].getCanvas();
		
		if (stolen_canvas != team_size) {
			
		}
		/*==> Retira os quadros de todos os ladrões da equipa que chegou
          ==> Desfaz a equipa e coloca os ladroes no campo de concentração
          ==> Se qualquer um dos ladrões chegar sem quadro coloca a sala como vazia*/
		return MasterThiefState.DECIDING_WHAT_TO_DO;
	}

	/**
	 * The master thief waits until awoken by the arrival of a team
	 *
	 * @return next master thief state as WAITING_FOR_GROUP_ARRIVAL
	 */
	@Override
	public synchronized MasterThiefState takeARest() {
		/* WAIT */

		for (int j = 0; j < teams.length; j++) {
			while (!teams[j].returned()) {
				try {
					wait();
				} catch (InterruptedException ex) {
					Logger.getLogger(MasterThiefControlCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}

		return MasterThiefState.WAITING_FOR_GROUP_ARRIVAL;
	}

	/**
	 * Changes state of master thief to PRESENTING_THE_REPORT
	 *
	 * @return next master thief state as PRESENTING_THE_REPORT
	 */
	public synchronized MasterThiefState sumUpResults() {
		return MasterThiefState.PRESENTING_THE_REPORT;
	}

	/**
	 * Returns two tables containing the current state of the empty rooms and assigned rooms in one table, and the created of the teams in the other.
	 *
	 * @return a string representation of the states of the arrays
	 */
	@Override
	public String toString() {
		String control = "========================= Master Thief Control Collection Site =========================\n";
		control += "==> Rooms State\n";
		String table_1 = "| Room      |";
		String table_2 = "| Empty     |";
		String table_3 = "| Assigned  |";
		for (int index = 0; index < this.num_rooms; index++) {
			table_1 += String.format("%1$7s|", index);
			table_2 += String.format("%1$7s|", this.empty_rooms[index]);
			table_3 += String.format("%1$7s|", this.assigned_rooms[index]);
		}
		control += table_1 + "\n" + table_2 + "\n" + table_3 + "\n";
		control += "==> Teams\n";
		table_1 = "| Team |";
		table_2 = "| Name |";
		for (int index = 0; index < this.max_teams; index++) {
			table_1 += String.format("    Team %1$4s    |", index);
			if (this.teams[index] == null) {
				table_2 += "        -        |";
			} else {
				table_2 += String.format("%1$17s|", this.teams[index].getName());
			}
		}
		return control += table_1 + "\n" + table_2 + "\n";
	}
}
