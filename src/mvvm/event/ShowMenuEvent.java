package mvvm.event;

import java.util.ArrayList;
import java.util.List;

/** 顯示目錄居段事件 */
public class ShowMenuEvent implements StateEvent {

	/** 目錄資訊 */
	private final List<MenuItem> items = new ArrayList<>();

	public void addMenuItem(MenuItem item) {
		items.add(item);
	}

	public List<MenuItem> getItems() {
		return items;
	}

	/** 目錄 */
	public static class MenuItem {
		private int index;
		private String name;
		private int price;
		private int remainCount;

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

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
	}
}
