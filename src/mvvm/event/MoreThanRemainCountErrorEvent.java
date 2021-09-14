package mvvm.event;

/** 輸入數量大於剩餘數量錯誤事件 */
public class MoreThanRemainCountErrorEvent implements ErrorEvent {
	private int productRemain;

	public int getProductRemain() {
		return productRemain;
	}

	public void setProductRemain(int productRemain) {
		this.productRemain = productRemain;
	}
}
