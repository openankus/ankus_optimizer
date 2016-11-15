package org.ankus.optimizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.ankus.optimizer.exceoption.OptimizerException;
import org.ankus.optimizer.util.Constants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;



/**
 * MapReduce 기반 유전자 알고리즘
 *
 * @description	MapReduce 기반 유전자 알고리즘
 * @version 0.1
 * @date  2016. 10. 13.
 * @author JungHo Kim
 *
 */
public class GA {
	
	
	
	/**
	 * 멀티스레드 알고리즘 평가 여부
	 */
	private boolean multiThreadEval = true;

	private int 		popSize;			// 개체크기
	private Random 		random;				// 난수 발생 객체
	private float		crossProb;			// 교배확률
	private float		mutProb;			// 돌연변이 확률
	private Chrom[]	pop;				// 개체집단
	
	private int 		numAttri;			// 입력데이터 속성 개수
	private int			numAlgPara;			// 최적화시킬 알고리즘 환경변수 개수
	private int 		binaryStrSize;		// 하나의 실수 값을 표현할 이진코드 개수
	private int[]		mask;				// 10진수를 계산하기 위한 마스크
	
	
	
	public boolean isMultiThreadEval() {
		return multiThreadEval;
	}

	public void setMultiThreadEval(boolean multiThreadEval) {
		this.multiThreadEval = multiThreadEval;
	}
	
	public int getPopSize() {
		return popSize;
	}

	public float getCrossProb() {
		return crossProb;
	}

	public float getMutProb() {
		return mutProb;
	}

	public Chrom[] getPop() {
		return pop;
	}

	public int getNumAttri() {
		return numAttri;
	}

	public int getNumAlgPara() {
		return numAlgPara;
	}

	public int getBinaryStrSize() {
		return binaryStrSize;
	}

	public int[] getMask() {
		return mask;
	}
	

	/**
	 * 유전자알고리즘 환경변수 설정 함수
	 * @param popSize	개체크기
	 * @param maxGen	최대 진화횟수
	 * @param seed		랜덤씨드
	 * @param crossProb	교배 확률
	 * @param mutProb	돌연변이 확률
	 */
	public void setParameters(int popSize, int seed, float crossProb, float mutProb) {
		
		this.popSize 	= popSize;
		this.crossProb = crossProb;
		this.mutProb 	= mutProb;
		this.random 	= new Random(seed);
		
	}

	/**
	 * 초기 개체집단 생성 함수
	 * 2진수를 10진수로 계산하기 위한 마스크 설정
	 * (조건: popSize와 random이 설정되어 있어야 함)
	 * @param numAttri		입력 속성 개수
	 * @param numAlgPara	알고리즘 환경변수 개수
	 * @param binaryStrSize	이진문자열 크기 (모든 변수가 동일한 크기를 가짐) => 알고리즘 환경변수 별로 할당되는 이진문자열 길이
	 * @param min			알고리즘 환경변수로 설정될 수 있는 최소값
	 * @param max			알고리즘 환경변수로 설정될 수 있는 최대값
	 * @param classindex	클래스 인덱스
	 */
	public void setInitialPopulation(int numAttri, int numAlgPara, int binaryStrSize, int classIndex) {
		
		this.numAttri = numAttri;
		this.numAlgPara = numAlgPara;
		this.binaryStrSize = binaryStrSize;

		this.pop = new Chrom [this.popSize];
		
		for(int i=0 ; i<this.popSize ; i++){
			this.pop[i] = new Chrom(this.random, numAttri+(numAlgPara*binaryStrSize), classIndex);
//			OptimizerLogger.println(i+" 개체: "+ this._pop[i].toStringGene());
		}
		
		this.mask = new int [this.binaryStrSize];
		for(int i=0 ; i<this.binaryStrSize ; i++ ){
			this.mask[i] = (int)Math.pow(2.0, (double)i);
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
		float[] accumulation = new float [this.pop.length];
		Chrom[] newPop = new Chrom [this.pop.length];
		
		for(int i=0 ; i<this.pop.length ; i++){
			sumFitness += this.pop[i].getFitness();
			if(maxFitness < this.pop[i].getFitness()){
				maxFitness = this.pop[i].getFitness();
				maxChromIndex = i;
			}
		}
		
		for(int i=0 ; i<this.pop.length ; i++){
			if((i-1)<0){
				accumulation[i] = this.pop[i].getFitness()/sumFitness;
			}else{
				accumulation[i] = accumulation[i-1]+(this.pop[i].getFitness()/sumFitness);
			}
		}
		
		newPop[0] = new Chrom(this.pop[maxChromIndex]);
		
		double randomVal;
		for(int i=1 ; i<this.pop.length ; i++){
			randomVal = this.random.nextDouble();
			for(int j=0 ; j<this.pop.length ; j++){
				if(randomVal <= accumulation[j]){
					newPop[i] = new Chrom(this.pop[j]);
					break;
				}
			}
		}
		
		this.pop = null;
		this.pop = new Chrom [this.popSize];
		for(int i=0 ; i<this.popSize ; i++){
			this.pop[i] = new Chrom(newPop[i]);
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
		
		for(int i=1 ; i<this.popSize-1 ; i++){
			if(this.random.nextDouble() < this.crossProb){
				crossoverYesNO.put(i, 0);
				countYes++;
			}else{
				crossoverYesNO.put(i, 1);
			}
		}
		
		if((countYes%2)!=0){
			crossoverYesNO.put(this.popSize-1, 1);
		}else{
			crossoverYesNO.put(this.popSize-1, 0);
		}
		
		Chrom[] newPop = new Chrom [this.popSize];
		int numParent = 1;
		int P1=-1, P2=-1;
		
		for(int i=0 ; i<this.popSize ; i++){
			newPop[i] = new Chrom(this.pop[i]);
			if(crossoverYesNO.get(i) == 1){
				if(numParent == 2){
					P2 = i;
					multiCrossover(this.pop[P1],this.pop[P2],newPop[P1],newPop[P2]);
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
	private void multiCrossover(Chrom p1, Chrom p2, Chrom c1, Chrom c2) {
		
		int   numGene = this.numAttri+this.numAlgPara;
		int[] mask = new int [numGene];
		
		for(int i=0 ; i<numGene ; i++){
			mask[i] = this.random.nextInt(2);
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
		
		for(int i=1 ; i<this.popSize ; i++){
			for(int j=0 ; j<this.pop[i].getGeneSize() ; j++){
				if(j != classIndex){
					if(this.random.nextDouble() < this.mutProb){
						if(this.pop[i].getGene(j) == 0){
							this.pop[i].setGene(j,1);
						}else{
							this.pop[i].setGene(j,0);
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
	public Chrom getelitist() {
		
		int max = -1;
		float maxVal = Float.MIN_NORMAL;
		
		for(int i=0 ; i<this.popSize ; i++){
			if(maxVal < this.pop[i].getFitness()){
				max = i;
				maxVal = this.pop[i].getFitness();
			}
		}
		
		
		return this.pop[max];
	}

//	/**
//	 * 개체 평가 함수
//	 * @param model		예측모델을 생성할 모델 객체
//	 * @param algorithm	예측모델을 생성할 알고리즘 객체
//	 * @param parameters	예측모델을 생성할 알고리즘의 환경변수 설정 값
//	 * @param outputBase	평가결과 출력파일들을 담는 디렉터리 경로
//	 * @return 생성된 개체모델 정보들을 담는 파일의 경로 
//	 */
//	public String evaluation(Model model, Algorithm algorithm, Parameter[] parameters, String outputBase) throws OptimizerException{
//
//		float[] val;
//		int index;
//		int usedAttriCount;
//		int algParaCount;
//		Instances tempData;
//		
//		//	생성된 모델들의 정보를 담는 파일(개체 인덱스 오름차순)
//		String popInfoAllPath = outputBase+"/"+Constants.FILE_NAME_POP_INFO_LIST;
//		
//		for(int i=0 ; i<this.popSize ; i++){
//			
//			tempData = new Instances(model.getInstances());
//			val = new float [this.numAlgPara];
//			
//			// 속성선택 유무를 표현하는 유전인자 인코딩	
//			index = 0; 
//			usedAttriCount = 0;
////			for(int j=0 ; j<tempData.numAttributes() ; index++){
////				if(this.pop[i].getGene(index)==0){
////					tempData.deleteAttributeAt(j);
////				}else{
////					j++;
////					usedAttriCount++;
////				}
////			}
//			for (int idx = tempData.getSizeOfAttributeIndexList() ; idx >=0 ; idx--){
//				if (this.pop[i].getGene(idx) == 0){
//					tempData.deleteAttributeIndexAt(idx);
//				}else{
//					usedAttriCount++;
//				}
//			}
//			
//			if(usedAttriCount == 1)
//			{
////				OptimizerLogger.println(i+ " 개체에서  모델 생성에 사용되는 속성 개수: "+usedAttriCount);
////				System.exit(1);
//				this.pop[i].setFitness(0.0f);
//				this.pop[i].setModel(null);				
//			}else{
//			
//				// 알고리즘 환경변수를 표현하는 유전인자 인코딩
//				algParaCount = 0;
//				int numGene = this.numAttri+(this.numAlgPara * this.binaryStrSize);
//				for(int j=this.numAttri ; j < numGene  ; j=j+this.binaryStrSize){
//					index = 0;
//					for(int k = j ; index<this.binaryStrSize ; index++, k++){
//						val[algParaCount] += this.pop[i].getGene(k)*this.mask[index];
//					}
//					algParaCount++;
//				}
//
//
//				for(int j=0 ; j<parameters.length ; j++){
//					if (parameters[j] instanceof ParameterInt){
//						ParameterInt param = (ParameterInt)parameters[j];
//						param.decoding((int)val[j], this.binaryStrSize);
//					}else if (parameters[j] instanceof ParameterFloat){
//						ParameterFloat param = (ParameterFloat)parameters[j];
//						param.decoding(val[j], this.binaryStrSize);
//					}
//				}
//			
//				//모델 생성 및 평가
//				String popOutputBase = outputBase+"/pop_"+String.format("%05d", i);
//				model.methodData(algorithm,tempData,parameters, popOutputBase);
//			
//				this.pop[i].setFitness((float)model.getAccuracy());
//				this.pop[i].setModel(model);
//
//				
//				// 생성된 모델 및 평가 출력 (개체 단위 출력)
//				try{
//					//	출력 스트림 열기
//					Path outputPath = new Path(popInfoAllPath);
//					FileSystem fs = FileSystem.get(new Configuration());
//					FSDataOutputStream out = null;
//					if (fs.exists(outputPath)){
//						out = fs.append(outputPath);
//					}else{
//						out = fs.create(outputPath);
//					}
//					
//					//	개체표현(염색체 기반 표시)
//					out.write(this.pop[i].toStringGene().getBytes());
//					out.write(Constants.DELIMITER_GA.getBytes());
//					//	적합도 표시
//					out.write(String.format("%.10f", this.pop[i].getFitness()).getBytes());
//					out.write(Constants.DELIMITER_GA.getBytes());
//					//	환경변수 설정값
//					for (int idx=0; idx < parameters.length ; idx++){
//						StringBuffer sb = new StringBuffer();
//						sb.append("param["+ parameters[idx].getName() +"]");
//						sb.append("=");
//						sb.append(parameters[idx].getValueString());
//						if (idx < parameters.length - 1)
//							sb.append(Constants.DELIMITER_GA_PARAM);
//						out.write(sb.toString().getBytes());
//					}
//					out.write(Constants.DELIMITER_GA.getBytes());
//					//	선택된 입력속성
//					{
//						StringBuffer sb = new StringBuffer();
//						sb.append("attributes=");
//						int sizeAttr = tempData.getSizeOfAttributeIndexList();
//						for (int idx=0; idx < sizeAttr ; idx++){
//							sb.append(tempData.getAttributeIndexAt(idx));
//							if (idx < sizeAttr-1)
//								sb.append(Constants.DELIMITER_GA_VALUE);
//						}
//						out.write(sb.toString().getBytes());
//					}
//					out.write("\n".getBytes());
//					out.close();
//					
//				}catch(IOException ex){
//					throw new OptimizerException("생성된 모델 및 평가 출력 중 에러 발생...", ex);
//				}
//				
//
//			}
//			
//		}		
//		
//		return popInfoAllPath;
//		
//	}
	
	

	/**
	 * 개체 평가 함수
	 * @param model		예측모델을 생성할 모델 객체
	 * @param algorithm	예측모델을 생성할 알고리즘 객체
	 * @param parameters	예측모델을 생성할 알고리즘의 환경변수 설정 값
	 * @param outputBase	평가결과 출력파일들을 담는 디렉터리 경로
	 * @return 생성된 개체모델 정보들을 담는 파일의 경로 
	 */
	public String evaluation(Model model, AlgorithmProcessExec algorithm, Parameter[] parameters, String outputBase) throws OptimizerException{
		
		float[] val;
		int index;
		int usedAttriCount;
		int algParaCount;
		Instances tempData;
		Instances tempTrainData;
		Instances tempTestData;
		
		//	생성된 모델들의 정보를 담는 파일의 경로(개체 인덱스 오름차순)
		String popInfoAllPath = outputBase+"/"+Constants.FILE_NAME_POP_INFO_LIST;
		
		// 알고리즘 모델(개체) 평가항목 목록 
		List<AlgorithmModelEvalThread> evalItemList = new ArrayList<AlgorithmModelEvalThread>();
		
		
		for(int i=0 ; i<this.getPopSize() ; i++){
			
			tempData = new Instances(model.getInstances());
			tempTrainData = new Instances(model.getTrainData());
			tempTestData = new Instances(model.getTestData());
			val = new float [this.numAlgPara];
			
			// 속성선택 유무를 표현하는 유전인자 인코딩	
			index = 0; 
			usedAttriCount = 0;
			for (int idx = tempData.getSizeOfAttributeIndexList()-1 ; idx >=0 ; idx--){
				if (this.pop[i].getGene(idx) == 0){
					tempData.deleteAttributeIndexAt(idx);
					tempTrainData.deleteAttributeIndexAt(idx);
					tempTestData.deleteAttributeIndexAt(idx);
				}else{
					usedAttriCount++;
				}
			}
			
			if(usedAttriCount == 1)
			{
				this.pop[i].setFitness(0.0f);
				this.pop[i].setModel(null);				
			}else{
			
				// 알고리즘 환경변수를 표현하는 유전인자 인코딩
				algParaCount = 0;
				int numGene = this.getNumAttri()+(this.numAlgPara * this.binaryStrSize);
				for(int j=this.numAttri ; j < numGene  ; j=j+this.binaryStrSize){
					index = 0;
					for(int k = j ; index<this.binaryStrSize ; index++, k++){
						val[algParaCount] += this.pop[i].getGene(k)*this.mask[index];
					}
					algParaCount++;
				}


				for(int j=0 ; j<parameters.length ; j++){
					if (parameters[j] instanceof ParameterInt){
						ParameterInt param = (ParameterInt)parameters[j];
						param.decoding((int)val[j], this.binaryStrSize);
					}else if (parameters[j] instanceof ParameterFloat){
						ParameterFloat param = (ParameterFloat)parameters[j];
						param.decoding(val[j], this.binaryStrSize);
					}
				}
				
				
				//	개체평가 출력결과 디렉토리
				String popOutputBase = outputBase+"/pop_"+String.format("%05d", i);
				
//				//	개체 출력
//				OptimizerLogger.printf("pop[%d] parameters before call: ",i);
//				for (Parameter param : parameters){
//					OptimizerLogger.printf("param[%s]=%s, ", param.getName(), param.getValueString());
//				}
//				OptimizerLogger.println("");
			
				
				//	알고리즘 평가항목 추가
				AlgorithmModelEvalThread algorithmEvalThread = 
						new AlgorithmModelEvalThread(
								popOutputBase
								, model
								, algorithm
								, tempData
								, tempTrainData
								, tempTestData
								, parameters
								, this
								, i
								, popInfoAllPath);
				evalItemList.add(algorithmEvalThread);

			}
		}		
		
		//	알고리즘 평가항목 목록 수행
		run(evalItemList);
		
		
		return popInfoAllPath;
		
	}

	
	
	
	/**
	 * 알고리즘 모델 평가 스레드 목록 실행
	 * 
	 * @param evalThreadList 실행할 알고리즘 모델 평가 스레드 목록
	 * @throws OptimizerException
	 */
	private void run(List<AlgorithmModelEvalThread> evalThreadList) throws OptimizerException{

		
		OptimizerLogger.println("==== Request for evaluating algorithm models!!!");
		
		
		
		//	쓰레드 실행 완료 체크주기(초)
		int checkIntervalSec = 2;
		
		List<AlgorithmModelEvalThread> runningThreadList = 
				new ArrayList<AlgorithmModelEvalThread>(evalThreadList);
		
		boolean isMultiThread = isMultiThreadEval();
		String mode = "single";
		if (isMultiThread) mode = "multi";
		OptimizerLogger.println("Thread execution mode : "+mode);
		if (isMultiThread){
			//	멀티 쓰레드로 수행하는 경우,
			
			//	알고리즘 평가 스레드 목록 실행
			for (AlgorithmModelEvalThread runningEvalThread : runningThreadList){
				runningEvalThread.start();
			}
			
			
			//	알고리즘 평가 스레드 실행 상태 확인
			while (runningThreadList.size() > 0){
				
				//	평가완료한 스레드목록 추출
				for (int idx=runningThreadList.size()-1; idx >= 0; idx--){
					AlgorithmModelEvalThread thread = runningThreadList.get(idx);
					if (AlgorithmModelEvalThread.STATE_COMPLETED.equals(thread.getEvalState())){
						OptimizerLogger.printf("The algorithm model[popIndex=%d] has been evaluated....\n", thread.getPopIndex());
						runningThreadList.remove(idx);
					}else if (AlgorithmModelEvalThread.STATE_FAIL.equals(thread.getEvalState())){
						throw new OptimizerException("Fail to evaluate an algorithm model!!!", thread.getEvalFailException());
					}
				}
				
				//	부하방지를 위한 main thread의 sleep 수행
				try{
					Thread.sleep(checkIntervalSec * 1000);
				}catch(InterruptedException ex){
					throw new OptimizerException("Fail to evaluate an algorithm model", ex);
				}
			}
			
			OptimizerLogger.println("==== 알고리즘 모델 평가 스레드 실행 완료!!!");
			
			
		}else{
			//	단일 쓰레드로 수행하는 경우,
			
			for (AlgorithmModelEvalThread runningEvalThread : runningThreadList){
				
				//	알고리즘 평가 스레드 실행
				runningEvalThread.start();
				
				boolean isCompleted = false;
				while (!isCompleted){
					
					if (AlgorithmModelEvalThread.STATE_COMPLETED.equals(runningEvalThread.getEvalState())){
						OptimizerLogger.printf("The algorithm model[popIndex=%d] has been evaluated....\n", runningEvalThread.getPopIndex());
						isCompleted = true;
					}else if (AlgorithmModelEvalThread.STATE_FAIL.equals(runningEvalThread.getEvalState())){
						throw new OptimizerException("Fail to evaluate an algorithm model!!!", runningEvalThread.getEvalFailException());
					}
					
					//	부하방지를 위한 main thread의 sleep 수행
					try{
						Thread.sleep(checkIntervalSec * 1000);
					}catch(InterruptedException ex){
						throw new OptimizerException("Fail to evaluate an algorithm model", ex);
					}
//					OptimizerLogger.println("====> check state : "+runningEvalThread.getEvalState() );
				}
			}
			
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 현재 개체집단을 구성하고 있는 개체들을 출력하는 함수
	 * @return 객체가 표현하고 있는 이진 스트링
	 */
	public String toStringChroms() {
		
		String str = "";
		
		for(int i=0 ; i<this.popSize ; i++){
			str += i+" : "+this.pop[i].toStringGene()+" - "+this.pop[i].getFitness()  +"\n";
		}
		return str;
	}
	
	
	/**
	 * 개체정보들을 적합도 내림차순으로 정렬
	 * 
	 * @param popInfoListPath 개체정보들을 담은 목록 파일
	 * @param outputPath	출력파일경로
	 */
	public void writeSortedPopInfoList(String popInfoListPath, String outputPath) throws OptimizerException{

		//	전체 요약정보 파일 생성
		try{
			
			//	생성된 모델들의 정보를 담는 파일	
			ArrayList<String> popInfoList = new ArrayList<String>();
			FileSystem fs = FileSystem.get(new Configuration());
			FSDataInputStream in = fs.open(new Path(popInfoListPath));
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);
			String rl= null;
			while ((rl = br.readLine()) != null){
				popInfoList.add(rl);
			}
			br.close();
			isr.close();
			in.close();
			
			//	생성된 모델 정보 정렬(적합도 내림차순)
			Collections.sort(popInfoList, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					String[] splits1 = o1.split(Constants.DELIMITER_GA);
					String[] splits2 = o2.split(Constants.DELIMITER_GA);
					float fitness1 = Float.parseFloat(splits1[1]);
					float fitness2 = Float.parseFloat(splits2[1]);
					
					int retVal = 0;
					if (fitness1 > fitness2){
						retVal = -1;
					}else if (fitness1 == fitness2){
						retVal = 0;
					}else{
						retVal = 1;
					}
					return retVal;
				}
			});
			
			// 정렬된 정보 출력
			FSDataOutputStream out = fs.create(new Path(outputPath));
			//	전체 목록 출력
			for (String popInfo : popInfoList){
				out.write(popInfo.getBytes());
				out.write("\n".getBytes());
			}
			out.close();
			
		}catch(IOException ex){
			throw new OptimizerException("Fail to write an algorithm model and evaluation result...", ex);
		}
		
	}
	

	


}
