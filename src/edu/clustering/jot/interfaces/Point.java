package edu.clustering.jot.interfaces;

import java.util.List;


public interface Point {
	public double getWeight();
	public void setWeight(double weight);
	public double metric(Point x,Point y);
	public Point centroid(List<? extends Point> l); 
	
	public default double distance(Point y){
		return metric(this, y);
	}
}
