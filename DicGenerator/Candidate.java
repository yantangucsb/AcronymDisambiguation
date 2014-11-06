package DicGenerator;

import java.util.ArrayList;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Candidate{
	public String name;
	ArrayList<String> feature;
	public String text;
	
	public Candidate(String attr) {
		this.name = attr;
		feature = new ArrayList<String>();
		text = "";
	}
	
	public Candidate(){
		feature = new ArrayList<String>();
	}

	public Candidate(String str1, String str2) {
		name = str1;
		text = str2;
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
	
	public void setText(String str) {
		text += str;
	}
	
	public String getText() {
		return text;
	}
	
	public boolean hasText() {
		if(text.length() == 0)
			return false;
		return true;
	}
	
}