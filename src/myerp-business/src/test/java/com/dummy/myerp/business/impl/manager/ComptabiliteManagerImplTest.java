package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Date;

import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComptabiliteManagerImplTest {


    private ComptabiliteManagerImpl comptabiliteManagerUnderTest = new ComptabiliteManagerImpl();
    private EcritureComptable ecritureComptable;

    /*@Mock
    DaoProxy daoProxy;*/
    private DaoProxy daoProxy;
    private ComptabiliteDaoImpl comptabiliteDaoImpl;
    private ComptabiliteDao comptabiliteDao;
    private BusinessProxy businessProxy;
    private TransactionManager transactionManager;


    @Before
    public void init() {
        daoProxy = Mockito.mock(DaoProxy.class);
        comptabiliteDaoImpl = Mockito.mock(ComptabiliteDaoImpl.class);
        ConsumerHelper.configure(daoProxy);
        comptabiliteDao = Mockito.mock(ComptabiliteDao.class);
        daoProxy.setComptabiliteDao(comptabiliteDao);
        businessProxy = Mockito.mock(BusinessProxy.class);
        transactionManager = Mockito.mock(TransactionManager.class);
        AbstractBusinessManager.configure(businessProxy, daoProxy, transactionManager);
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
        comptabiliteManagerUnderTest.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        comptabiliteManagerUnderTest.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2() throws Exception {
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
                                                                                 new BigDecimal(1234)));
        comptabiliteManagerUnderTest.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                                                                                 null, new BigDecimal(123),
                                                                                 null));
        comptabiliteManagerUnderTest.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void givenEcritureComptable_whenAddReference_thenCodeOfEcritureCompatbleReferenceShouldEqualAtDerniereValPlusOne() throws NotFoundException {

        // GIVEN
        ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        ecritureComptable.setDate(new Date(2016, 05, 4));

        Mockito.when(daoProxy.getComptabiliteDao()).thenReturn(comptabiliteDao);
        /*when(daoProxy.getComptabiliteDao().getSeqEcritureComptableByJCodeAndYear(2016, "AC"))
                .thenReturn(new SequenceEcritureComptable(ecritureComptable.getJournal(), 2016, 40));*/
        doReturn(new SequenceEcritureComptable(ecritureComptable.getJournal(), 2016, 40))
                .when(comptabiliteDao).getSeqEcritureComptableByJCodeAndYear(2016, "AC");

        // WHEN
        comptabiliteManagerUnderTest.addReference(ecritureComptable);
        String[] incrementedDerniereVal = ecritureComptable.getReference().split("[-/]");
        //Est ce que c'est possible de récupérer la sequenceEcritureComptable?

        // THEN
        verify(daoProxy.getComptabiliteDao().getSeqEcritureComptableByJCodeAndYear(2016, "AC"));
        verify(daoProxy.getComptabiliteDao(), times(2));
        assertThat(incrementedDerniereVal[2]).isEqualTo("00041");

    }

    @Test
    public void givenEcritureComptable_whenAddReference_thenReturnNull_and_codeOfEcritureCompatbleReferenceShouldEqualAtOne() throws NotFoundException {

        // GIVEN
        when(daoProxy.getComptabiliteDao().getSeqEcritureComptableByJCodeAndYear(2016, "AC"))
                .thenReturn(null);

        // WHEN
        comptabiliteManagerUnderTest.addReference(ecritureComptable);
        String[] incrementedDerniereVal = ecritureComptable.getReference().split("[-/]");


        // THEN
        verify(daoProxy.getComptabiliteDao().getSeqEcritureComptableByJCodeAndYear(2016, "AC"));
        verify(daoProxy.getComptabiliteDao(), times(2));
        assertThat(incrementedDerniereVal[2]).isEqualTo("00001");

    }

}
