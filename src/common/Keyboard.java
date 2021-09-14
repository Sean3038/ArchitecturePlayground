package common;

import java.util.Scanner;

/** UI元件 鍵盤 */
public class Keyboard {

	private final Scanner scanner = new Scanner(System.in);

	public String input() {
		return scanner.nextLine();
	}
}
