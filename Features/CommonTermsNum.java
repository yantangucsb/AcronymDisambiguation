package Features;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import TextModel.Candidate;
import TextModel.TargetText;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.process.PTBTokenizer;
import DicGenerator.WordDic;

public class CommonTermsNum extends Feature{
	double value;

	@Override
	public void setFeature(TargetText targetText, WordDic worddic) {
		// TODO Auto-generated method stub
		
	}
}
