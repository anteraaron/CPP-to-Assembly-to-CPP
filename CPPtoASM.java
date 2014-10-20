import java.util.*;
import java.io.*;

public class CPPtoASM {
	

	public static void main(String[] args) {
		String filename, ASMstring = "";
		int stringCounter = 0;
		boolean newline = false;
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the filename of C++ file: ");
		filename = input.nextLine();
		try {
			String flnm = filename.replaceAll(".cpp", ".asm");
			FileWriter fileWriter = null;
	        File newTextFile = new File(flnm);
	        fileWriter = new FileWriter(newTextFile);
	        
	        fileWriter.write(".model small\n");
	        fileWriter.write(".data\n");
	        
			Scanner reader = new Scanner(new FileInputStream(filename));
			
			while(reader.hasNextLine()) {
				String line = reader.nextLine();
				line = line.replaceAll("\t", "");
				line = line.replaceAll("\n", "");
				line = line.replaceAll("\";", " \";");
				StringTokenizer st = new StringTokenizer(line, " ");
				
				while(st.hasMoreTokens()) {
					String word = st.nextToken();
					if(word.equals("cout")) {
						String word2 = st.nextToken();
						if(word2.equals("<<")) {
							while(st.hasMoreTokens()) {
								String word3 = st.nextToken();
								if(word3.equals("\";")) {
									newline = false;
									break;
								} else {
									if(word3.equals("<<") && st.nextToken().equals("endl;")) {
										newline = true;
										break;
									} else {
										ASMstring += " " + word3;
									}
								}
							}
						}
						stringCounter++;
						if(!newline) {
							fileWriter.write("str" + stringCounter + " db " + ASMstring + "\",'$'\n");
						} else {
							fileWriter.write("str" + stringCounter + " db " + ASMstring + ",0ah,'$'\n");
						}
						
						ASMstring = "";
					}
				}  
				
			}
	        fileWriter.write(".stack 100h\n");
	        fileWriter.write(".code\n");
	        fileWriter.write("main proc\n\n");
	        fileWriter.write("mov ax, @data\n");
	        fileWriter.write("mov ds, ax\n\n");
	        
	        for(int i = 1; i <= stringCounter; i++) {
	        	fileWriter.write("lea dx, str" + i + "\n");
				fileWriter.write("mov ah, 09h\n");
				fileWriter.write("int 21h\n\n");
	        }
	        
			fileWriter.write("mov ax, 4c00h\n");
			fileWriter.write("int 21h\n\n");
			fileWriter.write("main endp\n");
		    fileWriter.write("end main\n");
			reader.close();
			fileWriter.close();
		}
		catch(Exception e) {
			System.out.println("Error occured.");
		}
		System.out.println("DONE!");
	}

}
