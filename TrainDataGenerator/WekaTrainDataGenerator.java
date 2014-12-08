package TrainDataGenerator;

import java.util.ArrayList;

import DicGenerator.PrintDic;
import DicGenerator.WordDic;
import Features.DiscrimitveKeyWords;
import Features.Feature;
import Features.NameCoverPercen;
import Features.Popularity;
import Features.TFIDFsim;
import TextModel.TargetText;

public class WekaTrainDataGenerator {
	ArrayList<TargetText> trainData;
	ArrayList<WordDic> dic;
	private ArrayList<Feature> features;

	public WekaTrainDataGenerator() {
		// TODO Auto-generated constructor stub
		trainData = new ArrayList<TargetText>();
		dic = new ArrayList<WordDic>();
		features = new ArrayList<Feature>();
		addExistFeatures();
		PrintDic.loadTrainData(trainData, "wiki/traindata");
		PrintDic.loadDic(dic, "wiki/anchorData");
	}
	
	private void addExistFeatures() {
//		features.add(new CommonTermsNum());
		features.add(new TFIDFsim("textDistance", "numeric"));
		features.add(new Popularity("wikiPopularity", "numeric"));
//		features.add(new DiscrimitveKeyWords("discrimitiveWordsCoverage", "numeric"));
		features.add(new NameCoverPercen("nameCoverPercentage", "numeric"));
	}
	
	public void getWekaTrainData() {
		getFeatures();
	}

	private void getFeatures() {
		for(TargetText tt: trainData){
			WordDic wd = getDic(tt);
			
			if(wd == null){
				System.out.println("wd null");
				return;
			}
			
			tt.initWekaData(wd);
			for(Feature f: features){
				f.setFeature(tt, wd);
			}
		}
		printSourceFile();
	}

	private void printSourceFile() {
		String output = "";
		output += "@relation AcrDisambugity\n\n";
		for(Feature f : features){
			output += "@attribute ";
			output += f.getName() + " ";
			if(f.getValueType().equals("numeric"))
				output += f.getValueType() + "\n";
		}
		output += "@attribute ";
		output += "candiMatch {yes, no}\n";
		output += "\n";
		
		output += "@data\n";
		for(TargetText tt : trainData){
//	    	System.out.println(wd.printData());
	    	output += tt.printFeatures();
	    }
		PrintDic.printWekafile(output, "wiki/weka.arff");
		
	}

	private WordDic getDic(TargetText tt) {
		for(WordDic wd : dic) {
//			System.out.println(wd.getName() + " " + tt.getName() + "\n");
			if(wd.getName().equals(tt.getName()))
				return wd;
		}
		return null;
	}
}
