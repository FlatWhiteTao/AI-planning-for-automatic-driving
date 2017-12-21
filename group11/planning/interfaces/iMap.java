//Author: Tao Wang (707458), Yao Lu (751435), Danping Zeng (777691)

package group11.planning.interfaces;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import com.unimelb.swen30006.partc.core.objects.Car;
import com.badlogic.gdx.utils.XmlReader.Element;


public interface iMap {
	/**
	 * routeMapInit: Read from map_xml file and initialize a system-readable data structure map
	 * @param map_xml: virtural world file
	 */
	public void routeMapInit(String map_xml);
	
	/**
	 * processVertex: Process vertices of map
	 * @param intersection: intersection is considered as a vertex of map
	 */
	public void processVertex(Element intersection);
	
	/**
	 * processEdge: Process edges of map
	 * @param road: road is considered as a edge of map
	 */
	public void processEdge(Element road);
    
	/**
     * ComputeShortestPath : compute the shortest path via Dijkstra's algorithm
     * @param car: to get car's information such as current position,velocity,etc.
     * @param destination: the destination 
     * @return the shortest path 
     */
	public ArrayList<Point2D.Double> ComputeShortestPath(Car car, Point2D.Double destination);
	
	/**
	 * ComputePathLength: compute the length of a shortest path
	 * @param shortestPath: the shortestPath from method ComputeShortestPath
	 * @return the length of the shortest Path
	 */
	public double ComputePathLength(ArrayList<Point2D.Double> shortestPath);
	
	/**
	 * findNearestStartVertex: find the best vertex which is near to the car and toward the destination 
	 * @param carPosition: car's current position
	 * @param destination: the given destination
	 * @return the best and nearest vertex for representing starting position 
	 */
	public String findNearestStartVertex(Point2D.Double carPosition,Point2D.Double destination);
	/**
	 * findNearestDestinationVertex: find a vertex which is nearest to a given destination
	 * @param destination
	 * @return the nearest vertex for representing a given destination
	 */
	public String findNearestDestinationVertex(Point2D.Double destination);
}

