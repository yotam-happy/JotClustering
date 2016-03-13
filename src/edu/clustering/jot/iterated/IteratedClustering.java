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
	
	@Override
	public void doClustering(int k, List<T> points) {
		double bestCost = Double.MAX_VALUE;
		
		for(int i = 0; i < k; i++){
			algo.doClustering(k, points);
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
	
}
