package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;


public class EcritureComptableTest {

    private EcritureComptable vEcritureUnderTest;

    @BeforeEach
    public void initEcitureComptable() {
        vEcritureUnderTest = new EcritureComptable();
    }


    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                                     .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                                                                    vLibelle,
                                                                    vDebit, vCredit);
        return vRetour;
    }

    @Test
    public void shouldReturnTrue_when_totalDebit_isEqualTo_totalCredit() {

        // Arrange
        vEcritureUnderTest.getListLigneEcriture().clear();
        vEcritureUnderTest.setLibelle("Equilibrée");
        vEcritureUnderTest.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcritureUnderTest.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEcritureUnderTest.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcritureUnderTest.getListLigneEcriture().add(this.createLigne(2, "40", "7"));

        // Act
        Boolean isEquilibree = vEcritureUnderTest.isEquilibree();

        // Assert
        assertThat(isEquilibree).isEqualTo(true);
    }

    @Test
    public void isntEquilibree() {

        vEcritureUnderTest.getListLigneEcriture().clear();
        vEcritureUnderTest.setLibelle("Non équilibrée");
        vEcritureUnderTest.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcritureUnderTest.getListLigneEcriture().add(this.createLigne(1, "20", "1"));
        vEcritureUnderTest.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEcritureUnderTest.getListLigneEcriture().add(this.createLigne(2, "1", "2"));
        Assert.assertFalse(vEcritureUnderTest.toString(), vEcritureUnderTest.isEquilibree());
    }




}
