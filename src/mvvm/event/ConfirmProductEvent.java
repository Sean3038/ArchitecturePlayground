package mvvm.event;

/** 確認商品事件 */
public class ConfirmProductEvent implements StateEvent {
	private String productName;
	private int productPrice;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(int productPrice) {
		this.productPrice = productPrice;
	}
}
