package com.github.edisonlil.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * description
 *
 * @author edison
 * @since 2021/12/27 17:59
 */
public class TaskExecutor {

    ThreadPoolExecutor executor;

    List<Task> tasks = new ArrayList<>();

    public static TaskExecutor create(int corePoolSize){
        TaskExecutor executor = new TaskExecutor(corePoolSize,10);
        return executor;
    }

    public static TaskExecutor create(int corePoolSize,int queueCount){
        TaskExecutor executor = new TaskExecutor(corePoolSize,queueCount);
        return executor;
    }

    public static TaskExecutor create(){
        TaskExecutor executor = new TaskExecutor(1,10);
        return executor;
    }


    private TaskExecutor(int corePoolSize, int queueCount){
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(queueCount);
        executor = new ThreadPoolExecutor(corePoolSize,corePoolSize*2,60,TimeUnit.SECONDS,queue);
    }


    public TaskExecutor submit(Callable callable) throws ExecutionException, InterruptedException {
        Future future = executor.submit(callable);
        Task task = new Task(future);
        tasks.add(task);
        return this;
    }

    public List call() throws ExecutionException, InterruptedException {

        List list = new ArrayList();
        for (Task task : tasks) {
            list.add(task.value());
        }
        executor.purge();
        executor.shutdown();
        return list;
    }


    public void release(){
        if(!executor.isShutdown()){
            executor.purge();
            executor.shutdown();
        }
    }

    public TaskExecutor run(Runnable runnable) {

        executor.submit(runnable);
        return this;
    }

    /**
     * 任务
     */
    private static class Task {


        Future future;

        public Task(Future future) throws ExecutionException, InterruptedException {
            this.future = future;
        }

        public Object value() throws ExecutionException, InterruptedException {
            return future.get();
        }
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long start = System.currentTimeMillis();

        TaskExecutor.create().run(()-> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).call();


        System.out.println("消耗的时间：" + (System.currentTimeMillis() - start));

    }

}
