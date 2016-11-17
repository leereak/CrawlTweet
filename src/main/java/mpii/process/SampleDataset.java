package mpii.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SampleDataset {
	public static void main(String[] args){
		String filename="/home/lwang/Desktop/TrumpAndHillary/trump-1601-1611.txt";
		String writefile="/home/lwang/Desktop/TrumpAndHillary/sample-trump-1601-1611.txt";
		File file=new File(filename);
		BufferedWriter bw =null;
		FileWriter writer =null;
		try {
			writer = new FileWriter(writefile, true);
			bw=new BufferedWriter(writer);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new FileReader(file));
			String tempString = null;
	        int line = 1;
	        // 一次读入一行，直到读入null为文件结束
	        while ((tempString = reader.readLine()) != null) {
	            // 显示行号
	        	if(line%20==1){
	        		bw.write(tempString+"\n");
	        		System.out.println("line " + line);
	        	}	            
	            line++;
	        }
	        reader.close();
	        bw.close();
	        writer.close();
	        System.out.println("finish" + line);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
