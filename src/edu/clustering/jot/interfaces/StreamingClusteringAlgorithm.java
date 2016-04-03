package edu.clustering.jot.interfaces;

import java.util.List;

import edu.clustering.jot.kmeans.Cluster;

public interface StreamingClusteringAlgorithm<T extends Point> {
	void reset();
	void processPoint(T point);
	List<Cluster<T>> getEstimatedClusters();
	public String someStats();

	void setName(String name);
	String getName();
}