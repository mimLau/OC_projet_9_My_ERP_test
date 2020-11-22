package com.dummy.myerp.consumer.dao.impl.db.comptabilite;

import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.JournalComptableRM;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JournalComptableRMTest {
    @Test
    public void test_mapRow() throws SQLException {
        // Given
        JournalComptableRM journalComptableRM = new JournalComptableRM();

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(eq("code"))).thenReturn("123");
        when(resultSet.getString(eq("libelle"))).thenReturn("Dummy data");

        // When
        JournalComptable journalComptable = journalComptableRM.mapRow(resultSet, 1);

        // Then
        Assert.assertEquals(journalComptable.getCode(), "123");
        Assert.assertEquals(journalComptable.getLibelle(), "Dummy data");

    }
}
