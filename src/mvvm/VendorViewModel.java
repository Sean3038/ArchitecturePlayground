package mvvm;

import java.util.List;

import common.Call;
import common.Product;
import common.Utils;
import common.VendorModel;
import mvvm.event.AllProductEmptyErrorEvent;
import mvvm.event.ConfirmCountEvent;
import mvvm.event.ConfirmProductEvent;
import mvvm.event.ErrorEvent;
import mvvm.event.InvalidInputErrorEvent;
import mvvm.event.LessThanZeroErrorEvent;
import mvvm.event.MoreThanRemainCountErrorEvent;
import mvvm.event.ResultEvent;
import mvvm.event.ShowGuideEvent;
import mvvm.event.ShowMenuEvent;
import mvvm.event.ShutdownEvent;
import mvvm.event.StateEvent;
import mvvm.event.ShowMenuEvent.MenuItem;

public class VendorViewModel {

	/** 階段 */
	private enum State {
		Shutdown, 			//關閉程式
		ShowGuide, 			//顯示歡迎畫面
		ShowMenu, 			//展示目錄
		ConfirmProduct,   	//確認品項
		ConfirmCount, 		//確認數量
		Result 				//顯示結帳資訊
	}

	/** 程式狀態 */
	private State state = State.ShowGuide;
	private Product selectedProduct = null;
	private int selectedCount = 0;

	/** Model */
	private final VendorModel vendorModel;

	/** 對外發送更新事件 */
	public final Observable<StateEvent> stateEvent = new Observable<>();
	public final Observable<ErrorEvent> errorEvent = new Observable<>();

	VendorViewModel(VendorModel vendorModel) {
		this.vendorModel = vendorModel;
		syncCurrentState();
	}

	/** 根據內部狀態，設定ViewModel對外狀態 */
	private void syncCurrentState() {
		if (state == State.ShowGuide) {
			stateEvent.setValue(new ShowGuideEvent());

		} else if (state == State.ShowMenu) {
			Call<List<Product>> servingProducts = vendorModel.getServingProducts();
			List<Product> products = servingProducts.get();
			ShowMenuEvent event = new ShowMenuEvent();
			for (Product product : products) {
				MenuItem item = new MenuItem();
				item.setName(product.getName());
				item.setPrice(product.getPrice());
				item.setRemainCount(product.getRemainCount());
				event.addMenuItem(item);
			}
			stateEvent.setValue(event);

		} else if (state == State.ConfirmProduct) {
			ConfirmProductEvent event = new ConfirmProductEvent();
			event.setProductName(selectedProduct.getName());
			event.setProductPrice(selectedProduct.getPrice());
			stateEvent.setValue(event);

		} else if (state == State.ConfirmCount) {
			ConfirmCountEvent event = new ConfirmCountEvent();
			event.setProductName(selectedProduct.getName());
			stateEvent.setValue(event);

		} else if (state == State.Result) {
			int totalPrice = selectedProduct.getPrice() * selectedCount;
			ResultEvent event = new ResultEvent();
			event.setProductName(selectedProduct.getName());
			event.setCount(selectedCount);
			event.setTotalPrice(totalPrice);
			stateEvent.setValue(event);

		} else if (state == State.Shutdown) {
			stateEvent.setValue(new ShutdownEvent());
		}
	}

	/** 使用者操作 */
	public void onUserInput(String input) {
		input = input.trim();
		if ("q".equals(input)) {
			state = State.Shutdown;

		} else if (state == State.ShowGuide) {
			if ("m".equals(input)) {
				onShowMenu();
			} else {
				onInvalidInput();
			}

		} else if (state == State.ShowMenu) {
			Call<Integer> productCount = vendorModel.getProductCount();
			boolean isVendorServe = Utils.isNumeric(input)
					&& Integer.parseInt(input) < productCount.get()
					&& Integer.parseInt(input) >= 0;

			if (isVendorServe) {
				Call<Product> product = vendorModel.getProduct(Integer.parseInt(input));
				selectedProduct = product.get();
				state = State.ConfirmProduct;
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

		syncCurrentState();
	}

	private void onShowMenu() {
		Call<Boolean> isNoProductRemain = vendorModel.isNoProductRemain();
		if (isNoProductRemain.get()) {
			errorEvent.setValue(new AllProductEmptyErrorEvent());
			state = State.ShowGuide;
		} else {
			state = State.ShowMenu;
		}
	}

	private void onConfirmCount(int count) {
		int productRemain = selectedProduct.getRemainCount();
		if (count < 0) {
			errorEvent.setValue(new LessThanZeroErrorEvent());
		} else if (count > productRemain) {
			MoreThanRemainCountErrorEvent event = new MoreThanRemainCountErrorEvent();
			event.setProductRemain(productRemain);
			errorEvent.setValue(event);
		} else {
			selectedCount = count;
			selectedProduct.setRemainCount(productRemain - count);
			state = State.Result;
		}
	}

	private void onInvalidInput() {
		errorEvent.setValue(new InvalidInputErrorEvent());
	}

	/** 重置販賣機狀態 */
	private void reset() {
		selectedProduct = null;
		selectedCount = 0;
		state = State.ShowGuide;
	}
}
