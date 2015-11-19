/*
 * Copyright 2015 Pavel_Goncharenko.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.usbo.skypetwitter;

import com.skype.Chat;
import com.skype.Skype;
import com.skype.SkypeException;
import static com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;

/**
 *
 * @author Pavel_Goncharenko
 */
public class Run {
	
	private static String chat_group_id = "#ChatGroupID/Depersonilized"; //real
	private static String twitter_user_id;
	private static Integer twitter_timeout;
	
	private static Integer bChanged;
	
	private static ArrayList<Long> twitter_ids = new ArrayList<Long>();
	private static ArrayList<Long> vk_ids = new ArrayList<Long>();
	private static ArrayList<VK> vk = new ArrayList<VK>();
	private static String work_dir;
	
	public static void main(String[] args) throws SkypeException {
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		Properties props;
		props = (Properties) System.getProperties().clone();
		//loadProperties(props, "D:\\Data\\Java\\classpath\\twitter.props");
		work_dir = System.getProperty("user.dir");
		loadProperties(props, work_dir + "\\twitter.props");
		twitter_user_id = props.getProperty("twitter.user");
		twitter_timeout = Integer.parseInt(props.getProperty("twitter.timeout"));
		System.out.println("Twitter user: " + twitter_user_id);
		System.out.println("Twitter timeout: " + twitter_timeout);
		if ("".equals(twitter_user_id)) {
			return;
		}
		if (load_file() == 0) {
			System.out.println("File not found");
			return;
		}
		while (true) {
			bChanged = 0;
			/*
			create_id();
			Chat ch = Chat.getInstance(chat_group_id);
			ch.send("Привет!");
			*/
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			System.out.println("Looking at " + sdf.format(Calendar.getInstance().getTime()));
			Chat ch = Skype.chat(chat_group_id);
			Twitter twitter = new TwitterFactory().getInstance();
			try {
				List<Status> statuses;
				statuses = twitter.getUserTimeline(twitter_user_id);
				//System.out.println("Showing @" + twitter_user_id + "'s user timeline.");
				String sText;
				for (Status status : statuses) {
					//System.out.println(status.getId());
					Date d = status.getCreatedAt();
					// Добавим  часов
					Calendar cal = Calendar.getInstance(); 
					cal.setTime(d);
					cal.add(Calendar.HOUR_OF_DAY, 7); 
					d = cal.getTime();
					sText = "@" + status.getUser().getScreenName() + " от " + sdf.format(d) + " ( https://twitter.com/" + twitter_user_id + "/status/" + status.getId() + " ): \r\n" + status.getText() + "\r\n***";
					for (URLEntity e : status.getURLEntities())	{
						sText = sText.replaceAll(e.getURL(), e.getExpandedURL());
					}
					for (MediaEntity e : status.getMediaEntities()) {
						sText = sText.replaceAll(e.getURL(), e.getMediaURL());
					}						
					if (twitter_ids.indexOf(status.getId()) == -1) {
						System.out.println(sText);
						ch.send(sText);
						twitter_ids.add(status.getId());
						bChanged = 1;
					}
				}
			} catch (TwitterException te) {
				te.printStackTrace();
				System.out.println("Failed to get timeline: " + te.getMessage());
				//System.exit(-1);
			} catch (SkypeException ex) {
				ex.printStackTrace();
				System.out.println("Failed to send message: " + ex.getMessage());
			}
			
			// VK
			try {
				vk();
				for (VK v : vk) {
					if (vk_ids.indexOf(v.getId()) == -1) {
						Date d = v.getDate();
						// Добавим  часов
						Calendar cal = Calendar.getInstance(); 
						cal.setTime(d);
						cal.add(Calendar.HOUR_OF_DAY, 7); 
						d = cal.getTime();
						String sText = "@Depersonilized (VK) от " + sdf.format(d) + " ( http://vk.com/Depersonilized?w=wall-0_" + v.getId() + " ): \r\n" + v.getText();
						if (!"".equals(v.getAttachment())) {
							sText += "\r\n" + v.getAttachment();
						}
						sText += "\r\n***";
						System.out.println(sText);
						ch.send(sText);
						vk_ids.add(v.getId());
						bChanged = 1;
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
				System.out.println("Failed to get vk: " + e.getMessage());
				//System.exit(-1);
			} catch (SkypeException ex) {
				ex.printStackTrace();
				System.out.println("Failed to send message: " + ex.getMessage());
			}
			if (bChanged == 1) {
				save_file();
			}
			try {
				Thread.sleep(1000 * 60 * twitter_timeout);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
				System.out.println("Failed to sleep: " + ex.getMessage());
			}
		}
    }
	
	public static void create_id() throws Exception  {
		Chat ch = Skype.chat("Depersonilized1, Depersonilized2");
		String id = ch.getId();
		System.out.println(id);
	}
	
	public static void save_file() {
		try{
         FileOutputStream fos= new FileOutputStream(work_dir + "\\twitter_ids.data");
         ObjectOutputStream oos= new ObjectOutputStream(fos);
         oos.writeObject(twitter_ids);
         oos.close();
         fos.close();
       }catch(IOException ioe){
            ioe.printStackTrace();
        }
		
		try{
         FileOutputStream fos= new FileOutputStream(work_dir + "\\vk_ids.data");
         ObjectOutputStream oos= new ObjectOutputStream(fos);
         oos.writeObject(vk_ids);
         oos.close();
         fos.close();
       }catch(IOException ioe){
            ioe.printStackTrace();
        }
	}
	
	public static int load_file() {
		// TWITTER
		try
        {
            FileInputStream fis = new FileInputStream(work_dir + "\\twitter_ids.data");
            ObjectInputStream ois = new ObjectInputStream(fis);
            twitter_ids = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
         }catch(IOException ioe){
             ioe.printStackTrace();
             return 0;
          }catch(ClassNotFoundException c){
             System.out.println("Class not found");
             c.printStackTrace();
             return 0;
          }
		// VK
		try
        {
            FileInputStream fis = new FileInputStream(work_dir + "\\vk_ids.data");
            ObjectInputStream ois = new ObjectInputStream(fis);
            vk_ids = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
         }catch(IOException ioe){
             ioe.printStackTrace();
             return 0;
          }catch(ClassNotFoundException c){
             System.out.println("Class not found");
             c.printStackTrace();
             return 0;
          }
		return 1;
	}
	
	private static boolean loadProperties(Properties props, String path) {
        FileInputStream fis = null;
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                fis = new FileInputStream(file);
                props.load(fis);
                return true;
            }
        } catch (Exception ignore) {
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ignore) {

            }
        }
        return false;
    }
	
	private static void vk() throws ParseException {
		String url = "https://api.vk.com/method/wall.get?v=5.28&domain=Depersonilized&filter=owner&extended=1";
		String line = "";
		try {
			URL url2 = new URL(url);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url2.openStream(), "UTF-8"));
			line = reader.readLine();
			reader.close();

		} catch (MalformedURLException e) {
			// ...
		} catch (IOException e) {
			// ...
		}
		JSONObject json = (JSONObject)new JSONParser().parse(line);
		json = (JSONObject)new JSONParser().parse(json.get("response").toString());
		JSONArray jsona = (JSONArray) new JSONParser().parse(json.get("items").toString());
		vk.clear();
		for (int i = 0; i < jsona.size(); i++) {
			vk.add(new VK(jsona.get(i).toString()));
		}
	}
	
}
