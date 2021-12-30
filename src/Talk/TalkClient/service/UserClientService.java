package Talk.TalkClient.service;


import Talk.common.Message;
import Talk.common.MessageType;
import Talk.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 主要用来 创建 发送给服务器的链接请求
 */
public class UserClientService {
    //其他地方可能也用到
    private User u = new User();
    private Socket socket;
    private boolean res = false;

    /**
     * 客户端发送消息给服务器进行登录校验
     * @param userId uid
     * @param passwd pwd
     * @return 登陆结果
     */
    public boolean checkUser(String userId, String passwd) {
        //创建对象
        u.setUserId(userId);
        u.setPasswd(passwd);

        //链接服务端，发送用户u
        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            //得到objectOutputStream 发送user对象到服务器
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);
            //接受服务器端的登录校验返回
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();

            if (ms.getMessageType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {
                //登录成功  创建一个线程保持和服务器端进行通信
                ClientConnectServerThread cCst = new ClientConnectServerThread(socket);
                cCst.start();//启动线程
                //将该线程放入线程管理数组中
                ManageClientConnectServerThread.addClientConnectionServerThread(userId, cCst);
                res = true;
            } else {
                //登录失败，未建立通信线程。但是已经启动socket了
                socket.close();
                res = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 获取在线好友列表
     */
    public void onlineFriendList(){
        //发送一个Message 类型 MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMessageType(MessageType.MESSAGE_GET_ONLINE_FRIED);
        message.setSender(u.getUserId());

        //将消息发送给服务器
        try {
            //通过线程得到相应的输出流
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出系统
     */
    public void clientExit() {
        //创建客户端退出message
        Message message = new Message();
        message.setSender(u.getUserId());
        message.setMessageType(MessageType.MESSAGE_CLIENT_EXIT);

        //通过socket发送消息
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭当前用户的socket并将通讯线程从管理中删除 socket的关闭在通信线程中处理
        ManageClientConnectServerThread.removeClientConnectionServerThread(u.getUserId());
        System.out.println(u.getUserId()+"退出了系统");
    }


}
