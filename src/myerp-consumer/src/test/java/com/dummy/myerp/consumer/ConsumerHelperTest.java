package com.dummy.myerp.consumer;

import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ConsumerHelperTest {

    @Test
    public void checkIfDaoProxyIsSet() {
        // Given
        DaoProxy daoProxyMock = mock(DaoProxy.class);

        // When
        ConsumerHelper.configure(daoProxyMock);

        // Then
        Assertions.assertEquals(ConsumerHelper.getDaoProxy(), daoProxyMock);
    }
}
