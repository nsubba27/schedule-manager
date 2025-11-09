/**
 * ********************************************************************************
 * File         : Manager.java
 * Project      : schedule-manager
 * Author       : Nishan Subba
 * Date         : November 07, 2025
 * Description  : Acts as the controller for all schedule operations. Responsible
 * for reading and writing schedule data, managing multiple schedules,
 * inserting and removing tasks, exporting schedules to CSV, and
 * interacting with the user through a text-based interface.
 * Dependencies :
 * Relies on Schedule.java for linked-list operations.
 * Uses Item.java to represent individual tasks.
 * ********************************************************************************
 */

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The Manager class handles all core operations such as reading and writing schedules,
 * inserting and removing tasks, exporting to CSV, and displaying data to the user.
 */
public class Manager {

    /**
     * A list that holds all schedule objects currently in memory.
     */
    private final List<Schedule> schedules;

    /**
     * The name of the data file associated with the manager.
     */
    private final String fileName;

    /**
     * A single shared Scanner instance for user input throughout the program.
     */
    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * The directory where schedule files are stored and accessed.
     */
    private final static String DIR = "src/main/resources/";

    /**
     * Constructs a new Manager instance with the specified file name.
     *
     * @param fileName the file name to read from or write to
     */
    public Manager(String fileName) {
        this.fileName = fileName;
        this.schedules = new ArrayList<>();
    }

    /**
     * Displays all schedules and their tasks in a formatted table.
     * Prints task IDs, date, time, and name for each schedule.
     */
    public void displaySchedules() {
        if (schedules.isEmpty()) {
            System.out.println("⚠️  No schedules available to display");
            return;
        }

        for (Schedule schedule : schedules) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("📅 Schedule: " + schedule.getName());
            System.out.println("=".repeat(60));
            System.out.printf(" %-3s | %-5s | %-14s | %-8s | %s%n", "#", "ID", "Date", "Time", "Task");
            System.out.println("=".repeat(60));

            Item current = schedule.getHead();
            int count = 1;

            while (current != null) {
                System.out.printf(" %-3d | %-5d | %-14s | %-8s | %s%n",
                        count,
                        current.getId(),
                        current.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                        current.getTime().format(DateTimeFormatter.ofPattern("hh:mm a")),
                        current.getName());
                current = current.getNext();
                count++;
            }

            System.out.println("═".repeat(60));
            System.out.println("Total: " + schedule.getSize() + " task(s)\n");
        }
    }

    /**
     * Reads the schedule directory and returns all available files
     * that end with .txt or .csv extensions.
     *
     * @return a list of available file names
     */
    public List<String> readDirectory() {
        File[] files = new File(DIR).listFiles();
        List<String> fileManager = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    fileManager.add(file.getName());
                }
            }
        }
        return fileManager;
    }

    /**
     * Reads the schedule data from the specified file and loads tasks into memory.
     * Automatically creates schedules if they do not already exist.
     */
    public void readFile() {
        int itemCounts = 0;

        File file = new File(fileName);

        if (!file.exists()) {
            System.out.println("❌ " + file.getName() + " does not exist");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            System.out.println("📂 File opened successfully: " + file.getName());
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] split_line = line.split(";");

                if (split_line.length < 4) {
                    System.out.println("⚠️  Skipping malformed line: " + line);
                    continue;
                }

                String scheduleName = split_line[0];
                int index = findSchedule(scheduleName);
                if (index == -1) {
                    Schedule schedule = new Schedule(scheduleName);
                    schedules.add(schedule);
                    index = schedules.size() - 1;
                }

                Item item = new Item();
                item.setName(split_line[3]);
                item.setDate(split_line[1]);
                item.setTime(split_line[2]);
                schedules.get(index).insertSorted(item);
                itemCounts++;
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading file: " + e.getMessage());
        }

        System.out.println("✅ Successfully loaded " + itemCounts + " task(s) across " + schedules.size() + " schedule(s).\n");

    }

    /**
     * Allows the user to insert a new item into an existing or new schedule.
     * The item is validated to prevent duplicates and appended to the file.
     */
    public void insertNewItem() {
        System.out.println("\n🗓️  Which schedule would you like to insert into?");
        String scheduleName = SCANNER.nextLine();

        int index = findSchedule(scheduleName);

        if (index == -1) {
            Schedule schedule = new Schedule(scheduleName);
            schedules.add(schedule);
            System.out.println("✨ New schedule created: " + scheduleName);
            index = schedules.size() - 1;
        }
        System.out.println("📋 Adding task to: " + schedules.get(index).getName());

        Item item = new Item();

        System.out.println("📅 Enter date (MM/dd/yyyy): ");
        item.setDate(SCANNER.nextLine());

        System.out.println("⏰ Enter time (hh:mm AM/PM): ");
        item.setTime(SCANNER.nextLine());

        System.out.println("📝 Enter task description: ");
        item.setName(SCANNER.nextLine());

        LocalDateTime ldt = LocalDateTime.of(item.getDate(), item.getTime());
        Item existingItem = schedules.get(index).getData(ldt);

        if (existingItem != null) {
            System.out.println("\n⚠️  Schedule Conflict Detected!");
            System.out.println("A task already exists at the same date and time:");
            System.out.println("👉 " + existingItem);
            return;
        }

        schedules.get(index).insertSorted(item);
        System.out.println("✅ Task successfully added!\n");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            String itemFormatter = scheduleName + ";" +
                    item.getDate().format(Item.DATE_FORMATTER) + ";" +
                    item.getTime().format(Item.TIME_FORMATTER) + ";" +
                    item.getName();

            writer.write(itemFormatter);
            writer.newLine();
            System.out.println("💾 Task written to file successfully!");
        } catch (IOException e) {
            System.out.println("❌ Error writing to file: " + e.getMessage());
        }
    }

    /**
     * Removes an item from a specific schedule by ID after user confirmation.
     */
    public void removeItemFromSchedule() {
        if (schedules.isEmpty()) {
            System.out.println("\n⚠️  No schedules available to modify.\n");
            return;
        }

        System.out.println("\n🗑️  Which schedule would you like to delete an item from?");
        String scheduleName = SCANNER.nextLine();

        int index = findSchedule(scheduleName);

        if (index == -1) {
            System.out.println("❌ Schedule not found: " + scheduleName);
            return;
        }

        System.out.println("Which item task would you like to delete?");
        int id = SCANNER.nextInt();
        SCANNER.nextLine();

        if (!schedules.get(index).itemExists(id)) {
            System.out.println("⚠️  ID " + id + " not found in schedule: " + schedules.get(index).getName());
        } else {
            schedules.get(index).removeItem(id);
            System.out.println("✅ Task ID " + id + " successfully removed.\n");
        }
    }

    /**
     * Finds a schedule by its name.
     *
     * @param scheduleName the name of the schedule to search for
     * @return the index of the schedule in the list, or -1 if not found
     */
    public int findSchedule(String scheduleName) {
        for (int i = 0; i < schedules.size(); i++) {
            if (schedules.get(i).getName().equals(scheduleName)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Allows the user to reverse a specific schedule.
     * Displays available schedules and prompts for user input.
     */
    public void reverseSchedule() {
        if (schedules.isEmpty()) {
            System.out.println("\n⚠️  No schedules available to reverse.\n");
            return;
        }

        if (schedules.size() == 1) {
            schedules.getFirst().reverseSchedule();
            return;
        }

        int choice;

        do {
            System.out.println("\n🔄 Which schedule would you like to reverse?");

            for (int i = 0; i < schedules.size(); i++) {
                System.out.println(i + ": " + schedules.get(i).getName());
            }
            System.out.println(schedules.size() + ": Exit");
            System.out.print("Enter your choice: ");

            while (!SCANNER.hasNextInt()) {
                System.out.print("⚠️  Please enter a valid number (0 - " + schedules.size() + "): ");
                SCANNER.nextLine();
            }

            choice = SCANNER.nextInt();
            SCANNER.nextLine();

            if (choice >= 0 && choice < schedules.size()) {
                schedules.get(choice).reverseSchedule();
            } else if (choice != schedules.size()) {
                System.out.println("⚠️  Invalid choice, please try again.");
            }

        } while (choice != schedules.size());

        System.out.println("Which schedule would you like to reverse?");

        for (int i = 0; i < schedules.size(); i++) {
            System.out.println(i + ": " + schedules.get(i).getName());
        }
        System.out.println("Enter your choice: ");
    }

    /**
     * Removes an entire schedule by name.
     */
    public void removeSchedule() {
        if (schedules.isEmpty()) {
            System.out.println("\n⚠️  No schedules available to remove.\n");
            return;
        }

        System.out.println("\n🗑️  Which schedule would you like to remove?");
        String scheduleName = SCANNER.nextLine();

        int index = findSchedule(scheduleName);

        if (index == -1) {
            System.out.println("\n⚠️  schedule " + scheduleName + " does not exist.\n");
            return;
        }

        schedules.remove(index);
        System.out.println("✅ Schedule " + scheduleName + " successfully removed.\n");
    }

    /**
     * Exports all schedules and their items to a CSV file in the same directory.
     */
    public void exportSchedule() {
        if (schedules.isEmpty()) {
            System.out.println("⚠️  No schedules available to export.");
            return;
        }

        String csvFileName = fileName.replace(".txt", ".csv");
        File csvFile = new File(csvFileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFileName))) {
            StringBuilder sb = new StringBuilder();

            sb.append("ScheduleName,Date,Time,Task\n");

            for (Schedule schedule : schedules) {
                Item current = schedule.getHead();
                while (current != null) {
                    sb.append(schedule.getName()).append(",")
                            .append(current.getDate()).append(",")
                            .append(current.getTime()).append(",")
                            .append(current.getName().replace(",", " ")).append(",")
                            .append("\n");
                    current = current.getNext();
                }
            }

            writer.write(sb.toString());
            System.out.println("✅ Successfully exported to: " + csvFile.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("❌ Error exporting to CSV: " + e.getMessage());
        }
    }

    /**
     * Displays the interactive main menu for the schedule manager.
     * Handles all major user options and loops until exit.
     */
    public void mainMenu() {
        int choice;
        do {
            System.out.println("\n📘 ===============================");
            System.out.println(" Main Menu - Schedule Manager");
            System.out.println("=================================");
            System.out.println(" 1️⃣  Display All Schedules");
            System.out.println(" 2️⃣  Reverse a Schedule");
            System.out.println(" 3️⃣  Insert New Task");
            System.out.println(" 4️⃣  Remove Task by ID");
            System.out.println(" 5️⃣  Remove Schedule");
            System.out.println(" 6️⃣  Export Schedule to CSV");
            System.out.println(" 7️⃣  Exit");
            System.out.print("Enter your choice: ");

            while (!SCANNER.hasNextInt()) {
                System.out.println("⚠️  Please enter a valid number (1-7): ");
                SCANNER.nextLine();
            }
            choice = SCANNER.nextInt();
            SCANNER.nextLine();
            switch (choice) {
                case 1:
                    displaySchedules();
                    break;
                case 2:
                    reverseSchedule();
                    break;
                case 3:
                    insertNewItem();
                    break;
                case 4:
                    removeItemFromSchedule();
                    break;
                case 5:
                    removeSchedule();
                    break;
                case 6:
                    exportSchedule();
                    break;
                case 7:
                    System.out.println("👋 Exiting Schedule Manager. Goodbye!");
                    break;
                default:
                    System.out.println("⚠️  Invalid choice. Try again.");
            }

        } while (choice != 7);

        SCANNER.close();
    }

}
