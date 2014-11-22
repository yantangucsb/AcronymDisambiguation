package TrainDataGenerator;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;









import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import DicGenerator.PrintDic;
import DicGenerator.linkPage;

public class ContentParser{
	public LinkedList<linkPage> links;
	public Document doc;
	static String linkHead = "http://en.wikipedia.org";
	int linkCount;
	WordsParser wordsparser;
	FeatureGenerator fg;
	
	public ContentParser(){
		wordsparser = new WordsParser();
		
		links = new LinkedList<linkPage>();
		String firstLink ="/wiki/GNU_General_Public_License";
		String title = "Wikipedia";
		links.add(new linkPage(firstLink, title));
		fg = new FeatureGenerator();
		
		doc = null;
		linkCount = 0;
	}
	public void geneTrainData(){
		try {
//			Elements el = ;
//			doc.appendChild(el);
			
			while(!links.isEmpty() && wordsparser.trainData.size()<=10){
				linkPage curpage = links.poll();
				URL url = new URL(linkHead+curpage.link);
//				URLConnection conn = url.openConnection();
				Document tmpdoc = Jsoup.parse(url, 10000);
//				Document subdoc = dBuilder.newDocument();
//				if(linkCount ==1 )
//					System.out.println(curpage.link);
				getTextContent(tmpdoc, curpage);
				
				linkCount++;
//				System.out.println(linkCount);

			}
//			wordsparser.printWords("wiki/acronymDic");
//			wordsparser.printDic2Console();
//			PrintDic.printXML2file(wordsparser.words, "wiki/acronymDic");
			wordsparser.printTrainData();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void testTFIDF(){
		fg.getBestCandi(wordsparser.expansions, wordsparser.trainData);
	}
	public void getTextContent(Document tmpdoc, linkPage curpage){
		try {
			Elements ps= tmpdoc.select("p");
			for(int i=0; i<ps.size(); i++){
//				System.out.println(nl.getLength());
				Element p = ps.get(i);
				addlinktoQueue(p);
								
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void addlinktoQueue(Element p){
		Elements tmplinks = p.select("a");
		for(int i=0; i<tmplinks.size(); i++){
			Element link = tmplinks.get(i);
			String tmpLink = link.attr("href");
			if(tmpLink != null){
				linkPage linkpage = new linkPage();
				if(isEntity(tmpLink)){
					links.add(linkpage);
					
					linkpage.link = tmpLink;
/*					String attrTitle = link.attr("title");
					if(attrTitle != null)
						linkpage.title = attrTitle;*/
					
					//get its expansion through the link
					String ex = getCurEx(tmpLink);
					if(ex.length() == 0)
						continue;
					
					wordsparser.getWords(link.text(), ex, p.text());
				}
			}
		}
	}
	
	private String getCurEx(String tmpLink) {
		String curlink = linkHead + tmpLink;
		try {
			Document expansionHtml = Jsoup.connect(curlink).timeout(10000).userAgent("Mozilla").get();
			String title = expansionHtml.title();
			if(title != null){
				String[] titlePieces = title.split(" - ");
				return titlePieces[0];
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private boolean isEntity(String tmpLink) {
		if(tmpLink.length() > 6 && (tmpLink.substring(0, 6)).equals("/wiki/")){
			if(tmpLink.length() > 10 && (tmpLink.substring(0, 10).equals("/wiki/File") || tmpLink.substring(0, 10).equals("/wiki/Help")))
				return false;
			return true;
		}
		return false;
	}
}

