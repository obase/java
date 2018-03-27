package demo.test.contoller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.obase.webc.Kits;
import com.github.obase.webc.Webc;
import com.github.obase.webc.annotation.ServletController;
import com.github.obase.webc.annotation.ServletMethod;

@ServletController(path = Webc.$)
public class IndexController {

	@ServletMethod(path = Webc.$, csrf = false)
	public void index(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Kits.writePlain(response, "你已经成功访问了此页面!");
	}

}
