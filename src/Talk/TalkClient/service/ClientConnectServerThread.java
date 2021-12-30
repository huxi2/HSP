package Talk.TalkClient.service;

import Talk.common.Message;
import Talk.common.MessageType;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * 每个用户的一个 维持服务端通讯 线程. 用来处理UserClientService的请求
 */
public class ClientConnectServerThread extends Thread{
    //该线程通过持有一个socket和服务器保持联系
    private Socket socket;

    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        //客户端读取服务器返回的消息，根据消息类型判断操作
        while(true){
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //若服务器没有发送消息，线程阻塞再这里
                Message msg = (Message)ois.readObject();

                if (msg.getMessageType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){
                    System.out.println("\n用户登录成功");
                }else if (msg.getMessageType().equals(MessageType.MESSAGE_LOGIN_FAIL)){
                    System.out.println("\n用户登录失败，请检查用户名和密码是否正确");
                }else if (msg.getMessageType().equals(MessageType.MESSAGE_CLIENT_EXIT_CHECK)){
                    //服务器通知客户端退出
                    System.out.println("\n服务器通知 用户"+msg.getReceiver()+" 下线" );
                    //此处直接退出就行，因为socket的关闭操作在server已经处理了
                    socket.close();
                    break;
                }else if (msg.getMessageType().equals(MessageType.MESSAGE_RET_ONLINE_FRIED)){
                    //服务器返回在线用户列表
                    System.out.println("\n在线用户列表查询成功");
                    String[] onlineUsers =  msg.getContent().split(" ");
                    for (String uid : onlineUsers) {
                        System.out.println("用户："+uid);
                    }
                }else if (msg.getMessageType().equals(MessageType.MESSAGE_COMM_MES)){
                    //收到私聊消息
                    System.out.println("\n收到 "+msg.getSender()+" 的私聊消息："+msg.getContent());
                }else if (msg.getMessageType().equals(MessageType.MESSAGE_FILE_MES)){
                    //收到文件发送消息
                    System.out.println("\n收到 "+msg.getSender()+" 的文件，保存在"+msg.getDest());

                    FileOutputStream fos = new FileOutputStream(msg.getDest());
                    fos.write(msg.getFileBytes());
                    fos.close();
                    System.out.println("\n文件保存成功");

                }else if (msg.getMessageType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                    //收到群聊消息
                    System.out.println("\n收到 "+msg.getSender()+" 的群聊消息："+msg.getContent());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
