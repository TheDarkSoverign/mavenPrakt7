package org.example;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public final class Sort extends ArrayPI {
    Integer[] array_sort;

    Sort(Integer[] array) {
        this.array_sort = array;
    }

    public void sort() {
        System.out.println("Запускаю сортировку...");
        for (int i = 0; i < array_sort.length - 1; i++) {
            for(int j = 0; j < array_sort.length - i - 1; j++) {
                if(array_sort[j + 1] < array_sort[j]) {
                    int swap = array_sort[j];
                    array_sort[j] = array_sort[j + 1];
                    array_sort[j + 1] = swap;
                }
            }
        }
        System.out.print("Массив отсортирован: " + Arrays.toString(array_sort));

        insertData();
    }

    @Override
    public void insertData() {
        System.out.println("Сохраняю в таблицу...");
        String query = "UPDATE " + table + " SET sorted_array = ? WHERE id = (SELECT MAX(id) FROM " + table + " WHERE sorted_array IS NULL)";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setArray( 1, con.createArrayOf("INTEGER", array_sort));
            pst.executeUpdate();
            System.out.println("Все выполненные результаты добавлены в таблицу!");
        } catch (
                SQLException e) {
            System.out.println("Не удалось выполнить запрос, " + e.getMessage());
        }
    }
}
