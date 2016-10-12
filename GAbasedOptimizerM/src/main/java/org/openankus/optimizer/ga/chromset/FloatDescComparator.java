package org.openankus.optimizer.ga.chromset;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class FloatDescComparator extends WritableComparator{
	
	public FloatDescComparator() {
		super(FloatWritable.class, true);
	}
	
	@Override
	public int compare(WritableComparable a, WritableComparable b) {
		// TODO Auto-generated method stub
		FloatWritable k1 = (FloatWritable) a;
		FloatWritable k2 = (FloatWritable) b;
		
		int cmp = k1.compareTo(k2);
		if (cmp != 0){
			return -1 * cmp;
		}
		return cmp;
	}

}
