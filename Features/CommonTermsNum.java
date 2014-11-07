package Features;

import java.util.ArrayList;
import java.util.HashMap;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import DicGenerator.Candidate;

public class CommonTermsNum extends Feature{
	double value;

	@Override
	public void setFeature(String str1, String str2,
			HashMap<String, Candidate> candis) {
		str1 = tokenizeAndStem(str1);
		
	}

	private String tokenizeAndStem(String str) {
/*		DocumentPreprocessor dp = new DocumentPreprocessor(str);
	      for (List sentence : dp) {
	        System.out.println(sentence);
	      }*/
	      PTBTokenizer ptbt = new PTBTokenizer(null, null, str);
	      for (CoreLabel label; ptbt.hasNext(); ) {
	        label = (CoreLabel) ptbt.next();
	        System.out.println(label);
	      }
		return str;
	}

	@Override
	public String getfeatureString() {
		// TODO Auto-generated method stub
		return null;
	}
}
