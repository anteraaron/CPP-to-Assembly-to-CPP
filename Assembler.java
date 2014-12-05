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
		
	
	/**
	 * The Function which is called when converting to C++
	 * Contains opening of file, writing to file and calling the process that converts the assembly file
	 * @param filepath
	 * @return 
	 */
	public int Assemble(String filepath){
		setFilePath(filepath);
		System.out.println(filepath);
		String line = null;
		String content = "";
		try {
			@SuppressWarnings("resource")
			BufferedReader inputStream = new BufferedReader(new FileReader(getFilePath()));
			//reads the file content and save its content.
			while(true){
				line = inputStream.readLine();
				if(line == null){
					break;
				}
				appendContent(line);
			}
			
			translate(content); //calls the process that translates the assembly to c++
			
			//writing the translated code to .cpp file with the same file name
			
			File f = new File(filepath);
			filepath = f.getName();
			filepath = filepath.substring(0, filepath.length()-4);
			PrintWriter writer = new PrintWriter(filepath + ".cpp", "UTF-8");
			writer.println(getNewContent());
			writer.close();
			return 0;
		} catch (FileNotFoundException e) {
			System.out.println("File not Found");
			return 1;
		} catch(IOException e){
			System.out.println("Error reading file!");
			return 2;
		} catch (Exception e){
			System.out.println("Error");
			return 3;
		}

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
