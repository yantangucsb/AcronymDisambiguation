package CandiRankModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import Features.Feature;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Attribute;

public class wekaModel {
	public NaiveBayes nB;
	public Instances train;
	
	
	public wekaModel() {
		nB = new NaiveBayes();
	}
	
	public void trainModel() {
		try {
			loadTrainData();
			
			nB.buildClassifier(train);
			Evaluation eval = new Evaluation(train);
			eval.crossValidateModel(nB, train, 10, new Random(1));
			System.out.println(eval.toSummaryString("\nResults\n*********\n", true));
			System.out.println("f-measure: " + eval.fMeasure(1));
			System.out.println("precision: " + eval.precision(1));
			System.out.println("recall: " + eval.recall(1));
			
			ArrayList<String> inst = new ArrayList<String>();
			inst.add(" ");
			inst.add("50.238");
			inst.add("300");
			inst.add("5");
			classcifyInstantce(inst);
/*			eval.evaluateModel(nB, train);
			System.out.println(eval.toSummaryString("\nResults\n*********\n", true));*/
			saveModel(nB, "wekaModel/nb2.model");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void loadModel(String filename) throws Exception {

	    FileInputStream fis = new FileInputStream(filename);
	    ObjectInputStream ois = new ObjectInputStream(fis);

	    nB = (NaiveBayes) ois.readObject();
	    loadTrainData();
	    ois.close();
	}
	
	private void loadTrainData() {
		BufferedReader breader = null;
		try {
			breader = new BufferedReader(new FileReader("wiki/test2/weka.arff"));
			train = new Instances(breader);
			train.setClassIndex(train.numAttributes() - 1);
			
	/*			int size = train.numInstances() / 10;
			int begin = 0;
			int end = size - 1;
			for(int i = 1; i <= 10; i++) {
				System.out.println("Iteration: " + i);
				Instances trainingInstances = new Instances(train);
				Instances testingInstances = new Instances(train, begin, (end - begin));
				for(int j=0; j<(end-begin); j++) {
					trainingInstances.delete(begin);
				}
			}*/
			breader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try
		    {
		        if ( breader != null)
		        breader.close( );
		    }
		    catch ( Exception e)
		    {
		    }
		}
		
		
	}

	private void saveModel(NaiveBayes c, String filename) throws Exception {


	    File file = new File(filename);
	    ObjectOutputStream oos = null;
	    if(!file.exists()) {
	    	file.createNewFile();
	    }
	    
	    try {
	        oos = new ObjectOutputStream(
	                new FileOutputStream(file));

	    } catch (Exception e1) {
	        e1.printStackTrace();
	    }
	    oos.writeObject(c);
	    oos.flush();
	    oos.close();

	}

	public double classcifyInstantce(ArrayList<String> str) throws Exception {
/*		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		ArrayList<String> classVal = new ArrayList<String>();
		classVal.add("yes");
		classVal.add("no");
		for(Feature f: ModelTest.features) {
			atts.add(new Attribute(f.getName()));
		}
		atts.add(new Attribute("candiMatch", classVal));
		
		Instances dataRaw = new Instances("TestInstances", atts, 0);
		double[] inst = new double[dataRaw.numAttributes()];
		int i = 0;
		for(i=0; i<atts.size()-1; i++){
			inst[i] = Double.parseDouble(str.get(i+1));
		}
		inst[i] = 0;
		dataRaw.add(new DenseInstance(1.0, inst));
		dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
		double classifIndex = nB.classifyInstance(dataRaw.firstInstance());
		double[] classif = nB.distributionForInstance(dataRaw.firstInstance());
		for(double x: classif)
			System.out.println(x);
		System.out.println(classifIndex);
		return (int)classif[0];*/
		Instance inst = new DenseInstance(4);
		int i=0;
		for(i=0; i<train.numAttributes()-1; i++) {
			inst.setValue(i, Double.parseDouble(str.get(i+1)));
		}
		inst.setValue(i, 0);
		train.add(inst);
		double classifIndex = nB.classifyInstance(train.lastInstance());
		double[] classif = nB.distributionForInstance(train.lastInstance());
		for(double x: classif)
			System.out.println(x);
		System.out.println(classifIndex);
		return classif[0];
	}
}
