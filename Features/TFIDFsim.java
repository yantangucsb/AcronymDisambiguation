package Features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import TextModel.Candidate;
import TextModel.TargetText;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.Morphology;
import DicGenerator.PrintDic;
import DicGenerator.WordDic;

public class TFIDFsim extends Feature {
	ArrayList<String> stopWords;
	
	public TFIDFsim() {
		stopWords = PrintDic.loadStopWords();
	}

	@Override
	public void setFeature(TargetText tt, WordDic worddic) {
		HashMap<String, Double> idfs = new HashMap<String, Double>();
		tt.tokenizeAndStem(stopWords);
		HashMap<String, Candidate> candis = worddic.getExpansions();
		Iterator<Entry<String, Candidate>> it = candis.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Candidate candi = (Candidate) pairs.getValue();
			candi.tokenizeAndStem(stopWords);
			HashMap<String, Integer> tfs = candi.getTF();
			Iterator<Entry<String, Candidate>> TFit = candis.entrySet().iterator();
			while(TFit.hasNext()){
				Map.Entry TFpairs = (Map.Entry)TFit.next();
				String word = (String) TFpairs.getKey();
				if(idfs.containsKey(word))
					idfs.put(word, idfs.get(word)+1);
				else
					idfs.put(word, (double) 1);
			}
		}
		Iterator<Entry<String, Double>> IDFit = idfs.entrySet().iterator();
		while(IDFit.hasNext()){
			Map.Entry IDFpairs = (Map.Entry)IDFit.next();
			Double idf = (Double) IDFpairs.getValue();
			idf = 1 + Math.log(candis.size()/idf);
			IDFpairs.setValue(idf);
		}
		it = candis.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Candidate candi = (Candidate) pairs.getValue();
			Double docweight = 0.0;
			candi.tokenizeAndStem(stopWords);
			HashMap<String, Integer> tfs = candi.getTF();
			Iterator<Entry<String, Candidate>> TFit = candis.entrySet().iterator();
			while(TFit.hasNext()){
				Map.Entry TFpairs = (Map.Entry)TFit.next();
				String word = (String) TFpairs.getKey();
				if(idfs.containsKey(word)){
					docweight += idfs.get(word)*(Integer) TFpairs.getValue();
				}
			}
			candi.setFeature(docweight.toString());
		}
	}
	
	
}
