### SQL
##### 语句插入

```sql
insert test_table(clobcolumn) values(to_clob('待插入的字符串'));
```

```sql
DECLARE  
 REALLYBIGTEXTSTRING CLOB := '待插入的海量字符串';  
BEGIN  
   INSERT INTO test_table VALUES('test', REALLYBIGTEXTSTRING, '0');  
end ;  
/  
commit; 
```

#### 文件插入
##### 创建存储过程
```sql
create or replace
procedure UPDATECLOBFORMFILE (TABLENAME varchar2,CLOB_COL_NAME varchar2,P_RID rowid,DIRNAME varchar2,FILENAME varchar2,ISPRINT BOOLEAN)
	--tableName : the table's name which you will be update
	--clob_col_name : the column'name which type is clob and you will be update
	--p_rid : the record 's rowid use for filter 
	--dirName : the dirctory name you create in oracle which mapping the dirctory in your os
	--fileName : the file's name which you want to save 
  --isPrint : is print the file's context or not
is
	P_CLOB clob;
	P_UPDATESQL varchar2(200);
	P_BFILE bfile;
  P_DEST_OFFSET integer:=1;
  P_SRC_OFFSET integer:=1;
  P_CHARSET varchar2(32);
  P_BFILE_CSID number;
  P_LANG_CONTEXT integer :=DBMS_LOB.DEFAULT_LANG_CTX;
  P_WARNING integer;
  P_BUFFER raw(32000);
  P_BUFFER_SIZE integer:=32000;
  P_OFFSET integer:=1;
  
begin
  --get the db charset id use for load file by suitable charset ,otherwise the context read from file will be garbled
  select value into P_CHARSET from V$NLS_PARAMETERS where PARAMETER='NLS_CHARACTERSET';
  select NLS_CHARSET_ID(P_CHARSET) into P_BFILE_CSID from DUAL;
  
	--create the dynamic sql str
	P_UPDATESQL :='update '||TABLENAME||' set '||CLOB_COL_NAME||'=empty_clob() where rowid=:1 return '||CLOB_COL_NAME||' into :2';
	--execute the dynamic sql
	execute immediate P_UPDATESQL using P_RID returning into P_CLOB ;
 	
	P_BFILE := BFILENAME(DIRNAME,FILENAME);
  
	if (DBMS_LOB.FILEEXISTS(P_BFILE)!=0) 
	then
		DBMS_LOB.FILEOPEN(P_BFILE,DBMS_LOB.FILE_READONLY);
		DBMS_LOB.LOADCLOBFROMFILE(P_CLOB,P_BFILE,DBMS_LOB.GETLENGTH(P_BFILE),P_DEST_OFFSET,P_SRC_OFFSET,P_BFILE_CSID,P_LANG_CONTEXT,P_WARNING);
    if ISPRINT then
      --setup the print buffer size
      DBMS_OUTPUT.enable (BUFFER_SIZE=>null);
      WHILE P_OFFSET<DBMS_LOB.GETLENGTH(P_CLOB) LOOP
        DBMS_LOB.read(P_BFILE,P_BUFFER_SIZE,P_OFFSET,P_BUFFER);
        P_OFFSET:=P_OFFSET+P_BUFFER_SIZE;
        DBMS_OUTPUT.PUT_LINE(UTL_RAW.CAST_TO_VARCHAR2(P_BUFFER));
      end LOOP;
    end if;
    DBMS_LOB.FILECLOSE(P_BFILE);--close the file
		commit;
	else--if the specific file is not exist
    	dbms_output.put_line('file not found');
    	rollback;
  	end if;
	--close refcursor;
  	exception when others then
  		DBMS_OUTPUT.PUT_LINE('other exception occur,pls check the trace log!');
      raise;
end;
```
##### 调用存储过程
```sql
DECLARE
  TABLENAME VARCHAR2(200);
  CLOB_COL_NAME VARCHAR2(200);
  P_RID ROWID;
  DIRNAME VARCHAR2(200);
  FILENAME VARCHAR2(200);
  ISDEBUG BOOLEAN;
BEGIN
  TABLENAME := 'tablename';--表名称
  CLOB_COL_NAME := 'columnname';--clob字段名称
  P_RID := 'AAAXKyAAGAAAG72AAK';--需要更新clob的记录的rowid
  DIRNAME := 'BFILE_DIR';--oracle目录名称
  FILENAME := 'aaa.txt';--文件名称
  ISDEBUG:=TRUE;
  UPDATECLOBFORMFILE(
    TABLENAME => TABLENAME,
    CLOB_COL_NAME => CLOB_COL_NAME,
    P_RID => P_RID,
    DIRNAME => DIRNAME,
    FILENAME => FILENAME,
    ISPRINT => ISDEBUG
  );
END;
```



### jdbc

##### CharacterStream方式

```java
 
 
/** 
 * 读取CLOB字段的代码示例 
 */  
public void readClob() {  
    //自定义的数据库连接管理类　  
    Connection conn = DbManager.getInstance().getConnection();  
    try {  
        PreparedStatement stat = conn  
                .prepareStatement("select clobfield from t_clob where id='1'");  
        ResultSet rs = stat.executeQuery();  
        if (rs.next()) {  
            oracle.sql.CLOB clob = (oracle.sql.CLOB) rs  
                    .getClob("clobfield");  
            String value = clob.getSubString(1, (int) clob.length());  
            System.out.println("CLOB字段的值：" + value);  
        }  
        conn.commit();  
    } catch (SQLException e) {  
        e.printStackTrace();  
    }  
 
    DbManager.getInstance().closeConnection(conn);  
}  
 
/** 
 * 写入、更新CLOB字段的代码示例 
 */  
public void writeClob() {  
    //自定义的数据库连接管理类　  
    Connection conn = DbManager.getInstance().getConnection();  
    try {  
        conn.setAutoCommit(false);  
        // 1.这种方法写入CLOB字段可以。  
        PreparedStatement stat = conn  
                .prepareStatement("insert into t_clob (id,clobfield) values(sys_guid(),?)");  
        String clobContent = "This is a very very long string";  
        StringReader reader = new StringReader(clobContent);  
        stat.setCharacterStream(1, reader, clobContent.length());  
        stat.executeUpdate();  
 
        // 2.使用类似的方法进行更新CLOB字段，则不能成功　  
        // stat.close();  
        // stat =null;  
        // stat =  
        // conn.prepareStatement("update t_clob set clobfield=? where id=1");  
        // stat.setCharacterStream(1, reader, clobContent.length());  
        // stat.executeUpdate();  
 
        // 3.需要使用for update方法来进行更新，  
        // 但是，特别需要注意，如果原来CLOB字段有值，需要使用empty_clob()将其清空。  
        // 如果原来是null，也不能更新，必须是empty_clob()返回的结果。  
        stat = conn  
                .prepareStatement("select clobfield from t_clob where id='1' for update");  
        ResultSet rs = stat.executeQuery();  
        if (rs.next()) {  
            oracle.sql.CLOB clob = (oracle.sql.CLOB) rs  
                    .getClob("clobfield");  
            Writer outStream = clob.getCharacterOutputStream();  
            char[] c = clobContent.toCharArray();  
            outStream.write(c, 0, c.length);  
            outStream.flush();  
            outStream.close();  
        }  
        conn.commit();  
    } catch (SQLException | IOException e) {  
        // TODO Auto-generated catch block  
        e.printStackTrace();  
    }  
    DbManager.getInstance().closeConnection(conn);  
}  
```

##### BufferedReader方式

```java
import java.sql.*;  
import java.io.*;  
 
public class ReadClob {  
    public static void main(String[] args) {  
        PreparedStatement pstmt = null;  
        ResultSet rset = null;  
        BufferedReader reader = null;  
        Connection conn = null;  
        String driver = "oracle.jdbc.driver.OracleDriver";  
        String strUrl = "jdbc:oracle:thin@127.0.0.1:1521:ORCL";  
        try {  
            Class.forName(driver);  
            conn = DriverManager.getConnection(strUrl, "scott", "tiger");  
            pstmt = conn  
                    .prepareStatement("select v_clob form ord where ORD_id =?");  
            pstmt.setInt(1, 1);  
            rset = pstmt.executeQuery();  
            while (rset.next()) {  
                Clob clob = rset.getClob(1);// java.sql.Clob类型  
                reader = new BufferedReader(new InputStreamReader(clob  
                        .getAsciiStream()));  
                String line = null;  
                while ((line = reader.readLine()) != null) {  
                    System.out.println(line);  
                }  
            }  
        } catch (ClassNotFoundException e) {  
            e.printStackTrace();  
        } catch (SQLException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}  
```

```java
//写入操作  
String stmtString = "select v_clob form ord  where ord_id =? for update";  
pstmt = conn.prepareStatement(stmtString);  
pstmt.setInt(1, 2);  
rset = pstmt.executeQuery();  
while(rset.next()){  
    //造型为oracle.sql.CLOB  
    CLOB clob = (CLOB)rset.getClob(1);  
    String newClobDate = new String("NEW CLOOB DATE");  
    Writer writer = clob.getCharacterOutputStream();  java
    //OutStream writer = clob.getAsciiOutputStream();  
    writer.write(newClobDate);  
}  
```

#### 生成一个clob对象，通过预处理的setClob达到插入更新的目的。

##### 方法一

```java
Connection con = dbl.loadConnection();  
strSql = "insert into table1(id,a) values (1,EMPTY_CLOB())";  
dbl.executeSql(strSql);  
String str2 = "select a from table1 where id=1";  
 
ResultSet rs = dbl.openResultSet(str2);  
if(rs.next()){  
    CLOB c = ((OracleResultSet)rs).getCLOB("a");  
    c.putString(1, "长字符串");  
    String sql = "update table1 set a=? where id=1";  
    PreparedStatement pstmt = con.prepareStatement(sql);  
    pstmt.setClob(1, c);  
    pstmt.executeUpdate();  
    pstmt.close();  
}  
con.commit();  
```

##### 方法二

```java
Connection con = dbl.loadConnection();  
CLOB clob   = oracle.sql.CLOB.createTemporary(con, false,oracle.sql.CLOB.DURATION_SESSION);  
clob.putString(1,  "长字符串");  
Sql1 = "update table1 set a=? where id=1";  
PreparedStatement pst = con.prepareStatement(Sql1);  
pst.setClob(1, clob);  
pst.executeUpdate();  
pst.close();  
con.commit();  
```

