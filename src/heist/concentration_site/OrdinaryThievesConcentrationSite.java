/*
 * Ordinary Thieves Concentration Site
 */
package heist.concentration_site;


import heist.concentration_site.interfaces.It_MasterThief_ConcentrationSite;
import heist.concentration_site.interfaces.It_Thief_ConcentrationSite;
import settings.HeistSettings;

/**
 * Waiting place for all thieves awaiting to be called to work.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class OrdinaryThievesConcentrationSite implements It_MasterThief_ConcentrationSite, It_Thief_ConcentrationSite {

	//========================================================================================================================//
	// Ordinary thieves data
	//========================================================================================================================//
	/**
	 * Flag for heist completion
	 */
	public boolean heist_complete;
	/**
	 * Flags for prepared teams
	 */
	private final boolean[] prepared_teams = new boolean[HeistSettings.TOTAL_TEAMS];
	/**
	 * Prepared team member count
	 */
	private final int[] member_count = new int[HeistSettings.TOTAL_TEAMS];
	//========================================================================================================================//

	/**
	 * Constructor for Ordinary Thieves Concentration Site, also forces initialisation of member_count at 0 and prepared_teams as false.
	 */
	public OrdinaryThievesConcentrationSite() {

		this.heist_complete = false;

		for (int index = 0; index < HeistSettings.TOTAL_TEAMS; index++) {
			this.prepared_teams[index] = false;
			this.member_count[index] = 0;
		}
	}

	//========================================================================================================================//
	// Master thief methods
	//========================================================================================================================//
	/**
	 * Sets the assault party with given id as prepared and proceeds to notify all thieves of the occurrence.
	 *
	 * @param assault_party_id id of assault party to be added
	 */
	@Override
	public synchronized void sendAssaultParty(int assault_party_id) {
		// sets team flag as true
		this.prepared_teams[assault_party_id] = true;
		// notifies all thieves
		notifyAll();
	}

	/**
	 * Sets heist_complete flag as true and notifies all thieves of this alteration setting them for termination. Finalises the log file by adding its finishing lines.
	 */
	@Override
	public synchronized void sumUpResults() {
		// sets completion flag as true
		this.heist_complete = true;
		// notifies all thieves
		notifyAll();
		// finishes log
		//repository.logFinish();
	}

	//========================================================================================================================//
	// Thief methods
	//========================================================================================================================//
	/**
	 * Sets thief in a waiting cycle where each time he is awoken checks if heist_complete flag is true, if not proceeds to verify if his team is in the prepared teams queue if
	 * there are any on it. If he is the last team member to get ready, un-checks the team as prepared.
	 *
	 * Waiting cycle continues until:
	 * <ul>
	 * <li>team verification results as his team is on the prepared queue,</li>
	 * <li>heist_complete flag is set as true.</li>
	 * </ul>
	 *
	 * @param assault_party_id thief assault party id
	 * @return
	 * <ul>
	 * <li>-1 if heist is complete</li>
	 * <li>1 if team is prepared</li>
	 * <li>0 if interrupted</li>
	 * </ul>
	 */
	@Override
	public synchronized int amINeeded(int assault_party_id) {
		while (!heist_complete) {
			// checks if team is prepared then adds to member count if so, afterwards, if last one, un-checks team as prepared
			if (this.prepared_teams[assault_party_id]) {
				this.member_count[assault_party_id]++;
				if (this.member_count[assault_party_id] == HeistSettings.TEAM_SIZE) {
					this.member_count[assault_party_id] = 0;
					this.prepared_teams[assault_party_id] = false;
				}
				break;
			}
			// waits until awoken
			try {
				wait();
			} catch (InterruptedException ex) {
				return 0;
			}
		}
		// if heist is complete stops, otherwise prepares for excursion
		if (heist_complete) {
			return -1;
		} else {
			return 1;
		}
	}
}
