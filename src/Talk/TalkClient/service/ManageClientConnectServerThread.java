package Talk.TalkClient.service;

import java.io.IOException;
import java.util.HashMap;

/**
 * 客户端链接服务器端的线程管理类
 */
public class ManageClientConnectServerThread {
    //key userid, value 通讯线程
    private static HashMap<String, ClientConnectServerThread> hm = new HashMap<>();

    /**
     * 将某个线程加入到集合中
     * @param userid userid
     * @param thread thread
     */
    public static void addClientConnectionServerThread(String userid, ClientConnectServerThread thread){
        hm.put(userid,thread);
    }

    /**
     * 通过userid获得相应线程
     * @param userid userid
     * @return thread
     */
    public static ClientConnectServerThread getClientConnectServerThread(String userid) {
        return hm.get(userid);
    }

    /**
     * 通过userId将相应的用户线程从管理中删除
     * @param userid userId
     */
    public static void removeClientConnectionServerThread(String userid){
        ClientConnectServerThread thread = hm.get(userid);
        if (thread != null){
            hm.remove(userid);
        }
    }
}
