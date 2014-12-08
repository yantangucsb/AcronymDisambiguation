package Features;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import DicGenerator.WordDic;
import TextModel.Candidate;
import TextModel.TargetText;

public class NameCoverPercen extends Feature {

	public NameCoverPercen() {
	}

	public NameCoverPercen(String str1, String str2) {
		super(str1, str2);
	}

	@Override
	public void setFeature(TargetText tt, WordDic worddic) {
		//TFIDF has done, otherwise this will fail
		HashMap<String, Integer> qtf = tt.getTF();
		HashMap<String, Candidate> candis = worddic.getExpansions();
		Iterator<Entry<String, Candidate>> it = candis.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Candidate candi = (Candidate) pairs.getValue();
			String[] words = candi.getName().split(" ");
			int score = 0;
			for(String word: words) {
				word = word.toLowerCase();
				if(qtf.containsKey(word)){
					score += qtf.get(word);
				}
			}
			tt.set2WekaData(candi.getName(), Integer.toString(score));
		}
	}

}
