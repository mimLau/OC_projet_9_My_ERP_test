package com.dummy.myerp.model.bean.comptabilite;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LigneEcritureComptableTest {


    private LigneEcritureComptable ligneEcritureComptable;
    private static CompteComptable compteComptable = new CompteComptable(1, "Banques");


    @Test
    public void testToString() {

        // GIVEN
        ligneEcritureComptable = new LigneEcritureComptable();
        ligneEcritureComptable.setCompteComptable(compteComptable);
        ligneEcritureComptable.setLibelle("Paiement fournisseur");
        ligneEcritureComptable.setDebit(new BigDecimal(1500));
        ligneEcritureComptable.setCredit(new BigDecimal(0));

        // WHEN
        String result = ligneEcritureComptable.toString();

        // THEN
        assertEquals( "LigneEcritureComptable{compteComptable=CompteComptable{numero=1, libelle='Banques'}, libelle='Paiement fournisseur', debit=1500, credit=0}", result);

    }

}
