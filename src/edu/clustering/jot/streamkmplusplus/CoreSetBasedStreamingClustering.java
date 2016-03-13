package edu.clustering.jot.streamkmplusplus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.clustering.jot.interfaces.ClusteringAlgorithm;
import edu.clustering.jot.interfaces.Point;
import edu.clustering.jot.interfaces.StreamingClusteringAlgorithm;
import edu.clustering.jot.kmeans.Cluster;

public class CoreSetBasedStreamingClustering<T extends Point> implements StreamingClusteringAlgorithm<T>{
	// number of final clusters
	int k;
	
	// number of points in coreset
	int m;
	
	StreamingClusteringAlgorithm<T> coreSetConstructor;
	ClusteringAlgorithm<T> clusterer;
	
	public CoreSetBasedStreamingClustering(
			int k, 
			int m,
			StreamingClusteringAlgorithm<T> coreSetConstructor,
			ClusteringAlgorithm<T> clusterer) {
		this.k = k;
		this.m = m;

		this.coreSetConstructor = coreSetConstructor;
		this.clusterer = clusterer;
	}
	
	@Override
	public void processPoint(T point) {
		coreSetConstructor.processPoint(point);
	}

	@Override
	public List<Cluster<T>> getEstimatedClusters() {
		// get CoreSet
		List<Cluster<T>> arr = coreSetConstructor.getEstimatedClusters();
		List<T> coreSet = new ArrayList<>();
		coreSet = arr.stream().map((x)->x.getCentroid()).collect(Collectors.toList());
		
		// cluster the coreset
		clusterer.doClustering(k, coreSet);
		return clusterer.getClusters();
	}

}
