//Author: Tao Wang (707458), Yao Lu (751435), Danping Zeng (777691)

package group11.planning.interfaces;

import com.unimelb.swen30006.partc.ai.interfaces.PerceptionResponse;

public interface iDrive {
	
	/**
	 * Process received perception response
	 * @param results: the perception results from perception system 
	 * @return remaining distance to the destination 
	 */
	public double centralControl(PerceptionResponse[] results);
	
	/**
	 * Control car's direction, velocity, acceleration, brake.
	 */
	public void update();

}
