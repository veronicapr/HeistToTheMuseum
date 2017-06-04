/*
 * It Repository AssaultParty
 */
package heist.repository.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Contains the log line methods called by Assault Party on the repository.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_Repository_AssaultParty extends Remote {

	/**
	 * Log line containing updated info over a team target room.
	 *
	 * @param team_id team identification
	 * @param target_room team updated target room
	 * @throws java.rmi.RemoteException
	 */
	public void logLine_AssaultPartyUpdateRoom(int team_id, int target_room) throws RemoteException;

	/**
	 * Log line containing updated info over a team element positions.
	 *
	 * @param team_id team identification
	 * @param team_members team distances order
	 * @param team_positions updated team distances
	 * @throws java.rmi.RemoteException
	 */
	public void logLine_AssaultPartyUpdatePositions(int team_id, int[] team_members, int[] team_positions) throws RemoteException;
}
