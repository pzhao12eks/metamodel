package org.eobjects.metamodel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.swing.table.TableModel;

import junit.framework.TestCase;

import org.eobjects.metamodel.data.DataSet;
import org.eobjects.metamodel.data.DataSetTableModel;
import org.eobjects.metamodel.jdbc.JdbcDataContext;
import org.eobjects.metamodel.query.FromItem;
import org.eobjects.metamodel.query.JoinType;
import org.eobjects.metamodel.query.Query;
import org.eobjects.metamodel.query.SelectItem;
import org.eobjects.metamodel.schema.Schema;
import org.eobjects.metamodel.schema.Table;

/**
 * Integrationtests for Firebird SQL.
 * 
 * This test uses the "employee" sampledata shipped with Firebird. The JDBC
 * driver ("jaybird") is not available in the Maven repository so you will have
 * to download and attach it to the eclipse project yourself.
 * 
 * @see http://www.firebirdsql.org/manual/qsg10-connecting.html
 * @see http://www.firebirdsql.org/index.php?op=files&id=jaybird
 */
public class FirebirdTest extends TestCase {

	private static final String CONNECTION_STRING = "jdbc:firebirdsql:127.0.0.1:employee.fdb";
	private static final String USERNAME = "SYSDBA";
	private static final String PASSWORD = "eobjects";
	private Connection _connection;
	private DataContext _dataContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Class.forName("org.firebirdsql.jdbc.FBDriver");
		_connection = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
		_connection.setReadOnly(true);
		_dataContext = new JdbcDataContext(_connection);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		_connection.close();
	}

	public void testGetSchemas() throws Exception {
		Schema[] schemas = _dataContext.getSchemas();
		assertEquals(1, schemas.length);
		Schema schema = _dataContext.getDefaultSchema();
		assertEquals("{JdbcTable[name=COUNTRY,type=TABLE,remarks=<null>],"
				+ "JdbcTable[name=CUSTOMER,type=TABLE,remarks=<null>],"
				+ "JdbcTable[name=DEPARTMENT,type=TABLE,remarks=<null>],"
				+ "JdbcTable[name=EMPLOYEE,type=TABLE,remarks=<null>],"
				+ "JdbcTable[name=EMPLOYEE_PROJECT,type=TABLE,remarks=<null>],"
				+ "JdbcTable[name=JOB,type=TABLE,remarks=<null>],"
				+ "JdbcTable[name=PHONE_LIST,type=VIEW,remarks=<null>],"
				+ "JdbcTable[name=PROJECT,type=TABLE,remarks=<null>],"
				+ "JdbcTable[name=PROJ_DEPT_BUDGET,type=TABLE,remarks=<null>],"
				+ "JdbcTable[name=SALARY_HISTORY,type=TABLE,remarks=<null>],"
				+ "JdbcTable[name=SALES,type=TABLE,remarks=<null>]}", Arrays.toString(schema.getTables()));

		assertEquals(
				"{Relationship[primaryTable=COUNTRY,primaryColumns={COUNTRY},foreignTable=CUSTOMER,foreignColumns={COUNTRY}],"
						+ "Relationship[primaryTable=COUNTRY,primaryColumns={COUNTRY},foreignTable=JOB,foreignColumns={JOB_COUNTRY}],"
						+ "Relationship[primaryTable=CUSTOMER,primaryColumns={CUST_NO},foreignTable=SALES,foreignColumns={CUST_NO}],"
						+ "Relationship[primaryTable=DEPARTMENT,primaryColumns={DEPT_NO},foreignTable=DEPARTMENT,foreignColumns={HEAD_DEPT}],"
						+ "Relationship[primaryTable=DEPARTMENT,primaryColumns={DEPT_NO},foreignTable=EMPLOYEE,foreignColumns={DEPT_NO}],"
						+ "Relationship[primaryTable=DEPARTMENT,primaryColumns={DEPT_NO},foreignTable=PROJ_DEPT_BUDGET,foreignColumns={DEPT_NO}],"
						+ "Relationship[primaryTable=EMPLOYEE,primaryColumns={EMP_NO},foreignTable=DEPARTMENT,foreignColumns={MNGR_NO}],"
						+ "Relationship[primaryTable=EMPLOYEE,primaryColumns={EMP_NO},foreignTable=EMPLOYEE_PROJECT,foreignColumns={EMP_NO}],"
						+ "Relationship[primaryTable=EMPLOYEE,primaryColumns={EMP_NO},foreignTable=PROJECT,foreignColumns={TEAM_LEADER}],"
						+ "Relationship[primaryTable=EMPLOYEE,primaryColumns={EMP_NO},foreignTable=SALARY_HISTORY,foreignColumns={EMP_NO}],"
						+ "Relationship[primaryTable=EMPLOYEE,primaryColumns={EMP_NO},foreignTable=SALES,foreignColumns={SALES_REP}],"
						+ "Relationship[primaryTable=JOB,primaryColumns={JOB_CODE},foreignTable=EMPLOYEE,foreignColumns={JOB_CODE}],"
						+ "Relationship[primaryTable=JOB,primaryColumns={JOB_GRADE},foreignTable=EMPLOYEE,foreignColumns={JOB_GRADE}],"
						+ "Relationship[primaryTable=JOB,primaryColumns={JOB_COUNTRY},foreignTable=EMPLOYEE,foreignColumns={JOB_COUNTRY}],"
						+ "Relationship[primaryTable=PROJECT,primaryColumns={PROJ_ID},foreignTable=EMPLOYEE_PROJECT,foreignColumns={PROJ_ID}],"
						+ "Relationship[primaryTable=PROJECT,primaryColumns={PROJ_ID},foreignTable=PROJ_DEPT_BUDGET,foreignColumns={PROJ_ID}]}",
				Arrays.toString(schema.getRelationships()));
	}

	public void testExecuteQuery() throws Exception {
		Schema schema = _dataContext.getDefaultSchema();
		Table departmentTable = schema.getTableByName("DEPARTMENT");
		Table employeeTable = schema.getTableByName("EMPLOYEE");
		Query q = new Query().from(new FromItem(JoinType.INNER, departmentTable.getRelationships(employeeTable)[0]));
		q.select(departmentTable.getColumns()[1]);
		q.select(new SelectItem(employeeTable.getColumns()[4]).setAlias("hire-date"));
		assertEquals(
				"SELECT \"DEPARTMENT\".\"DEPARTMENT\", \"EMPLOYEE\".\"HIRE_DATE\" AS hire-date FROM \"EMPLOYEE\" INNER JOIN \"DEPARTMENT\" ON \"EMPLOYEE\".\"EMP_NO\" = \"DEPARTMENT\".\"MNGR_NO\"",
				q.toString());

		DataSet data = _dataContext.executeQuery(q);
		assertNotNull(data);

		TableModel tableModel = new DataSetTableModel(data);
		assertEquals(2, tableModel.getColumnCount());
		assertEquals(17, tableModel.getRowCount());
		assertEquals("Quality Assurance", tableModel.getValueAt(4, 0).toString());

		Date date = (Date) tableModel.getValueAt(4, 1);
		assertEquals("1989-04-17 00:00:00.000000", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").format(date));

	}
}