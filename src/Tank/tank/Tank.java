package Tank.tank;

import Tank.tank.Weapon;

import java.util.Vector;

public class Tank {
    private int x;
    private int y;
    //0123上右下左
    private int direct = 0;
    private int speed = 1;
    private boolean isAlive = true;
    //坦克类型 0玩家，1敌方
    private int tankType = 1;
    //子弹数量限制
    private int weaponLimit = 5;
    private Weapon weapon = null;//发射的最后一颗子弹生命周期
    //多线程使用vector
    Vector<Weapon> weapons = new Vector<>();
    //坦克碰撞检测
    public static Vector<Tank> tankVector = new Vector<>();


    public Tank(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @param x        x
     * @param y        y
     * @param tankType 0 玩家，1敌人。默认是1
     */
    public Tank(int x, int y, int tankType) {
        this(x, y);
        this.tankType = tankType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public int getTankType() {
        return tankType;
    }

    public void setTankType(int tankType) {
        this.tankType = tankType;
    }

    public int getWeaponLimit() {
        return weaponLimit;
    }

    public void setWeaponLimit(int weaponLimit) {
        this.weaponLimit = weaponLimit;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public Vector<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(Vector<Weapon> weapons) {
        this.weapons = weapons;
    }

    public Vector<Tank> getTankVector() {
        return tankVector;
    }

    public void setTankVector(Vector<Tank> tanks) {
        tankVector = tanks;
    }

    public void moveUp() {
        y -= speed;
        if (y < 0) y = 0;
    }

    public void moveRight() {
        x += speed;
        if (x > 1000) x = 1000 - 60;
    }

    public void moveDown() {
        y += speed;
        if (y > 750) y = 750 - 60;
    }

    public void moveLeft() {
        x -= speed;
        if (x < 0) x = 0;
    }

    /**
     * 发射一个子弹，放入weapons中.
     *
     * @param weaponType 0玩家发射，1敌方发射
     */
    public void useWeapon(int weaponType) {
        if (!weapons.contains(weapon)) weapon = null;
        //判断是否可以发射下一刻子弹。拒绝激光枪
        boolean flag = (weapon == null || weapon.canShoot());
        //最多 weaponLimit颗子弹.  有一个
        if (!flag || weapons.size() >= weaponLimit) return;

        switch (getDirect()) {
            case 0://向上
                weapon = new Weapon(getX() + 20 - 2, getY(), 0);
                break;
            case 1://向右
                weapon = new Weapon(getX() + 60, getY() + 20 - 2, 1);
                break;
            case 2://向下
                weapon = new Weapon(getX() + 20 - 2, getY() + 60, 2);
                break;
            case 3://向左
                weapon = new Weapon(getX(), getY() + 20 - 2, 3);
                break;
        }
        //设置子弹是谁发出的
        weapon.setWeaponType(weaponType);
        weapons.add(weapon);
        //启动射击线程
        Thread thread = new Thread(weapon);
        thread.start();
    }

    /**
     * 将死亡的子弹从vector中删除
     *
     * @param weapon 子弹
     */
    public void removeWeapon(Weapon weapon) {
        weapons.remove(weapon);
    }

    /**
     * 根据方向移动坦克
     *
     * @param direction 移动方向
     */
    public void moveByDirection(int direction) {
        //设置移动方向
        setDirect(direction);
        //可移动检测
        if (!movableDetect()) return;
        switch (direction) {
            case 0:
                moveUp();
                break;
            case 1:
                moveRight();
                break;
            case 2:
                moveDown();
                break;
            case 3:
                moveLeft();
                break;
        }
    }

    /**
     * 当前方向是否可移动
     *
     * @return true 可以移动，false不可
     */
    public boolean movableDetect() {
        //边界检测
        boolean up = getY() > 0;
        boolean right = getX() + 60 < 1000;
        boolean down = getY() + 60 < 750;
        boolean left = getX() > 0;

        //防止坦克重叠
        boolean isTankCrash = crashDetect();

        switch (getDirect()) {
            case 0:
                return up && !isTankCrash;
            case 1:
                return right && !isTankCrash;
            case 2:
                return down && !isTankCrash;
            case 3:
                return left && !isTankCrash;
            default:
                return !isTankCrash;

        }
    }

    /**
     * 判断当前坦克是否和其他发生碰撞
     *
     * @return 发生碰撞返回true，否则返回false
     */
    public boolean crashDetect() {
        switch (this.direct) {
            case 0:
                //当前坦克和其他坦克比较
                for (int i = 0; i < tankVector.size(); i++) {
                    Tank othTank = tankVector.get(i);
                    //不和自己比较
                    if (othTank != this) {
                        //敌人上下、坦克范围 [x,x+40][y,y+60]
                        if (othTank.getDirect() == 0 || othTank.getDirect() == 2) {
                            if (((this.x >= othTank.getX() && this.x <= othTank.getX() + 40) ||
                                    (this.x + 40 >= othTank.getX() && this.x + 40 <= othTank.getX() + 40))
                                    && this.y >= othTank.getY() && this.y <= othTank.getY() + 60) {
                                return true;
                            }
                        } else {//左右
                            if (((this.x >= othTank.getX() && this.x <= othTank.getX() + 60) ||
                                    (this.x + 40 >= othTank.getX() && this.x + 40 <= othTank.getX() + 60))
                                    && this.y >= othTank.getY() && this.y <= othTank.getY() + 40) {
                                return true;
                            }
                        }
                    }
                }
                break;
            case 1:
                //当前坦克和其他坦克比较
                for (int i = 0; i < tankVector.size(); i++) {
                    Tank othTank = tankVector.get(i);
                    //不和自己比较
                    if (othTank != this) {
                        //敌人上下、坦克范围 [x,x+40][y,y+60]
                        if (othTank.getDirect() == 0 || othTank.getDirect() == 2) {
                            if (this.x + 60 >= othTank.getX() && this.x + 60 <= othTank.getX() + 40 &&
                                    ((this.y >= othTank.getY() && this.y <= othTank.getY() + 60)
                                            || (this.y + 40 >= othTank.getY() && this.y + 40 <= othTank.getY() + 60))) {
                                return true;
                            }
                        } else {//左右
                            if (this.x + 60 >= othTank.getX() && this.x + 60 <= othTank.getX() + 60 &&
                                    ((this.y >= othTank.getY() && this.y <= othTank.getY() + 40)
                                            || (this.y + 40 >= othTank.getY() && this.y + 40 <= othTank.getY() + 40))) {
                                return true;
                            }
                        }
                    }
                }
                break;
            case 2:
                //当前坦克和其他坦克比较
                for (int i = 0; i < tankVector.size(); i++) {
                    Tank othTank = tankVector.get(i);
                    //不和自己比较
                    if (othTank != this) {
                        //敌人上下、坦克范围 [x,x+40][y,y+60]
                        if (othTank.getDirect() == 0 || othTank.getDirect() == 2) {
                            if (((this.x >= othTank.getX() && this.x <= othTank.getX() + 40) ||
                                    (this.x + 40 >= othTank.getX() && this.x + 40 <= othTank.getX() + 40))
                                    && this.y + 60 >= othTank.getY() && this.y + 60 <= othTank.getY() + 60) {
                                return true;
                            }
                        } else {//左右
                            if (((this.x >= othTank.getX() && this.x <= othTank.getX() + 40) ||
                                    (this.x + 40 >= othTank.getX() && this.x + 40 <= othTank.getX() + 60))
                                    && this.y + 60 >= othTank.getY() && this.y + 60 <= othTank.getY() + 40) {
                                return true;
                            }
                        }
                    }
                }
                break;
            case 3:
                //当前坦克和其他坦克比较
                for (int i = 0; i < tankVector.size(); i++) {
                    Tank othTank = tankVector.get(i);
                    //不和自己比较
                    if (othTank != this) {
                        //敌人上下、坦克范围 [x,x+40][y,y+60]
                        if (othTank.getDirect() == 0 || othTank.getDirect() == 2) {
                            if (this.x >= othTank.getX() && this.x <= othTank.getX() + 40 &&
                                    ((this.y >= othTank.getY() && this.y <= othTank.getY() + 60)
                                            || (this.y + 40 >= othTank.getY() && this.y + 40 <= othTank.getY() + 60))) {
                                return true;
                            }
                        } else {//左右
                            if (this.x >= othTank.getX() && this.x <= othTank.getX() + 60 &&
                                    ((this.y >= othTank.getY() && this.y <= othTank.getY() + 40)
                                            || (this.y + 40 >= othTank.getY() && this.y + 40 <= othTank.getY() + 40))) {
                                return true;
                            }
                        }
                    }
                }
                break;
        }
        return false;
    }
}
