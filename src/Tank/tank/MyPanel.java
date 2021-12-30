package Tank.tank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Vector;

/**
 * 坦克大战绘图区域
 */
public class MyPanel extends JPanel implements KeyListener, Runnable {
    //定义玩家的坦克
    Hero hero = null;
    //定义敌人坦克放入Vector中
    Vector<Tank> enemyTanks = new Vector<>();
    //定义Node，存放敌人的坐标和方向
    Vector<Node> enemyTankNodes = new Vector<>();
    //定义炸毁后效果向量
    Vector<Bomb> bombs = new Vector<>();
    //定义敌人坦克数量
    int enemyTankSize = 6;
    //我的成绩
    private int score = 0;

    //爆炸效果展示图
    Image img1 = null;
    Image img2 = null;
    Image img3 = null;


    public MyPanel(String key) {
        File file = new File(Record.getRecordFile());
        if (file.exists()) {
            enemyTankNodes = Record.readRecord();
        } else {
            System.out.println("记录不存在，开启新的游戏");
            key = "N";
        }
        //将enemyTanks 传递给Record
        Record.setTanks(enemyTanks);
        //初始化自己的坦克
        hero = new Hero(200, 600);

        //是否进行上次游戏
        switch (key) {
            case "Y":
                for (int i = 0; i < enemyTankNodes.size(); i++) {
                    Node node = enemyTankNodes.get(i);
                    Enemy enemy = new Enemy(node.getX(), node.getY());
                    enemy.setDirect(node.getDirect());
                    //启动坦克自由移动的线程
                    new Thread(enemy).start();
                    //创建子弹
                    enemy.useWeapon(1);
                    //将enemyTanks传递到enemy中
                    enemy.setTankVector(enemyTanks);
                    enemyTanks.add(enemy);
                }
                break;
            case "N":
                //初始化敌人坦克 并创建子弹
                for (int i = 0; i < enemyTankSize; i++) {
                    Enemy enemy = new Enemy(100 * (i + 1), 0);
                    //启动坦克自由移动的线程
                    new Thread(enemy).start();
                    //创建子弹
                    enemy.useWeapon(1);
                    //将enemyTanks传递到enemy中
                    enemy.setTankVector(enemyTanks);
                    enemyTanks.add(enemy);
                }
                break;
            default:
                System.out.println("输入信息有误");
        }

        //加载爆炸效果图
        img1 = Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("../asset/bomb_1.gif"));
        img2 = Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("../asset/bomb_2.gif"));
        img3 = Toolkit.getDefaultToolkit().getImage(MyPanel.class.getResource("../asset/bomb_3.gif"));

        //播放音乐
        new AePlayWave("Tank/asset/111.wav").start();
    }

    public void showInfo(Graphics g) {
        //绘制玩家总成绩
        g.setColor(Color.black);
        Font font = new Font("宋体", Font.BOLD, 25);
        g.setFont(font);

        g.drawString("您累计击毁敌方坦克", 1020, 30);
        g.drawString(Record.getAllEnemyTankNum() + "", 1080, 100);

        drawTank(1020, 60, g, 0, 1);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //绘制坦克活动区域
        g.fillRect(0, 0, 1000, 750);
        //绘制提示信息
        showInfo(g);

        //如果玩家死亡，就让它重生.并且保留之前子弹
        if (hero == null || !hero.isAlive()) {
            Vector<Weapon> weapons = hero.weapons;
            hero = new Hero(200, 600);
            hero.weapons = weapons;
        }
        //绘制玩家坦克
        if (hero != null && hero.isAlive()) {
            drawTank(hero.getX(), hero.getY(), g, hero.getDirect(), 0);
        }
        //画出玩家发出的子弹
        showWeapons(hero, g);

        //绘制敌人坦克 和 敌人子弹
        for (int i = 0; i < enemyTanks.size(); i++) {
            Enemy enemy = (Enemy) enemyTanks.get(i);
            //绘制存活的坦克
            if (enemy != null && enemy.isAlive()) {
                drawTank(enemy.getX(), enemy.getY(), g, enemy.getDirect(), 1);
            } else {
                enemyTanks.remove(enemy);
            }
            //绘制所有敌人子弹, 即使敌方死了，子弹也可以继续存活
            showWeapons(enemy, g);
        }

        //绘制炸弹 
        for (int i = 0; i < bombs.size(); i++) {
            //取出炸弹，根据life绘制炸弹图
            Bomb bomb = bombs.get(i);
            if (bomb.life > 6) {
                g.drawImage(img1, bomb.x, bomb.y, 60, 60, this);
            } else if (bomb.life > 3) {
                g.drawImage(img2, bomb.x, bomb.y, 60, 60, this);
            } else {
                g.drawImage(img3, bomb.x, bomb.y, 60, 60, this);
            }
            bomb.lifeDown();
            if (!bomb.isAlive) {
                bombs.remove(bomb);
            }
        }
    }

    /**
     * 是否有坦克被击中
     */
    public void isTankBeHit() {
        //重绘前判断是否有坦克被击中
        //判断我方子弹是否击中敌人
        for (int i = 0; i < hero.weapons.size(); i++) {
            Weapon weapon = hero.weapons.get(i);
            if (weapon != null && weapon.isAlive()) {
                for (int j = 0; j < enemyTanks.size(); j++) {
                    hitTank(weapon, enemyTanks.get(j));
                }
            }
        }
        //判断自己是否被击中 遍历敌人坦克
        for (int i = 0; i < enemyTanks.size(); i++) {
            //遍历敌人子弹
            Vector<Weapon> weapons = enemyTanks.get(i).weapons;
            for (int j = 0; j < weapons.size(); j++) {
                Weapon weapon = weapons.get(j);
                if (weapon != null && weapon.isAlive()) {
                    hitTank(weapon, hero);
                }
            }
        }
    }

    /**
     * 判断 是否有坦克被击中
     *
     * @param weapon
     * @param tank
     */
    public void hitTank(Weapon weapon, Tank tank) {
        int wx = weapon.getX();
        int wy = weapon.getY();
        int tx = tank.getX();
        int ty = tank.getY();
        switch (tank.getDirect()) {
            //0 2 是上下方向，1,3是左右方向 坦克形状一致;
            case 0:
            case 2:
                if (wx > tx && wx < tx + 40 && wy > ty && wy < ty + 60) {//
                    weapon.setAlive(false);
                    if (weapon.getWeaponType() != tank.getTankType()) {
                        tank.setAlive(false);
                        //记录成绩
                        if (tank instanceof Hero) {
                            Record.addHeroNum();
                        } else {
                            Record.addAllEnemyTankNum();
                        }
                        //创建bomb对象加入到bombs
                        Bomb bomb = new Bomb(tank.getX(), tank.getY());
                        bombs.add(bomb);
                    }
                }
                break;
            case 1:
            case 3:
                if (wx > tx && wx < tx + 60 && wy > ty && wy < ty + 40) {//
                    weapon.setAlive(false);
                    if (weapon.getWeaponType() != tank.getTankType()) {
                        tank.setAlive(false);
                        //记录成绩
                        if (tank instanceof Hero) {
                            Record.addHeroNum();
                        } else {
                            Record.addAllEnemyTankNum();
                        }
                        //创建bomb对象加入到bombs
                        Bomb bomb = new Bomb(tank.getX(), tank.getY());
                        bombs.add(bomb);
                    }
                }
        }
    }

    /**
     * 使用多态完成子弹的绘制判断
     *
     * @param tank
     * @param g
     */
    public void showWeapons(Tank tank, Graphics g) {
        //绘制所有敌人子弹
        for (int j = 0; j < tank.weapons.size(); j++) {
            //取出该敌方坦克的子弹，判断是否需要绘制到面板上
            Weapon weapon = tank.weapons.get(j);
            if (weapon != null && weapon.isAlive()) {
                drawWeapon(weapon.getX(), weapon.getY(), g);
            } else {//若子弹已死亡，则删除该子弹
                tank.removeWeapon(weapon);
            }
        }
    }

    /**
     * 将子弹绘制在面板上
     *
     * @param x
     * @param y
     * @param g
     */
    public void drawWeapon(int x, int y, Graphics g) {
        g.setColor(Color.gray);
        g.drawOval(x, y, 5, 5);
//        System.out.println("子弹重绘");
    }

    /**
     * 将坦克绘制在面板上
     *
     * @param x      x
     * @param y      y
     * @param g      画笔
     * @param direct 方向
     * @param type   类型
     */
    public void drawTank(int x, int y, Graphics g, int direct, int type) {
        //类型
        switch (type) {
            case 0://我的
                g.setColor(Color.cyan);
                break;
            case 1://敌人
                g.setColor(Color.yellow);
                break;
        }

        //绘制坦克
        switch (direct) {
            case 0://向上
                g.fill3DRect(x, y, 10, 60, false);
                g.fill3DRect(x + 30, y, 10, 60, false);
                g.fill3DRect(x + 10, y + 10, 20, 40, false);
                g.fillOval(x + 10, y + 20, 20, 20);
                g.drawLine(x + 20, y + 30, x + 20, y);
                break;
            case 1://向右
                g.fill3DRect(x, y, 60, 10, false);
                g.fill3DRect(x, y + 30, 60, 10, false);
                g.fill3DRect(x + 10, y + 10, 40, 20, false);
                g.fillOval(x + 20, y + 10, 20, 20);
                g.drawLine(x + 30, y + 20, x + 60, y + 20);
                break;
            case 2://向下
                g.fill3DRect(x, y, 10, 60, false);
                g.fill3DRect(x + 30, y, 10, 60, false);
                g.fill3DRect(x + 10, y + 10, 20, 40, false);
                g.fillOval(x + 10, y + 20, 20, 20);
                g.drawLine(x + 20, y + 30, x + 20, y + 60);
                break;
            case 3://向左
                g.fill3DRect(x, y, 60, 10, false);
                g.fill3DRect(x, y + 30, 60, 10, false);
                g.fill3DRect(x + 10, y + 10, 40, 20, false);
                g.fillOval(x + 20, y + 10, 20, 20);
                g.drawLine(x + 30, y + 20, x, y + 20);
                break;
            default:
                System.out.println("暂时没处理");
                break;
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            hero.moveByDirection(0);
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            hero.moveByDirection(1);
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            hero.moveByDirection(2);
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            hero.moveByDirection(3);
        } else if (e.getKeyCode() == KeyEvent.VK_J) {
            hero.useWeapon(0);
        }

        this.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


    /**
     * 每隔100ms刷新一下面板
     */
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //判断是否有子弹击中坦克
            isTankBeHit();

            this.repaint();
        }
    }
}
