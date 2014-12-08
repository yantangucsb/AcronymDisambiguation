package TextModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import DicGenerator.WordDic;
import Features.*;

public class TargetText extends TextBasicModel{
	String expansion;
	ArrayList<Integer> AcrIndex;
//	WordDic worddic;
	
	private ArrayList<ArrayList<String>> trainDataWeka;
	
	public TargetText(String name, String ex, String text){
		super();
		this.name = name;
		this.expansion = ex;
		this.text = preprocess(text);
		initialize();
	}
	
	public void initialize() {
		trainDataWeka = new ArrayList<ArrayList<String>>();
	}
/*	public void setWordDic(WordDic wd) {
		worddic = wd;
	}*/
	
	public void initWekaData(WordDic wd) {
		Iterator<Entry<String, Candidate>> it = wd.getExpansions().entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, Candidate> pairs = it.next();
			Candidate candi = pairs.getValue();
			ArrayList<String> featureSeq = new ArrayList<String>();
			featureSeq.add(candi.getName());
			trainDataWeka.add(featureSeq);
		}
	}

	
/*	public void getfeatures(WordDic worddic) {
		for(Candidate trainPara : trainText) {
			trainPara.tokenizeAndStem();
			Iterator<Entry<String, Candidate>> it = expansions.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, Candidate> pairs = it.next();
				Candidate candi = pairs.getValue();
				candi.tokenizeAndStem();
				for(Feature f: features){
					f.setFeature(trainPara, candi, expansions);
				}
				String isSame = (trainPara.name.equals(candi.name))? "Y":"N";
				set2WekaData(isSame);
			}
		}
		initWekaData(worddic);

	}*/
	
	public void set2WekaData(String name, String feature) {
		boolean setSuccess = false;
		for(ArrayList<String> seq : trainDataWeka){
			if(name == seq.get(0)){
				seq.add(feature);
				setSuccess = true;
			}
		}
		assert(setSuccess == false);
		return;
	}

	public String getName() {
		return name;
	}
	
	public String getExpansion() {
		return expansion;
	}
	
	public String getText() {
		return text;
	}
	
	public void tokenize(ArrayList<String> stopWords) {
		this.tokenizeAndStem(text, stopWords);
	}

	public String printFeatures() {
		String output = "";
		for(ArrayList<String> al : trainDataWeka) {
			for(int i=1; i<al.size(); i++)
				output += al.get(i) + " ";
			if(al.get(0).equals(this.expansion))
				output += "yes\n";
			else
				output += "no\n";
		}
		return output;
	}
	
	public ArrayList<ArrayList<String>> getWekaData(){
		return this.trainDataWeka;
	}

	public void setHighlightIndex() {
		if(AcrIndex == null){
			AcrIndex = new ArrayList<Integer>();
		}
		
		int index = this.text.indexOf(name);
		while(index >= 0) {
			AcrIndex.add(index);
			index = this.text.indexOf(name, index + 1);
		}
		
	}
	
	public ArrayList<Integer> getHighlightIndex() {
		return this.AcrIndex;
	}

/*	public boolean getBestCandi(WordDic wd) {
		getfeatures(wd);
		
		String bestCandiName = wd.getBestCandi().getName();
		if(expansion.equals(bestCandiName)){
			System.out.println("Success in Example 1:" + expansion + " " + bestCandiName);
			return true;
		}
		System.out.println("Failure in Example 1:" + expansion + " " + bestCandiName);
		
		//whether classify right or not cannot be judged on the similarity of names.
		//Because the expansion may always within the doc or not.
		System.out.println("Cur Acr's ex: " + bestCandiName);
		return false;
	}*/
}
