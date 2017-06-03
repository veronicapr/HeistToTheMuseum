/*
 * It Thief ConcentrationSite
 */
package heist.concentration_site.interfaces;

/**
 * Contains the methods called by the thief in Ordinary Thieves Concentration Site.
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_Thief_ConcentrationSite {

	/**
	 * Sets thief in a waiting cycle where each time he is awoken checks if heist_complete flag is true, if not proceeds to verify if his team is in the
	 * prepared teams queue if there are any on it. If he is the last team member to get ready, un-checks the team as prepared.
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
	int amINeeded(int assault_party_id);
}
