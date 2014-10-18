import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WordsParser{
	String para;
	HashMap<String, WordDic> words;
	int count;
	WordsParser(){
		count=0;
		words = new HashMap<String, WordDic>();
	}
	void getWords(String text){
		String[] tmpwords = text.split(" ");
		for(int i=0; i<tmpwords.length; i++){
			String tmpword = tmpwords[i];
			if(isCapital(tmpword) && tmpword.length()>1){
				WordDic wd = new WordDic(tmpword);
				add2Dic(wd);
//				System.out.println(tmpword);
			}
		}
//		printDic2Console();
	}
	
	public void printDic2Console() {
		Iterator<Entry<String, WordDic>> it = words.entrySet().iterator();
		System.out.println(words.size());
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println(pairs.getKey());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		
	}
	private void add2Dic(WordDic wd) {
		if(words.containsKey(wd.name) == true)
			return;
		words.put(wd.name, wd);
		
	}
	boolean isCapital(String word){
		for(int i=0; i<word.length(); i++){
			if(!Character.isUpperCase(word.charAt(i)))
				return false;
		}
		return true;
	}
	
	void printWords(String filename){
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter( new FileWriter(filename));
		    Iterator<Entry<String, WordDic>> it = words.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        writer.write((String)pairs.getKey());
		        it.remove(); // avoids a ConcurrentModificationException
		    }

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