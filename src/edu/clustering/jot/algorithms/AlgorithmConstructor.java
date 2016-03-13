package edu.clustering.jot.algorithms;

import edu.clustering.jot.interfaces.ClusteringAlgorithm;
import edu.clustering.jot.interfaces.Point;
import edu.clustering.jot.kmeans.KMeans;
import edu.clustering.jot.kmeansplusplus.KMeansPlusPlusInitializer;
import edu.clustering.jot.streamkmplusplus.CoreSetConstructor;

public class AlgorithmConstructor {
	
	public static <T extends Point> ClusteringAlgorithm<T> getKMeans(
    		int maxIterations,
    		double minDelta){
		KMeans<T> algo = new KMeans<>(maxIterations, minDelta);
		return algo;
	}
	
	public static <T extends Point> ClusteringAlgorithm<T> getKMeansPlusPlus(
    		int maxIterations,
    		double minDelta){
		KMeans<T> algo = new KMeans<>(maxIterations, minDelta);
		algo.setClusterInitializer(new KMeansPlusPlusInitializer<>(1));
		return algo;
	}

	public static <T extends Point> ClusteringAlgorithm<T> getKMeansSharp(
    		int maxIterations,
    		double minDelta,
    		int k){
		KMeans<T> algo = new KMeans<>(maxIterations, minDelta);
		// t = 3 * log2(k)
		algo.setClusterInitializer(new KMeansPlusPlusInitializer<>(
				(int)(3.0 * Math.log(k) / Math.log(2))));
		return algo;
	}

	public static <T extends Point> ClusteringAlgorithm<T> getCoreSetConstructor(){
		CoreSetConstructor<T> algo = new CoreSetConstructor<>();
		return algo;
	}
}
