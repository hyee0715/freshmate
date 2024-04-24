package com.icebox.freshmate.global.docs;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocsViewController {

	@GetMapping("/docs")
	public String getDocs() {
		return "docs/docs-home";
	}
}
