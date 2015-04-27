import java.util.ArrayList;
import java.util.List;


public class ThreadLocalList {
	protected static final ThreadLocal<List<Triple>> threadList = new ThreadLocal<List<Triple>>() {
		protected List<Triple> initialValue() {
			return new ArrayList<Triple>();
		}
	};

	public static void put(Triple p) {
		threadList.get().add(p);
	}

	public static List<Triple> get() {
		return threadList.get();
	}

}
