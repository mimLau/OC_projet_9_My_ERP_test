package com.dummy.myerp.consumer.dao.impl.db.comptabilite;

import com.dummy.myerp.consumer.dao.impl.cache.JournalComptableDaoCache;
import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.SequenceEcritureComptableRM;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SequenceEcritureComptableRMTest {
    private SequenceEcritureComptableRM sequenceEcritureComptableRM = new SequenceEcritureComptableRM();

    @Test
    public void test_mapRow() throws SQLException, NoSuchFieldException {
        // Given
        // Given
        JournalComptableDaoCache journalComptableDaoCache = mock(JournalComptableDaoCache.class);
        JournalComptable journalComptable = new JournalComptable("1234", "Un libellé");
        FieldSetter.setField(sequenceEcritureComptableRM, sequenceEcritureComptableRM.getClass().getDeclaredField("journalComptableDaoCache"), journalComptableDaoCache);
        when(journalComptableDaoCache.getByCode(anyString())).thenReturn(journalComptable);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(eq("journal_code"))).thenReturn("JCODE");
        when(resultSet.getInt(eq("annee"))).thenReturn(2020);
        when(resultSet.getInt(eq("derniere_valeur"))).thenReturn(123);

        // When
        SequenceEcritureComptable sequenceEcritureComptable = sequenceEcritureComptableRM.mapRow(resultSet, 1);

        // Then
        Assert.assertTrue(sequenceEcritureComptable.getAnnee() == 2020);
        Assert.assertTrue(sequenceEcritureComptable.getDerniereValeur() == 123);
    }
}
