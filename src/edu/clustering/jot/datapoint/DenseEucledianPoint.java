package edu.clustering.jot.datapoint;

import java.io.Serializable;
import java.util.List;

import edu.clustering.jot.interfaces.Point;

public class DenseEucledianPoint implements Point, Serializable{
	private static final long serialVersionUID = -3655787571208615993L;

	double weight = 1;
	double[] coords;
	Metric metricToUse = Metric.EuclideanDistance;
	
	public void set(int d, double v){
		coords[d] = v;
	}
	
	public double get(int d){
		return coords[d];
	}
	
	public DenseEucledianPoint(int d){
		coords = new double[d];
	}
	public DenseEucledianPoint(int d, Metric m){
		coords = new double[d];
		metricToUse = m;
	}
	public DenseEucledianPoint(double[] coords, Metric m){
		this.coords = coords;
		metricToUse = m;
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

		if (metricToUse == Metric.EuclideanDistance){
			
			double dist = 0;
			for(int i = 0; i < xp.coords.length; i++){
				dist+= Math.pow(Math.abs(xp.coords[i] - yp.coords[i]), 2);
			}
			return dist;
		}else{
			double a = 0;
			double b = 0;
			double c = 0;
			for(int i = 0; i < xp.coords.length; i++){
				a += xp.coords[i] * yp.coords[i]; 
				b += xp.coords[i] * xp.coords[i]; 
				c += yp.coords[i] * yp.coords[i]; 
			}
			double v = a / (Math.sqrt(b) * Math.sqrt(c));
			if (v < -1){
				v = -1;
			} else if (v > 1){
				v = 1;
			}
			return Math.abs(Math.acos(v));
		}
	}

	@Override
	public Point centroid(List<? extends Point> l) {
		DenseEucledianPoint p0 = (DenseEucledianPoint)l.get(0);
		DenseEucledianPoint r = new DenseEucledianPoint(p0.coords.length, p0.metricToUse);
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

	public static enum Metric {
		EuclideanDistance,
		CosineDistnce
	}
}
