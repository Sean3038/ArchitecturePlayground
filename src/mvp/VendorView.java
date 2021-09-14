package mvp;

import java.util.List;

/**View Contract*/
public interface VendorView {

	void showGuide();

	void showMenu(List<String> names, List<Integer> prices, List<Integer> remainCounts);

	void showConfirmProduct(String productName, int productPrice);

	void showConfirmCount(String productName);

	void showResult(String productName, int count, int totalPrice);

	void notifyShutdown();

	void alertAllProductEmpty();

	void alertLessThanZero();

	void alertMoreThanRemainCount(int productRemain);

	void alertInvalidInput();
}
