package mvc;

import java.util.List;

import common.Call;
import common.Keyboard;
import common.Monitor;
import common.Product;
import common.Utils;
import common.VendorModel;

public class VendorMachineController {

	/** 階段 */
	private enum State {
		Shutdown, 		// 關閉程式
		ShowGuide, 		// 顯示歡迎畫面
		ShowMenu, 		// 展示目錄
		ConfirmProduct, // 確認品項
		ConfirmCount, 	// 確認數量
		Result 			// 顯示結帳資訊
	}

	/** 程式狀態 */
	private State state = State.ShowGuide;
	private Product selectedProduct = null;
	private int selectedCount = 0;

	/** View */
	private final Monitor monitor;
	private final Keyboard keyboard;

	/** Model */
	private final VendorModel vendorModel;

	private boolean isRunning = false;

	VendorMachineController(Monitor monitor, Keyboard keyboard, VendorModel vendorModel) {
		this.monitor = monitor;
		this.keyboard = keyboard;
		this.vendorModel = vendorModel;
	}

	/** 啟動程式 */
	public void launch() {
		startApplication();
	}

	private void startApplication() {
		isRunning = true;
		while (isRunning) {
			if (state == State.ShowGuide) {
				showGuide();

			} else if (state == State.ShowMenu) {
				Call<List<Product>> servingProducts = vendorModel.getServingProducts();
				showMenu(servingProducts.get());

			} else if (state == State.ConfirmProduct) {
				showConfirmProduct(selectedProduct);

			} else if (state == State.ConfirmCount) {
				showConfirmCount(selectedProduct);

			} else if (state == State.Result) {
				int totalPrice = selectedProduct.getPrice() * selectedCount;
				showResult(selectedProduct, selectedCount, totalPrice);
			}

			String input = keyboard.input();

			onUserInput(input);
		}
	}

	/** 使用者操作 */
	private void onUserInput(String input) {
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
			boolean isVendorServe = Utils.isNumeric(input)
					&& Integer.parseInt(input) < productCount.get()
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

	private void onShowMenu() {
		Call<Boolean> isNoProductRemain = vendorModel.isNoProductRemain();
		if (isNoProductRemain.get()) {
			monitor.showMessage("\n抱歉商品已售罄\n");
			state = State.ShowGuide;
		}
	}

	private void onConfirmCount(int count) {
		int productRemain = selectedProduct.getRemainCount();
		if (count < 0) {
			monitor.showMessage("想睡了嗎？數量需大於零");
		} else if (count > productRemain) {
			monitor.showMessage("抱歉，剩餘數量不足，只剩 " + productRemain + " 杯");
		} else {
			selectedCount = count;
			selectedProduct.setRemainCount(productRemain - count);
			state = State.Result;
		}
	}

	private void onInvalidInput() {
		monitor.showMessage("\n別找麻煩啊...");
	}

	private void onShutdown() {
		monitor.showMessage("\n程序結束\n");
		isRunning = false;
	}

	private void reset() {
		selectedProduct = null;
		selectedCount = 0;
		state = State.ShowGuide;
	}

	/** 顯示UI */
	private void showGuide() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n～～歡迎，飲料訂起來～～\n");
		stringBuilder.append("(m) 菜單\n");
		stringBuilder.append("(q) 離開");
		monitor.showMessage(stringBuilder.toString());
	}

	private void showMenu(List<Product> products) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n今天想喝什麼呀？\n");
		stringBuilder.append("今天有\n");
		for (int index = 0; index < products.size(); index++) {
			Product product = products.get(index);
			if (product.getRemainCount() > 0) {
				stringBuilder.append("[").append(index).append("]");
				stringBuilder.append(product.getName());
				stringBuilder.append("(售價").append(product.getPrice()).append(")");
				stringBuilder.append(" 還有 ").append(product.getRemainCount()).append("杯\n");
			}
		}
		stringBuilder.append("請輸入欲點選編號:");
		monitor.showMessage(stringBuilder.toString());
	}

	private void showConfirmProduct(Product product) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n您要的是 ");
		stringBuilder.append(product.getName());
		stringBuilder.append("(售價").append(product.getPrice()).append(")");
		stringBuilder.append("嗎?(y/n)");
		monitor.showMessage(stringBuilder.toString());
	}

	private void showConfirmCount(Product product) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n");
		stringBuilder.append(product.getName());
		stringBuilder.append(" 要幾杯呢？(p)退回");
		monitor.showMessage(stringBuilder.toString());
	}

	private void showResult(Product product, int count, int totalPrice) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n非常感謝，您點的是 ");
		stringBuilder.append(product.getName());
		stringBuilder.append(" 一共 ").append(count).append("杯");
		stringBuilder.append("，總金額：").append(totalPrice).append("元\n");
		stringBuilder.append("(請按Enter回首頁....)");
		monitor.showMessage(stringBuilder.toString());
	}

	/** main */
	public static void main(String[] argv) {
		Monitor monitor = new Monitor();
		Keyboard keyboard = new Keyboard();
		VendorModel vendorModel = new VendorModel();
		vendorModel.initialize();
		VendorMachineController controller = new VendorMachineController(monitor, keyboard, vendorModel);
		controller.launch();
	}
}
