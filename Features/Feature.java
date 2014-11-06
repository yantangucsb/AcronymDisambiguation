package Features;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public abstract class Feature {
	String name;
	public String getFeatureName() {
		return name;
	}
	
	public void setFeatureName(String str) {
		name = str;
	}
	
	private String setTagger(String text) {
		 MaxentTagger tagger = new MaxentTagger("taggers/left3words-distsim-wsj-0-18.tagger");
		 String tagged = tagger.tagString(text);
		 return tagged;
//		 System.out.println(tagged);
	}
	
	public abstract void setFeature(String str1, String str2);
}
