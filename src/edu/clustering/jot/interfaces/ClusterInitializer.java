package edu.clustering.jot.interfaces;

import java.util.List;

import edu.clustering.jot.kmeans.Cluster;

public interface ClusterInitializer<T extends Point> {
    List<Cluster<T>> initializeClusters(int k, List<T> points);
}
