package TrainDataGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import DicGenerator.PrintDic;
import DicGenerator.WordDic;
import Features.DiscrimitveKeyWords;
import Features.Feature;
import Features.NameCoverPercen;
import Features.Popularity;
import Features.TFIDFsim;
import TextModel.Candidate;
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
		RandomGenerateTrainData();
//		preprocessTrainData();
		
		
		PrintDic.loadDic(dic, "wiki/test3/anchorData3");
//		preprocessDic();
	}
	
	private void preprocessTrainData() {
		ArrayList<TargetText> tts = new ArrayList<TargetText>();
		PrintDic.loadTrainData(tts, "wiki/test2/trainData");
		for(TargetText tt: tts) {
//			System.out.println(tt.getExpansion());
			tt.tokenize();
		}
		
	}

	private void preprocessDic() {
		for(WordDic wd : dic) {
			Iterator<Entry<String, Candidate>> it = wd.getExpansions().entrySet().iterator();
			while(it.hasNext()){
				Map.Entry pairs = (Map.Entry)it.next();
				Candidate candi = (Candidate) pairs.getValue();
//				System.out.println(candi.getName());
				candi.tokenize();
			}
		}
		
	}

	public void RandomGenerateTrainData() {
		ArrayList<TargetText> tts = new ArrayList<TargetText>();
		PrintDic.loadTrainData(tts, "wiki/test3/trainDataFull");
		while(trainData.size() < 100) {
			Random r = new Random();
			int i = r.nextInt(tts.size());
			trainData.add(tts.get(i));
			tts.remove(i);
		}
//		PrintDic.printTestCase(trainData, "wiki/test3/test3");
	}

	private void addExistFeatures() {
//		features.add(new CommonTermsNum());
		features.add(new TFIDFsim("textDistance", "numeric"));
		features.add(new Popularity("wikiPopularity", "numeric"));
//		features.add(new DiscrimitveKeyWords("discrimitiveWordsCoverage", "numeric"));
		features.add(new NameCoverPercen("nameCoverPercentage", "numeric"));
	}
	
	public void getWekaTrainData() {
//		getTrainDataInfo();
		getFeatures();
	}

	private void getTrainDataInfo() {
		String output = "";
		for(TargetText tt: trainData) {
			for(WordDic wd : dic) {
				if(wd.getName().equals(tt.getName())){
//					System.out.println("Get acr:" +tt.getName());
					Candidate candi = wd.getExpansions().get(tt.getExpansion());
					candi.increaseTestCaseNum();
//					System.out.println(candi.getTestCaseNum());
				}
			}
		}
		for(WordDic wd : dic) {
			output += wd.getName() + "************\n";
			Iterator<Entry<String, Candidate>> it = wd.getExpansions().entrySet().iterator();
		    while (it.hasNext()) {
		    	Map.Entry pairs = (Map.Entry)it.next();
		    	Candidate candi = (Candidate) pairs.getValue();
		    	output += candi.getName() + "  " + Integer.toString(candi.getViewNum()) + "  ";
		    	output += Integer.toString(candi.getTestCaseNum()) + "\n";
		    	System.out.println(Integer.toString(candi.getTestCaseNum()));
		    }
		}
		PrintDic.printLog(output, "wiki/test2/logfile");
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
		PrintDic.printWekafile(output, "wiki/test3/weka4.arff");
		
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
