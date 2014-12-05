import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Dissasembler{
  
  //arraylist para sa declared variables
  private ArrayList<String> variables = new ArrayList<String>();
  //arraylist for .code part
	private ArrayList<String> code = new ArrayList<String>();
	//arraylist for .data part
	private ArrayList<String> data = new ArrayList<String>();
	private String blank = "";


  public void convertCPPtoASM() throws FileNotFoundException{
  	
  		String fileName = "test2.cpp";
		String line = "";
		String lineWithNoSpaces = "";
		String forLoop = "";
		String toCode = "";
		
		int stringCounter = 0;
		int messageCounter = 1;
		int variableCounter = 1;
		int loopNumber = 0;
		int indentation = 0;
		int forLoopCnt = 0;
		int dowhileCnt = 0;
		int whileCnt = 0;
		int ifCnt = 0;
		int elseCnt = 0;
		
		boolean newline = false;
		boolean insideLoop1 = false;
		boolean insideLoop2 = false;
		
		Scanner input = new Scanner(System.in);
		
		ArrayList<String> loop1 = new ArrayList<String>();
		ArrayList<String> loop2 = new ArrayList<String>();
		ArrayList<String> loop1End = new ArrayList<String>();
		ArrayList<String> loop2End = new ArrayList<String>();
		
		System.out.print("Filename of C++ file:");
		//fileName = input.nextLine();
		
		Scanner fileReader = new Scanner(new FileInputStream(fileName));
	
		ArrayList<String> conditionals1 = new ArrayList<String>();
		ArrayList<String> conditionals2 = new ArrayList<String>();
		
		
		while(fileReader.hasNextLine()){
			line = fileReader.nextLine();
	
			forLoop = line;
			
			line = line.replaceAll(";", "");
			System.out.println(line);
			
			if(line.contains("}")){
				line = line.trim();
			}
			
			if(line.contains("cout")){
				
				StringTokenizer st = new StringTokenizer(line, "<<");
				
				while(st.hasMoreTokens()){
					String token = st.nextToken();
					
					token = token.trim();
					
					toCode = "";
					for(int x = 0; x < indentation - 1; x++){
						toCode = toCode + "   "; 
					}
					
					if((token.charAt(0) == '"') && (token.charAt(token.length() - 1) == '"')){
						
						
						//String message = "message" + messageCounter + " db " +  token.substring(0, token.length()) + ", '$'";
						
						data.add("message" + messageCounter + " db " +  token.substring(0, token.length()) + ", '$'");
						//String showString = "mov dx, offset message" + messageCounter + "\nmov ah, 09h \nint 21h";
						
						code.add(toCode + "mov dx, offset message" + messageCounter + "\n" + toCode + "mov ah, 09h \n" + toCode + "int 21h");

						messageCounter++;
						
					}
					else if(variables.contains(token)){
						code.add(toCode + "mov dh, " + token + "\n" + toCode + "mov ah, 02h" + "\n" + toCode + "int 21h");
					}
					else if(token.contains("endl")){
						//String showString = "mov dl, 10h \nmov ah, 02h \nint 21h";
						code.add("mov dl, 10h \nmov ah, 02h \nint 21h");
						
					}
				}
				
				
			}
			else if(line.contains("for")){
					loopNumber++;
					forLoopCnt++;
					indentation++;
					
					toCode = "";
					for(int x = 0; x < indentation - 1; x++){
						toCode = toCode + "   "; 
					}
					
					if(!insideLoop1 && !insideLoop2){
						insideLoop1 = true;
						
						StringTokenizer conditions = new StringTokenizer(forLoop.substring(forLoop.indexOf('(') + 1, forLoop.lastIndexOf(')')), ";");
						
						while(conditions.hasMoreTokens()){
							conditionals1.add(conditions.nextToken());
						}
						
						
						String initial = conditionals1.get(0);
						String test = conditionals1.get(1);
						String incOrDec = conditionals1.get(2);
						 
						
						if(initial.contains("int")){
							initial = initial.replace("int", "");
						}
						//initial = initial.replace("int", "");
						
						//System.out.println(initial);
						
						StringTokenizer initialCondition = new StringTokenizer(initial, "=");
													
						String variableUsed = initialCondition.nextToken();
						
						variableUsed = variableUsed.trim();
						
						String startValue = initialCondition.nextToken(); 
						
						startValue = startValue.trim();
						//System.out.println(startValue);
						
						if(variables.contains(variableUsed)){							
							code.add(toCode + "mov " + variableUsed + ", " + startValue +  "\n" + toCode + "forLoop" + forLoopCnt + ":");							
						}
						else{
							variables.add(variableUsed);													
														
							code.add(toCode + "forLoop" + forLoopCnt + ":");
							
							data.add(toCode + variableUsed + " db " + startValue);
						}
						
						if(incOrDec.contains("++")){
							
							StringTokenizer testCondition = new StringTokenizer(test, "<");
							
							String endValue = "";
							
							while(testCondition.hasMoreTokens()){
								endValue = testCondition.nextToken();
								endValue = endValue.trim();
							}
							
							loop1End.add(toCode + "inc " + variableUsed + "\n" + toCode + "cmp " + variableUsed + ", " + endValue + "\n" + toCode + "jne forLoop" + forLoopCnt);
							/*if(test.contains("<")){
								StringTokenizer testCondition = new StringTokenizer(test, "<");
								
								String endValue = "";
								
								while(testCondition.hasMoreTokens()){
									endValue = testCondition.nextToken();
									endValue = endValue.trim();
								}
								
								loop1End.add(toCode + "inc " + variableUsed + "\n" + toCode + "cmp " + variableUsed + ", " + endValue + "\n" + toCode + "jne forLoop" + forLoopCnt);
							}
							else if(test.contains(">")){
								StringTokenizer testCondition = new StringTokenizer(test, "<");
								
								String endValue = "";
								
								while(testCondition.hasMoreTokens()){
									endValue = testCondition.nextToken();
									endValue = endValue.trim();
								}
								
								loop1End.add(toCode + "cmp " + variableUsed + ", " + endValue + "\n" + toCode + "jne forLoop" + forLoopCnt);
							}*/
						}
						else if(incOrDec.contains("--")){
							//loop1End.add(toCode + "dec " + variableUsed + "\n");
							
							StringTokenizer testCondition = new StringTokenizer(test, ">");
							
							String endValue = "";
							
							while(testCondition.hasMoreTokens()){
								endValue = testCondition.nextToken();
								endValue = endValue.trim();
							}
							
							loop1End.add(toCode + "dec " + variableUsed + "\n" + toCode + "cmp " + variableUsed + ", " + endValue + "\n" + toCode + "jne forLoop" + forLoopCnt);
							
							/*if(test.contains("<")){
								StringTokenizer testCondition = new StringTokenizer(test, "<");
								
								String endValue = "";
								
								while(testCondition.hasMoreTokens()){
									endValue = testCondition.nextToken();
									endValue = endValue.trim();
								}
								
								loop1End.add(toCode + "cmp " + variableUsed + ", " + endValue + "\n" + toCode + "jne forLoop" + forLoopCnt);
							}
							else if(test.contains(">")){
								StringTokenizer testCondition = new StringTokenizer(test, "<");
								
								String endValue = "";
								
								while(testCondition.hasMoreTokens()){
									endValue = testCondition.nextToken();
									endValue = endValue.trim();
								}
								
								loop1End.add(toCode + "cmp " + variableUsed + ", " + endValue + "\n" + toCode + "jne forLoop" + forLoopCnt);
							}*/
						}
						
						//System.out.println(loop1End.toString());
						//toCode = "";
						conditionals1.clear();
						
					}
					else if(insideLoop1 && !insideLoop2){
						toCode = "";
						
						for(int x = 0; x < indentation - 1; x++){
							toCode += "   "; 
						}
						
						insideLoop2 = true;
						//System.out.println("yohoooo");
						StringTokenizer conditions = new StringTokenizer(forLoop.substring(forLoop.indexOf('(') + 1, forLoop.lastIndexOf(')')), ";");
						
						while(conditions.hasMoreTokens()){
							conditionals2.add(conditions.nextToken());
						}
						
						
						String initial = conditionals2.get(0);
						String test = conditionals2.get(1);
						String incOrDec = conditionals2.get(2);
						
						String addtodata = ""; 
						
						initial = initial.replace("int", "");
						
						//System.out.println(initial);
						
						StringTokenizer initialCondition = new StringTokenizer(initial, "=");
						
						//int tokenCount = 0;
						
						String variableUsed = initialCondition.nextToken();
						
						variableUsed = variableUsed.trim();
						
						String startValue = initialCondition.nextToken(); 
						
						startValue = startValue.trim();
						
						if(variables.contains(variableUsed)){
							code.add(toCode + "mov " + variableUsed + ", " + startValue +  "\n" + toCode + "forLoop" + forLoopCnt + ":");
								
						}
						else{
							variables.add(variableUsed);
							
							code.add(toCode + "forLoop" + forLoopCnt + ":");
							
							data.add(variableUsed + " db " + startValue);
						}
						
						if(incOrDec.contains("++")){

								StringTokenizer testCondition = new StringTokenizer(test, "<");
								
								String endValue = "";
								
								while(testCondition.hasMoreTokens()){
									endValue = testCondition.nextToken();
									endValue = endValue.trim();
								}
								
								loop2End.add(toCode + "inc " + variableUsed + "\n" + toCode + "cmp " + variableUsed + ", " + endValue + "\n" + toCode + "jne forLoop" + forLoopCnt);

						}
						else if(incOrDec.contains("--")){
							//System.out.println("dito nakapasok " + loopNumber);
							
								StringTokenizer testCondition = new StringTokenizer(test, ">");
								
								String endValue = "";
								
								while(testCondition.hasMoreTokens()){
									endValue = testCondition.nextToken();
									endValue = endValue.trim();
								}
								
								loop2End.add(toCode + "dec " + variableUsed + "\n" + toCode + "cmp " + variableUsed + ", " + endValue + "\n" + toCode + "jne forLoop" + forLoopCnt);
							
						}
						
						//System.out.println(loop2End.toString());
						//toCode = "";
						conditionals2.clear();
					}
					
					
			}
			else if(line.contains("while(")){
				loopNumber++;
				indentation++;
				whileCnt++;
				//System.out.println("PUMASOK SA WHILE CONDITION");
				
				
				if(forLoop.contains(";")){
					//System.out.println(dowhileCnt);
					loopNumber--;
					//System.out.println(indentation);
					toCode = "";
					indentation--;
					for(int x = 0; x < indentation - 1; x++){
						toCode = toCode + "   "; 
					}
								
					if(!insideLoop1 && !insideLoop2){
						insideLoop1 = true;
						
						if(line.contains("<")){
							StringTokenizer whileCondition = new StringTokenizer(line.substring(line.indexOf('(') + 1, line.indexOf(')')), "<");
							
							while(whileCondition.hasMoreTokens()){
								conditionals1.add(whileCondition.nextToken());
							}
							
						code.add(toCode + "cmp " + conditionals1.get(0).trim() + ", " + conditionals1.get(1).trim() + toCode + "\njng do-whileloop" + dowhileCnt);
							
						}
						else if(line.contains(">")){
							StringTokenizer whileCondition = new StringTokenizer(line.substring(line.indexOf('(') + 1, line.indexOf(')')), ">");
							conditionals1.add(whileCondition.nextToken());
							
							while(whileCondition.hasMoreTokens()){
								conditionals1.add(whileCondition.nextToken());
							}
							
						code.add(toCode + "cmp " + conditionals1.get(0).trim() + ", " + conditionals1.get(1).trim() + toCode + "\njg do-whileloop" + dowhileCnt);
						}
						
						conditionals1.clear();
						
					}
					else if(insideLoop1 && !insideLoop2){
						insideLoop2 = true;
						
						if(line.contains("<")){
							StringTokenizer whileCondition = new StringTokenizer(line.substring(line.indexOf('(') + 1, line.indexOf(')')), "<");
							conditionals2.add(whileCondition.nextToken());
							
							while(whileCondition.hasMoreTokens()){
								conditionals2.add(whileCondition.nextToken());
							}
							
						code.add(toCode + "cmp " + conditionals2.get(0).trim() + ", " + conditionals2.get(1).trim() + toCode + "\njng do-whileloop" + dowhileCnt);
						}
						else if(line.contains(">")){
							StringTokenizer whileCondition = new StringTokenizer(line.substring(line.indexOf('(') + 1, line.indexOf(')')), ">");
							conditionals2.add(whileCondition.nextToken());
							
							while(whileCondition.hasMoreTokens()){
								conditionals2.add(whileCondition.nextToken());
							}
							
						code.add(toCode + "cmp " + conditionals2.get(0).trim() + ", " + conditionals2.get(1).trim() + toCode + "\njng do-whileloop" + dowhileCnt);
						}
						
						conditionals2.clear();
						
					}
										
				}
				else{
					//System.out.println("PUMASOK SA ELSE CONDITION");
					
					
					if(!insideLoop1 && !insideLoop2){
						insideLoop1 = true;
						
						toCode = "";
						for(int x = 0; x < indentation - 1; x++){
							toCode = toCode + "   "; 
						}
						
						//System.out.println("DAPAT PALA ANDITO SIYA");
						//System.out.println(indentation);
						if(line.contains("<")){
							StringTokenizer whileCondition = new StringTokenizer(line.substring(line.indexOf('(') + 1, line.indexOf(')')), "<");
							
							while(whileCondition.hasMoreTokens()){
								conditionals1.add(whileCondition.nextToken());
							
							}						
							
							code.add(toCode + "whileLoop" + whileCnt + ":" + "\n" + toCode + "cmp " + conditionals1.get(0).trim() + ", " + conditionals1.get(1).trim() + "\n" + toCode + "je endWhile" + whileCnt);
							
							loop1End.add(toCode + "cmp " + conditionals1.get(0).trim() + ", " + conditionals1.get(1).trim() + "\n"  + toCode +  "jng whileLoop" + whileCnt + "\n\n"  + toCode +  "endWhile" + whileCnt + ":");
							
						}
						else if(line.contains(">")){
							StringTokenizer whileCondition = new StringTokenizer(line.substring(line.indexOf('('), line.indexOf(')')), ">");
							
							while(whileCondition.hasMoreTokens()){
								conditionals1.add(whileCondition.nextToken());
								//System.out.println(whileCondition.nextToken());
							}
							
							code.add(toCode + "whileLoop" + whileCnt + ":" + "\n" + "cmp " + conditionals1.get(0).trim() + ", " + conditionals1.get(1).trim() + "\n" + "je endWhile" + whileCnt);
							
							
							loop1End.add(toCode + "cmp " + conditionals1.get(0).trim() + ", " + conditionals1.get(1).trim() + "\n" + "jnl whileLoop" + whileCnt + "\n\n" + "endWhile" + whileCnt + ":");
								
						}
						else if(line.contains("!=")){
							StringTokenizer whileCondition = new StringTokenizer(line.substring(line.indexOf('('), line.indexOf(')')), "!=");
							
							while(whileCondition.hasMoreTokens()){
								conditionals1.add(whileCondition.nextToken());
								//System.out.println(whileCondition.nextToken());
							}
							
							code.add(toCode + "whileLoop" + whileCnt + ":" + "\n" + "cmp " + conditionals1.get(0).trim() + ", " + conditionals1.get(1).trim() + "\n" + "je endWhile" + whileCnt);
							
							
							loop1End.add(toCode + "cmp " + conditionals1.get(0).trim() + ", " + conditionals1.get(1).trim() + "\n" + "jne whileLoop" + whileCnt + "\n\n" + "endWhile" + whileCnt + ":");
								
						}
					}
					else if(insideLoop1 && !insideLoop2){
						//System.out.println("DAPAT ANDITO SIYA");
						insideLoop2 = true;
						
						toCode = "";
						for(int x = 0; x < indentation - 1; x++){
							toCode = toCode + "   "; 
						}
						
						if(line.contains("<")){
							StringTokenizer whileCondition = new StringTokenizer(line.substring(line.indexOf('(') + 1, line.indexOf(')')), "<");
							
							while(whileCondition.hasMoreTokens()){
								conditionals2.add(whileCondition.nextToken());
								//System.out.println(whileCondition.nextToken());
							}
							
							//System.out.println(indentation);
							
							code.add(toCode + "whileLoop" + whileCnt + ":\n" + toCode + "cmp " + conditionals2.get(0).trim() + ", " + conditionals2.get(1).trim() + "\n" + toCode + "je endWhile" + whileCnt);
							
							
							loop2End.add(toCode + "cmp " + conditionals2.get(0).trim() + ", " + conditionals2.get(1).trim() + "\n" + toCode + "jng whileLoop" + whileCnt + "\n" + toCode + "endWhile" + whileCnt + ":");
							
							
						}
						else if(line.contains(">")){
							StringTokenizer whileCondition = new StringTokenizer(line.substring(line.indexOf('(') + 1, line.indexOf(')')), ">");
							
							while(whileCondition.hasMoreTokens()){
								conditionals2.add(whileCondition.nextToken());
							
							}
							
							//System.out.println(indentation);
							
							code.add(toCode + "whileLoop" + whileCnt + ":" + toCode + "cmp " + conditionals2.get(0) + ", " + conditionals2.get(1) + toCode + "jng endWhile" + whileCnt);
							
							loop2End.add(toCode + "cmp " + conditionals2.get(0) + ", " + conditionals2.get(1) + toCode + "jnl whileLoop" + whileCnt + "endWhile" + whileCnt);
														
						}
						else if(line.contains("!=")){
							StringTokenizer whileCondition = new StringTokenizer(line.substring(line.indexOf('('), line.indexOf(')')), "!=");
							
							while(whileCondition.hasMoreTokens()){
								conditionals1.add(whileCondition.nextToken());
								//System.out.println(whileCondition.nextToken());
							}
							
							code.add(toCode + "whileLoop" + whileCnt + ":" + "\n" + "cmp " + conditionals1.get(0).trim() + ", " + conditionals1.get(1).trim() + "\n" + "je endWhile" + whileCnt);
							
							
							loop1End.add(toCode + "cmp " + conditionals1.get(0).trim() + ", " + conditionals1.get(1).trim() + "\n" + "jne whileLoop" + whileCnt + "\n\n" + "endWhile" + whileCnt + ":");
								
						}
					}
										
				}
			}
			else if(line.contains("do")){
				indentation++;
				loopNumber++;
				dowhileCnt++;
				insideLoop1 = true;
				
				toCode = "";
				for(int x = 0; x < indentation - 1; x++){
					toCode = toCode + "   "; 
				}
				
				code.add(toCode + "do-whileLoop" + loopNumber + ":");
				//System.out.println(loopNumber);
			}
			else if(line.contains("if") && !line.contains("else")){
				indentation++;
				ifCnt++;
				
				String toManipulate = "";
				
				toCode = "";
				for(int x = 0; x < indentation - 1; x++){
					toCode = toCode + "   "; 
				}
				
				toManipulate = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
				
				System.out.println(toManipulate);
				
				if(toManipulate.contains(">")){
					StringTokenizer st = new StringTokenizer(toManipulate, ">");
					String variabletocompare = st.nextToken().trim();
					String valuetocompare = st.nextToken().trim();
					
					System.out.println(variabletocompare);
					System.out.println(valuetocompare);
					
					code.add(toCode + "cmp " + variabletocompare + ", " + valuetocompare + "\n" + toCode + "jg if" + ifCnt + ":");
					
				}
				//StringTokenizer st = new StringTokenizer();
				
			}
			else if(line.contains("if") && line.contains("else")){
				//indentation++;
				elseCnt++;
				
				toCode = "";
				for(int x = 0; x < indentation - 1; x++){
					toCode = toCode + "   "; 
				}
				
			}
			else if(line.contains("int ") && !line.contains("main")){
				
				toCode = "";
				for(int i = 0; i < indentation - 1; i++){
					toCode = toCode + "  ";
				}
				
				StringTokenizer st;
				
				if(line.contains("=") && line.contains(",")){
					
					line = line.trim();
					
					String declarations = line.substring(line.indexOf(' ') + 1, line.length());
					
					StringTokenizer multDecs = new StringTokenizer(declarations, ",");
					StringTokenizer eachDec;
					while(multDecs.hasMoreTokens()){
						String newVar = "";
						String dec = multDecs.nextToken();
						dec = dec.trim();
						eachDec = new StringTokenizer(dec, "=");
						
						int counter = 0;
						while(eachDec.hasMoreTokens()){
							if(counter == 0){
								newVar = eachDec.nextToken().trim();
								variables.add(newVar);
								counter++;
							}
							else{
								newVar += " db " + eachDec.nextToken().trim();
								counter = 0;
							}
							
						}
						
						data.add(newVar);
						
						
					}
					
					//System.out.println(declarations);
					
				}
				else if(line.contains("=")){
					
					line = line.trim();
					st = new StringTokenizer(line.substring(4, line.length()), "=");
					
					int tokenCount = 0;
					
					String token = st.nextToken();
					
					token = token.trim();
					
					variables.add(token);
					
					String varDeclaration = token + " db ";
					
					tokenCount++;
					
					while(st.hasMoreTokens()){
						token = st.nextToken();
						token = token.trim();
						
						if(tokenCount == 1){
							token = token.trim();
							varDeclaration += token;
							}
					}
					
					data.add(varDeclaration);
				
				}
				else{
					st = new StringTokenizer(line, " ");
				
					StringTokenizer declarationOnly = new StringTokenizer(line, " ");
					
					ArrayList<String> declaration = new ArrayList<String>();
					
					while(declarationOnly.hasMoreTokens()){
						declaration.add(declarationOnly.nextToken());
					}
					
					data.add(declaration.get(1) + " db 0");
					variables.add(declaration.get(1).trim());
				}
				
				
				
				/*int tokenCount = 0;
				
				String token = st.nextToken();
				
				token = token.trim();
				
				variables.add(token);
				
				String varDeclaration = token + " db ";
				
				tokenCount++;
				
				while(st.hasMoreTokens()){
					token = st.nextToken();
					token = token.trim();
					
					if(tokenCount == 1){
						token = token.trim();
						varDeclaration += token;
						}
				}
				
				data.add(varDeclaration);*/
					
			}
			else if(line.contains("char ")){
				StringTokenizer st = new StringTokenizer(line.substring(5, line.length()), "=");
				
				int tokenCount = 0;
				
				String token = st.nextToken();
				
				token = token.trim();
				
				variables.add(token);
				
				String varDeclaration = token + " db ";
				
				tokenCount++;
				
				while(st.hasMoreTokens()){
					token = st.nextToken();
					token = token.trim();
					
					if(tokenCount == 1){
						token = token.trim();
						varDeclaration += token + ", '$'";
						}
					
				}
				
				data.add(varDeclaration);
			}						
			else if(line.equals("}")){
				
				if(insideLoop1 && !insideLoop2){
					for(String loopEnd : loop1End){
						code.add(loopEnd);
					}
					insideLoop1 = false;
					conditionals1.clear();
					loop1End.clear();
					loopNumber--;
					indentation--;
				}
				else if(insideLoop1 && insideLoop2){
					for(String loopEnd : loop2End){
						code.add(loopEnd);
					}
					insideLoop2 = false;
					conditionals2.clear();
					loop2End.clear();
					loopNumber--;
					indentation--;
				}
				
			}
			else if(line.contains("+=")){
				toCode = "";
				for(int i = 0; i < indentation - 1; i++){
					toCode += "  ";
				}
				
				System.out.println(toCode + "lelz");
				
				StringTokenizer st = new StringTokenizer(line, "+=");
				
				String variableUsed = st.nextToken().trim();
				
				System.out.println(variableUsed);
				
				String newValue = st.nextToken().trim();
				newValue.replaceAll(" ", "");
				
				System.out.println(newValue);	
				
				if(newValue.contains("+") || newValue.contains("-") || newValue.contains("%") || newValue.contains("*") || newValue.contains("/")){
					
					ArrayList<Character> symbolsPresent = new ArrayList<Character>();
					
					for(int a = 0; a < newValue.length(); a++){
						if(newValue.charAt(a) == '+' || newValue.charAt(a) == '-' || newValue.charAt(a) == '*' || newValue.charAt(a) == '/' || newValue.charAt(a) == '%'){
							symbolsPresent.add(newValue.charAt(a));
						}
					}
					
					ArrayList<String> newValues = new ArrayList<String>();
					
					StringTokenizer vals = new StringTokenizer(newValue, "+-/*%");
					
					
					while(vals.hasMoreElements()){
						newValues.add(vals.nextToken().trim());
					}
					
					if(newValues.size() == 1){
						code.add(toCode + "mov " + variableUsed + ", " + newValue);
					}
					else{
						for(int a = 0; a < newValues.size(); a++){
							if(a == 0){
								code.add(toCode + "mov " + variableUsed + ", " + newValues.get(a));
							}
							else{
								if(symbolsPresent.get(a - 1) == '+'){
									code.add(toCode + "add " + variableUsed + ", " + newValues.get(a));
								}
								else if(symbolsPresent.get(a - 1) == '-'){
									code.add(toCode + "sub " + variableUsed + ", " + newValues.get(a));
								}
							}
						}
					}
				}
				else{
					if(variables.contains(newValue)){
						code.add(toCode + "mov bh, " + newValue + "\n" + toCode + "mov " + variableUsed + ", bh ");
					}
					else
						code.add(toCode + "mov " + variableUsed + ", " + newValue);
				}
				
				
				//code.add("TAKTE");
				
			}
			else if(line.contains("-=")){
				
			}
			else if(line.contains("=")){
				
				toCode = "";
				for(int i = 0; i < indentation - 1; i++){
					toCode += "  ";
				}
						
				
				System.out.println(toCode + "lelz");
				
				StringTokenizer st = new StringTokenizer(line, "=");
				
				String variableUsed = st.nextToken().trim();
				
				System.out.println(variableUsed);
				
				String newValue = st.nextToken().trim();
				newValue.replaceAll(" ", "");
				
				System.out.println(newValue);	
				
				if(newValue.contains("+") || newValue.contains("-") || newValue.contains("%") || newValue.contains("*") || newValue.contains("/")){
					
					ArrayList<Character> symbolsPresent = new ArrayList<Character>();
					
					for(int a = 0; a < newValue.length(); a++){
						if(newValue.charAt(a) == '+' || newValue.charAt(a) == '-' || newValue.charAt(a) == '*' || newValue.charAt(a) == '/' || newValue.charAt(a) == '%'){
							symbolsPresent.add(newValue.charAt(a));
						}
					}
					
					ArrayList<String> newValues = new ArrayList<String>();
					
					StringTokenizer vals = new StringTokenizer(newValue, "+-/*%");
					
					
					while(vals.hasMoreElements()){
						newValues.add(vals.nextToken().trim());
					}
					
					if(newValues.size() == 1){
						code.add(toCode + "mov " + variableUsed + ", " + newValue);
					}
					else{
						for(int a = 0; a < newValues.size(); a++){
							if(a == 0){
								code.add(toCode + "mov " + variableUsed + ", " + newValues.get(a));
							}
							else{
								if(symbolsPresent.get(a - 1) == '+'){
									code.add(toCode + "add " + variableUsed + ", " + newValues.get(a));
								}
								else if(symbolsPresent.get(a - 1) == '-'){
									code.add(toCode + "sub " + variableUsed + ", " + newValues.get(a));
								}
							}
						}
					}
				}
				else{
				code.add(toCode + "mov " + variableUsed + ", " + newValue);
				}
				
			}
			
			
		}
		
		System.out.println("------------------------------------CONVERTED ASSEMBLY CODE------------------------------------");
		
		String asmHeaders = ".model small \n\n.data \n\n";
		String middlePart = "\n.stack 100h \n\n.code \n\nmain proc \n\nmov ax, @data \nmov ds, ax \n\n";
		String endProgram = "mov ax, 4c00h \nint 21h \n\nmain endp \nend main\n";
		
		/*//File file2 = new File("testfile.asm");
		try {
			//BufferedWriter output = new BufferedWriter(new FileOutputStream(file));
			File file2 = new File("testfile.asm");
			FileWriter fw = new FileWriter(file2.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			
			bw.write(asmHeaders);
			for(String dataDeclarations : data){
				bw.write(dataDeclarations + "\n");
			}
			
			bw.write(middlePart);
			for(String codeBody : code){
				bw.write(codeBody + "\n\n");
			}
			
			bw.write(endProgram);
			bw.flush();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		
		System.out.println(asmHeaders);
		for(String dataDeclarations : data){
			System.out.println(dataDeclarations);
		}
		
		System.out.println(middlePart);
		System.out.println();
		for(String codeBody : code){
			//System.out.println(codeBody);
			System.out.println(codeBody + "\n");
		}
		
		System.out.println(endProgram);
		
		System.out.println(loop1End.toString());
		System.out.println(loop2End.toString());
		
		if(insideLoop1 && insideLoop2){
			System.out.println("OO");
		}
		//System.out.println(data.toString());
		System.out.println(variables.toString());
	
	}
  	
  }
}
