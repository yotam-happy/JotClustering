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
	int interk;
	int l;
	ClusteringAlgorithm<T> intermediateClusterer;
	ClusteringAlgorithm<T> finalClusterer;
	List<List<T>> levels = new ArrayList<List<T>>();
	
	public void reset(){
		intermediateClusterer.reset();
		finalClusterer.reset();
		levels = new ArrayList<>();
	}
	
	public MergeAndReduce(
			int k, 
			int l,
			ClusteringAlgorithm<T> clusterer){
		this(k,k,l,clusterer,clusterer);
	}

	public MergeAndReduce(
			int k, 
			int interk,
			int l,
			ClusteringAlgorithm<T> intermediateClusterer,
			ClusteringAlgorithm<T> finalClusterer){
		this.k = k;
		this.interk = k;
		this.l = l;
		this.intermediateClusterer = intermediateClusterer;
		this.finalClusterer = finalClusterer;
		
		levels.add(new ArrayList<>());
	}
	
	public void processPoint(T point){
		processPoints(Arrays.asList(point), 0);
	}
	
	protected void processPoints(List<T> points, int level){
		if(level >= levels.size()){
			levels.add(new ArrayList<>());
		}
		levels.get(level).addAll(points);
		if (levels.get(level).size() >= k * l){
			intermediateClusterer.doClustering(interk, interk, levels.get(level));
			levels.get(level).clear();
			
			processPoints(intermediateClusterer.getWeigthedCentroids(), level + 1);
		}
	}
	
	public List<Cluster<T>> getEstimatedClusters(){
		// clear all levels
		if (levels.size() > 1){
			for(int i = 0; i < levels.size() - 1; i++){
				if (levels.get(i).size() / l >= 2){
					intermediateClusterer.doClustering(levels.get(i).size() / l, 
							levels.get(i).size() / l, 
							levels.get(i));
					levels.get(i).clear();
					levels.get(i + 1).addAll(intermediateClusterer.getWeigthedCentroids());
				}
			}
		}
		finalClusterer.doClustering(k, k, levels.get(levels.size() - 1));
		
		return finalClusterer.getClusters();
	}
	
	public String someStats(){
		//int mem = levels.stream().mapToInt((l)->l.size()).sum();
		return "levels: " + levels.size() + " memory: " + levels.get(levels.size() - 1).size() + " (samples)";
	}

	String name;
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
}
