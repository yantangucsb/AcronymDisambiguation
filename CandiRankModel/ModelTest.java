package CandiRankModel;

import java.io.Reader;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.Morphology;
import DicGenerator.PrintDic;
import DicGenerator.WordDic;
import Features.Feature;
import Features.NameCoverPercen;
import Features.Popularity;
import Features.TFIDFsim;
import TextModel.Candidate;
import TextModel.TargetText;

public class ModelTest {
	public static wekaModel classifier = new wekaModel();
	public static ArrayList<String> testData = new ArrayList<String>();
	public static HashMap<String, WordDic> dic = new HashMap<String, WordDic>();
	public static ArrayList<TargetText> tts = new ArrayList<TargetText>();
	public static ArrayList<Feature> features = new ArrayList<Feature>();
	public static String path = "";
	
	public static void init() {
		PrintDic.loadWordDic(dic, "wiki/anchorData");
		features.add(new TFIDFsim("textDistance", "numeric"));
		features.add(new Popularity("wikiPopularity", "numeric"));
		features.add(new NameCoverPercen("NameCoverPercentage", "numeric"));
	}
	public static void loadModel(String filename) throws Exception {
		classifier.loadModel(filename);
		init();
	}
	
	public static String loadTestData(String filename) {
		String output = "";
		testData = PrintDic.loadTestData(filename);
		for(String str: testData) {
			output += str+'\n';
		}
		return output;
	}

	public static String runModel(String text) throws Exception {
		String[] strs = text.split("\n");
		testData.clear();
		tts.clear();
		for(String str : strs){
			testData.add(str);
		}
		for(String str : testData) {
			getDic(str);
		}
		return getOutput();
	}

	private static String getOutput() throws Exception {
		String output = "Result***************";
		
		for(TargetText tt: tts){
			ArrayList<ArrayList<String>> data = tt.getWekaData();
			int best = PredicNB(data);
			
			output += tt.getName() + " " + (data.get(best)).get(0) + "\n\n";
			output += "The probabilities of each candidate are:\n";
			for(ArrayList<String> strs: data) {
				output += strs.get(0) + "   " + strs.get(strs.size()-1) + "\n";
			}
			output += "\n\n";
		}
		return output;
	}

	private static int PredicNB(ArrayList<ArrayList<String>> data) throws Exception {
		double max = 0;
		int best = 0;
		for(int i=0; i<data.size(); i++) {
			double x = classifier.classcifyInstantce(data.get(i));
			DecimalFormat df = new DecimalFormat("#.##");      
			x = Double.valueOf(df.format(x));
			data.get(i).add(Double.toString(x));
			if(x > max) {
				max = x;
				best = i;
			}
		}
		return best;
	}
	private static void getDic(String str) {
		Reader reader = new StringReader(str);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		Iterator<List<HasWord>> it = dp.iterator();
		while (it.hasNext()) {
			List<HasWord> sentence = it.next();
			for (HasWord token : sentence) {
				Morphology morp = new Morphology();
				StringBuilder wordStr = new StringBuilder();
				wordStr.append(token);
				if(dic.containsKey(wordStr.toString())){
					GenerateTT(str, dic.get(wordStr.toString()));
				}
			}
		}
				
	}

	private static void GenerateTT(String str, WordDic wd) {
		TargetText tt = new TargetText(wd.getName(), "", str);
		tt.setHighlightIndex();
		tt.initWekaData(wd);
		for(Feature f: features){
			f.setFeature(tt, wd);
		}
		tts.add(tt);
	}

	public static String showDic() {
		String output = "";
		
		Iterator<Entry<String, WordDic>> it = dic.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			String acr = (String) pairs.getKey();
			output += acr + "\n";
		}
		return output;
	}
	public static String compareModel() throws Exception {
		String output = "\nTest file path: ";
		output += path;
		output += "\n\nTotal number of instantces: ";
		output += tts.size() +"\n\n";
		output += EvaluateNB();
		output += EvaluateRandom();
		return output;
	}
	
	private static String EvaluateRandom() {
		String output = "\n\nRandom\nResults\n*********\n";
		int total = tts.size();
		int correct = 0;
		for(TargetText tt: tts){
			ArrayList<ArrayList<String>> data = tt.getWekaData();
			Random generator = new Random(); 
			int best = generator.nextInt(data.size());
			if(tt.getExpansion().equals(data.get(best).get(0))){
				correct ++;
			}
		}
		output += "Precision: " + Double.toString(correct*1.0/total);
		return output;
	}
	private static String EvaluateNB() throws Exception {
		String output = "\nNaive Bayes\nResults\n*********\n";
		int total = tts.size();
		int correct = 0;
		for(TargetText tt: tts){
			ArrayList<ArrayList<String>> data = tt.getWekaData();
			int best = PredicNB(data);
			if(tt.getExpansion().equals(data.get(best).get(0))){
				correct ++;
			}
		}
		output += "Precision: " + Double.toString(correct*1.0/total);
		return output;
	}
	public static void loadDataFile(String filename) {
		tts.clear();
		path = filename;
		PrintDic.loadTrainData(tts, filename);
		for(TargetText tt: tts){
			tt.initWekaData(dic.get(tt.getName()));
			for(Feature f: features){
				f.setFeature(tt, dic.get(tt.getName()));
			}
		}
	}

}
