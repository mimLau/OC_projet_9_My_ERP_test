package com.dummy.myerp.consumer.dao.impl.cache;

import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class CompteComptableDaoCacheTest {
    private DaoProxy daoProxyMock = mock(DaoProxy.class);
    private ComptabiliteDao comptabiliteDaoMock = mock(ComptabiliteDao.class);
    private CompteComptableDaoCache compteComptableDaoCache = new CompteComptableDaoCache();

    @Before
    public void init(){
        ConsumerHelper.configure(daoProxyMock);
    }

    @Test
    public void shouldReturnDataFromDaoProxy() {
        // Given

        CompteComptable compteComptable = new CompteComptable();
        compteComptable.setNumero(512);
        List<CompteComptable> comptesComptable = new ArrayList<>();
        comptesComptable.add(compteComptable);

        Mockito.when(daoProxyMock.getComptabiliteDao())
                .thenReturn(comptabiliteDaoMock);
        when(daoProxyMock.getComptabiliteDao()
                .getListCompteComptable()).thenReturn(comptesComptable);

        // When
        CompteComptable returnedCompteComptable = compteComptableDaoCache.getByNumero(512);

        // Then
        Assertions.assertEquals(returnedCompteComptable, compteComptable);
    }
}
