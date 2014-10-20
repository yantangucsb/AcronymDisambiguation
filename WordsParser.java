import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class WordsParser{
	String para;
	HashMap<String, WordDic> words;
	ArrayList<String> stopWord;
	int count;
	WordsParser(){
		count=0;
		words = new HashMap<String, WordDic>();
		loadAcronyms();
		stopWord = new ArrayList<String>();
		stopWord.add("TV"); //convert to read from file
		stopWord.add("II");
		stopWord.add("III");
		stopWord.add("VI");
		
	}
	private void loadAcronyms() {
		BufferedReader br = null;
	    try {
	    	br = new BufferedReader(new FileReader("wiki/acronymDic"));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        WordDic tmpword = new WordDic(line);
	        add2Dic(tmpword);

	        while (line != null) {
	            sb.append(line);
//	            sb.append(System.lineSeparator());
	            WordDic tmpword1 = new WordDic(line);
		        add2Dic(tmpword1);
	            line = br.readLine();
	        }
//	        String everything = sb.toString();
//	        System.out.print(everything);
	        
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
	void getWords(String text){
		String[] tmpwords = text.split(" ");
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
		if(!isCapital(tmpword))
			return;
		
		if(isStopWord(tmpword))
			return;
		WordDic wd = new WordDic(tmpword);
		add2Dic(wd);
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
		        writer.write((String)pairs.getKey()+"\r\n");
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