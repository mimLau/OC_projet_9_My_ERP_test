package com.dummy.myerp.model.bean.comptabilite;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SequenceEcritureComptableTest {

     SequenceEcritureComptable sequenceEcritureComptableUnderTest;
     JournalComptable journalComptable;


    @Test
    @DisplayName("Testing the method toString of ligne ecriture")
    public void givenSequenceEcritureComptable_whenToString_returnTheGoodSentence() {

        // GIVEN
        sequenceEcritureComptableUnderTest = new SequenceEcritureComptable();
        sequenceEcritureComptableUnderTest.setAnnee(2019);
        sequenceEcritureComptableUnderTest.setDerniereValeur(12345);

        // WHEN
        String result = sequenceEcritureComptableUnderTest.toString();

        // THEN
        assertThat(result).isEqualTo("SequenceEcritureComptable{annee=2019, derniereValeur=12345}");
    }
}
