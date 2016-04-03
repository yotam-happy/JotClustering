package edu.clustering.jot.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.clustering.jot.algorithms.AlgorithmConstructor;
import edu.clustering.jot.algorithms.StreamingAlgorithmConstructor;
import edu.clustering.jot.datapoint.DenseEucledianPoint;
import edu.clustering.jot.interfaces.ClusteringAlgorithm;
import edu.clustering.jot.interfaces.StreamingClusteringAlgorithm;
import edu.clustering.jot.kmeans.Cluster;

public class TestMain {

	public static void testAllAlgorithms(List<DenseEucledianPoint> points, 
			List<Integer> kArr, 
			List<ClusteringAlgorithm<DenseEucledianPoint>> nonStreaming,
			List<StreamingClusteringAlgorithm<DenseEucledianPoint>> streaming){
		if (nonStreaming != null){
			nonStreaming.forEach((algo)->{
				kArr.forEach((k)->{
					log("\n\ndoing algo: " + algo.getName() + " k=" + k + "\n\n");
					for(int i = 0; i < 5; i++){
						algo.reset();
						doNonStreamingClustering(algo, k, points);
					}
				});
			});
		}
		if (streaming != null){
			streaming.forEach((algo)->{
				log("\n\ndoing algo: " + algo.getName() + "\n\n");
				for(int i = 0; i < 5; i++){
					algo.reset();
					doStreamingClustering(algo, points);
				}
			});
		}
	}
	
	public static List<StreamingClusteringAlgorithm<DenseEucledianPoint>> getStreamingAlgoForK(
			List<Integer> ks){
		List<StreamingClusteringAlgorithm<DenseEucledianPoint>> streaming = new ArrayList<>();
		ks.forEach((k)->{
//			streaming.add(StreamingAlgorithmConstructor.getStreamKMPlusPlus(k, k * 200, 50, 0.000001));
//			streaming.add(StreamingAlgorithmConstructor.getStreamingKMeansSharp(k, (int)(Math.sqrt(311079 * k) / k), 10, 0.0000001));
//			streaming.add(StreamingAlgorithmConstructor.getRandomSample(k, k * 200, 50, 0.0000001));
			streaming.add(StreamingAlgorithmConstructor.getSimpleMergeAndReduce(k, 40, 
					AlgorithmConstructor.getKMeansPlusPlus(20, 0.0000001)));
		});
		return streaming;
	}
	public static void main(String[] args) throws IOException {
		List<ClusteringAlgorithm<DenseEucledianPoint>> nonStreaming = new ArrayList<>();
//		nonStreaming.add(AlgorithmConstructor.getKMeansSharp(50, 0.00000001, 50));
		nonStreaming.add(AlgorithmConstructor.getKMeans(50, 0.00000001));
		nonStreaming.add(AlgorithmConstructor.getKMeansPlusPlus(500, 0.00000001));

		
		List<DenseEucledianPoint> norm25 = norm25();
		List<Integer> norm25k = new ArrayList<>();
		norm25k.add(10);
		norm25k.add(25);
		norm25k.add(50);

		List<DenseEucledianPoint> intrusion = intrusion();
		List<DenseEucledianPoint> intrusionStd = intrusion();
		standardizePoints(intrusionStd);
		List<Integer> intrusionK = new ArrayList<>();
		intrusionK.add(10);
		intrusionK.add(20);
		intrusionK.add(30);
		intrusionK.add(50);

		List<DenseEucledianPoint> tower = tower();
		List<Integer> towerK = new ArrayList<>();
		towerK.add(20);
		towerK.add(40);
		towerK.add(60);

		log("\n\nDoing norm25!\n\n");
//		testAllAlgorithms(norm25, norm25k, /*nonStreaming*/ null, getStreamingAlgoForK(norm25k));
		log("\n\nDoing intrusion!\n\n");
		testAllAlgorithms(intrusion, intrusionK, /*nonStreaming*/ null, getStreamingAlgoForK(intrusionK));
//		log("\n\nDoing intrusion normalized!\n\n");
//		testAllAlgorithms(intrusionStd, intrusionK, /*nonStreaming*/ null, getStreamingAlgoForK(intrusionK));
//		log("\n\nDoing tower!\n\n");
//		testAllAlgorithms(tower, towerK, nonStreaming, getStreamingAlgoForK(towerK));
	}

	
	public static List<DenseEucledianPoint> norm25(){
		return Norm25DatasetGenerator.getPoints(25, 15, 400, 1.0);
	}
	
	public static List<DenseEucledianPoint> tower() throws IOException{
		FileInputStream fis = new FileInputStream("C:/EclipseWorkspace/JotClustering/data/tower.txt");
		 
		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	 
		List<DenseEucledianPoint> points = new ArrayList<>();
		String line = null;
		while ((line = br.readLine()) != null) {
			DenseEucledianPoint p = new DenseEucledianPoint(3);
			p.set(0, Double.parseDouble(line));
			p.set(1, Double.parseDouble(br.readLine()));
			p.set(2, Double.parseDouble(br.readLine()));
			points.add(p);
		}
		br.close();
		return points;
	}

	public static List<DenseEucledianPoint> intrusion() throws IOException{
		FileInputStream fis = new FileInputStream("C:/EclipseWorkspace/JotClustering/data/kddcup.newtestdata_10_percent_unlabeled");

		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	 
		List<DenseEucledianPoint> points = new ArrayList<>();
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] parts = line.split(",");
			DenseEucledianPoint p = new DenseEucledianPoint(parts.length);
			for(int i = 0; i < parts.length; i++){
				try{
					p.set(i, Double.parseDouble(parts[i]) * 10000);
				}catch(Exception e){
					// symbolic features, just ignore
					p.set(i, 0);
				}
			}
			points.add(p);
		}
		br.close();
		return points;
	}

	public static void doNonStreamingClustering(ClusteringAlgorithm<DenseEucledianPoint> algo, 
			int k, 
			List<DenseEucledianPoint> points){

		Date date = new Date();
		log("---------------------------------------------\n");
		log("starting clustering (" + date.toString() + ")\n");
		algo.doClustering(k, k, points);
		Date date2 = new Date();
		
		List<Cluster<DenseEucledianPoint>> clusters = algo.getClusters();
		double cost = clusters.stream().mapToDouble((c)->c.cost()).sum();
		double count = clusters.stream().mapToInt((c)->c.points.size()).sum();

		long seconds = (date2.getTime()- date.getTime())/1000;
		log("cost: " + cost + " count: " + count  + " time (seconds): " + seconds + "\n");

		System.out.println("Final solution cost is: " + cost); 
	}

	public static void log(String s){
		try {
			Writer output = new BufferedWriter(new FileWriter("clustering.out", true));
			output.write(s);
			output.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void rescalePoints(List<DenseEucledianPoint> points){
		double[] n = new double[points.get(0).dim()];
		points.forEach((p)->{
			for(int i = 0; i < points.get(0).dim(); i++){
				if (n[i] < p.get(i)){
					n[i] = p.get(i);
				}
			}
		});
		points.forEach((p)->{
			for(int i = 0; i < points.get(0).dim(); i++){
				if(n[i] > 0.00000001) {
					p.set(i, p.get(i) / n[i]);
				}
			}
		});
	}
	
	public static void standardizePoints(List<DenseEucledianPoint> points) {
		double[] mean = new double[points.get(0).dim()];
		double[] dev = new double[points.get(0).dim()];
		points.forEach((p)->{
			for(int i = 0; i < points.get(0).dim(); i++){
				mean[i] += p.get(i);
			}
		});
		for(int i = 0; i < points.get(0).dim(); i++){
			mean[i] /= points.size();
		}
		points.forEach((p)->{
			for(int i = 0; i < points.get(0).dim(); i++){
				dev[i] += Math.pow(p.get(i) - mean[i], 2);
			}
		});
		for(int i = 0; i < points.get(0).dim(); i++){
			dev[i] = Math.sqrt(dev[i] / points.size());
		}

		points.forEach((p)->{
			for(int i = 0; i < points.get(0).dim(); i++){
				if(dev[i] > 0.00000001) {
					p.set(i, (p.get(i) - mean[i])/dev[i]);
				}
			}
		});
	}
	
	public static void doStreamingClustering(
			StreamingClusteringAlgorithm<DenseEucledianPoint> streamAlgo,
			List<DenseEucledianPoint> points) {
		int[] count = new int[1];
		System.out.println("do clustering");
		count[0] = 0;


		Date date = new Date();
		log("---------------------------------------------\n");
		log("starting clustering (" + date.toString() + ")\n");

		points.forEach((p)->{
			streamAlgo.processPoint(p);

			if (count[0] % 10000 == 0){
				System.out.println("done " + count[0]);
			}
			count[0]++;
		});

		List<Cluster<DenseEucledianPoint>> clusters = streamAlgo.getEstimatedClusters();

		Date date2 = new Date();
		log("done clustering (" + date2.toString() + ") with: " +streamAlgo.getEstimatedClusters().size() + " clusters \n");
		
		System.out.println("calc results");
		count[0] = 0;
		
		double[] cost = new double[1];
		points.forEach((p)->{
			double bestD = Double.MAX_VALUE;
			for(Cluster<DenseEucledianPoint> cluster : clusters){
				double d = cluster.centroid.distance(p);
				if (d < bestD){
					bestD = d;
				}
			}
			cost[0] += bestD;

			if (count[0] % 10000 == 0){
				System.out.println("done " + count[0]);
			}
			count[0]++;
		});

		long seconds = (date2.getTime()- date.getTime())/1000;
		log("cost: " + cost[0] + " count: " + count[0] + " mean cost: " + (cost[0] / (double)count[0]) + " time (seconds): " + seconds + "\n");
		log("some stats: " + streamAlgo.someStats() + "\n");
		System.out.println("Final solution cost is: " + cost[0] / (double)count[0] + "\n");
	}
}
