package com.dummy.myerp.testbusiness.business;


import com.dummy.myerp.business.impl.manager.ComptabiliteManagerImpl;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/schema.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/data.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/truncate_DB_IT.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/populate_DB_IT.sql")})

public class ComptabiliteManagerImplIT extends BusinessTestCase {

    private ComptabiliteManagerImpl comptabiliteManager = new ComptabiliteManagerImpl();


    @Test
    public void checkGetListCompteComptable() {
        List<CompteComptable> compteComptableList = comptabiliteManager.getListCompteComptable();
        assertThat(compteComptableList).isNotEmpty();
    }

}
