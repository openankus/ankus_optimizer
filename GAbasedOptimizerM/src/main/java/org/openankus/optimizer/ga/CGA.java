package org.openankus.optimizer.ga;
import java.util.Hashtable;
import java.util.Random;

import weka.classifiers.trees.J48;
import weka.core.Instances;

public class CGA {

	private int 		_popSize;
	private Random 		_random;
	private float		_crossProb;
	private float		_mutProb;
	private CChrom[]	_pop;
	
	private int 		_numAttri;
	private int			_numAlgPara;
	private int 		_binaryStrSize;
	private float[]		_minAlgPara;
	private float[]		_maxAlgPara;
	private int[]		_mask;
	
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
	public void setInitialPopulation(int numAttri, int numAlgPara, int binaryStrSize, float[] min, float[] max, int classIndex) {
		
		this._numAttri = numAttri;
		this._numAlgPara = numAlgPara;
		this._binaryStrSize = binaryStrSize;
		this._minAlgPara = min.clone();
		this._maxAlgPara = max.clone();
		
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
	 * 개체집단을 구성하고 있는 각 개체의 적합도 생성
	 * @param decisionTree	예측모델을 생성할 수 있는 알고리즘 객체
	 * @param data			예측모델을 생성하기 위한 입력데이터(학습데이터와 테스트데이터)
	 * @throws Exception 
	 */
	public void setFitnessVal(J48 decisionTree, Instances data) throws Exception {
	
		float[] val;
		int index;
		int algParaCount;
		Instances tempData;
		
		for(int i=0 ; i<this._popSize ; i++){
			
			tempData = new Instances(data);
			val = new float [this._numAlgPara];
			
			// 속성선택 유무를 표현하는 유전인자 인코딩	
			index = 0; 
			for(int j=0 ; j<tempData.numAttributes() ; index++){
				if(this._pop[i].getGene(index)==1){
					tempData.deleteAttributeAt(j);
				}else{
					j++;
				}
			}
			
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

			
			for(int j=0 ; j<this._numAlgPara ; j++){
				System.out.println("10진수: "+ j+" : "+val[j]);
				val[j] = ((val[j]*(float)(this._maxAlgPara[j]-this._minAlgPara[j]))/((float)Math.pow(2.0, (double)this._binaryStrSize)-1.0f))+(float)this._minAlgPara[j];
				System.out.println("정규화: "+ j+" : "+val[j]);
			}
			
			//모델 생성
			decisionTree.setConfidenceFactor(val[0]);
			decisionTree.setMinNumObj((int)val[1]);
			decisionTree.buildClassifier(tempData);
			
			System.out.println(decisionTree.toString());
			System.exit(1);
			
			//모델 평가
			double realOutput;
			int correctCount = 0; 
			for(int j=0 ; j<tempData.numInstances() ; j++){
				realOutput = decisionTree.classifyInstance(tempData.instance(j));
				if(tempData.instance(j).value(tempData.classIndex()) == realOutput){
					correctCount++;
				}
			}
			
			this._pop[i].setFitness((float)correctCount/(float)tempData.numInstances());
			this._pop[i].setModel(decisionTree);
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
			for(int j=0 ; j<this._pop.length ; j++){
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

}
