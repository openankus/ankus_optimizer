package org.openankus.optimizer.ga;
import java.util.Hashtable;
import java.util.Random;

import org.openankus.optimizer.ml.Algorithm;
import org.openankus.optimizer.ml.Model;
import org.openankus.optimizer.ml.Parameter;

import weka.core.Instances;

public class CGA {

	private int 		_popSize;			// 개체크기
	private Random 		_random;			// 난수 발생 객체
	private float		_crossProb;			// 교배확률
	private float		_mutProb;			// 돌연변이 확률
	private CChrom[]	_pop;				// 개체집단
	
	private int 		_numAttri;			// 입력데이터 속성 개수
	private int			_numAlgPara;		// 최적화시킬 알고리즘 환경변수 개수
	private int 		_binaryStrSize;		// 하나의 실수 값을 표현할 이진코드 개수
	private int[]		_mask;				// 10진수를 계산하기 위한 마스크
	
	/**
	 * 유전자알고리즘 환경변수 설정 함수
	 * @param popSize	개체크기
	 * @param maxGen	최대 진화횟수
	 * @param seed		랜덤씨드
	 * @param crossProb	교배 확률
	 * @param mutProb	돌연변이 확률
	 */
	public void setParameters(int popSize, int seed, float crossProb, float mutProb) {
		
		this._popSize 	= popSize;
		this._crossProb = crossProb;
		this._mutProb 	= mutProb;
		this._random 	= new Random(seed);
		
	}

	/**
	 * 초기 개체집단 생성 함수
	 * 2진수를 10진수로 계산하기 위한 마스크 설정
	 * (조건: 전역변수 _popSize와 _random이 설정되어 있어야 함)
	 * @param numAttri		입력 속성 개수
	 * @param numAlgPara	알고리즘 환경변수 개수
	 * @param binaryStrSize	이진문자열 크기 (모든 변수가 동일한 크기를 가짐)
	 * @param min			알고리즘 환경변수로 설정될 수 있는 최소값
	 * @param max			알고리즘 환경변수로 설정될 수 있는 최대값
	 * @param classindex	클래스 인덱스
	 */
	public void setInitialPopulation(int numAttri, int numAlgPara, int binaryStrSize, int classIndex) {
		
		this._numAttri = numAttri;
		this._numAlgPara = numAlgPara;
		this._binaryStrSize = binaryStrSize;

		this._pop = new CChrom [this._popSize];
		
		for(int i=0 ; i<this._popSize ; i++){
			this._pop[i] = new CChrom(this._random, numAttri+(numAlgPara*binaryStrSize), classIndex);
//			System.out.println(i+" 개체: "+ this._pop[i].toStringGene());
		}
		
		this._mask = new int [this._binaryStrSize];
		for(int i=0 ; i<this._binaryStrSize ; i++ ){
			this._mask[i] = (int)Math.pow(2.0, (double)i);
		}
	}


	/**
	 * 개체선택 함수
	 * 엘리티스트 방법 + 룰렛휠 방법
	 */
	public void selectMethod() {
		
		int maxChromIndex = -1;
		float maxFitness = Float.MIN_VALUE;
		float sumFitness = 0.0f;
		float[] accumulation = new float [this._pop.length];
		CChrom[] newPop = new CChrom [this._pop.length];
		
		for(int i=0 ; i<this._pop.length ; i++){
			sumFitness += this._pop[i].getFitness();
			if(maxFitness < this._pop[i].getFitness()){
				maxFitness = this._pop[i].getFitness();
				maxChromIndex = i;
			}
		}
		
		for(int i=0 ; i<this._pop.length ; i++){
			if((i-1)<0){
				accumulation[i] = this._pop[i].getFitness()/sumFitness;
			}else{
				accumulation[i] = accumulation[i-1]+(this._pop[i].getFitness()/sumFitness);
			}
		}
		
		newPop[0] = new CChrom(this._pop[maxChromIndex]);
		
		double randomVal;
		for(int i=1 ; i<this._pop.length ; i++){
			randomVal = this._random.nextDouble();
			for(int j=0 ; j<this._pop.length ; j++){
				if(randomVal <= accumulation[j]){
					newPop[i] = new CChrom(this._pop[j]);
					break;
				}
			}
		}
		
		this._pop = null;
		this._pop = new CChrom [this._popSize];
		for(int i=0 ; i<this._popSize ; i++){
			this._pop[i] = new CChrom(newPop[i]);
		}
	}

	/**
	 * 교배연산 함수
	 * 교배연산을 적용시킬 부모개체 선택
	 */
	public void crossover() {
		
		Hashtable<Integer,Integer> crossoverYesNO = new Hashtable<Integer,Integer>();
		int countYes=0;
		
		crossoverYesNO.put(0, 0);
		
		for(int i=1 ; i<this._popSize-1 ; i++){
			if(this._random.nextDouble() < this._crossProb){
				crossoverYesNO.put(i, 0);
				countYes++;
			}else{
				crossoverYesNO.put(i, 1);
			}
		}
		
		if((countYes%2)!=0){
			crossoverYesNO.put(this._popSize-1, 1);
		}else{
			crossoverYesNO.put(this._popSize-1, 0);
		}
		
		CChrom[] newPop = new CChrom [this._popSize];
		int numParent = 1;
		int P1=-1, P2=-1;
		
		for(int i=0 ; i<this._popSize ; i++){
			newPop[i] = new CChrom(this._pop[i]);
			if(crossoverYesNO.get(i) == 1){
				if(numParent == 2){
					P2 = i;
					multiCrossover(this._pop[P1],this._pop[P2],newPop[P1],newPop[P2]);
					numParent = 1;
				}else{
					P1 = i;
					numParent++;
				}				
			}
		}
	}
	

	/**
	 * 다중 교점 교배여산자
	 * @param p1	부모개체 1
	 * @param p2	부모개체 2
	 * @param c1	자식개체 1
	 * @param c2	자식개체 2
	 */
	private void multiCrossover(CChrom p1, CChrom p2, CChrom c1, CChrom c2) {
		
		int   numGene = this._numAttri+this._numAlgPara;
		int[] mask = new int [numGene];
		
		for(int i=0 ; i<numGene ; i++){
			mask[i] = this._random.nextInt(2);
		}
		
		for(int i=0 ; i<numGene ; i++){
			if(mask[i] == 1){
				c1.setGene(i, p2.getGene(i));
				c2.setGene(i, p1.getGene(i));
			}else{
				c1.setGene(i, p1.getGene(i));
				c2.setGene(i, p2.getGene(i));				
			}
		}
	}

	/**
	 * 돌연변이 연산: 균등 돌연변이 연산
	 * 각 유전인자에 난수를 발생한 후 돌연변이 확률보다 작을 경우 해당 유전인자 값을 변환시켜 줌
	 * 클래스 인덱스를 나타내는 유전인자에 대해서는 돌연변이 연산을 적용시키지 않음
	 * @param classIndex 클래스 인덱스
	 */
	public void mutation(int classIndex) {
		
		for(int i=1 ; i<this._popSize ; i++){
			for(int j=0 ; j<this._pop[i].getGeneSize() ; j++){
				if(j != classIndex){
					if(this._random.nextDouble() < this._mutProb){
						if(this._pop[i].getGene(j) == 0){
							this._pop[i].setGene(j,1);
						}else{
							this._pop[i].setGene(j,0);
						}
					}
				}
			}
		}
		
	}

	/**
	 * 개체집단에서 가장 적합도가 높은 개체 반환 함수
	 * @return 개체
	 */
	public CChrom getelitist() {
		
		int max = -1;
		float maxVal = Float.MIN_NORMAL;
		
		for(int i=0 ; i<this._popSize ; i++){
			if(maxVal < this._pop[i].getFitness()){
				max = i;
				maxVal = this._pop[i].getFitness();
			}
		}
		
		
		return this._pop[max];
	}

	/**
	 * 개체 평가 함수
	 * @param model		예측모델을 생성할 모델 객체
	 * @param algorithm	예측모델을 생성할 알고리즘 객체
	 * @param parameters	예측모델을 생성할 알고리즘의 환경변수 설정 값
	 */
	public void evaluation(Model model, Algorithm algorithm, Parameter[] parameters) {

		float[] val;
		int index;
		int usedAttriCount;
		int algParaCount;
		Instances tempData;
		
		for(int i=0 ; i<this._popSize ; i++){
			
			tempData = new Instances(model.getInstance());
			val = new float [this._numAlgPara];
			
			// 속성선택 유무를 표현하는 유전인자 인코딩	
			index = 0; 
			usedAttriCount = 0;
			for(int j=0 ; j<tempData.numAttributes() ; index++){
				if(this._pop[i].getGene(index)==0){
					tempData.deleteAttributeAt(j);
				}else{
					j++;
					usedAttriCount++;
				}
			}
			
			if(usedAttriCount == 1)
			{
//				System.out.println(i+ " 개체에서  모델 생성에 사용되는 속성 개수: "+usedAttriCount);
//				System.exit(1);
				this._pop[i].setFitness(0.0f);
				this._pop[i].setModel(null);				
			}else{
			
				// 알고리즘 환경변수를 표현하는 유전인자 인코딩
				algParaCount = 0;
				int numGene = this._numAttri+(this._numAlgPara * this._binaryStrSize);
				for(int j=this._numAttri ; j < numGene  ; j=j+this._binaryStrSize){
					index = 0;
					for(int k = j ; index<this._binaryStrSize ; index++, k++){
						val[algParaCount] += this._pop[i].getGene(k)*this._mask[index];
					}
					algParaCount++;
				}


				for(int j=0 ; j<parameters.length ; j++){
					parameters[j].decoding(val[j], this._binaryStrSize);
				}
			
				//모델 생성 및 평가
				model.methodwData(algorithm,tempData,parameters);
			
				this._pop[i].setFitness((float)model.getAccuracy());
				this._pop[i].setModel(model);
			}
			
		}		
		
	}

	/**
	 * 현재 개체집단을 구성하고 있는 개체들을 출력하는 함수
	 * @return 객체가 표현하고 있는 이진 스트링
	 */
	public String toStringChroms() {
		
		String str = "";
		
		for(int i=0 ; i<this._popSize ; i++){
			str += i+" : "+this._pop[i].toStringGene()+" - "+this._pop[i].getFitness()  +"\n";
		}
		return str;
	}

}
