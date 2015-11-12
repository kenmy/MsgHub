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

import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Pavel_Goncharenko
 */
public class VK {
	
	private long id;
	private String text;
	private Date date;
	private String attachment;

	VK(String line) throws ParseException {
		JSONObject json2 = (JSONObject)new JSONParser().parse(line);
		id = Long.parseLong(json2.get("id").toString());
		text = json2.get("text").toString();
		if ("".equals(text)) {
			if (json2.get("copy_history") != null) {
				JSONArray json3 = (JSONArray)new JSONParser().parse(json2.get("copy_history").toString());
				if (json3.size()> 0) {
					JSONObject json4 = (JSONObject)new JSONParser().parse(json3.get(0).toString());
					text = json4.get("text").toString();
				}
			} else {
				text = "";
			}
		}
		date = new Date(Long.parseLong(json2.get("date").toString()) * 1000);
		attachment = findAttachment(json2);
	}
	
	public long getId() {
		return id;
	}
	
	public String getText() {
		return text;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getAttachment() {
		return attachment;
	}
	
	private String findAttachment(JSONObject js) throws ParseException {
		String att = "";
		System.out.println("att0" + js.get("id").toString());
		if (js.get("attachments") != null) {
			System.out.println("att1");
			JSONArray attach = (JSONArray)new JSONParser().parse(js.get("attachments").toString());
			if (attach.size()> 0) {
				System.out.println("att2");
				JSONObject attach1 = (JSONObject)new JSONParser().parse(attach.get(0).toString());
				if (attach1.get("type") != null && "photo".equals(attach1.get("type").toString())) {
					System.out.println("att3");
					JSONObject photo = (JSONObject)new JSONParser().parse(attach1.get("photo").toString());
					if (photo.get("photo_604") != null) {
						att = photo.get("photo_604").toString();
					}
				}
			}
		}
		return att;
	}

}
