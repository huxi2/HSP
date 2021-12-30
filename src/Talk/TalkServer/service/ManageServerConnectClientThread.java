package Talk.TalkServer.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * 服务器 链接 客户端 通信线程管理类
 */
public class ManageServerConnectClientThread {
    //key userid, value 通讯线程
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    /**
     * 将某个线程加入到集合中
     *
     * @param userid userid
     * @param thread thread
     */
    public static void addServerConnectClientThread(String userid, ServerConnectClientThread thread) {
        hm.put(userid, thread);
    }

    /**
     * 通过userid获得相应线程
     */
    public static ServerConnectClientThread getServerConnectClientThread(String userid) {
        return hm.get(userid);
    }

    /**
     * 通过userid删除相应的线程
     *
     * @param userid userid
     */
    public static void removeServerConnectClientThread(String userid) {
        ServerConnectClientThread thread = hm.get(userid);
        if (thread != null) {
            hm.remove(userid);
        }
    }

    /**
     * 返回在在线用户列表
     *
     * @return String
     */
    public static String getOnlineUsers() {
        Set<String> uids = hm.keySet();
        StringBuffer onlineUserList = new StringBuffer();
        for (String uid : uids) {
            onlineUserList.append(uid + " ");
        }
        return onlineUserList.toString();
    }
}
