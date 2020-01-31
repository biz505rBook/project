package com.biz.ems.service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.biz.ems.config.NAVER;
import com.biz.ems.domain.NaverMember;
import com.biz.ems.domain.NaverMemberResponse;
import com.biz.ems.domain.NaverReturnAuth;
import com.biz.ems.domain.NaverTokenVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NaverLoginService {

	@Value("${naver.client.id}")
	private String clientID;
	@Value("${naver.client.secret}")
	private String clientSec;

	private final String loginAPI_URL = "https://nid.naver.com/oauth2.0/authorize";
	private final String tokenAPI_URL = "https://nid.naver.com/oauth2.0/token";
	private final String naverMemberAPI_URL = "https://openapi.naver.com/v1/nid/me";
	private final String callbackLocalURL = "http://localhost:8080/ems/naver/ok";
	private final String callbackURL = "https://callor.com:12600/ems_qussoa/member/naver/ok";

	public String oAuthLoginGet() {

		String redirectURI = null;

		try {
			redirectURI = URLEncoder.encode(callbackURL, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SecureRandom random = new SecureRandom();

		// 최대값 100fit 크기 임의의 정수를 생성하여 문잘려 만들기
		String stateKey = new BigInteger(130, random).toString();
		log.debug("StateKey : " + stateKey);

		/*
		 * spring 4.1에서 사용하는 Uriquery 문자열을 생성하는 클래스
		 * 
		 * fromHttpUri() : 요청을 수행할 server 주소
		 * queryparam() : 변수=값 형태의 query문자열 생성
		 * build(true) : 생성하는 문자열중에 encoding이 필요한 부분이 있으면 encoding 수행
		 * 5.1이상에서는 별도로 encoding() method가 있다
		 * queryparam("name","kerea") &name =korea
		 */
		String apiURL = 		
				UriComponentsBuilder.fromHttpUrl(loginAPI_URL)
					.queryParam("client_id", this.clientID)
					.queryParam("response_type", "code")
					.queryParam("redirect_uri", callbackURL)
					.queryParam("state", stateKey)
					.build(true).toUriString();		
//		
//		apiURL = loginAPI_URL;
//
//		apiURL += "?client_id=" + this.clientID;
//		apiURL += "&response_type=code";
//		apiURL += "&redirect_uri=" + redirectURI;
//		apiURL += "&state=" + stateKey;

		log.debug("getLoginURL : ", apiURL);
		return apiURL;
	}

	/**
	 * 네이버에 정보 요구를 할 때 사용할 토큰을 요청 token을 요청할 때 GET/POST method를 사용할 수 있ㄴ느데
	 * 
	 * 여기서는 POST를 사용해서 요청을 하겟다
	 * 
	 * @param naverOk
	 */
	public NaverTokenVO oAuthAcessGetToken(NaverReturnAuth naverOk) {

		String redirectURL = null;
		try {
			redirectURL = URLEncoder.encode(callbackURL, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Naver-Client-id", this.clientID);
		headers.set("X-Naver-Client-secret", this.clientSec);

		/*
		 * Map interface 상속 받아 작성된 spring framework 전용 Map interface Http protocol과 관련된
		 * 곳에서 기본 Map 대신 사용하는 interface Hppt protocol과 관련된 내장 method가 포함된어 있다
		 */
		MultiValueMap<String, String> bodies= new LinkedMultiValueMap<String, String>();

		bodies.add(NAVER.TOKEN.grant_type, NAVER.GRANT_TYPE.authorization_code);
		bodies.add(NAVER.TOKEN.client_id, this.clientID);
		bodies.add(NAVER.TOKEN.client_secret, this.clientSec);
		bodies.add(NAVER.TOKEN.code, naverOk.getCode());
		bodies.add(NAVER.TOKEN.state, naverOk.getState());

		// headers에 담긴 정보와
		// Params에 담긴 정보를
		// HttpEntity 데이터로 변환
		HttpEntity<MultiValueMap<String, String>> request 
		= new HttpEntity<MultiValueMap<String, String>>(bodies,	headers);

		URI restURI = null;
		try {
			restURI = new URI(tokenAPI_URL);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * RestTemplate를 사용하여 네이버에 token을 요청
		 */
		RestTemplate restTemp = new RestTemplate();
		ResponseEntity<NaverTokenVO> result = null;

		result = restTemp.exchange(restURI, HttpMethod.POST, request, NaverTokenVO.class);

		log.debug("Naver Token : " + result.getBody().toString());

		return result.getBody();
	}

	public NaverMember getNaverMemberInfo(NaverTokenVO tokenVO) {

		String token = tokenVO.getAccess_token();
		String header = "bearer " + token;

		// header 문자열을 GET의 http Header에 실어서
		// GET 방식으로 요청
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", header);

		/*
		 * body 부분에 parameter라는 문자열을 추가하고
		 * header부분에 위의 headers에서 설정한 형식으로 생성하고
		 * 	Authorization = "bearer.."
		 * 모두 문자열로 변환하여 Http Protocol 데이터 구조로 변경
		 * 그리고 Http Protocol을 사용하여 데이터를 전송할 수 있도록 준비
		 * HpptEntity<String> 형식으로 선언하면 : 단일 생성방식
		 * 
		 * HttpEntity<MultiValue<String,Object<t>> 형식으로 선언하면
		 * body부분에 더이터를 다음과 같이 생성
		 *  변수=[값], 변수=[값]
		 *  header 부분의 데이터는 [변수;값, 변수;값]
		 */
		
		HttpEntity<String> request = new HttpEntity<String>("parameter" , headers);
		
		log.debug("Entity : " + request.toString());

		ResponseEntity<NaverMemberResponse> result;
		RestTemplate restTemp = new RestTemplate();

		result = restTemp.exchange(naverMemberAPI_URL, HttpMethod.GET, request,
									NaverMemberResponse.class);
		
		NaverMember member = result.getBody().response;
		
		return member;
	}

}
