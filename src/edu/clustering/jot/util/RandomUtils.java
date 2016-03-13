package edu.clustering.jot.util;

public class RandomUtils {
	public static int getItemByProb(double probs[], double rnd){
		for(int i = 0; i < probs.length; i++){
			if (rnd <= probs[i]){
				return i;
			}
			rnd -= probs[i];
		}
		
		// we should never get here. If we did then either rnd > 1.0 or
		// probs does not sum to 1
		throw new RuntimeException("Something went wrong");
	}
}
