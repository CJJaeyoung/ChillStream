package com.example.chillStream.service;


import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
   private final JavaMailSender emailSender;
   public final String ePw = createKey();
   
   private MimeMessage createMessage(String to) throws Exception {
      System.out.println("보내는 대상 : " + to);
      System.out.println("인증 번호 : " + ePw);
      MimeMessage message = emailSender.createMimeMessage();
      
      message.addRecipients(Message.RecipientType.TO, to);//보내는 대상
      message.setSubject("ChillStream. 회원 가입 이메일 인증"); //제목
      
      String msgg = "";
      msgg += "<div style='margin:20px;'>";
      msgg += "<h1> 안녕하세요 편안한 ChillStream 입니다. </h1>";
      msgg += "<br>";
      msgg += "<p>아래 코드를 복사해 입력해주세요.<p>";
      msgg += "<br>";
      msgg += "<p>감사합니다</p>";
      msgg += "<br>";
      msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
      msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
      msgg += "<div style='font-size:130%'>";
      msgg += "CODE: <strong>";
      msgg += ePw + "</strong><div><br/>";
      msgg += "</div>";
      message.setText(msgg, "utf-8", "html"); //내용
      message.setFrom(new InternetAddress("****@naver.com", "ChillStream"));
      
      return message;
   }
   
   public static String createKey() {
      StringBuffer key = new StringBuffer();
      Random r = new Random();
      
      for (int i = 0; i < 8; i++) {
         int index = r.nextInt(3);
         switch (index) {
            case 0:
               key.append((char) ((int) r.nextInt(26) + 97));//a~z
               break;
            case 1:
               key.append((char) ((int) (r.nextInt(26)) + 65));//A~Z
               break;
            case 2:
               key.append(r.nextInt(10));//0~9
               break;
         }
      }
      return key.toString();
      
   }
   
   public String sendSimpleMessage(String to) throws Exception {
      MimeMessage message = createMessage(to);
      try {
         emailSender.send(message);
      } catch (MailException es) {
         es.printStackTrace();
         throw new IllegalArgumentException();
      }
      return ePw;
   }
   
   public void sendMail(String to, String subject, String text) {
      try {
         MimeMessage message = emailSender.createMimeMessage();
         MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // UTF-8 인코딩 설정
         
         helper.setTo(to);
         helper.setSubject(subject);
         helper.setText(text, true);
         helper.setFrom("****@naver.com");
         
         emailSender.send(message);
         System.out.println("Email sent successfully to " + to);
      } catch (Exception e) {
         System.err.println("Error sending email: " + e.getMessage());
         throw new RuntimeException(e);
      }
   }
}
