package Features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import DicGenerator.WordDic;
import TextModel.Candidate;
import TextModel.TargetText;

public class Popularity extends Feature {

	public Popularity() {
		super();
	}

	public Popularity(String str1, String str2) {
		super(str1, str2);
	}

	@Override
	public void setFeature(TargetText tt, WordDic worddic) {
		HashMap<String, Candidate> exs = worddic.getExpansions();
		Iterator<Entry<String, Candidate>> it = exs.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, Candidate> pairs = it.next();
			Candidate candi = pairs.getValue();
//			candi.setFeature(Integer.toString(candi.getViewNum()));
			tt.set2WekaData(candi.getName(), Integer.toString(candi.getViewNum()));
		}
	}

}
