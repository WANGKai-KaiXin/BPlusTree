import java.util.Scanner;

public class UI {
    public BTree bTree;
    /**
     * Read btree from the file "data.txt"
     * check if the btree is built
     * if it is not built, then insert/delete/search etc. commands cannot be executed
     * if it is built, then the insert/delete/search etc. commands can be executed.
     */
    public boolean isReady = false;

    /**
     * User Interface
     * It is only responsible for checking if the input commands are valid,
     * regardless of whether the command is executed successfully.
     */
    public void runOnce() {
        //Get the input command
        Scanner scanner = new Scanner(System.in);
        String[] input;

        //deal with the input command
        while (true) {
            System.out.print("> ");
            input = scanner.nextLine().split(" ");
            if (!isReady) {
                if ("btree".equals(input[0])) {
                    try {
                        if ("-help".equals(input[1])) {
                            System.out.println("Usage: btree [fname]\n" +
                                    "   fname: the name of the data file storing the search key values");
                        } else {
                            bTree = new BTree(System.getProperty("user.dir") + "\\" + input[1]);
                            isReady = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Please input the valid command.");
                    }
                } else {
                    System.out.println("Cannot recognize the command. Please build the tree first.");
                }
            } else {
                switch (input[0]) {
                    case "insert":
                        try {
                            bTree.insert(Integer.parseInt(input[1]), Integer.parseInt(input[2]), Integer.parseInt(input[3]));
                            System.out.println("Insert successfully!");
                        } catch (Exception e) {
                            System.out.println("Please input valid numbers!");
                        }
                        break;
                    case "delete":
                        try {
                            if(bTree.search(Integer.parseInt(input[1]), Integer.parseInt(input[2])).equals("")){
                                System.out.println("Cannot find the key between ["+Integer.parseInt(input[1])+", "+Integer.parseInt(input[2])+"]");
                            }else {
                                bTree.delete(Integer.parseInt(input[1]), Integer.parseInt(input[2]));
                                System.out.println("Delete successfully!");
                            }
                        } catch (Exception e) {
                            System.out.println("Please input valid numbers!");
                        }
                        break;
                    case "print":
                        bTree.print();
                        break;
                    case "stats":
                        bTree.printStats();
                        break;
                    case "search":
                        try {
                            String output = bTree.search(Integer.parseInt(input[1]), Integer.parseInt(input[2]));
                            if(output.equals("")) {
                                System.out.println("Cannot find the key between ["+Integer.parseInt(input[1])+", "+Integer.parseInt(input[2])+"]");
                            }else{
                                System.out.println("Key(s) between ["+Integer.parseInt(input[1])+", "+Integer.parseInt(input[2])+"] is "+output);
                            }
                        } catch (Exception e) {
                            System.out.println("Please input valid numbers!");
                        }
                        break;
                    case "quit":
                        System.out.println("Thanks!Byebye");
                        System.exit(0);
                    default:
                        System.out.println("Cannot recognize the command. Please input valid command.");
                }
            }
        }
    }

    public static void main(String[] args) {
        UI ui = new UI();
        ui.runOnce();
    }
}