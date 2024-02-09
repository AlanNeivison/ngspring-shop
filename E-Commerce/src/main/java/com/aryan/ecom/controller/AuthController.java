package com.aryan.ecom.controller;

import java.util.Optional;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aryan.ecom.dto.AuthenticationRequest;
import com.aryan.ecom.dto.SignupRequest;
import com.aryan.ecom.dto.UserDto;
import com.aryan.ecom.model.User;
import com.aryan.ecom.repository.UserRepository;
import com.aryan.ecom.services.auth.AuthService;
import com.aryan.ecom.services.jwt.UserDetailsServiceImpl;
import com.aryan.ecom.utils.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;

	private final UserDetailsServiceImpl userDetailsService;

	private final UserRepository userRepository;

	private final JwtUtil jwtUtil;

	private final String TOKEN_PREFIX = "Bearer ";

	private final String HEADER_STRING = "Authorization";

	private final AuthService authService;

	@PostMapping("/authenticate")
	public void createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
			HttpServletResponse response) {

		try {

			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));

		} catch (Exception e) {
			e.printStackTrace();
			throw new BadCredentialsException("incorrect user or pass");
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		Optional<User> user = userRepository.findFirstByEmail(userDetails.getUsername());
		final String jwt = jwtUtil.generateToken(userDetails.getUsername());

		if (user.isPresent()) {
			try {
				response.getWriter().write(new JSONObject().put("userId", user.get().getId())
						.put("role", user.get().getRole()).toString());
				response.addHeader("Access-Control-Expose-Headers", "Authorization");
				response.addHeader("Access-Control-Allow-Headers", "Authorization, X-PINGOTHER, Origin, "
						+ "X-Requested-With, Content-Type, Accent, X-Custom-header");
			} catch (Exception e) {
			}
			response.addHeader(HEADER_STRING, TOKEN_PREFIX + jwt);
		}

	}

	@PostMapping("/sign-up")
	public ResponseEntity<?> signUpUser(@RequestBody SignupRequest signupRequest) {
		if (authService.hasUserWithEmail(signupRequest.getEmail())) {
			return new ResponseEntity<>("user already exists", HttpStatus.NOT_ACCEPTABLE);
		}
		UserDto userDto = authService.createUser(signupRequest);
		return new ResponseEntity<>(userDto, HttpStatus.OK);
	}

}
