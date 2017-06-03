/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heist.assault_party.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Contains the methods called by the master thief in Museum.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_MasterThief_AssaultParty extends Remote {
	
	/**
	 * <p>
	 * Prepares assault party by setting its target room and target distance.</p>
	 * <p>
	 * Also forces the return signal to false.</p>
	 *
	 * @param target_room room number
	 * @param target_distance room distance
	 * @throws java.rmi.RemoteException
	 */
	
	void prepareAssaultParty(int target_room, int target_distance) throws RemoteException;

}
