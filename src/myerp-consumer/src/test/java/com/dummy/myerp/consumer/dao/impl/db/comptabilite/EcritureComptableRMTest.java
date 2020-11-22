package com.dummy.myerp.consumer.dao.impl.db.comptabilite;

import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.consumer.dao.impl.cache.JournalComptableDaoCache;
import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.EcritureComptableRM;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EcritureComptableRMTest {
    private EcritureComptableRM ecritureComptableRM = new EcritureComptableRM();;
    @Test
    public void test_mapRow() throws NoSuchFieldException, SQLException {
        // Given
        JournalComptableDaoCache journalComptableDaoCache = mock(JournalComptableDaoCache.class);
        JournalComptable journalComptable = new JournalComptable("1234", "Un libellé");
        FieldSetter.setField(ecritureComptableRM, ecritureComptableRM.getClass().getDeclaredField("journalComptableDaoCache"), journalComptableDaoCache);
        when(journalComptableDaoCache.getByCode(anyString())).thenReturn(journalComptable);

        DaoProxy daoProxy = mock(DaoProxy.class);
        ConsumerHelper.configure(daoProxy);
        ComptabiliteDao comptabiliteDao = mock(ComptabiliteDao.class);
        when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(eq("id"))).thenReturn(123);
        when(resultSet.getString(eq("journal_code"))).thenReturn("1234");
        when(resultSet.getString(eq("reference"))).thenReturn("234SDF");
        when(resultSet.getDate(eq("date"))).thenReturn(new Date(560705990L));
        when(resultSet.getString(eq("libelle"))).thenReturn("Dummy data");

        // When
        EcritureComptable ecritureComptable = ecritureComptableRM.mapRow(resultSet, 1);

        // Then
        Assert.assertEquals(ecritureComptable.getId(), Integer.valueOf(123));
        Assert.assertEquals(ecritureComptable.getLibelle(), "Dummy data");
    }
}
