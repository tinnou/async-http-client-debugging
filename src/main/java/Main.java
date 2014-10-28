import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by antoine.boyer on 10/27/14.
 */
public class Main {

    // LOG
    static final Logger LOG = LoggerFactory.getLogger(Main.class);
    final static AtomicInteger threadCreationCount = new AtomicInteger(0);
    final static AtomicInteger responseCount = new AtomicInteger(0);

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        LOG.info("Hello world log");

        ExecutorService executor = new CustomThreadPoolExecutor(2, 2, 60,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                int number = threadCreationCount.incrementAndGet();
                LOG.info("newThread - {} ", number);

                Thread th = new Thread(r);
                th.setName("AsyncHttpClient-" + number);
                LOG.info("thread ={}", th);
                return th;
            }
        });

        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        builder.setMaximumConnectionsTotal(-1)
                .setMaximumConnectionsPerHost(-1)
                .setConnectionTimeoutInMs(1000)
                .setIdleConnectionInPoolTimeoutInMs(60000)
                .setIdleConnectionTimeoutInMs(60000)
                .setRequestTimeoutInMs(3000)
                .setFollowRedirects(true)
                .setMaximumNumberOfRedirects(5)
                .setAllowPoolingConnection(true)
                .setIOThreadMultiplier(4)
                .build();

        builder.setExecutorService(executor);
//        builder.setExecutorService(Executors.newCachedThreadPool(new ThreadFactory() {
//            @Override
//            public Thread newThread(Runnable r) {
//                int number = threadCreationCount.incrementAndGet();
//                LOG.info("newThread - {} ", number);
//
//                Thread th = new Thread(r);
//                th.setName("AsyncHttpClient-" + number);
//                LOG.info("thread ={}", th);
//                return th;
//            }
//        }));

        AsyncHttpClientConfig config = builder.build();
        AsyncHttpClient client = new AsyncHttpClient(new NettyAsyncHttpProvider(config), config);

        for (int i = 1; i <= 500; i++) {
            LOG.info("i = {}", i);
            ListenableFuture<Response> future = client.prepareGet("http://www.google.com").execute(
                    new AsyncCompletionHandler<Response>() {
                        @Override public Response onCompleted(Response response) throws Exception {
                            LOG.info("Response = {}, count = {}", response.getStatusCode(),
                                    responseCount.incrementAndGet());
                            return response;
                        }

                        @Override
                        public void onThrowable(Throwable t) {
                            LOG.error("on throwable ={}", t);
                        }

                    });
        }

    }
}