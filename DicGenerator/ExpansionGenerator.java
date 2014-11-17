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
	boolean needRm;
	
	public ExpansionGenerator(int x) {
		
		words = new HashMap<String, String>();
		expansions = new HashMap<String, ArrayList<String>>();
		waitWords = new ArrayList<String>();
		
		if(x == 0)
			exInitialize();
		if(x == 1)
			exAddFromDF();
		if(x == 2)
			exCheckWiki();
		
		
	}

	private void exInitialize() {
		String filename = "wiki/candisFull_A";
		PrintDic.loadWords(words, "wiki/acronyms");
		PrintDic.loadExpansions(expansions, filename);
		GetExpansions();
//		GetExpansionsDF();
		PrintDic.printSubAcr(words, "wiki/acronyms_A");
		PrintDic.printExpansions(expansions, filename);
		PrintDic.printList(waitWords, "wiki/waitWords_A");
		
	}

	public void exAddFromDF() {
		String filename = "wiki/candisDF_X";
		PrintDic.loadWords(words, "wiki/acronyms_X");
		PrintDic.loadExpansions(expansions, filename);
//		GetExpansions();
		do{
			GetExpansionsDF();
		
//		exFilter();
		PrintDic.printSubAcr(words, "wiki/acronyms_X");
		PrintDic.printExpansions(expansions, "wiki/candisDF_X");
		}while(notExist);
//		PrintDic.printList(waitWords, "wiki/waitWords_A");
	}

	private void exCheckWiki() {
		PrintDic.loadWords(words, "wiki/OriginalAcr/acronyms_Z");
		PrintDic.loadExpansions(expansions, "wiki/candis_Z");
		HashMap<String, ArrayList<String>> expans1 = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> expans2 = new HashMap<String, ArrayList<String>>();
		PrintDic.loadExpansions(expans1, "wiki/candisDF_Z");	
		PrintDic.loadExpansions(expans2, "wiki/candisFull_Z");	
		exFilter(expans1, expans2);
		PrintDic.printSubAcr(words, "wiki/acronymsFinal_Z");
		PrintDic.printExpansions(expansions, "wiki/candis_Z");
		
	}
	
	//combine expansion files and filter
	private void exFilter(HashMap<String, ArrayList<String>> expans1, HashMap<String, ArrayList<String>> expans2) {
		Iterator<Entry<String, String>> it = words.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			String name = (String) pairs.getKey();
			ArrayList<String> candisAll = new ArrayList<String>();
			ArrayList<String> candis1 = new ArrayList<String>();
			ArrayList<String> candis2 = new ArrayList<String>();
			if(expansions.containsKey(name))
				candisAll = expansions.get(name);
			if(expans1.containsKey(name)){
				candis1 = expans1.get(name);
			}
			if(expans2.containsKey(name)){
				candis2 = expans2.get(name);
				for(String candis: candis2){
					if(!isExistedEx(candis, candis1)){
						candis1.add(candis);
					}
				}
			}
			
			do{
				for(int i=candis1.size()-1; i>=0; i--){
					String candi = candis1.get(i);
					if(candisAll.contains(candi))
						continue;
					notExist = false;
					if(!isExistWiki(candi)){
						if(!notExist)
							candis1.remove(candi);
					}else if(!notExist){
						candisAll.add(candi);
						candis1.remove(candi);
					}
					
				}
			}while(candis1.size() != 0);
			if(candisAll.isEmpty()){
				it.remove();
				System.out.println("rm acr: "+ name);
			}else{
				expansions.put(name, candisAll);
				System.out.println(name);
				for(String candi : candisAll)
					System.out.println("ex: " + candi);
			}
		}
		
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

	//for candis already processed with abbrevations.com
	private void GetExpansionsDF() {
		Iterator<Entry<String, String>> it = words.entrySet().iterator();
		notExist = false;
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			String name = (String) pairs.getKey();
			
			int size = 0;
			ArrayList<String> candis = new ArrayList<String>();
			linkfailed = false;
			if(expansions.containsKey(name)){
				continue;
//				candis = expansions.get(name);
//				size = candis.size();
//				if(candis.size() != 0 && !isExistWiki(candis.get(0)))
//					candis.remove(0);
				
			}
//			else{
//				candis = getWordExpansion(words.get(name));
//			}
			if(!linkfailed){
				needRm = false;
				getWordExpansionDF(name, candis);
				
			}
			
			if(!linkfailed && candis != null && candis.size() != size){
				expansions.put(name, candis);
				System.out.println(name);
				for(String candi : candis)
					System.out.println("ex: " + candi);
			}
			if(linkfailed)
				waitWords.add(name);
			if(needRm)
				it.remove();
		}
		if(notExist)
			System.out.println("Need repeat!");
		
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
		String curlink = "http://acronyms.thefreedictionary.com/"+ link +"?hl=en&SearchBy=1";
		try {
			Document expansionHtml = Jsoup.connect(curlink).timeout(10000).userAgent("Mozilla").get();
			if(expansionHtml != null){
				boolean isExist = true;
				Elements metas = expansionHtml.select("META");
				for(Element meta: metas){
					String data = meta.attr("NAME");
					if(data.equals("ROBOTS")){
						isExist = false;
						break;
					}
				}
				if(!isExist){
//					System.out.println("No exist on FD:" + link);
					needRm = true;
					return;
				}
			}
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
			notExist = true;
			
		}
		return;
	}
	
	

	private String transformFormat(String link) {
		link = link.replace("%", "%25");
		link = link.replace("+", "%2b");
		link = link.replace(' ', '+');
		link = link.replace("#", "%23");
		link = link.replace("$", "%24");
		link = link.replace("&", "%26");
		link = link.replace("'", "%27");
		link = link.replace(",", "%2c");
		link = link.replace("/", "%2f");
		link = link.replace(":", "%3a");
		link = link.replace(";", "%3b");
		link = link.replace("=", "%3d");
		link = link.replace("?", "%3f");
		link = link.replace("@", "%40");
		link = link.replace("[", "%5b");
		link = link.replace("\\", "%5c");
		link = link.replace("]", "%5d");
		link = link.replace("^", "%5e");
		link = link.replace("`", "%60");
		link = link.replace("{", "%7b");
		link = link.replace("|", "%7c");
		link = link.replace("}", "%7d");
		
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
			notExist = true;
			return true;
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
