import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Banking {
    private static final List<BankAccount> accounts = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        mainLoop:
        while (true) {
            Banking.printOptions();

            byte option = getOption(0, Consts.OPTION_COUNT);

            switch (option) {
                case 0:
                    break mainLoop;
                case 1:
                    registerAccount();
                    break;
                case 2:
                    checkBalance();
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

    private static byte getOption(int minVal, int maxVal) {
        byte option = -1;
        while (option == -1) {
            try {
                option = Banking.scanner.nextByte();

                if (option < minVal || option > maxVal) {
                    System.out.printf("Invalid input, please input a whole number in the range of [0; %d]%n", maxVal);
                    option = -1;
                }
            } catch (InputMismatchException inputExc) {
                System.out.printf("Invalid input, please input a whole number in the range of [0; %d]%n", maxVal);
                Banking.scanner.next();
            } catch (Exception exc) {
                System.out.println("An exception has occurred while trying to read user input.");
                Banking.scanner.next();
            }
        }
        return option;
    }

    /* ------------------------------------------------- BANK ACCOUNT ACTION METHODS -------------------------------------------------*/

    private static void registerAccount() {
        Banking.accounts.add(new BankAccount());

        System.out.println("Bank account " + accounts.size() + " has been successfully registered.");
    }

    private static void checkBalance() {
        System.out.println("Please select which account's balance would you like to check: ");
        int accountNum = getOption(0, Banking.accounts.size());
        System.out.println("Bank account " + accountNum + " has an account balance of: " +  accounts.get(accountNum));
    }

    /* ------------------------------------------------- UTIL METHODS -------------------------------------------------*/

    private static void printOptions() {
        System.out.println(Consts.MENU);
    }
}