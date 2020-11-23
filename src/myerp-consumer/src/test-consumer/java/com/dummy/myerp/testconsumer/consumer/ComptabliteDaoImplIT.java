package com.dummy.myerp.testconsumer.consumer;

import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.consumer.db.AbstractDbConsumer;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/applicationContextIT.xml")

/*@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/truncate_DB_IT.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/data.sql")})*/

public class ComptabliteDaoImplIT extends AbstractDbConsumer {


    private static ComptabiliteDaoImpl comptabiliteDaoImpl;
    EcritureComptable ecritureComptable;

    @BeforeAll
    public static void testSetupBeforeAll() {
        comptabiliteDaoImpl = ComptabiliteDaoImpl.getInstance();
    }

    @BeforeEach
    public void init() {
        ecritureComptable = new EcritureComptable();
    }

    @Test
    @Order(1)
    public void checkListEcritureComptable_whenGetListEcritureComptable() {

        // GIVEN
        List<EcritureComptable> ecritureComptableList = comptabiliteDaoImpl.getListEcritureComptable();

        // WHEN
        int size = ecritureComptableList.size();

        // THEN
        assertThat(size).isEqualTo(5);
    }

    @Test
    public void checkListCompteComptable_whenGetListCompteComptable() {

        // GIVEN
        List<CompteComptable> compteComptableList = comptabiliteDaoImpl.getListCompteComptable();

        // WHEN
        int size = compteComptableList.size();

        // THEN
        assertThat(size).isEqualTo(7);
    }

    @Test
    public void checkListJournalComptable_whenGetListJournalComptable() {

        // GIVEN
        List<JournalComptable> journalComptableList = comptabiliteDaoImpl.getListJournalComptable();

        // WHEN
        int size = journalComptableList.size();

        // THEN
        assertThat(size).isEqualTo(4);
    }


    @Test
    public void checkDeleteEcritureComptable() {

        // GIVEN ecritureComptable in init()

        // WHEN
        comptabiliteDaoImpl.deleteEcritureComptable(-3);
        Throwable t = Assertions.assertThrows(NotFoundException.class, () -> {
            comptabiliteDaoImpl.getEcritureComptableById(-3);
        });

        // THEN
        Assertions.assertTrue(t.getMessage().equals("EcritureComptable non trouvée : id= -3"));

    }

    @Test
    public void checkGetEcritureComtableByRef() throws NotFoundException {

        // GIVEN ecritureComptable in init()

        // WHEN
        ecritureComptable = comptabiliteDaoImpl.getEcritureComptableByRef("BQ-2016/00005");

        // THEN
        assertThat(ecritureComptable.getReference()).isEqualTo("BQ-2016/00005");

    }

    @Test
    public void checkGetEcritureComptableById() throws NotFoundException {

        // GIVEN ecritureComptable in init()

        // WHEN
        ecritureComptable = comptabiliteDaoImpl.getEcritureComptableById(-2);

        // THEN
        assertThat(ecritureComptable.getLibelle()).isEqualTo("TMA Appli Xxx");

    }

    @Test
    public void checkInsertEcritureComptable() throws NotFoundException {

        // GIVEN
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("BB-2020/00001");
        ecritureComptable.setLibelle("Test");
        ecritureComptable.setDate(new Date());

        EcritureComptable ecritureComptableFromDB;

        // WHEN
        comptabiliteDaoImpl.insertEcritureComptable(ecritureComptable);
        ecritureComptableFromDB = comptabiliteDaoImpl.getEcritureComptableByRef("BB-2020/00001");


        // THEN
        assertThat("BB-2020/00001").isEqualTo(ecritureComptableFromDB.getReference());

    }

    @Test
    public void checkgetSequenceEcritureComptable() throws NotFoundException {

        // GIVEN
        Integer year = 2016;
        String journalCode = "OD";

        // WHEN
        SequenceEcritureComptable sequenceEcritureComptable = comptabiliteDaoImpl.getSeqEcritureComptableByJCodeAndYear(year, journalCode);

        // THEN
        assertThat(sequenceEcritureComptable.getDerniereValeur()).isEqualTo(88);
    }

    @Test
    public void checkInsertSequenceEcritureComptable() throws NotFoundException {

        // GIVEN
        JournalComptable journalComptable = new JournalComptable("VE", "Vente");
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable(journalComptable, 2020, 1 );
        Integer dernierVal = sequenceEcritureComptable.getDerniereValeur();
        SequenceEcritureComptable sequenceEcritureComptableFromDB;

        // WHEN
        comptabiliteDaoImpl.insertSequenceEcritureComptable(sequenceEcritureComptable);
        sequenceEcritureComptableFromDB = comptabiliteDaoImpl.getSeqEcritureComptableByJCodeAndYear(2020, "VE");


        // THEN
        assertThat(dernierVal).isEqualTo(sequenceEcritureComptableFromDB.getDerniereValeur());

    }

    @Test
    public void checkUpdateSequenceEcritureComptable() throws NotFoundException {

        // GIVEN
        SequenceEcritureComptable sequenceEcritureComptable =  comptabiliteDaoImpl.getSeqEcritureComptableByJCodeAndYear(2016, "VE");
        Integer oldDerniereVal = sequenceEcritureComptable.getDerniereValeur();
        sequenceEcritureComptable.setDerniereValeur(100);
        SequenceEcritureComptable sequenceEcritureComptableFromDB;

        // WHEN
        comptabiliteDaoImpl.updateSequenceEcritureComptable(sequenceEcritureComptable);
        sequenceEcritureComptableFromDB = comptabiliteDaoImpl.getSeqEcritureComptableByJCodeAndYear(2016, "VE");

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
