package test;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TestMain {

	public static void main(String[] args) throws InterruptedException {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				return t;
			}
		});
		service.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				System.out.println(new Date());
			}
		}, 3, 3, TimeUnit.SECONDS);
		service.shutdown();
		
		TimeUnit.SECONDS.sleep(10000);
	}

}
