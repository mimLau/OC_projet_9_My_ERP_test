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

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.sql.Date.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.then;
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
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00042");
        ecritureComptable.setLibelle("Libelle");

        LocalDate localDate = LocalDate.of(2020, 10, 22);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);
    }

    @Test
    @Tag("ConstraintViolation")
    @DisplayName("CheckEcritureComptableUnitViolation should throw FunctionalException when lineEcriture less than 2.")
    public void givenEcritureComptableWithLigneEcritureLessThan2lines_whenCheckEcritureComptableConstraints_thenThrowFunctionalException() throws Exception {

        // GIVEN
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, ()-> comptabiliteManagerImpl.checkEcritureComptableUnitConstraints(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("L'écriture comptable ne respecte pas les contraintes de validation : L'écriture comptable doit avoir au minimum 2 lignes d'écriture comptable: 1 au débit, 1 au crédit.");
    }

    @Test
    @Tag("ConstraintViolation")
    @DisplayName("CheckEcritureComptableUnitViolation should throw FunctionalException when Date is null.")
    public void givenEcritureCompatbleWithoutDate_whencheckEcritureComptableConstraints_thenThrowFunctionalException(){

        // GIVEN
        ecritureComptable.setDate(null);
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(1243),
                null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, ()-> comptabiliteManagerImpl.checkEcritureComptableUnitConstraints(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("L'écriture comptable ne respecte pas les contraintes de validation : La date ne doit pas être nulle.");

    }

    @Test
    @Tag("ConstraintViolation")
    @DisplayName("CheckEcritureComptableUnitViolation should throw FunctionalException when Reference format is bad")
    public void givenEcritureCompatbleWithBadFormatOfRef_whencheckEcritureComptableConstraints_thenThrowFunctionalException(){

        // GIVEN
        ecritureComptable.setReference("QS-12234-2345455");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(1243),
                null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, ()-> comptabiliteManagerImpl.checkEcritureComptableUnitConstraints(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("L'écriture comptable ne respecte pas les contraintes de validation : La référence ne respecte pas le format requis.");
    }

    @Test
    @Tag("ConstraintViolation")
    @DisplayName("CheckEcritureComptableUnitViolation should throw FunctionalException when Journal code is null")
    public void givenEcritureCompatbleWithoutJcode_whencheckEcritureComptableConstraints_thenThrowFunctionalException(){

        // GIVEN
        ecritureComptable.setJournal(null);
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),null, new BigDecimal(123),null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),null, new BigDecimal(1243),null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, ()-> comptabiliteManagerImpl.checkEcritureComptableUnitConstraints(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("L'écriture comptable ne respecte pas les contraintes de validation : Le journal comptable ne doit pas être nul.");
    }

    @Test
    @Tag("ConstraintViolation")
    @DisplayName("CheckEcritureComptableUnitViolation should throw FunctionalException when libelle is null")
    public void givenEcritureCompatbleWithBadLibelleSize_whencheckEcritureComptableConstraints_thenThrowFunctionalException(){

        // GIVEN
        ecritureComptable.setLibelle("");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(1243),
                null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, ()-> comptabiliteManagerImpl.checkEcritureComptableUnitConstraints(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("L'écriture comptable ne respecte pas les contraintes de validation : Le libellé doit comporter entre 1 et 200 caractères.");
    }

    @Test
    @DisplayName("CheckEcritureComptableUnitDebitOrCredit should throw a fucntionnal exception if ecriture comptable has a line ecriture with a debit and a credit ")
    void givenEcritureComptableWithLignesEcriture_whenCheckEcritureComptableUnitDebitOrCredit_thenThrowFunctionalException() {

        // GIVEN
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(584), new BigDecimal(100)));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal(505)));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(3), null, new BigDecimal(123), null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnitDebitOrCredit(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Une écriture comptable ne doit pas avoir un montant au débit et un montant au crédit.");
    }

    @Test
    @Tag("RG1")
    @DisplayName("Given Compte comptable Without List of line ecriture,when CheckEcritureComptableUnitRG1 then throw notFoundException")
    public void givenCompteComptableWithListOfLigneEcritureNull_whenCheckEcritureComptableUnitRG1_thenThrowNotFoundException() throws NotFoundException {

        // Given
        CompteComptable compteComptable = new CompteComptable();
        compteComptable.setNumero(512);

        Mockito.when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
        when(daoProxyMock.getComptabiliteDao().getListLigneEcritureComptableByCompteNumber(compteComptable.getNumero())).thenReturn(null);

        // WHEN
        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG1(compteComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("There is any ligne ecriture for the accounting account number : " + compteComptable.getNumero());
    }

    @Test
    @Tag("RG1")
    @DisplayName("Given Compte comptable With List of line ecriture which subtract of total debit with totql credit is < 0,when CheckEcritureComptableUnitRG1 then throw FunctionnalException")
    public void givenCompteComptableWithListOfLigneEcritureWithNegativeBalance_whenCheckEcritureComptableUnitRG1_thenThrowFunctionalException() throws NotFoundException {

        // Given
        CompteComptable compteComptable = new CompteComptable();
        compteComptable.setNumero(512);
        List<LigneEcritureComptable> ligneEcritureComptableList = new ArrayList<>();
        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable();
        LigneEcritureComptable ligneEcritureComptable2 = new LigneEcritureComptable();
        LigneEcritureComptable ligneEcritureComptable3 = new LigneEcritureComptable();
        ligneEcritureComptable1.setCredit(new BigDecimal(300));
        ligneEcritureComptable1.setCompteComptable(compteComptable);
        ligneEcritureComptable2.setCredit(new BigDecimal(400));
        ligneEcritureComptable2.setCompteComptable(compteComptable);
        ligneEcritureComptable3.setDebit(new BigDecimal(250));
        ligneEcritureComptable3.setCompteComptable(compteComptable);

        ligneEcritureComptableList.add(ligneEcritureComptable1);
        ligneEcritureComptableList.add(ligneEcritureComptable2);
        ligneEcritureComptableList.add(ligneEcritureComptable3);

        Mockito.when(daoProxyMock.getComptabiliteDao())
                .thenReturn(comptabiliteDaoMock);
        when(daoProxyMock.getComptabiliteDao()
                .getListLigneEcritureComptableByCompteNumber(compteComptable.getNumero())).thenReturn(ligneEcritureComptableList);

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, ()
                        -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG1(compteComptable));

        // THEN
        assertThat(exception.getMessage())
                .isEqualTo(String.format("Le compte comptable numéro %s est créditeur.", compteComptable.getNumero()));
    }

    @Test
    @Tag("RG1")
    @DisplayName("Given Compte comptable With List of line ecriture which subtract of total debit with total credit is > 0,when CheckEcritureComptableUnitRG1 then throw FunctionnalException")
    public void givenCompteComptableWithListOfLigneEcritureWithPositiveBalance_whenCheckEcritureComptableUnitRG1_thenThrowFunctionalException() throws NotFoundException, FunctionalException {

        // Given
        CompteComptable compteComptable = new CompteComptable();
        compteComptable.setNumero(512);
        List<LigneEcritureComptable> ligneEcritureComptableList = new ArrayList<>();
        LigneEcritureComptable ligneEcritureComptable1 = new LigneEcritureComptable();
        LigneEcritureComptable ligneEcritureComptable2 = new LigneEcritureComptable();
        LigneEcritureComptable ligneEcritureComptable3 = new LigneEcritureComptable();
        ligneEcritureComptable1.setCredit(new BigDecimal(300));
        ligneEcritureComptable1.setCompteComptable(compteComptable);
        ligneEcritureComptable2.setCredit(new BigDecimal(400));
        ligneEcritureComptable2.setCompteComptable(compteComptable);
        ligneEcritureComptable3.setDebit(new BigDecimal(950));
        ligneEcritureComptable3.setCompteComptable(compteComptable);

        ligneEcritureComptableList.add(ligneEcritureComptable1);
        ligneEcritureComptableList.add(ligneEcritureComptable2);
        ligneEcritureComptableList.add(ligneEcritureComptable3);

        Mockito.when(daoProxyMock.getComptabiliteDao())
                .thenReturn(comptabiliteDaoMock);
        when(daoProxyMock.getComptabiliteDao()
                .getListLigneEcritureComptableByCompteNumber(compteComptable.getNumero())).thenReturn(ligneEcritureComptableList);

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, ()
                        -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG1(compteComptable));

        // THEN
        assertThat(exception.getMessage())
                .isEqualTo(String.format("Le compte comptable numéro %s est débiteur.", compteComptable.getNumero()));
    }

    @Test
    @Tag("RG2")
    @DisplayName("CheckEcritureComptableUnit_RG2 should throw FunctionalException when ecriture comptable isn't equilibre.")
    public void givenEcritureComptableWithNotEquilibreLinesEcriture_whenCheckEcritureComptableUnitRG2_thenThrowFunctionalException() {

        // Given
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(new CompteComptable(1),null, new BigDecimal(123),null));
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(new CompteComptable(2),null, null, new BigDecimal(1234)));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG2(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("L'écriture comptable n'est pas équilibrée.");
    }



    @Test
    @Tag("RG3")
    @DisplayName("CheckEcritureComptableUnit_RG3 should throw FunctionalException when ecriture comptable hasn't at least one line ecriture with debit and one with a credit")
    public void givenEcritureComptableWithLinesEcritureWithoutAnyDebit_whenCheckEcritureComptableUnitRG3_thenThrowFunctionalException() {

        // Given
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(new CompteComptable(1),null, new BigDecimal(123),null));
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(new CompteComptable(1),null, new BigDecimal(123),null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG3(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
    }

    @Test
    @Tag("RG3")
    @DisplayName("CheckEcritureComptableUnit_RG3 should throw FunctionalException when ecriture comptable hasn't at least one line ecriture with debit and one with a credit")
    public void givenEcritureComptableWithLinesEcritureWithoutAnyCredit_whenCheckEcritureComptableUnitRG3_thenThrowFunctionalException() {

        // Given
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(new CompteComptable(1),null, null, new BigDecimal(123)));
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(new CompteComptable(1),null, null ,new BigDecimal(123)));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG3(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
    }

    @Test
    @Tag("RG3")
    @DisplayName("CheckEcritureComptableUnit_RG3 should throw FunctionalException when ecriture comptable hasn't at least one line ecriture with debit and one with a credit")
    public void givenEcritureComptableWith1LineEcritureWithOneCreditAndOneDebit_whenCheckEcritureComptableUnitRG3_thenThrowFunctionalException() {

        // Given
        ecritureComptable.getListLigneEcriture().add(
                new LigneEcritureComptable(new CompteComptable(1),null, new BigDecimal(123), new BigDecimal(123)));


        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG3(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
    }

    @Test
    @Tag("RG5")
    @DisplayName("Given ecriture comptable at addReference(), if ecriture comptable exists in bdd then added reference should be equal at the derniere value plus one")
    public void givenEcritureComptable_whenAddReference_thenCodeOfEcritureCompatbleReferenceShouldEqualAtDerniereValPlusOne() throws NotFoundException {

        // Given
        Mockito.when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
        doReturn(new SequenceEcritureComptable(ecritureComptable.getJournal(), 2020, 40))
                .when(comptabiliteDaoMock).getSeqEcritureComptableByJCodeAndYear(2020, "AC");

        // WHEN
        comptabiliteManagerImpl.addReference(ecritureComptable);
        String[] incrementedDerniereVal = ecritureComptable.getReference().split("[-/]");

        // THEN
        //verify(daoProxyMock.getComptabiliteDao()).getSeqEcritureComptableByJCodeAndYear(2020, "AC");
        verify(daoProxyMock.getComptabiliteDao(), times(1)).getSeqEcritureComptableByJCodeAndYear(2020, "AC");
        assertThat(incrementedDerniereVal[2]).isEqualTo("00041");
    }

    @Test
    @Tag("RG5")
    @DisplayName("Given ecriture comptable at addReference(), if ecriture comptable doesn't exist in bdd then added reference should be equal at one")
    public void givenEcritureComptable_whenAddReference_thenReturnNull_and_codeOfEcritureCompatbleReferenceShouldEqualAtOne() throws NotFoundException {

        // GIVEN
        Mockito.when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
        when(daoProxyMock.getComptabiliteDao().getSeqEcritureComptableByJCodeAndYear(2020, "AC")).thenReturn(null);

        // WHEN
        comptabiliteManagerImpl.addReference(ecritureComptable);
        String[] incrementedDerniereVal = ecritureComptable.getReference().split("[-/]");

        // THEN
        //verify(daoProxyMock.getComptabiliteDao()).getSeqEcritureComptableByJCodeAndYear(2020, "AC");
        verify(daoProxyMock.getComptabiliteDao(), times(1)).getSeqEcritureComptableByJCodeAndYear(2020, "AC");
        assertThat(incrementedDerniereVal[2]).isEqualTo("00001");
        then(daoProxyMock.getComptabiliteDao()).should(atLeast(1)).getSeqEcritureComptableByJCodeAndYear(2020, "AC");
    }


    @Test
    @Tag("RG5")
    public void  givenEcritureComptableWithRefWithCodeDifferentFromJournalCode_whenCheckEcritureComptableUnit_RG5_thenThrowFunctionalException () {

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
    public void givenEcritureCompatableWithRefWithYearDifferentFromYearInBdd_whenCheckEcritureComptableUnit_RG5_thenThrowFunctionalException () {

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
    public void givenEcritureComptableWithRefWithBadFormat_whenCheckEcritureComptableUnit_RG5_thenThrowFunctionalException () {

        // GIVEN
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
        EcritureComptable returnedEcritureComptable = new EcritureComptable();
        returnedEcritureComptable.setReference("AC-2020/00042");
        returnedEcritureComptable.setId(ecritureComptable.getId() + 1);

        Mockito.when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
        when(daoProxyMock.getComptabiliteDao().getEcritureComptableByRef(ecritureComptable.getReference())).thenReturn(returnedEcritureComptable);

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG6(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Une autre écriture comptable existe déjà avec la même référence.");
    }

    @Test
    @Tag("RG7")
    @DisplayName("Given Compte comptable Without List of line ecriture,when CheckEcritureComptableUnitRG1 then throw notFoundException")
    public void givenCompteComptable_checkEcritureComptableUnit_RG7_thenThrowFunctionnalException() {

        // GIVEN
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), "Lib", new BigDecimal("100.255"), null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), "Lib", null, new BigDecimal("250.7")));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(3), "Lib", new BigDecimal(123), null));

        // WHEN
        FunctionalException exception = assertThrows(
                FunctionalException.class, () -> comptabiliteManagerImpl.checkEcritureComptableUnit_RG7(ecritureComptable));

        // THEN
        assertThat(exception.getMessage()).isEqualTo("Le montant des lignes écriture doit comporter au maximum 2 chiffres.");
    }

    @Test
    public void givenEcritureComptable_whenCheckEcritureComptable() throws NotFoundException, FunctionalException {

        // GIVEN
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), "Lib1", new BigDecimal("100.25"), null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), "Lib2", null, new BigDecimal("100.25")));
        Mockito.when(daoProxyMock.getComptabiliteDao()).thenReturn(comptabiliteDaoMock);
        when(daoProxyMock.getComptabiliteDao().getEcritureComptableByRef(ecritureComptable.getReference())).thenReturn(ecritureComptable);

        // WHEN
        comptabiliteManagerImpl.checkEcritureComptable(ecritureComptable);

        // THEN
    }
}
