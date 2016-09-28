package org.openankus.optimizer.rf;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openankus.optimizer.ml.DecisionTreeC45;

import weka.core.Instance;
import weka.core.Instances;

public class RadomForest {
	private ArrayList<DecisionTreeC45> trees;
	  
	private RadomForest() {
		trees = new ArrayList<>();
	}
	
	public RadomForest(ArrayList<RadomForest> trees) {

	}
	
	public  ArrayList<DecisionTreeC45> getTrees() {
		    return trees;
	 }
	
	public double classify(Instance total, Random rng, Instance Input) {
		double[] predictions = null;
		
		return predictions[rng.nextInt()];
	}
	
	public void saveForest(String path){
		
	}
	
	public void loadRForest(String path){
		
	}
	
	public void getNumOfNodes(){
		
	}
	
	public void printRForest(){
		
	}
	
	public void RFBuilder(int treeId){
		
	}
	//
	public DecisionTreeC45[] training(Instances[] data){
		DecisionTreeC45[] trees = null;
		return trees;
	}
	
	int maxDepth;
	
	public void setMaxDepth(int md) {
	    this.maxDepth = md;
	  }
	int maxTrees;
	public void setMaxTrees(int mt) {
		this.maxTrees = mt;
	}
	
	

	
	public void splitDataWithBagging(Instances data, Random rng){
		Bagging bag = new Bagging();
		Instances[] bagList = bag.bagging(data, rng);
	}
	public void split(){
		
	}
	private double getVoting(double[] resultSet){
		
		return resultSet[0];
	}
}
