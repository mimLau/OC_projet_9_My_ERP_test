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
import jdk.nashorn.internal.ir.FunctionNode;
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
        String journalCode = pEcritureComptable.getJournal().getCode();

        //Récupération de la date de pEcritureComptable puis extraire l'année d'écriture
        Date date = pEcritureComptable.getDate();
        Integer ecritureYear = DateUtility.convertToCalender(date).get(Calendar.YEAR);

        //Récupération de la séquence correpondante au journalcode donnée et à l'année d'écriture.
        SequenceEcritureComptable seqEcritureComptable = this.getSequenceEcritureComptable(ecritureYear, journalCode);

        String pattern = "00000";
        DecimalFormat referenceCodeFormat = new DecimalFormat(pattern);

        if (seqEcritureComptable != null) {
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
        String updatedReference = journalCode + "-" + ecritureYear + "/" + referenceCodeFormat.format(incrementedDerniereValeur);
        pEcritureComptable.setReference(updatedReference);
    }

    /**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnitConstraints(pEcritureComptable);
        this.checkEcritureComptableUnit_RG2(pEcritureComptable);
        this.checkEcritureComptableUnit_RG3(pEcritureComptable);
        this.checkEcritureComptableUnit_RG5(pEcritureComptable);
        this.checkEcritureComptableUnit_RG6(pEcritureComptable);
        this.checkEcritureComptableUnit_RG7(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableUnitConstraints(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            String errorMessage = "L'écriture comptable ne respecte pas les contraintes de validation :";
            for (ConstraintViolation v : vViolations) {
                errorMessage = errorMessage + " " + v.getMessage();
            }
            throw new FunctionalException(errorMessage);
        }
    }

    /**
     * Vérifie aue une ligne écriture comporte soit une montant au crédit soit un montant au débit mais pas les 2.
     *
     * @param pEcritureComptable -
     * @throws FunctionalException
     */
    protected void checkEcritureComptableUnitDebitOrCredit(EcritureComptable pEcritureComptable) throws FunctionalException {
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(), BigDecimal.ZERO)) != 0
                    && (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(), BigDecimal.ZERO)) != 0)) {
                throw new FunctionalException(
                        "Une écriture comptable ne doit pas avoir un montant au débit et un montant au crédit.");
            }
        }
    }

    // ===== RG_Compta_1 : Le solde d'un compte comptable est égal à la somme des montants au débit des lignes d'écriture
    // diminuées de la somme des montants au crédit.
    // Si le résultat est positif, le solde est dit "débiteur", si le résultat est négatif le solde est dit "créditeur".
    protected BigDecimal checkEcritureComptableUnit_RG1(CompteComptable compteComptable) throws NotFoundException, FunctionalException {
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal balance;

        List<LigneEcritureComptable> ligneEcritureComptables = getDaoProxy().getComptabiliteDao().getListLigneEcritureComptableByCompteNumber(compteComptable.getNumero());

        if (ligneEcritureComptables == null)
            throw new NotFoundException("There is any ligne ecriture for the accounting account number : " + compteComptable.getNumero());

        for (LigneEcritureComptable ligne : ligneEcritureComptables) {
            if (ligne.getDebit() != null) {
                totalDebit = totalDebit.add(ligne.getDebit());
            }
            if (ligne.getCredit() != null) {
                totalCredit = totalCredit.add(ligne.getCredit());
            }
        }

        balance = totalDebit.subtract(totalCredit);

        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            throw new FunctionalException(String.format("Le compte comptable numéro %s est débiteur.", compteComptable.getNumero()));

        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new FunctionalException(String.format("Le compte comptable numéro %s est créditeur.", compteComptable.getNumero()));
        }

        return balance;
    }



    /**
     * Check if ecriture comptable is equilibre
     * @param pEcritureComptable
     * @throws FunctionalException
     */
    // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
    protected void checkEcritureComptableUnit_RG2(EcritureComptable pEcritureComptable) throws FunctionalException {
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }
    }

    /**
     * Check if the ecriture comptable has 2 writing lines. One debit and the second credit.
     * @param pEcritureComptable
     * @throws FunctionalException
     */
    // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
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

    // ===== RG_Compta_4 : Les montants des lignes d'écriture sont signés et peuvent prendre des valeurs négatives (même si cela est peu fréquent).
    protected void checkEcritureComptableUnit_RG4(EcritureComptable pEcritureComptable) throws FunctionalException {


    }

    /**
     * Vérifie le format et le contenu de la référence d'une ecriture journal
     * @param pEcritureComptable
     * @throws FunctionalException
     */
    // ===== RG_Compta_5 : la référence d'une écriture compatble doit respecter un format bien précis, et doit contnenir le bon journal code et la bonne date d'écriutre
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

    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
    protected void checkEcritureComptableUnit_RG6(EcritureComptable pEcritureComptable) throws FunctionalException {
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

    // ===== RG_Compta_7 : Les montants des lignes d'écritures peuvent comporter 2 chiffres maximum après la virgule.
    protected void checkEcritureComptableUnit_RG7(EcritureComptable pEcritureComptable) throws FunctionalException {
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {

            String debit;
            String credit;
            int debitIndex;
            int creditIndex;
            int debitNbDigit = 0;
            int creditNbDigit = 0;

            if(vLigneEcritureComptable.getDebit() != null) {
                debit = vLigneEcritureComptable.getDebit().toPlainString();
                debitIndex = debit.indexOf(".");
                 debitNbDigit = debitIndex < 0 ?  0 : debit.length() - debitIndex - 1;
            }

            if(vLigneEcritureComptable.getCredit() != null) {
                credit = vLigneEcritureComptable.getCredit().toPlainString();
                creditIndex = credit.indexOf(".");
                creditNbDigit = creditIndex < 0 ?  0 : credit.length() - creditIndex - 1;
            }

            if(creditNbDigit > 2 || debitNbDigit > 2)
                throw new FunctionalException("Le montant des lignes écriture doit comporter au maximum 2 chiffres.");
        }

        /*
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
                Set<ConstraintViolation<LigneEcritureComptable>> vViolations = getConstraintValidator().validate(vLigneEcritureComptable);
                if (!vViolations.isEmpty()) {
                    for(ConstraintViolation v: vViolations)
                        v.getMessage();
                    throw new FunctionalException("Le montant des lignes écriture doit comporter au maximum 2 chiffres.");
                }
            }
         */
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
     *  Update a sequenceEcritureComptable
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
}
