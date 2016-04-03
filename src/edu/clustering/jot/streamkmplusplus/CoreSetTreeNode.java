package edu.clustering.jot.streamkmplusplus;

import java.util.List;
import java.util.function.Consumer;

import edu.clustering.jot.interfaces.Point;

public class CoreSetTreeNode<T extends Point> {
	List<T> pointSet;
	T representative;
	double weight;
	int nPoints;
	CoreSetTreeNode<T> childL, childR;
	CoreSetTreeNode<T> parent;
	
	public CoreSetTreeNode(
			List<T> pointSet, 
			T representative,
			CoreSetTreeNode<T> parent){
		this.pointSet = pointSet;
		this.representative = representative;
		this.parent = parent;
		this.childL = null;
		this.childR = null;
		this.weight = calcWeight(pointSet, representative);
		this.nPoints = pointSet.size();
	}

	protected double calcWeightRecursive(){
		if(pointSet == null){
			return childL.calcWeightRecursive() + childR.calcWeightRecursive();
		}
		return calcWeight(pointSet, representative);
	}
	protected double calcWeight(List<T> s, T r){
		double w = 0;
		for (T p : s){
			w += r.distance(p) * p.getWeight();
		}
		return w;
	}
	
	public void split(CoreSetTreeNode<T> l, CoreSetTreeNode<T> r){
		this.childL = l;
		this.childR = r;
		this.pointSet = null;
;		updateWeight((l.weight + r.weight) - weight);
	}
	
	public void updateWeight(double delta){
		weight += delta;
		if(parent != null){
			parent.updateWeight(delta);
		}
	}
	
	public boolean isLeaf(){
		return childL == null && childR == null;
	}
	
	public void setLeftChild(CoreSetTreeNode<T> child){
		childL = child;
	}
	public void setRightChild(CoreSetTreeNode<T> child){
		childR = child;
	}
	
	public CoreSetTreeNode<T> getLeftChild(){
		return childL;
	}
	public CoreSetTreeNode<T> getRightChild(){
		return childR;
	}
	
	public double getWeight(){
		return weight;
	}
	
	public T getRepresentative(){
		return representative;
	}
	
	public List<T> getPointSet(){
		return pointSet;
	}
	
	public void forEach(Consumer<CoreSetTreeNode<T>> consumer){
		consumer.accept(this);
		if (childL != null){
			childL.forEach(consumer);
		}
		if (childR != null){
			childR.forEach(consumer);
		}
	}
}
