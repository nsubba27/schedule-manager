/**
 * ********************************************************************************
 * File         : Main.java
 * Project      : schedule-manager
 * Author       : Nishan Subba
 * Date         : November 07, 2025
 * Description  : The entry point of the Schedule Manager program. Handles user
 * interaction for creating or opening existing schedule files.
 * Initializes the Manager class to load schedules from file and
 * launches the interactive main menu for managing tasks.
 * ********************************************************************************
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * The entry point of the Schedule Manager program.
 * Handles user interaction for creating or opening files,
 * initializes the Manager class, and starts the main menu.
 */
public class Main {

    /**
     * Directory path where all schedule files are stored.
     */
    private final static String DIR = "src/main/resources/";

    /**
     * Main method — program execution starts here.
     * Prompts the user to either open an existing file or create a new one,
     * then initializes the {@link Manager} to manage schedules.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("📁 Do you want to open an existing file or create a new one?");
        System.out.print("Enter (O) to Open or (N) to Create New: ");

        String option = scanner.nextLine().trim();
        String fileName;

        if (option.equalsIgnoreCase("O")) {
            Manager tempManger = new Manager(DIR);
            List<String> files = tempManger.readDirectory();

            if (files.isEmpty()) {
                System.out.println("⚠️  No files found in directory. Creating a new file instead.");
                fileName = createNewFile(scanner);
            } else {
                System.out.println("\nAvailable schedule files:");

                for (int i = 0; i < files.size(); i++) {
                    System.out.println(" " + (i + 1) + ". " + files.get(i));
                }

                System.out.print("Enter the number of the file you want to open: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice > 0 && choice <= files.size()) {
                    fileName = files.get(choice - 1);
                    System.out.println("✅ Opening file: " + fileName);
                    System.out.println("📂 Path: " + DIR + fileName);
                } else {
                    System.out.println("⚠️  Invalid choice. Creating a new file instead.");
                    fileName = createNewFile(scanner);
                }
            }

        } else {
            fileName = createNewFile(scanner);
        }

        Manager m = new Manager(DIR + fileName);
        m.readFile();
        m.mainMenu();

        scanner.close();
    }

    /**
     * Creates a new schedule file if it does not already exist.
     * If the directory does not exist, it will be created automatically.
     *
     * @param scanner the Scanner object used for user input
     * @return the name of the new or existing file
     */
    public static String createNewFile(Scanner scanner) {

        File dir = new File(DIR);

        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("📁 Directory created: " + dir.getAbsolutePath());
            } else {
                System.out.println("⚠️  Failed to create directory: " + dir.getAbsolutePath());
            }
        }

        System.out.print("Enter the name of the new file (without extension): ");
        String newFileName = scanner.nextLine().trim();
        if (newFileName.isBlank()) {
            newFileName = "schedule_" + System.currentTimeMillis() + ".txt";
            System.out.println("⚠️  Empty name entered — defaulting to: " + newFileName);
        } else if (!newFileName.endsWith(".txt")) {
            newFileName += ".txt";
        }

        File newFile = new File(DIR + newFileName);
        try {
            if (newFile.exists()) {
                System.out.println("ℹ️  File already exists, using existing one: " + newFile.getName());
            } else {
                // ✅ Use BufferedWriter to create and write header
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(newFile))) {
                    writer.write("");
                    System.out.println("✅ File created successfully: " + newFile.getName());
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error creating file: " + newFile.getName() + " - " + e.getMessage());
        }

        return newFileName;
    }
}
