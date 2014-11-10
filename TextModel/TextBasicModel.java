package TextModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.Morphology;

public abstract class TextBasicModel {
	String name;
	String text;
	HashMap<String, Integer> wordSum;
	public TextBasicModel(){
		wordSum = new HashMap<String, Integer>();
	}
	
	public void tokenizeAndStem(ArrayList<String> stopWords) {
		DocumentPreprocessor dp = new DocumentPreprocessor(text);
		Iterator<List<HasWord>> it = dp.iterator();
		while (it.hasNext()) {
			List<HasWord> sentence = it.next();
			for (HasWord token : sentence) {
				Morphology morp = new Morphology();
				StringBuilder wordStr = new StringBuilder();
				wordStr.append(token);
				if(stopWords.contains(wordStr.toString()))
					continue;
				String stemWord = morp.stem(wordStr.toString());
				add2WordSum(stemWord);
			}
		}
	}
	
	public HashMap<String, Integer> getTF() {
		return wordSum;
	}
	private void add2WordSum(String stemWord) {
		if(wordSum.containsKey(stemWord)){
			int count = wordSum.get(stemWord);
			wordSum.put(stemWord, count+1);
		}else
			wordSum.put(stemWord, 1);
	}
	
	public String toString() {
		return name;
	}
}
