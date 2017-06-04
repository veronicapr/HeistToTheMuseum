/*
 * It Repository Thief
 */
package heist.repository.interfaces;

import heist.enums.State_Thief;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Contains the log line methods called by the thieves on the repository.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_Repository_Thief extends Remote {

	/**
	 * Log line containing full updated thief info.
	 *
	 * @param clock caller thief clock
	 * @param thief_id thief identification
	 * @param assault_party_id thief assault party id
	 * @param agility thief agility
	 * @param stolen_canvas updated current number of canvas in hold
	 * @param state updated thief state
	 * @return updated clock
	 * @throws java.rmi.RemoteException
	 */
	public int logLine_ThiefUpdateFull(int clock, int thief_id, int assault_party_id, int agility, int stolen_canvas, State_Thief state) throws RemoteException;

	/**
	 * Log line containing updated thief info over stolen canvas and state.
	 *
	 * @param clock caller thief clock
	 * @param thief_id thief identification
	 * @param stolen_canvas updated current number of canvas in hold
	 * @param state updated thief state
	 * @return updated clock
	 * @throws java.rmi.RemoteException
	 */
	public int logLine_ThiefUpdateStateCanvas(int clock, int thief_id, int stolen_canvas, State_Thief state) throws RemoteException;

	/**
	 * Log line containing updated thief info over its state.
	 *
	 * @param clock caller thief clock
	 * @param thief_id thief identification
	 * @param state updated thief state
	 * @return updated clock
	 * @throws java.rmi.RemoteException
	 */
	public int logLine_ThiefUpdateState(int clock, int thief_id, State_Thief state) throws RemoteException;

}
