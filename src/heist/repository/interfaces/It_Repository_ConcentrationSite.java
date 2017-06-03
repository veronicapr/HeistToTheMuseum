/*
 * It_Repository_ConcentrationSite
 */
package heist.repository.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Contains the log line methods called by ConcentrationSite on the repository.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_Repository_ConcentrationSite extends Remote {

	/**
	 * Finalises log, updates all states from master thief and thieves to its final states, log a line with updated info and the final messages.
	 *
	 * @throws java.rmi.RemoteException
	 */
	public void logFinish_ConcentrationSiteUpdate() throws RemoteException;

}
