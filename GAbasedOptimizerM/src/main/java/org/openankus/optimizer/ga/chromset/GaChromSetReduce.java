package org.openankus.optimizer.ga.chromset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

/**
 * 개체집합 별 진화연산을 수행하는 Map 결과를 병합하는 Reduce
 *
 */
public class GaChromSetReduce extends Reducer<Text, Text, Text, Text>{
	
	private MultipleOutputs<Text, Text> mos = null;
	
	@Override
	protected void setup(Reducer<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		super.setup(context);
		mos = new MultipleOutputs<Text,Text>(context);
	}
	
	@Override
	protected void reduce(Text key, Iterable<Text> values,
			Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
		
		
		Iterator<Text> iter = values.iterator();
		while (iter.hasNext()){
			//	평가결과 병합
			
			Text value = iter.next();
			
			//----<개체집합 단위 출력파일 쓰기>------
//			FloatWritable avgFitness = key;
			Text chromSetId = key;
//			context.write(point, value);
//			mos.write("chromSet", avgFitness, value);
			mos.write(GaChromSetMain.PREFIX_CHROMSET, chromSetId, value);
			//----<개체집합 단위 출력파일 쓰기>------
			
			//----<개체 단위 출력파일 쓰기>------	
			System.out.println("-----<chromSet ID :"+chromSetId.toString()+">----------");
			StringTokenizer tokenizer = new StringTokenizer(value.toString(), Chrom.DELIMITER_CHROM);
			Text fitnessChromText = new Text();
			Text fitnessText = new Text();
			while (tokenizer.hasMoreTokens()){
				String fitnessChromString = tokenizer.nextToken();
				fitnessChromText.set(fitnessChromString);
				Chrom chrom = new Chrom(fitnessChromString);
				fitnessText.set(String.format("%.10f", chrom.getFitness()));
				
				mos.write(GaChromSetMain.PREFIX_CHROM, fitnessChromText, fitnessText);
				System.out.println(fitnessChromString+"\t"+String.format("%.10f", chrom.getFitness()));
			}
			System.out.println("---------------");
			//----</개체 단위 출력파일 쓰기>------	
		}
	}
	
	@Override
	protected void cleanup(Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
		mos.close();
	}

}
