package edu.clustering.jot.kmeans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import edu.clustering.jot.interfaces.ClusterInitializer;
import edu.clustering.jot.interfaces.ClusteringAlgorithm;
import edu.clustering.jot.interfaces.Point;
import edu.clustering.jot.util.RandomUtils;
import edu.clustering.jot.util.Tuple;

public class KMeans<T extends Point> implements ClusterInitializer<T>, ClusteringAlgorithm<T>{

    protected List<Cluster<T>> clusters;
    
    protected ClusterInitializer<T> initializer = this;
    protected ClusterInitializer<T> initializerForDead = this;

    protected int maxIterations;
    protected double minDelta;
    
    protected BiFunction<T, Cluster<T>, Boolean> constraint;

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
 
    /**
     * constrain is a lambda that takes a point and a cluster and returns true
     * iff the point can be added to the cluster (doesn't violate the constraint)
     */
    public void setContraint(BiFunction<T, Cluster<T>, Boolean> constraint) {
    	this.constraint = constraint;
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
        	double loss = assignCluster(points, clusters) / points.size();
            
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
        	if (clustersResurected > 0){
        		// splitting clusters can mess with id's so fix them
        		fixClusterIds(clusters);
        	}
        	
        	iteration++;
        	
        	// not quite sure about this
        	if(iteration > maxIterations || (clustersResurected == 0 && delta <= minDelta)) {
        		finish = true;
        	}
        	System.out.println("Done iteration " + iteration + " (delta=" + delta + ", loss=" + loss + ")");
        }
        
    	// remove redundant centroids
    	for(Iterator<Cluster<T>> it = clusters.iterator(); it.hasNext(); ){
    		Cluster<T> cluster = it.next();
            if(cluster.centroid == null) {
            	it.remove();
            }
        }
        
    }

    protected void fixClusterIds(Collection<Cluster<T>> clusters){
    	// fix cluster ids
    	int i = 0;
    	for(Cluster<T> cluster : clusters){
    		cluster.id = i;
    		i++;
    	}
    }
    
    protected boolean splitLargestCluster(){
    	Cluster<T> c = clusters.stream()
		    	.sorted((c1,c2)->-Integer.compare(c1.points.size(),c2.points.size()))
		    	.findFirst().orElse(null);
    	
    	List<Cluster<T>> n = initializerForDead.initializeClusters(2, c.points, c.centroid);
    	if (n.size() < 2){
    		return false;
    	}
    	assignCluster(c.points, n);
    	clusters.remove(c);
    	clusters.addAll(n);
    	return true;
    }
    
    protected void clearClusters() {
    	for(Cluster<T> cluster : clusters) {
    		cluster.clear();
    	}
    }
    
    public static <T extends Point> Tuple<Cluster<T>,Double> findClosestCluster(
    		T point, 
    		List<Cluster<T>> clusters){
    	return findClosestCluster(point, clusters, null);
    }
    
    public static <T extends Point> Tuple<Cluster<T>,Double> findClosestCluster(
    		T point, 
    		List<Cluster<T>> clusters,
    		BiFunction<T, Cluster<T>, Boolean> constraint){
    	// get a sorted list
    	List<Tuple<Cluster<T>,Double>> sorted = 
    			clusters.parallelStream()
    			.map((cluster)->new Tuple<Cluster<T>,Double>(cluster,point.distance(cluster.getCentroid())))
    			.sorted((e1,e2)->Double.compare(e1.y, e2.y))
    			.collect(Collectors.toList());

    	if(constraint == null){
    		return sorted.get(0);
    	} else {
    		for(Tuple<Cluster<T>,Double> t : sorted){
    			if(constraint.apply(point, t.x)){
    				return t;
    			}
    		}
    		return null;
    	}		
    }
    
    protected double assignCluster(List<T> points, List<Cluster<T>> c) {
        double[] loss = new double[1];
    	points.stream()
        .forEach((point)->{
    		Tuple<Cluster<T>,Double> t = findClosestCluster(point,c,constraint);
            if (t != null){
            	t.x.addPoint(point);
            }
            loss[0] += t.y * t.y;
        });
    	return loss[0];
    }
    protected double calcLoss(List<T> points, List<Cluster<T>> c){
        double[] loss = new double[1];
    	points.stream()
        .forEach((point)->{
    		Tuple<Cluster<T>,Double> t = findClosestCluster(point,c,constraint);
            loss[0] += t.y * t.y;
        });
    	return loss[0];
    }
    
    public List<Cluster<T>> initializeClusters(int k, List<T> points) {
    	return initializeClusters(k, points, null);
    }

    public List<Cluster<T>> initializeClusters(int k, List<T> points, T firstCentroid){
		List<Cluster<T>> clusters = new ArrayList<Cluster<T>>();
    	Random rnd = RandomUtils.getRandom();
    	
    	if (firstCentroid != null){
    		Cluster<T> cluster = new Cluster<T>(0);
    		cluster.setCentroid(firstCentroid);
    		clusters.add(cluster);
    	}
    	
    	//Initialize clusters with random centroids
    	for (int i = (firstCentroid == null ? 0 : 1); i < k; i++) {
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