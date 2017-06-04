/*
 * It MasterThief Museum
 */
package heist.museum.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Contains the methods called by the master thief in Museum.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_MasterThief_Museum extends Remote {

	/**
	 * Returns the distance of the rooms.
	 *
	 * @return room_distance array containing every room distance
	 * @throws java.rmi.RemoteException
	 */
	int[] startOperations() throws RemoteException;
}
