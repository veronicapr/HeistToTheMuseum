/*
 * It Thief Museum 
 */
package heist.museum.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Contains the methods called by the thief in Museum.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_Thief_Museum extends Remote {

	/**
	 * <p>
	 * Verifies if the rooms has canvas or not.</p>
	 * <p>
	 * If the room is not empty, increments the number of stolen paintings and decrements the number of paintings in the room.</p>
	 * <p>
	 * Prints a log line after taking the canvas.</p>
	 *
	 * @param thief_id thief id
	 * @param target_room thief target room index
	 * @return stolen_canvas
	 * <ul>
	 * <li>1 if there where any canvas left</li>
	 * <li>0 if room was already empty</li>
	 * </ul>
	 * @throws java.rmi.RemoteException
	 */
	public int rollACanvas(int thief_id, int target_room) throws RemoteException;
}
