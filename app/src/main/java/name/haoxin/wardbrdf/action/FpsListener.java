package name.haoxin.wardbrdf.action;

/**
 * Created by hx on 16/3/25.
 */
public class FpsListener {
    private final long FPS_INTERVAL_MILL = 1000L; // 1s
    private long nowFPS = 0L;
    private long time;
    private long timeNow;
    private long frameCount = 0L;

    public void reset() {
        time = System.currentTimeMillis();
        frameCount = 0L;
    }

    public void tick() {
        frameCount++;
        timeNow = System.currentTimeMillis();
        long elapsedTime = timeNow - time;
        if (elapsedTime >= FPS_INTERVAL_MILL) {
            nowFPS = frameCount * FPS_INTERVAL_MILL / elapsedTime;
            time = timeNow;
            frameCount = 0;
        }
    }

    public void makeFPS() {
        frameCount++;
//        interval += PERIOD;
        //当实际间隔符合时间时。
//        if (interval >= FPS_MAX_INTERVAL) {
//            //nanoTime()返回最准确的可用系统计时器的当前值，以毫微秒为单位
//            long timeNow = System.nanoTime();
//            // 获得到目前为止的时间距离
//            long realTime = timeNow - time; // 单位: ns
//            //换算为实际的fps数值
//            nowFPS = ((double) frameCount / realTime) * FPS_MAX_INTERVAL;
//            //变更数值
//            frameCount = 0L;
//            interval = 0L;
//            time = timeNow;
//        }

    }

    public long getNowFPS() {
        return nowFPS;
    }

    public String getFPS() {
//        return df.format(nowFPS);
        return String.valueOf(nowFPS);
    }


}
