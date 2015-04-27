import java.util.concurrent.atomic.AtomicInteger;

 public class ThreadId {
     //next ID:
     private static final AtomicInteger nextThreadId = new AtomicInteger(1);

     // Thread local variable containing each thread's ID
     private static final ThreadLocal<Integer> threadId =new ThreadLocal<Integer>() {
             @Override protected Integer initialValue() {
                 return nextThreadId.getAndIncrement();
         }
     };

     // Returns the current thread's unique ID, assigning it if necessary
     public static int get() {
         return threadId.get();
     }
 }
