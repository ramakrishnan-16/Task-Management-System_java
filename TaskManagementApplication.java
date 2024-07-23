//ramakrishnan_16
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class TaskManagementApplication extends JFrame implements ActionListener {

    private JTextField taskField, assigneeField, deadlineField;
    private JButton addButton, deleteButton, markIncompleteButton, switchButton;
    private JComboBox<String> sortComboBox;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private List<Task> tasks;
    private boolean isAdmin;

    public TaskManagementApplication() {
        setTitle("Task Management Application");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tasks = new ArrayList<>();

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel taskLabel = new JLabel("Task:");
        taskField = new JTextField();
        JLabel assigneeLabel = new JLabel("Employee name:");
        assigneeField = new JTextField();
        JLabel deadlineLabel = new JLabel("Deadline Date:");
        deadlineField = new JTextField();
        addButton = new JButton("Add Task");
        addButton.addActionListener(this);

        inputPanel.add(taskLabel);
        inputPanel.add(taskField);
        inputPanel.add(assigneeLabel);
        inputPanel.add(assigneeField);
        inputPanel.add(deadlineLabel);
        inputPanel.add(deadlineField);
        inputPanel.add(addButton);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        sortComboBox = new JComboBox<>(new String[]{"Sort by Task", "Sort by Employee", "Sort by Deadline"});
        sortComboBox.addActionListener(this);
        deleteButton = new JButton("Delete Selected Task");
        deleteButton.addActionListener(this);
        markIncompleteButton = new JButton("Mark Incomplete Tasks as Completed");
        markIncompleteButton.addActionListener(this);

        controlPanel.add(sortComboBox);
        controlPanel.add(deleteButton);
        controlPanel.add(markIncompleteButton);

        switchButton = new JButton("Switch to Admin/Employee");
        switchButton.addActionListener(this);

        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        switchPanel.add(switchButton);

        tableModel = new DefaultTableModel(new Object[]{"Task", "Assignee", "Deadline", "Completed"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };

        taskTable = new JTable(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(taskTable);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(tableScrollPane, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.SOUTH);
        getContentPane().add(switchPanel, BorderLayout.WEST);

        login();
    }

    private void login() {
        String[] options = {"Admin", "Employee"};
        int choice = JOptionPane.showOptionDialog(this, "Select User Type", "Login", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        isAdmin = (choice == 0);
        if (!isAdmin) { // Employee view
            taskField.setEditable(false);
            assigneeField.setEditable(false);
            deadlineField.setEditable(false);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            markIncompleteButton.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addTask();
        } else if (e.getSource() == deleteButton) {
            deleteSelectedTask();
        } else if (e.getSource() == sortComboBox) {
            sortTasks();
        } else if (e.getSource() == markIncompleteButton) {
            markSelectedTaskAsIncomplete();
        } else if (e.getSource() == switchButton) {
            switchUser();
        }
    }

    private void addTask() {
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "Only admin can add tasks.");
            return;
        }
        String task = taskField.getText().trim();
        String assignee = assigneeField.getText().trim();
        String deadline = deadlineField.getText().trim();
        boolean completed = false;

        if (!task.isEmpty() && !assignee.isEmpty() && !deadline.isEmpty()) {
            tasks.add(new Task(task, assignee, deadline, completed));
            refreshTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
        }
    }

    private void deleteSelectedTask() {
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "Only admin can delete tasks.");
            return;
        }
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            tasks.remove(selectedRow);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.");
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Task task : tasks) {
            tableModel.addRow(new Object[]{task.getTask(), task.getAssignee(), task.getDeadline(), task.isCompleted()});
        }
    }

    private void clearFields() {
        taskField.setText("");
        assigneeField.setText("");
        deadlineField.setText("");
    }

    private void sortTasks() {
        int selectedIndex = sortComboBox.getSelectedIndex();
        switch (selectedIndex) {
            case 0:
                tasks.sort((t1, t2) -> t1.getTask().compareToIgnoreCase(t2.getTask()));
                break;
            case 1:
                tasks.sort((t1, t2) -> t1.getAssignee().compareToIgnoreCase(t2.getAssignee()));
                break;
            case 2:
                tasks.sort((t1, t2) -> t1.getDeadline().compareTo(t2.getDeadline()));
                break;
        }
        refreshTable();
    }

    private void markSelectedTaskAsIncomplete() {
    if (!isAdmin) {
        JOptionPane.showMessageDialog(this, "Only admin can mark tasks as incomplete.");
        return;
    }
    int selectedRow = taskTable.getSelectedRow();
    if (selectedRow != -1) {
        Task task = tasks.get(selectedRow);
        task.setCompleted(!task.isCompleted()); // Toggle completion status
        refreshTable();
    } else {
        JOptionPane.showMessageDialog(this, "Please select a task to mark as incomplete.");
    }
}


    private void switchUser() {
        login();
        if (isAdmin) {
            taskField.setEditable(true);
            assigneeField.setEditable(true);
            deadlineField.setEditable(true);
            addButton.setEnabled(true);
            deleteButton.setEnabled(true);
            markIncompleteButton.setEnabled(true);
        } else {
            taskField.setEditable(false);
            assigneeField.setEditable(false);
            deadlineField.setEditable(false);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            markIncompleteButton.setEnabled(false);
        }
    }

    private class Task {
        private String task;
        private String assignee;
        private String deadline;
        private boolean completed;

        public Task(String task, String assignee, String deadline, boolean completed) {
            this.task = task;
            this.assignee = assignee;
            this.deadline = deadline;
            this.completed = completed;
        }

        public String getTask() {
            return task;
        }

        public String getAssignee() {
            return assignee;
        }

        public String getDeadline() {
            return deadline;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }

    public static void main(String[] ramakrishnan_16) {
        SwingUtilities.invokeLater(() -> {
            new TaskManagementApplication().setVisible(true);
        });
    }
}
//ramakrishnan_16