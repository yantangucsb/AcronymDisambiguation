package DicGenerator;

import java.io.IOException;
import java.net.SocketTimeoutException;
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

public class ExpansionGenerator {
	HashMap<String, String> words;
	HashMap<String, ArrayList<String>> expansions;
	ArrayList<String> waitWords;
	boolean linkfailed;
	boolean notExist;
	
	public ExpansionGenerator() {
		String filename = "wiki/candisFull_A";
		words = new HashMap<String, String>();
		expansions = new HashMap<String, ArrayList<String>>();
		waitWords = new ArrayList<String>();
		
		PrintDic.loadWords(words, "wiki/acronyms");
		PrintDic.loadExpansions(expansions, filename);
//		GetExpansions();
		GetExpansionsDF();
		PrintDic.printSubAcr(words, "wiki/acronyms_A");
		PrintDic.printExpansions(expansions, filename);
		PrintDic.printList(waitWords, "wiki/waitWords_A");
	}
	
	public ExpansionGenerator(int x) {
		String filename = "wiki/candisFull_B";
		words = new HashMap<String, String>();
		expansions = new HashMap<String, ArrayList<String>>();
		waitWords = new ArrayList<String>();
		
		PrintDic.loadWords(words, "wiki/acronyms_B");
		PrintDic.loadExpansions(expansions, filename);
//		GetExpansions();
		GetExpansionsDF();
//		exFilter();
//		PrintDic.printSubAcr(words, "wiki/acronymsFinal_B");
		PrintDic.printExpansions(expansions, "wiki/candis_B");
//		PrintDic.printList(waitWords, "wiki/waitWords_A");
	}

	private void exFilter() {
		Iterator<Entry<String, String>> it = words.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			String name = (String) pairs.getKey();
			if(expansions.containsKey(name)){
				ArrayList<String> candis = expansions.get(name);
				for(int i=candis.size()-1; i>=0; i--){
					String candi = candis.get(i);
					if(!isExistWiki(candi)){
						candis.remove(candi);
					}
				}
				if(candis.size() == 0){
					expansions.remove(name);
					it.remove();
					System.out.println("rm acr: " + name);
				}
			}
		}
		
	}

	//for candis already processed with abbrevations.com
	private void GetExpansionsDF() {
		Iterator<Entry<String, String>> it = words.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			String name = (String) pairs.getKey();
			
			int size = 0;
			ArrayList<String> candis = new ArrayList<String>();
			linkfailed = false;
			if(expansions.containsKey(name)){
				candis = expansions.get(name);
				size = candis.size();
//				if(candis.size() != 0 && !isExistWiki(candis.get(0)))
//					candis.remove(0);
				
			}
//			else{
//				candis = getWordExpansion(words.get(name));
//			}
			int count = 0;
			while(!linkfailed && count < 3){
				notExist = false;
				getWordExpansionDF(name, candis);
				if(notExist)
					count++;
				else
					break;
			}
			if(count == 3)
				System.out.println("not exist on fd: " + name);
			
			if(!linkfailed && candis != null && candis.size() != size){
				expansions.put(name, candis);
				System.out.println(name);
				for(String candi : candis)
					System.out.println("ex: " + candi);
			}
			if(linkfailed)
				waitWords.add(name);
		}
		
	}


	//already processed in abbre and fd
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
			if(name.charAt(0) != 'A' && name.charAt(0) != 'a' )
				continue;
			linkfailed = false;
			ArrayList<String> candis = getWordExpansion(words.get(name));
			if(!linkfailed)
				getWordExpansionDF(name, candis);
			if(!linkfailed && candis != null && candis.size()>0) {
				expansions.put(name, candis);
				System.out.println(name);
				for(String candi : candis)
					System.out.println("ex: " + candi);
			}
			if(linkfailed){
				waitWords.add(name);
			}
			if(!linkfailed && (candis == null || candis.size() ==0)){
				it.remove();
				System.out.println("rm acr: "+name);
			}
		}
	}

	private ArrayList<String> getWordExpansion(String link) {
		ArrayList<String> candis = new ArrayList<String>();
		if(link.charAt(0) == '/')
			link = link.substring(1, link.length());
		String curlink = AcronymGenerator.acronymSourceLink+ link + "&p=99999";
//		URL url;
		try {
			Document expansionHtml = Jsoup.connect(curlink).timeout(10000).userAgent("Mozilla").get();
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
//						if(!isExistWiki(curp.text()))
//							break;
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
					if(!isExistedEx(cur, candis))
						candis.add(cur);
				}
				
			}
			
		} catch (SocketTimeoutException ste) {
//			ste.printStackTrace();
			System.out.println("connect abbreviation over time.");
			System.out.println("current link: " + link);
			linkfailed = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			notExist = true;
		}
		
		return candis;
	}
	
	private void getWordExpansionDF(String link, ArrayList<String> candis) {
		if(candis == null){
			candis = new ArrayList<String> ();
		}
		link = transformFormat(link);
		String curlink = "http://acronyms.thefreedictionary.com/"+link;
		try {
			Document expansionHtml = Jsoup.connect(curlink).timeout(10000).userAgent("Mozilla").get();
			Elements tables = expansionHtml.select("table");
			if(tables.size() == 0)
				return;
			boolean hasEx = false;
			Element curTable = null;
			for(Element table: tables){
				if(table.attr("id").equals("AcrFinder")){
					curTable = table;
					break;
				}
			}
			if(curTable == null)
				return;
			Elements rows = curTable.select("tr");
			for(int i=0; i<rows.size(); i++){
				Element row = rows.get(i);
				Elements cols = row.select("td");
				boolean isEx = false;
				for(int j=0; j<cols.size(); j++){
					Element col = cols.get(j);
					if(j == 0 && col.attr("class").equals("acr")){
						isEx = true;
					}
					if(isEx && j>0){
						String exText = col.text();
						Elements spans = cols.select("span");
						if(spans != null){
							for(Element ele: spans){
								exText = exText.replace(ele.text(), "");
							}
						}
						while(exText.charAt(exText.length() - 1) == ' '){
							int len = exText.length();
							exText = exText.substring(0, len-1);
//							System.out.println(exText);
						}
						if(hasNonAscii(exText))
							break;
//						if(!isExistWiki(exText))
//							break;
						if(!isExistedEx(exText, candis))
							candis.add(exText);
					}
				}
			}
		} catch (SocketTimeoutException ste) {
//			ste.printStackTrace();
			System.out.println("connect abbreviation over time.");
			System.out.println("current link: " + link);
			waitWords.add(link);
			linkfailed = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("not exist on fd: " + link);
			
		}
		return;
	}
	
	private boolean isExistedEx(String exText, ArrayList<String> candis) {
		
		String ex = exText.toLowerCase();
		for(String candi:candis){
			if(candi.toLowerCase().equals(ex))
				return true;
		}
//		System.out.println("1: " + exText);
		return false;
	}

	private String transformFormat(String link) {
		link.replace(' ', '+');
		link.replace("#", "%23");
		link.replace("$", "%24");
		link.replace("%", "%25");
		link.replace("&", "%26");
		link.replace("'", "%27");
		link.replace("+", "%2b");
		link.replace(",", "%2c");
		link.replace("/", "%2f");
		link.replace(":", "%3a");
		link.replace(";", "%3b");
		link.replace("=", "%3d");
		link.replace("?", "%3f");
		link.replace("@", "%40");
		link.replace("[", "%5b");
		link.replace("\\", "%5c");
		link.replace("]", "%5d");
		link.replace("^", "%5e");
		link.replace("`", "%60");
		link.replace("{", "%7b");
		link.replace("|", "%7c");
		link.replace("}", "%7d");
		
		return link;
	}

	private boolean isExistWiki(String candi) {
//		candi = candi.toLowerCase();
		String name = candi.replaceAll(" ", "_");
		String link = "http://en.wikipedia.org" + "/wiki/Special:Search/" + name;
		try {
			Document tmpdoc = Jsoup.connect(link).timeout(10000).userAgent("Mozilla").get();
			if(tmpdoc != null){
/*				Elements ps = tmpdoc.select("p");
				for(Element p: ps){
					candi.setText(p.text());
				}*/
//				System.out.println(tmpdoc.title());
				if(tmpdoc.title().contains("Search results"))
					return false;
				return true;
			}
				
		} catch (SocketTimeoutException ste) {
//			ste.printStackTrace();
			System.out.println("connect wiki over time.");
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("not in wiki: "+ candi);
			return false;
		}
		return true;
	}
	
	private boolean hasNonAscii(String s) {
		CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
		if(!asciiEncoder.canEncode(s))
			return true;
		return false;
	}

}
