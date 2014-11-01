package DicGenerator;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class WordDic{
	public String name;
	public HashMap<String, Candidate> expansions;
	public WordDic(String name){
		this.name = name;
		expansions = new HashMap<String, Candidate>();
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
}