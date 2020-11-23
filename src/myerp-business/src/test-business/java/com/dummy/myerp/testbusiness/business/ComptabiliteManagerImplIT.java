package com.dummy.myerp.testbusiness.business;


import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static java.sql.Date.valueOf;
import static org.assertj.core.api.Assertions.assertThat;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/com/dummy/myerp/business/applicationContext.xml")

@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:/truncate_DB_IT.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:/data.sql")})

public class ComptabiliteManagerImplIT extends BusinessTestCase {

    private ComptabiliteManagerImpl comptabiliteManager = new ComptabiliteManagerImpl();
    EcritureComptable ecritureComptable;


    @BeforeEach
    public void init() {

       // truncateTables();
        //insertData();

        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00001");
        ecritureComptable.setLibelle("Achat de logiciel");
        ecritureComptable.setDate(new Date());
        ecritureComptable.getListLigneEcriture().add(this.createLigne(606, "105", "250"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(606, "1500", "2500"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(512, null, "150"));
        ecritureComptable.getListLigneEcriture().add(this.createLigne(401, "1295", null));
    }

    @Test
    @Order(1)
    public void checkListEcritureComptable_whenGetListEcritureComptable() {

        // GIVEN
        List<EcritureComptable> ecritureComptableList = comptabiliteManager.getListEcritureComptable();

        // WHEN
        int size = ecritureComptableList.size();

        // THEN
        assertThat(size).isEqualTo(5);
    }

    @Test
    public void checkListCompteComptable_whenGetListCompteComptable() {

        // GIVEN
        List<CompteComptable> compteComptableList = comptabiliteManager.getListCompteComptable();

        // WHEN
        int size = compteComptableList.size();

        // THEN
        assertThat(size).isEqualTo(7);
    }

    @Test
    public void checkListJournalComptable_whenGetListJournalComptable() {

        // GIVEN
        List<JournalComptable> journalComptableList = comptabiliteManager.getListJournalComptable();

        // WHEN
        int size = journalComptableList.size();

        // THEN
        assertThat(size).isEqualTo(4);
    }

    @Test
    @Order(2)
    public void checkInsertEcritureComptable_whenInsertEcritureComptable() throws FunctionalException {

        // GIVEN ecritureComptable in init()

        // WHEN
        comptabiliteManager.insertEcritureComptable(ecritureComptable);
        String ref = comptabiliteManager.getLastEcritureComptable().getReference();
        // THEN
        assertThat(ref).isEqualTo(ecritureComptable.getReference());
    }

    /*@Test
    public void checkInsertEcritureComptable_whenInsertEcritureComptableAlreadyExists_thenThrowFunctionnalException() {

        // GIVEN ecritureComptable in init()

        // WHEN
        Throwable t = Assertions.assertThrows(FunctionalException.class, () -> {
            comptabiliteManager.insertEcritureComptable(ecritureComptable);
        });

        // THEN
        Assertions.assertTrue(t.getMessage().equals("Une autre écriture comptable existe déjà avec la même référence."));
    }*/

    @Test
    @Order(4)
    public void checkUpdateEcritureComptable() throws FunctionalException, NotFoundException {

        // GIVEN
        EcritureComptable ecritureComptableFromDB = comptabiliteManager.getEcritureComptableById(-3);
        String oldLibelle = ecritureComptableFromDB.getLibelle();
        ecritureComptableFromDB.setLibelle("New Libelle");

        // WHEN
        comptabiliteManager.updateEcritureComptable(ecritureComptableFromDB);
        ecritureComptableFromDB = comptabiliteManager.getEcritureComptableById(-3);

        // THEN
        assertThat(oldLibelle).isNotEqualTo("New Libelle");
        assertThat(ecritureComptableFromDB.getLibelle()).isEqualTo("New Libelle");

    }

    @Test
    @Order(4)
    public void checkDeleteEcritureComptable() {

        // GIVEN ecritureComptable in init()

        // WHEN
        comptabiliteManager.deleteEcritureComptable(-3);
        Throwable t = Assertions.assertThrows(NotFoundException.class, () -> {
            comptabiliteManager.getEcritureComptableById(-3);
        });

        // THEN
        Assertions.assertEquals("EcritureComptable non trouvée : id= -3", t.getMessage());

    }

    @Test
    public void givenFirstEcritureComptableOfYear_whenAddReference_shouldCreateaRefWithDerniereValeurAtOne() throws FunctionalException {

        // GIVEN
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("BQ", "Banque"));
        ecritureComptable.setLibelle("Remboursement credit");
        ecritureComptable.setDate(new Date());

        // WHEN
        comptabiliteManager.addReference(ecritureComptable);
        String ref = ecritureComptable.getReference();

        // THEN
        assertThat(ref).isEqualTo("BQ-2020/00001");

    }

    @Test
    public void givenFirstEcritureComptableOfYear_whenAddReference_shouldCreateaRefWithIncrementedDerniereValeur() throws FunctionalException {

        // GIVEN
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("BQ", "Banque"));
        ecritureComptable.setLibelle("Remboursement credit");
        LocalDate localDate = LocalDate.of(2016,10,01);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);

        // WHEN
        comptabiliteManager.addReference(ecritureComptable);
        String ref = ecritureComptable.getReference();

        // THEN
        assertThat(ref).isEqualTo("BQ-2016/00052");

    }

    @Test
    public void checkGetEcritureComtableByRef() {

        // GIVEN ecritureComptable in init()

        // WHEN
        ecritureComptable = comptabiliteManager.getEcritureComptableByRef("BQ-2016/00005");

        // THEN
        assertThat(ecritureComptable.getReference()).isEqualTo("BQ-2016/00005");

    }

    @Test
    public void checkGetEcritureComptableById() throws NotFoundException {

        // GIVEN ecritureComptable in init()

        // WHEN
        ecritureComptable = comptabiliteManager.getEcritureComptableById(-2);

        // THEN
        assertThat(ecritureComptable.getLibelle()).isEqualTo("TMA Appli Xxx");

    }


    @Test
    public void checkgetSequenceEcritureComptable() {

        // GIVEN
        Integer year = 2016;
        String journalCode = "OD";

        // WHEN
        SequenceEcritureComptable sequenceEcritureComptable = comptabiliteManager.getSequenceEcritureComptable(year, journalCode);

        // THEN
        assertThat(sequenceEcritureComptable.getDerniereValeur()).isEqualTo(88);
    }

    @Test
    public void checkInsertSequenceEcritureComptable() {

        // GIVEN
        JournalComptable journalComptable = new JournalComptable("VE", "Vente");
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable(journalComptable, 2020, 1 );
        Integer dernierVal = sequenceEcritureComptable.getDerniereValeur();
        SequenceEcritureComptable sequenceEcritureComptableFromDB;

        // WHEN
        comptabiliteManager.createNewSequenceEcritureComptrable(sequenceEcritureComptable);
        sequenceEcritureComptableFromDB = comptabiliteManager.getSequenceEcritureComptable(2020, "VE");


        // THEN
        assertThat(dernierVal).isEqualTo(sequenceEcritureComptableFromDB.getDerniereValeur());

    }

    @Test
    public void checkUpdateSequenceEcritureComptable() {

         // GIVEN
         SequenceEcritureComptable sequenceEcritureComptable =  comptabiliteManager.getSequenceEcritureComptable(2016, "VE");
         Integer oldDerniereVal = sequenceEcritureComptable.getDerniereValeur();
         sequenceEcritureComptable.setDerniereValeur(100);
         SequenceEcritureComptable sequenceEcritureComptableFromDB;

         // WHEN
         comptabiliteManager.updateSequenceEcritureComptable(sequenceEcritureComptable);
         sequenceEcritureComptableFromDB = comptabiliteManager.getSequenceEcritureComptable(2016, "VE");

         // THEN
         assertThat(oldDerniereVal).isNotEqualTo(sequenceEcritureComptableFromDB.getDerniereValeur());
         assertThat(sequenceEcritureComptableFromDB.getDerniereValeur()).isEqualTo(100);

     }



    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        return new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                vLibelle,
                vDebit, vCredit);
    }

}
