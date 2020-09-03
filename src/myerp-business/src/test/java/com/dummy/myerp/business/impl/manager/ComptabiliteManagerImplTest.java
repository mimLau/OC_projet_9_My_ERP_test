package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.technical.util.DateUtility;
import org.junit.Before;
//import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComptabiliteManagerImplTest {


    private ComptabiliteManagerImpl comptabiliteManagerImpl = new ComptabiliteManagerImpl();

    private static DaoProxy daoProxyMock = mock(DaoProxy.class);
    private static ComptabiliteDao comptabiliteDaoMock = mock(ComptabiliteDao.class);
    private static BusinessProxy businessProxyMock = mock(BusinessProxy.class);
    private static TransactionManager transactionManagerMock = mock(TransactionManager.class);
    EcritureComptable ecritureComptable;


    @BeforeAll
    public static void setUp() {
        AbstractBusinessManager.configure(businessProxyMock, daoProxyMock, transactionManagerMock);
    }

    @BeforeEach
    public void init(){
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date());
    }


    @Test
    public void checkEcritureComptableUnit() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal(123)));
        comptabiliteManagerImpl.checkEcritureComptableUnit(vEcritureComptable);
    }

    /*@Test(expected = FunctionalException.class)  // Erreur car nexiste pas dans junit jupiter test, mais que dans junit test.
    public void checkEcritureComptableUnitViolation() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        comptabiliteManagerImpl.checkEcritureComptableUnit(vEcritureComptable);
    }*/

    @Test
    public void checkEcritureComptableUnitRG2() {

        // Given
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(new CompteComptable(1),null, new BigDecimal(123),null));
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(new CompteComptable(2),null, null, new BigDecimal(1234)));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG2(ecritureComptable));

        // Assert
        assertThat(exception.getMessage()).isEqualTo("L'écriture comptable n'est pas équilibrée.");
    }

    @Test
    public void checkEcritureComptableUnitRG3() {

        // Given
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(new CompteComptable(1),null, new BigDecimal(123),null));
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(new CompteComptable(1),null, new BigDecimal(123),null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG3(ecritureComptable));

        // Assert
        assertThat(exception.getMessage()).isEqualTo("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");

    }


    @Test
    @Tag("RG5")
    public void givenEcritureComptable_whenAddReference_thenCodeOfEcritureCompatbleReferenceShouldEqualAtDerniereValPlusOne() throws NotFoundException {

        // Given
        /*when(daoProxyMock.getComptabiliteDao().getSeqEcritureComptableByJCodeAndYear(2020, "AC"))
                .thenReturn(new SequenceEcritureComptable(ecritureComptable.getJournal(), 2020, 40));*/

        Mockito.when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
        doReturn(new SequenceEcritureComptable(ecritureComptable.getJournal(), 2020, 40))
                .when(comptabiliteDaoMock).getSeqEcritureComptableByJCodeAndYear(2020, "AC");

        // WHEN
        comptabiliteManagerImpl.addReference(ecritureComptable);
        String[] incrementedDerniereVal = ecritureComptable.getReference().split("[-/]");

        // THEN
        verify(daoProxyMock).getComptabiliteDao().getSeqEcritureComptableByJCodeAndYear(2020, "AC");
        verify(daoProxyMock.getComptabiliteDao(), times(2)).getSeqEcritureComptableByJCodeAndYear(2020, "AC");
        assertThat(incrementedDerniereVal[2]).isEqualTo("00041");
    }

    @Test
    @Tag("RG5")
    public void givenEcritureComptable_whenAddReference_thenReturnNull_and_codeOfEcritureCompatbleReferenceShouldEqualAtOne() throws NotFoundException {

        // GIVEN
        Mockito.when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
        doReturn(null).when(comptabiliteDaoMock).getSeqEcritureComptableByJCodeAndYear(2020, "AC");

        // WHEN
        comptabiliteManagerImpl.addReference(ecritureComptable);
        String[] incrementedDerniereVal = ecritureComptable.getReference().split("[-/]");

        // THEN
        verify(comptabiliteDaoMock).getSeqEcritureComptableByJCodeAndYear(2020, "AC");
        verify(comptabiliteDaoMock, times(1)).getSeqEcritureComptableByJCodeAndYear(2020, "AC");
        assertThat(incrementedDerniereVal[2]).isEqualTo("00001");
    }


    @Test
    @Tag("RG5")
    public void checkRG5_shouldThrowFunctionalException_whenRefCode_isDifferentFrom_ecritureCompatbleCode () {

        // GIVEN
        ecritureComptable.setReference("BQ-2020/00001");

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG5(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo(String.format(
                "Le journal code de l'écriture comptable (%s) ne correspond pas à celui de la référence (%s).",
                ecritureComptable.getJournal().getCode(), ecritureComptable.getReference().split("[-/]")[0]));
    }

    @Test
    @Tag("RG5")
    public void checkRG5_shouldThrowFunctionalException_whenYearInRef_isDifferentFrom_ecritureCompatbleYear () {

        // GIVEN
        ecritureComptable.setReference("AC-2019/00001");

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG5(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo(String.format(
                "L'année de l'écriture comptable (%s) est diférente de celle de la référence (%s).",
                DateUtility.convertToCalender(ecritureComptable.getDate()).get(Calendar.YEAR), ecritureComptable.getReference().split("[-/]")[1]));
    }

    @Test
    @Tag("RG5")
    public void checkRG5_shouldThrowFunctionalException_whenRef_doesntMatchRequiredFormat () {

        // GIVEN
        ecritureComptable.setReference("AC-2020l-00001");

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG5(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo(String.format(
                "La référence (%s) ne respecte pas le format requis: xx-AAAA/#####.", ecritureComptable.getReference()));
    }
}
