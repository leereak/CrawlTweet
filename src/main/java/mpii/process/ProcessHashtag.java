package mpii.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ProcessHashtag {
	
	private String filename="/home/lwang/Desktop/TrumpAndHillary/result/pcthashtag.txt";
	private String writefile="/home/lwang/Desktop/TrumpAndHillary/result/process-pcthashtag.txt";
	public void process(){
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
	        HashMap<String, Integer> hm=new HashMap();
	        while ((tempString = reader.readLine()) != null) {
	        	String [] temp=tempString.split(",");
	        	int tagNum=Integer.parseInt(temp[1]);
	        	String [] tags=temp[0].split(" ");
	        	for(String tag:tags){
	        		if(hm.get(tag)!=null){
	        			int oldtagnum=hm.get(tag);
	        			hm.put(tag, oldtagnum+tagNum);
	        		}else{
	        			hm.put(tag, tagNum);
	        		}
	        	}	        	
	            line++;
	        }
	        for(Map.Entry<String, Integer> entry:hm.entrySet()){
	        	String ws=entry.getKey()+";"+entry.getValue()+"\n";
	        	bw.write(ws);
	        }
	        reader.close();
	        bw.close();
	        writer.close();
	        System.out.println("finish" + line);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args){
		ProcessHashtag pf=new ProcessHashtag();
		pf.process();
		
	}

}
