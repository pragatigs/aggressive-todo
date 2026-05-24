package com.todo.auth_service.service;

import com.todo.auth_service.exception.EmailSendingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    public enum EmailType {
        OTP,
        RESET_LINK
    }

    public void sendEmail(String toEmail, String content, String subject, EmailType type) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setFrom(from);
            helper.setSubject("✦ " + subject);
            helper.setText(buildEmailBody(content, type), true);

            javaMailSender.send(message);
            System.out.println("=========== mail sent ===========");

        } catch (Exception e) {
            throw new EmailSendingException("Error sending email, please try sometime later");
        }
    }

    private String buildEmailBody(String content, EmailType type) {

        String title;
        String subtitle;
        String contentBlock;

        if (type == EmailType.OTP) {

            title = "Please verify your email.";
            subtitle = """
                    Use the code below to verify your email address.<br/>
                    It expires in <span style="color:#f0f0f8;">10 minutes</span>.
                    """;

            contentBlock = """
                    <tr>
                      <td align="center" style="padding:0 40px 32px;">
                        <div style="
                            background:#0a0a0f;
                            border:1px solid #7c6dfa;
                            border-radius:16px;
                            padding:24px 40px;
                            display:inline-block;">
                          <p style="
                              margin:0 0 6px;
                              font-size:11px;
                              letter-spacing:2px;
                              text-transform:uppercase;
                              color:#6b6b8a;">
                              your code
                          </p>

                          <p style="
                              margin:0;
                              font-size:42px;
                              font-weight:700;
                              letter-spacing:12px;
                              color:#f0f0f8;
                              text-shadow:0 0 20px rgba(124,109,250,0.5);">
                              %s
                          </p>
                        </div>
                      </td>
                    </tr>
                    """.formatted(content);

        } else {

            title = "Reset your password";
            subtitle = """
                    We received a request to reset your password.<br/>
                    Click below to continue.<br/>
                    Link expires in <span style="color:#f0f0f8;">10 minutes</span>.
                    """;

            contentBlock = """
                    <tr>
                      <td align="center" style="padding:0 40px 30px;">
                        <a href="%s"
                           style="
                              display:inline-block;
                              padding:16px 34px;
                              border-radius:14px;
                              text-decoration:none;
                              font-weight:700;
                              font-size:15px;
                              color:white;
                              background:linear-gradient(135deg,#7c6dfa,#fa6d8f);
                              box-shadow:0 0 0 1px rgba(255,255,255,.08) inset,
                                         0 0 25px rgba(124,109,250,.45);
                           ">
                           Reset Password →
                        </a>
                      </td>
                    </tr>

                    <tr>
                      <td align="center" style="padding:0 40px 32px;">
                        <p style="
                            margin:0;
                            font-size:11px;
                            color:#6b6b8a;">
                            If button doesn't work:
                        </p>

                        <p style="
                            margin:12px 0 0;
                            font-size:11px;
                            color:#9b9bc0;
                            word-break:break-all;
                            line-height:1.6;">
                            %s
                        </p>
                      </td>
                    </tr>
                    """.formatted(content, content);
        }

        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                </head>

                <body style="
                    margin:0;
                    padding:0;
                    background:#0a0a0f;
                    font-family:'Courier New',monospace;">

                  <table width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                      <td align="center" style="padding:48px 24px;">

                        <table width="480" cellpadding="0" cellspacing="0"
                          style="
                            background:#13131a;
                            border:1px solid #2a2a3a;
                            border-radius:24px;
                            overflow:hidden;">

                          <tr>
                            <td style="
                                height:3px;
                                background:linear-gradient(90deg,transparent,#7c6dfa,transparent);">
                            </td>
                          </tr>

                          <tr>
                            <td align="center" style="padding:40px 40px 24px;">
                              <div>
                                <span style="
                                    font-size:22px;
                                    font-weight:800;
                                    color:#f0f0f8;">
                                    ✦ todo<span style="color:#7c6dfa;">auth</span>
                                </span>
                              </div>
                            </td>
                          </tr>

                          <tr>
                            <td align="center" style="padding:0 40px 24px;">
                              <h1 style="
                                  margin:0;
                                  font-size:28px;
                                  font-weight:800;
                                  color:#f0f0f8;">
                                  %s
                              </h1>
                            </td>
                          </tr>

                          <tr>
                            <td align="center" style="padding:0 40px 30px;">
                              <p style="
                                  margin:0;
                                  font-size:14px;
                                  line-height:1.7;
                                  color:#8a8aa3;">
                                  %s
                              </p>
                            </td>
                          </tr>

                          %s

                          <tr>
                            <td align="center" style="padding:0 40px 30px;">
                              <p style="
                                  margin:0;
                                  font-size:12px;
                                  color:#6b6b8a;
                                  line-height:1.6;">
                                  If you didn't request this, safely ignore this email.
                              </p>
                            </td>
                          </tr>

                          <tr>
                            <td style="padding:0 40px 24px;">
                              <div style="height:1px;background:#2a2a3a;"></div>
                            </td>
                          </tr>

                          <tr>
                            <td align="center" style="padding:0 40px 40px;">
                              <p style="
                                  margin:0;
                                  font-size:11px;
                                  color:#6b6b8a;">
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
                """.formatted(title, subtitle, contentBlock);
    }
}