/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heist.control_site;

import genclass.GenericIO;
import heist.control_site.interfaces.It_MasterThief_ControlCollectionSite;
import heist.control_site.interfaces.It_Thief_ControlCollectionSite;
import heist.enums.State_Thief;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import settings.HeistSettings;


/**
 * <p>
 * Master thief operation control. </p>
 * <p>
 * Contains information about the state of empty rooms, assigned rooms, assigned teams and number of stolen canvas. </p>
 * <p>
 * Information used for master thief decisions. </p>
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class MasterThiefControlCollectionSite implements It_MasterThief_ControlCollectionSite, It_Thief_ControlCollectionSite, Serializable  {

	//========================================================================================================================//
	/**
	 * Total number of thieves
	 */
	private final int total_thieves = HeistSettings.TOTAL_THIEVES;
	/**
	 * Total number of museum rooms
	 */
	private final int total_rooms = HeistSettings.TOTAL_ROOMS;
	/**
	 * Total number of assault parties
	 */
	private final int total_teams = HeistSettings.TOTAL_TEAMS;
	/**
	 * Number of elements in each assault party
	 */
	private final int team_size = HeistSettings.TEAM_SIZE;
	//========================================================================================================================//
	/**
	 * Number of stolen canvas
	 */
	int stolen_canvas;
	/**
	 * Flags for empty rooms
	 */
	private final boolean[] empty_rooms;
	/**
	 * Empty room verification start
	 */
	private int first_non_empty_room;
	/**
	 * Flags for assigned rooms
	 */
	private final boolean[] assigned_rooms;
	/**
	 * Number of assigned rooms
	 */
	private int total_assigned_rooms;
	/**
	 * Flags for non fully returned teams
	 */
	private final boolean[] returning_team;
	/**
	 * Returned team member count
	 */
	private final int[] member_count;
	//========================================================================================================================//
	/**
	 * Returned members queue size
	 */
	private int returned_members_size;
	/**
	 * Returned members queue start
	 */
	private int returned_members_queue_start;
	/**
	 * Returned members queue end
	 */
	private int returned_members_queue_end;
	/**
	 * Returned members queue
	 */
	private final int[] returned_members;
	//========================================================================================================================//

	/**
	 * Constructor to the Master Thief Control Collection Site, also initialises the the all flag arrays as false and all member_count at 0, as well as the stolen_canvas at 0.
	 *
	 * @throws java.rmi.RemoteException
	 */
	public MasterThiefControlCollectionSite()  throws RemoteException {
		super();

		this.stolen_canvas = 0;
		this.first_non_empty_room = 0;
		this.total_assigned_rooms = 0;
		this.empty_rooms = new boolean[total_rooms];
		this.assigned_rooms = new boolean[total_rooms];
		for (int index = 0; index < total_rooms; index++) {
			this.empty_rooms[index] = false;
			this.assigned_rooms[index] = false;
		}
		this.returning_team = new boolean[total_teams];
		this.member_count = new int[total_teams];
		for (int index = 0; index < total_teams; index++) {
			this.returning_team[index] = false;
			this.member_count[index] = 0;
		}

		this.returned_members_size = 0;
		this.returned_members_queue_start = 0;
		this.returned_members_queue_end = 0;
		this.returned_members = new int[total_thieves];
		for (int index = 0; index < total_thieves; index++) {
			this.returned_members[index] = 0;
		}
	}

	
	/**
	 * MasterThieves Control and Collection Site object reference [singleton]
	 */
	private static MasterThiefControlCollectionSite self;
	/**
	 * Registry host name
	 */
	private static String registry_host_name;
	/**
	 * Registry port number
	 */
	private static int registry_port_number;
	/**
	 * Ordinary Thieves Concentration Site server start, requires 3 argument.
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
		
		// Regist Master Thieves Concentration Site
		try {
			self = new MasterThiefControlCollectionSite();
			LocateRegistry.getRegistry(registry_host_name, registry_port_number).rebind("Master Thief Control and Collection Site", self);
			GenericIO.writelnString("Master Thief Control and Collection Site bound!");
		} catch (RemoteException ex) {
			GenericIO.writelnString("Master Thief Control and Collection Site exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		// log full update
		
		// ready message
		GenericIO.writelnString("Master Thief Control and Collection Site server ready!");
	}
	//========================================================================================================================//
	// Master thief methods
	//========================================================================================================================//
	/**
	 * <p>
	 * Master thief decides the next action based on the current situation. Result decisions can be either</p>
	 * <ul>
	 * <li>To dispatch a new team to a determined target room, that is unassigned and empty at that time</li>
	 * <li>Await for the teams arrival, when no more teams can be dispatched, there are returned members in queue or no more rooms to be assigned</li>
	 * <li>Or present the results, when all rooms are empty and no all teams have already returned</li>
	 * </ul>
	 *
	 * @return
	 * <ul>
	 * <li>target_room when a new team is to be dispatched</li>
	 * <li>-1 when no more teams can be dispatched, there are returned members in queue or no more rooms to be assigned</li>
	 * <li>-2 when all rooms are empty and no all teams have already returned</li>
	 * </ul>
	 */
	@Override
	public synchronized int appraiseSit() {
		boolean all_rooms_empty = true;
		boolean no_returning_teams = (this.total_assigned_rooms == 0);
		// if someone is waiting to deliver a canvas of if no more teams can be assigned, retreives the canvas or awaits for arrivals
		if ((this.returned_members_size != 0) || (this.total_assigned_rooms == this.total_teams)) {
			return -1;
		}
		// cycles trough the rooms
		for (int room_index = this.first_non_empty_room; room_index < this.total_rooms; room_index++) {
			// if room is empty and all previous rooms are in the same situation, next cycle begins from here, and continues
			if (all_rooms_empty && this.empty_rooms[room_index]) {
				this.first_non_empty_room = room_index + 1;
				continue;
			}
			// if room is empty continues
			if (this.empty_rooms[room_index]) {
				continue;
			}
			// if room isn't empty and unassigned, assigns it and returns the room index
			if (!this.empty_rooms[room_index] && !this.assigned_rooms[room_index]) {
				this.assigned_rooms[room_index] = true;
				this.total_assigned_rooms++;
				return room_index;
			}
			// if a room is not empty but assigned
			all_rooms_empty = false;
		}
		// decides whether to terminate heist or await for someone
		if (all_rooms_empty & no_returning_teams) {
			return -2;
		} else {
			return -1;
		}
	}

	/**
	 * <p>
	 * Master thief waiting cycle.</p>
	 * <p>
	 * Exits cycle if queue isn't empty.</p>
	 *
	 * @return Arrived assault party id
	 */
	@Override
	public synchronized int takeARest() {
		while (this.returned_members_size == 0) {
			try {
				wait();
			} catch (InterruptedException ex) {
				return -1;
			}
		}
		// retrieves and removes queue first assault party id
		int team_id = this.returned_members[this.returned_members_queue_start];
		this.returned_members_queue_start = (this.returned_members_queue_start + 1) % this.total_thieves;
		this.returned_members_size--;

		return team_id;
	}

	/**
	 * <p>
	 * Retrieves and removes next assault party id form the queue and checks if corresponding member count is complete.</p>
	 * <p>
	 * If member count is complete, resets it to 0 and changes returning team flag to false. Returns assault party id.</p>
	 * <p>
	 * Returns -1 if member count for queue assault party id isn't complete.</p>
	 *
	 * @param team_id Assault party id
	 * @return
	 * <ul>
	 * <li>1 if there where any canvas left</li>
	 * <li>-1 if not all team members have returned</li>
	 * </ul>
	 */
	@Override
	public synchronized int collectCanvas(int team_id) {
		// checks if all assault party members associated with retrieved id have returned, if so returns team id and checks returning as false and member count as 0
		if (this.member_count[team_id] == this.team_size) {
			this.member_count[team_id] = 0;
			this.returning_team[team_id] = false;
			this.total_assigned_rooms--;
			// returns team id
			return team_id;
		} else {
			// returns -1 if member count isn't complete
			return -1;
		}
	}

	//========================================================================================================================//
	// Thief methods
	//========================================================================================================================//
	/**
	 * <p>
	 * The returned thief hands his stolen canvas to the master thief or sets his target room as empty if he has returned empty handed.</p>
	 * <p>
	 * Sets team as returning and adds to team member count, if all members arrived sets target room as unassigned.</p>
	 * <p>
	 * Adds his assault party id to the returned members queue.</p>
	 *
	 * @param state thief state
	 * @param thief_id thief id
	 * @param assault_party_id thief assault party id
	 * @param target_room thief target room
	 * @param stolen_canvas thief stolen canvas
	 */
	@Override
	public synchronized void handACanvas(State_Thief state, int thief_id, int assault_party_id, int target_room, int stolen_canvas) {
		// hands a canvas
		this.stolen_canvas += stolen_canvas;
		// if empty handed, sets room as empty
		if (stolen_canvas == 0) {
			this.empty_rooms[target_room] = true;
		}
		// sets team as returning and adds to member count, if all members arrived sets target room as unassigned
		this.returning_team[assault_party_id] = true;
		this.member_count[assault_party_id]++;
		if (this.member_count[assault_party_id] == this.team_size) {
			this.assigned_rooms[target_room] = false;
		}
		// adds his assault party id to the returned members queue
		this.returned_members[this.returned_members_queue_end] = assault_party_id;
		this.returned_members_queue_end = (this.returned_members_queue_end + 1) % this.total_thieves;
		this.returned_members_size++;
		// logs line
		//repository.logLine(stolen_canvas, state, thief_id);
		// notifies master thief
		notifyAll();
	}

}
