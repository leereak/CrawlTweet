package mpii.crawlTweet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * Hello world!
 *
 */
public class CrawlAllTweets
{
	static Logger logger = Logger.getLogger(CrawlAllTweets.class);
	private int count=0;//count all the tweets
	private int counta=0;
	private JSONArray alltweets=new JSONArray();
	//crawl all the conversation tweets by tweetId
	public JSONArray crawlById(String tweetUrl){
		this.alltweets.clear();
		String userName="";
		String tweetId="";
		try{
			String [] temp=tweetUrl.split("/");
			userName=temp[1];
			tweetId=temp[3];
		}catch(Exception e){
			e.printStackTrace();
			logger.error("tweetUrl is not formal");
			return null;
		}
		
		try {		 	
//		 	String containerClassName="js-tweet-text-container";
//		 	String stream_containerClassName="stream-container";
//		 	String min_position_attr="data-min-position";	    
			String url=Constant.twitterURL+tweetUrl;
			//String firstpage=restGet(url);
			//Document doc = Jsoup.parse(firstpage);
			Document doc = Jsoup.connect(url).get();
			//System.out.println(url);
		 	//check the page number
			
		 	Elements stream_containers = doc.getElementsByClass("stream-container");		 	
		 	String min_position=stream_containers.get(0).attr("data-min-position");								 			 	

		 	
	 		//process the first page
		 	String process_result=processPage(doc,true);
		 	if(process_result.equals("comment")) {
		 		System.out.println("its a comment");
		 		return this.alltweets;
		 	}
		 	
		 	//process showing more pages by scrolling 
		 	while(!min_position.equals("null")&&!min_position.equals("")){
		 		String getUrl=Constant.query_l+userName+"/conversation/"+tweetId+Constant.query_m+min_position+Constant.query_r;
		 		//System.out.println(getUrl);
		 		String result=restGet(getUrl); 
		        JSONObject job= JSONObject.fromObject(result);  
		        min_position=job.get("min_position").toString();
		        String items_html=job.getString("items_html");	
		        //System.out.println(items_html);
		        items_html=" <html><body><head><title> </title></head> "+items_html+" </body></html>";
		        Document moredoc=Jsoup.parse(items_html);		        
		        processPage(moredoc,false);
		 	}		 	
		}catch(Exception e){
			e.printStackTrace();
			logger.error("show more error or first page error");
			return null;
		}
		return this.alltweets;
	}
	//process the whole conversation page
	public String processPage(Document doc, boolean ifFirst){
		HashMap tweet=new HashMap();						
		Elements contents=doc.getElementsByClass("content");
		if(ifFirst){
			contents.remove(0);
		}
		//if(contents.size()<1) return;
		for (Element content:contents){
			try {
				String classname=content.attr("class");
				if(classname.equals("content clearfix")){
					alltweets.clear();
					logger.info("its a comment");
					return "comment";
				}
				Element streamContainer=content.getElementsByClass("stream-item-header").first();
				Element smalltime=null;
				try {
					smalltime=streamContainer.child(1);
				} catch (Exception e) {
					System.out.println("content label not match");
					logger.error("content label not match");
					continue;
				}
				//user id,href,label,avatar
				//Element acountgroup=streamContainer.getElementsByClass("js-nav").first();				
				Element acountgroup=streamContainer.child(0);
//				if(acountgroup==null){
//					acountgroup=streamContainer.getElementsByClass("js-user-profile-link").first();
//				}
				tweet.put("data_user_id", acountgroup.attr("data-user-id"));
				tweet.put("user_href", acountgroup.attr("href"));
//				Element strong=acountgroup.getElementsByClass("show-popup-with-id").first();
//				if(strong==null){
//					strong=acountgroup.getElementsByClass("js-action-profile-name").first();
//				}
				Element strong=acountgroup.child(1);
				tweet.put("user_label", strong.text());
//				Element img=acountgroup.getElementsByClass("js-action-profile-avatar").first();
//				tweet.put("user_avatar", img.attr("src"));
				
//				Element time=streamContainer.getElementsByClass("js-short-timestamp").first();
//				if(time==null){
//					time=streamContainer.getElementsByClass("js-relative-timestamp").first();
//				}
				
				//Element smalltime=streamContainer.child(1);
				Element smalla=smalltime.child(0);
				//tweet url id
				tweet.put("tweet_id", smalla.attr("href"));	
				
				//tweet time
				Element time=smalla.child(0);
				tweet.put("tweet_time", time.attr("data-time-ms"));
				
				
				//tweet context
				Element textContainer=content.getElementsByClass("js-tweet-text-container").first();
				String tweetcontext=textContainer.text().replaceAll("[^\\u0000-\\uFFFF]", "");
				tweet.put("tweet_context", tweetcontext);
				//tweet mentioned person
			 	//Elements mentions = textContainer.getElementsByClass("data-mentioned-user-id");		
				Elements mentions = textContainer.select("a[data-mentioned-user-id]");
				if(mentions!=null){
					String [] mentionUsers=new String [mentions.size()];
					for (int i = 0; i < mentionUsers.length; i++) {
						mentionUsers[i]=mentions.get(i).attr("data-mentioned-user-id");					
					}
					tweet.put("mention_user", mentionUsers);				
				}
				
				//tweet reply, retweet, like
			 	Elements comments = content.getElementsByClass("ProfileTweet-actionCount");				
				tweet.put("tweet_reply", comments.get(0).attr("data-tweet-stat-count"));
				tweet.put("tweet_retweet",comments.get(1).attr("data-tweet-stat-count"));
				tweet.put("tweet_like", comments.get(2).attr("data-tweet-stat-count"));
				//hashtags
				Elements hashtags=content.select("a.twitter-hashtag.pretty-link");
				tweet.put("hashtag", processHashtags(hashtags));
				//transform to json
				JSONObject jtweet=JSONObject.fromObject(tweet);
//				System.out.println(jtweet.toString());
				alltweets.add(jtweet);
//				count=count+1;
//			 	System.out.println(count+"---");
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("error in a tweet");
				continue;
			}
				 	
		}
		
	 	
	 	//counta=counta+tempcon.size();
		//process show more by click the button
		Elements showMores= doc.getElementsByClass("ThreadedConversation-showMore");		
	 	if(showMores!=null&&showMores.size()>0){
	 		for(Element showMore:showMores){
		 		String threadTweetId=showMore.attr("data-expansion-url");
		 		String url=Constant.twitterURL+threadTweetId;		 	
		 		//System.out.println(url);
		 		try {
		 			String result=restGet(url);
		 			JSONObject job=JSONObject.fromObject(result);  
			        String conversation_html=job.get("conversation_html").toString();
			        conversation_html=" <html><body><head><title> </title></head> "+conversation_html+" </body></html>";
			        Document showmoredoc=Jsoup.parse(conversation_html);
					processPage(showmoredoc,false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error("process page error");
					return "error";
				}
		 	}
	 	}
	 	return "success";
	 }
	//send a GET rest request
	public String restGet(String url){
		String result="";
		DefaultHttpClient client = new DefaultHttpClient();  
        HttpGet request = new HttpGet(url);
        //System.out.println(url);
        try{
        	HttpResponse response = client.execute(request);
        	//Thread.sleep(1000); 
            //System.out.println("Response Code: " + response.getStatusLine().getStatusCode());  	
            BufferedReader rd = new BufferedReader(  
                    new InputStreamReader(response.getEntity().getContent())); 
            String line = "";  
            while((line = rd.readLine()) != null) {  
            	result=result+line;	        } 
            rd.close();
        }catch(Exception e){
        	e.printStackTrace();
        	logger.error("rest get error");
        	return null;
        }   
        request.releaseConnection();
		return result;
	}
	private static String processHashtags(Elements hashtags) {
		StringBuilder sb = new StringBuilder();
		for(Element hashtag:hashtags){
			sb.append(hashtag.text());
			sb.append(" ");
		}
		
		return sb.toString().trim();
	}
	public static void main(String[] args) {
		CrawlAllTweets cot = new CrawlAllTweets();
    	//String tweetid ="/ErinSchrode/status/794255752055562240";
		String tweetid="/HillaryClinton/status/795003547968385024";
		//String tweetid="/realDonaldTrump/status/794281898570825728";
    	JSONArray jry=cot.crawlById(tweetid);
 		System.out.println(jry.toString()); 
    }
}
