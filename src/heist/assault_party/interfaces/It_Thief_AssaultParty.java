/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heist.assault_party.interfaces;

import heist.enums.State_Thief;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Contains the methods called by the thief in AssaultParty.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_Thief_AssaultParty extends Remote {
	
	/**
	 * Thief prepares for excursion by setting its position to the initial position and verifies if everyone is ready, if so send start signal.
	 *
	 * @param thief_id thief reference
	 * @return target_room room number
	 * @throws java.rmi.RemoteException
	 */
	int prepareExcursion(int thief_id) throws RemoteException;

	/**
	 * Crawls in towards assault party target room, minding his position gap between team members, proceeds in the following way:
	 * <ul>
	 * <li>1st - finds his position in team,</li>
	 * <li>2nd - calculates his advancement,</li>
	 * <li>3rd - if he is not the last one, sees if is advancement passes the gap over the one behind,</li>
	 * <li>4th - if he is not the last or first one, sees if the next thief position is not over the previous + gap,</li>
	 * <li>5th - goes to the last free position (further away) it finds in the line according to how much he can move,</li>
	 * <li>6th - if he goes over the target distance, sets back to the target distance,</li>
	 * <li>7th - if he moves from his location, reorganises the team positions, and logs the change,</li>
	 * <li>8th - decides whenever he reached the room or continues to crawl to it.</li>
	 * </ul>
	 *
	 * @param state thief state
	 * @param thief_id thief reference
	 * @param thief_agility thief agility
	 * @return ThiefState next state as State_Thief.AT_A_ROOM or State_Thief.CRAWLING_INWARDS
	 * @throws java.rmi.RemoteException
	 */
	State_Thief crawlIn(State_Thief state, int thief_id, int thief_agility) throws RemoteException;

	/**
	 * Verifies if everyone has already arrived at the room, if so signals everyone that the can start crawling back.
	 *
	 * @return return signal
	 * @throws java.rmi.RemoteException
	 */
	boolean reverseDirection() throws RemoteException;

	/**
	 * Crawls out towards the concentration site, minding his position gap between team members, proceeds in the following way:
	 * <ul>
	 * <li>1st - finds his position in team,</li>
	 * <li>2nd - calculates his advancement,</li>
	 * <li>3rd - if he is not the first one, sees if is advancement passes the gap over the one behind,</li>
	 * <li>4th - if he is not the first or last one, sees if the next thief position is not over the previous + gap,</li>
	 * <li>5th - goes to the last free position (further away) it finds in the line according to how much he can move,</li>
	 * <li>6th - if he goes over the concentration site, sets back to 0,</li>
	 * <li>7th - if he moves from his location, reorganises the team positions, and logs the change,</li>
	 * <li>8th - decides whenever he reached the concentration site or continues to crawl to it.</li>
	 * </ul>
	 *
	 * @param state thief state
	 * @param thief_id thief reference
	 * @param thief_agility thief agility
	 * @return next state as return State_Thief.OUTSIDE or State_Thief.CRAWLING_OUTWARDS
	 * @throws java.rmi.RemoteException
	 */
	State_Thief crawlOut(State_Thief state, int thief_id, int thief_agility) throws RemoteException;

}
