/*
 * It Thief ControlSite
 */
package heist.control_site.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Contains the methods called by the thief in Master Thief Control Collection Site.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_Thief_ControlSite extends Remote {

	/**
	 * <p>
	 * The returned thief hands his stolen canvas to the master thief or sets his target room as empty if he has returned empty handed.</p>
	 * <p>
	 * Sets team as returning and adds to team member count, if all members arrived sets target room as unassigned.</p>
	 * <p>
	 * Adds his assault party id to the returned members queue.</p>
	 *
	 * @param thief_id thief id
	 * @param assault_party_id thief assault party id
	 * @param target_room thief target room
	 * @param stolen_canvass
	 * @throws java.rmi.RemoteException
	 */
	void handACanvas(int thief_id, int assault_party_id, int target_room, int stolen_canvass) throws RemoteException;

}
