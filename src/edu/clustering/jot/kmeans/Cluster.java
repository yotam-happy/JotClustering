package edu.clustering.jot.kmeans;

import java.util.ArrayList;
import java.util.List;

import edu.clustering.jot.interfaces.Point;

public class Cluster<T extends Point> {
	
	public List<T> points;
	public T centroid;
	public int id;
	
	//Creates a new Cluster
	public Cluster(int id) {
		this.id = id;
		this.points = new ArrayList<>();
		this.centroid = null;
	}
 
	public List<T> getPoints() {
		return points;
	}
	
	public synchronized void addPoint(T point) {
		points.add(point);
	}
 
	public synchronized void setPoints(List<T> points) {
		this.points = points;
	}
	
	public void setId(int id){
		this.id = id;
	}
 
	public T getCentroid() {
		return centroid;
	}
 
	public synchronized void setCentroid(T centroid) {
		this.centroid = centroid;
	}
 
	public int getId() {
		return id;
	}
	
	public synchronized void clear() {
		points.clear();
	}
	
	public double cost(){
		double c = 0;
		for(T p : points){
			c += p.distance(centroid);
		}
		return c;
	}
}