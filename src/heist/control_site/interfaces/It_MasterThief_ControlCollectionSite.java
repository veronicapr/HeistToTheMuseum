/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heist.control_site.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Contains the methods called by the master thief in Master Thief Control Collection Site.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_MasterThief_ControlCollectionSite extends Remote {

	/**
	 * <p>
	 * Master thief decides the next action based on the current situation. Result decisions can be either</p>
	 * <ul>
	 * <li>To dispatch a new team to a determined target room, that is unassigned and empty at that time</li>
	 * <li>Await for the teams arrival, when no more teams can be dispatched, there are returned members in queue or no more rooms to be assigned</li>
	 * <li>Or present the results, when all rooms are empty and no all teams have already returned</li>
	 * </ul>
	 *
	 * @return
	 * <ul>
	 * <li>target_room when a new team is to be dispatched</li>
	 * <li>-1 when no more teams can be dispatched, there are returned members in queue or no more rooms to be assigned</li>
	 * <li>-2 when all rooms are empty and no all teams have already returned</li>
	 * </ul>
	 * @throws java.rmi.RemoteException
	 */
	int appraiseSit() throws RemoteException;

	/**
	 * <p>
	 * Master thief waiting cycle.</p>
	 * <p>
	 * Exits cycle if queue isn't empty.</p>
	 *
	 * @return Arrived assault party id
	 * @throws java.rmi.RemoteException
	 */
	int takeARest() throws RemoteException;

	
	/**
	 * <p>
	 * Retrieves and removes next assault party id form the queue and checks if corresponding member count is complete.</p>
	 * <p>
	 * If member count is complete, resets it to 0 and changes returning team flag to false. Returns assault party id.</p>
	 * <p>
	 * Returns -1 if member count for queue assault party id isn't complete.</p>
	 *
	 * @param team_id Assault party id
	 * @return
	 * <ul>
	 * <li>1 if there where any canvas left</li>
	 * <li>-1 if not all team members have returned</li>
	 * </ul>
	 * @throws java.rmi.RemoteException
	 */
	int collectCanvas(int team_id) throws RemoteException;
}
