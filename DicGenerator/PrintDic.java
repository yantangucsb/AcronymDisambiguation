package DicGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PrintDic{
	public static Document transformWords2XML(HashMap words){
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
}