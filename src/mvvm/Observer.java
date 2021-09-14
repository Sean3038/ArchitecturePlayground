package mvvm;

/** ViewModel狀態觀察者 */
public interface Observer<T> {
	void onValueChanged(T value);
}
