package CandiRankModel;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		String output = "";
		double max = 0;
		int best = 0;
		for(TargetText tt: tts){
			ArrayList<ArrayList<String>> data = tt.getWekaData();
			for(int i=0; i<data.size(); i++) {
				double x = classifier.classcifyInstantce(data.get(i));
				if(x > max) {
					max = x;
					best = i;
				}
/*				ArrayList<String> str = data.get(i);
				double x = Double.parseDouble(str.get(1));
				if(x > max){
					max = x;
					best = i;
				}*/
			}
			output += tt.getName() + " " + (data.get(best)).get(0) + '\n';
		}
		return output;
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

}
