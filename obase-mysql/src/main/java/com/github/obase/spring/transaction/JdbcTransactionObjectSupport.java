package com.github.obase.spring.transaction;

import java.sql.SQLException;
import java.sql.Savepoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.NestedTransactionNotSupportedException;
import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.TransactionUsageException;
import org.springframework.transaction.support.SmartTransactionObject;

public abstract class JdbcTransactionObjectSupport implements SavepointManager, SmartTransactionObject {

	private static final Log logger = LogFactory.getLog(JdbcTransactionObjectSupport.class);

	private ConnectionHolder connectionHolder;

	private Integer previousIsolationLevel;

	private boolean savepointAllowed = false;

	public void setConnectionHolder(ConnectionHolder connectionHolder) {
		this.connectionHolder = connectionHolder;
	}

	public ConnectionHolder getConnectionHolder() {
		return this.connectionHolder;
	}

	public boolean hasConnectionHolder() {
		return (this.connectionHolder != null);
	}

	public void setPreviousIsolationLevel(Integer previousIsolationLevel) {
		this.previousIsolationLevel = previousIsolationLevel;
	}

	public Integer getPreviousIsolationLevel() {
		return this.previousIsolationLevel;
	}

	public void setSavepointAllowed(boolean savepointAllowed) {
		this.savepointAllowed = savepointAllowed;
	}

	public boolean isSavepointAllowed() {
		return this.savepointAllowed;
	}

	@Override
	public void flush() {
		// no-op
	}

	@Override
	public Object createSavepoint() throws TransactionException {
		ConnectionHolder conHolder = getConnectionHolderForSavepoint();
		try {
			if (!conHolder.supportsSavepoints()) {
				throw new NestedTransactionNotSupportedException("Cannot create a nested transaction because savepoints are not supported by your JDBC driver");
			}
			return conHolder.createSavepoint();
		} catch (SQLException ex) {
			throw new CannotCreateTransactionException("Could not create JDBC savepoint", ex);
		}
	}

	@Override
	public void rollbackToSavepoint(Object savepoint) throws TransactionException {
		ConnectionHolder conHolder = getConnectionHolderForSavepoint();
		try {
			conHolder.getConnection().rollback((Savepoint) savepoint);
		} catch (Throwable ex) {
			throw new TransactionSystemException("Could not roll back to JDBC savepoint", ex);
		}
	}

	@Override
	public void releaseSavepoint(Object savepoint) throws TransactionException {
		ConnectionHolder conHolder = getConnectionHolderForSavepoint();
		try {
			conHolder.getConnection().releaseSavepoint((Savepoint) savepoint);
		} catch (Throwable ex) {
			logger.debug("Could not explicitly release JDBC savepoint", ex);
		}
	}

	protected ConnectionHolder getConnectionHolderForSavepoint() throws TransactionException {
		if (!isSavepointAllowed()) {
			throw new NestedTransactionNotSupportedException("Transaction manager does not allow nested transactions");
		}
		if (!hasConnectionHolder()) {
			throw new TransactionUsageException("Cannot create nested transaction if not exposing a JDBC transaction");
		}
		return getConnectionHolder();
	}

}
