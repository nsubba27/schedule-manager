/**
 * ********************************************************************************
 * File         : Schedule.java
 * Project      : schedule-manager
 * Author       : Nishan Subba
 * Date         : November 07, 2025
 * Description  : Represents a schedule that holds multiple tasks using a linked
 * list of Item nodes. Each schedule maintains insertion order based
 * on task date and time, supporting chronological sorting and
 * efficient traversal.
 * Dependencies :
 * - Uses Item.java to represent individual tasks.
 * ********************************************************************************
 */


import java.time.LocalDateTime;

/**
 * The Schedule class represents a linked list of {@link Item} objects.
 * Each schedule maintains a list of tasks sorted by their date and time.
 */
public class Schedule {

    /**
     * The name of the schedule.
     */
    private String name;

    /**
     * The first node (head) of the linked list.
     */
    private Item head;

    /**
     * The last node (tail) of the linked list.
     */
    private Item tail;

    /**
     * The number of items (tasks) in this schedule.
     */
    private int size;

    /**
     * Default constructor used to construct an empty Schedule object.
     */
    public Schedule() {
        this.name = "";
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    /**
     * Constructs a Schedule object with a given name.
     *
     * @param name the name of the schedule
     */
    public Schedule(String name) {
        this.name = name;
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Inserts an {@link Item} into the schedule in sorted order based on its date and time.
     *
     * @param item the item to be inserted
     */
    public void insertSorted(Item item) {

        if (item == null) {
            System.out.println("⚠️  Cannot insert a null task.");
            return;
        }

        if (head == null) {
            head = item;
            tail = item;
            size++;
            return;
        }

        if (item.getDateTime().isBefore(head.getDateTime())) {
            item.setNext(head);
            head = item;
            size++;
            return;
        }

        Item current = this.head;

        while (current.getNext() != null && current.getNext().getDateTime().isBefore(item.getDateTime())) {
            current = current.getNext();
        }

        item.setNext(current.getNext());
        current.setNext(item);

        // update tail if inserted at end
        if (item.getNext() == null) {
            tail = item;
        }

        size++;
    }

    /**
     * Returns the name of the schedule.
     *
     * @return the schedule name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the number of tasks in the schedule.
     *
     * @return the size of the schedule
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Sets the name of the schedule.
     *
     * @param name the new name for the schedule
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the last {@link Item} in the schedule.
     *
     * @return the tail item
     */
    public Item getTail() {
        return this.tail;
    }

    /**
     * Reverses the linked list of items in this schedule.
     * Prints a message if the schedule is empty.
     */
    public void reverseSchedule() {
        if (size == 0) {
            System.out.println("⚠️  Schedule '" + name + "' is empty — nothing to reverse.");
            return;
        }

        Item current = head;
        Item previous = null;
        Item next;
        while (current != null) {
            next = current.getNext();
            current.setNext(previous);
            previous = current;
            current = next;
        }

        tail = head;
        head = previous;
        System.out.println("🔄 Schedule '" + name + "' has been reversed successfully!");
    }

    /**
     * Retrieves a task (item) based on its date and time.
     *
     * @param nodeNum the LocalDateTime representing the task to find
     * @return the matching item, or null if not found
     */
    public Item getData(LocalDateTime nodeNum) {
        Item current = head;

        while (current != null) {
            if (current.getDateTime().equals(nodeNum)) {
                return current;
            }
            current = current.getNext();
        }

        return null;
    }

    /**
     * Returns the first {@link Item} in the schedule.
     *
     * @return the head item
     */
    public Item getHead() {
        return head;
    }

    /**
     * Removes an item from the schedule by its ID.
     * Prints appropriate messages if the schedule is empty or the item is not found.
     *
     * @param id the ID of the item to remove
     */
    public void removeItem(int id) {

        // check if list is empty
        if (head == null) {
            System.out.println("⚠️  Cannot insert a null task into schedule: " + name);
            return;
        }

        // check head
        if (head.getId() == id) {
            System.out.println("⚠️  Schedule '" + name + "' is empty. Nothing to reverse.");
            head = head.getNext();
            size--;
            return;
        }

        Item current = this.head;

        while (current.getNext() != null) {
            if (current.getNext().getId() == id) {
                Item removed = current.getNext();

                if (removed == tail) {
                    tail = current;
                }

                current.setNext(current.getNext().getNext());
                size--;
                System.out.println("🗑️  Task: " + head.getName() + ", ID: " + head.getId() + " removed from schedule: " + name);
                return;
            }
            current = current.getNext();
        }
        System.out.println("⚠️  Task ID " + id + " does not exist in schedule '" + name + "'.");
    }

    /**
     * Checks if an item with the specified ID exists in the schedule.
     *
     * @param id the ID to search for
     * @return true if the item exists, false otherwise
     */
    public boolean itemExists(int id) {
        Item current = this.head;

        while (current != null) {
            if (current.getId() == id) {
                return true;
            }

            current = current.getNext();
        }

        return false;
    }

    /**
     * Converts the schedule into a readable string listing all tasks.
     *
     * @return a string representation of the schedule
     */
    @Override
    public String toString() {
        Item current = head;
        StringBuilder sb = new StringBuilder();
        while (current != null) {
            sb.append(current).append("\n");
            current = current.getNext();
        }

        return sb.toString();
    }
}
