/*
 * It Repository ControlSite
 */
package heist.repository.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Contains the log line methods called by Control Site on the repository.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_Repository_ControlSite extends Remote {

	/**
	 * Log line containing updated info over stolen canvas.
	 *
	 * @param stolen_canvas number of stolen canvas
	 * @throws java.rmi.RemoteException
	 */
	public void logLine_ControlSiteUpdate(int stolen_canvas) throws RemoteException;
}
