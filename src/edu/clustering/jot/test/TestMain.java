package edu.clustering.jot.test;

import java.util.List;

import edu.clustering.jot.algorithms.AlgorithmConstructor;
import edu.clustering.jot.datapoint.DenseEucledianPoint;
import edu.clustering.jot.interfaces.ClusteringAlgorithm;

public class TestMain {

	public static void main(String[] args) {
		List<DenseEucledianPoint> points = Norm25DatasetGenerator.getPoints(25, 15, 400, 1.0);
		ClusteringAlgorithm<DenseEucledianPoint> algo = 
				AlgorithmConstructor.getKMeans(100, 0.00000001);
		algo.doClustering(25, points);
	}

}
