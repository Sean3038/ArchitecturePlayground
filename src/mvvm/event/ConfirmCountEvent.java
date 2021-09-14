package mvvm.event;

/** 確認數量階段 */
public class ConfirmCountEvent implements StateEvent {
	private String productName;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
}
