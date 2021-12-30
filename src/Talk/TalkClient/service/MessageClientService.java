package Talk.TalkClient.service;

import Talk.common.Message;
import Talk.common.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

public class MessageClientService {

    /**
     * 私聊消息
     *
     * @param senderId   发送者
     * @param receiverId 接收者
     * @param content    消息
     */
    public static void sendMessageToOne(String senderId, String receiverId, String content) {
        //创建消息对象
        Message message = new Message();
        message.setMessageType(MessageType.MESSAGE_COMM_MES);
        message.setSender(senderId);
        message.setReceiver(receiverId);
        message.setContent(content);
        message.setSendTIme(new Date().toString());
        System.out.println(senderId + " 向 " + receiverId + " 说： " + content);
        //获取发送者线程 进行通信
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 群发消息
     *
     * @param senderId 发送者
     * @param content  内容
     */
    public static void sendMessageToAll(String senderId, String content) {
        //创建消息对象
        Message message = new Message();
        message.setMessageType(MessageType.MESSAGE_TO_ALL_MES);
        message.setSender(senderId);
        message.setContent(content);
        message.setSendTIme(new Date().toString());
        System.out.println(senderId + " 对大家说： " + content);
        //获取发送者线程 进行通信
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
