import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
		
		ga.setParameters(popSize,seed,crossProb,mutProb);
		
		// 초기 개체집단 생성
		int 	numAttri		= 5;	// 입력속성 개수
		int		numAlgPara		= 2;	// 알고리즘 환경변수 개수
		int		binaryStrSize	= 5;	// 이진문자열 크기
		float[]	min				= {0.1f,2.0f};	// 알고리즘의 환경변수로 설정될 수 있는 최소값
		float[]	max				= {1.0f,75.0f};	// 알고리즘의 환경변수로 설정될 수 있는 최대값
		int		classIndex		= 4;		// 입력데이터에 대한 클래스 속성 설정
		
		
		ga.setInitialPopulation(numAttri,numAlgPara,binaryStrSize,min,max,classIndex);
//		System.out.println("개체 초기화 완료...");
		
		// 개체평가
		// 모델을 생성 시킬 알고리즘 객체 생성 (다층퍼셉트론 생성)
		//weka.classifiers.functions.MultilayerPerceptron mlp = new weka.classifiers.functions.MultilayerPerceptron();
		weka.classifiers.trees.J48 decisionTree = new weka.classifiers.trees.J48();
		
		// weka 입력데이터 불러옴 (arff 파일)
		BufferedReader reader = new BufferedReader(new FileReader("D:/Programs/data/iris.arff"));
		Instances data = new Instances(reader);
		data.setClassIndex(classIndex);
		
		ga.setFitnessVal(decisionTree, data);
//		System.out.println("개체 평가 완료..");
		
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
			ga.setFitnessVal(decisionTree, data);
			
			generation++;
			
			System.out.println(" ************************************ "+generation+" 세대: "+ga.getelitist().getFitness());
			ga.getelitist().toStringModel();
	
		}while(generation <= maxGeneration);
		
		
	}

}
