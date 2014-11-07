package Features;

import java.util.HashMap;

import DicGenerator.Candidate;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public abstract class Feature {
	String name;
	public String getFeatureName() {
		return name;
	}
	
	public void setFeatureName(String str) {
		name = str;
	}
	
	
	public abstract void setFeature(String str1, String str2, HashMap<String, Candidate> candis);

	public abstract String getfeatureString() ;
}
