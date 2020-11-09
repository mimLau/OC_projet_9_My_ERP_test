package com.dummy.myerp.testbusiness.business;


import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComptabiliteManagerImplIT extends BusinessTestCase {

    private ComptabiliteManagerImpl comptabiliteManager = new ComptabiliteManagerImpl();
    EcritureComptable ecritureComptable;


    @BeforeEach
    public void init() {

        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setReference("AC-2020/00003");
        ecritureComptable.setLibelle("Achat de fournitures");
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

    @Test
    public void checkInsertEcritureComptable_whenInsertEcritureComptableAlreadyExists_thenThrowFunctionnalException() throws FunctionalException {

        // GIVEN ecritureComptable in init()

        // WHEN
        Throwable t = Assertions.assertThrows(FunctionalException.class, () -> {
            comptabiliteManager.insertEcritureComptable(ecritureComptable);
        });

        // THEN
        Assertions.assertTrue(t.getMessage().equals("Une autre écriture comptable existe déjà avec la même référence."));
    }

    @Test
    public void checkUpdateEcritureComptable() {

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
