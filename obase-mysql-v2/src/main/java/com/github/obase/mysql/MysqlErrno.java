package com.github.obase.mysql;

/**
 * Should be [
 */
public interface MysqlErrno {

	String SOURCE = "MYSQL";
	int __ = 0x10000; // Mysql相关错误的起始值.每个系统保留16bit的错误编号.
	int META_INFO_EXT_FAILED = __ | 1; // 元数据析提取失败
	int META_INFO_NOT_FOUND = __ | 2; // 元数据找不到
	int META_INFO_DUBLICATE_TABLE = __ | 3; // 配置类重复
	int JDBC_META_NOT_SUPPORTED = __ | 4; // JDBC ACTION不支持
	int SQL_CONFIG_NOT_FOUND = __ | 5; // Sql配置找不到
	int SQL_CONFIG_DUPLICATE = __ | 6; // 配置重复

	int SQL_CONFIG_EXCEED_PARAMS = __ | 7; // 配置过多参数, 例如foreach, isnull, notnull只允许一个参数
}
