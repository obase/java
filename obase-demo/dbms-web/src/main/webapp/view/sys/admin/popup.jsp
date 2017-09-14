<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<style>
<!--
.form-horizontal .control-label {
	width: 80px;
}

.form-horizontal .controls {
	height: 40px;
}

input.input-normal {
	width: 200px;
}
-->
</style>
<form id="popwinForm" class="form-horizontal">
	<div class="row">
		<div class="control-group">
			<label class="control-label">
				<s>*</s>
				通行证：
			</label>
			<div class="controls">
				<input id="id" name="id" type="text" data-rules="{required:true}" placeholder="通行证" class="input-normal control-text" />
				<!-- must have version -->
				<input id="version" name="version" type="hidden" />
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label"> 实名： </label>
			<div class="controls">
				<input id="realname" name="realname" type="text" placeholder="个人实名" class="input-normal control-text" />
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label"> 邮箱： </label>
			<div class="controls">
				<input id="email" name="email" type="text" placeholder="公司邮箱" class="input-normal control-text">
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label"> 手机： </label>
			<div class="controls">
				<input id="phone" name="phone" type="text" placeholder="个人手机" class="input-normal control-text">
			</div>
		</div>
	</div>
	<div class="row">
		<div class="control-group">
			<label class="control-label"> 等级： </label>
			<div class="controls">
				<select id="level" name="level" class="input-normal">
					<option value="0">游客</option>
					<option value="1">用户</option>
					<option value="7">管理员</option>
				</select>
			</div>
		</div>
	</div>
</form>