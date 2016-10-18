package org.ankus.optimizer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import org.ankus.optimizer.util.ArgumentsConstants;
import org.ankus.optimizer.util.Constants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * 최적화 알고리즘 실행 메인
 *
 * @description
 * @version 0.1
 * @date  2016. 10. 17.
 * @author JungHo Kim
 *
 */
public class GAMain{

	
	public static void main(String[] args) throws Exception {

		GA ga	= null;
		Parameter[] parameters = null;
		
		//GA 환경변수 설정
		//cmd 예: -s 1 -p 10 -mG 3 -cp 0.9 -mp 0.5 -bs 5 -in D:/Programs/data/iris.arff
		int 	seed = 10;
//		int 	popSize = 300;
//		int		maxGeneration = 500;
		int 	popSize = 1;
		int		maxGeneration = 0;
		float 	crossProb = 0.9f;
		float	mutProb = 0.5f;
//		int		binaryStrSize = 5;	// 이진문자열 크기
		int		binaryStrSize = 3;	// 이진문자열 크기
//		String  inputFile = "D:/Programs/data/nursery_shuffle.arff";
		String  inputFile = "/data/iris.csv";
		
		String parname = "";
		for(String arg:args){
			if(parname.isEmpty() && arg.startsWith("-")){
				parname =arg;
			}else{
				if(parname.equals("-s")){
					seed = Integer.parseInt(arg);			// 렌덤씨드
					System.out.println("랜덤씨드: "+seed);
				}else if(parname.equals("-p")){
					popSize = Integer.parseInt(arg); 		// 개체크기
					System.out.println("개체크기: "+popSize);
				}else if(parname.equals("-mG")){
					maxGeneration = Integer.parseInt(arg); 	// 최대 세대수
					System.out.println("최대 세대수: "+maxGeneration);
				}else if(parname.equals("-cp")){
					crossProb = Float.parseFloat(arg);		// 교배확률
					System.out.println("교배확률: "+crossProb);
				}else if(parname.equals("-mp")){
					mutProb = Float.parseFloat(arg);		// 돌연변이 확률
					System.out.println("돌연변이 확률: "+mutProb);
				}else if(parname.equals("-bs")){
					binaryStrSize = Integer.parseInt(arg);	// 이진문자열 크기
					System.out.println("이진문자열 크기: "+binaryStrSize);
				}else if(parname.equals("-in")){
					inputFile = arg;						// 입력데이터 파일
					System.out.println("입력데이터: "+inputFile);
				}
				parname = "";
			}
		}
		
		
		// GA 객체 생성
		ga = new GA();
		ga.setParameters(popSize,seed,crossProb,mutProb);
		
//		// 초기 개체집단 생성
//		int 	numAttri;	
//		int		classIndex		= 8;		// 입력데이터에 대한 클래스 속성 설정
//		
//		// weka 입력데이터 불러옴 (arff 파일)
//		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
//		Instances data = new Instances(reader);
//		numAttri = data.numAttributes();
//		System.out.println("속성 개수: "+numAttri);
//		System.out.println("데이터 개수: "+data.numInstances());
//
//		data.setClassIndex(classIndex);
		
		String delimiter = ",";
    	int numAttri = 0;
    	int classIndex = 4;

		FileSystem fs = FileSystem.get(new Configuration());
        FSDataInputStream fin = fs.open(new Path(inputFile));
        BufferedReader br = new BufferedReader(new InputStreamReader(fin, Constants.UTF8));
        String rl = br.readLine();
    	StringTokenizer st = new StringTokenizer(rl, delimiter);
    	while (st.hasMoreTokens()){
    		st.nextToken();
    		numAttri++;
    	}
    	br.close();
    	fin.close();
    	System.out.println("속성 개수:"+numAttri);

    	
    	ArrayList<Integer> attributeIndexList = new ArrayList<Integer>();
    	for (int idx=0; idx<numAttri; idx++){
    		if (idx != classIndex)
    			attributeIndexList.add(idx);
    	}
    	Instances data = new Instances(inputFile, delimiter, attributeIndexList, classIndex);
    	
	
		Model model = new Model(data);
		Algorithm 	algorithm = null;
		int			numAlgPara = -1;

		System.out.println("> step 5");
		
		switch("KNN"){
		case "C45":
			System.out.println("C45");
			algorithm = 
					new AlgorithmProcessExec(
							"/home/hadoop/hadoop2/bin/hadoop jar /mnt/hgfs/shared_for_vm/ankus-core-1.1.0.jar C45");
			numAlgPara = 2;
			parameters = new Parameter[numAlgPara];
//			parameters[0] = new Parameter("CF",0.1f,0.5f);
//			parameters[1] = new Parameter("min",2.0f,80.0f);
			parameters[0] = 
					new Parameter(ArgumentsConstants.PURITY,0.1f,0.5f);
			parameters[1] = 
					new Parameter(ArgumentsConstants.MIN_LEAF_DATA,2.0f,80.0f);
			break;
		case "MLP":
			System.out.println("MLP");			
			algorithm = 
					new AlgorithmProcessExec(
							"/home/hadoop/hadoop2/bin/hadoop jar /mnt/hgfs/shared_for_vm/ankus-core-1.1.0.jar MultilayerPerceptron");
			numAlgPara = 3;
			parameters = new Parameter[numAlgPara];
//			parameters[0] = new Parameter("lr",0.1f,1.0f);
//			parameters[1] = new Parameter("mm",0.1f,1.0f);
//			parameters[2] = new Parameter("h",1.0f,50.0f);
			parameters[0] = 
					new Parameter(ArgumentsConstants.LEARNING_RATE,0.1f,1.0f);
			parameters[1] = 
					new Parameter(ArgumentsConstants.MOMENTUN,0.1f,1.0f);
			parameters[2] = 
					new Parameter(ArgumentsConstants.HIDDEN_NODE_NUM,1.0f,50.0f);
			break;
		case "KNN":
			System.out.println("KNN");	
			algorithm = 
					new AlgorithmProcessExec(
							"/home/hadoop/hadoop2/bin/hadoop jar /mnt/hgfs/shared_for_vm/ankus-core-1.1.0.jar kNN");
			numAlgPara = 1;
			parameters = new Parameter[numAlgPara];
//			parameters[0] = new Parameter("k",1.0f,50.f);
			parameters[0] = 
					new Parameter(ArgumentsConstants.K_CNT,1.0f,50.f);
			break;
		case "RandomForest":
			System.out.println("RandomForest");	
//			algorithm = new RForest();
			numAlgPara = 2;
			parameters = new Parameter[numAlgPara];
			parameters[0] = new Parameter("MD",0.0f,50.0f);
			parameters[1] = new Parameter("numDT",1.0f,100.0f);
			
		};

		
		if(numAlgPara != -1){
			
			int generation = 0;
			
			Date startTime = new Date();
			
			// 개체초기화
			ga.setInitialPopulation(numAttri,numAlgPara,binaryStrSize,classIndex);
			//System.out.println("개체 초기화 완료...");
			
			// 개체평가
			String outputBase = "/tmp_evaluation/gen_"+String.format("%05d", generation);
			ga.evaluation(model,algorithm,parameters, outputBase);
			//System.out.println(_ga.toStringChroms());
			//System.out.println("----- elitist()");
			//System.out.println(_ga.getelitist().toStringGene()+" - "+_ga.getelitist().getFitness());
			//System.exit(1);

			System.out.println(" ************************************ "+generation+" 세대: "+ga.getelitist().getFitness());
			//System.out.println(_ga.getelitist().toStringGene());
			//System.exit(1);
			
			do{
				
				generation++;

				// 개체선택
				ga.selectMethod();
				//System.out.println("개체 선택 완료..");
			
				// 교배 확률
				ga.crossover();
				//System.out.println("개체 교배 완료..");
			
				// 돌연변이 확률
				ga.mutation(classIndex);
				//System.out.println("개체 돌연변이 완료..");

				
				// 개체평가
				//System.out.println(_ga.toStringChroms());
				outputBase = "/tmp_evaluation/gen_"+String.format("%05d", generation);
				ga.evaluation(model,algorithm,parameters, outputBase);
				//System.out.println("개체평가 완료..");

				
				System.out.println(" ************************************ "+generation+" 세대: "+ga.getelitist().getFitness());

				if(generation == maxGeneration){
					System.out.println(ga.getelitist().toStringGene());
					ga.getelitist().toStringModel();
				}
	
			}while(generation <= maxGeneration);
			
			Date endTime = new Date();
			
			long lTime = (long) ((endTime.getTime() - startTime.getTime())/(60.0*1000.0));
			System.out.println("Start Time: "+ startTime.toString());
			System.out.println("End Time: "+endTime.toString());
			System.out.println("TIME : " + lTime + "분");
			
		}else{
			System.out.println("오류 00001");
		}
		
	}
	
	
}
