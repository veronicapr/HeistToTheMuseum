/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heisttothemuseum.sharedzones;

import heisttothemuseum.threads.Thief;
import heisttothemuseum.enums.ThiefState;
import heisttothemuseum.interfaces.It_AssaultParty;
import heisttothemuseum.enums.AssaultPartyNames;
import java.util.Random;

/**
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class AssaultParty implements It_AssaultParty {

	private static int next_id = 0;		// INCREMENTING ID
	private final int id;				// Team id
	private final String name;			// Team name

	private final int size;				// Team size
	private final int target_room;		// Number of target room
	private final int target_distance;	// Target room distance
	private final int max_gap;			// Maximum gap between thieves
	
	public boolean start;				//
	public boolean hasReturned;			//
	
	private int stolen_canvas;			//
	private int mthief_paintings;		// Number of paintings already delivered to the Master Thief
	private int[] team_positions;		// Current team positions
	private Thief[] team_elements;		// Team elements

	private final Museum museum;

	public AssaultParty(int size, int target_room, int target_distance, int max_gap, Thief[] team_elements, Museum museum) {
		this.id = next_id;
		next_id++;
		this.name = generateName();

		this.size = size;
		this.target_room = target_room;
		this.target_distance = target_distance;
		this.max_gap = max_gap;

		this.museum = museum;
		
		this.team_elements = team_elements;
		this.team_positions = new int[size];
		for (int i = 0; i < size; i++) {
			this.team_positions[i] = 0;
		}
	}



	public String getName() {
		return id + " " + name;
	}

	private String generateName() {
		Random random = new Random();
		int random_result = random.nextInt(AssaultPartyNames.values().length);
		return AssaultPartyNames.values()[random_result].toString();
	}

	public int getCanvas() {
		return stolen_canvas;
	}
	
	public int getTargetRoom(){
		return target_room;
	}

	public boolean returned() {
		for (int i = 0; i < size; i++) {
			if (team_positions[i] != 0) {
				return false;
			}
		}
		return true;
	}

	
	public synchronized ThiefState crawlIn(Thief th) {
		/*==> Aumenta a posição na equipa por um valor entre 1 e MD
        ==> Se distancia a um elemento anterior for >= 3, Não avança (devidos cuidados com a distância)
        ==> Aumenta até à distância da sala objectivo altera direcção para Idle aí*/
		int id = th.getID();
		int advance = th.advance();
		
		if (team_positions[id] + advance > target_distance) {
			team_positions[id] = target_distance;
		}
		
		if(team_positions[id] == target_distance){
			return ThiefState.AT_A_ROOM;
		} else{
			return ThiefState.CRAWLING_INWARDS;
		}
		
	}
	
	public synchronized ThiefState reverseDirection() {
		/*Change Direction to Out*/
		
		return ThiefState.CRAWLING_OUTWARDS;
		
	}

	public synchronized ThiefState crawlOut(Thief th) {
		/*==> Reduz a posição na equipa por um valor entre 1 e MD
        ==> Se distancia a um elemento anterior for >= 3, Não recua (devidos cuidados com a distância)
        ==> Reduz até 0 */
		int id = th.getID();
		int advance = th.advance();
		
		if (team_positions[id] - advance < 0) {
			team_positions[id] = 0;
		}
		
		if(team_positions[id] == 0){
			return ThiefState.OUTSIDE;
		} else{
			return ThiefState.CRAWLING_OUTWARDS;
		}
	}
	
	public synchronized ThiefState rollACanvas() {
		/*returns AT_A_ROOM state and decrements the number of paintings in the target room */
		museum.num_paintings[target_room]--;
		stolen_canvas++;
		return ThiefState.AT_A_ROOM;
	}
	
	public synchronized ThiefState handACanvas() {
		mthief_paintings++;
		return ThiefState.OUTSIDE;		
	}
	
	@Override
	public String toString() {
		String museum = String.format("========================= Assault Party - %1$17s ========================\n", getName());
		museum += "ID = " + id;
		museum += "==> Party\n";
		String table_1 = "| Index    |";
		String table_2 = "| Name     |";
		String table_3 = "| Position |";
		for (int index = 0; index < this.size; index++) {
			table_1 += String.format("%1$17s|", index);
			if (this.team_elements[index] == null) {
				table_2 += "        -        |";
			} else {
				table_2 += String.format("%1$17s|", this.team_elements[index].getName());
			}
			table_3 += String.format("%1$17s|", this.team_positions[index]);
		}
		return museum += table_1 + "\n" + table_2 + "\n" + table_3 + "\n";
	}
}
/*private void verify(int max_distance, int[] dist){
      /*verifies the distance between the thiefs */
 /* for(int i = 0; i < dist.length; i++){
          if(dist[i] > max_distance){
              //DO SOMETHING - STOP
          }
      }
  
  }*/
