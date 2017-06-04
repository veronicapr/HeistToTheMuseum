/*
 * It MasterThief ConcentrationSite
 */
package heist.concentration_site.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Contains the methods called by the master thief in Ordinary Thieves Concentration Site.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_MasterThief_ConcentrationSite extends Remote {

	/**
	 * Sets the assault party with given id as prepared and proceeds to notify all thieves of the occurrence.
	 *
	 * @param assault_party_id id of assault party to be added
	 * @throws java.rmi.RemoteException
	 */
	void sendAssaultParty(int assault_party_id) throws RemoteException;

	/**
	 * Sets heist_complete flag as true and notifies all thieves of this alteration setting them for termination. Finalises the log file by adding its finishing
	 * lines.
	 *
	 * @throws java.rmi.RemoteException
	 */
	void sumUpResults() throws RemoteException;
}
