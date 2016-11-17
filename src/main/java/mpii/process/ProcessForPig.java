package mpii.process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ProcessForPig {
	private String filename="/home/lwang/Desktop/TrumpAndHillary/trump-1601-1611.txt";
	private String writefile="/home/lwang/Desktop/TrumpAndHillary/pc-trump-1601-1611.txt";
	public void conversationTweet(){
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
	        while ((tempString = reader.readLine()) != null) {
	        	tempString=tempString.substring(0, tempString.length()-1);
	        	tempString=tempString.replace("\t", " ");
	        	JSONObject job=JSONObject.fromObject(tempString);
	        	JSONObject firsttweet=job.getJSONObject("firstTweet");
	        	JSONArray conversation=job.getJSONArray("conversation");
	        	
	        	for (int i = 0; i < conversation.size(); i++) {
	        		String result="";
		        	result=result+firsttweet.getString("id")+"\t";
					JSONObject con=conversation.getJSONObject(i);
					result=result+con.getString("tweet_id")+"\t";
					result=result+con.getString("data_user_id")+"\t";
					result=result+con.getString("user_label")+"\t";
					result=result+con.getString("tweet_like")+"\t";
					result=result+con.getString("tweet_reply")+"\t";
					result=result+con.getString("tweet_retweet")+"\t";
					result=result+con.getString("tweet_time")+"\t";
					String mention=con.getJSONArray("mention_user").toString();
					mention=mention.replace("[", "").replace("]", "").replace("\"","");
					result=result+mention+"\t";
					//match hashtag
					String context=con.getString("tweet_context");
					context=context.replaceAll("[^\\u0000-\\uFFFF]", "");
					Pattern MY_PATTERN = Pattern.compile("#(\\S+)");
					Matcher mat = MY_PATTERN.matcher(context);
					String hashtag="";
					while (mat.find()) {
						  //System.out.println(mat.group(1));
						  //if(mat.group(1).length()>30) continue;
						  hashtag+=mat.group(1)+" ";
						}
					hashtag=hashtag.replace(",", "");
					result=result+hashtag+"\t";
					result=result+context+"\n";
					
					bw.write(result);
					
				}
	        	
	        	System.out.println(line);
//	        	System.out.println(line);
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
	public void firsttweet(){
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
	        while ((tempString = reader.readLine()) != null) {
	        	tempString=tempString.substring(0, tempString.length()-1);
	        	tempString=tempString.replace("\t", " ");
	        	JSONObject job=JSONObject.fromObject(tempString);
	        	JSONObject firsttweet=job.getJSONObject("firstTweet");
	        	JSONArray conversation=job.getJSONArray("conversation");
	        	String result="";
	        	result=result+firsttweet.getString("id")+"\t";
	        	result=result+firsttweet.getString("username")+"\t";
	        	result=result+firsttweet.getJSONObject("date").getString("time")+"\t";
	        	result=result+firsttweet.getString("favorites")+"\t";
	        	result=result+firsttweet.getString("retweets")+"\t";
	        	result=result+conversation.size()+"\t";
	        	result=result+firsttweet.getString("hashtags")+"\t";	        	
	        	result=result+firsttweet.getString("mentions")+"\t";
	        	result=result+firsttweet.getString("text")+"\n";	
	        	bw.write(result);
	        	System.out.println(line);
//	        	System.out.println(line);
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
	public static void main(String[] args){
		ProcessForPig pf=new ProcessForPig();
		//pf.firsttweet();
		pf.conversationTweet();
		
	}

}
