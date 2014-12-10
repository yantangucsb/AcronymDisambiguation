package TextModel;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Features.TFIDFsim;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.Morphology;

public abstract class TextBasicModel {
	String name;
	String text;
	HashMap<String, Integer> wordSum;
	public TextBasicModel(){
		wordSum = new HashMap<String, Integer>();
		text = "";
		name = "";
	}
	
	public void tokenizeAndStem(String text) {
		Reader reader = new StringReader(text);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		Iterator<List<HasWord>> it = dp.iterator();
		while (it.hasNext()) {
			List<HasWord> sentence = it.next();
			for (HasWord token : sentence) {
				Morphology morp = new Morphology();
				StringBuilder wordStr = new StringBuilder();
				wordStr.append(token);
				String word = wordStr.toString().toLowerCase();
				//check word if it is legal
				
				if(TFIDFsim.stopWords.contains(word))
					continue;
				if(word.length() == 1){
					continue;
				}
				if(!Pattern.matches("[a-zA-Z]+", word)){
					if(isYearOrComplexWord(word))
						add2WordSum(word);
					continue;
				}
				String stemWord = morp.stem(word);
				add2WordSum(stemWord);
			}
		}
	}
	
	private boolean isYearOrComplexWord(String word) {
		if(Pattern.matches("[0-9]", word)){
			
		}
		return false;
	}
	
	public String preprocess(String str) {
		Pattern p = Pattern.compile("\\[(.)*\\]");
		Matcher m = p.matcher(str);
		return m.replaceAll("");
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
