package edu.clustering.jot.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.clustering.jot.interfaces.ClusteringAlgorithm;
import edu.clustering.jot.interfaces.Point;
import edu.clustering.jot.kmeans.Cluster;
import edu.clustering.jot.util.RandomUtils;

public class UniformSampleClustering<T extends Point> implements ClusteringAlgorithm<T>{

	public void reset(){
		clusters = null;
	}
	
	List<Cluster<T>> clusters; 
	@Override
	public void doClustering(int k, int minClustersToMaintain, List<T> points) {
		clusters = new ArrayList<>();
		Random rnd = RandomUtils.getRandom();
		for(int i = 0; i < k; i++){
			int idx = rnd.nextInt(points.size());
			Cluster<T> cluster = new Cluster<>(i);
			cluster.setCentroid(points.get(idx));
			cluster.addPoint(points.get(idx));
			clusters.add(cluster); 
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
