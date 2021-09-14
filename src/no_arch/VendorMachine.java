package no_arch;

import common.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VendorMachine {
	
	/**階段*/
	private enum State {
		Shutdown, 			//關閉程式
		ShowGuide, 			//顯示歡迎畫面
		ShowMenu, 			//展示目錄
		ConfirmProduct, 	//確認品項
		ConfirmCount, 		//確認數量
		Result 				//顯示結帳資訊
	}

	/**商品資訊*/
	private final List<Product> productList = new ArrayList<>();

	/**使用者輸入介面*/
	private final Scanner scanner = new Scanner(System.in);
	
	/**程式狀態*/
	private State state = State.ShowGuide;
	private Product selectedProduct = null;
	private int selectedCount = 0;

	private boolean isRunning = false;
	
	/**啟動程式*/
	public void launch() {
		initializeProducts();
		startApplication();
	}

	private void startApplication() {
		isRunning = true;
		while (isRunning) {
			if (state == State.ShowGuide) {
				showGuide();

			} else if (state == State.ShowMenu) {
				showMenu();

			} else if (state == State.ConfirmProduct) {
				showConfirmProduct(selectedProduct);

			} else if (state == State.ConfirmCount) {
				showConfirmCount(selectedProduct);

			} else if (state == State.Result) {
				int totalPrice = selectedProduct.getPrice() * selectedCount;
				showResult(selectedProduct, selectedCount, totalPrice);
			}

			String input = scanner.nextLine();

			onUserInput(input);
		}
	}

	/**使用者操作*/
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
			if (isNumeric(input) && Integer.parseInt(input) < productList.size() && Integer.parseInt(input) >= 0) {
				state = State.ConfirmProduct;
				selectedProduct = productList.get(Integer.parseInt(input));
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
			if (isNumeric(input)) {
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
		boolean isAllProductSoldOut = true;
		for (Product product : productList) {
			if (product.getRemainCount() > 0) {
				isAllProductSoldOut = false;
				break;
			}
		}
		if (isAllProductSoldOut) {
			showMessage("\n抱歉商品已售罄\n");
			state = State.ShowGuide;
		}
	}

	private void onInvalidInput() {
		showMessage("\n別找麻煩啊...");
	}

	private void onConfirmCount(int count) {
		int productRemain = selectedProduct.getRemainCount();
		if (count < 0) {
			showMessage("想睡了嗎？數量需大於零");
		} else if (count > productRemain) {
			showMessage("抱歉，剩餘數量不足，只剩 " + productRemain + " 杯");
		} else {
			selectedCount = count;
			selectedProduct.setRemainCount(productRemain - count);
			state = State.Result;
		}
	}

	private void onShutdown() {
		showMessage("\n程序結束\n");
		isRunning = true;
	}

	private void reset() {
		selectedProduct = null;
		selectedCount = 0;
		state = State.ShowGuide;
	}
	
	/**顯示UI*/
	private void showGuide() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n～～歡迎，飲料訂起來～～\n");
		stringBuilder.append("(m) 菜單\n");
		stringBuilder.append("(q) 離開");
		showMessage(stringBuilder.toString());
	}

	private void showMenu() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n今天想喝什麼呀？\n");
		stringBuilder.append("今天有\n");
		for (int index = 0; index < productList.size(); index++) {
			Product product = productList.get(index);
			if (product.getRemainCount() > 0) {
				stringBuilder.append("[").append(index).append("]");
				stringBuilder.append(product.getName());
				stringBuilder.append("(售價").append(product.getPrice()).append(")");
				stringBuilder.append(" 還有 ").append(product.getRemainCount()).append("杯\n");
			}
		}
		stringBuilder.append("請輸入欲點選編號:");
		showMessage(stringBuilder.toString());
	}

	private void showConfirmProduct(Product product) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n您要的是 ");
		stringBuilder.append(product.getName());
		stringBuilder.append("(售價").append(product.getPrice()).append(")");
		stringBuilder.append("嗎?(y/n)");
		showMessage(stringBuilder.toString());
	}

	private void showConfirmCount(Product product) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n");
		stringBuilder.append(product.getName());
		stringBuilder.append(" 要幾杯呢？(p)退回");
		showMessage(stringBuilder.toString());
	}

	private void showResult(Product product, int count, int totalPrice) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n非常感謝，您點的是 ");
		stringBuilder.append(product.getName());
		stringBuilder.append(" 一共 ").append(count).append("杯");
		stringBuilder.append("，總金額：").append(totalPrice).append("元\n");
		stringBuilder.append("(請按Enter回首頁....)");
		showMessage(stringBuilder.toString());
	}

	/**啟動程式時取得商品資訊*/
	private void initializeProducts() {
		Product p1 = new Product();
		p1.setName("珍奶");
		p1.setPrice(45);
		p1.setRemainCount(50);
		Product p2 = new Product();
		p2.setName("紅茶");
		p2.setPrice(25);
		p2.setRemainCount(10);
		productList.add(p1);
		productList.add(p2);
	}

	/**顯示資訊至畫面*/
	private void showMessage(String message) {
		System.out.println(message);
	}

	/**判斷是否為純數字*/
	private boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**main*/
	public static void main(String[] argv) {
		VendorMachine machine = new VendorMachine();
		machine.launch();
	}
}
