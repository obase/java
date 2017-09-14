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
-->
</style>
<form id="popwinForm" class="form-horizontal">
	<div class="row">
		<div class="control-group">
			<label class="control-label"> 主键： </label>
			<div class="controls">
				<input id="id" name="id" type="text" readonly="readonly" placeholder="自动主键,系统生成" class="input-normal control-text" style="background: #f9f9f9;" />
				<!-- must have version -->
				<input id="version" name="version" type="hidden" />
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label">
				<s>*</s>
				项目：
			</label>
			<div class="controls">
				<select id="projectid" name="projectid" data-rules="{required:true}" placeholder="项目" class="input-normal control-select">
					<option value="" class="default">请选择</option>
				</select>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label">
				<s>*</s>
				实例：
			</label>
			<div class="controls">
				<select id="instanceid" name="instanceid" data-rules="{required:true}" placeholder="实例" class="input-normal control-select">
					<option value="" class="default">请选择</option>
				</select>
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label">
				<s>*</s>
				数据库：
			</label>
			<div class="controls">
				<input id="dbname" name="dbname" type="text" data-rules="{required:true}" placeholder="数据库名称" class="input-normal control-text" />
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label"> 备注： </label>
			<div class="controls">
				<input id="remark" name="remark" type="text" placeholder="简单备注" class="input-large control-text" />
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label"> 详情： </label>
			<div class="controls">
				<textarea id="description" name="description" placeholder="详情信息" class="control-row4 input-large"></textarea>
			</div>
		</div>
	</div>
</form>