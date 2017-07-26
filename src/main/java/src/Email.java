package src;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ranran
 * @version V1.0
 * @Title:
 * @Package PACKAGE_NAME
 * @Description: 邮件 java 原生
 * @date 2017/7/26 15:31
 */
public class Email {






    /**
     * 设置发送邮件的基本信息, 包括地址，主题，内容
     *
     * @param desEmailAddr
     * @param subject
     * @param content
     * @param helper
     */
    public void setEmailBasicInfo(String desEmailAddr, String subject, String content, MimeMessageHelper helper) {
        try {
            helper.setText(content, true);
            helper.setTo(desEmailAddr);
            helper.setSubject(subject);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        Jlog.info("------------ add desEmailAddr subject content");
    }


    /**
     * 设置发送多个附件的邮件
     *
     * @param paths  多个附件 path 的集合
     * @param helper
     */
    public void setAttachments(ArrayList<String> paths, MimeMessageHelper helper) throws FileNotFoundException {
        try {
            // 添加附件
            for (String path : paths) {
                File file = new File(path);
                while (!file.exists()) {
                    // 睡 3s 等待下载文件
                    Thread.sleep(3000);
                }
                FileSystemResource fileSystemResource = new FileSystemResource(file);
                //用于解决邮件显示附件名中含有中文
                ClassPathResource fileName = new ClassPathResource(path);
                try {
                    helper.addAttachment(MimeUtility.encodeText(fileName.getFilename()), fileSystemResource);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Jlog.info("------------ add attachments");
    }




    /**
     * 选择一个账号登陆发送邮件
     *
     * @return
     */
    public Map chooseAuthBasicInfo() {


        Map<String, String> authInfo = new HashMap<>();
        authInfo.put("host", "host");
        authInfo.put("userName", "userName");
        authInfo.put("password", "pass");
        authInfo.put("port", "25");

        Jlog.info("sendEmailBatch threadName:" + Thread.currentThread().getName());

        return authInfo;
    }


    /**
     * 发送邮件，通用
     *
     * @param address
     * @param subject
     * @param content
     * @param attachmentInfo
     */
    public void sendMail(String address, String subject, String content, Map<String, String> attachmentInfo) throws FileNotFoundException, SendFailedException, MessagingException{
        // 解决附件名过长乱码问题
        System.getProperties().setProperty("mail.mime.splitlongparameters", "false");

        // 获取系统属性
        Properties properties = new Properties();

        Map<String, String> authInfo = chooseAuthBasicInfo();
        String host = authInfo.get("host");
        final String userName = authInfo.get("userName");
        final String password = authInfo.get("password");
        String port = authInfo.get("port");
        String from = userName;
        Jlog.info("sendEmailBatch userName:" + userName);
        Jlog.info("sendEmailBatch password:" + password);

        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");

        if (host.equals("smtp.exmail.qq.com")) {
            // qq 邮箱的 ssl 加密
            try {
                MailSSLSocketFactory sslSocketFactory = new MailSSLSocketFactory();
                sslSocketFactory.setTrustAllHosts(true);
                properties.put("mail.smtp.ssl.enable", "true");
                properties.put("mail.smtp.socketFactory", sslSocketFactory);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }

        Jlog.info("sendMail properties:" + properties);

        // 到服务器验证发送的用户名和密码是否正确  获取默认session对象
        Session session = Session.getInstance(properties, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
        try {
            Transport transport = session.getTransport("smtp");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        MimeMessage mimeMessage = new MimeMessage(session);

        InternetAddress sendFrom = new InternetAddress(from);
        Jlog.info("sendMail sendFrom: " + sendFrom.toString());
        mimeMessage.setFrom(sendFrom);
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

        setEmailBasicInfo(address, subject, content, helper);

        // 有附件
        if (!attachmentInfo.isEmpty()) {
            ArrayList<String> paths = new ArrayList<>();
            String attachmentJsonString = attachmentInfo.get("attachmentJsonString");
            String taskFileDir = attachmentInfo.get("taskFileDir");
            JSONArray jsonArray = JSON.parseArray(attachmentJsonString);
            for (Object jsonObject : jsonArray) {
                Map<String, String> attachmentAttributeMap = (Map<String, String>) jsonObject;
                String fileName = attachmentAttributeMap.get("fileName");
                String singleFilePath = taskFileDir + "/" + fileName;
                paths.add(singleFilePath);
                Jlog.info(attachmentAttributeMap);
            }
            setAttachments(paths, helper);
        }
        Transport.send(mimeMessage);

    }



    /**
     * 邮件消息的 url 提取 包括所有除 Img 标签的 http 或 https 开头的 url
     * @param htmlMessage
     * @return
     */
    public static HashMap<Integer, String> getUrlsFromEmailMessage(String htmlMessage) {
        Pattern patternImg = Pattern.compile("<img.*?src=[\"']?((https?)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]+\\.(jpg|gif|png))[\"']?");
        Pattern patternUrl = Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Matcher matcherImg = patternImg.matcher(htmlMessage);
        Matcher matcherUrl = patternUrl.matcher(htmlMessage);

        HashMap<Integer, String> linkOrderMap = new HashMap<>();
        List<String> urlLinkList = new ArrayList<>();
        List<String> imgLinkList = new ArrayList<>();

        while(matcherImg.find()) {
            String imgLink = matcherImg.group(1);
            imgLinkList.add(imgLink);
        }

        while(matcherUrl.find()) {
            String urlLink = matcherUrl.group();
            urlLinkList.add(urlLink);
        }

        urlLinkList.removeAll(imgLinkList);

        Jlog.info("links to be tracked:" + urlLinkList);

        for (int i = 0; i < urlLinkList.size(); i++) {
            linkOrderMap.put(i, urlLinkList.get(i));
        }

        return linkOrderMap;
    }


}
