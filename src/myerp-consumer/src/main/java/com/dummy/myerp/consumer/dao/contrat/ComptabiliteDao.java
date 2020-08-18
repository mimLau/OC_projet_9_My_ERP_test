package com.dummy.myerp.consumer.dao.contrat;

import java.util.List;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Interface de DAO des objets du package Comptabilite
 */
public interface ComptabiliteDao {

    /**
     * Renvoie la liste des Comptes Comptables
     * @return {@link List}
     */
    List<CompteComptable> getListCompteComptable();


    /**
     * Renvoie la liste des Journaux Comptables
     * @return {@link List}
     */
    List<JournalComptable> getListJournalComptable();


    // ==================== EcritureComptable ====================

    /**
     * Renvoie la liste des Écritures Comptables
     * @return {@link List}
     */
    List<EcritureComptable> getListEcritureComptable();

    /**
     * Renvoie l'Écriture Comptable d'id {@code pId}.
     *
     * @param pId l'id de l'écriture comptable
     * @return {@link EcritureComptable}
     * @throws NotFoundException : Si l'écriture comptable n'est pas trouvée
     */
    EcritureComptable getEcritureComptable(Integer pId) throws NotFoundException;

    /**
     * Renvoie l'Écriture Comptable de référence {@code pRef}.
     *
     * @param pReference la référence de l'écriture comptable
     * @return {@link EcritureComptable}
     * @throws NotFoundException : Si l'écriture comptable n'est pas trouvée
     */
    EcritureComptable getEcritureComptableByRef(String pReference) throws NotFoundException;

    /**
     * Charge la liste des lignes d'écriture de l'écriture comptable {@code pEcritureComptable}
     *
     * @param pEcritureComptable -
     */
    void loadListLigneEcriture(EcritureComptable pEcritureComptable);

    /**
     * Insert une nouvelle écriture comptable.
     *
     * @param pEcritureComptable -
     */
    void insertEcritureComptable(EcritureComptable pEcritureComptable);

    /**
     * Met à jour l'écriture comptable.
     *
     * @param pEcritureComptable -
     */
    void updateEcritureComptable(EcritureComptable pEcritureComptable);

    /**
     * Supprime l'écriture comptable d'id {@code pId}.
     *
     * @param pId l'id de l'écriture
     */
    void deleteEcritureComptable(Integer pId);


    /**
     * Renvoie la sequence de l'écriture comptable du jounal_code {@code jCode}
     * pour l'année d'écriutre {@code année }
     *
     * @param ecritureYear l'année d'écriture du jounal code
     * @param  ecritureComptable
     * @return {@link SequenceEcritureComptable}
     * @throws NotFoundException : Si la séquence de l'écriture comptable n'a pas été trouvée
     */
    SequenceEcritureComptable  getSeqEcritureComptableByJCodeAndYear(Integer ecritureYear, EcritureComptable ecritureComptable) throws NotFoundException;

    /**
     * Met à jour la séquence d'écriture comptable.
     *
     * @param sequenceEcritureComptable
     */
    void updateSequenceEcritureComptable(SequenceEcritureComptable sequenceEcritureComptable);

    /**
     * Insérer une séquence d'écriture comptable.
     *
     * @param sequenceEcritureComptable
     */
    void insertSequenceEcritureComptable(SequenceEcritureComptable sequenceEcritureComptable);

    /**
     *
     * @return number of rows of ecriture_comptable table
     */
    int countEcritureComptableRows();
}
