package com.zy.cn.util;

import com.zy.cn.entity.MailSenderInfo;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class SendMailUtil {

    /**
     * @param mailSenderInfo 邮件对象
     * @throws Exception
     */
    public static boolean sendMail(MailSenderInfo mailSenderInfo) throws Exception {

        Properties prop = new Properties();
        prop.put("mail.transport.protocol", mailSenderInfo.getProtocol());// 连接协议
        prop.put("mail.smtp.host", mailSenderInfo.getMailServerHost());// 主机名
        prop.put("mail.smtp.port", mailSenderInfo.getMailServerPort());// 端口号
        prop.put("mail.smtp.auth", mailSenderInfo.isValidate());
        prop.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接 ---一般都使用
        prop.put("mail.debug", mailSenderInfo.isDisplayMessage());// 设置是否显示debug信息 true 会在控制台显示相关信息
        Transport ts = null;
        try {
            //使用JavaMail发送邮件的5个步骤
            //1、创建session
            Session session = Session.getInstance(prop);
            //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
            session.setDebug(true);
            //2、通过session得到transport对象
            ts = session.getTransport();
            //3、连上邮件服务器
            ts.connect(mailSenderInfo.getMailServerHost(),mailSenderInfo.getUserName() , mailSenderInfo.getPassword());
            //4、创建邮件
            Message message = createAttachMail(session,mailSenderInfo);
            //5、发送邮件
            ts.sendMessage(message, message.getAllRecipients());
            ts.close();
        }catch (Exception e){
            e.printStackTrace();
            ts.close();
            return false;
        }
        return true;
    }

    /**
     * @Method: createAttachMail
     * @Description: 创建一封带附件的邮件
     * @Anthor:孤傲苍狼
     *
     * @param session
     * @return
     * @throws Exception
     */
    public static MimeMessage createAttachMail(Session session,MailSenderInfo mailSenderInfo) throws Exception{
        MimeMessage message = new MimeMessage(session);

        //设置邮件的基本信息
        //发件人
        message.setFrom(new InternetAddress(mailSenderInfo.getFromAddress()));
        //收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(mailSenderInfo.getToAddress()));
        //邮件标题
        message.setSubject(mailSenderInfo.getSubject());
        //抄送人   多个抄送人之间以逗号间隔,不能有空格
        InternetAddress[] cc = new InternetAddress().parse(mailSenderInfo.getCcAddress());
        message.setRecipients(Message.RecipientType.CC, cc);

        //创建邮件正文，为了避免邮件正文中文乱码问题，需要使用charset=UTF-8指明字符编码
        MimeBodyPart text = new MimeBodyPart();
        text.setContent(mailSenderInfo.getContent(), "text/html;charset=UTF-8");

        //创建邮件附件
        MimeBodyPart attach = new MimeBodyPart();
        DataHandler dh = new DataHandler(new FileDataSource(mailSenderInfo.getFilePath()));
        attach.setDataHandler(dh);
        attach.setFileName(dh.getName());  //

        //创建容器描述数据关系
        MimeMultipart mp = new MimeMultipart();
        mp.addBodyPart(text);
        mp.addBodyPart(attach);
        mp.setSubType("mixed");

        message.setContent(mp);
        message.saveChanges();
        //将创建的Email写入到E盘存储
        //message.writeTo(new FileOutputStream("E:\\attachMail.eml"));
        //返回生成的邮件
        return message;
    }

    //发送QQ邮件的相关参数
    public static void main(String[] args) throws Exception {
        MailSenderInfo mailSenderInfo = new MailSenderInfo();
        mailSenderInfo.setMailServerHost("smtp.qq.com");
        mailSenderInfo.setMailServerPort("465");
        mailSenderInfo.setValidate(true);
        //控制台不打相关信息
        mailSenderInfo.setDisplayMessage(false);
        mailSenderInfo.setProtocol("smtp");
        mailSenderInfo.setUserName("2088686608@qq.com");
        mailSenderInfo.setPassword("wmfaappluhoxbfbe");
        mailSenderInfo.setFromAddress("2088686608@qq.com");
        mailSenderInfo.setToAddress("875538935@qq.com");
        mailSenderInfo.setCcAddress("1181742229@qq.com");
        mailSenderInfo.setSubject("JavaMail邮件发送测试");
        mailSenderInfo.setContent("邮件内容");
        mailSenderInfo.setFilePath("f:\\poi.xlsx");
        boolean b = SendMailUtil.sendMail(mailSenderInfo);
        System.out.println(b);
    }
    //发送163邮箱
}
