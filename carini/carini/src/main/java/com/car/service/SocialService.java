package com.car.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Optional;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import com.car.dto.Member;
import com.car.persistence.MemberRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Service
public class SocialService {
   
   @Autowired
   private MemberRepository memberRepository;
   
   public String getKakaoAccessToken(String code,String clientId,String redirectUri) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + clientId); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri="+ redirectUri); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);
         
          //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            @SuppressWarnings("deprecation")
            JsonParser parser = new JsonParser();
            @SuppressWarnings("deprecation")
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();
         
         br.close();
         bw.close();
      }catch (Exception e) {
         e.printStackTrace();
      }
        return access_Token;
   }

   public HashMap<String, Object> getKakaoUserInfo (String access_Token) {
        
        //    요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        HashMap<String, Object> userInfo = new HashMap<>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            
            //    요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);
            
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);
            
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            
            String line = "";
            String result = "";
            
            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);
            
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            
            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
            
            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            System.out.println(nickname);
            String email = kakao_account.getAsJsonObject().get("email").getAsString();
            
            userInfo.put("nickname", nickname);
            userInfo.put("email", email);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return userInfo;
    }

   public void kakaoLogout(String accessToken) {
      
      String reqURL = "https://kapi.kakao.com/v2/user/logout";
      try {
         URL url = new URL(reqURL);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Authorization","Bearer"+accessToken);
         int responseCode = conn.getResponseCode();
         System.out.println("responseCode = " + responseCode);
      
         BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         
         String result = "";
         String line = "";
         
         while((line = br.readLine()) != null) {
            result = result+line;
         }
         System.out.println(result);

         
      }catch (Exception e) {
         e.printStackTrace();
      }
      
   }
   
   public Member kakaoSignUp(Member member) {
      
      Optional<Member> findeMember = memberRepository.findByMemberEmail(member.getMemberEmail());
      System.out.println(findeMember.isPresent());
      
      if(!findeMember.isPresent() || (findeMember.isPresent() && findeMember.get().getMemberSocial().equals("naver"))) {
         SecureRandom random = new SecureRandom();
         String Id = new BigInteger(130, random).toString(32);
         member.setMemberId(Id);
         member.setMemberEmail(member.getMemberEmail());
         member.setMemberName(member.getMemberNickname());
         member.setMemberNickname(member.getMemberNickname());
         member.setMemberSocial("kakao");
         member.setMemberRole("사용자");
         memberRepository.save(member);
         
         return member;
      }
      return findeMember.get();
   }
   
   // 네이버===============================================================================================
   
   
   // 인가 코드를 이용해 토큰 발급받기
   public String getNaverAccessToken(String code, String state, String client_id, String client_secret) {
      
      // 네이버에 요청 보내기
        WebClient webclient = WebClient.builder()
         .baseUrl("https://nid.naver.com")
         .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
         .build();

     
      String response = webclient.post()
            .uri(uriBuilder -> uriBuilder
              .path("oauth2.0/token")
              .queryParam("grant_type", "authorization_code")
              .queryParam("client_id", client_id)
              .queryParam("client_secret", client_secret)
              .queryParam("code", code)
              .queryParam("state", state)
              .build())
            .retrieve().bodyToMono(String.class).block();
      
      //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
      @SuppressWarnings("deprecation")
      JsonParser parser = new JsonParser();
      @SuppressWarnings("deprecation")
      JsonObject object = parser.parse(response).getAsJsonObject();

       return object.get("access_token").toString();
   }
   
   
   // 토큰으로 사용자 정보 가져오기
   public HashMap<String, Object> getNaverUserInfo(String accessToken) {
      
      HashMap<String, Object> userInfo = new HashMap<>();
      
       // 사용자 정보 요청하기
       WebClient webClient = WebClient.builder()
           .baseUrl("https://openapi.naver.com")
           .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
           .build();
       
       String response = webClient.get()
           .uri(uriBuilder -> uriBuilder
               .path("v1/nid/me")
               .build())
           .header("Authorization", "Bearer " + accessToken)
           .retrieve()
           .bodyToMono(String.class)
           .block();
       
       System.out.println("response : " + response);
       
       //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
       @SuppressWarnings("deprecation")
       JsonParser parser = new JsonParser();
       @SuppressWarnings("deprecation")
       JsonObject object = parser.parse(response).getAsJsonObject();

       JsonObject res = object.get("response").getAsJsonObject();

       if (res != null) {
          
          String id = res.get("id").toString();
          String name = res.get("name").toString();
          String nickname = res.get("nickname").toString();
          String email = res.get("email").toString();
          String mobile = res.get("mobile").toString();
          
          System.out.println("id: "+id);
          System.out.println("name: "+ name);
          System.out.println("nickname: "+ nickname);
          System.out.println("email: "+ email);
          System.out.println("mobile: "+ mobile);
          
          userInfo.put("id", id);
          userInfo.put("name", name);
          userInfo.put("nickname", nickname);
          userInfo.put("email", email);
          userInfo.put("mobile", mobile);
          
       } // if end
       
       return userInfo;
   }
   
   public Member naverSignUp(Member member) {
         
         Optional<Member> findeMember = memberRepository.findByMemberEmail(member.getMemberEmail());
         System.out.println(findeMember.isPresent());
         
         if(!findeMember.isPresent() || (findeMember.isPresent() && findeMember.get().getMemberSocial().equals("kakao"))) {
            
        	 member.setMemberId(member.getMemberId().replace("\"", ""));
        	 member.setMemberName(member.getMemberName().replace("\"", ""));
        	 member.setMemberNickname(member.getMemberNickname().replace("\"", ""));
        	 member.setMemberEmail(member.getMemberEmail().replace("\"", ""));
        	 member.setMemberPhoneNum(member.getMemberPhoneNum().replace("\"", ""));
        	 member.setMemberSocial("naver");
        	 member.setMemberRole("사용자");;
        	 memberRepository.save(member);
            
        	 return member;
        	 
         }
         return findeMember.get();
   }
   
}