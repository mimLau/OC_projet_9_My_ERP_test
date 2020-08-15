package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.dummy.myerp.model.bean.comptabilite.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;


/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ====================


    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {
    }


    // ==================== Getters/Setters ====================
    @Override
    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();
    }


    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }

    /**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) {

        // Bien se réferer à la JavaDoc de cette méthode !
        /* Le principe :
                1.  Remonter depuis la persitance la dernière valeur de la séquence du journal pour l'année de l'écriture
                    (table sequence_ecriture_comptable)
                2.  * S'il n'y a aucun enregistrement pour le journal pour l'année concernée :
                        1. Utiliser le numéro 1.
                    * Sinon :
                        1. Utiliser la dernière valeur + 1
                3.  Mettre à jour la référence de l'écriture avec la référence calculée (RG_Compta_5)
                4.  Enregistrer (insert/update) la valeur de la séquence en persistance
                    (table sequence_ecriture_comptable)
         */

        //Récupération du code du journal_code de pEcritureComptable
        String journalCode =  pEcritureComptable.getJournal().getCode();

        //Récupération de la date de pEcritureComptable puis extraire l'année d'écriture
        Date date = pEcritureComptable.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Integer ecritureYear = calendar.get(Calendar.YEAR);

        //Récupération de la séquence correpondante au journalcode donnée et à l'année d'écriture.
        SequenceEcritureComptable retrievedSeqEcritureComptable = this.getSequenceEcritureComptable(ecritureYear, journalCode);
        SequenceEcritureComptable newSequenceEcritureComptable = new SequenceEcritureComptable();

        if(retrievedSeqEcritureComptable != null) {
            // Si la séquence d'écriture existe, on incrémente de 1 la dernière valeur.
            retrievedSeqEcritureComptable.setDerniereValeur(retrievedSeqEcritureComptable.getDerniereValeur() + 1);
            //Mettre à jour la séquence d'écriture
            this.updateSequenceEcritureComptable(retrievedSeqEcritureComptable);

        } else {
            // Sinon, on crée une nouvelle séquence d'écriture avec 1 pour dernière valeur.
            newSequenceEcritureComptable.setAnnee(ecritureYear);
            newSequenceEcritureComptable.setJournal(pEcritureComptable.getJournal());
            newSequenceEcritureComptable.setDerniereValeur(1);
            this.createNewSequenceEcritureComptrable(newSequenceEcritureComptable);
        }


        // Récupération du numéro de séquence de la référence de la derniere écriture comptable enregistrée.
        int lastSeqValue = getDaoProxy().getComptabiliteDao().countEcritureComptableRows();
        // Génlration de la nouvelle référence
        String updatedReference = journalCode + "-" + ecritureYear + "/" + String.format("%05d", lastSeqValue + 1);
        // Mettre à jour la référence de l'écritureComptable



        //TODO   Mettre à jour la référence de l'écriture avec la référence calculée (RG_Compta_5)
        /*
         *  La référence d'une écriture comptable est composée du code du journal dans lequel figure l'écriture
         *  suivi de l'année et d'un numéro de séquence (propre à chaque journal) sur 5 chiffres incrémenté
         *  automatiquement à chaque écriture.
         *  la référence est : XX-AAAA/#####.
         *  Ex : Journal de banque (BQ), écriture au 31/12/2016
         *       --> BQ-2016/00001
         *
         * Questions? On cosnidère que pEcritureComptable est une écriture qui existe déjà dans la bdd? mais si elle existe
         * déjà, je n'arrive pas à comprendre pourquoi lui créer une séquence, y a un truc que je ne saisis pas. Une séquence n'est
         * elle pas créée ou updatée à chaque fois qu'une nouvelle écriture comptable est insérée dans la table.
         * Donc pour lui générer sa référence, il faut compter le nb de ligne qu'il y a dans la table et incrémenter de 1
         * cette valeur qui correspondra au numéro de séquence de la ref?
         * */
    }

    /**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    // TODO tests à compléter
    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
                                          new ConstraintViolationException(
                                              "L'écriture comptable ne respecte pas les contraintes de validation",
                                              vViolations));
        }

        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }

        // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
        int vNbrCredit = 0;
        int vNbrDebit = 0;
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrCredit++;
            }
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrDebit++;
            }
        }
        // On test le nombre de lignes car si l'écriture à une seule ligne
        //      avec un montant au débit et un montant au crédit ce n'est pas valable
        if (pEcritureComptable.getListLigneEcriture().size() < 2
            || vNbrCredit < 1
            || vNbrDebit < 1) {
            throw new FunctionalException(
                "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        }

        // TODO ===== RG_Compta_5 : Format et contenu de la référence
        // vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal...
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(
                    pEcritureComptable.getReference());

                // Si l'écriture à vérifier est une nouvelle écriture (id == null),
                // ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                // c'est qu'il y a déjà une autre écriture avec la même référence
                if (pEcritureComptable.getId() == null
                    || !pEcritureComptable.getId().equals(vECRef.getId())) {
                    throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * Search a sequenceEcritureCompatble in fucntion the year and the journalCode
     * @param ecritureYear year of the ecriture comptable.
     * @param journalCode the journal code of the ecriture comptable.
     * @return the retrieved sequenceEcritureComptable
     */
    protected SequenceEcritureComptable getSequenceEcritureComptable(Integer ecritureYear, String journalCode) {
        SequenceEcritureComptable retrievedSeqEcritureComptable;
        try {
            TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
            try {
                retrievedSeqEcritureComptable = getDaoProxy()
                        .getComptabiliteDao()
                        .getSeqEcritureComptableByJCodeAndYear(ecritureYear, journalCode);
                getTransactionManager().commitMyERP(vTS);
                vTS = null;
            } finally {
                getTransactionManager().rollbackMyERP(vTS);
            }
        } catch (NotFoundException e) {
            retrievedSeqEcritureComptable = null;
        }
        return retrievedSeqEcritureComptable;
    }

    /**
     *  Update a seauenceEcritureComptable
     * @param sequenceEcritureComptable to be updated
     */
    protected void updateSequenceEcritureComptable(SequenceEcritureComptable sequenceEcritureComptable) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
                getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(sequenceEcritureComptable);
                getTransactionManager().commitMyERP(vTS);
                vTS = null;
            } finally {
                getTransactionManager().rollbackMyERP(vTS);
        }
    }


    /**
     *  Insert a new seauenceEcritureComptable
     * @param sequenceEcritureComptable
     */
    protected void createNewSequenceEcritureComptrable(SequenceEcritureComptable sequenceEcritureComptable) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(sequenceEcritureComptable);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }


}
