package Tank.tank;

public class Weapon implements Runnable {
    private int x;
    private int y;
    private int direction = 0;
    private int speed = 10;
    private int weaponType = 1;//武器类型 0玩家，1敌方
    private boolean isAlive = true;
    private int live = 2;//300ms的生命周期内无法发射新的子弹
    private boolean canShoot=false;

    public Weapon(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public Weapon(int x, int y, int direction, int weaponType) {
        this(x, y, direction);
        this.weaponType = weaponType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDirection() {
        return direction;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public int getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(int weaponType) {
        this.weaponType = weaponType;
    }

    public void liveDown(){
        if (live>0) {
            live--;
        }else{
            canShoot = true;
        }
    }

    public int getLive() {
        return live;
    }

    public boolean canShoot() {
        return canShoot;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //根据子弹方向修改子弹坐标
            switch (direction) {
                case 0:
                    this.y -= speed;
                    break;
                case 1:
                    this.x += speed;
                    break;
                case 2:
                    this.y += speed;
                    break;
                case 3:
                    this.x -= speed;
                    break;
            }
//            System.out.println("x=" + x + " y=" + y);
            //子弹消失行为: 1.碰到边界   2.碰到坦克
            if (!(x >= 0 && x <= 1000 && y >= 0 && y <= 750 && isAlive)) {
                isAlive = false;
                break;
            }
            liveDown();
        }
    }
}
