//Author: Tao Wang (707458), Yao Lu (751435), Danping Zeng (777691)

package group11.planning.coreClass;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import com.unimelb.swen30006.partc.ai.interfaces.IPlanning;
import com.unimelb.swen30006.partc.ai.interfaces.PerceptionResponse;
import com.unimelb.swen30006.partc.core.objects.Car;

import group11.planning.interfaces.iDrive;

public class Planner implements IPlanning {
	   
	   //Private car under control
	   //Private map read from input file
	   private Car car;
	   private Map map;
	   
	   //Private variables on specific information of the planned route
	   private ArrayList<Point2D.Double> shortestPath;
	   private double totalDistance;
	   private double remainingDistance;
	   
	   //Main controller for updating the car
	   private iDrive driveController;
	   
	   //Identifiers of car's current state
	   private boolean firstTime;

	   //Private variables regarding time
	   private float currentTime;
	   private float eta;
	   
	   /**
	    * Planner constructor
	    * @param car: the car under control
	    * @param map_xml: an input map file
	    */
	   public Planner(Car car){
		   this.car = car;
		   this.map = new Map("test_course.xml");
		   this.currentTime=0;
		   this.firstTime = true;
	   }
	   
	   /**
	    * Plan the shortest route for the car
	    * based on the given destination
	    */
	   @Override
	   public boolean planRoute(Double destination) {
	      
	       this.shortestPath= map.ComputeShortestPath(car,destination);
	       System.out.println("Start:（Planner.planRoute）");
	       System.out.println("StartPoint: "+this.car.getPosition());
	       System.out.println("Destination: "+destination);
	       System.out.println("\t\nShortest Path:(Planner.planRoute)");
	       if(shortestPath.size()!=0)
	       {for(int j=0;j<this.shortestPath.size();j++){
	    	  System.out.println(shortestPath.get(j));
	       }
	       }
	      
		   this.totalDistance = map.ComputePathLength(this.shortestPath);
		   System.out.println("Path total distance: "+ this.totalDistance);
		   System.out.println("\n");
		   this.remainingDistance=this.totalDistance;
		   if(shortestPath.size()!=0){
                      return true;
                  }
                  return false;
	   }
	   
	   /**
	    * Take appropriate reactions against the perception responses around the car
	    * @param results: a group of perception response passed by the Perception subsystem
	    * @param delta: the duration time from the last update process
	    */
	   @Override
	   public void update(PerceptionResponse[] results, float delta) {
		   
		   //Initial the drive controller
		   if(this.firstTime){
			   this.driveController=new Drive(this.car,this.totalDistance,this.shortestPath);
			   firstTime=false;
			   }
		   
		   //Priority the PerceptionResponse
			results=this.prioritisingPerceptionResponses(results);
			
			//Operate the car
			this.remainingDistance = this.driveController.centralControl(results);
			this.driveController.update();
			this.car.update(delta);
			
			//Update the current time
			currentTime += delta;
			
		}
	   
	  /**
	   * Provide the estimation of arrival time
	   */
	   public float eta() {
		    //Variable deltaD: distance completed
		    //Variable averageV: the past average velocity
		    double deltaD;	    
		    double averageV;
			
			deltaD = totalDistance - remainingDistance;
			averageV = deltaD/currentTime;
			eta = (float) (remainingDistance/averageV);
			
			return eta;
		}
	   
	   /**
	    * Priorities the passed group of perception responses
	    * according to the attribute of timeToCollision
	    * @param results
	    * @return
	    */
	   private PerceptionResponse[] prioritisingPerceptionResponses(PerceptionResponse[] results){
			if(results != null){
				PerceptionResponse[] order = new PerceptionResponse[results.length - 1];//this array contains the result after prioritisation
				
				//Apply the insertion sorting algorithm
			    order[0] = results[0];
			    int j = 0;
			    for(int i=1; i < results.length;){
				    if(results[i].timeToCollision < order[j].timeToCollision){
				    	j ++;
				    	order[j] = order[j-1];
				    	order[j-1] = results[i];
				    }
				    else{
				    	j ++;
				    	order[j] = results[i];
			    	}
			    	i ++;
		    	}
			
			    //Put the sorted result back into the original array results
			    for(int i=0; i < order.length; i++){
				    results[i] = order[i];
			    }
			}			
			return results;
		}

}
