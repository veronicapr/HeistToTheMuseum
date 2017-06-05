/*
 * Thief
 */
package heist.thieves;

import clock_vector.ClockVector;
import genclass.GenericIO;
import heist.assault_party.interfaces.It_Thief_AssaultParty;
import heist.concentration_site.interfaces.It_Thief_ConcentrationSite;
import heist.control_site.interfaces.It_Thief_ControlSite;
import heist.enums.State_Thief;
import heist.museum.interfaces.It_Thief_Museum;
import heist.repository.interfaces.It_Repository_Thief;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Random;
import settings.HeistSettings;

/**
 * Ordinary thief.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class Thief extends Thread implements Serializable {

	//========================================================================================================================//
	// Thief data
	//========================================================================================================================//
	/**
	 * Thief clock
	 */
	private final ClockVector clock = new ClockVector();
	/**
	 * Thief id
	 */
	private final int id;
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

	//========================================================================================================================//
	// Thief contructor
	//========================================================================================================================//
	/**
	 * Constructor for the Thief.
	 *
	 * @param id thief id
	 * @param assault_party_id assault party id
	 */
	public Thief(int id, int assault_party_id) {
		super();
		this.id = id;
		this.setName("Thief " + this.id);
		Random rand = new Random();
		this.agility = rand.nextInt((HeistSettings.MAX_AGILITY - HeistSettings.MIN_AGILITY) + 1) + HeistSettings.MIN_AGILITY;
		this.state = State_Thief.OUTSIDE;
		this.target_room = 0;
		this.stolen_canvas = 0;
		this.assault_party_id = assault_party_id;
	}

	//========================================================================================================================//
	// Thief client info and main
	//========================================================================================================================//
	/**
	 * Thief object references [singleton]
	 */
	private static Thief[] self;
	/**
	 * Repository host name
	 */
	private static String repository_host_name;

	/**
	 * Museum host name
	 */
	private static String museum_host_name;

	/**
	 * Assault party host name
	 */
	private static String assault_party_host_name;

	/**
	 * Control site host name
	 */
	private static String control_site_host_name;

	/**
	 * Concentration site host name
	 */
	private static String concentration_site_host_name;
	/**
	 * Registry port number
	 */
	private static int registry_port_number;

	/**
	 * General repository server start, requires 6 argument.
	 *
	 * @param args program arguments should be:
	 * <ul>
	 * <li>repository host name</li>
	 * <li>museum host name</li>
	 * <li>assault party host name</li>
	 * <li>control site host name</li>
	 * <li>concentration site host name</li>
	 * <li>registry port number</li>
	 * </ul>
	 */
	public static void main(String[] args) {
		if (args.length != 6) {
			GenericIO.writelnString("Wrong number of arguments!");
			System.exit(1);
		} else {
			try {
				repository_host_name = args[0];
				museum_host_name = args[1];
				assault_party_host_name = args[2];
				control_site_host_name = args[3];
				concentration_site_host_name = args[4];
				registry_port_number = Integer.parseInt(args[5]);
			} catch (NumberFormatException ex) {
				GenericIO.writelnString("Port number must be an integer!");
				System.exit(1);
			}
		}
		// security manager
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		GenericIO.writelnString("Security manager was installed!");
		// regist repository
		try {
			self = new Thief[HeistSettings.TOTAL_THIEVES];
			for (int index = 0; index < HeistSettings.TOTAL_THIEVES; index++) {
				self[index] = new Thief(index, index / HeistSettings.TEAM_SIZE);
				self[index].clock.increment();
				self[index].clock.updateTime(((It_Repository_Thief) LocateRegistry.getRegistry(repository_host_name, registry_port_number).lookup("General_Repository"))
						.logLine_ThiefUpdateFull(self[index].clock.getTime(), self[index].id, self[index].assault_party_id, self[index].agility, self[index].stolen_canvas, self[index].state));
			}
		} catch (RemoteException ex) {
			GenericIO.writelnString("Remote Exception (main log line): " + ex.getMessage());
			System.exit(1);
		} catch (NotBoundException ex) {
			GenericIO.writelnString("Not Bound Exception (main log line):  " + ex.getMessage());
			System.exit(1);
		}
		// ready message
		GenericIO.writelnString("Thief ready, start running!");
		for (int index = 0; index < HeistSettings.TOTAL_THIEVES; index++) {
			self[index].start();
		}
	}

	//========================================================================================================================//
	// Thread run
	//========================================================================================================================//
	/**
	 * Thief life cycle.
	 */
	@Override
	public void run() {
		String method_name = "???";
		boolean hasRolledACanvas = false;
		int wake_type;
		while (true) {
			try {
				switch (state) {
					case OUTSIDE:
						if (hasRolledACanvas) {
							// if the thief made a excursion, he hands the results of it to the master thief otherwise awaits for the next order from the master thief
							method_name = "OUTSIDE [has rolled a canvas] - hand a canvas";
							clock.increment();
							clock.increment();
							((It_Thief_ControlSite) LocateRegistry.getRegistry(control_site_host_name, registry_port_number).lookup("Control_Site"))
									.handACanvas(id, assault_party_id, target_room, stolen_canvas);
							hasRolledACanvas = false;
							stolen_canvas = 0;
							method_name = "OUTSIDE [has rolled a canvas] - state and canvas update";
							clock.increment();
							clock.updateTime(((It_Repository_Thief) LocateRegistry.getRegistry(repository_host_name, registry_port_number).lookup("General_Repository"))
									.logLine_ThiefUpdateStateCanvas(clock.getTime(), id, stolen_canvas, state));
							break;
						} else {
							// depending on the order he either chooses to prepare for an excursion or goes to listen to the report
							method_name = "OUTSIDE [wake type] - am i needed";
							clock.increment();
							wake_type = ((It_Thief_ConcentrationSite) LocateRegistry.getRegistry(concentration_site_host_name, registry_port_number).lookup("Concentration_Site"))
									.amINeeded(assault_party_id);
							switch (wake_type) {
								case 1:
									method_name = "OUTSIDE [case 1] - prepare excursion";
									clock.increment();
									this.target_room = ((It_Thief_AssaultParty) LocateRegistry.getRegistry(assault_party_host_name, registry_port_number).lookup("Assault_Party_" + assault_party_id))
											.prepareExcursion(id);
									this.state = State_Thief.CRAWLING_INWARDS;
									method_name = "OUTSIDE [case 1] - state update";
									clock.increment();
									clock.updateTime(((It_Repository_Thief) LocateRegistry.getRegistry(repository_host_name, registry_port_number).lookup("General_Repository"))
											.logLine_ThiefUpdateState(clock.getTime(), id, state));
									break;
								case -1:
									// no need to update state as it is updated before closing the file
									this.state = State_Thief.HEAR_REPORT;
									break;
								default:
									// if thief wait is interrupted
									break;
							}
							break;
						}
					case CRAWLING_INWARDS:
						// initial part of the excursion, the thief crawls inwards toward the museum room
						method_name = "CRAWLING_INWARDS - crawl in";
						clock.increment();
						clock.increment();
						state = ((It_Thief_AssaultParty) LocateRegistry.getRegistry(assault_party_host_name, registry_port_number).lookup("Assault_Party_" + assault_party_id))
								.crawlIn(id, agility);
						if (state == State_Thief.AT_A_ROOM) {
							method_name = "CRAWLING_INWARDS [next state is at a room] - state update";
							clock.increment();
							clock.updateTime(((It_Repository_Thief) LocateRegistry.getRegistry(repository_host_name, registry_port_number).lookup("General_Repository"))
									.logLine_ThiefUpdateState(clock.getTime(), id, state));
						}
						break;
					case AT_A_ROOM:
						if (!hasRolledACanvas) {
							// if thief just arrived to the room and hasn't rolled a canvas, rolls one
							method_name = "AT_A_ROOM - roll a canvas";
							clock.increment();
							clock.increment();
							stolen_canvas = ((It_Thief_Museum) LocateRegistry.getRegistry(museum_host_name, registry_port_number).lookup("Museum"))
									.rollACanvas(id, target_room);
							method_name = "AT_A_ROOM - state and canvas update";
							clock.increment();
							clock.updateTime(((It_Repository_Thief) LocateRegistry.getRegistry(repository_host_name, registry_port_number).lookup("General_Repository"))
									.logLine_ThiefUpdateStateCanvas(clock.getTime(), id, stolen_canvas, state));
							hasRolledACanvas = true;
							break;
						} else {
							// awaits for entire team arrival to the room before start returning to camp
							method_name = "AT_A_ROOM - reverse direction";
							clock.increment();
							boolean reverse = ((It_Thief_AssaultParty) LocateRegistry.getRegistry(assault_party_host_name, registry_port_number).lookup("Assault_Party_" + assault_party_id))
									.reverseDirection();
							if (reverse) {
								state = State_Thief.CRAWLING_OUTWARDS;
								method_name = "AT_A_ROOM - state update";
								clock.increment();
								clock.updateTime(((It_Repository_Thief) LocateRegistry.getRegistry(repository_host_name, registry_port_number).lookup("General_Repository"))
										.logLine_ThiefUpdateState(clock.getTime(), id, state));
							}
							break;
						}
					case CRAWLING_OUTWARDS:
						// final part of the excursion, the thief crawls outwards toward the concFFentration site
						method_name = "CRAWLING_OUTWARDS - crawl out";
						clock.increment();
						clock.increment();
						this.state = ((It_Thief_AssaultParty) LocateRegistry.getRegistry(assault_party_host_name, registry_port_number).lookup("Assault_Party_" + assault_party_id))
								.crawlOut(id, agility);
						if (this.state == State_Thief.OUTSIDE) {
							method_name = "CRAWLING_OUTWARDS [next state is outside] - state update";
							clock.increment();
							clock.updateTime(((It_Repository_Thief) LocateRegistry.getRegistry(repository_host_name, registry_port_number).lookup("General_Repository"))
									.logLine_ThiefUpdateState(clock.getTime(), id, state));
						}
						break;
					case HEAR_REPORT:
						// does nothing
						break;
					default:
						// shouldn't get here
						break;
				}

			} catch (RemoteException ex) {
				GenericIO.writelnString("Remote Exception (" + method_name + "): " + ex.getMessage());
				System.exit(1);
			} catch (NotBoundException ex) {
				GenericIO.writelnString("Not Bound Exception (" + method_name + "):  " + ex.getMessage());
				System.exit(1);
			}
			// Thread stopping condition
			if (this.state == State_Thief.HEAR_REPORT) {
				break;
			}
		}
	}
}
