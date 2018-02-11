package com.unimelb.swen30006.group11;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.unimelb.swen30006.partc.ai.interfaces.PerceptionResponse;
import com.unimelb.swen30006.partc.core.objects.Car;
import com.unimelb.swen30006.partc.roads.RoadMarking;

public class Drive implements iDrive{
	
	private Car car;
	
	private ArrayList<Point2D.Double> route;

	private Point2D.Double currentPosition;
	private Vector2 velocity;
	
	public double angle;
	public boolean accelerate;
	private boolean brake;
	
	private Point2D.Double previousPosition;
	private double remainingDistance;
	private Point2D.Double nextTurnningPoint;
	
	private boolean firstTime;
	
	/**
	 * 
	 * @param car
	 * @param distance
	 * @param route
	 */
	public Drive(Car car,double distance,ArrayList<Point2D.Double> route){
		this.car = car;
		this.remainingDistance = distance;
		this.route = route;
		this.nextTurnningPoint = route.get(0);
		this.firstTime = true;
		this.currentPosition = new Point2D.Double(0,0);
		this.previousPosition = new Point2D.Double(0,0);
	}

	/**
	 * 
	 */
	public double centralControl(PerceptionResponse[] results) {
//		initial
		this.angle = 0;
		this.accelerate = this.brake = false;
		
//		get the detail of the car
		this.velocity = this.car.getVelocity();
		this.currentPosition = this.car.getPosition();
		
		if(this.firstTime){
//			car is on the start point, initial the position and accelerate the car 
			this.car.accelerate();
			previousPosition.x = currentPosition.x;
			previousPosition.y = currentPosition.y;
			this.firstTime = false;
		}else{
//			handle the highest priority item
			this.response(results);
			this.nextTurnningPoint = route.get(0);
			if((nextTurnningPoint.x-1)<currentPosition.x && currentPosition.x<(nextTurnningPoint.x+1) 
					&& (nextTurnningPoint.y-1)<currentPosition.y && currentPosition.y<(nextTurnningPoint.y+1)){
//				if it is the turning point, turn left or right
				this.turn();
			}
			this.correctDirection();
		}

//		update remaining distance
		double distanceX = Math.abs(currentPosition.x-previousPosition.x);
		double distanceY = Math.abs(currentPosition.y-previousPosition.y);
		this.remainingDistance = remainingDistance-distanceX-distanceY;
		
//		System.out.println(currentPosition.x+"  "+previousPosition.x);
		System.out.println(this.remainingDistance);

		previousPosition.x = currentPosition.x;
		previousPosition.y = currentPosition.y;
		return remainingDistance;
	}

	public void response(PerceptionResponse[] results) {
//		boolean getItem=true;
//		int i=0;
//		Classification type=null;;
//		PerceptionResponse p=null;
//		while(getItem){
//			if(i<results.length){
////				get the detail of the next item
//				p=results[i];
//				float x=p.direction.x;
//				float y=p.direction.y;
//				type=p.objectType;
////				get the first car or trafficlight which is in front of the car
//				if(x*velocity.x+y*velocity.y>0 && (type.equals("Car")||type.equals("TrafficLight"))){
//					getItem=false;
//				}else{
//					i++;
//				}
//			}			
//		}
////		if something need to be responed, accelerate or brake
//		if(p!=null){
//			if(type.equals("TrafficLight") && p.information.get("State")=="green"){
//				if(p.distance<3 && velocity.x*velocity.x+velocity.y*velocity.y>100){
//					this.brake = true;
//				}else{
//					this.accelerate = true;
//				}
//			}else{
//				if(p.timeToCollision<3 && velocity.x+velocity.y>0){
//					this.brake = true;
//				}else{
//					this.accelerate = true;
//				}
//			}
//		}
		
		

//		test code
//		control the velocity of the car, set the velocity from 10 to 20
		double speed = velocity.x*velocity.x+velocity.y*velocity.y;
		if(speed > 400)
			this.brake = true;
		if(speed < 100)
			this.accelerate = true;
		
		if(speed/100 < this.remainingDistance+1 && speed/100 > this.remainingDistance-1 ){
			this.brake = true;
			this.accelerate = false;
		}
		
	}

	public void turn() {
		route.remove(0);
		if(!route.isEmpty())
			this.nextTurnningPoint = route.get(0);
		else{
			System.out.println("it is the end");
		}
		int deltaX = (int)(nextTurnningPoint.x-currentPosition.x);
		int deltaY = (int)(nextTurnningPoint.y-currentPosition.y);

		int x = (int)velocity.x;
		int y = (int)velocity.y;
		
		if(x*deltaX==0 && y*deltaY==0){
			if(deltaX<0 || deltaY>0){
//				turn left
				System.out.println("TURN LEFT");
				this.angle = 22.5;
			}else{
//				turn right
				System.out.println("TURN RIGHT");
				this.angle = -22.5;
			}
		}else if(x*deltaX<0 || y*deltaY<0){
//			turn around
			System.out.println("TURN AROUND");
			this.angle = 45;
			
		}
	}

	public void correctDirection() {

//		double correct = 0;
////		correct the direction
//		if(x==0){
//			if(y>0){
//				if(velocity.x>0){
//					this.angle += 0.1;
//				}
//				if(velocity.x<0){
//					this.angle -= 0.1;
//				}
//			}else{
//				if(velocity.x>0){
//					this.angle -= 0.1;
//				}
//				if(velocity.x<0){
//					this.angle += 0.1;
//				}
//			}
//		}
//		if(y==0){
//			if(x>0){
//				if(velocity.y>0){
//					this.angle -= 0.1;
//				}
//				if(velocity.y<0){
//					this.angle += 0.1;
//				}
//			}else{
//				if(velocity.y>0){
//					this.angle += 0.1;
//				}
//				if(velocity.y<0){
//					this.angle -= 0.1;
//				}			
//			}
//		}
//		this.angle += correct;
	}

	public void update() {
		if(this.accelerate)
			this.car.accelerate();
		if(this.brake)
			this.car.brake();
		if(this.angle != 0)
			this.car.turn((float)angle);
	}
}
