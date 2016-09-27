import java.util.Random;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.trees.J48;

public class CChrom {

	private int[]	_chromosome;
	private float	_fitness;
	private J48 	_model;
	
	/**
	 * ������
	 * @param random	  �����߻� ��ü
	 * @param numGene	  �������� ����
	 * @param classIndex Ŭ���� �ε���
	 */
	public CChrom(Random random, int numGene, int classIndex) {
		
		this._chromosome = new int[numGene];
		
		for(int i=0 ; i<numGene ; i++){
			if(i == classIndex){
				this._chromosome[i] = 0;
			}else{
				this._chromosome[i] = random.nextInt(2);
			}
		}
	}

	/**
	 * ������: ��ü�� ������ �� Ȱ��
	 * @param chrom		������ ��ü
	 * @param fitness	������ ��ü 
	 */
	public CChrom(CChrom original) {
		
		this._chromosome = new int[original.getGeneSize()];
		
		for(int i=0 ; i<original.getGeneSize() ; i++){
			this._chromosome[i] = original.getGene(i);
		}
		
		this._fitness = original.getFitness();
	}

	/**
	 * ��ü�� �����ϰ� �������� ���� ��ȯ �Լ�
	 * ���� �� �����ڵ� 00001 �߻�
	 * @return �������� ����
	 */
	public int getGeneSize() {
		
		int reVal = 0;
		
		if(this._chromosome == null){
			System.out.println("�����ڵ� 00001");
			System.exit(1);
		}else{
			reVal = this._chromosome.length;
		}
		
		return reVal;
	}

	/**
	 * ��ü�� Ư�� �������� �� ��ȯ �Լ�
	 * @param index		�������� �ε��� (0����)
	 * @return			�ش� �������� ��
	 */
	public int getGene(int index) {
		return this._chromosome[index];
	}
	
	/**
	 * ���յ� ���� �Լ�
	 * @param fitness	���յ�
	 */
	public void setFitness(float fitness) {
		this._fitness = fitness;	
	}
	
	/**
	 * ���յ� ��ȯ �Լ�
	 * @return	���յ�
	 */
	public float getFitness(){
		return this._fitness;
	}

	/**
	 * �������� �� ���� �Լ�
	 * @param index	�������� �ε���
	 * @param value	�������� ���� ��
	 */
	public void setGene(int index, int value) {
		this._chromosome[index] = value;
	}

	/**
	 * �������� ��� �Լ�
	 * @return	��������
	 */
	public String toStringGene() {
		
		String str = "";
		
		for(int i=0 ; i<this._chromosome.length ; i++){
			str += this._chromosome[i]+" / ";
		}
		
		return str;
	}

	/**
	 * ������ �з����� �����ϴ� �Լ�
	 */
	public void setModel(J48 decisionTree) throws Exception {
		this._model = (J48)AbstractClassifier.makeCopy(decisionTree); 
	}

	public void toStringModel() {
		
		System.out.println(this._model.toSummaryString());
		System.out.println(this._model.toString());
	}


}
