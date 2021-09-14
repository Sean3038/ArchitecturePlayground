package common;

public class Product {
	private String name;
	private int price;
	private int remainCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getRemainCount() {
		return remainCount;
	}

	public void setRemainCount(int remainCount) {
		this.remainCount = remainCount;
	}
	
	public boolean isSoldOut(){
		return remainCount == 0;
	}
}
