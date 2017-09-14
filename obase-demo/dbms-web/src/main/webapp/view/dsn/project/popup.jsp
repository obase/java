<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<style>
<!--
.form-horizontal .control-label {
	width: 80px;
}

.form-horizontal .controls {
	height: 40px;
}

.form-horizontal .input-normal {
	width: 200px;
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
				类型：
			</label>
			<div class="controls">
				<input id="projecttype" name="projecttype" type="text" data-rules="{required:true}" placeholder="项目类型" class="input-normal control-text" />
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label">
				<s>*</s>
				代号：
			</label>
			<div class="controls">
				<input id="projectcode" name="projectcode" type="text" data-rules="{required:true}" placeholder="项目代号" class="input-normal control-text" />
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label">
				<s>*</s>
				名称：
			</label>
			<div class="controls">
				<input id="name" name="name" type="text" data-rules="{required:true}" placeholder="项目名称" class="input-large control-text" />
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label"> 管理员： </label>
			<div class="controls">
				<input id="admin" name="admin" type="text" placeholder="项目管理员,多值请用逗号分隔" class="input-large control-text" />
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label"> 项目成员： </label>
			<div class="controls">
				<input id="member" name="member" type="text" placeholder="项目成员,多值请用逗号分隔" class="input-large control-text" />
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label"> 项目运维： </label>
			<div class="controls">
				<input id="op" name="op" type="text" placeholder="项目运维,多值请用逗号分隔" class="input-large control-text" />
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label"> 位置： </label>
			<div class="controls">
				<input id="scm" name="scm" type="text" placeholder="源码位置" class="input-large control-text" />
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