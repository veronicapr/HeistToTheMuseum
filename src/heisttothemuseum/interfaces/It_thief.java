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
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public interface It_thief {
	
	ThiefState amINeeded(Thief thief);
	
	ThiefState prepareExcursion();
	
	
}
