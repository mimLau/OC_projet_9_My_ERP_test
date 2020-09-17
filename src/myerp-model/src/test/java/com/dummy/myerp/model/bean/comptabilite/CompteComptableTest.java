package com.dummy.myerp.model.bean.comptabilite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CompteComptableTest {

    CompteComptable compteComptableUnderTest;
    List<CompteComptable> compteComptableList = new ArrayList<>();

    @BeforeEach
    public void initCompteCompableList() {

        compteComptableUnderTest = new CompteComptable(1, "Founisseurs");
        compteComptableList.add(compteComptableUnderTest);
        compteComptableUnderTest = new CompteComptable(2, "Achats");
        compteComptableList.add(compteComptableUnderTest);
        compteComptableUnderTest = new CompteComptable(3, "Banque");
        compteComptableList.add(compteComptableUnderTest);
    }

    @Test
    @DisplayName("Retrieve a compte comtable by his number.")
    public void givenCompteComptableNb_whenGetByNumero_ifCompteExist_shouldReturnCompteComptable() {

        // GIVEN
        int compteComptableNb = 1;

        // WHEN
        CompteComptable compteComptable = CompteComptable.getByNumero(compteComptableList,compteComptableNb);

        // THEN
        assertThat(compteComptable.getNumero()).isEqualTo(compteComptableNb);
    }

    @Test
    @DisplayName("Getting a compte comptable with a number which doesn't exist, then should return null.")
    public void givenCompteComptableNb_whenGetByNumero_ifCompteDontExist_shouldReturnNull() {

        // GIVEN
        int compteComptableNb = 4;

        // WHEN
        CompteComptable compteComptable = CompteComptable.getByNumero(compteComptableList,compteComptableNb);

        // THEN
        assertThat(compteComptable).isEqualTo(null);
    }

    @Test
    @DisplayName("Testing the method toString of compte comptable")
    public void givenCompteComptableNb_whenToString_returnTheGoodSentence(){

       // GIVEN
        CompteComptable compteComptable = compteComptableList.get(1);

        // WHEN
        String result = compteComptable.toString();

        // THEN
        assertThat("CompteComptable{numero=2, libelle='Achats'}").isEqualTo(result);
    }
}
