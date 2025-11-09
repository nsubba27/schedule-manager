/**
 * ********************************************************************************
 * File         : Item.java
 * Project      : schedule-manager
 * Author       : Nishan Subba
 * Date         : November 07, 2025
 * Description  : Defines the Item class, representing a single task node in a
 * schedule linked list. Each Item stores the task name, due date,
 * due time, and a reference to the next task in sequence.
 * ********************************************************************************
 */


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The Item class represents a single task node in the schedule.
 * Each item contains a name, date, time, and a pointer to the next node.
 */
public class Item {

    /**
     * Static counter used to generate unique IDs for each Item instance.
     */
    private static int itemCounter = 0;

    /**
     * Unique identifier for each item.
     */
    private final int id;

    /**
     * The name or description of the task.
     */
    private String name;

    /**
     * The time when the task is due.
     */
    private LocalTime time;

    /**
     * The date when the task is due.
     */
    private LocalDate date;

    /**
     * Pointer to the next Item node in the linked list.
     */
    private Item next;

    /**
     * Formatter used to display time in hh:mm AM/PM format.
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    /**
     * Formatter used to display date in MM/dd/yyyy format.
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    /**
     * Default constructor that initializes an empty Item with the current date and time.
     */
    public Item() {
        this.id = ++itemCounter;
        this.name = "";
        this.time = LocalTime.now();
        this.date = LocalDate.now();
        this.next = null;
    }

    /**
     * Constructs a new Item with the specified date, time, and name.
     *
     * @param date the date when the task is due
     * @param time the time when the task is due
     * @param name the name of the task
     */
    public Item(LocalDate date, LocalTime time, String name) {
        this.id = ++itemCounter;
        this.name = name;
        this.time = time;
        this.date = date;
        this.next = null;
    }

    /**
     * Returns the name of the task.
     *
     * @return the task name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the time when the task is due.
     *
     * @return the task time
     */
    public LocalTime getTime() {
        return this.time;
    }

    /**
     * Returns the next Item node in the linked list.
     *
     * @return the next Item
     */
    public Item getNext() {
        return this.next;
    }

    /**
     * Returns the date when the task is due.
     *
     * @return the task date
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * Sets the date of the task from a string in MM/dd/yyyy format.
     *
     * @param date the date string to parse
     */
    public void setDate(String date) {
        try {
            this.date = LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("⚠️  Invalid date format! Please use MM/dd/yyyy (Ex: 10/20/2025).");
        }
    }

    /**
     * Sets the name or description of the task.
     *
     * @param name the task name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the time of the task from a string in hh:mm AM/PM format.
     *
     * @param time the time string to parse
     */
    public void setTime(String time) {
        try {
            this.time = LocalTime.parse(time.toUpperCase(), TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("⚠️  Invalid time format! Please use hh:mm AM/PM (Ex: 02:30 PM).");
        }
    }

    /**
     * Sets the reference to the next Item node in the linked list.
     *
     * @param next the next Item
     */
    public void setNext(Item next) {
        this.next = next;
    }

    /**
     * Combines the date and time into a single {@link LocalDateTime} object.
     *
     * @return a LocalDateTime representing the task's date and time
     */
    public LocalDateTime getDateTime() {
        // create an instance of date time from the item for comparison
        return LocalDateTime.of(date, time);
    }

    /**
     * Returns the unique ID assigned to this Item.
     *
     * @return the item ID
     */
    public int getId() {
        return id;
    }

    /**
     * Resets the static item counter back to zero.
     * Useful for resetting during testing or program restart.
     */
    public void resetItemCounter() {
        itemCounter = 0;
    }

    /**
     * Returns a string representation of the task in "MM/dd/yyyy : hh:mm a : name" format.
     *
     * @return a formatted string of the item's details
     */
    @Override
    public String toString() {
        return date.format(DATE_FORMATTER) + " : " + time.format(TIME_FORMATTER) + " : " + name;
    }
}
