package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayPI extends Main {
    Integer[] array;

    public void task1() {
        String query = "SELECT table_name AS Названия_таблиц FROM information_schema.tables WHERE table_schema = '" + schema + "'";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            try {
                int nameLength = 15;
                while (rs.next()) {
                    int currentNameLength = rs.getString(1).length();
                    if (currentNameLength > nameLength) {
                        nameLength = currentNameLength;
                    }
                }
                String tablePart = "+" + "-".repeat(5) + "+" + "-".repeat(nameLength + 2) + "+";
                System.out.println("Список таблиц:");
                System.out.println(tablePart);
                System.out.printf("| %-3s | %-" + nameLength + "s |\n", "ID", "Названия таблиц");
                rs = st.executeQuery(query);
                int i = 1;
                while (rs.next()) {
                    String tableName = rs.getString("Названия_таблиц");
                    System.out.println(tablePart);
                    System.out.printf("| %-3d | %-" + nameLength + "s |\n", i++, tableName);
                }
                System.out.println(tablePart);
            } catch (SQLException e) {
                System.out.println("Не удалось вывести результат, " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Не удалось выполнить запрос, " + e.getMessage());
        }
    }

    public void task2() {
        System.out.print("Введите название таблицы: ");
        table = sc.next();
        sc.nextLine();
        String createTable = "CREATE TABLE IF NOT EXISTS " + table +" (ID SERIAL, unsorted_array int[], sorted_array int[])";
        try {
            PreparedStatement pst = con.prepareStatement(createTable);
            pst.executeUpdate();
            System.out.println("Таблица " + table + " успешно создана/выбрана!");
        } catch (SQLException e) {
            System.out.println("Не удалось выполнить запрос, " + e.getMessage());
            task2();
        }
    }

    public void task3() {
        System.out.print("Введите массив из 35 чисел: ");
        array = createArray();
        System.out.println("Массив: " + Arrays.toString(array));

        insertData();
        selectData();
    }

    public void task4() {
        if (array == null) {
            System.out.println("Матрицы пустые!");
            task3();
        }
        Sort sort = new Sort(array);
        sort.sort();

        selectData();
    }

    public void insertData() {
        System.out.println("Сохраняю в таблицу...");
        String insertIntoTable = "INSERT INTO " + table + " (unsorted_array, sorted_array) VALUES (?, ?)";
        try (PreparedStatement pst = con.prepareStatement(insertIntoTable)) {
            pst.setArray(1, con.createArrayOf("INTEGER", array));
            pst.setNull(2, Types.NULL);
            pst.executeUpdate();
            System.out.println("Все выполненные результаты добавлены в таблицу!");
        } catch (
                SQLException e) {
            System.out.println("Не удалось выполнить запрос, " + e.getMessage());
        }
    }

    public void selectData() {
        System.out.println("Получаю данные...");
        String query = "SELECT * FROM " + table;
        try (PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int length = rsmd.getColumnCount();

            String[] columnNames = new String[length];
            int[] maxLength = new int[length];

            List<List<String>> rows = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                columnNames[i] = rsmd.getColumnName(i + 1);
                maxLength[i] = rsmd.getColumnName(i + 1).length();
            }

            while (rs.next()) {
                List<String> row = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    String obj = rs.getString(i + 1);
                    obj = (obj != null) ? obj : "NULL";
                    row.add(obj);
                    if (obj.length() > maxLength[i]) {
                        maxLength[i] = obj.length();
                    }
                }
                rows.add(row);
            }

            StringBuilder border = new StringBuilder("+");
            StringBuilder header = new StringBuilder("|");

            for (int width : maxLength) {
                border.append("-".repeat(width + 2)).append("+");
            }
            System.out.println("Полученные данные из таблицы: ");
            System.out.println(border);


            for (int i = 0; i < length; i++) {
                header.append(" ").append(String.format("%-" + maxLength[i] + "s", columnNames[i])).append(" |");
            }
            System.out.println(header);
            System.out.println(border);

            for (List<String> row : rows) {
                StringBuilder rowStr = new StringBuilder("|");
                for (int i = 0; i < length; i++) {
                    String val = (i < row.size()) ? row.get(i) : "";
                    rowStr.append(" ").append(String.format("%-" + maxLength[i] + "s", val)).append(" |");
                }
                System.out.println(rowStr);
                System.out.println(border);
            }

        } catch (SQLException e) {
            System.out.println("Не удалось получить данные из таблицы, " + e.getMessage());
        }
    }

    public Integer[] createArray() {
        Integer[] array = new Integer[35];
        for (int i = 0; i < array.length; i++) {
            try {
                array[i] = Integer.parseInt(sc.next());
            } catch (NumberFormatException e) {
                System.out.print("Неправильный тип данных символа ");
                System.out.println(i);
                System.out.println("Продолжите массив чисел, начиная с " + i-- + " числа: ");
            }
        }

        sc.nextLine();
        return array;
    }
}
