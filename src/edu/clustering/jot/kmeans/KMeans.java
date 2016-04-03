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
import edu.clustering.jot.util.RandomUtils;
import edu.clustering.jot.util.Tuple;

public class KMeans<T extends Point> implements ClusterInitializer<T>, ClusteringAlgorithm<T>{

    protected List<Cluster<T>> clusters;
    
    protected ClusterInitializer<T> initializer = this;
    protected ClusterInitializer<T> initializerForDead = this;

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
    public void setClusterInitializerForDeadClusters(ClusterInitializer<T> initializer) {
    	this.initializerForDead = initializer;
    }
    
	public void reset(){
		clusters = null;
	}

	public List<Cluster<T>> getClusters() {
    	return clusters;
    }
    
	/**
	 * Perform K-means by Loyd iteration
	 */
    public void doClustering(int k, int minClustersToMaintain, List<T> points) {
    	// initialize clusters 
    	clusters = initializer.initializeClusters(k, points);
    	
        boolean finish = false;
        int iteration = 0;
        
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while(!finish) {
        	//Clear cluster state
        	clearClusters();
        	
        	//Assign points to the closer cluster
        	assignCluster(points, clusters, true);
            
            //Calculate new centroids.
        	int cid = 0;
        	int clustersResurected;
        	double delta = 0;
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
                	it.remove();
                }
            }
        	
        	// if some clusters died, we split the largest clusters
        	clustersResurected = 0;
        	while(clusters.size() < minClustersToMaintain){
	        		boolean b = splitLargestCluster();
	        		if (!b){
	        			break;
	        		}
	        		clustersResurected ++;
        	}
        	
        	iteration++;
        	
        	// not quite sure about this
        	if(iteration > maxIterations || (clustersResurected == 0 && delta <= minDelta)) {
        		finish = true;
        	}
        }
        
    	// remove redundant centroids
    	for(Iterator<Cluster<T>> it = clusters.iterator(); it.hasNext(); ){
    		Cluster<T> cluster = it.next();
            if(cluster.centroid == null) {
            	it.remove();
            }
        }
        
    }
    
    protected boolean splitLargestCluster(){
    	Cluster<T> c;
    	
    	c = clusters.stream()
    	.sorted((c1,c2)->-Integer.compare(c1.points.size(),c2.points.size()))
    	.findFirst().orElse(null);
    	
    	List<Cluster<T>> n = initializerForDead.initializeClusters(2, c.points);
    	if (n.size() < 2){
    		return false;
    	}
    	assignCluster(c.points, n, false);
    	clusters.remove(c);
    	clusters.addAll(n);
    	return true;
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
    
    protected void assignCluster(List<T> points, List<Cluster<T>> c, boolean report) {
    	Counting counter = report ? new Counting(10000, "Assigning clusters") : null;
        points.stream()
        .forEach((point)->{
    		Tuple<Integer,Double> t = findClosestCluster(point,c);
            if (t != null){
            	c.get(t.x).addPoint(point);
            }
            if (report){
            	counter.addOne();
            }
        });
    }

    public List<Cluster<T>> initializeClusters(int k, List<T> points) {
    	
		List<Cluster<T>> clusters = new ArrayList<Cluster<T>>();
    	Random rnd = RandomUtils.getRandom();
    	
    	//Initialize clusters with random centroids
    	for (int i = 0; i < k; i++) {
    		Cluster<T> cluster = new Cluster<T>(i);
    		T centroid = points.get(rnd.nextInt(points.size()));
    		cluster.setCentroid(centroid);
    		clusters.add(cluster);
    	}
    	
    	return clusters;
    }

    String name;
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
}