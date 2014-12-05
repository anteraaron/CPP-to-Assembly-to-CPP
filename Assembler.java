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
	 * Translate assembly variables to C++
	 */
	@SuppressWarnings("null")
	private void translateVariables(){
		setSplittedContent(getContent());
		String[] splittedVariables = null;
		String[] temp = null;
		int ctr = 0;
		int indexOfComment = 0;
		String comments = "";
		
		//removes comment
		for(int i = getCurrentLine(); i<getSplittedContent().length; i++){	
			setCurrentLine(i);
			indexOfComment = 0;
			if(getSplittedContent()[i].contains(";")){
				indexOfComment = getSplittedContent()[i].indexOf(';');
				comments = getSplittedContent()[i].substring(indexOfComment);
				comments = comments.replaceAll(";", "");
				getSplittedContent()[i] = getSplittedContent()[i].substring(0, indexOfComment);
				appendNewContent("//" + comments);
			}
		}
		//evaluate the pre-define codes in assembly
		for(int i=0; i<getSplittedContent().length; i++){	
			if((getSplittedContent()[i].replaceAll("\\s+","")).equalsIgnoreCase(".data")){	//indicates that we are in the .data part	
				i++;
				//continue until the end of .data segment
				while(!((getSplittedContent()[i].replaceAll("\\s+", "")).equalsIgnoreCase(".model")||(getSplittedContent()[i].replaceAll("\\s+", "")).equalsIgnoreCase(".code")||(getSplittedContent()[i].replaceAll("\\s+", "")).contains(".stack"))){			
					//remove db, dw, dd
					String dataType, variableName, value, oldVarName = "";
					
					if(getSplittedContent()[i].toLowerCase().matches(".*\\bdb\\b.*")){
						splittedVariables = getSplittedContent()[i].split("\\b[dD][bB]\\b");
					}else if(getSplittedContent()[i].toLowerCase().matches(".*\\bdw\\b.*")){
						splittedVariables = getSplittedContent()[i].split("\\b[dD][wW]\\b");
					}else if(getSplittedContent()[i].toLowerCase().matches(".*\\bdd\\b.*")){
						splittedVariables = getSplittedContent()[i].split("\\b[dD][dD]\\b");
					}
					//set variable name
					variableName = splittedVariables[0].replaceAll("\\s+", "");
					
					//set data type
					dataType = "char";
					splittedVariables[1] = splittedVariables[1].replaceAll("\\$", "");//remove dollar signs					
					if(splittedVariables[1].contains("\"")||(splittedVariables[1].contains("'")&&splittedVariables[1].replaceAll("\\s+","").length() > 3)){
						variableName = variableName + "[" + splittedVariables[1].length() +"]"; //make every variables as an array
						
						//parse decimal and hex value in declaration outside quotation marks
						if(splittedVariables[1].contains(",")){
							temp = null;
							splittedVariables[1] = splittedVariables[1].replaceAll("\\\\\"", ";");
							temp = splittedVariables[1].split("(?x)   " + 
				                     ",          " +   // Split on comma
				                     "(?=        " +   // Followed by
				                     "  (?:      " +   // Start a non-capture group
				                     "    [^\"]* " +   // 0 or more non-quote characters
				                     "    \"     " +   // 1 quote
				                     "    [^\"]* " +   // 0 or more non-quote characters
				                     "    \"     " +   // 1 quote
				                     "  )*       " +   // 0 or more repetition of non-capture group (multiple of 2 quotes will be even)
				                     "  [^\"]*   " +   // Finally 0 or more non-quotes
				                     "  $        " +   // Till the end  (This is necessary, else every comma will satisfy the condition)
				                     ")          "     // End look-ahead
				                         );
							
							for(int j = 0; j < temp.length; j++){
								temp[j] = temp[j].replaceAll(";", "''");
								if(temp[j].replaceAll("\\s+", "").equalsIgnoreCase("9")||temp[j].replaceAll("\\s+", "").equalsIgnoreCase("09h")||temp[j].replaceAll("\\s+", "").equalsIgnoreCase("9h")){
									temp[j] = "\\t";
								}else if(temp[j].replaceAll("\\s+", "").equalsIgnoreCase("7")||temp[j].replaceAll("\\s+", "").equalsIgnoreCase("07h")||temp[j].replaceAll("\\s+", "").equalsIgnoreCase("7h")){
									temp[j] = "\\a";
								}else if(temp[j].replaceAll("\\s+", "").equalsIgnoreCase("8")||temp[j].replaceAll("\\s+", "").equalsIgnoreCase("08h")||temp[j].replaceAll("\\s+", "").equalsIgnoreCase("8h")){
									temp[j] = "\\b";
								}else if(temp[j].replaceAll("\\s+", "").equalsIgnoreCase("10")||temp[j].replaceAll("\\s+", "").equalsIgnoreCase("0ah")){
									temp[j] = "\\n";
								}else if(temp[j].replaceAll("\\s+", "").equalsIgnoreCase("11")||temp[j].replaceAll("\\s+", "").equalsIgnoreCase("0bh")){
									temp[j] = "\\v";
								}else if(temp[j].replaceAll("\\s+", "").equalsIgnoreCase("12")||temp[j].replaceAll("\\s+", "").equalsIgnoreCase("0ch")){
									temp[j] = "\\f";
								}else if(temp[j].replaceAll("\\s+", "").equalsIgnoreCase("13")||temp[j].replaceAll("\\s+", "").equalsIgnoreCase("0dh")){
									temp[j] = "\\r";
								}else if(temp[j].replaceAll("\\s+", "").equalsIgnoreCase("0")||temp[j].replaceAll("\\s+", "").equalsIgnoreCase("0h")||temp[j].replaceAll("\\s+", "").equalsIgnoreCase("00h")){
									temp[j] = "\\0";
								}else if(!(temp[j].contains("'")||temp[j].contains("\""))){
									//loop to convert decimal to ascii to string
									for(int k = 32; k < 256; k++){
											
										if(temp[j].toLowerCase().contains("h")){
											if(Integer.parseInt(temp[j].replaceAll("\\s+", ""), 16) == k){
												if(k!=34 && k!=92 && k!=63 && k!=39)
													temp[j] = Character.toString((char)k);
												else
													temp[j] = "\\" + Character.toString((char)k);
												break;
											}
										}else{
											if(Integer.parseInt(temp[j].replaceAll("\\s+", "")) == k){
												if(k!=34 && k!=92 && k!=63 && k!=39)
													temp[j] = Character.toString((char)k);
												else{
													temp[j] = "\\" + Character.toString((char)k);		
												}
												break;
											}
										}
									}
									
									
								}
							}
							splittedVariables[1] = "";
							//remove quotation marks and apostrophe and add double quotes to beginning and end
							for(int j = 0; j < temp.length; j++){
								if(!(temp[j].replaceAll("\\s+", "").length()==2 && temp[j].contains("\\"))){
									temp[j] = temp[j].trim().replaceAll("^'|'$", "");
									temp[j] = temp[j].replaceAll("^\"|\"$", "");
								}
								splittedVariables[1] += temp[j];
							}	
						}
						
						splittedVariables[1] = "\"" + splittedVariables[1].trim() + "\"";
					}else{
						splittedVariables[1] = splittedVariables[1].replaceAll("\\s+", "");
						
						int occurances = 0;
						for( int k=0; k<splittedVariables[1].length(); k++ ) {
						    if( splittedVariables[1].charAt(k) == ',' ) {
						        occurances++;
						    } 
						}		
						occurances++;
						oldVarName = variableName;
						variableName = variableName + "[" + occurances + "]";
					}
					
					
					
					if(splittedVariables[1].contains("?")&&!splittedVariables[1].contains("\"")){//if declared as ?, set as 0 default value
						value = "{0}";
					}else{
						ctr = 0;
						for(int j=0; j<splittedVariables[1].length(); j++){
							if(splittedVariables[1].charAt(j) == '"'){
								ctr++;
							}
							//check if it is a string
							if(ctr % 2 == 0){
								if(splittedVariables[1].contains("\"")){
									if(splittedVariables[1].charAt(j) == ','){
										StringBuilder sb = new StringBuilder(splittedVariables[1]);
										sb.setCharAt(j, ' ');
										splittedVariables[1] = sb.toString();
										splittedVariables[1] = splittedVariables[1].replaceAll("\"", "");
										splittedVariables[1] = "\"" + splittedVariables[1] + "\"";
									}
								}
							}	
						}
					//if declared an array using dup, convert to c++
					if(splittedVariables[1].contains("dup")&&!splittedVariables[1].contains("\"")){
						
						String[] splittedDup = splittedVariables[1].split("dup");
						value = "";
						for(int j = 0; j < Integer.parseInt(splittedDup[0]); j++){
	
							value += splittedDup[1].replaceAll("\\(", "").replaceAll("\\)", "");
							
							if(j < Integer.parseInt(splittedDup[0])-1){
								value += ",";
							}
							splittedVariables[1] = value;			
						}
						splittedVariables[1] = splittedVariables[1].replaceAll("\\s+", "");
	
						int occurances = 0;
						for( int k=0; k<splittedVariables[1].length(); k++ ) {
						    if( splittedVariables[1].charAt(k) == ',' ) {
						        occurances++;
						    } 
						}				
						occurances++;
						oldVarName = oldVarName + "[" + occurances + "]";
						variableName = oldVarName;
					}
											
					value = "{" + splittedVariables[1] + "}";
					}
		
					appendNewContent(dataType + " " + variableName + " = " + value + ";"); //converting finished.
					dataType = "";
					variableName = "";
					value = "";
					oldVarName = "";
					i++;
					
					while(getSplittedContent()[i].replaceAll("\\s+", "").equals("")){
						i++;
					}
					
				}
				
				setCurrentLine(i); //remember the current line being evaluated
				break;
			}
		}	
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
