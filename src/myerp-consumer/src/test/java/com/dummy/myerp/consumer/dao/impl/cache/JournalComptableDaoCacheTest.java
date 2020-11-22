package com.dummy.myerp.consumer.dao.impl.cache;

import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JournalComptableDaoCacheTest {
    private DaoProxy daoProxyMock = mock(DaoProxy.class);
    private ComptabiliteDao comptabiliteDaoMock = mock(ComptabiliteDao.class);
    private JournalComptableDaoCache journalComptableDaoCache = new JournalComptableDaoCache();

    @Before
    public void init(){
        ConsumerHelper.configure(daoProxyMock);
    }

    @Test
    public void shouldReturnDataFromDaoProxy() {
        // Given
        JournalComptable journalComptable = new JournalComptable();
        journalComptable.setCode("ABCD");
        journalComptable.setLibelle("Libellé de test");
        List<JournalComptable> journauxComptable = new ArrayList<>();
        journauxComptable.add(journalComptable);

        Mockito.when(daoProxyMock.getComptabiliteDao())
                .thenReturn(comptabiliteDaoMock);
        when(daoProxyMock.getComptabiliteDao()
                .getListJournalComptable()).thenReturn(journauxComptable);

        // When
        JournalComptable returnedJournalComptable = journalComptableDaoCache.getByCode("ABCD");

        // Then
        Assertions.assertEquals(returnedJournalComptable, journalComptable);
    }
}
