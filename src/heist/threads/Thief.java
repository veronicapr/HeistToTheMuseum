/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heist.threads;

import genclass.GenericIO;
import heist.assault_party.interfaces.It_Thief_AssaultParty;
import heist.concentration_site.interfaces.It_Thief_ConcentrationSite;
import heist.control_site.interfaces.It_Thief_ControlCollectionSite;
import heist.enums.State_Thief;
import heist.museum.interfaces.It_Thief_Museum;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Random;

/**
 * Ordinary thief.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class Thief extends Thread {

	//========================================================================================================================//
	/**
	 * Thief id
	 */
	private final int id;
	//========================================================================================================================//
	/**
	 * Thief has rolled a canvas
	 */
	private boolean hasRolledACanvas;
	/**
	 * Assault party id
	 */
	private final int assault_party_id;
	/**
	 * Thief agility, used for movement
	 */
	private final int agility;
	/**
	 * Current state
	 */
	private State_Thief state;
	/**
	 * Holding canvas
	 */
	private int stolen_canvas;
	/**
	 * Thief target room
	 */
	private int target_room;
	/**
	 * Server host name
	 */
	private final String server_host_name;
	/**
	 * Server port number
	 */
	private final int server_port_number;

	//========================================================================================================================//
	/**
	 * Constructor for the Thief.
	 *
	 * @param id thief id
	 * @param assault_party_id assault party id
	 * @param min_agility thief minimum agility
	 * @param max_agility thief maximum agility
	 * @param host_name server host name
	 * @param port server port number
	 */
	public Thief(int id, int assault_party_id, int min_agility, int max_agility, String host_name, int port) {
		this.id = id;

		server_host_name = host_name;
		server_port_number = port;

		this.target_room = 0;
		this.stolen_canvas = 0;
		this.hasRolledACanvas = false;
		this.assault_party_id = assault_party_id;

		Random rand = new Random();
		this.agility = rand.nextInt((max_agility - min_agility) + 1) + min_agility;

		this.state = State_Thief.OUTSIDE;
		this.setName("Thief " + this.id);
	}

	/**
	 * Registry host name
	 */
	private static String registry_host_name;
	/**
	 * Registry port number
	 */
	private static int registry_port_number;

	//========================================================================================================================//
	/**
	 * Thief life cycle.
	 */
	@Override
	public void run() {
		int wake_type;

		while (true) {
			switch (this.state) {
				case OUTSIDE:
					// if the thief made a excursion, he hands the results of it to the master thief otherwise awaits for the next order from the master thief
					if (this.hasRolledACanvas) {
						try {
							((It_Thief_ControlCollectionSite) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Control_Site"))
									.handACanvas(this.state, this.id, this.assault_party_id, this.target_room, this.stolen_canvas);
							//Fazer Log ?

						} catch (RemoteException ex) {
							GenericIO.writelnString("Remote Exception (main log line): " + ex.getMessage());
						} catch (NotBoundException ex) {
							GenericIO.writelnString("Not Bound Exception (main log line):  " + ex.getMessage());
						}
						this.stolen_canvas = 0;
						this.hasRolledACanvas = false;
						break;
					} else {
						try {
							wake_type = ((It_Thief_ConcentrationSite) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Conentration_Site"))
									.amINeeded(this.assault_party_id);

							// depending on the order he either chooses to prepare for an excursion or goes to listen to the report
							switch (wake_type) {
								case 1:
									this.target_room = ((It_Thief_AssaultParty) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Assault_Party_" + this.assault_party_id))
											.prepareExcursion(this.id);
									this.state = State_Thief.CRAWLING_INWARDS;
									break;
								default:
									this.state = State_Thief.HEAR_REPORT;
									break;
							}
						} catch (RemoteException ex) {
							GenericIO.writelnString("Remote Exception (main log line): " + ex.getMessage());
						} catch (NotBoundException ex) {
							GenericIO.writelnString("Not Bound Exception (main log line):  " + ex.getMessage());
						}
						break;
					}
				case CRAWLING_INWARDS:
					// initial part of the excursion, the thief crawls inwards toward the museum room
					try {
						this.state = ((It_Thief_AssaultParty) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Assault_Party_" + this.assault_party_id))
								.crawlIn(this.state, this.id, this.agility);
						//Fazer Log ?

					} catch (RemoteException ex) {
						GenericIO.writelnString("Remote Exception (main log line): " + ex.getMessage());
					} catch (NotBoundException ex) {
						GenericIO.writelnString("Not Bound Exception (main log line):  " + ex.getMessage());
					}
					break;
				case AT_A_ROOM:
					// if thief just arrived to the room and hasn't rolled a canvas, rolls one otherwise awaits for entire team arrival to the room before start returning to camp
					if (!this.hasRolledACanvas) {
						try {
							this.stolen_canvas = ((It_Thief_Museum) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Museum"))
									.rollACanvas(this.id, this.target_room);
							//Fazer Log ?

						} catch (RemoteException ex) {
							GenericIO.writelnString("Remote Exception (main log line): " + ex.getMessage());
						} catch (NotBoundException ex) {
							GenericIO.writelnString("Not Bound Exception (main log line):  " + ex.getMessage());
						}
						this.hasRolledACanvas = true;
						break;
					} else {
						boolean reverse = false;
						try {
							reverse = ((It_Thief_AssaultParty) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Assault_Party_" + this.assault_party_id))
									.reverseDirection();
							//Fazer Log ?

						} catch (RemoteException ex) {
							GenericIO.writelnString("Remote Exception (main log line): " + ex.getMessage());
						} catch (NotBoundException ex) {
							GenericIO.writelnString("Not Bound Exception (main log line):  " + ex.getMessage());
						}
						
						if (reverse) {
							this.state = State_Thief.CRAWLING_OUTWARDS;
						} else {
							this.state = State_Thief.AT_A_ROOM;
						}
						break;
					}
				case CRAWLING_OUTWARDS:
					// final part of the excursion, the thief crawls outwards toward the concentration site
					try {
						this.state = ((It_Thief_AssaultParty) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Assault_Party_" + this.assault_party_id))
								.crawlOut(this.state, this.id, this.agility);
						//Fazer Log ?

					} catch (RemoteException ex) {
						GenericIO.writelnString("Remote Exception (main log line): " + ex.getMessage());
					} catch (NotBoundException ex) {
						GenericIO.writelnString("Not Bound Exception (main log line):  " + ex.getMessage());
					}
					break;
				case HEAR_REPORT:
					// does nothing
					break;
				default:
					// shouldn't get here
					break;
			}
			// Thread stopping condition
			if (this.state == State_Thief.HEAR_REPORT) {
				break;
			}
		}
	}
}
