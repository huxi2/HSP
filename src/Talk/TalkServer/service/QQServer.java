package Talk.TalkServer.service;

import Talk.common.Message;
import Talk.common.MessageType;
import Talk.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class QQServer {

    private ServerSocket ss = null;
    //创建用户集合，存放多个用户
    // ConcurrentHashMap 是线程安全的  而HashMap是线程不安全的
    private static ConcurrentHashMap<String, User> validUsers = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ArrayList<Message>> offLineDb = new ConcurrentHashMap<>();

    static {//初始化用户列表
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("101", new User("101", "123456"));
        validUsers.put("102", new User("102", "123456"));
        validUsers.put("103", new User("103", "123456"));
        validUsers.put("104", new User("104", "123456"));
    }

    /**
     * 验证用户
     * @param uid 用户名
     * @param pwd 密码
     * @return 检验结果
     */
    private boolean checkUser(String uid, String pwd) {
        User u = validUsers.get(uid);
        if (u == null) {
            return false;
        }
        if (!u.getPasswd().equals(pwd)) {
            return false;
        }
        return true;
    }

    /**
     * 发送该用户的离线信息
     * @param uid 登录用户
     */
    public void sendOffLineMessage(String uid) {
        ArrayList<Message> messages = offLineDb.get(uid);
        if (messages != null) {
            System.out.println("正在发送用户"+uid+"的离线消息:"+messages.size()+"...");
            ServerConnectClientThread thread = ManageServerConnectClientThread.getServerConnectClientThread(uid);
            try {
                ObjectOutputStream oos = null;
                for (Message message : messages) {
                    oos = new ObjectOutputStream(thread.getSocket().getOutputStream());
                    oos.writeObject(message);
                    System.out.println("\t"+message.getSender()+"的离线消息发送成功");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //消息发送完成后删除该用户记录
            offLineDb.remove(uid);
        }
    }

    /**
     * 存储用户离线消息
     * @param uid uid
     * @param message message
     */
    public static void addOffLineMessage(String uid,Message message){
        ArrayList<Message> messages = offLineDb.get(uid);
        if (messages!=null){
            messages.add(message);
        }else{
            messages = new ArrayList<>();
            messages.add(message);
        }
        offLineDb.put(uid,messages);
        System.out.println(uid+" 的离线消息："+offLineDb.get(uid).size());
    }

    public QQServer() {
        try {
            System.out.println("服务端在9999端口监听");
            ss = new ServerSocket(9999);
            //启动消息推送线程
            new Thread(new SendNewsToAllService()).start();

            //服务端一直监听
            while (true) {
                Socket socket = ss.accept();
                //得到socket的对象输入流
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                User u = (User) ois.readObject();
                //设置返回结果
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                Message message = new Message();
                //验证用户密码
                if (checkUser(u.getUserId(), u.getPasswd())) {
                    //返回登录结果
                    message.setMessageType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    oos.writeObject(message);
                    //创建线程保持联系
                    ServerConnectClientThread scct = new ServerConnectClientThread(socket, u.getUserId());
                    scct.start();
                    //将该线程对象当入服务端线程管理类
                    ManageServerConnectClientThread.addServerConnectClientThread(u.getUserId(), scct);
                    //将缓存的离线消息发送出去
                    sendOffLineMessage(u.getUserId());
                } else {
                    System.out.println("用户：" + u.getUserId() + " 密码：" + u.getPasswd() + " 登陆失败");
                    message.setMessageType(MessageType.MESSAGE_LOGIN_FAIL);
                    oos.writeObject(message);
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //服务端退出wile循环，关闭serversocket
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
