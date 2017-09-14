BUI.use([], function(Cookie, Menu, Tab , Select) {

    var sideBarMenu = $('#SideBarMenu'),
		contentHeader =  $('#ContentHeader'),
		contentFrame = $('#ContentFrame'),
		navPage = getNavPage(),
        selectedItem = null;

    sideBarMenu.delegate('a[href!="#"]', 'click', function(e) {
        e.preventDefault();
        var href = $(this).attr('href');
        $('li', sideBarMenu).removeClass('active');
        $('li.treeview', sideBarMenu).removeClass('selected');
        selectedItem = $(this).parent('li').addClass('active selected');
        selectedItem.parents('li.treeview').addClass('active selected');
		resetTitle(selectedItem, selectedItem.parents('li.treeview'));
        resetFrameSrc(href);
		setNavPage(href);
        $(window).scrollTop(0);
    });
    // 页面加载完后根据hash值设置当前选中项
    if (navPage) {
        selectedItem = sideBarMenu.find('a[href="' + navPage + '"]').parent();
    }
    selectedItem = selectedItem || $('a#indexPage', sideBarMenu).parent();
    selectedItem.find('a:first').trigger('click').parents('li.treeview').find('a:first').trigger('click');

    function resetFrameSrc(href) {
        contentFrame.attr('src', href);
        setNavPage(href);
    }

    function resetTitle(li, parentLi) {
        var title = (parentLi.length ? $('a:first', parentLi).text() + '<i class="fa fa-angle-double-right"></i>' : '') + $('a:first', li).text();
        contentHeader.html(title);
    }

    //更改地址栏连接
    function setNavPage(href) {
        location.hash = href || '';
    }

    function getNavPage() {
    	var str = location.hash;
    	return str ? str.substring(1) : '';
    }
});