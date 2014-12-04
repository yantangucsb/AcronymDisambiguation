import TrainDataGenerator.ContentParser;
import DicGenerator.*;

public class GetWebContent {
	public static void main(String[] args) {
 
//		ContentParser cp = new ContentParser();
//		cp.geneTrainData();
//		cp.testTFIDF();
//		AcronymGenerator ag = new AcronymGenerator();
//		ag.getAcronyms();
//		ExpansionGenerator eg = new ExpansionGenerator(3);
		AnchorDataGenerator adg = new AnchorDataGenerator();
		adg.getAnchorData();
	}
}