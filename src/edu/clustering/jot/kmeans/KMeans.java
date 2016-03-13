package edu.clustering.jot.kmeans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import edu.clustering.jot.interfaces.ClusterInitializer;
import edu.clustering.jot.interfaces.ClusteringAlgorithm;
import edu.clustering.jot.interfaces.Point;
import edu.clustering.jot.util.Counting;
import edu.clustering.jot.util.Tuple;

public class KMeans<T extends Point> implements ClusterInitializer<T>, ClusteringAlgorithm<T>{

    protected List<Cluster<T>> clusters;
    
    protected ClusterInitializer<T> initializer = this;

    protected int maxIterations;
    protected double minDelta;

    /**
     * @param maxIterations		maimum iterations to run
     * @param minDelta			minimum delta criterion to stop iteration (minimum sum of changes to
     *							centroids to continue iterations) 							
     */
    public KMeans(int maxIterations, double minDelta) {
    	this.maxIterations = maxIterations;
    	this.minDelta = minDelta;
    }
    
    public void setClusterInitializer(ClusterInitializer<T> initializer) {
    	this.initializer = initializer;
    }
    
    public List<Cluster<T>> getClusters() {
    	return clusters;
    }
    
	/**
	 * Perform K-means by Loyd iteration
	 */
    public void doClustering(int k, List<T> points) {
    	// initialize clusters 
    	clusters = initializer.initializeClusters(k, points);
    	
        boolean finish = false;
        int iteration = 0;
        
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while(!finish) {
        	//Clear cluster state
        	clearClusters();
        	
        	//Assign points to the closer cluster
        	assignCluster(points);
            
            //Calculate new centroids.
        	int cid = 0;
        	double delta = 0;
        	boolean clusterDied = false;
        	for(Iterator<Cluster<T>> it = clusters.iterator(); it.hasNext();){
        		Cluster<T> cluster = it.next();
                if(!cluster.getPoints().isEmpty()) {
                    @SuppressWarnings("unchecked")
					T centroid = (T)cluster.getPoints().get(0).centroid(cluster.getPoints());
                	delta += cluster.centroid.distance(centroid);
                	cluster.setCentroid(centroid);
                	cluster.setId(cid);
                	cid++;
                } else {
                	clusterDied = true;
                	it.remove();
                }
            }
        	
        	iteration++;
        	System.out.println("Done iteration: " + iteration + 
        			(clusterDied ? ", some clusters died" : ", centroids delta: " + delta));
        	
        	// not quite sure about this
        	if(iteration > maxIterations || (!clusterDied && delta <= minDelta)) {
        		finish = true;
        	}
        }
        
    	System.out.println("Done iterations!");
    	
    	// remove redundant centroids
    	for(Iterator<Cluster<T>> it = clusters.iterator(); it.hasNext(); ){
    		Cluster<T> cluster = it.next();
            if(cluster.centroid == null) {
            	it.remove();
            }
        }
        
    }
    
    protected void clearClusters() {
    	for(Cluster<T> cluster : clusters) {
    		cluster.clear();
    	}
    }
    
    public static <T extends Point> Tuple<Integer,Double> findClosestCluster(
    		T point, 
    		List<Cluster<T>> clusters){
    	List<Tuple<Integer,Double>> l = clusters.parallelStream()
			.map((cluster)->new Tuple<Integer,Double>(cluster.id,point.distance(cluster.getCentroid())))
	    	.sorted((e1,e2)->Double.compare(e1.y, e2.y)).collect(Collectors.toList());
    	
		return l.get(0);
    }
    
    protected void assignCluster(List<T> points) {
    	Counting counter = new Counting(10000, "Assigning clusters"); 
        points.stream()
        .forEach((point)->{
    		Tuple<Integer,Double> t = findClosestCluster(point,clusters);
            if (t != null){
            	clusters.get(t.x).addPoint(point);
            }
            counter.addOne();
        });
    }

    public List<Cluster<T>> initializeClusters(int k, List<T> points) {
    	
		List<Cluster<T>> clusters = new ArrayList<Cluster<T>>();
    	Random rnd = new Random(System.currentTimeMillis());
    	
    	//Initialize clusters with random centroids
    	for (int i = 0; i < k; i++) {
    		Cluster<T> cluster = new Cluster<T>(i);
    		T centroid = points.get(rnd.nextInt(points.size()));
    		cluster.setCentroid(centroid);
    		clusters.add(cluster);
    	}
    	
    	return clusters;
    }
}