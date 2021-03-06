/*
 * It Repository Museum
 */
package heist.repository.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Contains the log line methods called by Museum on the repository.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_Repository_Museum extends Remote {

	/**
	 * Log line containing full updated museum info.
	 *
	 * @param rooms_paintings rooms current paintings full info
	 * @param rooms_distance rooms distance full info
	 * @throws java.rmi.RemoteException
	 */
	public void logLine_MuseumUpdateFull(int[] rooms_paintings, int[] rooms_distance) throws RemoteException;

	/**
	 * Log line containing updated museum info over a single room.
	 *
	 * @param room_index to be updated room index
	 * @param room_paintings current number of paintings in the room
	 * @throws java.rmi.RemoteException
	 */
	public void logLine_MuseumUpdateSingle(int room_index, int room_paintings) throws RemoteException;
}
