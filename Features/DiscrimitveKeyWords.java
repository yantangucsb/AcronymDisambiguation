package Features;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import TextModel.Candidate;
import TextModel.TargetText;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import DicGenerator.WordDic;

public class DiscrimitveKeyWords extends Feature{
	double value;
	ArrayList<String> ttKeywords;
	ArrayList<String> candiKeywords;
	
	public DiscrimitveKeyWords () {
		super();
		init();
	}

	public DiscrimitveKeyWords(String str1, String str2) {
		super(str1, str2);
		init();
	}
	
	private void init(){
		value = 0;
		ttKeywords = new ArrayList<String>();
		candiKeywords = new ArrayList<String>();
	}
	
	@Override
	public void setFeature(TargetText targetText, WordDic worddic) {
		getTTkeywords(targetText);
		
		HashMap<String, Candidate> exs = worddic.getExpansions();
		Iterator<Entry<String, Candidate>> it = exs.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, Candidate> pairs = it.next();
			String acr = pairs.getKey();
			Candidate candi = pairs.getValue();
			
			getKeyWords(candi.getPrimeText(), candi, acr);
			getKeyWords(candi.getAnchorString(), candi, acr);
			value = keyWordsMatch(targetText);
//			candi.setFeature(Integer.toString(score));
			this.candiKeywords.clear();
		}
		
	}
	
	private void getTTkeywords(TargetText targetText) {
		String tagged = setTagger(targetText.getText());
		String[] taggedWords = tagged.split(" ");
		for(String tagWord : taggedWords) {
			if(tagWord.contains("/NN")){
				String[] words = tagWord.split("/");
				Morphology morp = new Morphology();
				String wordStem = morp.stem(words[0]);
				if(ttKeywords.contains(wordStem))
					continue;
				this.ttKeywords.add(wordStem);
			}
		}
		
	}

	private int keyWordsMatch(TargetText targetText) {
		int score = 0;
		for(String ttKeyword : ttKeywords) {
			if(candiKeywords.contains(ttKeyword)){
				score += 1;
			}
		}
		return score;
	}

	private void getKeyWords(String text, Candidate candi, String acr) {
		Reader reader = new StringReader(text);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);

//		List<String> sentenceList = new LinkedList<String>();
		Iterator<List<HasWord>> it = dp.iterator();
		while (it.hasNext()) {
		  
		   List<HasWord> sentence = it.next();
		   StringBuilder sentenceSb = new StringBuilder();
		   for (HasWord token : sentence) {
		      sentenceSb.append(token);
		      sentenceSb.append(' ');
//		      Morphology morp = new Morphology();
//		      String st = morp.stem(sentenceSb.toString());
//		      System.out.println(st);
		   }
		   String sen = sentenceSb.toString();
		   String tagged = setTagger(sen);
		   wordProcessor(tagged, candi, acr);
		}
	}

	private String setTagger(String text) {
		 MaxentTagger tagger;
		try {
			tagger = new MaxentTagger("taggers/left3words-wsj-0-18.tagger");
			String tagged = tagger.tagString(text);
			return tagged;
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		 System.out.println(tagged);
		return "";
	}

	private void wordProcessor(String tagged, Candidate candi, String acr) {
		String[] taggedWords = tagged.split(" ");
		for(String tagWord : taggedWords) {
			if(tagWord.contains("/NN")){
				String[] words = tagWord.split("/");
				if(acr.equals(words[0]))
					continue;
				Morphology morp = new Morphology();
				String wordStem = morp.stem(words[0]);
				if(candiKeywords.contains(wordStem))
					continue;
				this.candiKeywords.add(wordStem);
			}
		}
	}
}
