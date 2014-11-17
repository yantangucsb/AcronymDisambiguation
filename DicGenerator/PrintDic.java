package DicGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

public class PrintDic{
	static String stopWordFile = "wiki/stopwords";
	static String trainDatafilename = "wiki/traindata";
	
	public static Document transformWords2XML(HashMap<String, WordDic> words){
		Document doc = Jsoup.parse("");
		doc.html("");
		
		Element e = doc.appendElement("body");
		Iterator<Entry<String, WordDic>> it = words.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry pairs = (Map.Entry)it.next();
			Element ac = e.appendElement("acronym");
			ac.attr("name", (String)pairs.getKey());
			WordDic wd = (WordDic)pairs.getValue();
			Iterator<Entry<String, Candidate>> it2 = wd.getExpansions().entrySet().iterator();
			while(it2.hasNext()){
				Map.Entry<String, Candidate> pairs2 = (Map.Entry<String, Candidate>) it2.next();
				Element candidate = ac.appendElement("candidate");
				candidate.attr("name", (String)pairs2.getKey());
				//add feature
			}
		}
	    return doc;
	}
	
	public static void printXML2file(HashMap words, String filename) {
		Document doc = transformWords2XML(words);
		
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter( new FileWriter(filename));
		    writer.write(doc.toString());
		    System.out.println("Success to XML file");
		    writer.close();

		}
		catch ( Exception e)
		{
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		    }
		    catch ( Exception e)
		    {
		    }
		}		
	}
	public static void print2OriFile(HashMap words, String filename){
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter( new FileWriter(filename));
		    Iterator<Entry<String, WordDic>> it = words.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        writer.write((String)pairs.getKey()+" ### "+(String)pairs.getValue()+"\r\n");
//		        it.remove(); // avoids a ConcurrentModificationException
		    }
		    writer.close();
		    System.out.println("Success to file");

		}
		catch ( Exception e)
		{
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		    }
		    catch ( Exception e)
		    {
		    }
		}
	}
	
	public static void loadWords(HashMap<String, String> acronyms, String filename) {
		BufferedReader br = null;
	    try {
	    	br = new BufferedReader(new FileReader(filename));
//	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	        	String[] tmp = line.split(" ### ");
	        	int i = 0;
	        	for(; i<tmp[i].length(); i++){
	        		if(tmp[1].charAt(i) == '#'){
	        			tmp[0] += '#';
	        			continue;
	        		}
	        		break;
	        	}
	        	tmp[1].substring(i);
	        	acronyms.put(tmp[0], tmp[1]);
	            line = br.readLine();
	        }
	        br.close();
	        
	    }catch(Exception e){
	    	System.out.println("load words failed!");
	    }
		
	}
	public static void printTrainData(HashMap<String, ArrayList<Candidate>> trainData) {
		// TODO Auto-generated method stub
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter( new FileWriter(trainDatafilename));
		    Iterator<Entry<String, ArrayList<Candidate>>> it = trainData.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        writer.write((String)pairs.getKey());
		        ArrayList<Candidate> candis = (ArrayList<Candidate>)pairs.getValue();
		        for(Candidate data: candis){
		        	writer.write(" ### " + data.getName() + " ### " + data.getText() + '\n');
		        }
//		        writer.write("\r\n");
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		    writer.close();
		    System.out.println("Success to file");

		}
		catch ( Exception e)
		{
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		    }
		    catch ( Exception e)
		    {
		    }
		}
	}
	
	public static void loadTrainData(HashMap<String, ArrayList<Candidate>> trainData) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		try
		{
			br = new BufferedReader( new FileReader(trainDatafilename));
		    String line = br.readLine();
	        while (line != null) {
	        	String[] tmp = line.split("###");
	        	ArrayList<Candidate> al = new ArrayList<Candidate>();
	        	Candidate candi = new Candidate(tmp[1], tmp[2]);
	        	if(trainData.containsKey(tmp[0])){
	        		trainData.get(tmp[0]).add(candi);
	        	}else{
	        		ArrayList<Candidate> paras = new ArrayList<Candidate>();
	        		paras.add(candi);
	        		trainData.put(tmp[0], paras);
	        	}
	            line = br.readLine();
	        }
	        br.close();
	        System.out.println("Load training data successfully.");

		}
		catch ( Exception e)
		{
			System.out.println("open training data file failed.");
		}
		finally
		{
		    try
		    {
		        if ( br != null)
		        br.close( );
		    }
		    catch ( Exception e)
		    {
		    }
		}
	}
	
	public static ArrayList<String> loadStopWords() {
		BufferedReader br = null;
		ArrayList<String> stopWords = new ArrayList<String>();
		try
		{
			br = new BufferedReader( new FileReader(stopWordFile));
		    String line = br.readLine();
	        while (line != null) {
	        	stopWords.add(line);
	            line = br.readLine();
	        }
	        br.close();

		}
		catch ( Exception e)
		{
		}
		return stopWords;
	}
	
	public static void transformFileFormat() {
		BufferedWriter writer = null;
		
		try{
			File file = new File("wiki/candidate_a");
		    writer = new BufferedWriter( new FileWriter("wiki/candis_A"));
	    	Document tmpdoc = Jsoup.parse(file, "UTF-8");
	    	Elements eles = tmpdoc.select("acronym");
	    	for(int i=0; i<eles.size(); i++){
	    		Element ele = eles.get(i);
	    		writer.write(ele.attr("name"));
	    		Elements features = ele.select("candidate");
	    		for(int j=0; j<features.size(); j++){
	    			Element fea = features.get(j);
	    			writer.write("###"+fea.attr("name"));
	    		}
	    		writer.write("\r\n");;
	    	}
	    	writer.close();
		}catch(Exception e){
			System.out.println("Load expansion file failed.");
		}
	}

	public static void loadExpansions(HashMap<String, ArrayList<String>> expansions, String filename) {
		BufferedReader br = null;
		try
		{
			br = new BufferedReader( new FileReader(filename));
		    String line = br.readLine();
	        while (line != null) {
	        	String[] tmp = line.split(" ### ");
/*	        	System.out.println("Ac:" + tmp[0]);
	        	for(int i=1; i<tmp.length; i++){
	        		System.out.println(tmp[i]);
	        	}*/
	        	ArrayList<String> al = new ArrayList<String>();
	        	for(int i=1; i<tmp.length; i++){
	        		if(!al.contains(tmp[i]))
	        			al.add(tmp[i]);
	        	}
	        	expansions.put(tmp[0], al);
	            line = br.readLine();
	        }
	        br.close();
		}catch(Exception e){
			System.out.println("Load expansion file candis_A failed.");
		}
	}

	public static void printExpansions(
			HashMap<String, ArrayList<String>> expansions, String filename) {
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(filename));
			Iterator<Entry<String, ArrayList<String>>> it = expansions.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        writer.write((String)pairs.getKey());
		        ArrayList<String> candis = (ArrayList<String>) pairs.getValue();
		        for(String candi: candis)
		        	writer.write(" ### " + candi);
		        writer.write("\r\n");
		    }
	    	writer.close();
		}catch(Exception e){
			System.out.println("Open and write expansion file candis_A failed.");
		}
		
	}

	public static void printList(ArrayList<String> waitWords, String filename) {
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(filename));
			for(String ww : waitWords) {
				writer.write(ww + "\r\n");
			}
			writer.close();
		}catch(Exception e){
			System.out.println("Open and write waitWords file candis_A failed.");
		}
		
	}

	public static void printSubAcr(HashMap<String, String> words, String filename) {
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter( new FileWriter(filename));
		    Iterator<Entry<String, String>> it = words.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        String name = (String) pairs.getKey();
//		        if(name.charAt(0) == 'C' || name.charAt(0) == 'a')
		        	writer.write((String)pairs.getKey()+" ### "+(String)pairs.getValue()+"\r\n");
//		        it.remove(); // avoids a ConcurrentModificationException
		    }
		    writer.close();
		    System.out.println("Success to file");

		}
		catch ( Exception e)
		{
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		    }
		    catch ( Exception e)
		    {
		    }
		}
		
	}
}