package org.openankus.optimizer.ga.chromset;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.openankus.optimizer.ml.Algorithm;
import org.openankus.optimizer.ml.DecisionTreeC45;
import org.openankus.optimizer.ml.KNN;
import org.openankus.optimizer.ml.MLP;
import org.openankus.optimizer.ml.Model;
import org.openankus.optimizer.ml.Parameter;
import org.openankus.optimizer.ml.RForest;

import weka.core.Instances;
import weka.core.matrix.ExponentialFormat;

/**
 * 개체집합 별 진화연산을 수행하는 Map
 *
 */
public class GaChromSetMap extends Mapper<LongWritable, Text, Text, Text>{
	
	//	MapReduce 서버별로 로컬캐시된 파일목록
	private Path[] cacheFiles = null;
	
	//	출력파일의 key를 쓰기위한 개체
	Text outputKey = new Text();
	
	//	출력파일의 값을 쓰기위한 객체
	Text outputValue = new Text();

	
	
	@Override
	protected void setup(Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		//	로컬 캐시된 파일 목록 로드
		cacheFiles = DistributedCache.getLocalCacheFiles(context.getConfiguration());
	}
	
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		

//		System.out.println("입력값:"+value.toString());
//		System.out.println("gaChromset.popSize:"+context.getConfiguration().get("gaChromset.popSize"));
		
		
		//----<1.Map 입력데이터 파싱>---------
		String[] parts = value.toString().split("\t");
		String chromSetId = parts[0];
		StringTokenizer tokenizer = new StringTokenizer(parts[1], Chrom.DELIMITER_CHROM);
		List<Chrom> chromList = new ArrayList<Chrom>();
		while (tokenizer.hasMoreTokens()){
			chromList.add(new Chrom(tokenizer.nextToken()));
		}
		//----</1.Map 입력데이터 파싱>---------
		
		
		
		
		//----<2.Map 입력데이터 처리>---------
		
		int 	seed = Integer.parseInt(context.getConfiguration().get("GaChromSetMap.seed"));
		int 	popSize = Integer.parseInt(context.getConfiguration().get("GaChromSetMap.popSize"));
		int		maxGeneration = Integer.parseInt(context.getConfiguration().get("GaChromSetMap.maxGeneration"));
		float 	crossProb = Float.parseFloat(context.getConfiguration().get("GaChromSetMap.crossProb"));
		float	mutProb = Float.parseFloat(context.getConfiguration().get("GaChromSetMap.mutProb"));
		int		binaryStrSize = Integer.parseInt(context.getConfiguration().get("GaChromSetMap.binaryStrSize"));	// 이진문자열 크기
		String  inputFile = cacheFiles[0].toString(); //
		

		
		// GA 객체 생성
		GA ga = new GA();
		ga.setParameters(popSize,seed,crossProb,mutProb);

		
		// 초기 개체집단 생성
		int numAttri = 
				Integer.parseInt(context.getConfiguration().get("GaChromSetMap.numAttri"));		// 입력속성 개수	
		int	classIndex = 
				Integer.parseInt(context.getConfiguration().get("GaChromSetMap.classIndex"));;		// 입력데이터에 대한 클래스 속성 설정
		
		// weka 입력데이터 불러옴 (arff 파일)
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		Instances data = new Instances(reader);
		data.setClassIndex(classIndex);
		
		Model model = new Model(data);
		Algorithm 	algorithm = null;
		int			numAlgPara = -1;
		
		Parameter[] _parameters = null;
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
			
			// 개체 설정
			ga.updatetChromList(numAttri,numAlgPara,binaryStrSize,classIndex, chromList.size(), chromList);
			
			// 개체평가	
			ga.evaluation(model,algorithm,_parameters);
			
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
				ga.evaluation(model,algorithm,_parameters);			
				
				generation++;
				
				System.out.println(" ************************************ "+generation+" 세대: "+ga.getelitist().getFitness());
				ga.getelitist().toStringModel();
	
			}while(generation <= maxGeneration);
		}else{
			System.out.println("오류 00001");
		}
		
		//----</2.Map 입력데이터 처리>---------
		
		
		
		
		
		
		
		
		//----<3.Map 출력데이터 처리>---------
		//	Key 설정 - 염색체집합 ID
		outputKey.set(chromSetId);

		//	Value 설정 - 개체목록
		//	개체표현 (염색체정보-적합도) 예> 0.75-0,1,0,0,1
		//	개체별 구분자 ^ 
		//	개체목록 표현 예> 0.75-0,1,0,0,1^0.333-1,1,0,0,1
		String popInString = ga.getPopInString();
		outputValue.set(popInString);
		
//		System.out.println("ID:"+chromSetId+" / "+popInString);
		
		//	출력데이터 생성
		context.write(outputKey, outputValue);
		//----</3.Map 출력데이터 처리>---------
		
	}

}
