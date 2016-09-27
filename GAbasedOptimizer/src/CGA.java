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
	 * �����ھ˰��� ȯ�溯�� ���� �Լ�
	 * @param popSize	��üũ��
	 * @param maxGen	�ִ� ��ȭȽ��
	 * @param seed		��������
	 * @param crossProb	���� Ȯ��
	 * @param mutProb	�������� Ȯ��
	 */
	public void setParameters(int popSize, int seed, float crossProb, float mutProb) {
		
		this._popSize 	= popSize;
		this._crossProb = crossProb;
		this._mutProb 	= mutProb;
		this._random 	= new Random(seed);
		
	}

	/**
	 * �ʱ� ��ü���� ���� �Լ�
	 * 2������ 10������ ����ϱ� ���� ����ũ ����
	 * (����: �������� _popSize�� _random�� �����Ǿ� �־�� ��)
	 * @param numAttri		�Է� �Ӽ� ����
	 * @param numAlgPara	�˰��� ȯ�溯�� ����
	 * @param binaryStrSize	�������ڿ� ũ�� (��� ������ ������ ũ�⸦ ����)
	 * @param min			�˰��� ȯ�溯���� ������ �� �ִ� �ּҰ�
	 * @param max			�˰��� ȯ�溯���� ������ �� �ִ� �ִ밪
	 * @param classindex	Ŭ���� �ε���
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
//			System.out.println(i+" ��ü: "+ this._pop[i].toStringGene());
		}
		
		this._mask = new int [this._binaryStrSize];
		for(int i=0 ; i<this._binaryStrSize ; i++ ){
			this._mask[i] = (int)Math.pow(2.0, (double)i);
		}
	}

	/**
	 * ��ü������ �����ϰ� �ִ� �� ��ü�� ���յ� ����
	 * @param decisionTree	�������� ������ �� �ִ� �˰��� ��ü
	 * @param data			�������� �����ϱ� ���� �Էµ�����(�н������Ϳ� �׽�Ʈ������)
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
			
			// �Ӽ����� ������ ǥ���ϴ� �������� ���ڵ�	
			index = 0; 
			for(int j=0 ; j<tempData.numAttributes() ; index++){
				if(this._pop[i].getGene(index)==1){
					tempData.deleteAttributeAt(j);
				}else{
					j++;
				}
			}
			
			// �˰��� ȯ�溯���� ǥ���ϴ� �������� ���ڵ�
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
				System.out.println("10����: "+ j+" : "+val[j]);
				val[j] = ((val[j]*(float)(this._maxAlgPara[j]-this._minAlgPara[j]))/((float)Math.pow(2.0, (double)this._binaryStrSize)-1.0f))+(float)this._minAlgPara[j];
				System.out.println("����ȭ: "+ j+" : "+val[j]);
			}
			
			//�� ����
			decisionTree.setConfidenceFactor(val[0]);
			decisionTree.setMinNumObj((int)val[1]);
			decisionTree.buildClassifier(tempData);
			
			System.out.println(decisionTree.toString());
			System.exit(1);
			
			//�� ��
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
	 * ��ü���� �Լ�
	 * ����Ƽ��Ʈ ��� + �귿�� ���
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
	 * ���迬�� �Լ�
	 * ���迬���� �����ų �θ�ü ����
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
	 * ���� ���� ���迩����
	 * @param p1	�θ�ü 1
	 * @param p2	�θ�ü 2
	 * @param c1	�ڽİ�ü 1
	 * @param c2	�ڽİ�ü 2
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
	 * �������� ����: �յ� �������� ����
	 * �� �������ڿ� ������ �߻��� �� �������� Ȯ������ ���� ��� �ش� �������� ���� ��ȯ���� ��
	 * Ŭ���� �ε����� ��Ÿ���� �������ڿ� ���ؼ��� �������� ������ �����Ű�� ����
	 * @param classIndex Ŭ���� �ε���
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
	 * ��ü���ܿ��� ���� ���յ��� ���� ��ü ��ȯ �Լ�
	 * @return ��ü
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
