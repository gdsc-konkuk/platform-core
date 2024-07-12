package gdsc.konkuk.platformcore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/swagger")

public class SwaggerController {

	@GetMapping()
	public ResponseEntity<String> swaggerTest() {
		String swaggerComment = "Hi This is swagger Test Controller";
		return ResponseEntity.ok(swaggerComment);
	}

}
