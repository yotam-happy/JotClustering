package edu.clustering.jot.streamkmplusplus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.clustering.jot.interfaces.ClusteringAlgorithm;
import edu.clustering.jot.interfaces.Point;
import edu.clustering.jot.kmeans.Cluster;
import edu.clustering.jot.util.RandomUtils;

/**
 * This algorithm performs the coreset construction from (1). Note that the use of ClusteringAlgorithm
 * interface as this algorithm returns a coreset and not a clustering. However there is quite a similarity
 * between the two and we use the same interface for convenience.
 *
 * (1)
 * @article{ackermann2012streamkm++,
 *   title={StreamKM++: A clustering algorithm for data streams},
 *   author={Ackermann, Marcel R and M{\"a}rtens, Marcus and Raupach, Christoph and Swierkot, Kamil and Lammersen, Christiane and Sohler, Christian},
 *   journal={Journal of Experimental Algorithmics (JEA)},
 *   volume={17},
 *   pages={2--4},
 *   year={2012},
 *   publisher={ACM}
 * }
 *  
 * @author yotamesh
 *
 * @param <T>
 */

public class CoreSetConstructor<T extends Point> implements ClusteringAlgorithm<T>{

	Random rnd = new Random(System.currentTimeMillis());
	
	// this is the final size of the returned coreset. Note in (1) it is denoted by m and
	// k has a different meaning
	int k;

	CoreSetTreeNode<T> root;
	
	protected double calcWeight(List<T> s, T r){
		double w = 0;
		for (T p : s){
			w += r.distance(p) * p.getWeight();
		}
		return w;
	}
	
	@Override
	public void doClustering(int k, List<T> points) {
		this.k = k;
		
		// first representative is selected at random
		int r = rnd.nextInt(points.size());
		root = new CoreSetTreeNode<>(
				new ArrayList<T>(points), 
				points.get(r),
				calcWeight(points, points.get(r)),
				null);
		
		// obtain k-1 new leaves.
		for(int i = 1; i < k; i++){
			// choose a leaf
			CoreSetTreeNode<T> leaf;
			do{
				leaf = chooseLeaf(root);
			}while(leaf.getPointSet().size() <= 1); // should not happen except in some extremely rare cases
													// but if it happened we just run chooseLeaf again
			// choose a new representative
			T newRepresentative = chooseRepresentative(leaf);
			
			// split leaf
			splitLeaf(leaf,newRepresentative);
		}
	}
	
	protected void splitLeaf(CoreSetTreeNode<T> leaf, T newRep){
		T repLeft = leaf.getRepresentative();
		T repRight = newRep;
		List<T> left = new ArrayList<>();
		List<T> right = new ArrayList<>();
		double weightLeft = 0;
		double weightRight = 0;
		for(T p : leaf.getPointSet()){
			double dLeft = p.distance(repLeft);
			double dRight = p.distance(repRight);
			if(dLeft < dRight){
				left.add(p);
				weightLeft += dLeft * p.getWeight();
			} else {
				right.add(p);
				weightRight += dRight * p.getWeight();
			}
		}
		CoreSetTreeNode<T> l = new CoreSetTreeNode<>(left, repLeft, weightLeft, leaf);
		CoreSetTreeNode<T> r = new CoreSetTreeNode<>(right, repRight, weightRight, leaf);
		leaf.split(l, r);
	}
	
	protected T chooseRepresentative(CoreSetTreeNode<T> leaf){
		double[] probs = new double[leaf.getPointSet().size()];
		double t = 0;
		for(int i = 0; i < probs.length; i++){
			probs[i] = leaf.getRepresentative().distance(leaf.getPointSet().get(i));
			t += probs[i];
		}
		for(int i = 0; i < leaf.getPointSet().size(); i++){
			probs[i] /= t;
		}
		int j = RandomUtils.getItemByProb(probs, rnd.nextDouble());
		return leaf.getPointSet().get(j);
	}
	
	double probs[] = new double[2];
	protected CoreSetTreeNode<T> chooseLeaf(CoreSetTreeNode<T> node){
		if(node.isLeaf()){
			return node;
		}
		double t = node.getLeftChild().getWeight() + node.getRightChild().getWeight();
		probs[0] = node.getLeftChild().getWeight() / t;
		probs[1] = node.getRightChild().getWeight() / t;
		int s = RandomUtils.getItemByProb(probs, rnd.nextDouble());
		return chooseLeaf(s == 0 ? node.getLeftChild() : node.getRightChild());
	}

	@Override
	public List<Cluster<T>> getClusters() {
		List<Cluster<T>> l = new ArrayList<>();
		root.forEach((e)->{
			if(e.isLeaf()){
				Cluster<T> c = new Cluster<>(-1);
				c.points = e.getPointSet();
				c.centroid = e.getRepresentative();
				l.add(c);
			}
		});
		for(int i = 0; i < l.size(); i++){
			l.get(i).id = i;
		}
		return l;
	}
}
