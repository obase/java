package com.github.obase.data;

public interface DataErrno {

	String SOURCE = "MYSQL";
	int __ = 0x70000; // Mysql相关错误的起始值.每个系统保留16bit的错误编号.

	int JSON_CODEC_FAILED = __ | 1; // JSON编码错误
	int CSV_CODEC_FAILED = __ | 2; // JSON编码错误
	int XML_CODEC_FAILED = __ | 3; // JSON编码错误

}
