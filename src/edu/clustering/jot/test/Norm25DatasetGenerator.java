package edu.clustering.jot.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.clustering.jot.datapoint.DenseEucledianPoint;

public class Norm25DatasetGenerator {
	public static final double hypercubeSize = 500;
	
	public static List<DenseEucledianPoint> getPoints(int k, int d, int n, double variance){
		Random rnd = new Random(System.currentTimeMillis());
		
		// get centers
		List<DenseEucledianPoint> centers = new ArrayList<>(); 
		for(int i = 0; i < k; i++){
			DenseEucledianPoint c = new DenseEucledianPoint(d);
			for(int j = 0; j < d; j++){
				c.set(j, rnd.nextDouble() * hypercubeSize);
			}
			centers.add(c);
		}
		
		// get points
		List<DenseEucledianPoint> points = new ArrayList<>(); 
		for(DenseEucledianPoint c : centers){
			for(int i = 0; i < n; i++){
				DenseEucledianPoint p = new DenseEucledianPoint(d);
				for(int j = 0; j < d; j++){
					p.set(j, rnd.nextGaussian() * variance + c.get(j));
				}
				points.add(p);
			}
		}
		Collections.shuffle(points, rnd);
		return points;
	}
}
