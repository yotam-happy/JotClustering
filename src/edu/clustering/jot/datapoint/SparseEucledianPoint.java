package edu.clustering.jot.datapoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.clustering.jot.interfaces.Point;

public class SparseEucledianPoint<T> implements Point{

	double weight = 1;
	Map<T,Double> coords = new HashMap<>();

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public void setWeight(double weight) {
		this.weight = weight;
	}

	@SuppressWarnings("unchecked")
	@Override
	public double metric(Point x, Point y) {
		SparseEucledianPoint<T> xp = (SparseEucledianPoint<T>)x;
		SparseEucledianPoint<T> yp = (SparseEucledianPoint<T>)y;
		
		double dist = 0;
		for(Entry<T,Double> e : xp.coords.entrySet()){
			Double c = yp.coords.get(e.getKey());
			if(c != null){
				dist += Math.pow(Math.abs(e.getValue() - c), 2);
			}
		}
		return dist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Point centroid(List<? extends Point> l) {
		SparseEucledianPoint<T> r = new SparseEucledianPoint<T>();
		double w = 0;

		for(Point p : l){
			SparseEucledianPoint<T> pp = (SparseEucledianPoint<T>) p;
			w += pp.getWeight();
			for(Entry<T,Double> e : pp.coords.entrySet()){
				Double d = r.coords.get(e.getKey());
				if(d == null){
					r.coords.put(e.getKey(), e.getValue() * p.getWeight());
				}else{
					r.coords.put(e.getKey(), d + e.getValue() * p.getWeight());
				}
			}
		}
		
		for(Entry<T,Double> e : r.coords.entrySet()){
			r.coords.put(e.getKey(), e.getValue() / w);
		}
		
		r.setWeight(w);
		return r;
	}

}
