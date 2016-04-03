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

	Random rnd = RandomUtils.getRandom();
	
	// this is the final size of the returned coreset. Note in (1) it is denoted by m and
	// k has a different meaning
	int k;

	CoreSetTreeNode<T> root;
	
	public void reset(){
		root = null;
		k = 0;
	}

	@Override
	public void doClustering(int k, int minClustersToMaintain, List<T> points) {
		this.k = k;
		
		// first representative is selected at random
		T rep = chooseRepresentative(points, null);
		root = new CoreSetTreeNode<>(
				new ArrayList<T>(points), 
				rep,
				null);
		
		// obtain k-1 new leaves.
		for(int i = 1; i < k; i++){
			// if the weight is 0 (up to numerical errors) then there is no point
			// to split again
			if (root.weight < 0.0000001){
				break;
			}

			// choose a leaf
			CoreSetTreeNode<T> leaf;
			int j = 0;
			do{
				leaf = chooseLeaf(root);
				j++;
			}while(leaf.getPointSet().size() < 2 && j < 10);
			
			if(j >= 10){
				// we cannot find a leaf to split
				return;
			}
			// choose a new representative
			T newRepresentative = chooseRepresentative(leaf.getPointSet(), leaf.getRepresentative());
			
			// split leaf
			splitLeaf(leaf,newRepresentative);
		}
	}
	
	protected void splitLeaf(CoreSetTreeNode<T> leaf, T newRep){
		T repLeft = leaf.getRepresentative();
		T repRight = newRep;
		List<T> left = new ArrayList<>();
		List<T> right = new ArrayList<>();
		for(T p : leaf.getPointSet()){
			double dLeft = p.distance(repLeft);
			double dRight = p.distance(repRight);
			if(dLeft < dRight){
				left.add(p);
			} else {
				right.add(p);
			}
		}
		CoreSetTreeNode<T> l = new CoreSetTreeNode<>(left, repLeft, leaf);
		CoreSetTreeNode<T> r = new CoreSetTreeNode<>(right, repRight, leaf);
		leaf.split(l, r);
	}
	
	protected T chooseRepresentative(List<T> points, T rep){
		double[] probs = new double[points.size()];
		double t = 0;
		for(int i = 0; i < probs.length; i++){
			probs[i] = (rep != null ? rep.distance(points.get(i)) : 1.0) * points.get(i).getWeight();
			t += probs[i];
		}
		for(int i = 0; i < points.size(); i++){
			
			probs[i] = t > 0 ? probs[i] / t : 1.0 / points.size();
		}
		int j = RandomUtils.getItemByProb(probs, rnd.nextDouble());
		return points.get(j);
	}
	
	double probs[] = new double[2];
	protected CoreSetTreeNode<T> chooseLeaf(CoreSetTreeNode<T> node){
		if(node.isLeaf()){
			return node;
		}
		double t = node.getLeftChild().getWeight() + node.getRightChild().getWeight();
		
		probs[0] = t > 0 ? node.getLeftChild().getWeight() / t : 0.5;
		probs[1] = t > 0 ? node.getRightChild().getWeight() / t : 0.5;
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
				c.centroid.setWeight(e.getPointSet().stream().mapToDouble((p)->p.getWeight()).sum());
				l.add(c);
			}
		});
		for(int i = 0; i < l.size(); i++){
			l.get(i).id = i;
		}
		return l;
	}

	String name;
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
}
