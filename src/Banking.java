import java.util.InputMismatchException;
import java.util.Scanner;

public class Banking {
    private static void printOptions() {
        System.out.println("------------------------------------------");
        System.out.println("0 - exit");
        System.out.println("1 - register a bank account");
        System.out.println("2 - check balance");
        System.out.println("3 - deposit money");
        System.out.println("4 - withdraw money");
        System.out.println("5 - transfer money");
        System.out.println("------------------------------------------");
        System.out.println("Waiting for user input...");
    }

    private static void registerAccount() {
        while (true) {

        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BankAccount[] accounts;

        mainLoop:
        while (true) {
            Banking.printOptions();

            byte option = -1;
            while (option == -1) {
                try {
                    option = scanner.nextByte();

                    if (option < 0 || option > 5) {
                        System.out.printf("Invalid input, please input a whole number in the range of [0; %d]%n", Consts.OPTION_COUNT);
                        option = -1;
                    }
                } catch (InputMismatchException inputExc) {
                    System.out.printf("Invalid input, please input a whole number in the range of [0; %d]%n", Consts.OPTION_COUNT);
                    scanner.next();
                } catch (Exception exc) {
                    System.out.println("An exception has occurred while trying to read user input.");
                    scanner.next();
                }
            }

            switch (option) {
                case 0:
                    break mainLoop;
                case 1:
                    registerAccount();
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                default:
                    break mainLoop;
            }
        }
    }
}