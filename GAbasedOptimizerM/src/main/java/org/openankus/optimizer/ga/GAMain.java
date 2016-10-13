package org.openankus.optimizer.ga;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;

import org.openankus.optimizer.ml.Algorithm;
import org.openankus.optimizer.ml.DecisionTreeC45;
import org.openankus.optimizer.ml.KNN;
import org.openankus.optimizer.ml.MLP;
import org.openankus.optimizer.ml.Model;
import org.openankus.optimizer.ml.Parameter;
import org.openankus.optimizer.ml.RForest;

import weka.core.Instances;

public class GAMain {
	
	private static CGA _ga	= null;
	private static Parameter[] _parameters = null;

	public static void main(String[] args) throws Exception {
		
		//GA 환경변수 설정
		//cmd 예: -s 1 -p 10 -mG 3 -cp 0.9 -mp 0.5 -bs 5 -in D:/Programs/data/iris.arff
		int 	seed = 10;
		int 	popSize = 300;
		int		maxGeneration = 500;
		float 	crossProb = 0.9f;
		float	mutProb = 0.5f;
		int		binaryStrSize = 5;	// 이진문자열 크기
		String  inputFile = "D:/Programs/data/nursery_shuffle.arff";
		
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
		_ga = new CGA();
		_ga.setParameters(popSize,seed,crossProb,mutProb);
		
		// 초기 개체집단 생성
		int 	numAttri;	
		int		classIndex		= 8;		// 입력데이터에 대한 클래스 속성 설정
		
		// weka 입력데이터 불러옴 (arff 파일)
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		Instances data = new Instances(reader);
		numAttri = data.numAttributes();
		System.out.println("속성 개수: "+numAttri);
		System.out.println("데이터 개수: "+data.numInstances());

		data.setClassIndex(classIndex);
		
		Model model = new Model(data);
		Algorithm 	algorithm = null;
		int			numAlgPara = -1;
	
		switch("RandomForest"){
		case "C45":
			System.out.println("C45");
			algorithm = new DecisionTreeC45();
			numAlgPara = 2;
			_parameters = new Parameter[numAlgPara];
			_parameters[0] = new Parameter("CF",0.1f,0.5f);
			_parameters[1] = new Parameter("min",2.0f,80.0f);
			break;
		case "MLP":
			System.out.println("MLP");			
			algorithm = new MLP();
			numAlgPara = 3;
			_parameters = new Parameter[numAlgPara];
			_parameters[0] = new Parameter("lr",0.1f,1.0f);
			_parameters[1] = new Parameter("mm",0.1f,1.0f);
			_parameters[2] = new Parameter("h",1.0f,50.0f);
			break;
		case "KNN":
			System.out.println("KNN");	
			algorithm = new KNN();
			numAlgPara = 1;
			_parameters = new Parameter[numAlgPara];
			_parameters[0] = new Parameter("k",1.0f,50.f);
			break;
		case "RandomForest":
			System.out.println("RandomForest");	
			algorithm = new RForest();
			numAlgPara = 2;
			_parameters = new Parameter[numAlgPara];
			_parameters[0] = new Parameter("MD",0.0f,50.0f);
			_parameters[1] = new Parameter("numDT",1.0f,100.0f);
			
		};
		
		if(numAlgPara != -1){
			
			Date startTime = new Date();
			
			// 개체초기화
			_ga.setInitialPopulation(numAttri,numAlgPara,binaryStrSize,classIndex);
			//System.out.println("개체 초기화 완료...");
			
			// 개체평가	
			_ga.evaluation(model,algorithm,_parameters);
			//System.out.println(_ga.toStringChroms());
			//System.out.println("----- elitist()");
			//System.out.println(_ga.getelitist().toStringGene()+" - "+_ga.getelitist().getFitness());
			//System.exit(1);

			int generation = 0;
			System.out.println(" ************************************ "+generation+" 세대: "+_ga.getelitist().getFitness());
			//System.out.println(_ga.getelitist().toStringGene());
			//System.exit(1);
			
			do{
				// 개체선택
				_ga.selectMethod();
				//System.out.println("개체 선택 완료..");
			
				// 교배 확률
				_ga.crossover();
				//System.out.println("개체 교배 완료..");
			
				// 돌연변이 확률
				_ga.mutation(classIndex);
				//System.out.println("개체 돌연변이 완료..");
				
				// 개체평가
				//System.out.println(_ga.toStringChroms());
				_ga.evaluation(model,algorithm,_parameters);
				//System.out.println("개체평가 완료..");

				generation++;
				
				System.out.println(" ************************************ "+generation+" 세대: "+_ga.getelitist().getFitness());

				if(generation == maxGeneration){
					System.out.println(_ga.getelitist().toStringGene());
					_ga.getelitist().toStringModel();
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
