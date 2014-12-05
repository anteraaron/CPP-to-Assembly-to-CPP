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
	 * Translates the main body of the assembly file
	 */
	private void translateMain(){
		//removes comment and convert it to C++ code
		int indexOfComment = 0;
		String comments = "";
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
			
			//evaluate the constant value in the assembly file
			if(getSplittedContent()[i].trim().equalsIgnoreCase("main proc")){
				appendNewContent("\nint main(){");
			}else if(getSplittedContent()[i].trim().equalsIgnoreCase("main endp")){
				appendNewContent("}");
			}else if(getSplittedContent()[i].replaceAll("\\s+",  "").equalsIgnoreCase("int21h")){
				
				String ah = "";
				
				for(int j = 0; j < i; j++){
					if(getSplittedContent()[j].replaceAll("\\s+",  "").equalsIgnoreCase("movah,09h")){
						ah = "9";
					}else if (getSplittedContent()[j].replaceAll("\\s+",  "").equalsIgnoreCase("movah,02h")){
						ah = "2";
					}
				}
				
				
				if(ah.equals("2")){
					appendNewContent("std::cout << dl[0];\n");
				}else if (ah.equals("9")){
					appendNewContent("std::cout << dx;\n");
				}
				
			}/*else if(getSplittedContent()[i].replaceAll("\\s+",  "").equalsIgnoreCase("movah,09h")&&getSplittedContent()[i+1].replaceAll("\\s+",  "").equalsIgnoreCase("int21h")){
				appendNewContent("cout << dx;\n");
			}*/
			
			String temp = "";
			String[] splittedOperands = null;
			//evaluate mov
			if(getSplittedContent()[i].toLowerCase().matches(".*\\bmov\\b.*")&&!getSplittedContent()[i].toLowerCase().matches(".*\\b4c00h\\b.*")){
				temp = getSplittedContent()[i].replaceAll("\\s+", "").substring(3);
				splittedOperands = temp.split(",");
				if(!splittedOperands[0].equalsIgnoreCase("ds")&&!splittedOperands[1].equalsIgnoreCase("@data")){ //if not @data
					
					if(splittedOperands[1].toLowerCase().matches(".*\\d+h")){ //if it is a hex value, convert to decimal
						splittedOperands[1] = String.valueOf((int)Integer.parseInt(splittedOperands[1].replaceAll("h", "").trim(), 16 ));
						appendNewContent(splittedOperands[0] + "[0] = " + splittedOperands[1] + ";");
						if(!(getSplittedContent()[i].replaceAll("\\s+",  "").equalsIgnoreCase("movah,09h")))
							appendNewContent("updateRegisters();"); 
					}else if(splittedOperands[1].toLowerCase().contains("offset")){ //if it is a string, use strcopy instead of =
						splittedOperands[1] = splittedOperands[1].replaceAll("offset", "");
						appendNewContent("strcpy("+splittedOperands[0]+","+splittedOperands[1]+");");
					}else if(splittedOperands[1].toLowerCase().contains("\'")){ //if it is a character '[a-z]'
						appendNewContent(splittedOperands[0] + "[0] = " + splittedOperands[1] + ";");
						appendNewContent("updateRegisters();");
					}else if(splittedOperands[1].toLowerCase().matches("\\d+")){ //if it is a number
						appendNewContent(splittedOperands[0] + "[0] = " + splittedOperands[1] + ";");
						appendNewContent("updateRegisters();");
					}else{
						appendNewContent("strcpy("+splittedOperands[0]+","+splittedOperands[1]+");");
					}
				}
			}else if(getSplittedContent()[i].toLowerCase().matches(".*\\blea\\b.*")){ //if lea is used in printing, use strcopy
				temp = getSplittedContent()[i].replaceAll("\\s+", "").substring(3);
				splittedOperands = temp.split(",");
				appendNewContent("strcpy("+splittedOperands[0]+","+splittedOperands[1]+");");			
			}else if(getSplittedContent()[i].toLowerCase().matches(".*\\bxor\\b.*")){ //if xor is used to clear registers
				temp = getSplittedContent()[i].replaceAll("\\s+", "").substring(3);
				splittedOperands = temp.split(",");
				if(splittedOperands[0].toLowerCase().equals("ax")&&splittedOperands[1].toLowerCase().equals("ax")){
					appendNewContent("ah[0] = 0;");
					appendNewContent("al[0] = 0;");
				}else if(splittedOperands[0].toLowerCase().equals("bx")&&splittedOperands[1].toLowerCase().equals("bx")){
					appendNewContent("bh[0] = 0;");
					appendNewContent("bl[0] = 0;");
				}else if(splittedOperands[0].toLowerCase().equals("cx")&&splittedOperands[1].toLowerCase().equals("cx")){
					appendNewContent("ch[0] = 0;");
					appendNewContent("cl[0] = 0;");
				}else if(splittedOperands[0].toLowerCase().equals("dx")&&splittedOperands[1].toLowerCase().equals("dx")){
					appendNewContent("dh[0] = 0;");
					appendNewContent("dl[0] = 0;");
				}	
				appendNewContent("updateRegisters();");	
			}else if(getSplittedContent()[i].toLowerCase().matches(".*\\badd\\b.*")||getSplittedContent()[i].toLowerCase().matches(".*\\binc\\b.*")){
				//addition
				if(getSplittedContent()[i].toLowerCase().matches(".*\\binc\\b.*")){
					temp = getSplittedContent()[i].replaceAll("\\s+", "").substring(3);
					appendNewContent(temp + "[0]++;");
				}else{
					temp = getSplittedContent()[i].replaceAll("\\s+", "").substring(3);
					splittedOperands = temp.split(",");
					
					if(!(Character.isDigit(splittedOperands[1].charAt(0))||(splittedOperands[1].replaceAll("\\s+", "").contains("'")&&splittedOperands.length < 4))){
						appendNewContent(splittedOperands[0] + "[0]" + "+=" +splittedOperands[1] + "[0];");
					}else{
						appendNewContent(splittedOperands[0] + "[0]" + "+=" +splittedOperands[1] + ";");
					}
				}
			}else if(getSplittedContent()[i].toLowerCase().matches(".*\\bsub\\b.*")||getSplittedContent()[i].toLowerCase().matches(".*\\bdec\\b.*")){
				//subtraction
				if(getSplittedContent()[i].toLowerCase().matches(".*\\bdec\\b.*")){
					temp = getSplittedContent()[i].replaceAll("\\s+", "").substring(3);
					appendNewContent(temp + "[0]--;");
				}else{
					temp = getSplittedContent()[i].replaceAll("\\s+", "").substring(3);
					splittedOperands = temp.split(",");
					
					if(!(Character.isDigit(splittedOperands[1].charAt(0))||(splittedOperands[1].replaceAll("\\s+", "").contains("'")&&splittedOperands.length < 4))){
						appendNewContent(splittedOperands[0] + "[0]" + "-=" +splittedOperands[1] + "[0];");
					}else{
						appendNewContent(splittedOperands[0] + "[0]" + "-=" +splittedOperands[1] + ";");
					}
										
				}
			}
			//loop and conditions
			if(getSplittedContent()[i].toLowerCase().matches(".*\\bcmp\\b.*")||getSplittedContent()[i].toLowerCase().contains(":")){
				setCurrentLine(i);
				translateConditions();
				i = getCurrentLine();
			}
			//return 0 line
			if(getSplittedContent()[i].replaceAll("\\s+",  "").equalsIgnoreCase("movax,4c00h")&&getSplittedContent()[i+1].replaceAll("\\s+",  "").equalsIgnoreCase("int21h")){
				appendNewContent("return 0;");
			}
		}
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
	 * Converts Assembly to C++.
	 * Pre-Define variables and declarations part
	 * @param content The content of the assembly file
	 */
	private void translate(String content){
		
		//set declarations
		appendNewContent("#include <iostream>");
		appendNewContent("#include <cstdlib>");
		appendNewContent("#include <cstring>\n");
		//appendNewContent("using namespace std; \n");
		appendNewContent("char ax[255] = {0};");
		appendNewContent("char bx[255] = {0};");
		appendNewContent("char cx[255] = {0};");
		appendNewContent("char dx[255] = {0};");
		appendNewContent("char al[2] = {0};");
		appendNewContent("char bl[2] = {0};");
		appendNewContent("char cl[2] = {0};");
		appendNewContent("char dl[2] = {0};");
		appendNewContent("char ah[2] = {0};");
		appendNewContent("char bh[2] = {0};");
		appendNewContent("char ch[2] = {0};");
		appendNewContent("char dh[2] = {0};");
		appendNewContent("char si[2] = {0};");
		translateVariables(); //converts assembly variables to C++
		
		
		appendNewContent("\nvoid updateRegisters(){");
		appendNewContent("ax[0] = al[0];\nax[1] = ah[0];");
		appendNewContent("bx[0] = bl[0];\nbx[1] = bh[0];");
		appendNewContent("cx[0] = cl[0];\ncx[1] = ch[0];");
		appendNewContent("dx[0] = dl[0];\ndx[1] = dh[0];");
		appendNewContent("}");
		
		
		translateMain(); //translate the body of the assembly to C++
		changeWhileConditions();
		
		
		
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
