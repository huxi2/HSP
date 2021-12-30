package Talk.TalkClient.service;


import Talk.common.Message;
import Talk.common.MessageType;

import java.io.*;
import java.net.Socket;

public class FileClientService {

    /**
     * @param senderId   发送者
     * @param receiverId 接收者
     * @param sourcePath 源地址
     * @param destPath   目的地址
     */
    public static void sendFileToOne(String senderId, String receiverId, String sourcePath, String destPath) {
        // 封装消息
        Message message = new Message();
        message.setMessageType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setReceiver(receiverId);
        message.setSrc(sourcePath);
        message.setDest(destPath);

        //读取文件
        FileInputStream fis = null;
        byte[] fileByte = new byte[(int) new File(sourcePath).length()];
        try {
            //读取文件
            fis = new FileInputStream(sourcePath);
            fis.read(fileByte);
            //存到message
            message.setFileBytes(fileByte);
            message.setFileLen(fileByte.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("\n" + senderId + " 给 " + receiverId + " 发送 "
                + sourcePath + " 到 " + destPath);

        //正式发送文件
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.
                    getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
