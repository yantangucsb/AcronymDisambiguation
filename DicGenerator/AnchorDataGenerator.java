package DicGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import TextModel.Candidate;

public class AnchorDataGenerator {
	HashMap<String, ArrayList<String>> expansions;
	ArrayList<WordDic> fullData;
	HashSet<String> titles;
	
	public AnchorDataGenerator() {
		expansions = new HashMap<String, ArrayList<String>>();
		PrintDic.loadExpansions(expansions, "wiki/testData");
		fullData = new ArrayList<WordDic>();
		titles = new HashSet<String>();
	}
	
	public void getAnchorData() {
		
		Iterator<Entry<String, ArrayList<String>>> it = expansions.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			ArrayList<String> candis = (ArrayList<String>) pairs.getValue();
			WordDic wd = new WordDic((String) pairs.getKey());
			for(String candi: candis) {
				Candidate curCandi = new Candidate(candi);
				if(isExistWiki(curCandi, (String)pairs.getKey()))
					wd.add2Expansions(curCandi);
			}
			if(wd.getExpansions().size() != 0)
				fullData.add(wd);
		}
		PrintDic.printAnchorData(fullData, "wiki/anchorData");
	}

	private boolean isExistWiki(Candidate candi, String acr) {
		String name = candi.getName().replaceAll(" ", "_");
		String link = "http://en.wikipedia.org/wiki/Special:Search/" + name;
		try {
			Document tmpdoc = Jsoup.connect(link).userAgent("Mozilla").get();
			if(tmpdoc != null){
				
				if(titles.contains(tmpdoc.title()))
					return false;
				else
					titles.add(tmpdoc.title());
				String[] titleStr = tmpdoc.title().split(" - ");
				candi.setTitle(titleStr[0]);
				Elements ps = tmpdoc.select("p");
				if(ps.size() != 0){
					String fpara = ps.get(0).text();
					if(fpara.contains("may refer to:"))
						return false;
					candi.setPrimeText(fpara);
				}
				for(Element p: ps){
					candi.setText(p.text());
				}
				Elements lis = tmpdoc.select("li");
				for(Element li : lis){
					if(!li.text().equals("What links here"))
						continue;
					Elements as = li.select("a");
					if(as.size() != 0){
						String anchorLink = as.get(0).attr("href");
						if(!getAnchorLinks(candi, anchorLink, acr))
							return false;
					}
				}
				return true;
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}

	private boolean getAnchorLinks(Candidate candi, String anchorLink, String acr) {
		String link = "http://en.wikipedia.org" + anchorLink;
		try {
			Document tmpdoc = Jsoup.connect(link).userAgent("Mozilla").get();
			if(tmpdoc != null){
				Elements uls = tmpdoc.select("ul");
				Element curEle = null;
				for(Element p: uls){
					if(p.attr("id").equals("mw-whatlinkshere-list")){
						curEle = p;
						break;
					}
				}
				Elements lis = curEle.select("li");
				for(Element li : lis){
					String text = li.text();
					if(text.contains("(redirect page)"))
						continue;
					Elements as = li.select("a");
					if(as.size() != 0){
						String curLink = as.get(0).attr("href");
						getAnchorPara(candi, curLink, acr);
					}
				}
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void getAnchorPara(Candidate candi, String curLink, String acr) {
		String link = "http://en.wikipedia.org" + curLink;
		try {
			Document tmpdoc = Jsoup.connect(link).userAgent("Mozilla").get();
			if(tmpdoc != null){
				Elements ps = tmpdoc.select("p");
				for(Element p: ps){
					Elements as = p.select("a");
					boolean containKeyword = false;
					for(Element a : as){
						String title = a.attr("title");
						if(title.equals(candi.getTitle()) || title.equals(acr)){
							containKeyword = true;
							break;
						}
					}
					if(!containKeyword)
						continue;
					String text = p.text();
					candi.setAnchorData(text);
				}
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
		
	}
}
