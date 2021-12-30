package Talk.TalkServer.service;

import Talk.common.Message;
import Talk.common.MessageType;
import Talk.utils.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class SendNewsToAllService implements Runnable {

    @Override
    public void run() {
        //多次推送消息
        while (true) {
            System.out.println("请输入服务器推送消息内容[exit表示退出推送系统]");
            String news = Utility.readString(100);
            if (news.equals("exit"))break;
            //构建一个消息，进行群发
            Message message = new Message();
            message.setSender("服务器");
            message.setContent(news);
            message.setSendTIme(new Date().toString());
            message.setMessageType(MessageType.MESSAGE_TO_ALL_MES);
            System.out.println("服务器对所有人说：" + news);

            //遍历当前所有的消息进行转发消息
            String[] uids = ManageServerConnectClientThread.getOnlineUsers().split(" ");
            for (String uid : uids) {
                Socket socket = ManageServerConnectClientThread.getServerConnectClientThread(uid).getSocket();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
