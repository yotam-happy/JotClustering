package edu.clustering.jot.kmeansplusplus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.clustering.jot.interfaces.ClusterInitializer;
import edu.clustering.jot.interfaces.Point;
import edu.clustering.jot.kmeans.Cluster;
import edu.clustering.jot.util.RandomUtils;

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
		T lastPoint = null;
		double[] dist = new double[points.size()];
		for(int i = 0; i < points.size(); i++){
			dist[i] = Double.MAX_VALUE;
			probs[i] = -1;
		}

		List<Cluster<T>> clusters = new ArrayList<Cluster<T>>();
    	Random rnd = RandomUtils.getRandom();

		// select first centroids at random
    	for(int i = 0; i < t; i++){
			Cluster<T> firstCluster = new Cluster<T>(0);
			lastPoint = points.get(rnd.nextInt(points.size()));
			firstCluster.setCentroid(lastPoint);
			clusters.add(firstCluster);
    	}
		
    	//Initialize rest of clusters with kmeans++ probabilities
    	for (int i = 1; i < k; i++) {
    		probs = setProbs(probs, dist, clusters, points, lastPoint);
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
			double dist[],
			List<Cluster<T>> clusters, 
			List<T> points,
			T lastPoint){
		double total = 0;
		
		// update dist
		for(int i = 0; i < dist.length; i++){
			double d = points.get(i).distance(lastPoint);
			if (d < dist[i]){
				dist[i] = d;
			}
		}

		for(int i = 0; i < probs.length; i++){
			total+= dist[i] * dist[i] * points.get(i).getWeight();
			probs[i] = dist[i] * dist[i] * points.get(i).getWeight();
		}
		
		double ddd = 0;
		// normalize
		for(int i = 0; i < probs.length; i++){
			probs[i] /= total;
			ddd += probs[i];
		}
		return probs;
	}
}
