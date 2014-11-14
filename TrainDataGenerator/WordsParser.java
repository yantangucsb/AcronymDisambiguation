package TrainDataGenerator;
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

import TextModel.Candidate;
import DicGenerator.AcronymGenerator;
import DicGenerator.PrintDic;
import DicGenerator.WordDic;
import DicGenerator.linkPage;


public class WordsParser{
	public String para;
	HashMap<String, String> words;
	public HashMap<String, ArrayList<Candidate>> trainData;
	ArrayList<String> stopWord;
	
	WordsParser(){
		words = new HashMap<String, String>();
		trainData = new HashMap<String, ArrayList<Candidate>>();
		PrintDic.loadWords(words, "wiki/acronyms");
		PrintDic.loadTrainData(trainData);
/*		stopWord = new ArrayList<String>();
		stopWord.add("TV"); //convert to read from file
		stopWord.add("II");
		stopWord.add("III");
		stopWord.add("VI");*/
		
	}
	
	void getWords(String text, String title, String string){
		if(!words.containsKey(text))
			return;
		Candidate trainpara = new Candidate(title, string);
		add2TrainData(text, trainpara);
		System.out.println("find a papra:" + text +"    "+ string);
	}
	
/*	void addAcronym(String tmpword) {
		System.out.println(tmpword.charAt(0));
		if(tmpword.length()<=1)
			return;
		if(tmpword.charAt(0) == '(')
			tmpword = tmpword.substring(1);
		if(tmpword.charAt(tmpword.length()-1) == ')')
			tmpword = tmpword.substring(0, tmpword.length()-1);
		if(tmpword.length()<=1)
			return;
		if(!isCapital(tmpword))
			return;
		
		if(isStopWord(tmpword))
			return;
		if(!words.containsKey(tmpword))
			return;
		add2TrainData(tmpword);
		return;
	}
	
	private boolean isStopWord(String tmpword) {
		if(stopWord.contains(tmpword))
			return true;
		return false;
	}*/
	
	
/*	public void printDic2Console() {
		Iterator<Entry<String, WordDic>> it = words.entrySet().iterator();
		System.out.println(words.size());
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println(pairs.getKey());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
	}*/
	
	private void add2TrainData(String text, Candidate candi) {
		if(trainData.containsKey(candi)){
			trainData.get(candi).add(candi);
		}else{
			ArrayList<Candidate> paras = new ArrayList<Candidate>();
			paras.add(candi);
			trainData.put(text, paras);
		}
	}

	public void printTrainData() {
		PrintDic.printTrainData(trainData);		
	}
}