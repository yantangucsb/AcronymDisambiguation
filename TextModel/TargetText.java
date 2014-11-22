package TextModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import DicGenerator.WordDic;
import Features.CommonTermsNum;
import Features.Feature;
import Features.TFIDFsim;

public class TargetText extends TextBasicModel{
	String expansion;
	WordDic worddic;
	private ArrayList<Feature> features;
	private ArrayList<String> trainDataWeka;
	
	public TargetText(String name, String ex, String text){
		super();
		this.name = name;
		this.expansion = ex;
		this.text = text;
		initialize();
	}
	
	public void initialize() {
		features = new ArrayList<Feature>();
		addExistFeatures();
	}
	public void setWordDic(WordDic wd) {
		worddic = wd;
	}
	
	private void addExistFeatures() {
//		features.add(new CommonTermsNum());
		features.add(new TFIDFsim());
	}
	
	public void getfeatures() {
/*		for(Candidate trainPara : trainText) {
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
		}*/
		for(Feature f: features){
			f.setFeature(this, worddic);
		}

	}
	
	private void set2WekaData(String isSame) {
		String trainLine = "";
		//get feature from candidate
		trainDataWeka.add(trainLine);
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

	public boolean getBestCandi() {
		getfeatures();
		
		String bestCandiName = worddic.getBestCandi().getName();
/*		if(expansion.equals(bestCandiName)){
			System.out.println("Success in Example 1:" + expansion + " " + bestCandiName);
			return true;
		}
		System.out.println("Failure in Example 1:" + expansion + " " + bestCandiName);*/
		
		//whether classify right or not cannot be judged on the similarity of names.
		//Because the expansion may always within the doc or not.
		System.out.println("Cur Acr's ex: " + bestCandiName);
		return false;
	}
}
