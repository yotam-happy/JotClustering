package edu.clustering.jot.iterated;

import java.util.List;

import edu.clustering.jot.interfaces.ClusteringAlgorithm;
import edu.clustering.jot.interfaces.Point;
import edu.clustering.jot.kmeans.Cluster;
import edu.clustering.jot.util.ClusteringUtils;

public class IteratedClustering<T extends Point> implements ClusteringAlgorithm<T>{
	
	ClusteringAlgorithm<T> algo;
	int k;
	
	List<Cluster<T>> clusters;
	
	public IteratedClustering(
			ClusteringAlgorithm<T> algo, 
			int k) {
		this.algo = algo;
		this.k = k;
	}
	
	public void reset(){
		algo.reset();
		clusters = null;
	}

	@Override
	public void doClustering(int k, int minClustersToMaintain, List<T> points) {
		double bestCost = Double.MAX_VALUE;
		
		for(int i = 0; i < k; i++){
			algo.doClustering(k, minClustersToMaintain, points);
			List<Cluster<T>> a = algo.getClusters();
			double c = ClusteringUtils.getCost(a);
			if(c < bestCost){
				bestCost = c;
				clusters = a;
			}
		}
	}

	@Override
	public List<Cluster<T>> getClusters() {
		return clusters;
	}
	
	String name;
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
}
