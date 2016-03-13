package end.clustering.jot.mergeandreduce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.clustering.jot.interfaces.ClusteringAlgorithm;
import edu.clustering.jot.interfaces.Point;
import edu.clustering.jot.interfaces.StreamingClusteringAlgorithm;
import edu.clustering.jot.kmeans.Cluster;

/**
 * Implements a simple divide and conquer clustering algorithm for a streaming setting
 * This algorithm 
 * @author yotamesh
 *
 */
public class MergeAndReduce<T extends Point> implements StreamingClusteringAlgorithm<T>{
	int k;
	int l;
	ClusteringAlgorithm<T> intermediateClusterer;
	ClusteringAlgorithm<T> finalClusterer;
	List<List<T>> levels = new ArrayList<List<T>>();
	
	public MergeAndReduce(
			int k, 
			int l,
			ClusteringAlgorithm<T> clusterer){
		this(k,l,clusterer,clusterer);
	}

	public MergeAndReduce(
			int k, 
			int l,
			ClusteringAlgorithm<T> intermediateClusterer,
			ClusteringAlgorithm<T> finalClusterer){
		this.k = k;
		this.l = l;
		this.intermediateClusterer = intermediateClusterer;
		this.finalClusterer = finalClusterer;
		
		levels.add(new ArrayList<>());
	}
	
	public void processPoint(T point){
		processPoints(Arrays.asList(point), 0);
	}
	
	protected void processPoints(List<T> points, int level){
		levels.get(level).addAll(points);
		if (levels.get(level).size() >= k * l){
			intermediateClusterer.doClustering(k, levels.get(level));
			levels.get(level).clear();
			processPoints(intermediateClusterer.getWeigthedCentroids(), level + 1);
		}
	}
	
	public List<Cluster<T>> getEstimatedClusters(){
		List<T> allPoints = new ArrayList<>();
		levels.forEach((l)->allPoints.addAll(l));
		finalClusterer.doClustering(k, allPoints);
		return finalClusterer.getClusters();
	}
}
