/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heist.assault_party;

import genclass.GenericIO;
import heist.assault_party.interfaces.It_Thief_AssaultParty;
import heist.assault_party.interfaces.It_MasterThief_AssaultParty;
import heist.enums.State_Thief;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import settings.HeistSettings;


/**
 * Thieves assault party. Controls the excursion of its members from on point to another in a orderly way.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class AssaultParty extends UnicastRemoteObject implements It_MasterThief_AssaultParty, It_Thief_AssaultParty, Serializable {

	//========================================================================================================================//
	/**
	 * Assault party id
	 */
	final int id;

	/**
	 * Random number generator
	 */
	private final Random random;
	//========================================================================================================================//
	/**
	 * Assault party max members
	 */
	private final int team_size =  HeistSettings.TEAM_SIZE;
	/**
	 * Max gap between members
	 */
	private final int max_gap = HeistSettings.MAX_GAP;
	/**
	 * Target room number
	 */
	private int target_room;
	/**
	 * Target room distance
	 */
	private int target_distance;
	//========================================================================================================================//
	/**
	 * Excursion start signal
	 */
	private boolean start_signal;
	/**
	 * Excursion end signal
	 */
	private boolean return_signal;
	//========================================================================================================================//
	/**
	 * Team members positions, always in crescent order
	 */
	final int[] team_distances = new int[HeistSettings.TEAM_SIZE];
	/**
	 * Team members id, ordered according to positions
	 */
	final int[] team_members = new int[HeistSettings.TOTAL_TEAMS];

	//========================================================================================================================//
	/**
	 * Constructor for the assault party.
	 *
	 * @param id assault party id
	 * @throws java.rmi.RemoteException
	 */
	public AssaultParty(int id) throws RemoteException {
		super();
		this.id = id;

		this.random = new Random();

		for (int index = 0; index < this.team_size; index++) {
			this.team_members[index] = -1;
			this.team_distances[index] = 0;
		}
	}

	/**
	 * Assault Party object reference [singleton]
	 */
	private static AssaultParty self;
	/**
	 * Registry host name
	 */
	private static String registry_host_name;
	/**
	 * Registry port number
	 */
	private static int registry_port_number;
	/**
	 * AssaultParty server start, requires 3 argument.
	 *
	 * @param args program arguments should be:
	 * <ul>
	 * <li>registry host name</li>
	 * <li>registry port number</li>
	 * </ul>
	 */
	
	public static void main(String[] args){
		if (args.length != 3) {
			GenericIO.writelnString("Wrong number of arguments!");
			return;
		} else {
			try {
				registry_host_name = args[0];
				registry_port_number = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				GenericIO.writelnString("Port number must be an integer!");
			}
		}
		// security manager
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		GenericIO.writelnString("Security manager was installed!");
		
		// regist Assault Party 
		try {
			//self = new AssaultParty();
			LocateRegistry.getRegistry(registry_host_name, registry_port_number).rebind("Assault Party", self);
			GenericIO.writelnString("Assault Party bound!");
		} catch (RemoteException ex) {
			GenericIO.writelnString("Assault Party exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		// log full update
		
		// ready message
		GenericIO.writelnString("Assault Party server ready!");
		
	}

	
	//========================================================================================================================//
	// Master thief methods
	//========================================================================================================================//
	/**
	 * <p>
	 * Prepares assault party by setting its target room and target distance.</p>
	 * <p>
	 * Also forces the return signal to false.</p>
	 *
	 * @param target_room room number
	 * @param target_distance room distance
	 */
	@Override
	public synchronized void prepareAssaultParty(int target_room, int target_distance) {
		this.target_room = target_room;
		this.target_distance = target_distance;
		this.start_signal = false;
		this.return_signal = false;
	}

	//========================================================================================================================//
	// Thief methods
	//========================================================================================================================//
	/**
	 * Thief prepares for excursion by setting its position to the initial position and verifies if everyone is ready, if so send start signal.
	 *
	 * @param thief_id thief reference
	 * @return target_room room number
	 */
	@Override
	public synchronized int prepareExcursion(int thief_id) {
		boolean all_prepared = true;
		// changes his position to 0 and adds his id to the team if not there
		for (int index = 0; index < this.team_size; index++) {
			if (this.team_members[index] == thief_id) {
				this.team_distances[index] = 0;
				break;
			}
			if (this.team_members[index] == -1) {
				this.team_members[index] = thief_id;
				this.team_distances[index] = 0;
				break;
			}
		}
		// checks if all memebers are prepared
		for (int index = 0; index < this.team_size; index++) {
			if (!(this.team_members[index] != -1 && this.team_distances[index] == 0)) {
				all_prepared = false;
				break;
			}
		}
		// if all are prepared sends start signal
		if (all_prepared) {
			this.start_signal = true;
			notifyAll();
		}
		// awaits until start signal
		while (!this.start_signal) {
			try {
				wait();
			} catch (InterruptedException ex) {
				break;
			}
		}
		return this.target_room;
	}

	/**
	 * Crawls in towards assault party target room, minding his position gap between team members, proceeds in the following way:
	 * <ul>
	 * <li>1st - finds his position in team,</li>
	 * <li>2nd - calculates his advancement,</li>
	 * <li>3rd - if he is not the last one, sees if is advancement passes the gap over the one behind,</li>
	 * <li>4th - if he is not the last or first one, sees if the next thief position is not over the previous + gap,</li>
	 * <li>5th - goes to the last free position (further away) it finds in the line according to how much he can move,</li>
	 * <li>6th - if he goes over the target distance, sets back to the target distance,</li>
	 * <li>7th - if he moves from his location, reorganises the team positions, and logs the change,</li>
	 * <li>8th - decides whenever he reached the room or continues to crawl to it.</li>
	 * </ul>
	 *
	 * @param state thief state
	 * @param thief_id thief reference
	 * @param thief_agility thief agility
	 * @return ThiefState next state as State_Thief.AT_A_ROOM or State_Thief.CRAWLING_INWARDS
	 */
	@Override
	public synchronized State_Thief crawlIn(State_Thief state, int thief_id, int thief_agility) {
		int thief_index;
		int advance;
		// finds his position in the team
		for (thief_index = 0; thief_index < this.team_size; thief_index++) {
			if (this.team_members[thief_index] == thief_id) {
				break;
			}
		}
		// calculates his advancement
		advance = this.team_distances[thief_index] + random.nextInt(thief_agility) + 1;
		// if he is not the last one, sees if is advancement passes the gap over the one behind
		if (!(thief_index == 0)) {
			if (advance > this.team_distances[thief_index - 1] + this.max_gap) {
				advance = this.team_distances[thief_index - 1] + this.max_gap;
			}
		}
		// if he is not the last or first one, sees if the next thief position is not over the previous + gap 
		if (!(thief_index + 1 == this.team_size) && !(thief_index == 0)) {
			if (this.team_distances[thief_index + 1] > this.team_distances[thief_index - 1] + this.max_gap) {
				advance = this.team_distances[thief_index - 1] + this.max_gap;
			}
		}
		// goes to the last free position (further away) it finds in the line according to how much he can move
		if (!(thief_index + 1 == this.team_size)) {
			for (int current_index = ((thief_index + this.max_gap > this.team_size - 1) ? this.team_size - 1 : thief_index + this.max_gap); current_index > thief_index; current_index--) {
				if (advance < this.team_distances[current_index]) {
					continue;
				}
				if (advance == this.team_distances[current_index]) {
					advance--;
					continue;
				}
				break;
			}
		}
		// if he goes over the target distance, sets back to the target distance
		if (advance > this.target_distance) {
			advance = this.target_distance;
		}
		// if he moves from his location, reorganiss the team positions, and logs the change
		if (advance != this.team_distances[thief_index]) {
			this.team_distances[thief_index] = advance;
			for (int index = thief_index; index < this.team_size - 1; index++) {
				if (this.team_distances[index + 1] < this.team_distances[index]) {
					int tmp_distance = this.team_distances[index];
					int tmp_thief_id = this.team_members[index];
					this.team_distances[index] = this.team_distances[index + 1];
					this.team_members[index] = this.team_members[index + 1];
					this.team_distances[index + 1] = tmp_distance;
					this.team_members[index + 1] = tmp_thief_id;
					continue;
				}
				break;
			}
			//repository.logLine(state, thief_id, thief_agility);
		}
		// decides whenever he reached the room or continues to crawl to it
		if (this.team_distances[thief_index] == this.target_distance) {
			return State_Thief.AT_A_ROOM;
		} else {
			return State_Thief.CRAWLING_INWARDS;
		}
	}

	/**
	 * Verifies if everyone has already arrived at the room, if so signals everyone that the can start crawling back.
	 *
	 * @return return signal
	 */
	@Override
	public synchronized boolean reverseDirection() {
		boolean all_at_room = true;
		// Verifies if everyone has arrived
		for (int thief_index = 0; thief_index < this.team_size; thief_index++) {
			if (this.team_distances[thief_index] != this.target_distance) {
				all_at_room = false;
				break;
			}
		}
		// if all arrived sends return signal
		if (all_at_room) {
			this.return_signal = true;
			notifyAll();
			return true;
		}
		// awaits until signal
		while (!this.return_signal) {
			try {
				wait();
			} catch (InterruptedException ex) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Crawls out towards the concentration site, minding his position gap between team members, proceeds in the following way:
	 * <ul>
	 * <li>1st - finds his position in team,</li>
	 * <li>2nd - calculates his advancement,</li>
	 * <li>3rd - if he is not the first one, sees if is advancement passes the gap over the one behind,</li>
	 * <li>4th - if he is not the first or last one, sees if the next thief position is not over the previous + gap,</li>
	 * <li>5th - goes to the last free position (further away) it finds in the line according to how much he can move,</li>
	 * <li>6th - if he goes over the concentration site, sets back to 0,</li>
	 * <li>7th - if he moves from his location, reorganises the team positions, and logs the change,</li>
	 * <li>8th - decides whenever he reached the concentration site or continues to crawl to it.</li>
	 * </ul>
	 *
	 * @param state thief state
	 * @param thief_id thief reference
	 * @param thief_agility thief agility
	 * @return next state as return State_Thief.OUTSIDE or State_Thief.CRAWLING_OUTWARDS
	 */
	@Override
	public synchronized State_Thief crawlOut(State_Thief state, int thief_id, int thief_agility) {
		int thief_index;
		int advance;
		// finds his position in team
		for (thief_index = 0; thief_index < this.team_size; thief_index++) {
			if (this.team_members[thief_index] == thief_id) {
				break;
			}
		}
		// calculates his advancement
		advance = this.team_distances[thief_index] - random.nextInt(thief_agility) - 1;
		// if he is not the first one, sees if is advancement passes the gap over the one behind
		if (!(thief_index + 1 == this.team_size)) {
			if (advance < this.team_distances[thief_index + 1] - this.max_gap) {
				advance = this.team_distances[thief_index + 1] - this.max_gap;
			}
		}
		// if he is not the first or last one, sees if the next thief position is not over the previous + gap
		if (!(thief_index == 0) && !(thief_index + 1 == this.team_size)) {
			if (this.team_distances[thief_index - 1] < this.team_distances[thief_index + 1] - this.max_gap) {
				advance = this.team_distances[thief_index + 1] - this.max_gap;
			}
		}
		// goes to the last free position (further away) it finds in the line according to how much he can move
		if (!(thief_index == 0)) {
			for (int current_index = ((thief_index - this.max_gap < 0) ? 0 : thief_index - this.max_gap); current_index < thief_index; current_index++) {
				if (this.team_distances[current_index] < advance) {
					continue;
				}
				if (advance == this.team_distances[current_index]) {
					advance++;
					continue;
				}
				break;
			}
		}
		// if he goes over the concentration site, sets back to 0
		if (advance < 0) {
			advance = 0;
		}
		// if he moves from his location, reorganises the team positions, and logs the change
		if (advance != this.team_distances[thief_index]) {
			this.team_distances[thief_index] = advance;
			for (int index = thief_index; index > 0; index--) {
				if (this.team_distances[index - 1] > this.team_distances[index]) {
					int tmp_distance = this.team_distances[index];
					int tmp_thief_id = this.team_members[index];
					this.team_distances[index] = this.team_distances[index - 1];
					this.team_members[index] = this.team_members[index - 1];
					this.team_distances[index - 1] = tmp_distance;
					this.team_members[index - 1] = tmp_thief_id;
					continue;
				}
				break;
			}
			//repository.logLine(state, thief_id, thief_agility);
		}
		// decides whenever he reached the concentration site or continues to crawl to it
		if (this.team_distances[thief_index] == 0) {
			return State_Thief.OUTSIDE;
		} else {
			return State_Thief.CRAWLING_OUTWARDS;
		}
	}
}
