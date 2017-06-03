/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heist.threads;

import genclass.GenericIO;
import heist.assault_party.interfaces.It_MasterThief_AssaultParty;
import heist.concentration_site.interfaces.It_MasterThief_ConcentrationSite;
import heist.control_site.interfaces.It_MasterThief_ControlCollectionSite;
import heist.enums.State_MasterThief;
import heist.museum.interfaces.It_MasterThief_Museum;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Master thief.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class MasterThief extends Thread {

	//========================================================================================================================//
	/**
	 * Master thief current state
	 */
	private State_MasterThief state;
	/**
	 * Total of assault parties
	 */
	private final int total_teams;
	/**
	 * Current assigned parties
	 */
	private final boolean[] assigned_teams;
	/**
	 * Current sent parties
	 */
	private final boolean[] sent_teams;
	/**
	 * Museum room distances
	 */
	private int[] room_distances;
	/**
	 * Returned assault party id
	 */
	private int returned_team;
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
	 * Constructor for the Master Thief Thread.
	 *
	 * @param total_teams total teams
	 * @param host_name host name
	 * @param port port number
	 */
	public MasterThief(int total_teams, String host_name, int port) {

		server_host_name = host_name;
		server_port_number = port;

		this.total_teams = total_teams;
		this.assigned_teams = new boolean[total_teams];
		this.sent_teams = new boolean[total_teams];
		for (int index = 0; index < total_teams; index++) {
			this.assigned_teams[index] = false;
			this.sent_teams[index] = false;
		}

		this.state = State_MasterThief.PLANNING_THE_HEIST;
		this.setName("Master Thief");
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
	 * Master thief life cycle.
	 */
	@Override
	public void run() {

		int target_room;
		int team_index;
		while (true) {
			switch (this.state) {
				case PLANNING_THE_HEIST:
					try {
						this.room_distances = ((It_MasterThief_Museum) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Museum"))
								.startOperations();
						this.state = State_MasterThief.DECIDING_WHAT_TO_DO;
						//Fazer Log

					} catch (RemoteException ex) {
						GenericIO.writelnString("Remote Exception (main log line): " + ex.getMessage());
					} catch (NotBoundException ex) {
						GenericIO.writelnString("Not Bound Exception (main log line):  " + ex.getMessage());
					}
					break;
				case DECIDING_WHAT_TO_DO:

					try {
						target_room = ((It_MasterThief_ControlCollectionSite) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Control_Site"))
								.appraiseSit();
						this.state = State_MasterThief.DECIDING_WHAT_TO_DO;
						//Fazer Log

						switch (target_room) {
							case -1:
								this.state = State_MasterThief.WAITING_FOR_GROUP_ARRIVAL;

								this.returned_team = ((It_MasterThief_ControlCollectionSite) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Control_Site"))
										.takeARest();

								//Fazer Log
								break;
							case -2:
								boolean no_assigned_teams = true;
								for (int index = 0; index < total_teams; index++) {
									if (this.assigned_teams[index]) {
										no_assigned_teams = false;
										break;
									}
								}
								if (no_assigned_teams) {
									((It_MasterThief_ConcentrationSite) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Concentration_Site"))
											.sumUpResults();
									this.state = State_MasterThief.PRESENTING_THE_REPORT;
								}
								//Fazer log
								break;
							default:
								for (team_index = 0; team_index < this.total_teams; team_index++) {
									if (!this.assigned_teams[team_index]) {
										this.assigned_teams[team_index] = true;
										break;
									}
								}
								this.state = State_MasterThief.ASSEMBLING_A_GROUP;
								((It_MasterThief_AssaultParty) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Assault_Party_" + team_index))
								.prepareAssaultParty(target_room, this.room_distances[target_room]);
								//Dazer log
								break;
						}
					} catch (RemoteException ex) {
						GenericIO.writelnString("Remote Exception (main log line): " + ex.getMessage());
					} catch (NotBoundException ex) {
						GenericIO.writelnString("Not Bound Exception (main log line):  " + ex.getMessage());
					}
					break;
				case ASSEMBLING_A_GROUP:
					for (team_index = 0; team_index < this.total_teams; team_index++) {
						if (this.assigned_teams[team_index] && !this.sent_teams[team_index]) {
							this.sent_teams[team_index] = true;
							break;
						}
					}

					try {
						((It_MasterThief_ConcentrationSite) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Concentration_Site"))
								.sendAssaultParty(team_index);
						this.state = State_MasterThief.DECIDING_WHAT_TO_DO;
						//Fazer Log

					} catch (RemoteException ex) {
						GenericIO.writelnString("Remote Exception (main log line): " + ex.getMessage());
					} catch (NotBoundException ex) {
						GenericIO.writelnString("Not Bound Exception (main log line):  " + ex.getMessage());
					}
					break;
				case WAITING_FOR_GROUP_ARRIVAL:

					try {
						((It_MasterThief_ControlCollectionSite) LocateRegistry.getRegistry(registry_host_name, registry_port_number).lookup("Control_Site"))
								.collectCanvas(this.returned_team);
						this.state = State_MasterThief.DECIDING_WHAT_TO_DO;
						//Fazer Log

					} catch (RemoteException ex) {
						GenericIO.writelnString("Remote Exception (main log line): " + ex.getMessage());
					} catch (NotBoundException ex) {
						GenericIO.writelnString("Not Bound Exception (main log line):  " + ex.getMessage());
					}

					if (returned_team > -1) {
						this.assigned_teams[this.returned_team] = false;
						this.sent_teams[this.returned_team] = false;
					}
					this.state = State_MasterThief.DECIDING_WHAT_TO_DO;
					break;
				case PRESENTING_THE_REPORT:
					break;
				default:
					break;
			}
			/* Thread stopping condition */
			if (this.state == State_MasterThief.PRESENTING_THE_REPORT) {
				break;
			}
		}
	}
}
