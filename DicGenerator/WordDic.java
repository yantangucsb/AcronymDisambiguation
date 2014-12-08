package DicGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import Features.*;
import TextModel.Candidate;

public class WordDic{
	private String name;
	private HashMap<String, Candidate> expansions;
	
	public WordDic(String name){
		this.name = name;
		expansions = new HashMap<String, Candidate>();
	}
	
	public WordDic(String text, ArrayList<Candidate> candis) {
		name = text;
		System.out.println(name);
		expansions = new HashMap<String, Candidate>();
		for(int i=0; i<candis.size(); i++){
			if(expansions.containsKey(candis.get(i).getName()))
				continue;
			expansions.put(candis.get(i).getName(), candis.get(i));
			System.out.println(candis.get(i).getName());
		}
//		System.out.println("Successfully");
	}

/*	public boolean getCandidates(Document tmpdoc) {
		try {
			Elements ps = tmpdoc.select("p");
			boolean hascandidate = false;
			for(int i=0; i<ps.size(); i++){
				Element p = ps.get(i);
				String value = p.text();
				if(value.contains("may refer to:")){
					hascandidate = true;
					break;
				}
			}
			if(hascandidate == false)
				return false;
			Elements lis= tmpdoc.select("li");
			System.out.println(lis.size());
			for(int i=0; i<lis.size(); i++){
				Element li = lis.get(i);
				Elements links = li.select("a");
				for(int j=0; j<links.size(); j++){
					Element link = links.get(j);
					String att = link.attr("herf");
					if(att.length() > 6 && att.substring(0, 6).equals("/wiki/")){
						Candidate temp = new Candidate(li.attr("title"));
						System.out.println(li.attr("title"));
						add2Expansions(temp);
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;		
	}*/

	public WordDic(String[] tmp) {
		this.name = tmp[0];
		expansions = new HashMap<String, Candidate>();
		setData(tmp);
	}

	public void add2Expansions(Candidate temp) {
		if(expansions.containsKey(temp.getName()))
			return;
//		temp.getFeature();
		expansions.put(temp.getName(), temp);
		System.out.println(temp.getName() + "added successfully");
	}

	public HashMap<String, Candidate> getExpansions() {
		return expansions;
	}

/*	public Candidate getBestCandi() {
		double max = 0.0;
		Candidate bestCandi = null;
		Iterator<Entry<String, Candidate>> it = expansions.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Candidate candi = (Candidate) pairs.getValue();
			ArrayList<String> features = candi.getFeature();
			double value = Double.parseDouble(features.get(0));
			if(value > max){
				max = value;
				bestCandi = candi;
			}
		}
		return bestCandi;
	}*/

	public String printData() {
		String output = "";
		Iterator<Entry<String, Candidate>> it = expansions.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Candidate candi = (Candidate) pairs.getValue();
			output += name + " ### " + candi.getName() + " ### ";
			output += Integer.toString(candi.getViewNum()) + " ### ";
			output += candi.getPrimeText() + " ### ";
			output += candi.getText();
			output += candi.getAnchorString();
			output += "\n";
			
		}
		return output;
	}
	
	public void setData(String[] tmp) {
		Candidate candi = new Candidate();
		candi.setName(tmp[1]);
		candi.setViewNum(Integer.parseInt(tmp[2]));
		candi.setPrimeText(tmp[3]);
		candi.setText(tmp[4]);
		candi.setAnchorData(tmp[5]);
		expansions.put(candi.getName(), candi);
	}

	public String getName() {
		return name;
	}
}