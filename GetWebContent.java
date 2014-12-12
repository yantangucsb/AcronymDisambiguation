import java.awt.*;
import java.awt.event.KeyEvent;

import javax.swing.*;

import TrainDataGenerator.*;
import UserInterface.MainFrame;
import CandiRankModel.*;
import DicGenerator.*;

public class GetWebContent {
	public static void main(String[] args) {
 
//		ContentParser cp = new ContentParser();
//		cp.geneTrainData();
//		cp.testTFIDF();
//		AcronymGenerator ag = new AcronymGenerator();
//		ag.getAcronyms();
//		ExpansionGenerator eg = new ExpansionGenerator(3);
//		AnchorDataGenerator adg = new AnchorDataGenerator();
//		adg.getAnchorParas();
//		adg.generateTrainData();
//		adg.regetAnchorData();
//		adg.filterDic();
		WekaTrainDataGenerator wdg = new WekaTrainDataGenerator();
		wdg.getWekaTrainData();
		wekaModel ml = new wekaModel();
		ml.trainModel();
		
		
//		MainFrame mf = new MainFrame();
//		mf.setVisible(true);
	}
}