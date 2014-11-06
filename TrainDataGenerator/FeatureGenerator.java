package TrainDataGenerator;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import DicGenerator.AcronymGenerator;
import DicGenerator.Candidate;
import DicGenerator.WordDic;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class FeatureGenerator {
	ArrayList<WordDic> wordDic;

	public void findExpansions(HashMap<String, String> words, HashMap<String, ArrayList<Candidate>> trainData) {
		Iterator<Entry<String, ArrayList<Candidate>>> it = trainData.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry pairs = (Map.Entry)it.next();
	    	String name = (String) pairs.getKey();
	    	ArrayList<Candidate> candis = getWordExpansion(words.get(name)); //what if no expansions
	    	if(candis != null){
	    		WordDic newWord = new WordDic(name, candis);
	    		ArrayList<Candidate> trainpara = (ArrayList<Candidate>) pairs.getValue();
	    		newWord.setTrainText(trainpara);
	    		wordDic.add(newWord);
	    	}else{
	    		//resolve it by adding an expansion manually
	    		//maybe remove the item
	    	}
	    }
		
	}
	
	private ArrayList<Candidate> getWordExpansion(String link) {
		ArrayList<Candidate> candis = new ArrayList<Candidate>();
		int index = 0;
		if(link.charAt(0) == '/')
			link = link.substring(1, link.length());
		String curlink = AcronymGenerator.acronymSourceLink+ link + "&p=99999";
//		URL url;
		try {
			Document expansionHtml = Jsoup.connect(curlink).userAgent("Mozilla").get();
			Elements tables = expansionHtml.select("table");
//			System.out.println(tables.size());
			if(tables.size() == 0)
				return null;
			Elements rows = (tables.get(0)).select("td");
//			System.out.println("rows: "+ rows.size());
			for(int i=0; i<rows.size(); i++){
				Element curAcronym = rows.get(i);
				String classTag = curAcronym.attr("class");
				
				if(!classTag.equals("tal dx"))
					continue;
				Elements expans = curAcronym.select("p");
//				System.out.println(expans.size());
				Candidate cur = new Candidate();
				boolean hasExpansion = false;
				for(int j=0; j<expans.size(); j++){
					Element curp = expans.get(j);
					
					if(curp.attr("class").equals("desc")){
						
//						if(isExistWiki(curp.text())){
						if(hasNonAscii(curp.text()))
							break;
						cur.setName(curp.text());
//							System.out.println("Get an expansion:" + curp.text());
						hasExpansion = true;
//						System.out.println("Test expansion:" + cur.name);
					}else if(curp.attr("class").equals("path")){
						Elements ps = curp.select("a");
						for(int k=0; k<ps.size(); k++){
							cur.addTags(ps.get(k).text());
						}
					}
				}
				if(hasExpansion){
					candis.add(cur);
				}
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(candis.size() != 1){
			int size = candis.size();
			for(int i=size - 1; i>=0; i--){
				if(!isExistWiki(candis.get(i))){
					candis.remove(i);
				}
				if(candis.size() == 1)
					break;
			}
			
		}
		return candis;
	}
	
	private boolean isExistWiki(Candidate candi) {
		String name = candi.name.replaceAll(" ", "_");
		String link = "http://en.wikipedia.org" + "/wiki/" + name;
		try {
			Document tmpdoc = Jsoup.connect(link).userAgent("Mozilla").get();
			if(tmpdoc != null){
				Elements ps = tmpdoc.select("p");
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
	
	private boolean hasNonAscii(String s) {
		CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
		if(!asciiEncoder.canEncode(s))
			return true;
		return false;
	}

	public void genetrainDataFeature() {
		for(WordDic wd: wordDic){
			wd.getfeatures();
		}
	}
}
