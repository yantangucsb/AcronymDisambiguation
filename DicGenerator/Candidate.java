package DicGenerator;

import java.util.ArrayList;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Candidate{
	String name;
	ArrayList<String> feature;
	
	public Candidate(String attr) {
		this.name = attr;
		feature = new ArrayList<String>();
	}
	
	public Candidate(){
		feature = new ArrayList<String>();
	}

	public void getFeature() {
		// TODO Auto-generated method stub
		
	}

	public void addTags(String text) {
		feature.add(text);
		
	}

	public void setName(String text) {
		name = text;
		
	}
	public String getName(){
		return name;
	}
	
	private void sentenceParser(String text) {
		 MaxentTagger tagger = new MaxentTagger("taggers/left3words-distsim-wsj-0-18.tagger");
		 String tagged = tagger.tagString(text);
		 System.out.println(tagged);
	}
}