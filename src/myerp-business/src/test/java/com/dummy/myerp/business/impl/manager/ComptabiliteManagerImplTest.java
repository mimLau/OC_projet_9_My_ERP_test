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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import static java.sql.Date.valueOf;
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
        ecritureComptable.setId(1);
    }

    @Test
    @Tag("ConstraintViolation")
    public void givenEcritureComptableWithLigneEcritureLessThan2lines_whenCheckEcritureComptableConstraintViolation_thenThrowFunctionalException() throws Exception {

        // GIVEN
        LocalDate localDate = LocalDate.of(2020, 10, 22);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00042");
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, ()-> comptabiliteManagerImpl.checkEcritureComptableUnitViolation(ecritureComptable));

        // THEN
        assertThat(exception.getMessage().equals("L'écriture comptable ne respecte pas les contraintes de validation"));
    }

    @Test
    @Tag("ConstraintViolation")
    public void givenEcritureCompatbleWithoutDate_whencheckEcritureComptableConstraintViolation_thenThrowFunctionalException(){

        // GIVEN
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00042");
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(1243),
                null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, ()-> comptabiliteManagerImpl.checkEcritureComptableUnitViolation(ecritureComptable));

        // THEN
        assertThat(exception.getMessage().equals("L'écriture comptable ne respecte pas les contraintes de validation"));

    }

    @Test
    @Tag("ConstraintViolation")
    public void givenEcritureCompatbleWithBadFormatOfRef_whencheckEcritureComptableConstraintViolation_thenThrowFunctionalException(){

        // GIVEN
        LocalDate localDate = LocalDate.of(2020, 10, 22);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00042000000");
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(1243),
                null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, ()-> comptabiliteManagerImpl.checkEcritureComptableUnitViolation(ecritureComptable));

        // THEN
        assertThat(exception.getMessage().equals("L'écriture comptable ne respecte pas les contraintes de validation"));
    }

    @Test
    @Tag("ConstraintViolation")
    public void givenEcritureCompatbleWithoutJcode_whencheckEcritureComptableConstraintViolation_thenThrowFunctionalException(){

        // GIVEN
        LocalDate localDate = LocalDate.of(2020, 10, 22);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);
        ecritureComptable.setReference("AC-2020/00042");
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(1243),
                null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, ()-> comptabiliteManagerImpl.checkEcritureComptableUnitViolation(ecritureComptable));

        // THEN
        assertThat(exception.getMessage().equals("L'écriture comptable ne respecte pas les contraintes de validation"));
    }

    @Test
    @Tag("ConstraintViolation")
    public void givenEcritureCompatbleWithBadLibelleSize_whencheckEcritureComptableConstraintViolation_thenThrowFunctionalException(){

        // GIVEN
        LocalDate localDate = LocalDate.of(2020, 10, 22);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00042");
        ecritureComptable.setLibelle("");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(1243),
                null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, ()-> comptabiliteManagerImpl.checkEcritureComptableUnitViolation(ecritureComptable));

        // THEN
        assertThat(exception.getMessage().equals("L'écriture comptable ne respecte pas les contraintes de validation"));
    }


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
        LocalDate localDate = LocalDate.of(2020, 10, 22);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00042");

        Mockito.when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
        doReturn(new SequenceEcritureComptable(ecritureComptable.getJournal(), 2020, 40))
                .when(comptabiliteDaoMock).getSeqEcritureComptableByJCodeAndYear(2020, "AC");

        /*Mockito.when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
        when(daoProxyMock.getComptabiliteDao().getSeqEcritureComptableByJCodeAndYear(2020, "AC"))
                .thenReturn(new SequenceEcritureComptable(ecritureComptable.getJournal(), 2020, 40));*/

        // WHEN
        comptabiliteManagerImpl.addReference(ecritureComptable);
        String[] incrementedDerniereVal = ecritureComptable.getReference().split("[-/]");

        // THEN
        verify(daoProxyMock.getComptabiliteDao()).getSeqEcritureComptableByJCodeAndYear(2020, "AC");
        verify(daoProxyMock.getComptabiliteDao(), times(1)).getSeqEcritureComptableByJCodeAndYear(2020, "AC");
        assertThat(incrementedDerniereVal[2]).isEqualTo("00041");
    }

    @Test
    @Tag("RG5")
    public void givenEcritureComptable_whenAddReference_thenReturnNull_and_codeOfEcritureCompatbleReferenceShouldEqualAtOne() throws NotFoundException {

        // GIVEN
        LocalDate localDate = LocalDate.of(2020, 10, 22);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00042");

        Mockito.when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
        doReturn(null).when(comptabiliteDaoMock).getSeqEcritureComptableByJCodeAndYear(2020, "AC");

        // WHEN
        comptabiliteManagerImpl.addReference(ecritureComptable);
        String[] incrementedDerniereVal = ecritureComptable.getReference().split("[-/]");

        // THEN
        verify(daoProxyMock.getComptabiliteDao()).getSeqEcritureComptableByJCodeAndYear(2020, "AC");
        verify(daoProxyMock.getComptabiliteDao(), times(1)).getSeqEcritureComptableByJCodeAndYear(2020, "AC");
        assertThat(incrementedDerniereVal[2]).isEqualTo("00001");
    }


    @Test
    @Tag("RG5")
    public void  givenEcritureComptableWithRefWithCodeDifferentFromJournalCode_whenCheckEcritureComptableUnit_RG5_thenThrowFunctionalException () {

        // GIVEN
        LocalDate localDate = LocalDate.of(2020, 10, 22);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
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
    public void givenEcritureCompatableWithRefWithYearDifferentFromYearInBdd_whenCheckEcritureComptableUnit_RG5_thenThrowFunctionalException () {

        // GIVEN
        LocalDate localDate = LocalDate.of(2020, 10, 22);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);
        ecritureComptable.setReference("AC-2019/00001");
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));

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
    public void givenEcritureComptableWithRefWithBadFormat_whenCheckEcritureComptableUnit_RG5_thenThrowFunctionalException () {

        // GIVEN
        LocalDate localDate = LocalDate.of(2020, 10, 22);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020l-00001");

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG5(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo(String.format(
                "La référence (%s) ne respecte pas le format requis: xx-AAAA/#####.", ecritureComptable.getReference()));
    }

    @Test
    @Tag("RG6")
    public void givenEcritureComptableWhichRefAlreadyExistsInBdd_whenCheckEcritureComptableContext_thenThrowFunctionalException() throws NotFoundException {

        // GIVEN
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00042");

        EcritureComptable returnedEcritureComptable = new EcritureComptable();
        returnedEcritureComptable.setReference("AC-2020/00042");
        returnedEcritureComptable.setId(ecritureComptable.getId() + 1);

        Mockito.when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
        when(daoProxyMock.getComptabiliteDao().getEcritureComptableByRef(ecritureComptable.getReference())).thenReturn(returnedEcritureComptable);

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableContext(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Une autre écriture comptable existe déjà avec la même référence.");
    }
}
