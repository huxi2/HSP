package Talk.TalkServer.service;


import Talk.common.Message;
import Talk.common.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 保持和某个客户端的通信线程
 */
public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userId;

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        //线程持续运行和客户端进行消息通信
        while (true) {
            try {
                System.out.println("服务器和客户端 " + userId + " 保持通信...");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message) ois.readObject();
                //使用处理消息
                if (msg.getMessageType().equals(MessageType.MESSAGE_GET_ONLINE_FRIED)) {
                    //客户端查询在线用用户
                    System.out.println(msg.getSender() + " 查询在线用户列表");
                    String onlineUserList = ManageServerConnectClientThread.getOnlineUsers();
                    //返回message构造
                    Message returnMessage = new Message();
                    returnMessage.setMessageType(MessageType.MESSAGE_RET_ONLINE_FRIED);
                    returnMessage.setContent(onlineUserList);
                    returnMessage.setReceiver(msg.getSender());
                    //将message传递回去
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(returnMessage);
                } else if (msg.getMessageType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    //客户端请求退出系统
                    System.out.println(msg.getSender() + "请求退出系统...");
                    //收到消息，通知客户端确认，使客户端退出线程
                    Message message = new Message();
                    message.setMessageType(MessageType.MESSAGE_CLIENT_EXIT_CHECK);
                    message.setReceiver(msg.getSender());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);

                    //将先线程从线程管理类中删除
                    ManageServerConnectClientThread.removeServerConnectClientThread(msg.getSender());
                    //关闭socket
                    socket.close();
                    //退出线程
                    System.out.println(msg.getSender() + "退出成功");
                    break;
                } else if (msg.getMessageType().equals(MessageType.MESSAGE_COMM_MES)) {
                    //送的私聊消息
                    System.out.println(msg.getSender() + " 请求和 " + msg.getReceiver() + " 通信");
                    //获取 接收者 的线程取得socket进行通信
                    //接收者在线直接发送
                    if (ManageServerConnectClientThread.getOnlineUsers().contains(msg.getReceiver())){
                        ServerConnectClientThread receiverThread = ManageServerConnectClientThread.getServerConnectClientThread(msg.getReceiver());
                        ObjectOutputStream oos = new ObjectOutputStream(receiverThread.getSocket().getOutputStream());
                        oos.writeObject(msg);
                    }else{//接收者不在线
                        //将消息存储起来
                        QQServer.addOffLineMessage(msg.getReceiver(),msg);
                    }
                } else if (msg.getMessageType().equals(MessageType.MESSAGE_FILE_MES)) {
                    //客户端发送的私聊消息
                    System.out.println(msg.getSender() + " 请求和 " + msg.getReceiver() + " 文件传递");
                    //获取 接收者 的线程取得socket进行通信
                    ServerConnectClientThread receiverThread = ManageServerConnectClientThread.getServerConnectClientThread(msg.getReceiver());
                    ObjectOutputStream oos = new ObjectOutputStream(receiverThread.getSocket().getOutputStream());
                    oos.writeObject(msg);

                } else if (msg.getMessageType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    //客户端发送的群聊消息
                    System.out.println(msg.getSender() + " 发送群聊消息 " + msg.getContent());

                    //获取所有在线用户
                    String[] uids = ManageServerConnectClientThread.getOnlineUsers().split(" ");
                    for (String uid : uids) {
                        if (!uid.equals(msg.getSender())) {
                            //获取 接收者 的线程取得socket进行通信
                            ServerConnectClientThread receiverThread = ManageServerConnectClientThread.getServerConnectClientThread(uid);
                            ObjectOutputStream oos = new ObjectOutputStream(receiverThread.getSocket().getOutputStream());
                            oos.writeObject(msg);//若接收者不在线，可以保存到数据库实现离线通信
                        }
                    }

                } else {
                    System.out.println("其他不处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

