package com.dummy.myerp.model.bean.comptabilite;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class JournalComptableTest {

    JournalComptable journalComptableUnderTest;
    List<JournalComptable> journalComptableList = new ArrayList<>();

    @BeforeEach
    public void initJournalComptableList() {

        journalComptableUnderTest = new JournalComptable("BQ","Banques");
        journalComptableList.add(journalComptableUnderTest);
        journalComptableUnderTest = new JournalComptable("AC","Achats");
        journalComptableList.add(journalComptableUnderTest);
        journalComptableUnderTest = new JournalComptable("FO","Fournisseurs");
        journalComptableList.add(journalComptableUnderTest);
    }

    @Test
    void givenJornalComptableCode_whenGetByCode_thenReturnTheGoodJournalComptable() {

        // GIVEN
        String journalComptableCode = "BQ";

        // WHEN
        JournalComptable journalComptable = JournalComptable.getByCode(journalComptableList,journalComptableCode);

        // THEN
        assertThat(journalComptable.getCode()).isEqualTo(journalComptableCode);
    }

    @Test
    void givenJornalComptableCode_whenGetByCode__ifJournalComptableDontExist_shouldReturnNull() {

        // GIVEN
        String journalComptableCode = "AT";

        // WHEN
        JournalComptable result = JournalComptable.getByCode(journalComptableList,journalComptableCode);

        // THEN
        assertThat(result).isEqualTo(null);
    }


    @Test
    public void givenComptableCode_whenToString_returnTheGoodSentence(){

        // GIVEN
        JournalComptable journalComptable = journalComptableList.get(2);

        // WHEN
        String result = journalComptable.toString();

        // THEN
        assertThat("JournalComptable{code='FO', libelle='Fournisseurs'}").isEqualTo(result);
    }
}
