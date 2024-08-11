import catalog.MainMenu;

public class Main {
    public static void main(String[] args) {
        while (true) {
            if (!MainMenu.displayMainMenu()) {
                break;
            }
        }
    }
}