async-http-client-debugging
===========================

If you run the current Main.main() it will block after about 50 requests (on my machine, might vary with yours). 
Now if you change from `NettyAsyncHttpProvider` to `ApacheAsyncHttpProvider` it will work just fine.
