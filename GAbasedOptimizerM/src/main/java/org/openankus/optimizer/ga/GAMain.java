package org.openankus.optimizer.ga;
import java.io.BufferedReader;
import java.io.FileReader;

import org.openankus.optimizer.ml.Algorithm;
import org.openankus.optimizer.ml.DecisionTreeC45;
import org.openankus.optimizer.ml.MLP;
import org.openankus.optimizer.ml.Model;
import org.openankus.optimizer.ml.Parameter;

import weka.core.Instances;

public class GAMain {

	public static void main(String[] args) throws Exception {
		
		// GA 객체 생성
		CGA ga = new CGA();
		
		// GA 환경변수 설정
		int 	seed 			= 1;
		int 	popSize			= 10;
		int		maxGeneration 	= 3;
		float 	crossProb 		= 0.9f;
		float	mutProb			= 0.5f;
		int		binaryStrSize	= 5;	// 이진문자열 크기
		
		ga.setParameters(popSize,seed,crossProb,mutProb);
		
		// 초기 개체집단 생성
		int 	numAttri		= 5;		// 입력속성 개수	
		int		classIndex		= 4;		// 입력데이터에 대한 클래스 속성 설정
		
		// weka 입력데이터 불러옴 (arff 파일)
		BufferedReader reader = new BufferedReader(new FileReader("D:/Programs/data/iris.arff"));
		Instances data = new Instances(reader);
		data.setClassIndex(classIndex);
		
		Model model = new Model(data);
		Algorithm 	algorithm = null;
		int			numAlgPara = -1;
		Parameter[] parameters = null;
		
		switch("MLP"){
		case "C45":
			algorithm = new DecisionTreeC45();
			numAlgPara = 2;
			parameters = new Parameter[numAlgPara];
			parameters[0] = new Parameter("CF",0.1f,1.0f);
			parameters[1] = new Parameter("min",2.0f,80.0f);
			break;
		case "MLP":
			algorithm = new MLP();
			numAlgPara = 3;
			parameters = new Parameter[numAlgPara];
			parameters[0] = new Parameter("lr",0.1f,1.0f);
			parameters[1] = new Parameter("mm",0.1f,1.0f);
			parameters[2] = new Parameter("h",1.0f,50.0f);
			break;
		};
		
		
		if(numAlgPara != -1){
			// 개체초기화
			ga.setInitialPopulation(numAttri,numAlgPara,binaryStrSize,classIndex);
	//		System.out.println("개체 초기화 완료...");
			
			// 개체평가	
			ga.evaluation(model,algorithm,parameters);
			
			int generation = 0;
			System.out.println(" ************************************ "+generation+" 세대: "+ga.getelitist().getFitness());
			ga.getelitist().toStringModel();
			
			do{
				// 개체선택
				ga.selectMethod();
	//			System.out.println("개체 선택 완료..");
			
				// 교배 확률
				ga.crossover();
	//			System.out.println("개체 교배 완료..");
			
				// 돌연변이 확률
				ga.mutation(classIndex);
	//			System.out.println("개체 돌연변이 완료..");
				
				// 개체평가	
				ga.evaluation(model,algorithm,parameters);			
				
				generation++;
				
				System.out.println(" ************************************ "+generation+" 세대: "+ga.getelitist().getFitness());
				ga.getelitist().toStringModel();
	
			}while(generation <= maxGeneration);
		}else{
			System.out.println("오류 00001");
		}
		
		
	}

}
