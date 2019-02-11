package gov.va.health.api.sentinel.mockcdw;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 * The mock connection that supports calling the prc_Entity_Return procedure.
 *
 * <p>See {@link MockEntityReturnDriver}
 */
@RequiredArgsConstructor(staticName = "of")
class MockEntityReturnCallableStatement implements CallableStatement {

  /** Responses will be used to find XML samples. */
  private final MockResponses responses;

  /** This call object will be populated by `setObject(...) calls. */
  private final MockCall call = new MockCall();

  @Override
  public void addBatch(String sql) throws SQLException {
    throw notSupported();
  }

  @Override
  public void addBatch() throws SQLException {
    throw notSupported();
  }

  @Override
  public void cancel() {
    /* noop */
  }

  @Override
  public void clearBatch() throws SQLException {
    throw notSupported();
  }

  @Override
  public void clearParameters() throws SQLException {
    throw notSupported();
  }

  @Override
  public void clearWarnings() throws SQLException {
    throw notSupported();
  }

  @Override
  public void close() {
    /* noop */
  }

  @Override
  public void closeOnCompletion() {
    /* noop */
  }

  @Override
  public boolean execute(String sql) throws SQLException {
    throw notSupported();
  }

  @Override
  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
    throw notSupported();
  }

  @Override
  public boolean execute(String sql, int[] columnIndexes) throws SQLException {
    throw notSupported();
  }

  @Override
  public boolean execute(String sql, String[] columnNames) throws SQLException {
    throw notSupported();
  }

  @Override
  public boolean execute() throws SQLException {
    throw notSupported();
  }

  @Override
  public int[] executeBatch() throws SQLException {
    throw notSupported();
  }

  @Override
  public ResultSet executeQuery() throws SQLException {
    throw notSupported();
  }

  @Override
  public ResultSet executeQuery(String sql) throws SQLException {
    throw notSupported();
  }

  @Override
  public int executeUpdate(String sql) throws SQLException {
    throw notSupported();
  }

  @Override
  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    throw notSupported();
  }

  @Override
  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
    throw notSupported();
  }

  @Override
  public int executeUpdate(String sql, String[] columnNames) throws SQLException {
    throw notSupported();
  }

  @Override
  public int executeUpdate() {
    return 0;
  }

  @Override
  public Array getArray(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public Array getArray(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  @Deprecated
  public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
    throw notSupported();
  }

  @Override
  public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public BigDecimal getBigDecimal(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public Blob getBlob(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public Blob getBlob(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public boolean getBoolean(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public boolean getBoolean(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public byte getByte(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public byte getByte(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public byte[] getBytes(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public byte[] getBytes(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public Reader getCharacterStream(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public Reader getCharacterStream(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public Clob getClob(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public Clob getClob(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public Connection getConnection() throws SQLException {
    throw notSupported();
  }

  @Override
  public Date getDate(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
    throw notSupported();
  }

  @Override
  public Date getDate(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public Date getDate(String parameterName, Calendar cal) throws SQLException {
    throw notSupported();
  }

  @Override
  public double getDouble(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public double getDouble(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public int getFetchDirection() throws SQLException {
    throw notSupported();
  }

  @Override
  public void setFetchDirection(int direction) throws SQLException {
    throw notSupported();
  }

  @Override
  public int getFetchSize() throws SQLException {
    throw notSupported();
  }

  @Override
  public void setFetchSize(int rows) throws SQLException {
    throw notSupported();
  }

  @Override
  public float getFloat(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public float getFloat(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public ResultSet getGeneratedKeys() throws SQLException {
    throw notSupported();
  }

  @Override
  public int getInt(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public int getInt(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public long getLong(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public long getLong(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public int getMaxFieldSize() {
    return 0;
  }

  @Override
  public void setMaxFieldSize(int max) {
    /* noop */
  }

  @Override
  public int getMaxRows() {
    return 0;
  }

  @Override
  public void setMaxRows(int max) {
    /* noop */
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    throw notSupported();
  }

  @Override
  public boolean getMoreResults() {
    return false;
  }

  @Override
  public boolean getMoreResults(int current) {
    return false;
  }

  @Override
  public Reader getNCharacterStream(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public Reader getNCharacterStream(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public NClob getNClob(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public NClob getNClob(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public String getNString(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public String getNString(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public Object getObject(int parameterIndex) throws SQLException {
    if (parameterIndex != 7) {
      throw new SQLException("Only parameter 7 (Response XML) is supported");
    }
    return MockEntityReturnClob.of(responses.responseFor(call));
  }

  @Override
  public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
    throw notSupported();
  }

  @Override
  public Object getObject(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
    throw notSupported();
  }

  @Override
  public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
    throw notSupported();
  }

  @Override
  public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
    throw notSupported();
  }

  @Override
  public ParameterMetaData getParameterMetaData() throws SQLException {
    throw notSupported();
  }

  @Override
  public int getQueryTimeout() {
    return 0;
  }

  @Override
  public void setQueryTimeout(int seconds) {
    /* noop */
  }

  @Override
  public Ref getRef(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public Ref getRef(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public ResultSet getResultSet() throws SQLException {
    throw notSupported();
  }

  @Override
  public int getResultSetConcurrency() {
    return ResultSet.CONCUR_READ_ONLY;
  }

  @Override
  public int getResultSetHoldability() {
    return 0;
  }

  @Override
  public int getResultSetType() {
    return ResultSet.TYPE_FORWARD_ONLY;
  }

  @Override
  public RowId getRowId(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public RowId getRowId(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public SQLXML getSQLXML(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public SQLXML getSQLXML(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public short getShort(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public short getShort(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public String getString(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public String getString(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public Time getTime(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
    throw notSupported();
  }

  @Override
  public Time getTime(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public Time getTime(String parameterName, Calendar cal) throws SQLException {
    throw notSupported();
  }

  @Override
  public Timestamp getTimestamp(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
    throw notSupported();
  }

  @Override
  public Timestamp getTimestamp(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
    throw notSupported();
  }

  @Override
  public URL getURL(int parameterIndex) throws SQLException {
    throw notSupported();
  }

  @Override
  public URL getURL(String parameterName) throws SQLException {
    throw notSupported();
  }

  @Override
  public int getUpdateCount() {
    return 0;
  }

  @Override
  public SQLWarning getWarnings() {
    return null;
  }

  @Override
  public boolean isCloseOnCompletion() {
    return false;
  }

  @Override
  public boolean isClosed() {
    return true;
  }

  @Override
  public boolean isPoolable() {
    return false;
  }

  @Override
  public void setPoolable(boolean poolable) throws SQLException {
    throw notSupported();
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) {
    return false;
  }

  private SQLException notSupported() {
    return new SQLException("functionality not supported in mock CDW driver");
  }

  @Override
  public void registerOutParameter(int parameterIndex, int sqlType) {
    /* noop */
  }

  @Override
  public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
    throw notSupported();
  }

  @Override
  public void registerOutParameter(int parameterIndex, int sqlType, String typeName)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
    throw notSupported();
  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType, int scale)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void registerOutParameter(String parameterName, int sqlType, String typeName)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void setArray(int parameterIndex, Array x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x, long length)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBlob(int parameterIndex, Blob x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream, long length)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBlob(String parameterName, InputStream inputStream, long length)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBlob(String parameterName, Blob x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBoolean(int parameterIndex, boolean x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBoolean(String parameterName, boolean x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setByte(int parameterIndex, byte x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setByte(String parameterName, byte x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBytes(int parameterIndex, byte[] x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setBytes(String parameterName, byte[] x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, int length)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader, long length)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader, int length)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader, long length)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setClob(int parameterIndex, Clob x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setClob(int parameterIndex, Reader reader) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setClob(String parameterName, Reader reader, long length) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setClob(String parameterName, Clob x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setClob(String parameterName, Reader reader) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setCursorName(String name) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setDate(int parameterIndex, Date x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setDate(String parameterName, Date x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setDouble(int parameterIndex, double x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setDouble(String parameterName, double x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setEscapeProcessing(boolean enable) {
    /* noop */
  }

  @Override
  public void setFloat(int parameterIndex, float x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setFloat(String parameterName, float x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setInt(int parameterIndex, int x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setInt(String parameterName, int x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setLong(int parameterIndex, long x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setLong(String parameterName, long x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value, long length)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNCharacterStream(String parameterName, Reader value, long length)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNClob(int parameterIndex, NClob value) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNClob(String parameterName, NClob value) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNClob(String parameterName, Reader reader) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNString(int parameterIndex, String value) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNString(String parameterName, String value) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNull(int parameterIndex, int sqlType) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNull(String parameterName, int sqlType) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType) {
    switch (parameterIndex) {
      case 4:
        call.count((Integer) x);
        break;
      case 5:
        call.page((Integer) x);
        break;
      case 6:
        call.fhirString((String) x);
        break;
      default:
        /* ignore */
        break;
    }
  }

  @Override
  public void setObject(int parameterIndex, Object x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void setObject(String parameterName, Object x, int targetSqlType, int scale)
      throws SQLException {
    throw notSupported();
  }

  @Override
  public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setObject(String parameterName, Object x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setRef(int parameterIndex, Ref x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setRowId(int parameterIndex, RowId x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setRowId(String parameterName, RowId x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setShort(int parameterIndex, short x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setShort(String parameterName, short x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setString(int parameterIndex, String x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setString(String parameterName, String x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setTime(int parameterIndex, Time x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setTime(String parameterName, Time x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setURL(int parameterIndex, URL x) throws SQLException {
    throw notSupported();
  }

  @Override
  public void setURL(String parameterName, URL val) throws SQLException {
    throw notSupported();
  }

  @Override
  @Deprecated
  public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
    throw notSupported();
  }

  @Override
  public <T> T unwrap(Class<T> iface) {
    return null;
  }

  @Override
  public boolean wasNull() {
    return false;
  }
}
