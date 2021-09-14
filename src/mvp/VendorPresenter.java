package mvp;

import java.util.ArrayList;
import java.util.List;

import common.Call;
import common.Product;
import common.Utils;
import common.VendorModel;

public class VendorPresenter {

	/**階段*/
	private enum State {
		Shutdown, 			//關閉程式
		ShowGuide, 			//顯示歡迎畫面
		ShowMenu, 			//展示目錄
		ConfirmProduct, 	//確認品項
		ConfirmCount, 		//確認數量
		Result 				//顯示結帳資訊
	}

	/**程式狀態*/
	private State state = State.ShowGuide;
	private Product selectedProduct = null;
	private int selectedCount = 0;

	/**View*/
	private VendorView vendorView;

	/**Model*/
	private final VendorModel vendorModel;

	VendorPresenter(VendorModel vendorModel) {
		this.vendorModel = vendorModel;
	}

	/**綁定View*/
	public void bindView(VendorView vendorView) {
		this.vendorView = vendorView;
	}

	/**解除綁定View*/
	public void unBindView(){
		this.vendorView = null;
	}

	/**看目前階段畫面*/
	public void showCurrentState() {
		if (state == State.ShowGuide) {
			vendorView.showGuide();

		} else if (state == State.ShowMenu) {
			Call<List<Product>> servingProducts = vendorModel.getServingProducts();
			List<Product> products = servingProducts.get();
			List<String> productNames = new ArrayList<>();
			List<Integer> productPrices = new ArrayList<>();
			List<Integer> productRemainCounts = new ArrayList<>();
			for (Product product : products) {
				productNames.add(product.getName());
				productPrices.add(product.getPrice());
				productRemainCounts.add(product.getRemainCount());
			}
			vendorView.showMenu(productNames, productPrices, productRemainCounts);

		} else if (state == State.ConfirmProduct) {
			vendorView.showConfirmProduct(selectedProduct.getName(), selectedProduct.getPrice());

		} else if (state == State.ConfirmCount) {
			vendorView.showConfirmCount(selectedProduct.getName());

		} else if (state == State.Result) {
			int totalPrice = selectedProduct.getPrice() * selectedCount;
			vendorView.showResult(selectedProduct.getName(), selectedCount, totalPrice);
		}
	}

	/**使用者輸入介面*/
	public void onUserInput(String input) {
		input = input.trim();
		if ("q".equals(input)) {
			state = State.Shutdown;
			onShutdown();

		} else if (state == State.ShowGuide) {
			if ("m".equals(input)) {
				state = State.ShowMenu;
				onShowMenu();
			} else {
				onInvalidInput();
			}

		} else if (state == State.ShowMenu) {
			Call<Integer> productCount = vendorModel.getProductCount();
			boolean isVendorServe = Utils.isNumeric(input) && Integer.parseInt(input) < productCount.get()
					&& Integer.parseInt(input) >= 0;

			if (isVendorServe) {
				state = State.ConfirmProduct;
				Call<Product> product = vendorModel.getProduct(Integer.parseInt(input));
				selectedProduct = product.get();
			} else {
				onInvalidInput();
			}

		} else if (state == State.ConfirmProduct) {
			if ("y".equals(input)) {
				state = State.ConfirmCount;
			} else if ("n".equals(input)) {
				state = State.ShowMenu;
			} else {
				onInvalidInput();
			}

		} else if (state == State.ConfirmCount) {
			if (Utils.isNumeric(input)) {
				onConfirmCount(Integer.parseInt(input));
			} else if ("p".equals(input)) {
				state = State.ShowMenu;
			} else {
				onInvalidInput();
			}

		} else if (state == State.Result) {
			reset();
		}
	}

	private void reset() {
		selectedProduct = null;
		selectedCount = 0;
		state = State.ShowGuide;
	}

	private void onShowMenu() {
		Call<Boolean> isNoProductRemain = vendorModel.isNoProductRemain();
		if (isNoProductRemain.get()) {
			vendorView.alertAllProductEmpty();
			state = State.ShowGuide;
		}
	}

	private void onConfirmCount(int count) {
		int productRemain = selectedProduct.getRemainCount();
		if (count < 0) {
			vendorView.alertLessThanZero();
		} else if (count > productRemain) {
			vendorView.alertMoreThanRemainCount(productRemain);
		} else {
			selectedCount = count;
			selectedProduct.setRemainCount(productRemain - count);
			state = State.Result;
		}
	}

	private void onInvalidInput() {
		vendorView.alertInvalidInput();
	}

	private void onShutdown() {
		vendorView.notifyShutdown();
	}

}
