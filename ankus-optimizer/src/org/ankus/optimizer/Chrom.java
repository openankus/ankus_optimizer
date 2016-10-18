package org.ankus.optimizer;

import java.util.Random;


/**
 * MapReduce 기반 유전자 알고리즘 개체(염색체)
 *
 * @description 유전자 알고리즘의 개체(염색체)
 * @version 0.1
 * @date  2016. 10. 13.
 * @author JungHo Kim
 *
 */
public class Chrom {

	private int[]	chromosome;		// 개체: 이진코드로 표현
	private float	fitness;		// 적합도
	private Model	model;			// 개체에 의해 생성된 모델
	
	/**
	 * 생성자
	 * @param random	  난수발생 객체
	 * @param numGene	  유전인자 개수
	 * @param classIndex 클래스 인덱스
	 */
	public Chrom(Random random, int numGene, int classIndex) {
		
		this.chromosome = new int[numGene];
		
		for(int i=0 ; i<numGene ; i++){
			if(i == classIndex){
				this.chromosome[i] = 1;
			}else{
				this.chromosome[i] = random.nextInt(2);
			}
		}
	}

	/**
	 * 생성자: 객체를 복사할 때 활용
	 * @param chrom		복사할 개체
	 * @param fitness	복사할 객체 
	 */
	public Chrom(Chrom original) {
		
		this.chromosome = new int[original.getGeneSize()];
		
		for(int i=0 ; i<original.getGeneSize() ; i++){
			this.chromosome[i] = original.getGene(i);
		}
		
		this.fitness = original.getFitness();
	}

	/**
	 * 개체를 구성하고 유전인자 개수 반환 함수
	 * 오류 시 오류코드 00001 발생
	 * @return 유전인자 개수
	 */
	public int getGeneSize() {
		
		int reVal = 0;
		
		if(this.chromosome == null){
			System.out.println("오류코드 00001");
			System.exit(1);
		}else{
			reVal = this.chromosome.length;
		}
		
		return reVal;
	}

	/**
	 * 개체의 특정 유전인자 값 반환 함수
	 * @param index		유전인자 인덱스 (0부터)
	 * @return			해당 유전인자 값
	 */
	public int getGene(int index) {
		return this.chromosome[index];
	}
	
	/**
	 * 적합도 설정 함수
	 * @param fitness	적합도
	 */
	public void setFitness(float fitness) {
		this.fitness = fitness;	
	}
	
	/**
	 * 적합도 반환 함수
	 * @return	적합도
	 */
	public float getFitness(){
		return this.fitness;
	}

	/**
	 * 유전인자 값 설정 함수
	 * @param index	유전인자 인덱스
	 * @param value	유전인자 설정 값
	 */
	public void setGene(int index, int value) {
		this.chromosome[index] = value;
	}

	/**
	 * 유전인자 출력 함수
	 * @return	유전인자
	 */
	public String toStringGene() {
		
		String str = "";
		
		for(int i=0 ; i<this.chromosome.length ; i++){
			str += this.chromosome[i]+" / ";
		}
		
		return str;
	}

	/**
	 * 생성된 모델 정보를 출력하는 함수
	 */
	public void toStringModel() {
		this.model.toSummaryString();
	}

	/**
	 * 모델 객체를 복사하는 함수
	 * @param model	복사될 모델 객체
	 */
	public void setModel(Model model) {
		if(model == null){
			this.model = null;
		}else{
			this.model = model.copy();
		}
	}

}
