package AssemblerDisassembler;
 

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
@SuppressWarnings("unused")
public class JFilePicker extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String textFieldLabel;
    private String buttonLabel;
     
    private JLabel label;
    private JTextField textField;
    private JButton button, to;
     
    private JFileChooser fileChooser;
     
    private int mode;
    public static final int MODE_OPEN = 1;
     
    public JFilePicker(String textFieldLabel, String buttonLabel, String toWhat) {
        this.textFieldLabel = textFieldLabel;
        this.buttonLabel = buttonLabel;
         
        fileChooser = new JFileChooser();
         
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
 
        // creates the GUI
        label = new JLabel(textFieldLabel);
         
        textField = new JTextField(60);
        textField.setColumns(30);
        button = new JButton(buttonLabel);
        to = new JButton(toWhat);
        
         
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                buttonActionPerformed(evt);            
            }
        });
        to.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                convertAction(evt);            
            }
        });
        
         
        add(label);
        add(textField);
        add(button);
        add(to);
        
         
    }
     
    private void buttonActionPerformed(ActionEvent evt) {
        if (mode == MODE_OPEN) {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        }
    }
    private void convertAction(ActionEvent evt) {
    	int event = 0;
        if(evt.getActionCommand().equals("Convert to C++")){
        	event = new Assembler().Assemble(textField.getText());
        			
        	if(event == 0){
        		JOptionPane.showMessageDialog(null, "Converted Successfully");		
        	}else if(event == 1){
        		JOptionPane.showMessageDialog(null, "Error: File Not Found!", "Failure", JOptionPane.ERROR_MESSAGE);	
        	}else if(event == 2){
        		JOptionPane.showMessageDialog(null, "Error: Error Reading File!", "Failure", JOptionPane.ERROR_MESSAGE);	
        	}else if(event == 3){
        		JOptionPane.showMessageDialog(null, "Error: File could not be converted!", "Failure", JOptionPane.ERROR_MESSAGE);
        	}
        		
        }else if(evt.getActionCommand().equals("Convert to Assembly")){
        	//System.out.println("to Assembly");
        	
        	try {
    			converter.convertCPPtoASM(textField.getText());
    			event = 0;
    		} catch (FileNotFoundException e) {
    			event = 1;
    			// TODO Auto-generated catch block
    			//e.printStackTrace();
    		} catch(IOException e){
    			event = 2;
    			//System.out.println("Error reading file!");
    			//return 2;
    		} catch (Exception e){
    			event = 3;
    			//System.out.println("Error");
    			//return 3;
    		}
        	
        	if(event == 0){
        		JOptionPane.showMessageDialog(null, "Converted Successfully");		
        	}else if(event == 1){
        		JOptionPane.showMessageDialog(null, "Error: File Not Found!", "Failure", JOptionPane.ERROR_MESSAGE);	
        	}else if(event == 2){
        		JOptionPane.showMessageDialog(null, "Error: Error Reading File!", "Failure", JOptionPane.ERROR_MESSAGE);	
        	}else if(event == 3){
        		JOptionPane.showMessageDialog(null, "Error: File could not be converted!", "Failure", JOptionPane.ERROR_MESSAGE);
        	}
        }
    }
 
    public void addFileTypeFilter(String extension, String description) {
        FileTypeFilter filter = new FileTypeFilter(extension, description);
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
    }
     
    public void setMode(int mode) {
        this.mode = mode;
    }
     
    public String getSelectedFilePath() {
        return textField.getText();
    }
     
    public JFileChooser getFileChooser() {
        return this.fileChooser;
    }
}
