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
  
  }
}
