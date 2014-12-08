package UserInterface;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import CandiRankModel.ModelTest;

public class MainFrame extends JFrame implements ActionListener{
	private JMenuBar menuBar;
	private DisplayBoard inputBoard, outputBoard;
	JMenuItem loadModel;
	JMenuItem runModel;
	JMenuItem loadTestFile;
	JMenuItem showDic;

	public MainFrame() {
		init();
	}

	private void init() {
		this.setLayout(null);
		this.setTitle("Arcnym Disambiguity Tool");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		menuBar = new JMenuBar();
		addMenu();
		this.setJMenuBar(menuBar);
		inputBoard = new DisplayBoard("Input:");
		outputBoard = new DisplayBoard("Output:");
		this.add(inputBoard);
		inputBoard.setLocation(10, 10);
		inputBoard.setSize(inputBoard.getPreferredSize());
		this.add(outputBoard);
		outputBoard.setLocation(10+ this.getWidth()/2, 10);
		outputBoard.setSize(outputBoard.getPreferredSize());
		outputBoard.setEditable(false);
		this.getContentPane().validate();
		this.getContentPane().repaint();
		
	}
	
	private void addMenu() {
		JMenu menu1 = new JMenu("Load Model");
		JMenu menu2 = new JMenu("Test Model");
		JMenu menu3 = new JMenu("Info");
		menuBar.add(menu1);
		menuBar.add(menu2);
		menuBar.add(menu3);
		
		loadModel = new JMenuItem("Open File");
//		JMenuItem GeneTrainData = new JMenuItem("Generate Train Data");
		runModel = new JMenuItem("Run Model");
		runModel.setEnabled(false);
		loadTestFile = new JMenuItem("Load Test Data");
		loadTestFile.setEnabled(false);
		runModel.setEnabled(false);
		menu1.add(loadModel);
		loadModel.addActionListener(this);
		menu2.add(loadTestFile);
		menu2.add(runModel);
		loadTestFile.addActionListener(this);
		runModel.addActionListener(this);
		
		showDic = new JMenuItem("Show Dic");
		menu3.add(showDic);
		showDic.addActionListener(this);
		showDic.setEnabled(false);

	}

	@Override
	public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Open File")){
			System.out.println(e.getActionCommand()+" selected");
			loadModel();
			runModel.setEnabled(true);
			loadTestFile.setEnabled(true);
			showDic.setEnabled(true);
		}else if(e.getActionCommand().equals("Load Test Data")){
			loadData();
			
		}else if(e.getActionCommand().equals("Run Model")){
			try {
				runModel();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(e.getActionCommand().equals("Show Dic")) {
			System.out.println(e.getActionCommand()+" selected");
			showDic();
		}
		
	}

	private void showDic() {
		String output = ModelTest.showDic();
		outputBoard.setOutput(output);
	}

	private void runModel() throws Exception {
		String output = ModelTest.runModel(inputBoard.getInput());
		inputBoard.setDisplay();
		outputBoard.setOutput(output);
	}

	private void loadData() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			System.out.println(file.getName() + ' ' + file.getPath());
			try {
				String str = ModelTest.loadTestData(file.getPath());
				inputBoard.setOutput(str);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void loadModel() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			System.out.println(file.getName() + ' ' + file.getPath());
			try {
				ModelTest.loadModel(file.getPath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
