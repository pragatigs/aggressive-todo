package com.todo.auth_service.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.todo.auth_service.exception.EmailSendingException;

import org.springframework.beans.factory.annotation.Value;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;


    public void sendEmail(String toEmail, String otp) {
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setFrom(from);
            helper.setSubject("✦ Your TodoAuth verification code");
            helper.setText(buildEmailBody(otp), true); // true = isHtml
            javaMailSender.send(message);
        }
        catch (Exception e){
            throw new EmailSendingException("Error sending OTP, please try sometime later");
        }
    }

    private String buildEmailBody(String otp) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8"/>
              <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
            </head>
            <body style="margin:0;padding:0;background:#0a0a0f;font-family:'Courier New',monospace;">
              <table width="100%" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" style="padding:48px 24px;">
                    <table width="480" cellpadding="0" cellspacing="0"
                      style="background:#13131a;border:1px solid #2a2a3a;border-radius:24px;overflow:hidden;">

                      <!-- Top glow bar -->
                      <tr>
                        <td style="height:3px;background:linear-gradient(90deg,transparent,#7c6dfa,transparent);"></td>
                      </tr>

                      <!-- Header -->
                      <tr>
                        <td align="center" style="padding:40px 40px 24px;">
                          <div style="display:inline-flex;align-items:center;gap:10px;">
                            <div style="width:36px;height:36px;background:linear-gradient(135deg,#7c6dfa,#fa6d8f);border-radius:10px;display:inline-block;text-align:center;line-height:36px;font-size:18px;">✦</div>
                            <span style="font-size:20px;font-weight:800;color:#f0f0f8;letter-spacing:-0.5px;">todo<span style="color:#7c6dfa;">auth</span></span>
                          </div>
                        </td>
                      </tr>

                      <!-- Title -->
                      <tr>
                        <td align="center" style="padding:0 40px 8px;">
                          <p style="margin:0;font-size:11px;letter-spacing:3px;text-transform:uppercase;color:#7c6dfa;">verification code</p>
                        </td>
                      </tr>
                      <tr>
                        <td align="center" style="padding:0 40px 24px;">
                          <h1 style="margin:0;font-size:28px;font-weight:800;color:#f0f0f8;letter-spacing:-0.5px;">Check your inbox.</h1>
                        </td>
                      </tr>

                      <!-- Subtitle -->
                      <tr>
                        <td align="center" style="padding:0 40px 32px;">
                          <p style="margin:0;font-size:14px;color:#6b6b8a;line-height:1.6;">
                            Use the code below to verify your email address.<br/>
                            It expires in <span style="color:#f0f0f8;">10 minutes</span>.
                          </p>
                        </td>
                      </tr>

                      <!-- OTP Box -->
                      <tr>
                        <td align="center" style="padding:0 40px 32px;">
                          <div style="background:#0a0a0f;border:1px solid #7c6dfa;border-radius:16px;padding:24px 40px;display:inline-block;">
                            <p style="margin:0 0 6px;font-size:11px;letter-spacing:2px;text-transform:uppercase;color:#6b6b8a;">your code</p>
                            <p style="margin:0;font-size:42px;font-weight:700;letter-spacing:12px;color:#f0f0f8;text-shadow:0 0 20px rgba(124,109,250,0.5);">""" + otp + """
                            </p>
                          </div>
                        </td>
                      </tr>

                      <!-- Warning -->
                      <tr>
                        <td align="center" style="padding:0 40px 16px;">
                          <p style="margin:0;font-size:12px;color:#6b6b8a;line-height:1.6;">
                            If you didn't request this, you can safely ignore this email.<br/>
                            Never share this code with anyone.
                          </p>
                        </td>
                      </tr>

                      <!-- Divider -->
                      <tr>
                        <td style="padding:0 40px 24px;">
                          <div style="height:1px;background:#2a2a3a;"></div>
                        </td>
                      </tr>

                      <!-- Footer -->
                      <tr>
                        <td align="center" style="padding:0 40px 40px;">
                          <p style="margin:0;font-size:11px;color:#6b6b8a;">
                            Sent by todoauth · your funky todo app
                          </p>
                        </td>
                      </tr>

                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """;
    }
}