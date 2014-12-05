package AssemblerDisassembler;
import java.io.*;

public class Assembler {
  private String filepath; //file path of the file
	private String content; //content of the file
	private String newContent; //converted content
	private int currentLine; //indicates the current line being manipulated
	private int range; // range of the if-else
	private String[] splittedContent; //splitted content of the file (line by line)
	private boolean isIf;
	/**
	 * Constructor
	 */
	public Assembler(){
		this.filepath = "";
		this.content = "";
		this.newContent = "";
		this.currentLine = 0;
		this.range = 0;
		this.splittedContent = null;
		this.isIf = true;
	}
		
		
		
	//Getter and Setters
	private void setFilePath(String filepath){
		this.filepath = filepath;
	}
	//gets the file path of the file to be opened
	private String getFilePath(){
		return this.filepath;
	}
	//appends a string to content variable
	private void appendContent(String content){
		this.content += content;
		this.content += "\n";
	}
	//returns the value of the content variable
	private String getContent(){
		return this.content;
	}
	//append String to the converted content
	private void appendNewContent(String newContent){
		this.newContent += newContent;
		this.newContent += "\n";
	}
	//sets new content
	private void setNewContent(String newContent){
		this.newContent = newContent;
	}
	
	//gets the value of the new content variable
	private String getNewContent(){
		return this.newContent;
	}
	//sets the current line being evaluated in the code
	private void setCurrentLine(int i){
		this.currentLine = i;
	}
	//sets the range for the if else to determine whether is else-if
	private void setRange(int range){
		this.range = range;
	}
	private int getRange(){
		return this.range;
	}
	//returns the value of the currentLine variable
	private int getCurrentLine(){
		return this.currentLine;
	}
	private void setIsIf(boolean isIf){
		this.isIf = isIf;
	}
	private boolean getIsIf(){
		return this.isIf;
	}
	//splits the content variable and save to splittedContent variable
	private void setSplittedContent(String content){
		this.splittedContent = content.split("\n");
	}
	//gets the value of the variable splitted content
	private String[] getSplittedContent(){
		return this.splittedContent;
	}

}
