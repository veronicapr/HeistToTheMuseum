/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package heisttothemuseum.interfaces;

import heisttothemuseum.threads.Thief;
import heisttothemuseum.enums.ThiefState;

/**
 *
 * @author migu_
 */
public interface It_AssaultParty {
	ThiefState crawlIn(Thief th);
	ThiefState reverseDirection();
	ThiefState crawlOut(Thief th);
	ThiefState rollACanvas();
	ThiefState handACanvas();
	
}
