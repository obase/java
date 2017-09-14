<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<style>
<!--
.form-horizontal .control-label {
	width: 80px;
}

.form-horizontal .controls {
	height: 40px;
}

.form-horizontal input.input-normal {
	width: 200px;
}

.form-horizontal select.input-normal {
	width: 225px;
}

.form-horizontal .input-large {
	width: 400px;
}

.bui-stdmod-body {
	overflow-x: hidden;
	overflow-y: auto;
}
-->
</style>
<form id="popwinForm" class="form-horizontal">
	<div class="row">
		<div class="control-group">
			<div class="span11">
				<label class="control-label"> 主键： </label>
				<div class="controls">
					<input id="id" name="id" type="text" readonly="readonly" placeholder="自动主键,系统生成" class="input-normal control-text" style="background: #f9f9f9;" />
					<!-- must have version -->
					<input id="version" name="version" type="hidden" />
				</div>
			</div>
			<div class="span9">
				<label class="control-label" style="width: 40px;">
					名称：
				</label>
				<div class="controls">
					<input id="name" name="name" type="text" data-rules="{required:true}" placeholder="实例名称" class="input-normal control-text" />
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<div class="span11" style="white-space: nowrap;">
				<label class="control-label" style="position: relative;"><s>*</s>
					项目：
				</label>
				<div class="controls" style="position: relative;">
					<select id="projectid" name="projectid" data-rules="{required:true}" placeholder="项目" class="input-normal control-select">
						<option value="" class="default">请选择</option>
					</select>
				</div>
			</div>
			<div class="span9">
				<label class="control-label" style="width: 40px;">
					<s>*</s>
					测试:
				</label>
				<div class="controls">
					<select id="istest" name="istest" data-rules="{required:true}" class="input-small control-select">
						<option value="0">[N] 否</option>
						<option value="1">[Y] 是</option>
					</select>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<div class="span11">
				<label class="control-label">
					<s>*</s>
					类型：
				</label>
				<div class="controls">
					<select id="type" name="type" data-rules="{required:true}" class="input-normal control-select">
						<option value="D">[D] dbms</option>
						<option value="E">[E] etc</option>
						<option value="S">[S] sandbox</option>
						<option value="C">[C] cloudmysql</option>
					</select>
				</div>
			</div>
			<div class="span9">
				<label class="control-label" style="width: 40px;">
					<s>*</s>
					备份:
				</label>
				<div class="controls">
					<select id="isbackup" name="isbackup" data-rules="{required:true}" class="input-small control-select">
						<option value="0">[N] 否</option>
						<option value="1">[Y] 是</option>
					</select>
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<div class="span11">
				<label class="control-label">
					<s>*</s>
					地址:
				</label>
				<div class="controls">
					<input id="ip" name="ip" type="text" data-rules="{required:true}" placeholder="实例地址,IPv4(10.0.0.1)" class="input-normal control-text" />
				</div>
			</div>
			<div class="span9">
				<label class="control-label" style="width: 40px;">
					<s>*</s>
					端口:
				</label>
				<div class="controls">
					<input id="port" name="port" type="text" data-rules="{required:true}" placeholder="实例端口" class="input-small control-text" />
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<div class="span11">
				<label class="control-label">主机(扩展):</label>
				<div class="controls">
					<input id="host" name="host" type="text" placeholder="主机扩展信息" class="input-normal control-text" />
				</div>
			</div>
			<div class="span9">
				<span class="label label-info">实例扩展信息</span>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<div class="span11">
				<label class="control-label">VIP(扩展):</label>
				<div class="controls">
					<input id="vip" name="vip" type="text" placeholder="VIP扩展信息" class="input-normal control-text" />
				</div>
			</div>
			<div class="span9">
				<span class="label label-info">实例扩展信息</span>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<div class="span11">
				<label class="control-label">
					<s>*</s>
					主从:
				</label>
				<div class="controls">
					<select id="role" name="role" data-rules="{required:true}" class="input-normal control-select">
						<option value="M">[M] master</option>
						<option value="S">[S] slave</option>
					</select>
				</div>
			</div>
			<div class="span9">
				<span class="label label-info">选择slave请填写主实例信息</span>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<div class="span11">
				<label class="control-label">主地址:</label>
				<div class="controls">
					<input id="masterip" name="masterip" type="text" placeholder="主实例地址,IPv4(10.0.0.1)" class="input-normal control-text" />
				</div>
			</div>
			<div class="span9">
				<label class="control-label" style="width: 40px;">主端口:</label>
				<div class="controls">
					<input id="masterport" name="masterport" type="text" placeholder="主实例端口" class="input-small control-text" />
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<div class="span17">
				<label class="control-label">
					<s>*</s>
					主DBA:
				</label>
				<div class="controls">
					<input id="mdba" name="mdba" type="text" data-rules="{required:true}" placeholder="主实例DBA,多值请用逗号分隔" class="input-large control-text" />
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<div class="span17">
				<label class="control-label">
					<s>*</s>
					从DBA:
				</label>
				<div class="controls">
					<input id="sdba" name="sdba" type="text" data-rules="{required:true}" placeholder="从实例DBA,多值请用逗号分隔" class="input-large control-text" />
				</div>
			</div>
		</div>
	</div>

	<div class="row">
		<div class="control-group">
			<div class="span17">
				<label class="control-label">
					<s>*</s>
					机房:
				</label>
				<div class="controls">
					<input id="idc" name="idc" type="text" data-rules="{required:true}" placeholder="实例所属机房信息" class="input-large control-text" />
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<div class="span17">
				<label class="control-label">
					<s>*</s>
					部门:
				</label>
				<div class="controls">
					<input id="dep" name="dep" type="text" data-rules="{required:true}" placeholder="实例所属部门信息" class="input-large control-text" />
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<div class="span17">
				<label class="control-label"> 备注： </label>
				<div class="controls">
					<input id="remark" name="remark" type="text" placeholder="简单备注" class="input-large control-text" />
				</div>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<div class="span17">
				<label class="control-label"> 详情： </label>
				<div class="controls">
					<textarea id="description" name="description" placeholder="详情信息" class="control-row4 input-large"></textarea>
				</div>
			</div>
		</div>
	</div>

</form>
</div>
