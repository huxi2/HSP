package Tank.tank;

import java.io.*;
import java.util.Vector;

public class Record {
    //定义变量，记录我方击毁敌人坦克数
    private static int allEnemyTankNum = 0;
    private static int heroNum = 0;
    private static Vector<Tank> tanks = null;
    private static Vector<Node> nodes = new Vector<>();
    //定义IO对象，准备写数据到文件中
    private static FileWriter fw = null;
    private static FileReader fr = null;
    private static BufferedWriter bw = null;
    private static BufferedReader br = null;
    private static String recordFile = "Tank\\asset\\myRecord.txt";

    public static int getAllEnemyTankNum() {
        return allEnemyTankNum;
    }

    public static void setAllEnemyTankNum(int allEnemyTankNum) {
        Record.allEnemyTankNum = allEnemyTankNum;
    }

    public static int getHeroNum() {
        return heroNum;
    }

    public static void setHeroNum(int heroNum) {
        Record.heroNum = heroNum;
    }

    public static Vector<Tank> getTanks() {
        return tanks;
    }

    public static void setTanks(Vector<Tank> tanks) {
        Record.tanks = tanks;
    }

    public static void addAllEnemyTankNum() {
        Record.allEnemyTankNum++;
    }

    public static Vector<Node> getNodes() {
        return nodes;
    }

    public static void setNodes(Vector<Node> nodes) {
        Record.nodes = nodes;
    }

    public static FileWriter getFw() {
        return fw;
    }

    public static void setFw(FileWriter fw) {
        Record.fw = fw;
    }

    public static FileReader getFr() {
        return fr;
    }

    public static void setFr(FileReader fr) {
        Record.fr = fr;
    }

    public static BufferedWriter getBw() {
        return bw;
    }

    public static void setBw(BufferedWriter bw) {
        Record.bw = bw;
    }

    public static BufferedReader getBr() {
        return br;
    }

    public static void setBr(BufferedReader br) {
        Record.br = br;
    }

    public static String getRecordFile() {
        return recordFile;
    }

    public static void setRecordFile(String recordFile) {
        Record.recordFile = recordFile;
    }

    public static void addHeroNum() {
        Record.heroNum++;
    }

    /**
     * 保存当前游戏状态
     */
    public static void keepRecord() {
        try {
            bw = new BufferedWriter(new FileWriter(recordFile));
            bw.write(allEnemyTankNum + "\r\n");

            for (int i = 0; i < tanks.size(); i++) {
                Tank tank = tanks.get(i);
                if (tank.isAlive()) {
                    String record = tank.getX() + " " + tank.getY() + " " + tank.getDirect();
                    bw.write(record + "\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Vector<Node> readRecord() {
        try {
            br = new BufferedReader(new FileReader(recordFile));
            allEnemyTankNum = Integer.parseInt(br.readLine());

            String line = "";//x y direct
            while ((line = br.readLine()) != null) {
                String[] s = line.split(" ");
                Node node = new Node(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
                nodes.add(node);//放入vector
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return nodes;
    }
}

