package com.unimelb.swen30006.group11;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.unimelb.swen30006.partc.ai.interfaces.IPlanning;
import com.unimelb.swen30006.partc.ai.interfaces.PerceptionResponse;
import com.unimelb.swen30006.partc.ai.interfaces.PerceptionResponse.Classification;
import com.unimelb.swen30006.partc.controllers.AIController;
import com.unimelb.swen30006.partc.core.objects.Car;
import com.unimelb.swen30006.partc.roads.RoadMarking;

public class PlanningGroup11 implements IPlanning{
	
	private Car car;
	
	private RoadMarking start;
	private RoadMarking destination;
	private double distance;
	private boolean canCaculate;
	private ArrayList<Point2D.Double> route;

	private float currentTime;
	private double remainingDistance;
	
	private float eta;
	

	private iDrive driveController;
	
	public PlanningGroup11(Car car){
		this.car=car;
		this.currentTime = 0;
//		text program,set a route and its distance
		this.route = new ArrayList<Point2D.Double>();
		route.add(new Point2D.Double(160,140));
		route.add(new Point2D.Double(160,270));
		route.add(new Point2D.Double(160,320));
		route.add(new Point2D.Double(270,320));
		distance=370;
		
		this.driveController = new Drive(this.car,this.distance,this.route);
		
	}
	
	@Override
	public boolean planRoute(Point2D.Double destination) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(PerceptionResponse[] results, float delta) {

//		priority the PerceptionResponse
		results=this.prioritisingPerceptionResponse(results);
//		handle the car
		this.remainingDistance = this.driveController.centralControl(results);
		this.driveController.update();
		this.car.update(delta);
//		update current time
		currentTime += delta;
	}

	@Override
	public float eta() {
		// TODO Auto-generated method stub
		return 0;
	}

	private PerceptionResponse[] prioritisingPerceptionResponse(PerceptionResponse[] results){
		return null;
		
	}
	
	private boolean compareRouteDistance(float distance){
		return false;
	}
}
