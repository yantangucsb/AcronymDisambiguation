package TextModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import Features.Feature;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Candidate extends TextBasicModel{
	ArrayList<String> features;
	String primeText;
	String title;
	ArrayList<String> anchorText;
	
	public Candidate(String attr) {
		super();
		this.name = attr;
		initialize();
	}
	
	public Candidate(){
		super();
		initialize();
	}
	
	private void initialize() {
		features = new ArrayList<String>();
		anchorText = new ArrayList<String>();
		title = "";
		primeText = "";
	}
	
	public Candidate(String str1, String str2) {
		name = str1;
		text = str2;
	}

	public ArrayList<String> getFeature() {
		return features;
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
	
	public void setPrimeText(String str) {
		primeText += str;
	}
	
	public String getPrimeText() {
		return primeText;
	}
	
	public boolean hasText() {
		if(text.length() == 0)
			return false;
		return true;
	}

	public void setFeature(String str) {
		features.add(str);
	}

	public void setTitle(String t) {
		this.title = t;
		
	}

	public String getTitle() {
		return this.title;
	}

	public void setAnchorData(String text) {
		anchorText.add(text);
	}

	public String getAnchorString() {
		String output = "";
		
		for(String str: anchorText){
			output += " ### " + str;
		}
		return output;
	}

}