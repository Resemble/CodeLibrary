package src;

/**
 * @author ranran
 * @version V1.0
 * @Title:
 * @Package PACKAGE_NAME
 * @Description: 邮件 java 原生
 * @date 2017/7/26 15:31
 */
public class Email {


//    // 每发送10次睡
//    private final int sendMaxThenSleep = 10;
//
//    // 睡10s
//    private final int sleepTimeMs = 10000;
//
//    private static AtomicInteger sendTaskTimeRecord = new AtomicInteger(0);
//
//    private static AtomicInteger sendEmailTimeRecord = new AtomicInteger(0);
//
//    /**
//     * 设置发送邮件的基本信息, 包括地址，主题，内容
//     *
//     * @param desEmailAddr
//     * @param subject
//     * @param content
//     * @param helper
//     */
//    public void setEmailBasicInfo(String desEmailAddr, String subject, String content, MimeMessageHelper helper) {
//        try {
//            helper.setText(content, true);
//            helper.setTo(desEmailAddr);
//            helper.setSubject(subject);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//
//        Jlog.info("------------ add desEmailAddr subject content");
//    }
//
//
//    /**
//     * 设置发送多个附件的邮件
//     *
//     * @param paths  多个附件 path 的集合
//     * @param helper
//     */
//    public void setAttachments(ArrayList<String> paths, MimeMessageHelper helper) throws FileNotFoundException {
//        try {
//            // 添加附件
//            for (String path : paths) {
//                File file = new File(path);
//                while (!file.exists()) {
//                    // 睡 3s 等待下载文件
//                    Thread.sleep(3000);
//                }
//                FileSystemResource fileSystemResource = new FileSystemResource(file);
//                //用于解决邮件显示附件名中含有中文
//                ClassPathResource fileName = new ClassPathResource(path);
//                try {
//                    helper.addAttachment(MimeUtility.encodeText(fileName.getFilename()), fileSystemResource);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Jlog.info("------------ add attachments");
//    }
//
//
//    /**
//     * 批量发送邮件, 并且更新 notice_tasks_result_info 表 和 notice_tacks 表
//     *
//     * @param items
//     * @return
//     */
//    public Boolean sendEmailBatch(List<NoticeTasksResultEntity> items) {
//
//        long startTime;
//        long endTime;
//        String sendTime = "";
//        String backTime = "";
//        String taskFileDir = "";
//        String detailInfo;
//        String sendStatus;
//        int taskId;
//        int len = items.size();
//        Map<String, String> attachmentInfo = new HashMap();
//
//        startTime = System.currentTimeMillis();
//
//        for (int i = 0; i < len; i++) {
//
//            controlFrequency();
//
//            NoticeTasksResultEntity item = items.get(i);
//            taskId = item.getTaskId();
//            String attachmentJsonString = noticeTaskService.getAttachmentByTaskId(taskId);
//
//            if ((attachmentJsonString != null) && (!"".equals(attachmentJsonString))) {
//                taskFileDir = attachmentStorePath + "/" + taskId;
//                attachmentInfo.put("attachmentJsonString", attachmentJsonString);
//                attachmentInfo.put("taskFileDir", taskFileDir);
//            }
//
//            try {
//
//                sendTime = Jdate.getNowStrTime();
//
//                sendMail(item.getAddress(), item.getSubject(), item.getMessage(), attachmentInfo);
//
//                detailInfo = "success";
//                sendStatus = AppConstants.TASK_RESULT_STATUS_SUCCESS;
//            } catch (SendFailedException e) {
//                Jlog.error("SendFailedException send emailAddress:" + item.getAddress());
//                Jlog.error("SendFailedException send email error:" + e.getMessage());
//                Address[] unsend = e.getValidUnsentAddresses();
//                if (null != unsend) {
//                    // 登录异常会再次选出来发送
//                    detailInfo = e.getMessage();
//                    sendStatus = AppConstants.TASK_RESULT_STATUS_AUTH_FAILED;
//                } else {
//                    detailInfo = e.getMessage();
//                    sendStatus = AppConstants.TASK_RESULT_STATUS_FAILED;
//                }
//            } catch (Exception e) {
//                Jlog.error("Exception send emailAddress:" + item.getAddress());
//                Jlog.error("Exception send email error:" + e.getMessage());
//                detailInfo = e.getMessage();
//                sendStatus = AppConstants.TASK_RESULT_STATUS_FAILED;
//
//            }
//
//            sendResultDeal(item.getRiid(), sendStatus, detailInfo, sendTime, taskFileDir);
//        }
//
//        endTime = System.currentTimeMillis();
//        System.out.println("发送一批邮件花费时间： " + (endTime - startTime) + " ms");
//        return true;
//    }
//
//    /**
//     * 选择一个账号登陆发送邮件
//     *
//     * @return
//     */
//    public Map chooseAuthBasicInfo() {
//
//        List<Integer> idList = emailAuthInfoService.getIdList();
//        int order = sendTaskTimeRecord.incrementAndGet() % idList.size();
//        int id = idList.get(order);
//        EmailAuthInfoEntity emailAuthInfoEntity = emailAuthInfoService.getEmailAuthInfoById(id);
//
//        Map<String, String> authInfo = new HashMap<>();
//        authInfo.put("host", emailAuthInfoEntity.getHost());
//        authInfo.put("userName", emailAuthInfoEntity.getUserName());
//        authInfo.put("password", emailAuthInfoEntity.getPassword());
//        authInfo.put("port", emailAuthInfoEntity.getPort());
//
//        Jlog.info("sendEmailBatch threadName:" + Thread.currentThread().getName());
//        Jlog.info("sendEmailBatch sendTaskTimeRecord:" + sendTaskTimeRecord);
//
//        return authInfo;
//    }
//
//
//    /**
//     * 发送邮件，通用
//     *
//     * @param address
//     * @param subject
//     * @param content
//     * @param attachmentInfo
//     */
//    public void sendMail(String address, String subject, String content, Map<String, String> attachmentInfo) throws FileNotFoundException, SendFailedException, MessagingException{
//        // 解决附件名过长乱码问题
//        System.getProperties().setProperty("mail.mime.splitlongparameters", "false");
//
//        // 获取系统属性
//        Properties properties = new Properties();
//
//        Map<String, String> authInfo = chooseAuthBasicInfo();
//        String host = authInfo.get("host");
//        String userName = authInfo.get("userName");
//        String password = authInfo.get("password");
//        String port = authInfo.get("port");
//        String from = userName;
//        Jlog.info("sendEmailBatch userName:" + userName);
//        Jlog.info("sendEmailBatch password:" + password);
//
//        // 设置邮件服务器
//        properties.setProperty("mail.smtp.host", host);
//        properties.put("mail.smtp.port", port);
//        properties.put("mail.smtp.auth", "true");
//
//        if (host.equals(qqEmailAuthHost)) {
//            // qq 邮箱的 ssl 加密
//            try {
//                MailSSLSocketFactory sslSocketFactory = new MailSSLSocketFactory();
//                sslSocketFactory.setTrustAllHosts(true);
//                properties.put("mail.smtp.ssl.enable", "true");
//                properties.put("mail.smtp.socketFactory", sslSocketFactory);
//            } catch (GeneralSecurityException e) {
//                e.printStackTrace();
//            }
//        }
//
//        Jlog.info("sendMail properties:" + properties);
//
//        // 到服务器验证发送的用户名和密码是否正确  获取默认session对象
//        Session session = Session.getInstance(properties, new Authenticator() {
//            public PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(userName, password);
//            }
//        });
//        try {
//            Transport transport = session.getTransport("smtp");
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        }
//        MimeMessage mimeMessage = new MimeMessage(session);
//
//        InternetAddress sendFrom = new InternetAddress(from);
//        Jlog.info("sendMail sendFrom: " + sendFrom.toString());
//        mimeMessage.setFrom(sendFrom);
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
//
//        setEmailBasicInfo(address, subject, content, helper);
//
//        // 有附件
//        if (!attachmentInfo.isEmpty()) {
//            ArrayList<String> paths = new ArrayList<>();
//            String attachmentJsonString = attachmentInfo.get("attachmentJsonString");
//            String taskFileDir = attachmentInfo.get("taskFileDir");
//            JSONArray jsonArray = JSON.parseArray(attachmentJsonString);
//            for (Object jsonObject : jsonArray) {
//                Map<String, String> attachmentAttributeMap = (Map<String, String>) jsonObject;
//                String fileName = attachmentAttributeMap.get("fileName");
//                String singleFilePath = taskFileDir + "/" + fileName;
//                paths.add(singleFilePath);
//                Jlog.info(attachmentAttributeMap);
//            }
//            setAttachments(paths, helper);
//        }
//        Transport.send(mimeMessage);
//
//    }


}
