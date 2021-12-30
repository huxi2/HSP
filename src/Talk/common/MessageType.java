package Talk.common;

/**
 * 消息类型接口
 */
public interface MessageType {
    String MESSAGE_LOGIN_SUCCEED = "1";     //登陆成功
    String MESSAGE_LOGIN_FAIL = "2";        //登录失败
    String MESSAGE_COMM_MES = "3";          //普通信息包
    String MESSAGE_GET_ONLINE_FRIED = "4";  //返回在线用户列表
    String MESSAGE_RET_ONLINE_FRIED = "5";  //返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6";       //客户端请求退出
    String MESSAGE_CLIENT_EXIT_CHECK = "7"; //服务器通知客户端退出请求
    String MESSAGE_TO_ALL_MES = "8";        //群发消息
    String MESSAGE_FILE_MES = "9";          //发送文件
}
