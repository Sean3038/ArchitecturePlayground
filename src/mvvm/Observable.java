package mvvm;

import java.util.ArrayList;
import java.util.List;

/** ViewModel通知狀態 */
public class Observable<T> {
    private final List<Observer<T>> registerObservers = new ArrayList<>();
    private T value;

    public void addObserver(Observer<T> observer) {
        if (!registerObservers.contains(observer)) {
            registerObservers.add(observer);
            if (value != null) {
                observer.onValueChanged(value);
            }
        }
    }

    public void removeObserver(Observer<T> observer) {
        registerObservers.remove(observer);
    }

    public void setValue(T value) {
        this.value = value;
        notifyValueChange();
    }

    private void notifyValueChange() {
        for (Observer<T> observer : registerObservers) {
            observer.onValueChanged(value);
        }
    }
}
