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

public class WordDic{
	private String name;
	private HashMap<String, Candidate> expansions;
	private ArrayList<Candidate> trainText;
	private ArrayList<Feature> features;
	private ArrayList<String> trainDataWeka;
	
	public WordDic(String name){
		this.name = name;
		expansions = new HashMap<String, Candidate>();
		trainText = new ArrayList<Candidate>();
		features = new ArrayList<Feature>();
		addExistFeatures();
	}
	
	private void addExistFeatures() {
		features.add(new CommonTermsNum());
		features.add(new TFIDFsim());
	}

	public WordDic(String text, ArrayList<Candidate> candis) {
		name = text;
		System.out.println(name);
		expansions = new HashMap<String, Candidate>();
		for(int i=0; i<candis.size(); i++){
			if(expansions.containsKey(candis.get(i).name))
				continue;
			expansions.put(candis.get(i).name, candis.get(i));
			System.out.println(candis.get(i).name);
		}
//		System.out.println("Successfully");
	}

	public boolean getCandidates(Document tmpdoc) {
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
	}

	public void add2Expansions(Candidate temp) {
		if(expansions.containsKey(temp.name))
			return;
		temp.getFeature();
		expansions.put(temp.name, temp);		
	}

	public void setTrainText(ArrayList<Candidate> trainpara) {
		trainText = trainpara;
		
	}

	public HashMap<String, Candidate> getExpansions() {
		return expansions;
	}

	public void getfeatures() {
		for(Candidate trainPara : trainText) {
			Iterator<Entry<String, Candidate>> it = expansions.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, Candidate> pairs = it.next();
				Candidate candi = pairs.getValue();
				for(Feature f: features){
					f.setFeature(trainPara.text, candi.text, expansions);
				}
				String isSame = (trainPara.name.equals(candi.name))? "Y":"N";
				set2WekaData(isSame);
			}
		}
	}
	
	private void set2WekaData(String isSame) {
		String trainLine = "";
		for(Feature f: features) {
			trainLine +=f.getfeatureString() + ' ';
		}
		trainLine +=isSame;
		trainDataWeka.add(trainLine);
	}

	private String setTagger(String text) {
		 MaxentTagger tagger = new MaxentTagger("taggers/left3words-distsim-wsj-0-18.tagger");
		 String tagged = tagger.tagString(text);
		 return tagged;
//		 System.out.println(tagged);
	}
}