window.App = {
	contextPath : '/console',
	path : function(lookupPath) {
		return App.contextPath + lookupPath;
	},
	alert : function(msg, level, func) {
		BUI.Message.Show({
			icon : level || 'info',
			msg : '<h3>' + msg + '</h3>',
			mask : false,
			buttons : [ {
				text : '确定',
				elCls : 'button button-primary',
				handler : function() {
					if (func) {
						func();
					}
					this.close();
				}
			} ]
		});
	},
	info : function(msg, func) {
		App.alert(msg, 'info', func);
	},
	warn : function(msg, func) {
		App.alert(msg, 'warning', func);
	},
	error : function(msg, func) {
		App.alert(msg, 'error', func);
	},
	confirm : function(msg, level, yesFunc, noFunc) {
		BUI.Message.Show({
			icon : level || 'question',
			msg : '<h3>' + msg + '</h3>',
			mask : false,
			buttons : [ {
				text : '确定',
				elCls : 'button button-primary',
				handler : function() {
					if (yesFunc) {
						yesFunc();
					}
					this.close();
				}
			}, {
				text : '取消',
				elCls : 'button button-primary',
				handler : function() {
					if (noFunc) {
						noFunc();
					}
					this.close();
				}
			} ]
		});
	},
	question : function(msg, func) {
		App.confirm(msg, 'question', func);
	},
	ajax : function(url, settings) {
		jQuery.ajax(url, {
			type : settings.type || 'GET',
			data : settings.data || {},
			dataType : 'json',
			cache : false,
			beforeSend : function(XHR) {

			},
			error : function(XHR, TS, ET) {
				BUI.Message.Show({
					icon : 'error',
					msg : '<h3>' + TS + ':' + ET + '</h3>',
					mask : false,
					buttons : [ {
						text : '确定',
						elCls : 'button button-primary',
						handler : function() {
							this.close();
						}
					} ]
				});
			},
			success : function(data, TS, XHR) {
				if (settings.success) {
					settings.success(data, TS, XHR);
				}
			},
			complete : function(XHR, TS) {
				// TODO
			}
		});
	},
	rest : function(method, lookupPath, data, handler) {

		var url = App.path(lookupPath);
		if (method == 'GET' || method == 'DELETE') {
			data = (typeof (data) == 'string') ? data : jQuery.param(data);
			url += '?' + data;
			data = null;
		} else {
			data = (typeof (data) == 'string') ? data : BUI.JSON.stringify(data);
		}

		App.ajax(url, {
			type : method,
			data : data,
			success : handler
		});
	},
	data : function(form) {
		if (typeof (form) == 'string') {
			return BUI.FormHelper.serializeToObject($(form)[0]);
		} else {
			return form.serializeToObject();
		}
	},
	json : function(data) {
		return BUI.JSON.stringify(data);
	},
	buiStore : function(lookupPath, params, pageSize) {
		return new BUI.Data.Store({
			url : App.path(lookupPath),
			// uniform params to page
			proxy : {
				method : 'GET',
				dataType : 'json',
			},
			params : params || {},
			pageSize : pageSize || 20,
			remoteSort : true,
			autoLoad : true
		})
	},
	buiGrid : function(settings) {
		var grid = new BUI.Grid.Grid({
			render : settings.selector,
			width : '100%',
			columns : settings.columns,
			store : settings.store,
			loadMask : true,
			tbar : {
				items : [ {
					btnCls : 'button button-small button-primary',
					text : '<i class="icon-white icon-plus"></i>添加',
					listeners : {
						'click' : settings.addClick
					}
				}, {
					btnCls : 'button button-small button-primary',
					text : '<i class="icon-white icon-pencil"></i>修改',
					listeners : {
						'click' : function() {
							var list = grid.getSelection();
							if (!list || list.length != 1) {
								App.warn('请选择一条数据!');
							} else {
								settings.updateClick(list[0]);
							}
						}
					}
				}, {
					btnCls : 'button button-small button-danger',
					text : '<i class="icon-white icon-remove"></i>删除',
					listeners : {
						'click' : function() {
							var list = grid.getSelection();
							if (!list || list.length != 1) {
								App.warn('请选择一条数据!');
							} else {
								App.question('确定删除选择的数据?', function() {
									settings.deleteClick(list[0]);
								});
							}
						}
					}
				}, {
					btnCls : 'button button-small button-info',
					text : '<i class="icon-white icon-list-alt"></i>详情',
					listeners : {
						'click' : function() {
							var list = grid.getSelection();
							if (!list || list.length != 1) {
								App.warn('请选择一条数据!');
							} else {
								settings.detailClick(list[0]);
							}
						}
					}
				} ]
			},
			bbar : {
				pagingBar : true
			},
			plugins : [ BUI.Grid.Plugins.CheckSelection ],
			listeners : {
				itemdblclick : settings.itemdblclick
			}
		});
		grid.render();
		var tbar = grid.get('tbar');
		bar1 = new BUI.Toolbar.Bar({
			elCls : 'pull-right',
			items : [ {
				xclass : 'bar-item-button',
				btnCls : 'button button-small button-warning',
				text : '<i class="icon-white icon-refresh"></i>刷新',
				listeners : {
					'click' : settings.refreshClick
				}
			} ]
		});
		tbar.addChild(bar1);
		return grid;
	},
	buiDialog : function(settings) {
		return new BUI.Overlay.Dialog({
			contentId : settings.element,
			width : settings.width,
			height : settings.height,
			mask : false,
			buttons : [ {
				text : '提交',
				elCls : 'button button-primary',
				handler : settings.submitClick
			}, {
				text : '取消',
				elCls : 'button button-primary',
				handler : function() {
					this.close();
				}
			} ],
			listeners : {
				beforeclosed : settings.beforeclosed
			}
		});
	},
	buiForm : function(settings) {
		return new BUI.Form.Form({
			srcNode : settings.selector,
			autoRender : settings.autoRender || true
		});
	},
	select : function($, selector, lookupPath, func) {
		var select = $(selector);
		select.children('option').not('.default').remove();
		App.rest('GET', lookupPath, {}, function(msg) {
			if (msg.errno) {
				App.error(msg.errmsg);
			} else {
				$.each(msg.data, function(i, v) {
					var tpl = func(v);
					$(tpl).appendTo($(select));
				})
			}
		})
	}
}
function _pad(v) {
	if (v < 10) {
		return '0' + v;
	} else {
		return '' + v;
	}
}
window.Renderer = {
	datetime : function(v, r) {
		if (v) {
			var d = new Date(v);
			return d.getFullYear() + '-' + _pad(d.getMonth() + 1) + '-' + _pad(d.getDate()) + ' ' + _pad(d.getHours()) + ':' + _pad(d.getMinutes()) + ':' + _pad(d.getSeconds());
		}
		return '';
	},
	date : function(v, r) {
		if (v) {
			var d = new Date(v);
			return d.getFullYear() + '-' + _pad(d.getMonth() + 1) + '-' + _pad(d.getDate());
		}
		return '';
	},
	time : function(v, r) {
		if (v) {
			var d = new Date(v);
			return _pad(d.getHours()) + ':' + _pad(d.getMinutes()) + ':' + _pad(d.getSeconds());
		}
		return '';
	},
	level : function(v, r) {
		var alias = {
			7 : '管理员',
			1 : '用户',
			0 : '游客'
		}
		return alias[v || 0];
	},
	instanceRole : function(v) {
		var alias = {
			'M' : 'master',
			'S' : 'slave'
		}
		return alias[v];
	},
	instanceType : function(v) {
		var alias = {
			'E' : 'etc',
			'S' : 'sandbox',
			'C' : 'cloudmysql',
			'D' : 'dbms'
		}
		return alias[v];
	},
	yesno : function(v) {
		return v ? '是' : '否';
	}
}