package DicGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AcronymGenerator{
//	HashMap<String, WordDic> acronyms;
	HashMap<String, String> acronyms;
	public static String acronymSourceLink="http://www.abbreviations.com/";
	public AcronymGenerator(){
//		acronyms = new HashMap<String, WordDic>();
//		loadWords();
		acronyms = new HashMap<String, String>();
	}
	
	public void getAcronyms(){
		try {
			LinkedList<String> links = new LinkedList<String>();
			for(char i='A'; i<='Z'; i++){
				String tmp = acronymSourceLink + "abbreviations/" + i + "/99999";
				links.add(tmp);
			}
			int count = 0;
			while(!links.isEmpty()){
				String curpage = links.poll();
				URL url = new URL(curpage);
				Document tmpdoc = Jsoup.parse(url, 10000);
				scanWords(tmpdoc);
				count++;
			}
			PrintDic.print2OriFile(acronyms, "wiki/ori_log");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void scanWords(Document tmpdoc) {
//		LinkedList<linkPage> wordlinks = new LinkedList<linkPage>();
		Elements tables = tmpdoc.select("table");
		Element curtable = tables.get(0);
		Elements wordsEle = curtable.select("a");
		for(int i=0; i<wordsEle.size(); i++){
			Element curlink = wordsEle.get(i);
//			linkPage lp = new linkPage(acronymSourceLink+curlink.attr("href"), curlink.text());
			if(curlink.text().length() == 1)
				continue;
			
			if(hasNonAscii(curlink.text()))
				continue;
			if(numofChar(curlink.text()) <= 1)
				continue;
			if(acronyms.containsKey(curlink.text()))
				continue;
/*			ArrayList<Candidate> candis= getWordExpansion(curlink.attr("href"));
			if(candis !=null && candis.size() != 0)
				add2Dic(new WordDic(curlink.text(), candis));*/
			add2Dic(curlink.text(), curlink.attr("href"));
			System.out.println(curlink.text() + "+" + curlink.attr("href"));
		}
	}
	
	private int numofChar(String text) {
		int count = 0;
		for(int i=0; i<text.length(); i++){
			char ch = text.charAt(i);
			if((ch>='a' && ch<='z') || (ch>='A' && ch<='Z') || (ch>='0' && ch<='9'))
				count++;
		}
		return count;
	}
	private boolean hasNonAscii(String s) {
		CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
		if(!asciiEncoder.canEncode(s))
			return true;
		return false;
	}
/*	private void add2Dic(WordDic wd) {
		acronyms.put(wd.name, wd);
	}*/
	private void add2Dic(String text, String link){
		if(acronyms.containsKey(text))
			return;
		acronyms.put(text, link);
	}
	/*
	private ArrayList<Candidate> getWordExpansion(String link) {
		ArrayList<Candidate> candis = new ArrayList<Candidate>();
		int index = 0;
		if(link.charAt(0) == '/')
			link = link.substring(1, link.length());
		String curlink = acronymSourceLink+ link + "&p=99999";
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
				if(!isExistWiki(candis.get(i).name)){
					candis.remove(i);
				}
				if(candis.size() == 1)
					break;
			}
			
		}
		return candis;
	}
	*/
/*	private boolean isExistWiki(String text) {
		text.replaceAll(" ", "_");
		String link = "http://en.wikipedia.org" + "/wiki/" + text;
		try {
			Document tmpdoc = Jsoup.connect(link).userAgent("Mozilla").get();
			if(tmpdoc != null)
				return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return false;
		}
		return false;
	}*/
}