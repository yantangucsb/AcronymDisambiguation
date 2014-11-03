package DicGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class PrintDic{
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
			Iterator<Entry<String, Candidate>> it2 = wd.expansions.entrySet().iterator();
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
		        writer.write((String)pairs.getKey()+"###"+(String)pairs.getValue()+"\r\n");
		        it.remove(); // avoids a ConcurrentModificationException
		    }
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
	
	public static void loadWords(HashMap<String, String> acronyms) {
		BufferedReader br = null;
	    try {
	    	/*	    	File file = new File("wiki/ori_log");
	    	Document tmpdoc = Jsoup.parse(file, "UTF-8");
	    	Elements eles = tmpdoc.select("acronym");
	    	for(int i=0; i<eles.size(); i++){
	    		Element ele = eles.get(i);
	    		WordDic wd = new WordDic(ele.attr("name"));
	    		Elements features = ele.select("candidate");
	    		for(int j=0; j<features.size(); j++){
	    			Element fea = features.get(j);
	    			Candidate can = new Candidate(fea.attr("name"));
	    			wd.add2Expansions(can);
	    		}
	    		acronyms.put(wd.name, wd);
	    	}*/
	    	br = new BufferedReader(new FileReader("wiki/acronyms"));
//	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	        	String[] tmp = line.split("###");
	        	acronyms.put(tmp[0], tmp[1]);
	            line = br.readLine();
	        }
	        
	    }catch(Exception e){
	    	
	    }finally {
	        try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		
	}
	public static void printTrainData(HashMap<String, ArrayList<String>> trainData, String filename) {
		// TODO Auto-generated method stub
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter( new FileWriter(filename));
		    Iterator<Entry<String, ArrayList<String>>> it = trainData.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        writer.write((String)pairs.getKey());
		        for(String data: (ArrayList<String>)pairs.getValue()){
		        	writer.write("###" + data);
		        }
		        writer.write("\r\n");
		        it.remove(); // avoids a ConcurrentModificationException
		    }
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