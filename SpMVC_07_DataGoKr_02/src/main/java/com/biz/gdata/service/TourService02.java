package com.biz.gdata.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.stereotype.Service;

import com.biz.gdata.config.DataGoConfig;
import com.biz.gdata.domain.CityVO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

@Service
public class TourService02 {

	public String getDataString(String queryString) throws IOException {

		URL url = new URL(queryString);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

		httpConn.setRequestMethod("GET");

		int resCode = httpConn.getResponseCode();

		BufferedReader buffer = null;
		if (resCode == 200) {
			InputStreamReader is = new InputStreamReader(httpConn.getInputStream());
			buffer = new BufferedReader(is);
		} else {
			InputStreamReader is = new InputStreamReader(httpConn.getInputStream());
			buffer = new BufferedReader(is);
		}
		String retString = "";
		String reader = "";
		while (true) {
			reader = buffer.readLine();
			if (reader == null)
				break;
			retString += reader;

		}
		buffer.close();
		return retString;

	}

//	public List<CityVO> getData() throws JsonSyntaxException, IOException {

		// JSONParser jParer = new JSONParser();
//		JsonElement jElement = JsonParser.parseString(this.getDataString());
//
//		// response tag
//		JsonObject oRes = (JsonObject) jElement.getAsJsonObject().get("response");
//		// response.body
//		JsonObject oBody = (JsonObject) oRes.get("body");
//		// response.body.items
//		JsonObject oItems = (JsonObject) oBody.get("items");
//		// response.body.items.item
//		JsonArray oItemList = (JsonArray) oItems.get("item");
//
//		// JsonArray를 List로 변환하기 위한 변환 바구니 생성
//		TypeToken<List<CityVO>> cityToken = new TypeToken<List<CityVO>>() {
//		};
//
//		/*
//		 * gson에 있는 class JsonArray 데이터를 List형데이터로 변환하는 도구
//		 * 
//		 * 1.Token도구를 사용하여 List<Class> 형으로 변환하는 method 호출 2.Gson을 이용하여 List로 변환
//		 */
//		Gson gson = new Gson();
//		List<CityVO> cityList = gson.fromJson(oItemList, cityToken.getType());
//
//		return cityList;
//	}


	public String getQueryString() throws UnsupportedEncodingException {
		String queryString = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaCode";

		queryString += "?ServiceKey=" + DataGoConfig.goDataAuth;
		queryString += "&MobileApp=" + URLEncoder.encode(DataGoConfig.MY_APP_NAME, "UTF-8");
		queryString += "&_type=json";
		queryString += "&MobileOS=ETC";

		queryString += String.format("&numOfRows=%d", 20);
		queryString += String.format("&pageNo=%d", 1);

		return queryString;

	}
}
