package edu.clustering.jot.datapoint;

import java.util.List;

import edu.clustering.jot.interfaces.Point;

public class DenseEucledianPoint implements Point{

	double weight = 1;
	double[] coords;
	
	public void set(int d, double v){
		coords[d] = v;
	}
	
	public double get(int d){
		return coords[d];
	}
	
	public DenseEucledianPoint(int d){
		coords = new double[d];
	}
	
	public int dim() {
		return coords.length;
	}
	
	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public double metric(Point x, Point y) {
		DenseEucledianPoint xp = (DenseEucledianPoint)x;
		DenseEucledianPoint yp = (DenseEucledianPoint)y;
		
		double dist = 0;
		for(int i = 0; i < xp.coords.length; i++){
			dist+= Math.pow(Math.abs(xp.coords[i] - yp.coords[i]), 2);
		}
		return dist;
	}

	@Override
	public Point centroid(List<? extends Point> l) {
		DenseEucledianPoint p0 = (DenseEucledianPoint)l.get(0);
		DenseEucledianPoint r = new DenseEucledianPoint(p0.coords.length);
		int w = 0;
		for(Point p : l){
			w += p.getWeight();
			DenseEucledianPoint pp = (DenseEucledianPoint) p;
			for(int i = 0; i < r.coords.length; i++){
				r.coords[i] += p.getWeight() * pp.coords[i];
			}
		}
		for(int i = 0; i < r.coords.length; i++){
			r.coords[i] /= (double)w;
		}
		r.setWeight(w);
		return r;
	}

}
