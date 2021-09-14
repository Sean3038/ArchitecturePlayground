package mvvm;

import java.util.List;

import common.Keyboard;
import common.Monitor;
import common.VendorModel;
import mvvm.event.*;
import mvvm.event.ShowMenuEvent.MenuItem;

public class VendorView {

    /** ViewModel */
    private final VendorViewModel vendorViewModel;

    /** UI元件 */
    private final Monitor monitor = new Monitor();
    private final Keyboard keyboard = new Keyboard();

    /** ViewModel State 觀察者 */
    private final Observer<StateEvent> stateEventObserver = value -> {
        if (value instanceof ShutdownEvent) {
            showShutdown();
        } else if (value instanceof ShowGuideEvent) {
            showGuide();
        } else if (value instanceof ShowMenuEvent) {
            ShowMenuEvent event = (ShowMenuEvent) value;
            showMenu(event.getItems());
        } else if (value instanceof ConfirmProductEvent) {
            ConfirmProductEvent event = (ConfirmProductEvent) value;
            showConfirmProduct(event.getProductName(), event.getProductPrice());
        } else if (value instanceof ConfirmCountEvent) {
            ConfirmCountEvent event = (ConfirmCountEvent) value;
            showConfirmCount(event.getProductName());
        } else if (value instanceof ResultEvent) {
            ResultEvent event = (ResultEvent) value;
            showResult(event.getProductName(), event.getCount(), event.getTotalPrice());
        }
    };

	/** ViewModel Error 觀察者 */
	private final Observer<ErrorEvent> errorEventObserver = value -> {
        if (value instanceof InvalidInputErrorEvent) {
            alertInvalidInput();
        } else if (value instanceof LessThanZeroErrorEvent) {
            alertLessThanZero();
        } else if (value instanceof MoreThanRemainCountErrorEvent) {
            MoreThanRemainCountErrorEvent event = (MoreThanRemainCountErrorEvent) value;
            alertMoreThanRemainCount(event.getProductRemain());
        } else if (value instanceof AllProductEmptyErrorEvent) {
            alertAllProductEmpty();
        }
    };

	private boolean isRunning = false;

    VendorView(VendorViewModel vendorViewModel) {
        this.vendorViewModel = vendorViewModel;
        bindViewModel();
    }

    /** 綁定ViewModel狀態 */
    private void bindViewModel() {
        vendorViewModel.stateEvent.addObserver(stateEventObserver);
        vendorViewModel.errorEvent.addObserver(errorEventObserver);
    }

    /** 解除綁定ViewModel狀態 */
    private void unbindViewModel() {
        vendorViewModel.stateEvent.removeObserver(stateEventObserver);
        vendorViewModel.errorEvent.removeObserver(errorEventObserver);
    }

    public void launch() {
        startApplication();
    }

    private void startApplication() {
        bindViewModel();
        isRunning = true;
        while (isRunning) {
            String input = keyboard.input();
            vendorViewModel.onUserInput(input);
        }
        unbindViewModel();
    }

    /** 顯示UI */
    private void showGuide() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n～～歡迎，飲料訂起來～～\n");
        stringBuilder.append("(m) 菜單\n");
        stringBuilder.append("(q) 離開");
        monitor.showMessage(stringBuilder.toString());
    }

    private void showMenu(List<MenuItem> items) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n今天想喝什麼呀？\n");
        stringBuilder.append("今天有\n");
        for (int index = 0; index < items.size(); index++) {
            MenuItem item = items.get(index);
            if (item.getRemainCount() > 0) {
                stringBuilder.append("[").append(index).append("]");
                stringBuilder.append(item.getName());
                stringBuilder.append("(售價").append(item.getPrice()).append(")");
                stringBuilder.append(" 還有 ").append(item.getRemainCount()).append("杯\n");
            }
        }
        stringBuilder.append("請輸入欲點選編號:");
        monitor.showMessage(stringBuilder.toString());
    }

    private void showConfirmProduct(String productName, int productPrice) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n您要的是 ");
        stringBuilder.append(productName);
        stringBuilder.append("(售價").append(productPrice).append(")");
        stringBuilder.append("嗎?(y/n)");
        monitor.showMessage(stringBuilder.toString());
    }

    private void showConfirmCount(String productName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append(productName);
        stringBuilder.append(" 要幾杯呢？(p)退回");
        monitor.showMessage(stringBuilder.toString());
    }

    private void showResult(String productName, int count, int totalPrice) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n非常感謝，您點的是 ");
        stringBuilder.append(productName);
        stringBuilder.append(" 一共 ").append(count).append("杯");
        stringBuilder.append("，總金額：").append(totalPrice).append("元\n");
        stringBuilder.append("(請按Enter回首頁....)");
        monitor.showMessage(stringBuilder.toString());
    }

    private void showShutdown() {
        monitor.showMessage("\n程序結束\n");
        isRunning = false;
    }

    private void alertAllProductEmpty() {
        monitor.showMessage("\n抱歉商品已售罄\n");
    }

    private void alertLessThanZero() {
        monitor.showMessage("\n想睡了嗎？數量需大於零\n");
    }

    private void alertMoreThanRemainCount(int productRemain) {
        monitor.showMessage("\n抱歉，剩餘數量不足，只剩 " + productRemain + " 杯\n");
    }

    private void alertInvalidInput() {
        monitor.showMessage("\n別找麻煩啊...\n");
    }

    /** main */
    public static void main(String[] argv) {
        VendorModel model = new VendorModel();
        model.initialize();
        VendorViewModel viewModel = new VendorViewModel(model);
        VendorView vendorView = new VendorView(viewModel);
        vendorView.launch();
    }
}
