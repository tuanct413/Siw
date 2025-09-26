package com.example.demo.common.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import java.util.Random;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public String sendRandomCodeEmail(String to, String subject) throws MessagingException {
        // Tạo mã OTP 6 chữ số
        String code = String.format("%06d", new Random().nextInt(1000000));

        // Tạo nội dung HTML đẹp và hiện đại
        String htmlContent = """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Xác thực email</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        margin: 0;
                        padding: 20px;
                        min-height: 100vh;
                    }
                    
                    .email-wrapper {
                        max-width: 600px;
                        margin: 0 auto;
                        background-color: #ffffff;
                        border-radius: 16px;
                        overflow: hidden;
                        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        padding: 40px 30px;
                        text-align: center;
                        color: white;
                    }
                    
                    .header h1 {
                        font-size: 28px;
                        font-weight: 600;
                        margin-bottom: 8px;
                        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    }
                    
                    .header p {
                        font-size: 16px;
                        opacity: 0.9;
                        font-weight: 300;
                    }
                    
                    .content {
                        padding: 40px 30px;
                    }
                    
                    .greeting {
                        font-size: 18px;
                        color: #2c3e50;
                        margin-bottom: 20px;
                        font-weight: 500;
                    }
                    
                    .message {
                        font-size: 16px;
                        color: #5a6c7d;
                        line-height: 1.6;
                        margin-bottom: 30px;
                    }
                    
                    .code-container {
                        background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
                        border-radius: 12px;
                        padding: 30px;
                        text-align: center;
                        margin: 30px 0;
                        border: 2px dashed #dee2e6;
                        position: relative;
                    }
                    
                    .code-container::before {
                        content: '🔐';
                        font-size: 24px;
                        position: absolute;
                        top: -12px;
                        left: 50%;
                        transform: translateX(-50%);
                        background: white;
                        padding: 0 10px;
                    }
                    
                    .code-label {
                        font-size: 14px;
                        color: #6c757d;
                        text-transform: uppercase;
                        letter-spacing: 1px;
                        margin-bottom: 10px;
                        font-weight: 600;
                    }
                    
                    .verification-code {
                        font-size: 36px;
                        font-weight: 700;
                        color: #495057;
                        letter-spacing: 8px;
                        font-family: 'Courier New', monospace;
                        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    }
                    
                    .instructions {
                        background-color: #e3f2fd;
                        border-left: 4px solid #2196f3;
                        padding: 20px;
                        border-radius: 0 8px 8px 0;
                        margin: 30px 0;
                    }
                    
                    .instructions p {
                        color: #1565c0;
                        font-size: 15px;
                        line-height: 1.5;
                        margin: 0;
                    }
                    
                    .divider {
                        height: 1px;
                        background: linear-gradient(90deg, transparent, #dee2e6, transparent);
                        margin: 30px 0;
                        border: none;
                    }
                    
                    .footer {
                        background-color: #f8f9fa;
                        padding: 25px 30px;
                        text-align: center;
                        color: #6c757d;
                        font-size: 14px;
                        line-height: 1.5;
                    }
                    
                    .warning {
                        background-color: #fff3cd;
                        border: 1px solid #ffeaa7;
                        border-radius: 8px;
                        padding: 15px;
                        margin-top: 20px;
                    }
                    
                    .warning p {
                        color: #856404;
                        margin: 0;
                        font-size: 13px;
                    }
                    
                    @media (max-width: 600px) {
                        .email-wrapper {
                            margin: 10px;
                            border-radius: 12px;
                        }
                        
                        .header, .content, .footer {
                            padding: 25px 20px;
                        }
                        
                        .verification-code {
                            font-size: 28px;
                            letter-spacing: 4px;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="email-wrapper">
                    <div class="header">
                        <h1>Xác thực Email</h1>
                        <p>Bảo mật tài khoản của bạn</p>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">Xin chào! 👋</div>
                        
                        <div class="message">
                            Cảm ơn bạn đã đăng ký tài khoản. Để hoàn tất quá trình đăng ký và bảo mật tài khoản, 
                            vui lòng sử dụng mã xác thực bên dưới:
                        </div>
                        
                        <div class="code-container">
                            <div class="code-label">Mã xác thực</div>
                            <div class="verification-code">{{CODE}}</div>
                        </div>
                        
                        <div class="instructions">
                            <p><strong>Hướng dẫn:</strong> Nhập mã này vào trang xác thực để kích hoạt tài khoản. 
                            Mã có hiệu lực trong 10 phút.</p>
                        </div>
                    </div>
                    
                    <hr class="divider">
                    
                    <div class="footer">
                        <p><strong>Lưu ý bảo mật:</strong></p>
                        <div class="warning">
                            <p>
                                ⚠️ Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này. 
                                Không chia sẻ mã xác thực với bất kỳ ai khác.
                            </p>
                        </div>
                        <p style="margin-top: 20px; color: #adb5bd; font-size: 12px;">
                            © 2024 - Email được gửi tự động, vui lòng không trả lời.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """;

        // Thay thế placeholder bằng mã code thực
        htmlContent = htmlContent.replace("{{CODE}}", code);

        // Tạo MimeMessage và gửi
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML

        mailSender.send(message);

        System.out.println("Mail đã gửi tới: " + to + " với mã: " + code);
        return code;
    }

}
