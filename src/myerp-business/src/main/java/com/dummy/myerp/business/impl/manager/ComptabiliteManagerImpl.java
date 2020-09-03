package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.util.DateUtility;
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

        //Récupération du code du journal_code de pEcritureComptable
        String journalCode =  pEcritureComptable.getJournal().getCode();

        //Récupération de la date de pEcritureComptable puis extraire l'année d'écriture
        Date date = pEcritureComptable.getDate();
        Integer ecritureYear = DateUtility.convertToCalender(date).get(Calendar.YEAR);

        //Récupération de la séquence correpondante au journalcode donnée et à l'année d'écriture.
        SequenceEcritureComptable seqEcritureComptable = this.getSequenceEcritureComptable(ecritureYear, journalCode);

        String pattern = "00000";
        DecimalFormat referenceCodeFormat = new DecimalFormat(pattern);

        if(seqEcritureComptable != null) {
            // Si la séquence d'écriture existe, on incrémente de 1 la dernière valeur.
            seqEcritureComptable.setDerniereValeur(seqEcritureComptable.getDerniereValeur() + 1);
            //Mettre à jour la séquence d'écriture
            this.updateSequenceEcritureComptable(seqEcritureComptable);

        } else {
            // Sinon, on crée une nouvelle séquence d'écriture avec 1 pour dernière valeur.
            seqEcritureComptable = new SequenceEcritureComptable();
            seqEcritureComptable.setAnnee(ecritureYear);
            seqEcritureComptable.setJournal(pEcritureComptable.getJournal());
            seqEcritureComptable.setDerniereValeur(1);
            this.createNewSequenceEcritureComptrable(seqEcritureComptable);
        }

        // Récupération de la dernière valeur de la séquence de l'ecriture comptable.
        int incrementedDerniereValeur = seqEcritureComptable.getDerniereValeur();
        // Mettre à jour la référence de l'écritureComptable
        String updatedReference = journalCode + "-" + ecritureYear + "/" +  referenceCodeFormat.format(incrementedDerniereValeur);
        pEcritureComptable.setReference(updatedReference);
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
        checkEcritureComptableUnit_RG2(pEcritureComptable);

        // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
        checkEcritureComptableUnit_RG3(pEcritureComptable);

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
                retrievedSeqEcritureComptable = getDaoProxy()
                        .getComptabiliteDao()
                        .getSeqEcritureComptableByJCodeAndYear(ecritureYear, journalCode);

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
     *  Insert a new sequenceEcritureComptable
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

    protected void checkEcritureComptableUnit_RG2(EcritureComptable pEcritureComptable) throws FunctionalException {
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }
    }

    protected void checkEcritureComptableUnit_RG3(EcritureComptable pEcritureComptable) throws FunctionalException {
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
    }


    protected void checkEcritureComptableUnit_RG5(EcritureComptable pEcritureComptable) throws FunctionalException {

        if (pEcritureComptable.getReference() != null) {

            //Récupération de la référence, du journal code et de l'année de l'écriture comptable
            String reference =  pEcritureComptable.getReference();
            String ecritureJCode = pEcritureComptable.getJournal().getCode();
            Date date = pEcritureComptable.getDate();
            String ecritureYear = String.valueOf(DateUtility.convertToCalender(date).get(Calendar.YEAR));
            String [] splitedRef = reference.split("[-/]");


            //Récupération de l'année de l'écriture comptable depuis la référence
            String refYear = splitedRef[1];
            //Récupération du journal_code depuis la référence
            String jcodeRef = splitedRef[0];
            //Récupération du code de la référence
            String refCode = splitedRef[2];

            //Vérifie si la référence respecte bien le format demandé
            Pattern refRegexFormat = Pattern.compile("\\w{2}-\\d{4}/\\d{5}");
            if (!refRegexFormat.matcher(reference).matches()) {
                throw new FunctionalException(String.format("La référence (%s) ne respecte pas le format requis: xx-AAAA/#####.", reference));
            }

            //Vérifie que le code journal dans la référence correspond à celui de l'écriture comptable
            if (!jcodeRef.equals(ecritureJCode)) {
                throw new FunctionalException(String.format("Le journal code de l'écriture comptable (%s) ne correspond pas à celui de la référence (%s).", ecritureJCode, jcodeRef ));
            }

            //Vérifie que l'année dans la référence correspond à l'année de l'écriture comptable
            if (!refYear.equals(ecritureYear)) {
                throw new FunctionalException(String.format("L'année de l'écriture comptable (%s) est diférente de celle de la référence (%s).", ecritureYear, refYear));
            }

            //Vérifie si le code de la référence contient bien 5 chiffres
            Pattern refCodeRegexFormat = Pattern.compile("\\d{5}");
            if (!refCodeRegexFormat.matcher(refCode).matches()) {
                throw new FunctionalException(String.format("Le code (%s) de la référence ne respecte pas le format requis de 5 chiffres.", refCode ));
            }

        } else {
            throw new FunctionalException("La référence de l'écriture comptable est null!!");
        }
    }


}
