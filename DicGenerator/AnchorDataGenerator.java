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
import TextModel.TargetText;

public class AnchorDataGenerator {
	HashMap<String, ArrayList<String>> expansions;
	HashMap<String, ArrayList<TargetText>> tts;
	ArrayList<WordDic> fullData;
	HashSet<String> titles;
	
	public AnchorDataGenerator() {
		expansions = new HashMap<String, ArrayList<String>>();
		PrintDic.loadExpansions(expansions, "wiki/test2/testDic");
		fullData = new ArrayList<WordDic>();
		titles = new HashSet<String>();
		tts = new HashMap<String, ArrayList<TargetText>>();

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
			if(wd.getExpansions().size() != 0){
				fullData.add(wd);
				PrintDic.printAnchorData(fullData, "wiki/test2/"+wd.getName());
			}
		}
		PrintDic.printAnchorData(fullData, "wiki/anchorData2");
		PrintDic.printTrainData(tts, "wiki/traindata2");
	}
	
	public void regetAnchorData() {
		PrintDic.loadDic(fullData, "wiki/test2/dic2");
//		PrintDic.loadTrainData(tts, "wiki/traindata2");
		System.out.println(fullData.size());
		
		Iterator<Entry<String, ArrayList<String>>> it = expansions.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			ArrayList<String> candis = (ArrayList<String>) pairs.getValue();
			String name = (String) pairs.getKey();
			WordDic curwd = null;
			for(WordDic wd: fullData){
				if(wd.getName().equals(name)){
					curwd = wd;
					break;
				}
			}
			if(curwd != null)
				continue;
			WordDic wd = new WordDic((String) pairs.getKey());
			for(String candi: candis) {
				Candidate curCandi = new Candidate(candi);
				if(isExistWiki(curCandi, (String)pairs.getKey()))
					wd.add2Expansions(curCandi);
			}
			if(wd.getExpansions().size() != 0){
				fullData.add(wd);
				PrintDic.printAnchorData(fullData, "wiki/test2/tmp1");
				PrintDic.printTrainData(tts, "wiki/test2/trainDataTmp1");
			}
		}
		PrintDic.printAnchorData(fullData, "wiki/anchorData3");
		PrintDic.printTrainData(tts, "wiki/traindata3");
	}
	
	public void generateTrainData() {
		PrintDic.loadDic(fullData, "wiki/test2/dic2");
		for(WordDic wd: fullData) {
			Iterator<Entry<String, Candidate>> it = wd.getExpansions().entrySet().iterator();
		    while (it.hasNext()) {
		    	Map.Entry pairs = (Map.Entry)it.next();
		    	Candidate candi = (Candidate) pairs.getValue();
		    	for(String tt: candi.getAnchorText()) {
		    		add2TrainData(wd.getName(), candi, tt, candi.getName());
		    	}
		    }
		}
		PrintDic.printTrainData(tts, "wiki/test2/trainData");
	}
	
	public void filterDic() {
		String output ="";
		Iterator<Entry<String, ArrayList<String>>> it = expansions.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			ArrayList<String> candis = (ArrayList<String>) pairs.getValue();
			for(String candi: candis) {
				Candidate curCandi = new Candidate(candi);
				String name = getWikiPage(curCandi, (String)pairs.getKey());
				if(name.equals(candi))
					continue;
				String str = (String)pairs.getKey() + " ### " + candi + " ### " + name + '\n';
				output += str;
				System.out.println(str);
			}
		}
		PrintDic.printLog(output, "tmplog");
	}

	private String getWikiPage(Candidate candi, String key) {
		String name = candi.getName().replaceAll(" ", "_");
		String link = "http://en.wikipedia.org/wiki/Special:Search/" + name;
		try {
			Document tmpdoc = Jsoup.connect(link).userAgent("Mozilla").get();
			if(tmpdoc != null){
				
			String[] titleStr = tmpdoc.title().split(" - ");
			Elements ps = getContentText(tmpdoc);
			
			if(ps.size() != 0){
				String fpara = ps.get(0).text();
				if(fpara.contains("may refer to:"))
					return "";
			}
			return titleStr[0];
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		return "";
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
				Elements ps = getContentText(tmpdoc);
				
				if(ps.size() != 0){
					String fpara = ps.get(0).text();
					if(fpara.contains("may refer to:"))
						return false;
					candi.setPrimeText(fpara);
				}
				for(Element p: ps){
					if(p.text().length() == 0)
						continue;
					candi.setText(p.text() + " ");
				}
				Elements lis = tmpdoc.select("li");
				for(Element li : lis){
					if(li.text().equals("What links here")){
						Elements as = li.select("a");
						if(as.size() != 0){
							String anchorLink = as.get(0).attr("href");
							if(!getAnchorLinks(candi, anchorLink, acr))
								return false;
						}
					}
					
					if(li.text().equals("Page information")){
						Elements as = li.select("a");
						if(as.size() != 0) {
							String anchorLink = as.get(0).attr("href");
							if(!getViewNum(candi, anchorLink))
								return false;
						}
						
					}
				}
				return true;
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Get wiki page failure. " + acr + " " + candi.getName());
			return false;
		}
		return false;
	}

	private Elements getContentText(Document tmpdoc) {
		Element content = tmpdoc.select("div[id=mw-content-text]").first();
		Elements ps = content.children();
		Elements output = new Elements();
//		System.out.println(ps.size());
		for(Element ele : ps){
			if(!ele.tagName().equals("p")){
				ele.remove();
				continue;
			}
			Elements eles = ele.getAllElements();
			for(Element e : eles){
				if(!isLegal(e.tagName()))
					e.remove();
			}
			output.add(ele);
		}
		return output;
	}

	//may not be complete
	private boolean isLegal(String tagName) {
		switch(tagName){
		case "p": return true;
		case "a": return true;
		case "b": return true;
		default: return false;
		}
	}

	private boolean getViewNum(Candidate candi, String anchorLink) {
		String link = "http://en.wikipedia.org" + anchorLink;
		try {
			Document tmpdoc = Jsoup.connect(link).userAgent("Mozilla").get();
			if(tmpdoc != null){
				Elements tables = tmpdoc.select("table");
				Element curtable = null;
				for(Element table : tables) {
					if(table.attr("class").equals("wikitable mw-page-info")) {
						curtable = table;
						break;
					}
				}
				Elements trs = curtable.select("tr");
				for(Element tr : trs) {
					if(tr.attr("id").equals("mw-pageinfo-watchers")){
						String str = tr.select("td").get(1).text();
						candi.setViewNum(getNum(str));
					}
				}
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Get wiki page View Num failure. " + candi.getName());
			return false;
		}
		return true;
	}

	private int getNum(String str) {
		String numstr = "";
		for(int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			if(ch >= '0' && ch <= '9'){
				numstr += ch;
			}
		}
		return Integer.parseInt(numstr);
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
					if(!isGoodPage(text))
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
			System.out.println("Get what links here page failure. " + acr + " " + candi.getName());
			return false;
		}
		return true;
	}

	private boolean isGoodPage(String text) {
		String[] strs={"(redirect page)", "User talk:", "Talk:", "File:", "User:", "Wikipedia:"};
		for(String str: strs){
			if(text.contains(str))
				return false;
		}
		return true;
	}

	private void getAnchorPara(Candidate candi, String curLink, String acr) {
		String link = "http://en.wikipedia.org" + curLink;
		try {
			Document tmpdoc = Jsoup.connect(link).userAgent("Mozilla").get();
			if(tmpdoc != null){
				Elements ps = getContentText(tmpdoc);
				for(Element p: ps){
					Elements as = p.select("a");
					boolean containKeyword = false;
					String hyperlinkText = "";
					for(Element a : as){
						String title = a.attr("title");
						if(title.equals(candi.getTitle()) || title.equals(acr)){
							containKeyword = true;
							hyperlinkText = a.text();
							break;
						}
					}
					if(!containKeyword)
						continue;
					String text = p.text();
					candi.setAnchorData(text);
					
					add2TrainData(acr, candi, text, hyperlinkText);
				}
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Get achor data failure. " + acr + " " + candi.getName());
		}
		return;
		
	}
	
	private void add2TrainData(String text, Candidate candi, String para, String hyperlinkText) {
//		para = para.replace(hyperlinkText, text);
//		System.out.println(para);
//		para = para.replace(candi.getTitle(), text);
		para = para.replace(candi.getName(), text);
		if(para.length() == 0)
			return;
		
		TargetText tt = new TargetText(text, candi.getName(), para);
		
		if(tts.containsKey(text)){
			tts.get(text).add(tt);
		}else{
			ArrayList<TargetText> paras = new ArrayList<TargetText>();
			paras.add(tt);
			tts.put(text, paras);
		}
	}

}
