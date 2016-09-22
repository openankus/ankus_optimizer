import java.util.Random;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.trees.J48;

public class CChrom {

	private int[]	_chromosome;
	private float	_fitness;
	private J48 	_model;
	
	/**
	 * 생성자
	 * @param random	  난수발생 객체
	 * @param numGene	  유전인자 개수
	 * @param classIndex 클래스 인덱스
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
	 * 생성자: 객체를 복사할 때 활용
	 * @param chrom		복사할 개체
	 * @param fitness	복사할 객체 
	 */
	public CChrom(CChrom original) {
		
		this._chromosome = new int[original.getGeneSize()];
		
		for(int i=0 ; i<original.getGeneSize() ; i++){
			this._chromosome[i] = original.getGene(i);
		}
		
		this._fitness = original.getFitness();
	}

	/**
	 * 개체를 구성하고 유전인자 개수 반환 함수
	 * 오류 시 오류코드 00001 발생
	 * @return 유전인자 개수
	 */
	public int getGeneSize() {
		
		int reVal = 0;
		
		if(this._chromosome == null){
			System.out.println("오류코드 00001");
			System.exit(1);
		}else{
			reVal = this._chromosome.length;
		}
		
		return reVal;
	}

	/**
	 * 개체의 특정 유전인자 값 반환 함수
	 * @param index		유전인자 인덱스 (0부터)
	 * @return			해당 유전인자 값
	 */
	public int getGene(int index) {
		return this._chromosome[index];
	}
	
	/**
	 * 적합도 설정 함수
	 * @param fitness	적합도
	 */
	public void setFitness(float fitness) {
		this._fitness = fitness;	
	}
	
	/**
	 * 적합도 반환 함수
	 * @return	적합도
	 */
	public float getFitness(){
		return this._fitness;
	}

	/**
	 * 유전인자 값 설정 함수
	 * @param index	유전인자 인덱스
	 * @param value	유전인자 설정 값
	 */
	public void setGene(int index, int value) {
		this._chromosome[index] = value;
	}

	/**
	 * 유전인자 출력 함수
	 * @return	유전인자
	 */
	public String toStringGene() {
		
		String str = "";
		
		for(int i=0 ; i<this._chromosome.length ; i++){
			str += this._chromosome[i]+" / ";
		}
		
		return str;
	}

	/**
	 * 생성된 분류모델을 복사하는 함수
	 */
	public void setModel(J48 decisionTree) throws Exception {
		this._model = (J48)AbstractClassifier.makeCopy(decisionTree); 
	}

	public void toStringModel() {
		
		System.out.println(this._model.toSummaryString());
		System.out.println(this._model.toString());
	}


}
