//Author: Tao Wang (707458), Yao Lu (751435), Danping Zeng (777691)

package group11.planning.coreClass;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.unimelb.swen30006.partc.ai.interfaces.PerceptionResponse;
import com.unimelb.swen30006.partc.ai.interfaces.PerceptionResponse.Classification;
import com.unimelb.swen30006.partc.core.objects.Car;

import group11.planning.interfaces.iDrive;

public class Drive implements iDrive{
	
	//Private car under control
	//Private route planned by Map class
	private Car car; 
	private ArrayList<Point2D.Double> route;

    //Private attributes of the car providing relevant information
	private Point2D.Double currentPosition;
	private Vector2 velocity;
	private double remainingDistance;
	private Point2D.Double previousPosition;
	
	//Identifiers of car's reaction to surroundings
	public double angle;
	public boolean accelerate;
	private boolean brake;
	boolean judge = true;
	
	//Identifiers of car's current state
	private boolean firstTime;
	private boolean arrived;
	
	//Private variables storing specific points in the route
	private Point2D.Double nextTurnningPoint;
	private Point2D.Double temporaryPoint;	
	
	
	/** 
	 * Drive constructor
	 * @param car: the car under control
	 * @param distance: the total distance of the route
	 * @param route: the shortest path from current position to the destination
	 */
	public Drive(Car car,double distance,ArrayList<Point2D.Double> route){
		this.car = car;
		this.remainingDistance = distance;
		adjustDestination(route);
		this.route = adjustTurnningPoint(route);
		this.nextTurnningPoint = route.get(0);
		this.temporaryPoint = route.get(0);
		this.firstTime = true;
		this.arrived = false;
		this.currentPosition = new Point2D.Double(0,0);
		this.previousPosition = new Point2D.Double(0,0);
	}
	
	@Override
	public double centralControl(PerceptionResponse[] results) {
        //Initialize the state of the car
		this.angle = 0;
		this.accelerate = this.brake = false;
		
        //Obtain the private information of the car
		this.velocity = this.car.getVelocity();
		this.currentPosition = this.car.getPosition();
		
		if(this.firstTime){ 
			this.car.accelerate();
			//Adjust the car to the right direction first
			adjustDirection();
			this.previousPosition.x = this.currentPosition.x;
			this.previousPosition.y = this.currentPosition.y;
			this.firstTime = false;
			
		}else{
			//Judge whether the car has arrived its destination
			//if not, take actions to the perception objects
			//or, stop the car
			if(!this.arrived){
				this.response(results);
				//Judge whether the car needs to turn
				this.nextTurnningPoint = route.get(0);
				if((nextTurnningPoint.x-11)<currentPosition.x && currentPosition.x<(nextTurnningPoint.x+11) 
						&& (nextTurnningPoint.y-11)<currentPosition.y && currentPosition.y<(nextTurnningPoint.y+11)){
					this.turn();	
				}
			}else
				this.brake = true;
		}

		this.caculateRemainingDistance();
		this.previousPosition.x = this.currentPosition.x;
		this.previousPosition.y = this.currentPosition.y;
		return remainingDistance;
	}
	
	
	/**
	 * Find the nearest viable location to the destination.
    */
	private void adjustDestination(ArrayList<Point2D.Double> route){
		//Delete the last point in the route
		//the destination of the car is the last intersection
		if(route.size()>1){
				route.remove(route.size()-1);
		}
	}
	
	
	/**
	 *Adjust the car to the correct direction at the start
	 */
	private void adjustDirection(){
		if(!route.isEmpty()){
			//the added point to the route so that the car can turn around to the correct direction and lane
			Point2D.Double temp = new Point2D.Double(0, 0);
			if(route.get(0).y < car.getPosition().y){												
				if(route.get(0).x < car.getPosition().x){
					System.out.println("TURN RIGHT (Drive.adjustDirection)");
				    this.angle = -22.5;//turn right
				    judge = false;
				    temp.x = car.getPosition().x;
				    temp.y = car.getPosition().y - 20;
			    	route.add(0, temp);
			    	route.get(1).y = route.get(0).y;
				}
				else{
					temp.x = route.get(0).x;
					temp.y = car.getPosition().y;						
			    	if(route.get(1).x < route.get(0).x){
			    		route.add(0, temp);
			    	}
			    	else{
			    	    route.set(0, temp);
			    	}
				}
			}
			else{
				if(route.get(0).x > car.getPosition().x){
					System.out.println("TURN RIGHT (Drive.adjustDirection)");
					this.angle = -22.5;//turn right
					judge = false;
					temp.x = car.getPosition().x;
					temp.y = car.getPosition().y - 20;
					route.add(0, temp);
					route.get(1).y = route.get(0).y;
				}
				else{
					Point2D.Double firstIntersection = new Point2D.Double();	
					firstIntersection.x = route.get(0).x;
					firstIntersection.y = car.getPosition().y;
					route.set(0, firstIntersection);
				}
			}
		}
	}


	/**
	 * Adjust intersections onto roads
	 * @param route: the planned route passed by Map class
	 * @return route: a refined route whose intersections are on the roads
	 */
	private ArrayList<Point2D.Double> adjustTurnningPoint(ArrayList<Point2D.Double> route){
		if(!route.isEmpty()){
		    for(int i = 0; i < route.size(); i++){
			    route.get(i).x = route.get(i).x + 10;
			    route.get(i).y = route.get(i).y + 10;
		    }
		}
		return route;
	}
	
	
	/**
	 * Calculate the remaining distance based on the current position and the route
	 */
	private void caculateRemainingDistance(){

		//Update the remaining distance
		if(!route.isEmpty()){
			double distanceX = Math.abs(route.get(0).x - car.getPosition().x);
			double distanceY = Math.abs(route.get(0).y - car.getPosition().y);
			remainingDistance = distanceX + distanceY;
			if(route.size()>1){
				for(int i=0;i<route.size() - 1;i++){
					distanceX = Math.abs(route.get(i+1).x - route.get(i).x);
					distanceY = Math.abs(route.get(i+1).y - route.get(i).y);
					remainingDistance = remainingDistance + distanceX +distanceY;
				}
			}
		}
	}
	
	/**
	 * Accelerate or brake the car
	 * according to the time to collide with the car or traffic light in front of the car
	 * @param results: the PerceptionResponses after being sorted
	 */
	private void response(PerceptionResponse[] results) {
		boolean getItem=true;
		int i=0;
		Classification type=null;;
		PerceptionResponse p=null;
		if(results != null){
			while(getItem){
				if(i < results.length){
					//Get the detail of the next item
					p=results[i];
					float x=p.direction.x;
					float y=p.direction.y;
					type=p.objectType;
					//Get the first car or traffic light which is in front of the car
					if(x*velocity.x+y*velocity.y>0 && (type.equals("Car")||type.equals("TrafficLight"))){
						getItem=false;
					}else{
						i++;
						if(i == results.length)
							getItem = false;
					}
				}			
			}
		}else{
			this.accelerate = true;
			this.brake = false;
		}
		
		//Accelerate or brake
		//if something needs to be responded 
		if(p!=null){
			if(type.equals("TrafficLight") && p.information.get("State")=="green"){
				if(p.distance<3 && velocity.x*velocity.x+velocity.y*velocity.y>100){
					this.brake = true;
				}else{
					this.accelerate = true;
				}
			}else{
				if(p.timeToCollision<3 && velocity.x+velocity.y>0){
					this.brake = true;
				}else{
					this.accelerate = true;
				}
			}
		}else{
			this.accelerate = true;
			this.brake = false;
		}	

		//Control the velocity of the car and set the velocity lower than 20
		double speed = velocity.x*velocity.x+velocity.y*velocity.y;
		if(speed > 400)
			this.brake = true;
		//Begin to brake if the car almost arrives at the destination
		if(this.remainingDistance < 21)
			this.arrived = true;
		if(this.arrived){
			this.brake = true;
			this.accelerate = false;
		}
		
	}

	/**
	 * Judge the turning direction and position
	 * according to the difference between the vector from current position to next intersection
	 * and the vector of the current velocity of the car
	 */
	
    private void turn() {
    	
    	if(route.size()>1){
			
			// get the nearest turning point and the next point in the route
			this.temporaryPoint = route.get(0);	
			this.nextTurnningPoint = route.get(1);
			
			Vector2 vector = new Vector2();
			vector.x = (float) (this.nextTurnningPoint.x - temporaryPoint.x);
			vector.y = (float) (this.nextTurnningPoint.y - temporaryPoint.y);
			
			float deltaAngle = (int)(vector.angle() - car.getVelocity().angle());
			if((deltaAngle > 5 && deltaAngle <= 180) ||(deltaAngle < - 180 && deltaAngle > -355 )) {
				//turn left
				//set turn left angle
				this.angle = 22.5;
			}else if((deltaAngle >= -180 && deltaAngle < -5) || (deltaAngle < 355 && deltaAngle > 180)){
				//turn right
				//set turn right angle
				this.angle = -22.5;
			}

			Point2D.Double front = new Point2D.Double(this.temporaryPoint.getX(), this.temporaryPoint.getY());
			int a = 8;
			if(angle == -22.5 ){
				a = 20;
			}
			if(judge){
				//this is the first time when the car arrives at the intersection
				//set a front point
				judge = false;			
				if(Math.abs(this.velocity.x) > Math.abs(this.velocity.y)){
					//if the horizontal velocity of the car is more than 0
					if(this.velocity.x > 0){
						front.x += a;
					}
					if(this.velocity.x < 0){
						front.x -= a-8;
					}
				}
				if(Math.abs(this.velocity.x) < Math.abs(this.velocity.y)){
					//if the vertical velocity of the car is more than 0
					if(this.velocity.y > 0){
						front.y += a;
					}
					if(this.velocity.y < 0){
						front.y -= a-8;
					}
				}
				route.add(0,front);
				route.remove(1);
				angle = 0;
			}else{
				judge = true;
				route.remove(0);
			}
		}
}

	@Override
	public void update() {
		if(this.accelerate)
			this.car.accelerate();
		if(this.brake)
			this.car.brake();
		if(this.angle != 0)
			this.car.turn((float)angle);
	}
	
}
