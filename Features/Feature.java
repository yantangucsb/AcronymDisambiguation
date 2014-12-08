package Features;

import java.util.HashMap;

import TextModel.Candidate;
import TextModel.TargetText;
import DicGenerator.WordDic;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public abstract class Feature {
	String name;
	String valueType;
	
	public Feature() {
		
	}
	public Feature(String str1, String str2) {
		name = str1;
		valueType = str2;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String str) {
		name = str;
	}
	
	public String getValueType() {
		return valueType;
	}
	
	public void setValueType(String str) {
		valueType = str;
	}

	public abstract void setFeature(TargetText targetText, WordDic worddic);
}
