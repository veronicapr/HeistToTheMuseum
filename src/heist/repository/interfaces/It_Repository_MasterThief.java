/*
 * It Repository MasterThief 
 */
package heist.repository.interfaces;

import heist.enums.State_MasterThief;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Contains the log line methods called by the master thief on the repository.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_Repository_MasterThief extends Remote{

	/**
	 * Log line containing updated master thief info over its state.
	 *
	 * @param state updated thief state
	 * @throws java.rmi.RemoteException
	 */
	public void logLine_MasterThiefUpdateState(State_MasterThief state) throws RemoteException;
}
