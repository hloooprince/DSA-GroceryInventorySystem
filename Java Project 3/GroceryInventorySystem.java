
import java.util.*;
import java.io.*;

// This class stores details of one grocery item
class Product {
    String id;
    String name;
    int quantity;
    double price;

    Product(String id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String toString() {
        return id + " | " + name + " | Qty: " + quantity
                + " | Price: " + price;
    }
}

// This class handles saving and loading inventory
class FileHandler {
    String fileName = "inventory.txt";

    ArrayList<Product> load() {
        ArrayList<Product> list = new ArrayList<>();

        try {
            File file = new File(fileName);

            if (!file.exists()) {
                return list;
            }

            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] data = line.split(",", 4);

                if (data.length == 4) {
                    try {
                        Product p = new Product(
                            data[0],
                            data[1],
                            Integer.parseInt(data[2]),
                            Double.parseDouble(data[3])
                        );

                        list.add(p);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid record skipped.");
                    }
                }
            }

            sc.close();

        } catch (IOException e) {
            System.out.println("Error loading file.");
        }

        return list;
    }

    void save(ArrayList<Product> list) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(fileName));

            for (Product p : list) {
                pw.println(p.id + "," + p.name + ","
                        + p.quantity + "," + p.price);
            }

            pw.close();
            System.out.println("Inventory saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving file.");
        }
    }
}

// This class manages all grocery items
class InventoryManager {
    ArrayList<Product> products = new ArrayList<>();
    HashMap<String, Product> productMap = new HashMap<>();

    FileHandler fileHandler;
    int nextId = 101;

    InventoryManager(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        loadFromFile();
    }

    // Load old inventory and update the ID counter
    void loadFromFile() {
        products = fileHandler.load();

        int highestId = 100;

        for (Product p : products) {
            productMap.put(p.id.toUpperCase(), p);

            if (p.id.toUpperCase().startsWith("G")) {
                try {
                    int number = Integer.parseInt(p.id.substring(1));
                    if (number > highestId) {
                        highestId = number;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid IDs
                }
            }
        }

        nextId = highestId + 1;
    }

    // Add a new grocery item
    void addItem(String name, int qty, double price) {
        String id = "G" + nextId++;

        Product p = new Product(id, name, qty, price);

        products.add(p);
        productMap.put(id, p);

        System.out.println("Item added successfully!");
        System.out.println(p);
    }

    // Sell an item using its ID
    void sellItem(String id, int qty) {
        Product p = productMap.get(id.toUpperCase());

        if (p == null) {
            System.out.println("Item not found.");
            return;
        }

        if (qty <= 0) {
            System.out.println("Enter a valid quantity.");
            return;
        }

        if (qty > p.quantity) {
            System.out.println("Insufficient stock. Available: "
                    + p.quantity);
            return;
        }

        p.quantity = p.quantity - qty;

        if (p.quantity == 0) {
            System.out.println("Sold " + qty + " of " + p.name
                    + ". Stock: 0 (Out of stock)");
        } else {
            System.out.println("Sold " + qty + " of " + p.name
                    + ". Stock: " + p.quantity);
        }
    }

    // Sort products alphabetically using Bubble Sort
    void sortByName() {
        for (int i = 0; i < products.size() - 1; i++) {
            for (int j = 0; j < products.size() - i - 1; j++) {

                String name1 = products.get(j).name;
                String name2 = products.get(j + 1).name;

                if (name1.compareToIgnoreCase(name2) > 0) {
                    Product temp = products.get(j);
                    products.set(j, products.get(j + 1));
                    products.set(j + 1, temp);
                }
            }
        }
    }

    // Display all items in sorted order
    void displaySorted() {
        if (products.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }

        sortByName();

        System.out.println("\n----- GROCERY INVENTORY -----");

        for (Product p : products) {
            System.out.println(p);
        }
    }

    // Display items having quantity below 5
    void lowStockReport() {
        boolean found = false;

        System.out.println("\n----- LOW STOCK REPORT -----");

        for (Product p : products) {
            if (p.quantity < 5) {
                System.out.println("Low Stock: " + p);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No low-stock items found.");
        }
    }

    void saveToFile() {
        fileHandler.save(products);
    }
}

// Main class of the project
public class GroceryInventorySystem {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        FileHandler fileHandler = new FileHandler();
        InventoryManager manager =
                new InventoryManager(fileHandler);

        int choice = 0;

        System.out.println("==================================");
        System.out.println("      GROCERY INVENTORY SYSTEM");
        System.out.println("==================================");
        System.out.println("Inventory loaded successfully.");

        do {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Add Item");
            System.out.println("2. Sell Item");
            System.out.println("3. Display Sorted by Name");
            System.out.println("4. Low-Stock Report");
            System.out.println("5. Exit");
            System.out.println("===============================");

            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    case 1:
                        System.out.print("Enter item name: ");
                        String name = sc.nextLine().trim();

                        if (name.isEmpty() || name.contains(",")) {
                            System.out.println("Enter a valid item name.");
                            break;
                        }

                        System.out.print("Enter quantity: ");
                        int qty = Integer.parseInt(sc.nextLine());

                        System.out.print("Enter price: ");
                        double price = Double.parseDouble(sc.nextLine());

                        if (qty <= 0 || price < 0) {
                            System.out.println("Invalid quantity or price.");
                            break;
                        }

                        manager.addItem(name, qty, price);
                        break;

                    case 2:
                        System.out.print("Enter Product ID: ");
                        String id = sc.nextLine().trim();

                        System.out.print("Enter quantity to sell: ");
                        int sellQty = Integer.parseInt(sc.nextLine());

                        manager.sellItem(id, sellQty);
                        break;

                    case 3:
                        manager.displaySorted();
                        break;

                    case 4:
                        manager.lowStockReport();
                        break;

                    case 5:
                        manager.saveToFile();
                        System.out.println("Thank you for using the system!");
                        break;

                    default:
                        System.out.println("Invalid choice. Try again.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }

        } while (choice != 5);

        sc.close();
    }
}
