package org.openankus.optimizer.ga.chromset;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class TextKeyDescComparator extends WritableComparator{
	
	public TextKeyDescComparator() {
		super(Text.class, true);
	}
	
	@Override
	public int compare(WritableComparable a, WritableComparable b) {
		// TODO Auto-generated method stub
		Text k1 = (Text) a;
		Text k2 = (Text) b;
		
		int cmp = k1.compareTo(k2);
		if (cmp != 0){
			return -1 * cmp;
		}
		return cmp;
	}

}
