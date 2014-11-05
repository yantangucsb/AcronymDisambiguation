package DicGenerator;

import java.util.ArrayList;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Candidate{
	public String name;
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
	
}