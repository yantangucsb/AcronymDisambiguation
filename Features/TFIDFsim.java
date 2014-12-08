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
		super();
		init();
		
	}

	private void init() {
		stopWords = PrintDic.loadStopWords();
		
	}

	public TFIDFsim(String str1, String str2) {
		super(str1, str2);
		init();
	}

	@Override
	public void setFeature(TargetText tt, WordDic worddic) {
		HashMap<String, Double> idfs = new HashMap<String, Double>();
		tt.tokenize(stopWords);
		HashMap<String, Candidate> candis = worddic.getExpansions();
		Iterator<Entry<String, Candidate>> it = candis.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Candidate candi = (Candidate) pairs.getValue();
			candi.tokenize(stopWords);
			HashMap<String, Integer> tfs = candi.getTF();
			Iterator<Entry<String, Integer>> TFit = tfs.entrySet().iterator();
			while(TFit.hasNext()){
				Map.Entry TFpairs = (Map.Entry)TFit.next();
				String word = (String) TFpairs.getKey();
				if(idfs.containsKey(word))
					idfs.put(word, idfs.get(word)+1);
				else
					idfs.put(word, (double) 1);
			}
		}
		//compute the score using (1+ln(1+ln(tf)))/((1-x)+x(dl/avdl)*qtf.ln((N+1)/df)
		getSimilarityScore(idfs, tt, worddic);
		
	}

	private void getSimilarityScore(HashMap<String, Double> idfs, TargetText tt, WordDic worddic) {
		double avdl = 0;
		double x = 0.2;
		int num = worddic.getExpansions().size();
		
		Iterator<Entry<String, Candidate>> it = worddic.getExpansions().entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Candidate candi = (Candidate) pairs.getValue();
			avdl += candi.getDocLength();
		}
		avdl = avdl/num;
		
		it = worddic.getExpansions().entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Candidate candi = (Candidate) pairs.getValue();
			Double docweight = 0.0;
//			candi.tokenizeAndStem(stopWords);
			HashMap<String, Integer> tfs = candi.getTF();
			double dl = candi.getDocLength();
			
			//get query's word vector
			Iterator<Entry<String, Integer>> TFit = tt.getTF().entrySet().iterator();
			while(TFit.hasNext()){
				Map.Entry TFpairs = (Map.Entry)TFit.next();
				String word = (String) TFpairs.getKey();
				int count = (int) TFpairs.getValue();
				
				if(tfs.containsKey(word)){
					int tf = tfs.get(word);
					double df = idfs.get(word);
					
					docweight += (1+Math.log((1+Math.log(tf))))/((1-0.2)+0.2*(dl/avdl))*count*Math.log((num+1)/df);
				}
			}
//			candi.setFeature(docweight.toString());
			tt.set2WekaData(candi.getName(), docweight.toString());
//			System.out.println("Get docweight " + docweight + " for ex: " +candi.getName());
		}
		
	}
	
	
}
