package edu.clustering.jot.util;

import java.util.List;

import edu.clustering.jot.interfaces.Point;
import edu.clustering.jot.kmeans.Cluster;

public class ClusteringUtils {
	public static <T extends Point> double getCost(List<Cluster<T>> clusters){
		double r = 0;
		for(Cluster<T> c : clusters){
			for(T p : c.getPoints()){
				r += c.getCentroid().distance(p) * p.getWeight();
			}
		}
		return r;
	}
}
