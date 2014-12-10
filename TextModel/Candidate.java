package TextModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Features.Feature;
import Features.TFIDFsim;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Candidate extends TextBasicModel{
//	ArrayList<String> features;
	String primeText;
	String title;
	ArrayList<String> anchorText;
	int viewNum;
	double dl;
	int testCaseNum;
	
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
//		features = new ArrayList<String>();
		anchorText = new ArrayList<String>();
		title = "";
		primeText = "";
		viewNum = 0;
		testCaseNum = 0;
	}
	
	public Candidate(String str1, String str2) {
		name = str1;
		text = str2;
	}

/*	public ArrayList<String> getFeature() {
		return features;
	}*/
	
	public void increaseTestCaseNum() {
		testCaseNum += 1;
	}
	
	public int getTestCaseNum() {
		return this.testCaseNum;
	}

	public void setName(String text) {
		name = text;
		
	}
	public String getName(){
		return name;
	}
	
	public void setText(String str) {
		text += preprocess(str);
	}

	public String getText() {
		return text;
	}
	
	public void setPrimeText(String str) {
		primeText += preprocess(str);;
	}
	
	public String getPrimeText() {
		return primeText;
	}
	
	public boolean hasText() {
		if(text.length() == 0)
			return false;
		return true;
	}

/*	public void setFeature(String str) {
		features.add(str);
	}*/

	public void setTitle(String t) {
		this.title = t;
		
	}

	public String getTitle() {
		return this.title;
	}

	public void setAnchorData(String text) {
		anchorText.add(preprocess(text));
	}

	public String getAnchorString() {
		String output = "";
		
		for(String str: anchorText){
			output += " ### " + str;
		}
		return output;
	}

	public void setViewNum(int num) {
		this.viewNum = num;
		
	}

	public int getViewNum() {
		return this.viewNum;
	}
	
	public void tokenize() {
		this.tokenizeAndStem(text);
		for(String str : this.anchorText){
			this.tokenizeAndStem(str);
		}
		
	}

	public ArrayList<String> getAnchorText() {
		return this.anchorText;
	}

	public double getDocLength() {
		int dl = 0;
		dl += this.text.length();
		for(String str: this.anchorText) {
			dl += str.length();
		}
		return dl;
	}
	
	public HashMap<String, Integer> getTF() {
		if(wordSum.isEmpty() || wordSum.size() == 0)
			this.tokenize();
		return wordSum;
	}

}