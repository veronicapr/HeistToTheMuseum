/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heisttothemuseum.threads;

import heisttothemuseum.sharedzones.Museum;
import heisttothemuseum.sharedzones.MasterThiefControlCollectionSite;
import heisttothemuseum.enums.MasterThiefState;
import static heisttothemuseum.enums.MasterThiefState.PLANNING_THE_HEIST;
import heisttothemuseum.interfaces.It_masterthief;

/**
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class MasterThief extends Thread {

	private final It_masterthief mthief;

	private final MasterThiefControlCollectionSite control;
	private final Museum museum;
	private MasterThiefState state;

	public MasterThief(It_masterthief mthief, String name, MasterThiefControlCollectionSite control, Museum museum) {
		super(name);

		this.state = PLANNING_THE_HEIST;
		this.control = control;
		this.museum = museum;
		this.mthief = mthief;
	}

	@Override
	public void run() {
		while (true) {
			switch (state) {
				case PLANNING_THE_HEIST:
					state = mthief.startOperations();
					break;
				case DECIDING_WHAT_TO_DO:
					/* Decides action based on the empty/assigned state of the rooms */
					int target_room = mthief.checkRooms();
					switch (target_room) {
						/* When there are no unassigned empty rooms */
						case -1:
							state = mthief.takeARest();
							break;
						/* When all rooms are empty */
						case -2:
							// state = control.sumUpResults()
							break;
						/* When there is a room to be assigned */
						default:
							state = mthief.prepareAssaultParty(target_room);
							break;
					}
					break;
				case ASSEMBLING_A_GROUP:
					state = mthief.sendAssaultParty();
					break;
				case WAITING_FOR_GROUP_ARRIVAL:
					/* If any dispatched team as arrived */
					// state = collectCanvas()
					break;
				case PRESENTING_THE_REPORT:
					//???
					break;
				default:
					break;
			}
		}

	}
}
