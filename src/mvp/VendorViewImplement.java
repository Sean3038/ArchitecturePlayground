package mvp;

import java.util.List;

import common.Keyboard;
import common.Monitor;
import common.VendorModel;

public class VendorViewImplement implements VendorView {

	/** UI元件 */
	private final Monitor monitor = new Monitor();
	private final Keyboard keyboard = new Keyboard();
	
	/** Presenter */
	private final VendorPresenter presenter;

	private boolean isRunning = false;

	public VendorViewImplement(VendorPresenter presenter) {
		this.presenter = presenter;
	}

	public void launch() {
		startApplication();
	}

	private void startApplication() {
		presenter.bindView(this);
		isRunning = true;
		while (isRunning) {
			presenter.showCurrentState();
			String input = keyboard.input();
			presenter.onUserInput(input);
		}
		presenter.unBindView();
	}
	
	/** 顯示UI */
	@Override
	public void showGuide() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n～～歡迎，飲料訂起來～～\n");
		stringBuilder.append("(m) 菜單\n");
		stringBuilder.append("(q) 離開");
		monitor.showMessage(stringBuilder.toString());
	}

	@Override
	public void showMenu(List<String> names, List<Integer> prices, List<Integer> remainCounts) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n今天想喝什麼呀？\n");
		stringBuilder.append("今天有\n");
		for (int index = 0; index < names.size(); index++) {
			if (remainCounts.get(index) > 0) {
				stringBuilder.append("[").append(index).append("]");
				stringBuilder.append(names.get(index));
				stringBuilder.append("(售價").append(prices.get(index)).append(")");
				stringBuilder.append(" 還有 ").append(remainCounts.get(index)).append("杯\n");
			}
		}
		stringBuilder.append("請輸入欲點選編號:");
		monitor.showMessage(stringBuilder.toString());
	}

	@Override
	public void showConfirmProduct(String productName, int productPrice) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n您要的是 ");
		stringBuilder.append(productName);
		stringBuilder.append("(售價").append(productPrice).append(")");
		stringBuilder.append("嗎?(y/n)");
		monitor.showMessage(stringBuilder.toString());
	}

	@Override
	public void showConfirmCount(String productName) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n");
		stringBuilder.append(productName);
		stringBuilder.append(" 要幾杯呢？(p)退回");
		monitor.showMessage(stringBuilder.toString());
	}

	@Override
	public void showResult(String productName, int count, int totalPrice) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n非常感謝，您點的是 ");
		stringBuilder.append(productName);
		stringBuilder.append(" 一共 ").append(count).append("杯");
		stringBuilder.append("，總金額：").append(totalPrice).append("元\n");
		stringBuilder.append("(請按Enter回首頁....)");
		monitor.showMessage(stringBuilder.toString());
	}

	@Override
	public void notifyShutdown() {
		monitor.showMessage("\n程序結束\n");
		isRunning = false;
	}

	@Override
	public void alertAllProductEmpty() {
		monitor.showMessage("\n抱歉商品已售罄\n");
	}

	@Override
	public void alertLessThanZero() {
		monitor.showMessage("\n想睡了嗎？數量需大於零\n");
	}

	@Override
	public void alertMoreThanRemainCount(int productRemain) {
		monitor.showMessage("\n抱歉，剩餘數量不足，只剩 " + productRemain + " 杯\n");
	}

	@Override
	public void alertInvalidInput() {
		monitor.showMessage("\n別找麻煩啊...\n");
	}

	/** main */
	public static void main(String[] argv) {
		VendorModel vendorModel = new VendorModel();
		vendorModel.initialize();
		VendorPresenter presenter = new VendorPresenter(vendorModel);
		VendorViewImplement vendorViewImpl = new VendorViewImplement(presenter);
		vendorViewImpl.launch();
	}
}
