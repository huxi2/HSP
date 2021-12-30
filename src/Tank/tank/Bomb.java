package Tank.tank;

public class Bomb {
    int x, y;//炸毁后的坐标
    int life = 9;//生命周期
    boolean isAlive = true;

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //减少生命值
    public void lifeDown() {
        if (life > 0) {
            life--;
        } else {
            isAlive = false;
        }
    }
}
