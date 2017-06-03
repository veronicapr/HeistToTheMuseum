/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heist.museum;

import java.util.Random;
import settings.HeistSettings;
import genclass.GenericIO;
import heist.museum.interfaces.It_MasterThief_Museum;
import heist.museum.interfaces.It_Thief_Museum;
import heist.repository.interfaces.It_Repository_Museum;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Museum representation containing every room distance and the current paintings in each room.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 *
 */
public class Museum extends UnicastRemoteObject implements It_MasterThief_Museum, It_Thief_Museum, Serializable {

	//========================================================================================================================//
	// Museum Data
	//========================================================================================================================//
	/**
	 * Array containing the number of paintings in each room
	 */
	private final int[] rooms_paintings = new int[HeistSettings.TOTAL_ROOMS];
	/**
	 * Array containing the distance of each room in the museum
	 */
	private final int[] rooms_distance = new int[HeistSettings.TOTAL_ROOMS];

	//========================================================================================================================//
	// Museum instance
	//========================================================================================================================//
	/**
	 * Generates the Museum by randomising a value of distance for each museum room between given minimum and maximum distance and giving a number of paintings equal to
	 * room index.
	 */
	private Museum() throws RemoteException {
		super();
		// randomises room distances and sets a number of paintings equals to the room index
		Random rand = new Random();
		for (int i = 0; i < HeistSettings.TOTAL_ROOMS; i++) {
			rooms_paintings[i] = i;
			rooms_distance[i] = rand.nextInt((HeistSettings.MAX_DISTANCE - HeistSettings.MIN_DISTANCE) + 1) + HeistSettings.MIN_DISTANCE;
		}
		// logs line
		// 
	}

	//========================================================================================================================//
	// Museum server info and main
	//========================================================================================================================//
	/**
	 * RMI registered name
	 */
	private static final String NAME = "Museum";
	/**
	 * Museum object reference [singleton]
	 */
	private static Museum self;
	/**
	 * Museum server port number
	 */
	private static int port_number;
	/**
	 * Registry host name
	 */
	private static String registry_host_name;
	/**
	 * Registry port number
	 */
	private static int registry_port_number;
	

	/**
	 * Museum server start, requires 3 argument.
	 *
	 * @param args program arguments should be:
	 * <ul>
	 * <li>museum server port number</li>
	 * <li>registry host name</li>
	 * <li>registry port number</li>
	 * </ul>
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			GenericIO.writelnString("Wrong number of arguments!");
			return;
		} else {
			try {
				port_number = Integer.parseInt(args[0]);
				registry_host_name = args[1];
				registry_port_number = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				GenericIO.writelnString("Port number must be an integer!");
			}
		}
		
		//System.setProperty("java.rmi.server.hostname","192.168.50.34");
		
		try {
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
			GenericIO.writelnString("Security manager was installed!");

			self = new Museum();
			
			LocateRegistry.getRegistry(registry_host_name, registry_port_number).rebind(NAME, self);

			GenericIO.writelnString("Museum bound!");
		} catch (RemoteException ex) {
			GenericIO.writelnString("Museum exception: " + ex.getMessage());
			ex.printStackTrace();
		}
		
		try {
		((It_Repository_Museum) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("General_Repository"))
				.logLine_MuseumUpdateFull(self.rooms_paintings, self.rooms_distance);
		} catch (RemoteException ex) {
			GenericIO.writelnString("Remote Exception (main log line): " + ex.getMessage());
			ex.printStackTrace();
		} catch (NotBoundException ex) {
			GenericIO.writelnString("Not Bound Exception (main log line):  " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	//========================================================================================================================//
	// Master thief methods
	//========================================================================================================================//
	/**
	 * Returns the distance of the rooms.
	 *
	 * @return room_distance array containing every room distance
	 */
	@Override
	public synchronized int[] startOperations() {
		return rooms_distance;
	}

	//========================================================================================================================//
	// Thief methods
	//========================================================================================================================//
	/**
	 * <p>
	 * Verifies if the rooms has canvas or not.</p>
	 * <p>
	 * If the room is not empty, increments the number of stolen paintings and decrements the number of paintings in the room.</p>
	 * <p>
	 * Prints a log line after taking the canvas.</p>
	 *
	 * @param thief_id thief id
	 * @param target_room thief target room index
	 * @return stolen_canvas
	 * <ul>
	 * <li>1 if there where any canvas left</li>
	 * <li>0 if room was already empty</li>
	 * </ul>
	 */
	@Override
	public synchronized int rollACanvas(int thief_id, int target_room) {
		int stolen_canvas = 0;
		// checks if room is not empty, if so decrements number of room canvas by one and increments stolen cnavas
		if (this.rooms_paintings[target_room] != 0) {
			stolen_canvas++;
			this.rooms_paintings[target_room]--;
		}
		// logs line
		// this.repository.logLine_MuseumUpdateSingle(target_room, rooms_paintings[target_room]);
		// returns stolen canvas
		return stolen_canvas;
	}
}
