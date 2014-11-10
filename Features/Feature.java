package Features;

import java.util.HashMap;

import TextModel.Candidate;
import TextModel.TargetText;
import DicGenerator.WordDic;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public abstract class Feature {
	String name;
	public String getFeatureName() {
		return name;
	}
	
	public void setFeatureName(String str) {
		name = str;
	}

	public abstract void setFeature(TargetText targetText, WordDic worddic);
}
