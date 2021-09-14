package common;

/**Model的異步通知介面*/
public interface Call<T> {
	T get();

	void cancel();
}
