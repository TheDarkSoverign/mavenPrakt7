package org.example;

import java.sql.*;
import java.util.Arrays;

public class ArrayPI extends Main {
    Integer[] array;

    public void task1() {
        String query = "SELECT table_name AS Названия_таблиц FROM information_schema.tables WHERE table_schema = '" + schema + "'";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            try {
                System.out.println("Список таблиц:");
                while (rs.next()) {
                    String tableName = rs.getString("Названия_таблиц");
                    System.out.println(tableName);
                }
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
        try (PreparedStatement pst = con.prepareStatement(selectFromTable)) {
            try (ResultSet rs = pst.executeQuery()) {
                System.out.println("Полученные данные: ");
                System.out.printf("%3s | %-150s | %-150s \n", "ID", "Изначальный список", "Отсортированный список");
                while (rs.next()) {
                    int ID = rs.getInt(1);

                    Array array1 = rs.getArray(2);
                    Integer[] array = (Integer[]) array1.getArray();

                    Array arraySort1 = rs.getArray(3);
                    if (arraySort1 != null) {
                        Integer[] arraySort = (Integer[]) arraySort1.getArray();
                        System.out.printf("%2d. | %-150s | %-150s \n", ID, Arrays.toString(array), Arrays.toString(arraySort));
                    } else {
                        System.out.printf("%2d. | %-150s | %-150s \n", ID, Arrays.toString(array), "NULL");
                    }
                }
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
