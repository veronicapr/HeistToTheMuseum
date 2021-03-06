/*
 * Clock Vector
 */
package clock_vector;

import java.io.Serializable;

/**
 * Vectorial clock counter to keep track of the order
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class ClockVector implements Serializable {

	private static final long serialVersionUID = 9882120011235175L;

	/**
	 * Pseudo-time.
	 */
	private int time;

	/**
	 * Constructor sets time  to zero.
	 */
	public ClockVector() {
		this.time = 0;
	}

	/**
	 * Updates current time.
	 *
	 * @param time new updated time
	 */
	public void updateTime(int time) {
		this.time = time;
	}
	
	/**
	 * Get current time.
	 *
	 * @return Current time.
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Increment time counter by one.
	 */
	public void increment() {
		time++;
	}
}
