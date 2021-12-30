package Tank.tank;

public class Enemy extends Tank implements Runnable {

    public Enemy(int x, int y) {
        super(x, y);
        this.setDirect(2);
    }

    @Override
    public void run() {
        while (true) {
            //产生一个随机数，决定坦克往某个方向走多久
            int step = (int) (Math.random() * 100);
            //沿着之前的方向一直走
            for (int i = 0; i < step; i++) {
                //移动到边界区域时，停止移动，去尝试换方向
                if(!movableDetect()){
                    break;
                }
                //根据方向移动坦克
                moveByDirection(getDirect());
                //随机是否发射子弹 30分之一
                int fight = (int) (Math.random() * 30);
                if (fight == 1) {
                    this.useWeapon(getDirect());
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //随机改变方向 [0-4)
            int direction = (int) (Math.random() * 4);
            this.setDirect(direction);


            //多线程一定要考虑什么时候退出多线程
            if (!isAlive()) {
                break;
            }
        }

    }
}
