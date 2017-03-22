/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heisttothemuseum.threads;

/**
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
import heisttothemuseum.sharedzones.OrdinaryThievesConcentrationSite;
import heisttothemuseum.sharedzones.Museum;
import heisttothemuseum.sharedzones.AssaultParty;
import heisttothemuseum.enums.ThiefState;
import static heisttothemuseum.enums.ThiefState.OUTSIDE;
import heisttothemuseum.interfaces.It_AssaultParty;
import heisttothemuseum.interfaces.It_thief;
import java.util.Random;



public class Thief extends Thread {
	
	private final It_thief it_thief;
	
	private It_AssaultParty it_assparty;

	private int MD;
	private int id;
	private AssaultParty team;
	private ThiefState state;
	private boolean hasCanvas;
	private final Museum museum;
	private final OrdinaryThievesConcentrationSite thieves_conc;
	private boolean isAvailable;

	public Thief(It_thief it_thief, int id, ThiefState state, Museum museum, OrdinaryThievesConcentrationSite thieves_conc) {
		this.id = id;
		this.state = state;
		this.museum = museum;
		this.thieves_conc = thieves_conc;
		
		this.it_thief = it_thief;
	}
	
	public void assignAssaultParty(AssaultParty team, It_AssaultParty it_assparty){
		this.it_assparty = it_assparty;
		this.team = team;
	}
	public int getID(){
		return id;
	}
	public int advance(){
		Random rand = new Random();
		int randomNum = rand.nextInt((this.MD - 1) + 1) + 1;
		return randomNum;
	}

	@Override
	public void run() {
		ThiefState state = OUTSIDE;
		while (true) {
			switch (state) {
				case OUTSIDE:
					if ( team.start && !team.hasReturned ) {
						state = it_thief.prepareExcursion();
					} else if ( hasCanvas ) {
						state = it_assparty.handACanvas();
					} else {
						state = it_thief.amINeeded(this);
					}
					break;
				case CRAWLING_INWARDS:
					state = it_assparty.crawlIn(this);
					break;
				case AT_A_ROOM:
					if((museum.num_paintings[team.getTargetRoom()] > 0) && !hasCanvas ){
						state = it_assparty.rollACanvas();
						hasCanvas = true;
					}
					else{
						state = it_assparty.reverseDirection();
					}
					break;
				case CRAWLING_OUTWARDS:
					state = it_assparty.crawlOut(this);
					break;
				default:
					break;
			}
		}

	}
}
