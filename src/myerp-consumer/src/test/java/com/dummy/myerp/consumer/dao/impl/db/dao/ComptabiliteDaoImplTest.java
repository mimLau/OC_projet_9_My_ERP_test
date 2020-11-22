package com.dummy.myerp.consumer.dao.impl.db.dao;

import com.dummy.myerp.consumer.db.AbstractDbConsumer;
import com.dummy.myerp.consumer.db.DataSourcesEnum;
import com.dummy.myerp.model.bean.comptabilite.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "jdk.internal.reflect.*", "java.xml.*"})
@PrepareForTest({ComptabiliteDaoImpl.class, AbstractDbConsumer.class})
public class ComptabiliteDaoImplTest {
    @BeforeClass
    public static void init() {
        DataSource dataSource = mock(DataSource.class);
        Map<DataSourcesEnum, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourcesEnum.MYERP, dataSource);
        AbstractDbConsumer.configure(dataSourceMap);
    }

    @Test
    public void test_getListCompteComptable() throws Exception {
        // Given
        ComptabiliteDaoImpl comptabiliteDao = ComptabiliteDaoImpl.getInstance();
        comptabiliteDao.setSQLgetListCompteComptable("Dymmy query");
        List<CompteComptable> vList = new ArrayList<>();
        CompteComptable compteComptable = new CompteComptable(1234, "Un libellé");
        vList.add(compteComptable);

        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(vList);
        PowerMockito.whenNew(JdbcTemplate.class).withAnyArguments().thenReturn(jdbcTemplate);

        //When
        List<CompteComptable> returnedList = comptabiliteDao.getListCompteComptable();

        // Then
        Assert.assertEquals(vList, returnedList);
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class));
    }

    @Test
    public void test_getListLigneEcritureComptableByCompteNumber() throws Exception {
        // Given
        ComptabiliteDaoImpl comptabiliteDao = ComptabiliteDaoImpl.getInstance();
        comptabiliteDao.setSQLgetListLigneEcritureComptableByCompteNb("Dymmy query");
        List<LigneEcritureComptable> vList = new ArrayList<>();
        CompteComptable compteComptable = new CompteComptable(1234, "Un libellé");
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable(compteComptable,
                "dummy ligne comptable", new BigDecimal(123), new BigDecimal(321));

        vList.add(ligneEcritureComptable);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        when(namedParameterJdbcTemplate.query(anyString(), any(MapSqlParameterSource.class),any(RowMapper.class))).thenReturn(vList);
        PowerMockito.whenNew(NamedParameterJdbcTemplate.class).withAnyArguments().thenReturn(namedParameterJdbcTemplate);

        //When
        List<LigneEcritureComptable> returnedList = comptabiliteDao.getListLigneEcritureComptableByCompteNumber(3123);

        // Then
        Assert.assertEquals(vList, returnedList);
        verify(namedParameterJdbcTemplate, times(1)).query(anyString(),any(MapSqlParameterSource.class), any(RowMapper.class));
    }

    @Test
    public void test_getListJournalComptable() throws Exception {
        // Given
        ComptabiliteDaoImpl comptabiliteDao = ComptabiliteDaoImpl.getInstance();
        comptabiliteDao.setSQLgetListJournalComptable("Dymmy query");
        List<JournalComptable> vList = new ArrayList<>();
        JournalComptable journalComptable = new JournalComptable("1234", "Un libellé");
        vList.add(journalComptable);

        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(vList);
        PowerMockito.whenNew(JdbcTemplate.class).withAnyArguments().thenReturn(jdbcTemplate);

        //When
        List<JournalComptable> returnedList = comptabiliteDao.getListJournalComptable();

        // Then
        Assert.assertEquals(vList, returnedList);
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class));
    }

    @Test
    public void test_getListEcritureComptable() throws Exception {
        // Given
        ComptabiliteDaoImpl comptabiliteDao = ComptabiliteDaoImpl.getInstance();
        comptabiliteDao.setSQLgetListEcritureComptable("Dymmy query");
        List<EcritureComptable> vList = new ArrayList<>();
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(123);
        ecritureComptable.setJournal(new JournalComptable("1234", "Un libellé"));
        ecritureComptable.setLibelle("Dummy ecriture comptable");
        vList.add(ecritureComptable);

        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(vList);
        PowerMockito.whenNew(JdbcTemplate.class).withAnyArguments().thenReturn(jdbcTemplate);

        //When
        List<EcritureComptable> returnedList = comptabiliteDao.getListEcritureComptable();

        // Then
        Assert.assertEquals(vList, returnedList);
        verify(jdbcTemplate, times(1)).query(anyString(), any(RowMapper.class));
    }

    @Test
    public void test_getEcritureComptable() throws Exception {
        // Given
        ComptabiliteDaoImpl comptabiliteDao = ComptabiliteDaoImpl.getInstance();
        comptabiliteDao.setSQLgetEcritureComptable("Dymmy query");
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(123);
        ecritureComptable.setJournal(new JournalComptable("1234", "Un libellé"));
        ecritureComptable.setLibelle("Dummy ecriture comptable");

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        when(namedParameterJdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class),any(RowMapper.class))).thenReturn(ecritureComptable);
        PowerMockito.whenNew(NamedParameterJdbcTemplate.class).withAnyArguments().thenReturn(namedParameterJdbcTemplate);

        //When
        EcritureComptable returnedValue = comptabiliteDao.getEcritureComptableById(3123);

        // Then
        Assert.assertEquals(ecritureComptable, returnedValue);
        verify(namedParameterJdbcTemplate, times(1)).queryForObject(anyString(),any(MapSqlParameterSource.class), any(RowMapper.class));
    }

    @Test
    public void test_getEcritureComptableByRef() throws Exception {
        // Given
        ComptabiliteDaoImpl comptabiliteDao = ComptabiliteDaoImpl.getInstance();
        comptabiliteDao.setSQLgetEcritureComptableByRef("Dymmy query");
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setId(123);
        ecritureComptable.setJournal(new JournalComptable("1234", "Un libellé"));
        ecritureComptable.setLibelle("Dummy ecriture comptable");

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        when(namedParameterJdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class),any(RowMapper.class))).thenReturn(ecritureComptable);
        PowerMockito.whenNew(NamedParameterJdbcTemplate.class).withAnyArguments().thenReturn(namedParameterJdbcTemplate);

        //When
        EcritureComptable returnedValue = comptabiliteDao.getEcritureComptableByRef("A_REF");

        // Then
        Assert.assertEquals(ecritureComptable, returnedValue);
        verify(namedParameterJdbcTemplate, times(1)).queryForObject(anyString(),any(MapSqlParameterSource.class), any(RowMapper.class));
    }

    @Test
    public void test_loadListLigneEcriture() throws Exception {
        // Given
        ComptabiliteDaoImpl comptabiliteDao = ComptabiliteDaoImpl.getInstance();
        comptabiliteDao.setSQLloadListLigneEcriture("Dymmy query");
        CompteComptable compteComptable = new CompteComptable(1234, "Un libellé");
        List<LigneEcritureComptable> vList = new ArrayList<>();
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable(compteComptable,
                "dummy ligne comptable", new BigDecimal(123), new BigDecimal(321));

        vList.add(ligneEcritureComptable);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        // when(namedParameterJdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class),any(RowMapper.class))).thenReturn(vList);
        PowerMockito.whenNew(NamedParameterJdbcTemplate.class).withAnyArguments().thenReturn(namedParameterJdbcTemplate);
        EcritureComptable ecritureComptable = mock(EcritureComptable.class);

        //When
        comptabiliteDao.loadListLigneEcriture(ecritureComptable);

        // Then
        verify(namedParameterJdbcTemplate, times(1)).query(anyString(),any(MapSqlParameterSource.class), any(RowMapper.class));
        verify(ecritureComptable, times(2)).getListLigneEcriture();
    }

    @Test
    public void test_updateEcritureComptable() throws Exception {
        // Given
        ComptabiliteDaoImpl comptabiliteDao = ComptabiliteDaoImpl.getInstance();
        ComptabiliteDaoImpl comptabiliteDaoSpy = Mockito.spy(ComptabiliteDaoImpl.getInstance());
        comptabiliteDao.setSQLupdateEcritureComptable("Dymmy query");
        EcritureComptable ecritureComptable = mock(EcritureComptable.class, RETURNS_DEEP_STUBS);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        PowerMockito.whenNew(NamedParameterJdbcTemplate.class).withAnyArguments().thenReturn(namedParameterJdbcTemplate);
        doNothing().when(comptabiliteDaoSpy).deleteEcritureComptable(any(Integer.class));

        //When
        comptabiliteDao.updateEcritureComptable(ecritureComptable);

        // Then
        verify(namedParameterJdbcTemplate, times(1)).update(anyString(),any(MapSqlParameterSource.class));
    }

    @Test
    public void test_deleteEcritureComptable() throws Exception {
        // Given
        ComptabiliteDaoImpl comptabiliteDao = ComptabiliteDaoImpl.getInstance();
        comptabiliteDao.setSQLdeleteEcritureComptable("Dymmy query");

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        PowerMockito.whenNew(NamedParameterJdbcTemplate.class).withAnyArguments().thenReturn(namedParameterJdbcTemplate);

        //When
        comptabiliteDao.deleteEcritureComptable(1234);

        // Then
        verify(namedParameterJdbcTemplate, times(1)).update(anyString(),any(MapSqlParameterSource.class));
    }

    @Test
    public void test_getSeqEcritureComptableByJCodeAndYear() throws Exception {
        // Given
        ComptabiliteDaoImpl comptabiliteDao = ComptabiliteDaoImpl.getInstance();
        comptabiliteDao.setSQLgetSeqEcritureComptable("Dymmy query");
        JournalComptable journalComptable = new JournalComptable("1234", "Un libellé");
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable();
        sequenceEcritureComptable.setJournal(journalComptable);
        sequenceEcritureComptable.setAnnee(2020);
        sequenceEcritureComptable.setDerniereValeur(123);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        when(namedParameterJdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class),any(RowMapper.class))).thenReturn(sequenceEcritureComptable);
        PowerMockito.whenNew(NamedParameterJdbcTemplate.class).withAnyArguments().thenReturn(namedParameterJdbcTemplate);

        //When
        SequenceEcritureComptable returnedValue = comptabiliteDao.getSeqEcritureComptableByJCodeAndYear(123, "JCODE");

        // Then
        Assert.assertEquals(sequenceEcritureComptable, returnedValue);
        verify(namedParameterJdbcTemplate, times(1)).queryForObject(anyString(),any(MapSqlParameterSource.class), any(RowMapper.class));
    }

    @Test
    public void test_updateSequenceEcritureComptable() throws Exception {
        // Given
        ComptabiliteDaoImpl comptabiliteDao = ComptabiliteDaoImpl.getInstance();
        //comptabiliteDao.setSQLupdateSequenceEcritureComptable("Dymmy query");
        JournalComptable journalComptable = new JournalComptable("1234", "Un libellé");
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable();
        sequenceEcritureComptable.setJournal(journalComptable);
        sequenceEcritureComptable.setAnnee(2020);
        sequenceEcritureComptable.setDerniereValeur(123);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        PowerMockito.whenNew(NamedParameterJdbcTemplate.class).withAnyArguments().thenReturn(namedParameterJdbcTemplate);

        //When
        comptabiliteDao.updateSequenceEcritureComptable(sequenceEcritureComptable);

        // Then
        verify(namedParameterJdbcTemplate, times(1)).update(anyString(),any(MapSqlParameterSource.class));
    }

    @Test
    public void test_insertSequenceEcritureComptable() throws Exception {
        // Given
        ComptabiliteDaoImpl comptabiliteDao = ComptabiliteDaoImpl.getInstance();
        //comptabiliteDao.setSQLinsertSequenceEcritureComptable("Dymmy query");
        JournalComptable journalComptable = new JournalComptable("1234", "Un libellé");
        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable();
        sequenceEcritureComptable.setJournal(journalComptable);
        sequenceEcritureComptable.setAnnee(2020);
        sequenceEcritureComptable.setDerniereValeur(123);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        PowerMockito.whenNew(NamedParameterJdbcTemplate.class).withAnyArguments().thenReturn(namedParameterJdbcTemplate);

        //When
        comptabiliteDao.insertSequenceEcritureComptable(sequenceEcritureComptable);

        // Then
        verify(namedParameterJdbcTemplate, times(1)).update(anyString(),any(MapSqlParameterSource.class));
    }
}
