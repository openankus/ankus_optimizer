import java.io.BufferedReader;
import java.io.FileReader;


import weka.core.Instances;

public class GAMain {

	public static void main(String[] args) throws Exception {
		
		// GA ��ü ����
		CGA ga = new CGA();
		
		// GA ȯ�溯�� ����
		int 	seed 			= 1;
		int 	popSize			= 10;
		int		maxGeneration 	= 3;
		float 	crossProb 		= 0.9f;
		float	mutProb			= 0.5f;
		
		ga.setParameters(popSize,seed,crossProb,mutProb);
		
		// �ʱ� ��ü���� ����
		int 	numAttri		= 5;	// �Է¼Ӽ� ����
		int		numAlgPara		= 2;	// �˰��� ȯ�溯�� ����
		int		binaryStrSize	= 5;	// �������ڿ� ũ��
		float[]	min				= {0.1f,2.0f};	// �˰����� ȯ�溯���� ������ �� �ִ� �ּҰ�
		float[]	max				= {1.0f,80.0f};	// �˰����� ȯ�溯���� ������ �� �ִ� �ִ밪
		int		classIndex		= 4;		// �Էµ����Ϳ� ���� Ŭ���� �Ӽ� ����
		
		
		ga.setInitialPopulation(numAttri,numAlgPara,binaryStrSize,min,max,classIndex);
//		System.out.println("��ü �ʱ�ȭ �Ϸ�...");
		
		// ��ü��
		// ���� ���� ��ų �˰��� ��ü ���� (�����ۼ�Ʈ�� ����)
		//weka.classifiers.functions.MultilayerPerceptron mlp = new weka.classifiers.functions.MultilayerPerceptron();
		weka.classifiers.trees.J48 decisionTree = new weka.classifiers.trees.J48();
		
		// weka �Էµ����� �ҷ��� (arff ����)
		BufferedReader reader = new BufferedReader(new FileReader("D:/Programs/data/iris.arff"));
		Instances data = new Instances(reader);
		data.setClassIndex(classIndex);
		
		ga.setFitnessVal(decisionTree, data);
//		System.out.println("��ü �� �Ϸ�..");
		
		int generation = 0;
		System.out.println(" ************************************ "+generation+" ����: "+ga.getelitist().getFitness());
		ga.getelitist().toStringModel();
		
		do{
			// ��ü����
			ga.selectMethod();
//			System.out.println("��ü ���� �Ϸ�..");
		
			// ���� Ȯ��
			ga.crossover();
//			System.out.println("��ü ���� �Ϸ�..");
		
			// �������� Ȯ��
			ga.mutation(classIndex);
//			System.out.println("��ü �������� �Ϸ�..");
			
			// ��ü��
			ga.setFitnessVal(decisionTree, data);
			
			generation++;
			
			System.out.println(" ************************************ "+generation+" ����: "+ga.getelitist().getFitness());
			ga.getelitist().toStringModel();
	
		}while(generation <= maxGeneration);
		
		
	}

}
