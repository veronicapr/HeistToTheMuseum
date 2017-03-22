/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heisttothemuseum.sharedzones;

import java.util.Random;

/**
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 *
 */
public class Museum {

	public int num_rooms;			// number of rooms
	public int min_distance;		// minimum distance to museum rooms
	public int max_distance;		// maximum distance to museum rooms
	public int[] num_paintings;		// array with the number of paintings per room
	public int[] dist_room;			// arry with the distance of each room

	/**
	 * Generates a Museum
	 *
	 * @param num_rooms number of rooms
	 * @param min_distance minimum distance of a room
	 * @param max_distance maximum distance of a room
	 */
	public Museum(int num_rooms, int min_distance, int max_distance) {
		this.num_rooms = num_rooms;
		this.min_distance = min_distance;
		this.max_distance = max_distance;

		this.dist_room = new int[num_rooms];
		this.num_paintings = new int[num_rooms];

		/* Randomises the number of paintings and distance of each room between given min and max */
		for (int i = 0; i < num_rooms; i++) {
			num_paintings[i] = i;
			dist_room[i] = generateRandom(min_distance, max_distance);
		}
	}

	/**
	 * Returns the distance of the target_room
	 *
	 * @param target_room wanted room number
	 * @return the rooms distance
	 */
	public int prepareAssaultParty(int target_room) {
		return dist_room[target_room];
	}
	
	/**
	 * Generates a random number between min and max
	 *
	 * @param min minimum random result
	 * @param max maximum random result
	 * @return random number between min and max
	 */
	private int generateRandom(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	
	/**
	 * Returns a table containing the information about the number of paintings and the distance of each room
	 *
	 * @return a string of a table with the museum content
	 */
	@Override
	public String toString() {
		String room_table_1 = "| Room      |";
		String room_table_2 = "| Paintings |";
		String room_table_3 = "| Distance  |";
		String museum = "======================================== Museum ========================================\n";
		for (int index = 0; index < this.num_rooms; index++) {
			room_table_1 += String.format("%1$7s|", index);
			room_table_2 += String.format("%1$7s|", this.num_paintings[index]);
			room_table_3 += String.format("%1$7s|", this.dist_room[index]);
		}
		return museum += room_table_1 + "\n" + room_table_2 + "\n" + room_table_3 + "\n";
	}
}
