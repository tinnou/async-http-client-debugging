import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by antoine.boyer on 10/27/14.
 */
public class CustomThreadPoolExecutor extends ThreadPoolExecutor implements ExecutorService {

    static final Logger LOG = LoggerFactory.getLogger(Main.class);


    public CustomThreadPoolExecutor(int i, int i1, int i2, TimeUnit seconds,
            LinkedBlockingQueue<Runnable> runnables, ThreadFactory threadFactory) {
        super(i, i1, i2, seconds, runnables, threadFactory);

        setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r,
                    ThreadPoolExecutor executor) {
                LOG.error("ANTOINE : Task rejected");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                executor.execute(r);
            }
        });
    }

    protected void beforeExecute(Thread t, Runnable r) {
        //LOG.info("{} beforeExecute", t.getName());
        super.beforeExecute(t, r);
    }

    protected void afterExecute(Runnable r, Throwable t) {
        if (t != null) {
            LOG.error("t={}", t);
        }
        //LOG.info("{} afterExecute", r);
        super.afterExecute(r, t);
    }
}
