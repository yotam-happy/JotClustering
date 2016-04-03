package edu.clustering.jot.interfaces;

import java.util.List;
import java.util.stream.Collectors;

import edu.clustering.jot.kmeans.Cluster;

public interface ClusteringAlgorithm<T extends Point> {
	
	void reset();
	void doClustering(int k, int minClustersToMaintain, List<T> points);
	
	List<Cluster<T>> getClusters();
	
	default List<T> getCentroids(){
		return getClusters().stream().map((x)->x.getCentroid()).collect(Collectors.toList());
	}
	
	default List<T> getWeigthedCentroids(){
		return getClusters().stream().map((x)->{
			T c = x.getCentroid();
			double w = x.getPoints().stream().mapToDouble((p)->p.getWeight()).sum();
			c.setWeight(w);
			return c;
		}).collect(Collectors.toList());
	}
	
	void setName(String name);
	String getName();
}
