import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import DicGenerator.AcronymGenerator;
import DicGenerator.Candidate;
import DicGenerator.PrintDic;
import DicGenerator.WordDic;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class WordsParser{
	public String para;
	HashMap<String, String> words;
	public HashMap<String, ArrayList<String>> trainData;
	ArrayList<String> stopWord;
	int count;
	WordsParser(){
		count=0;
		words = new HashMap<String, String>();
		trainData = new HashMap<String, ArrayList<String>>();
		PrintDic.loadWords(words);
/*		stopWord = new ArrayList<String>();
		stopWord.add("TV"); //convert to read from file
		stopWord.add("II");
		stopWord.add("III");
		stopWord.add("VI");*/
		
	}
	
	void getWords(String text){
		String[] tmpwords = text.split(" ");
		para = text;
		for(int i=0; i<tmpwords.length; i++){
			String tmpword = tmpwords[i];
			addAcronym(tmpword);
		}
//		printDic2Console();
	}
	
	void addAcronym(String tmpword) {
//		System.out.println(tmpword.charAt(0));
		if(tmpword.length()<=1)
			return;
		if(tmpword.charAt(0) == '(')
			tmpword = tmpword.substring(1);
		if(tmpword.charAt(tmpword.length()-1) == ')')
			tmpword = tmpword.substring(0, tmpword.length()-1);
		if(tmpword.length()<=1)
			return;
/*		if(!isCapital(tmpword))
			return;
		
		if(isStopWord(tmpword))
			return;*/
		if(!words.containsKey(tmpword))
			return;
		add2TrainData(tmpword);
		return;
	}
	
	private boolean isStopWord(String tmpword) {
		if(stopWord.contains(tmpword))
			return true;
		return false;
	}
	
	private String[] sentenceParser(String text) {
		LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
//	    String sent = "This: is an (easy) sentence.";
	    Tree parse = lp.parse(text);
//	    parse.pennPrint();
//	    System.out.println();
	    
	    TreebankLanguagePack tlp = lp.getOp().langpack();
	    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
//	    System.out.println(tdl);
//	    System.out.println();
	    
//	    System.out.println("The words of the sentence:");
	    String[] tmpwords = new String[parse.yield().size()];
	    int i=0;
	      for (Label lab : parse.yield()) {
	        
//	          System.out.println(lab.toString());
	    	  String tmpword = lab.toString();
	    	  tmpwords[i++] = tmpword;
	    	  
	       
	      }
//	      System.out.println();
	      
//	      System.out.println(parse.taggedYield());
//	      System.out.println();
		return tmpwords;
	}
/*	public void printDic2Console() {
		Iterator<Entry<String, WordDic>> it = words.entrySet().iterator();
		System.out.println(words.size());
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println(pairs.getKey());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
	}*/
	
	private void add2TrainData(String wd) {
		if(trainData.containsKey(wd)){
			trainData.get(wd).add(para);
		}else{
			ArrayList<String> paras = new ArrayList<String>();
			paras.add(para);
			trainData.put(wd, paras);
		}
	}
	private void getExpansions(String wd) {
		try {
			String curlink = AcronymGenerator.acronymSourceLink + words.get(wd) + "&p=99999";
			Document expansionDoc = Jsoup.connect(curlink).userAgent("Mozilla").get();
			Elements tables = expansionDoc.select("table");
//			System.out.println(tables.size());
			if(tables.size() == 0)
				return;
			WordDic wordDic = new WordDic(wd);
			
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
					wordDic.expansions.put(cur.getName(), cur);
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return;
		}		
	}
	
	private boolean hasNonAscii(String s) {
		CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
		if(!asciiEncoder.canEncode(s))
			return true;
		return false;
	}	
	
}