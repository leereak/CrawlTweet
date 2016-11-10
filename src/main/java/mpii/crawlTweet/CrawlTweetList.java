package mpii.crawlTweet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import me.jhenrique.manager.TweetManager;
import me.jhenrique.manager.TwitterCriteria;
import me.jhenrique.model.Tweet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CrawlTweetList{
	private int count=0;
	private String userName="";
	private String query="";
	private String since="";
	private String until="";
	private String filePath="";
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getSince() {
		return since;
	}
	public void setSince(String since) {
		this.since = since;
	}
	public String getUntil() {
		return until;
	}
	public void setUntil(String until) {
		this.until = until;
	}
	public String crawl(){
		
		List<Tweet> tweets = null; 
		if(this.filePath.equals("")) return "no file path";
		CrawlAllTweets cat = new CrawlAllTweets();
		try {
			TwitterCriteria criteria = TwitterCriteria.create();
			if(!this.userName.equals("")) criteria.setUsername(this.userName);
			if(!this.query.equals("")) criteria.setQuerySearch(this.query);
			if(!this.since.equals("")) criteria.setSince(this.since);
			if(!this.until.equals("")) criteria.setUntil(this.until);			
			tweets = TweetManager.getTweets(criteria);
			System.out.println(tweets.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < tweets.size(); i++) {
			try {
//				if(i==85){
//					System.out.println();
//				}
				JSONObject job = new JSONObject();
				Tweet t = tweets.get(i);
				JSONObject firstTweet = JSONObject.fromObject(t);
				String user=t.getUsername();
				String tweetUrl = "/" + user + "/" + "status/" + t.getId();
				JSONArray jar = cat.crawlById(tweetUrl);
				job.put("firstTweet", firstTweet);
				job.put("conversation", jar);
				count = count + jar.size() + 1;
				writeFile(job);
				System.out.println("firstTweet: "+i+" total: "+count);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
		}
		return "crawl "+this.count+" tweets in file : "+this.filePath;
    
//    System.out.println(TweetManager.getTweets(criteria).size());
	}

	public void writeFile(JSONObject job) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(this.filePath), true),
					StandardCharsets.UTF_8));
			writer.write(job.toString() + ",\n");
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
	}		
	public static void main(String[] args) throws ParseException {
		Options options = new Options();
        options.addOption("s", "since", true, "since");
        options.addOption("u", "until", true, "until");
        options.addOption("o", "outdir", true, "output directory");
        options.addOption("q", "queries", true, "queries delimited by commas");
        options.addOption("n", "name", true, "query by username");
        CommandLineParser parser = new BasicParser();
        CommandLine cmd = parser.parse(options, args);
        String outdir = "/home/lwang/Desktop/result.txt", keydir = "", querystr = "";
        String since="2016-01-01", until="2016-11-01";
        String userName="";
        CrawlTweetList cbu=new CrawlTweetList();
		
		//cbu.setQuery("china two child policy");
		
        if (cmd.hasOption("o")) {
        	//set filepath
            outdir = cmd.getOptionValue("o");
    		cbu.setFilePath(outdir);
    		System.out.println(outdir);
        }
        if (cmd.hasOption("q")) {
        	//set query string 
            querystr = cmd.getOptionValue("q");
            cbu.setQuery(querystr);
            System.out.println(querystr);
        }
        if (cmd.hasOption("s")) {
        	// set since time
            since = cmd.getOptionValue("s");
            cbu.setSince(since);
            System.out.println(since);
        }
        if (cmd.hasOption("u")) {
        	//set until time
            until = cmd.getOptionValue("u");
            cbu.setUntil(until);	
            System.out.println(until);
        }
        if (cmd.hasOption("n")) {
        	//set userName
            userName = cmd.getOptionValue("n");
    		cbu.setUserName(userName);
    		System.out.println(userName);
        }
        //print start time
		SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String TimeString = time.format(new java.util.Date());
		System.out.println("start at"+TimeString);
		
		String result=cbu.crawl();
		System.out.println(result);  
		TimeString = time.format(new java.util.Date());
		System.out.println("end at"+TimeString);
		
    }

}
