package DicGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import TextModel.Candidate;

public class AnchorDataGenerator {
	HashMap<String, ArrayList<String>> expansions;
	ArrayList<WordDic> fullData;
	
	AnchorDataGenerator() {
		expansions = new HashMap<String, ArrayList<String>>();
		PrintDic.loadExpansions(expansions, "wiki/finalTrainData/candis_N");
		fullData = new ArrayList<WordDic>();
	}
	
	public void getAnchorData() {
		
		Iterator<Entry<String, ArrayList<String>>> it = expansions.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			ArrayList<String> candis = (ArrayList<String>) pairs.getValue();
			for(String candi: candis) {
				isExistWiki(new Candidate(candi));
			}
		}
	}

	private void getData(String candi) {
		
		
	}
	
	private boolean isExistWiki(Candidate candi) {
		String name = candi.getName().replaceAll(" ", "_");
		String link = "http://en.wikipedia.org/wiki/Special:Search/" + name;
		try {
			Document tmpdoc = Jsoup.connect(link).userAgent("Mozilla").get();
			if(tmpdoc != null){
				Elements ps = tmpdoc.select("p");
				if(ps.size() != 0)
					candi.setPrimeText(ps.get(0).text());
				for(Element p: ps){
					candi.setText(p.text());
				}
				return true;
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return false;
		}
		return false;
	}
}
