package com.dummy.myerp.consumer.dao.impl.db.comptabilite;

import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.CompteComptableRM;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
public class CompteComptableRMTest {
    @Test
    public void test_mapRow() throws SQLException {
        // Given
        CompteComptableRM compteComptableRM = new CompteComptableRM();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(eq("numero"))).thenReturn(123);
        when(resultSet.getString(eq("libelle"))).thenReturn("Dummy data");

        // When
        CompteComptable compteComptable = compteComptableRM.mapRow(resultSet, 123);

        // Then
        Assert.assertEquals(compteComptable.getNumero(), Integer.valueOf(123));
        Assert.assertEquals(compteComptable.getLibelle(), "Dummy data");

    }
}
