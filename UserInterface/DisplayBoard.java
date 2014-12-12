package UserInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

import TextModel.TargetText;
import CandiRankModel.ModelTest;

public class DisplayBoard extends JPanel{
	private JTextArea textArea;

	public DisplayBoard(String labelName) {
		this.setLayout(new BorderLayout());
		JLabel label = new JLabel();
		label.setText(labelName);
		this.add(label, BorderLayout.PAGE_START);
//		label.setLocation(0, 0);
//		label.setSize(label.getPreferredSize());
		AddTextArea();
	}
	
	private void AddTextArea() {
		
		textArea = new JTextArea(32, 31);
		textArea.setLineWrap(true);
		JScrollPane sp = new JScrollPane(textArea);
		this.add(sp, BorderLayout.CENTER);
//		textArea.setLocation(5, 0);
//		textArea.setSize(textArea.getPreferredSize());
	}

	public Dimension setPreferredSize(){
		return new Dimension(350, 300);
	}

	public void setDisplay() throws Exception {
		String strs = "";
		for(String str : ModelTest.testData){
			strs += str +"\n";
		}
		textArea.setText(strs);
//		int size = 0;
		for(int i=0; i<ModelTest.tts.size(); i++) {
			TargetText tt = ModelTest.tts.get(i);
			String text= textArea.getText();
			int index = 0;
			while(index >= 0 && index < text.length()) {
				index = text.indexOf(tt.getName(), index);
				if(index >= 0){
					textArea.getHighlighter().addHighlight(index, index+tt.getName().length(), 
							new DefaultHighlighter.DefaultHighlightPainter(Color.gray));
					index += tt.getName().length();
				}
			}
/*			for(int x: tt.getHighlightIndex()) {
				textArea.getHighlighter().addHighlight(size+x, size+x+tt.getName().length(), 
						new DefaultHighlighter.DefaultHighlightPainter(Color.gray));
			}*/
			if(i != ModelTest.tts.size()-1){
				TargetText next = ModelTest.tts.get(i+1);
				if(tt.getText().equals(next.getText()))
					continue;
			}
//			size += tt.getText().length();
		}
	}

	public void setOutput(String output) {
		textArea.setText(output);
	}

	public void setEditable(boolean value) {
		textArea.setEditable(value);
		
	}
	
	public String getInput() {
		return textArea.getText();
	}

}
