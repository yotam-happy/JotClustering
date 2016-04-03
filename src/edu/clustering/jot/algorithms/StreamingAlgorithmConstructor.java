package edu.clustering.jot.algorithms;

import edu.clustering.jot.interfaces.ClusteringAlgorithm;
import edu.clustering.jot.interfaces.Point;
import edu.clustering.jot.interfaces.StreamingClusteringAlgorithm;
import edu.clustering.jot.iterated.IteratedClustering;
import edu.clustering.jot.streamkmplusplus.CoreSetBasedStreamingClustering;
import edu.clustering.jot.streamkmplusplus.CoreSetConstructor;
import edu.clustering.jot.test.UniformSampleClustering;
import end.clustering.jot.mergeandreduce.MergeAndReduce;

public class StreamingAlgorithmConstructor {
	
	public static <T extends Point> StreamingClusteringAlgorithm<T> getSimpleMergeAndReduce(
			int k, 
			int l, 
			ClusteringAlgorithm<T> c){
		MergeAndReduce<T> algo = new MergeAndReduce<>(k, l, c);
		algo.setName("merge_reduce(k=" + k + ",a=" + c.getName() + ")");
		return algo;
	}
	
	public static <T extends Point> StreamingClusteringAlgorithm<T> getStreamKMPlusPlus(
			int k, 
			int m,
    		int maxIterations,
    		double minDelta){
		ClusteringAlgorithm<T> coreSetAlgo = new CoreSetConstructor<>();
		StreamingClusteringAlgorithm<T> coreSetStreaming = getSimpleMergeAndReduce(m, 2, coreSetAlgo);

		ClusteringAlgorithm<T> finalClustering = 
				AlgorithmConstructor.getKMeansPlusPlus(maxIterations, minDelta);
		
		CoreSetBasedStreamingClustering<T> algo = 
				new CoreSetBasedStreamingClustering<>(k, m, coreSetStreaming, finalClustering);
		
		algo.setName("StreamKM++(k=" + k + ")");
		return algo;
	}

	public static <T extends Point> StreamingClusteringAlgorithm<T> getRandomSample(
			int k, 
			int m,
    		int maxIterations,
    		double minDelta){
		ClusteringAlgorithm<T> randomSample = new UniformSampleClustering<>();
		StreamingClusteringAlgorithm<T> coreSetStreaming = getSimpleMergeAndReduce(m, 2, randomSample);

		ClusteringAlgorithm<T> finalClustering = 
				AlgorithmConstructor.getKMeansPlusPlus(maxIterations, minDelta);
		
		CoreSetBasedStreamingClustering<T> algo = 
				new CoreSetBasedStreamingClustering<>(k, m, coreSetStreaming, finalClustering);
		
		algo.setName("randomSample(k="+k+")");
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
				(int)(3.0 * Math.log(k) / Math.log(2)),
				l, 
				algo1, 
				algo2);
		stream.setName("StreamingKMeans#(k="+k+")");
		return stream;
	}
}