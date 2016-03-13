package edu.clustering.jot.interfaces;

import java.util.List;

import edu.clustering.jot.kmeans.Cluster;

public interface StreamingClusteringAlgorithm<T extends Point> {
	void processPoint(T point);
	List<Cluster<T>> getEstimatedClusters();
}