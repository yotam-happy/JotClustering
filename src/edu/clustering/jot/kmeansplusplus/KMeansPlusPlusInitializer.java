package edu.clustering.jot.kmeansplusplus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.clustering.jot.interfaces.ClusterInitializer;
import edu.clustering.jot.interfaces.Point;
import edu.clustering.jot.kmeans.Cluster;
import edu.clustering.jot.kmeans.KMeans;
import edu.clustering.jot.util.RandomUtils;
import edu.clustering.jot.util.Tuple;

/**
 * Implementing the kmeans++ initialization procedure
 *
 * @inproceedings{arthur2007k,
 *   title={k-means++: The advantages of careful seeding},
 *   author={Arthur, David and Vassilvitskii, Sergei},
 *   booktitle={Proceedings of the eighteenth annual ACM-SIAM symposium on Discrete algorithms},
 *   pages={1027--1035},
 *   year={2007},
 *   organization={Society for Industrial and Applied Mathematics}
 * }
 *
 * @author yotamesh
 *
 */
public class KMeansPlusPlusInitializer<T extends Point> implements ClusterInitializer<T>{

	int t;

    public KMeansPlusPlusInitializer(int t) {
    	this.t = t;
	}
	
	@Override
	public List<Cluster<T>> initializeClusters(int k, List<T> points) {
		double[] probs = new double[points.size()];

		List<Cluster<T>> clusters = new ArrayList<Cluster<T>>();
    	Random rnd = RandomUtils.getRandom();

		// select first centroids at random
    	for(int i = 0; i < t; i++){
			Cluster<T> firstCluster = new Cluster<T>(0);
			firstCluster.setCentroid(points.get(rnd.nextInt(points.size())));
			clusters.add(firstCluster);
    	}
		
    	//Initialize rest of clusters with kmeans++ probabilities
    	for (int i = 1; i < k; i++) {
    		probs = setProbs(probs, clusters, points);
    		if (Double.isNaN(probs[0])){
    			// This means all points are the same, no need for more clusters
    			break;
    		}
    		for(int j = 0; j < t; j++){
	    		T centroid = points.get(RandomUtils.getItemByProb(probs, rnd.nextDouble()));
	    		Cluster<T> cluster = new Cluster<>(i * t + j);
	    		cluster.setCentroid(centroid);
	    		clusters.add(cluster);
    		}
    	}
    	
    	return clusters;
	}
	
	protected static <T extends Point> double[] setProbs(
			double probs[], 
			List<Cluster<T>> clusters, 
			List<T> points){
		double total = 0;
		
		for(int i = 0; i < probs.length; i++){
			if (clusters != null){
				Tuple<Integer,Double> t = KMeans.findClosestCluster(points.get(i), clusters);
				total+= t.y * points.get(i).getWeight();
				probs[i] = t.y * points.get(i).getWeight();
			} else {
				total+= points.get(i).getWeight();
				probs[i] = points.get(i).getWeight();
			}
		}
		
		// normalize
		for(int i = 0; i < probs.length; i++){
			probs[i] /= total;
		}
		
		return probs;
	}
}
