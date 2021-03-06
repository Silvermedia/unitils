package org.unitils.dbunit.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.dataset.Table;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class TableDifferenceGetActualTableTest {

    /* Tested object */
    private TableDifference tableDifference;

    private Table table;
    private Table actualTable;


    @Before
    public void initialize() {
        table = new Table("table");
        actualTable = new Table("table");

        tableDifference = new TableDifference(table, actualTable);
    }


    @Test
    public void getActualTable() {
        Table result = tableDifference.getActualTable();
        assertSame(actualTable, result);
    }
}
