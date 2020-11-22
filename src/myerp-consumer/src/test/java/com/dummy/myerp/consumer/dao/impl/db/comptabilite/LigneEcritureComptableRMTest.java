package com.dummy.myerp.consumer.dao.impl.db.comptabilite;

import com.dummy.myerp.consumer.dao.impl.cache.CompteComptableDaoCache;
import com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite.LigneEcritureComptableRM;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
public class LigneEcritureComptableRMTest {
    private LigneEcritureComptableRM ligneEcritureComptableRM = new LigneEcritureComptableRM();

    @Test
    public void test_mapRow() throws NoSuchFieldException, SQLException {
        // Given
        CompteComptable compteComptable = new CompteComptable(1234, "Un libellé");
        CompteComptableDaoCache compteComptableDaoCache = mock(CompteComptableDaoCache.class);
        FieldSetter.setField(ligneEcritureComptableRM, ligneEcritureComptableRM.getClass().getDeclaredField("compteComptableDaoCache"), compteComptableDaoCache);
        when(compteComptableDaoCache.getByNumero(any(Integer.class))).thenReturn(compteComptable);

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt(eq("compte_comptable_numero"))).thenReturn(123);
        when(resultSet.getString(eq("libelle"))).thenReturn("Dummy libelle");
        when(resultSet.getBigDecimal(eq("credit"))).thenReturn(BigDecimal.valueOf(134));
        when(resultSet.getBigDecimal(eq("debit"))).thenReturn(BigDecimal.valueOf(432));

        // When
        LigneEcritureComptable ligneEcritureComptable = ligneEcritureComptableRM.mapRow(resultSet, 1);

        // Then
        Assert.assertEquals(ligneEcritureComptable.getCredit(), BigDecimal.valueOf(134));
        Assert.assertEquals(ligneEcritureComptable.getLibelle(), "Dummy libelle");
    }
}
