package com.procurement.system.construction.industry;

import com.procurement.system.construction.industry.common.CommonFunctions;
import com.procurement.system.construction.industry.common.ResponseMessage;
import com.procurement.system.construction.industry.dto.UserDTO;
import com.procurement.system.construction.industry.entity.User;
import com.procurement.system.construction.industry.enums.UserRole;
import com.procurement.system.construction.industry.exception.ConflictException;
import com.procurement.system.construction.industry.repository.UserRepository;
import com.procurement.system.construction.industry.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@SpringBootApplication
@RestController
@RequiredArgsConstructor
@Tag(name = "Startup Controller")
public class ConstructionIndustryApplication {
	private final UserRepository userRepository;
	private final UserService userService;
	private final CommonFunctions commonFunctions;

	@Value("${server.port}")
	private int serverPort;

	public static void main(String[] args) {
		SpringApplication.run(ConstructionIndustryApplication.class, args);
	}

	@Operation(summary = "Check Application Status", description = "Check if the application is running.")
	@GetMapping("/")
	public String testMessage(){
		return "Application Running Well on Port " + serverPort;
	}

	@Operation(summary = "Create Super Admin Account", description = "Create a super admin account if one does not already exist.")
	@PostMapping("/super/admin")
	public ResponseEntity<ResponseMessage> superAdmin() throws ConflictException {
		Optional<User> admin = userRepository.findFirstByRole(UserRole.valueOf("ADMIN"));

		if(admin.isEmpty()){
			userService.addUser(UserDTO.builder()
					.firstName("super")
					.lastName("admin")
					.email("admin@gmail.com")
					.mobileNumber("0000000000")
					.password("1234")
					.role(UserRole.ADMIN)
					.isActive(true).build());
		}else{
			throw new ConflictException("Admin account already exists in the system");
		}

		return commonFunctions.successResponse("Admin account has been created successfully");
	}
}
