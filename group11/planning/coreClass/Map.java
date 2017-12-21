//Author: Tao Wang (707458), Yao Lu (751435), Danping Zeng (777691)
//The implemention of this class has one difference from the design report, the vertex of route is intersction
//now rather than roadmarking which we utilised in the report.

package group11.planning.coreClass;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.unimelb.swen30006.partc.core.objects.Car;
import com.unimelb.swen30006.partc.roads.Intersection;
import com.unimelb.swen30006.partc.roads.Road;

import group11.planning.interfaces.iMap;


public class Map implements iMap  {
	
	//Private instance variables to initialize the map
	private String fileName;
	private boolean initialised;
	private ArrayList<Road> roads;
	private HashMap<String, Intersection> intersections;
	
    // Private HashMap instance variable to store each vertex's id and each vertex's connecting vertices id
	private HashMap<String,ArrayList<String>> routeMap = new  HashMap<String,ArrayList<String>>();
	
	// Private variable disCarStartV : the distance between the car and start vertex
	// Private variable disDestEndV: the distance between the destination and destination vertex
	private double disCarStartV;
	private double disDestEndV; 
	
	// Map constructor 
	public Map(String map_xml){
		this.fileName = map_xml;
		this.intersections = new HashMap<String, Intersection>();
		this.roads = new ArrayList<Road>();
		routeMapInit(map_xml);
	}
	
	// Read map_xml file and initialize map
	@Override
	public void routeMapInit(String map_xml) {
		try{
		FileHandle file = Gdx.files.internal(map_xml);			
		XmlReader reader = new XmlReader();
		Element root = reader.parse(file);
	    
		//Read intersections for initializing vertices
		Element intersections = root.getChildByName("intersections");
		Array<Element> intersectionList = intersections.getChildrenByName("intersection");
		for(Element e : intersectionList){
			processVertex(e);
		}
		//Read road for initializing edges 
		Element roads = root.getChildByName("roads");
		Array<Element> roadList = roads.getChildrenByName("road");
		for(Element e : roadList){
			processEdge(e);
		}this.initialised = true;
		} 
		catch (Exception e){
			e.printStackTrace();
			System.exit(0);
	}
  }
	@Override
	// Process vertex details
	public void processVertex(Element intersection){
		String roadID = intersection.get("intersection_id");
		float x_pos = intersection.getFloat("start_x");
		float y_pos = intersection.getFloat("start_y");
		float width = intersection.getFloat("width");
		float height = intersection.getFloat("height");
		Intersection inter = new Intersection(new Point2D.Double(x_pos, y_pos), width, height);
		this.intersections.put(roadID, inter);
		this.routeMap.put(roadID, new ArrayList<String>());
	}
	
	@Override
	// Process edge details
	public void processEdge(Element road){
		float startX = road.getFloat("start_x");
		float startY = road.getFloat("start_y");
		float endX = road.getFloat("end_x");
		float endY = road.getFloat("end_y");
		float width = road.getFloat("width");
		int numLanes = road.getInt("num_lanes");
		
		Point2D.Double startPos = new Point2D.Double(startX, startY);
		Point2D.Double endPos = new Point2D.Double(endX, endY);
		
		Road r = new Road(startPos, endPos, width, numLanes, new int[]{0,0});
		this.roads.add(r);
		
		// read start intersection and end intersection
		Element intersection = road.getChildByName("intersections");
		Element startIntersection = intersection.getChildByName("start");
		Element endIntersection = intersection.getChildByName("end");
		
		// connect two vertices, and the map is an undirected graph 
		if (startIntersection!=null){
			String startID = startIntersection.get("id");
			if (endIntersection !=null){
			String endID = endIntersection.get("id");
			routeMap.get(startID).add(endID);
			routeMap.get(endID).add(startID);
			}
		}
	}
	
	// a method to return map
	public HashMap<String, ArrayList<String>> getMap(){
		return routeMap;
	}
	
	@Override
	public ArrayList<Point2D.Double> ComputeShortestPath(Car car, Point2D.Double destination){
		// set start vertex via method findNearestStartVertex
		// and end vertex via method findNearestDestinationVertex
		String startV = findNearestStartVertex(car.getPosition(),destination); 
		String endV = findNearestDestinationVertex(destination);
		
		// compute the distance between car and start vertex
		// compute the distance between destination and end vertex 
		this.disCarStartV = car.getPosition().distance(this.intersections.get(startV).pos);
		this.disDestEndV = destination.distance(this.intersections.get(endV).pos);
		
		// Find the shortest route via Dijkstra's algorithm 
		
		// initialize the shortest path and add the start vertex's position
		ArrayList<Point2D.Double> shortestPath = new ArrayList<Point2D.Double>();
		shortestPath.add(intersections.get(startV).pos); 
		
		// HashMap dist: Distance from source to v 
		// HashMap prev: Previous node in optimal path from source
		// ArrayList restVertex: Store the rest nodes
		// stack: Construct the shortest path with a stack
		HashMap <String,Double> dist = new HashMap<String,Double>();
		HashMap <String,String> prev = new HashMap<String,String>();
		ArrayList<String> restVertex = new ArrayList<String>();
		Stack<Point2D.Double> stack =new Stack<Point2D.Double>();
		
		// Initialize dist and prev
		for (Entry<String, ArrayList<String>> entry : routeMap.entrySet()){
	    	dist.put(entry.getKey(),Double.MAX_VALUE);
	    	prev.put(entry.getKey(),"");
	    }
	    
		// add the source vertex 
	    dist.replace(startV,0.0);
	    // Initialize the rest nodes list
		for (Entry<String, ArrayList<String>> entry : routeMap.entrySet()){
			restVertex.add(entry.getKey());
		}
		
		//add the shortest path vertex 
		while(restVertex.size()>0){
		     String nextNode = identifyNextNode(dist,restVertex);
		     restVertex.remove(nextNode);
		     for(String connections: routeMap.get(nextNode)){
		    	 Double conD = this.intersections.get(nextNode).pos.distance(this.intersections.get(connections).pos);
		    	 Double tempD = dist.get(nextNode) + conD;
		    	 if (tempD <= dist.get(connections)){
		    		 dist.replace(connections,tempD);
		    		 prev.replace(connections,nextNode);
		    	 }
		     }
		}
	    //Construct the shortest path with a stack
		while (startV != endV){
		   stack.push(this.intersections.get(endV).pos);
		   endV = prev.get(endV);
	   }
	   while(stack.size()!=0){
		   if(stack.size()!=0)
		   shortestPath.add(stack.pop());
	   }
	   shortestPath.add(destination);
	   
	   return shortestPath;
		
	}
	// find the next path node with the shortest distance
	private String identifyNextNode(HashMap <String,Double> dist,ArrayList<String> restVertex) {
		Double minD = Double.MAX_VALUE;
		String tempID ="";
		for (String vertexID: restVertex){
			if(dist.get(vertexID) <= minD){
				minD = dist.get(vertexID);
				tempID = vertexID;
			}
		}
			return tempID;
	}
	@Override
	// find the best vertex which is near to the car and toward the destination 
	public String findNearestStartVertex(Point2D.Double carPosition,Point2D.Double destination){
		  Double distance = Double.MAX_VALUE;
		  String vertexID="";
		  // high level condition: a vertex should physically toward to the destination firstly and then near to the car
		  if (destination.x<= carPosition.x)
		  { 
			  for (Entry<String, ArrayList<String>> entry : routeMap.entrySet()){
				  //route first node and car need be in the same road
			  if(this.intersections.get(entry.getKey()).pos.x <=carPosition.x && (carPosition.y-35)<=this.intersections.get(entry.getKey()).pos.y&&this.intersections.get(entry.getKey()).pos.y<=(carPosition.y)){
			  Double tempDis = carPosition.distance(this.intersections.get(entry.getKey()).pos);
			  if (distance > tempDis){ distance = tempDis; vertexID = entry.getKey();}
			  }
		  } 
		  return vertexID;
		  }
		 if (destination.x>= carPosition.x)
		  { 
			  for (Entry<String, ArrayList<String>> entry : routeMap.entrySet()){
				  //route first node and car need be in the same road
				  if(this.intersections.get(entry.getKey()).pos.x >= carPosition.x &&  (carPosition.y-35)<=this.intersections.get(entry.getKey()).pos.y&&this.intersections.get(entry.getKey()).pos.y<=(carPosition.y)){
			  Double tempDis = carPosition.distance(this.intersections.get(entry.getKey()).pos);
			  if (distance > tempDis){ distance = tempDis; vertexID = entry.getKey();}
		      }
		  } 
		  return vertexID;
		  }
		  return vertexID;
	 }
	@Override 
	// Find a vertex which is the nearest one to the destination 
	public String findNearestDestinationVertex(Point2D.Double destination){
		 Double distance = Double.MAX_VALUE;
		  String vertexID="";
		  for (Entry<String, ArrayList<String>> entry : routeMap.entrySet()){
			  Double tempDis = destination.distance(this.intersections.get(entry.getKey()).pos);
			  if (distance > tempDis){
				  distance = tempDis;
				  vertexID = entry.getKey();
			 }
		}
		  return vertexID;
	}
	
	@Override
	// compute length of the shortest path
	public double ComputePathLength(ArrayList<Point2D.Double> shortestPath){
		double totalDistance = 0.0;
		if(shortestPath.size()!=0)
		{
			int size = shortestPath.size();
		    // length of the path
			for(int i=0;i<size-1;i++)
			totalDistance+=shortestPath.get(i).distance(shortestPath.get(i+1));
		
		//totalDistance should include the disCarStartV and disDestEndV
		totalDistance=totalDistance+this.disCarStartV+this.disDestEndV;
		return totalDistance;
		}
		return 0;
	}
	
}
