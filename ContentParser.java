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
	
	ContentParser(){
		wordsparser = new WordsParser();
		
		links = new LinkedList<linkPage>();
		String firstLink ="/wiki/Wikipedia";
		String title = "Wikipedia";
		links.add(new linkPage(firstLink, title));
		
		doc = null;
		linkCount = 0;
	}
	public void getContentDoc(){
		try {
//			Elements el = ;
//			doc.appendChild(el);
			
			while(!links.isEmpty() && wordsparser.trainData.size()<=500){
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
			PrintDic.printTrainData(wordsparser.trainData, "wiki/traindata");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void getTextContent(Document tmpdoc, linkPage curpage){
		try {
			Elements ps= tmpdoc.select("p");
			
/*			Element supEl = subdoc.createElement("textTitle");
			supEl.setAttribute("name", curpage.title);
			System.out.println(curpage.title);
			subdoc.appendChild(supEl);*/
			for(int i=0; i<ps.size(); i++){
//				System.out.println(nl.getLength());
				Element p = ps.get(i);
				String text = p.text();
//				System.out.println(text);
				addlinktoQueue(p);
								
				wordsparser.getWords(text);
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
					linkpage.link = tmpLink;
					String attrTitle = link.attr("title");
					if(attrTitle != null)
						linkpage.title = attrTitle;
					links.add(linkpage);
				}
			}
		}
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

