package com.dummy.myerp.consumer.dao.impl;

import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class DaoProxyImplTest {
    @Test
    public void test_setter() {
        // Given
        ComptabiliteDao comptabiliteDao = mock(ComptabiliteDao.class);

        // When
        DaoProxyImpl daoProxy = DaoProxyImpl.getInstance();
        daoProxy.setComptabiliteDao(comptabiliteDao);

        // Then
        Assert.assertNotNull(daoProxy.getComptabiliteDao());
        Assert.assertTrue(daoProxy.getComptabiliteDao().equals(comptabiliteDao));
    }
}
