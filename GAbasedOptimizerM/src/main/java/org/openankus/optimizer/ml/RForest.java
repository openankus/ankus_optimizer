package org.openankus.optimizer.ml;

import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;

public class RForest implements Algorithm{
	public RandomForest _forest = null; 	// ���� ������Ʈ �ӻ�� ��ü ����
	public double _accuracy = -1; 			// ���� ��Ȯ��
	
	@Override
	/**
	 * RandomForest(�ӻ�� ��) �� ���� �� �� �Լ�
	 */
	public void method(Instances data, Parameter[] parameters) {
		try {	
			this._forest = new RandomForest();
			
			for(int i=0 ; i<parameters.length ; i++){
				switch(parameters[i].getName()){
				case "MD":
					this._forest.setMaxDepth((int)parameters[i].getValue());
					break;
				case "numDT":
					this._forest.setNumIterations((int)parameters[i].getValue());
					break;
				};
			}
			
			this._forest.buildClassifier(data);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("�н� �� ����......");
		}
		
		System.out.println(this._forest.toString());
		
		int correct = 0;
		int classIndex = data.classIndex();
		double realOutput;
		double targetOutput;
		for(int i=0 ; i<data.numInstances() ; i++){
			try {
				realOutput = this._forest.classifyInstance(data.get(i));
				targetOutput = data.get(i).value(classIndex);
				if(realOutput == targetOutput){
					correct++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		this._accuracy = (double)correct / (double)data.numInstances();
	}
	
	/**
	 * ������ RandomForest ���� ��Ȯ�� ��ȯ �Լ�
	 */
	public double getAccuracy(){
		return this._accuracy;
	}
	
	/**
	 * ������ RandomForest �𵨷� �Էµ������� ���ָ� �з��� ��� �� ��ȯ �Լ�
	 * (��ȯ ���� Ŭ������ �ε�����)
	 */
	public double classifyInstance(Instance inst){
		
		double retVal = -1.0f;
		
		try {
			retVal= this._forest.classifyInstance(inst);
		} catch (Exception e) {
			System.out.println("���� 00002");
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	/**
	 * ������ RandomForest �� ���� ��� �Լ�
	 */
	public void toModelSummary(){
		System.out.println(this._forest.toString());
	}
}
