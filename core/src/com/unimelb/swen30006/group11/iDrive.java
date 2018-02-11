package com.unimelb.swen30006.group11;

import com.unimelb.swen30006.partc.ai.interfaces.PerceptionResponse;

public interface iDrive {

	/**
	 * control the car to accelerate, brake or turn
	 * use the method response to 
	 * use the method turn to 
	 * use the method correctDirection to 
	 * @param results is the item around the car which is need to be considered
	 * @return a float to update the remaining distance
	 */
	public double centralControl(PerceptionResponse[] results);
	
	/**
	 * use the param in the class to update the car 
	 */
	public void update();
	
}
