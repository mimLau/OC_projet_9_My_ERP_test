package com.dummy.myerp.consumer.db.helper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResultSetHelperTest {

    @Test
    public void test_getInteger() throws SQLException {
        // Given
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(eq("id"))).thenReturn(123);

        // When
        int id = ResultSetHelper.getInteger(resultSet, "id");

        // Then
        Assert.assertTrue(id == 123);
    }

    @Test
    public void test_getLong() throws SQLException {
        // Given
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong(eq("id"))).thenReturn(123L);

        // When
        Long id = ResultSetHelper.getLong(resultSet, "id");

        // Then
        Assert.assertEquals(id, Long.valueOf(123));
    }

    @Test
    public void test_getDate() throws SQLException {
        // Given
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getDate(eq("date"))).thenReturn(new java.sql.Date(1560705990L));

        // When
        Date date = ResultSetHelper.getDate(resultSet, "date");

        // Then
        Assert.assertNotNull(date);
    }
}
