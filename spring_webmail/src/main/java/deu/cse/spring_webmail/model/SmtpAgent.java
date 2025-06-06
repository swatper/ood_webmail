/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package deu.cse.spring_webmail.model;

import com.sun.mail.smtp.SMTPMessage;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jongmin
 */
@Slf4j
public class SmtpAgent {

    @Getter
    @Setter
    protected String host = null;
    @Getter
    @Setter
    protected String userid = null;
    @Getter
    @Setter
    protected String to = null;
    @Getter
    @Setter
    protected String cc = null;
    @Getter
    @Setter
    protected String subj = null;
    @Getter
    @Setter
    protected String body = null;
    @Getter
    @Setter
    protected String file1 = null;
    @Getter
    @Setter
    private List<String> attachments = new ArrayList<>();

    public SmtpAgent(String host, String userid) {
        this.host = host;
        this.userid = userid;
    }

    // LJM 100418 -  현재 로그인한 사용자의 이메일 주소를 반영하도록 수정되어야 함. - test only
    // LJM 100419 - 일반 웹 서버와의 SMTP 동작시 setFrom() 함수 사용 필요함.
    //              없을 경우 메일 전송이 송신주소가 없어서 걸러짐.
    public boolean sendMessage() {
        boolean status = false;

        // 1. property 설정
        Properties props = System.getProperties();
        props.put("mail.debug", false);
        props.put("mail.smtp.host", this.host);
        log.debug("SMTP host : {}", props.get("mail.smtp.host"));

        // 2. session 가져오기
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(false);

        try {
            SMTPMessage msg = new SMTPMessage(session);

            msg.setFrom(new InternetAddress(this.userid));  // 200102 LJM - 테스트 목적으로 수정

            // setRecipient() can be called repeatedly if ';' or ',' exists
            if (this.to.indexOf(';') != -1) {
                this.to = this.to.replace(";", ",");
            }
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.to));  // 200102 LJM - 수정

            if (this.cc.length() > 1) {
                if (this.cc.indexOf(';') != -1) {
                    this.cc = this.cc.replace(";", ",");
                }
                msg.setRecipients(Message.RecipientType.CC, this.cc);
            }


            msg.setSubject(this.subj);

            msg.setHeader("User-Agent", "LJM-WM/0.1");

            // body
            MimeBodyPart mbp = new MimeBodyPart();
            // Content-Type, Content-Transfer-Encoding 설정 의미 없음.
            // 자동으로 설정되는 것 같음. - LJM 041202
            mbp.setText(this.body);

            Multipart mp = new MimeMultipart("mixed");
            mp.addBodyPart(mbp);

            // 첨부 파일 추가
            if (attachments != null && !attachments.isEmpty()) {
                for (String filePath : attachments) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    DataSource src = new FileDataSource(filePath);
                    attachmentPart.setDataHandler(new DataHandler(src));
                    String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
                    attachmentPart.setFileName(MimeUtility.encodeText(fileName, "UTF-8", "B"));
                    mp.addBodyPart(attachmentPart);
                }
            }
            msg.setContent(mp);

            // 메일 전송
            Transport.send(msg);

            // 메일 전송 완료되었으므로 서버에 저장된
            // 첨부 파일 삭제함
            for (String filePath : attachments) {
                File f = new File(filePath);
                if (!f.delete()) {
                    log.error(filePath + ": 파일 삭제가 제대로 안 됨.");
                }
            }
            status = true;
        } catch (Exception ex) {
            log.error("sendMessage() error: {}", ex);
        }
        return status;
    }
}
