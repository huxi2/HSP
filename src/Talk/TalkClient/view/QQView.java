package Talk.TalkClient.view;

import Talk.TalkClient.service.FileClientService;
import Talk.TalkClient.service.MessageClientService;
import Talk.TalkClient.service.UserClientService;
import Talk.utils.Utility;

public class QQView {

    private boolean loop = true;//控制菜单显示
    private String key = "";//获得用户输入
    public UserClientService userClientService = new UserClientService();//用户登录和注册

    public static void main(String[] args) {
        new QQView().mainMenu();
        System.out.println("客户端推出系统。。。。。");
    }

    //主菜单
    public void mainMenu() {
        while (loop) {
            System.out.println("===============欢迎登录网络通讯系统================");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 9 退出系统");

            System.out.print("请输入你的选择： ");
            key = Utility.readString(1);
            switch (key) {
                case "1":
                    System.out.print("请输入用户号：");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密  码：");
                    String passwd = Utility.readString(50);
                    // 进行用户验证  构建一个用户发送到服务端进行验证
                    boolean checkUser = userClientService.checkUser(userId, passwd);
                    if (checkUser) {
                        System.out.println("===============欢迎（用户 " + userId + " ）登录成功================");
                        //进入二级菜单
                        while (loop) {
                            System.out.println("===============网络通讯系统二级菜单（用户 " + userId + " ）================");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 9 退出系统");
                            System.out.print("请输入你的选择:");
                            key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    System.out.println("显示用户列表");
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.print("请输入群聊内容：");
                                    String content_all = Utility.readString(100);
                                    MessageClientService.sendMessageToAll(userId,content_all);
                                    break;
                                case "3":
                                    System.out.print("请输入私聊对象：");
                                    String receiverId = Utility.readString(50);
                                    System.out.print("请输入私聊内容：");
                                    String content = Utility.readString(100);
                                    MessageClientService.sendMessageToOne(userId,receiverId,content);
                                    break;
                                case "4":
                                    System.out.print("请输入文件接受对象：");
                                    String receiverId2 = Utility.readString(50);
                                    System.out.print("请输入要发送的文件：");
                                    String sourcePath = Utility.readString(100);
                                    System.out.print("请输入要保存的位置：");
                                    String receivePath = Utility.readString(100);
                                    FileClientService.sendFileToOne(userId,receiverId2,sourcePath,receivePath);
                                    break;
                                case "9":
                                    loop = false;
                                    System.out.println("系统正在退出...");
                                    userClientService.clientExit();
                                    System.exit(0);
                                    break;
                            }
                        }
                    } else {
                        System.out.println("==========================登录失败==========================");
                    }
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }
    }


}
