package edu.clustering.jot.algorithms;

import edu.clustering.jot.interfaces.ClusteringAlgorithm;
import edu.clustering.jot.interfaces.Point;
import edu.clustering.jot.interfaces.StreamingClusteringAlgorithm;
import edu.clustering.jot.iterated.IteratedClustering;
import edu.clustering.jot.streamkmplusplus.CoreSetBasedStreamingClustering;
import edu.clustering.jot.streamkmplusplus.CoreSetConstructor;
import end.clustering.jot.mergeandreduce.MergeAndReduce;

public class StreamingAlgorithmConstructor {
	
	public static <T extends Point> StreamingClusteringAlgorithm<T> getSimpleMergeAndReduce(
			int k, 
			int l, 
			ClusteringAlgorithm<T> c){
		MergeAndReduce<T> algo = new MergeAndReduce<>(k, l, c);
		return algo;
	}
	
	public static <T extends Point> StreamingClusteringAlgorithm<T> getStreamKMPlusPlus(
			int k, 
			int m,
    		int maxIterations,
    		double minDelta){
		ClusteringAlgorithm<T> coreSetAlgo = new CoreSetConstructor<>();
		StreamingClusteringAlgorithm<T> coreSetStreaming = getSimpleMergeAndReduce(k, 2, coreSetAlgo);

		ClusteringAlgorithm<T> finalClustering = 
				AlgorithmConstructor.getKMeansPlusPlus(maxIterations, minDelta);
		
		CoreSetBasedStreamingClustering<T> algo = 
				new CoreSetBasedStreamingClustering<>(k, m, coreSetStreaming, finalClustering);
		
		return algo;
	}
	
	public static <T extends Point> StreamingClusteringAlgorithm<T> getStreamingKMeansSharp(
			int k, 
			int l,
    		int maxIterations,
    		double minDelta){
		ClusteringAlgorithm<T> algo1 = AlgorithmConstructor.getKMeansSharp(maxIterations, minDelta, k);
		algo1 = new IteratedClustering<>(algo1, (int)(3.0 * Math.log(k) / Math.log(2)));
		
		ClusteringAlgorithm<T> algo2 = AlgorithmConstructor.getKMeansPlusPlus(maxIterations, minDelta);
		
		StreamingClusteringAlgorithm<T> stream = new MergeAndReduce<>(
				k, 
				l * (int)(3.0 * Math.log(k) / Math.log(2)), 
				algo1, 
				algo2);
		return stream;
	}
}