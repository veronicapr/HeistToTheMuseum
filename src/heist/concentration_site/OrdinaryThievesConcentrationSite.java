/*
 * Ordinary Thieves Concentration Site
 */
package heist.concentration_site;

import genclass.GenericIO;
import heist.concentration_site.interfaces.It_MasterThief_ConcentrationSite;
import heist.concentration_site.interfaces.It_Thief_ConcentrationSite;
import heist.repository.interfaces.It_Repository_ConcentrationSite;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import settings.HeistSettings;

/**
 * Waiting place for all thieves awaiting to be called to work.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class OrdinaryThievesConcentrationSite extends UnicastRemoteObject implements It_MasterThief_ConcentrationSite, It_Thief_ConcentrationSite, Serializable {

	//========================================================================================================================//
	// Concentration site data
	//========================================================================================================================//
	/**
	 * Flag for heist completion
	 */
	private boolean heist_complete;
	/**
	 * Flags for prepared teams
	 */
	private final boolean[] prepared_teams = new boolean[HeistSettings.TOTAL_TEAMS];
	/**
	 * Prepared team member count
	 */
	private final int[] member_count = new int[HeistSettings.TOTAL_TEAMS];

	//========================================================================================================================//
	// Concentration site constructor
	//========================================================================================================================//
	/**
	 * Constructor for Ordinary Thieves Concentration Site, also forces initialisation of member_count at 0 and prepared_teams as false.
	 *
	 * @throws java.rmi.RemoteException
	 */
	private OrdinaryThievesConcentrationSite() throws RemoteException {
		super(port_number);
		this.heist_complete = false;
		for (int index = 0; index < HeistSettings.TOTAL_TEAMS; index++) {
			this.prepared_teams[index] = false;
			this.member_count[index] = 0;
		}
	}

	//========================================================================================================================//
	// Concentration site server info and main
	//========================================================================================================================//
	/**
	 * Ordinary Thieves Concentration Site object reference [singleton]
	 */
	private static OrdinaryThievesConcentrationSite self;
	/**
	 * Object port number
	 */
	private static int port_number;
	/**
	 * Registry host name
	 */
	private static String repository_host_name;
	/**
	 * Registry port number
	 */
	private static int registry_port_number;

	/**
	 * Ordinary Thieves Concentration Site server start, requires 3 argument.
	 *
	 * @param args program arguments should be:
	 * <ul>
	 * <li>self port number</li>
	 * <li>repository host name</li>
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
				repository_host_name = args[1];
				registry_port_number = Integer.parseInt(args[2]);
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
		// regist concentration site
		try {
			self = new OrdinaryThievesConcentrationSite();
			LocateRegistry.createRegistry(registry_port_number).rebind("Concentration_Site", self);
			GenericIO.writelnString("Concentration Site bound!");
		} catch (RemoteException ex) {
			GenericIO.writelnString("Regist exception: " + ex.getMessage());
			System.exit(1);
		}
		// ready message
		GenericIO.writelnString("Ordinary Thieves Concentration Site server ready!");
	}

	//========================================================================================================================//
	// Master thief methods
	//========================================================================================================================//
	/**
	 * Sets the assault party with given id as prepared and proceeds to notify all thieves of the occurrence.
	 *
	 * @param assault_party_id id of assault party to be added
	 */
	@Override
	public synchronized void sendAssaultParty(int assault_party_id) {
		// sets team flag as true
		this.prepared_teams[assault_party_id] = true;
		// notifies all thieves
		notifyAll();
	}

	/**
	 * Sets heist_complete flag as true and notifies all thieves of this alteration setting them for termination. Finalises the log file by adding its finishing
	 * lines.
	 */
	@Override
	public synchronized void sumUpResults(int clock) {
		// sets completion flag as true
		this.heist_complete = true;
		// notifies all thieves
		notifyAll();
		// finishes log
		try {
			((It_Repository_ConcentrationSite) LocateRegistry.getRegistry(repository_host_name, registry_port_number).lookup("General_Repository"))
					.logFinish_ConcentrationSiteUpdate(clock);
		} catch (RemoteException ex) {
			GenericIO.writelnString("Remote Exception (sum up results): " + ex.getMessage());
			System.exit(1);
		} catch (NotBoundException ex) {
			GenericIO.writelnString("Not Bound Exception (sum up results):  " + ex.getMessage());
			System.exit(1);
		}
	}

	//========================================================================================================================//
	// Thief methods
	//========================================================================================================================//
	/**
	 * Sets thief in a waiting cycle where each time he is awoken checks if heist_complete flag is true, if not proceeds to verify if his team is in the
	 * prepared teams queue if there are any on it. If he is the last team member to get ready, un-checks the team as prepared.
	 *
	 * Waiting cycle continues until:
	 * <ul>
	 * <li>team verification results as his team is on the prepared queue,</li>
	 * <li>heist_complete flag is set as true.</li>
	 * </ul>
	 *
	 * @param assault_party_id thief assault party id
	 * @return
	 * <ul>
	 * <li>-1 if heist is complete</li>
	 * <li>1 if team is prepared</li>
	 * <li>0 if interrupted</li>
	 * </ul>
	 */
	@Override
	public synchronized int amINeeded(int assault_party_id) {
		while (!heist_complete) {
			// checks if team is prepared then adds to member count if so, afterwards, if last one, un-checks team as prepared
			if (this.prepared_teams[assault_party_id]) {
				this.member_count[assault_party_id]++;
				if (this.member_count[assault_party_id] == HeistSettings.TEAM_SIZE) {
					this.member_count[assault_party_id] = 0;
					this.prepared_teams[assault_party_id] = false;
				}
				break;
			}
			// waits until awoken
			try {
				wait();
			} catch (InterruptedException ex) {
				return 0;
			}
		}
		// if heist is complete stops, otherwise prepares for excursion
		if (heist_complete) {
			return -1;
		} else {
			return 1;
		}
	}
}
