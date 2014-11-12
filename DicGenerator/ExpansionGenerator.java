package DicGenerator;

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

import TextModel.Candidate;
import TextModel.TargetText;

public class ExpansionGenerator {
	HashMap<String, String> words;
	HashMap<String, ArrayList<String>> expansions;
	ArrayList<String> waitWords;
	
	public ExpansionGenerator() {
		String filename = "wiki/candis_J";
		words = new HashMap<String, String>();
		expansions = new HashMap<String, ArrayList<String>>();
		waitWords = new ArrayList<String>();
		
		PrintDic.loadWords(words);
		PrintDic.loadExpansions(expansions, filename);
		GetExpansions();
		PrintDic.printExpansions(expansions, filename);
		PrintDic.printList(waitWords);
	}

	private void GetExpansions() {
		Iterator<Entry<String, String>> it = words.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			String name = (String) pairs.getKey();
			if(expansions.containsKey(name)){
				if(expansions.get(name).size() == 0)
					expansions.remove(name);
				else{
					continue;
				}
			}
			if(name.charAt(0) != 'J' && name.charAt(0) != 'j' )
				continue;
			ArrayList<String> candis = getWordExpansion(words.get(name));
			if(candis != null && candis.size() > 0){
				expansions.put(name, candis);
				System.out.println(name);
				for(String candi : candis)
					System.out.println("ex: " + candi);
			}
		}
	}

	private ArrayList<String> getWordExpansion(String link) {
		ArrayList<String> candis = new ArrayList<String>();
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
//				Candidate cur = new Candidate();
				String cur = "";
				boolean hasExpansion = false;
				for(int j=0; j<expans.size(); j++){
					Element curp = expans.get(j);
					
					if(curp.attr("class").equals("desc")){
						
//						if(isExistWiki(curp.text())){
						if(hasNonAscii(curp.text()))
							break;
//						cur.setName(curp.text());
						cur = curp.text();
//							System.out.println("Get an expansion:" + curp.text());
						hasExpansion = true;
//						System.out.println("Test expansion:" + cur.name);
					}
/*					else if(curp.attr("class").equals("path")){
						Elements ps = curp.select("a");
						for(int k=0; k<ps.size(); k++){
							cur.addTags(ps.get(k).text());
						}
					}*/
				}
				if(hasExpansion){
					if(!candis.contains(cur))
						candis.add(cur);
				}
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("current link: " + link);
			waitWords.add(link);
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
	
	private boolean isExistWiki(String candi) {
		String name = candi.replaceAll(" ", "_");
		String link = "http://en.wikipedia.org" + "/wiki/" + name;
		try {
			Document tmpdoc = Jsoup.connect(link).userAgent("Mozilla").get();
			if(tmpdoc != null){
/*				Elements ps = tmpdoc.select("p");
				for(Element p: ps){
					candi.setText(p.text());
				}*/
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

}
