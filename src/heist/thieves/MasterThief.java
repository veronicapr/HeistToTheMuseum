/*
 * MasterThief
 */
package heist.thieves;

import genclass.GenericIO;
import heist.assault_party.interfaces.It_MasterThief_AssaultParty;
import heist.concentration_site.interfaces.It_MasterThief_ConcentrationSite;
import heist.enums.State_MasterThief;
import heist.museum.interfaces.It_MasterThief_Museum;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import heist.control_site.interfaces.It_MasterThief_ControlSite;
import heist.repository.interfaces.It_Repository_MasterThief;
import settings.HeistSettings;

/**
 * Master thief.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class MasterThief extends Thread {

	//========================================================================================================================//
	// Master thief data
	//========================================================================================================================//
	/**
	 * Master thief current state
	 */
	private State_MasterThief state;
	/**
	 * Current assigned parties
	 */
	private final boolean[] assigned_teams = new boolean[HeistSettings.TOTAL_TEAMS];
	/**
	 * Current sent parties
	 */
	private final boolean[] sent_teams = new boolean[HeistSettings.TOTAL_TEAMS];
	/**
	 * Museum room distances
	 */
	private int[] room_distances;

	//========================================================================================================================//
	// Master thief contructor
	//========================================================================================================================//
	/**
	 * Constructor for the Master Thief Thread.
	 */
	public MasterThief() {
		super();
		this.setName("Master Thief");
		this.state = State_MasterThief.PLANNING_THE_HEIST;
		for (int index = 0; index < HeistSettings.TOTAL_TEAMS; index++) {
			this.assigned_teams[index] = false;
			this.sent_teams[index] = false;
		}
	}

	//========================================================================================================================//
	// Thief client info and main
	//========================================================================================================================//
	/**
	 * Master thief object references [singleton]
	 */
	private static MasterThief self;
	/**
	 * Registry host name
	 */
	private static String registry_host_name;
	/**
	 * Registry port number
	 */
	private static int registry_port_number;

	/**
	 * General repository server start, requires 2 argument.
	 *
	 * @param args program arguments should be:
	 * <ul>
	 * <li>registry host name</li>
	 * <li>registry port number</li>
	 * </ul>
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			GenericIO.writelnString("Wrong number of arguments!");
			System.exit(1);
		} else {
			try {
				registry_host_name = args[0];
				registry_port_number = Integer.parseInt(args[1]);
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
			self = new MasterThief();
			((It_Repository_MasterThief) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Control_Site"))
					.logLine_MasterThiefUpdateState(self.state);
		} catch (RemoteException ex) {
			GenericIO.writelnString("Remote Exception (main log line): " + ex.getMessage());
			System.exit(1);
		} catch (NotBoundException ex) {
			GenericIO.writelnString("Not Bound Exception (main log line):  " + ex.getMessage());
			System.exit(1);
		}
		// ready message
		GenericIO.writelnString("Master thief ready, start running!");
		self.start();
	}

	//========================================================================================================================//
	// Thread run
	//========================================================================================================================//
	/**
	 * Master thief life cycle.
	 */
	@Override
	public void run() {
		String method_name = "???";
		int returned_team = -1;
		int target_room;
		int team_index;
		while (true) {
			try {
				switch (state) {
					case PLANNING_THE_HEIST:
						method_name = "PLANNING_THE_HEIST - start operations";
						room_distances = ((It_MasterThief_Museum) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Museum"))
								.startOperations();
						state = State_MasterThief.DECIDING_WHAT_TO_DO;
						method_name = "PLANNING_THE_HEIST - state update";
						((It_Repository_MasterThief) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Control_Site"))
								.logLine_MasterThiefUpdateState(state);
						break;
					case DECIDING_WHAT_TO_DO:
						target_room = ((It_MasterThief_ControlSite) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Control_Site"))
								.appraiseSit();
						switch (target_room) {
							case -1:
								state = State_MasterThief.WAITING_FOR_GROUP_ARRIVAL;
								method_name = "DECIDING_WHAT_TO_DO [case 1] - state update";
								((It_Repository_MasterThief) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Control_Site"))
										.logLine_MasterThiefUpdateState(state);
								method_name = "DECIDING_WHAT_TO_DO [case 1] - take a rest";
								returned_team = ((It_MasterThief_ControlSite) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Control_Site"))
										.takeARest();
								break;
							case -2:
								boolean no_assigned_teams = true;
								for (int index = 0; index < HeistSettings.TOTAL_TEAMS; index++) {
									if (assigned_teams[index]) {
										no_assigned_teams = false;
										break;
									}
								}
								if (no_assigned_teams) {
									((It_MasterThief_ConcentrationSite) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Concentration_Site"))
											.sumUpResults();
									// no need to update state as it is updated before closing the file
									state = State_MasterThief.PRESENTING_THE_REPORT;
								}
								break;
							default:
								for (team_index = 0; team_index < HeistSettings.TOTAL_TEAMS; team_index++) {
									if (!assigned_teams[team_index]) {
										assigned_teams[team_index] = true;
										break;
									}
								}
								state = State_MasterThief.ASSEMBLING_A_GROUP;
								method_name = "DECIDING_WHAT_TO_DO [case default] - state update";
								((It_Repository_MasterThief) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Control_Site"))
										.logLine_MasterThiefUpdateState(state);
								method_name = "DECIDING_WHAT_TO_DO [case default] - prepare assault party";
								((It_MasterThief_AssaultParty) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Assault_Party_" + team_index))
										.prepareAssaultParty(target_room, room_distances[target_room]);
								break;
						}
						break;
					case ASSEMBLING_A_GROUP:
						for (team_index = 0; team_index < HeistSettings.TOTAL_TEAMS; team_index++) {
							if (assigned_teams[team_index] && !sent_teams[team_index]) {
								sent_teams[team_index] = true;
								break;
							}
						}
						method_name = "ASSEMBLING_A_GROUP - send assault party";
						((It_MasterThief_ConcentrationSite) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Concentration_Site"))
								.sendAssaultParty(team_index);
						state = State_MasterThief.DECIDING_WHAT_TO_DO;
						method_name = "ASSEMBLING_A_GROUP - state update";
						((It_Repository_MasterThief) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Control_Site"))
								.logLine_MasterThiefUpdateState(state);
						break;
					case WAITING_FOR_GROUP_ARRIVAL:
						method_name = "WAITING_FOR_GROUP_ARRIVAL - collect canvas";
						((It_MasterThief_ControlSite) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Control_Site"))
								.collectCanvas(returned_team);
						if (returned_team > -1) {
							this.assigned_teams[returned_team] = false;
							this.sent_teams[returned_team] = false;
						}
						this.state = State_MasterThief.DECIDING_WHAT_TO_DO;
						method_name = "WAITING_FOR_GROUP_ARRIVAL - state update";
						((It_Repository_MasterThief) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Control_Site"))
								.logLine_MasterThiefUpdateState(state);
						break;
					case PRESENTING_THE_REPORT:
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
			/* Thread stopping condition */
			if (this.state == State_MasterThief.PRESENTING_THE_REPORT) {
				break;
			}
		}
	}
}
