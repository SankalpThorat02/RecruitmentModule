package com.sankalp.prototype.service;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository sessionRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RsaCryptoService rsaCryptoService;

    @Autowired
    private EmployeeRepository employeeRepository;


    @Value("${app.session.timeout-minutes}")
    private long sessionTimeoutMinutes;

    public ApiResponse<LoginResponse> login(LoginRequest request, HttpServletRequest httpRequest) {

        String email = request.getEmail();
//		String hashedPassword = passwordEncoder.encode("Rahsha@123");
//		System.out.println(hashedPassword);
        String decryptedPassword = rsaCryptoService.decrypt(request.getPassword());

        String targetId;
        String targetPassword;
        Integer targetRoleId;

        log.info("[AUDIT] Checking login credentials for email={}", request.getEmail());

        if(email != null && email.toLowerCase().endsWith("@concertosoft.com")) {
            log.trace("[TRACE] Internal Domain. Checking concerto_employees table");
            Optional<Employee> employeeOptional = employeeRepository.findByEmail(email);

            if(employeeOptional.isEmpty()) {
                log.error("[ERROR] Login failed. User not found for email={}", request.getEmail());
                throw new RuntimeException("User Not Found");
            }

            Employee employee = employeeOptional.get();

            targetId = employee.getId();
            targetPassword = employee.getPassword();
            targetRoleId = employee.getRoleId();

        } else {
            log.trace("[TRACE] External Domain. Checking recrut_registration table");
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

            if(userOptional.isEmpty()) {
                log.error("[ERROR] Login failed. User not found for email={}", request.getEmail());
                throw new RuntimeException("User Not Found");
            }

            User user =userOptional.get();
            targetId = String.valueOf(user.getId());
            targetPassword = user.getPassword();
            targetRoleId = user.getRoleId();

        }

        if (!passwordEncoder.matches(decryptedPassword, targetPassword)) {
            log.error("[ERROR] Login failed. Invalid password for userId={}", targetId);
            throw new RuntimeException("Invalid password");
        }


        // 2. Check active session
        List<UserSession> activeSessions = sessionRepo.findActiveByUserId(Long.valueOf(targetId));
        log.trace("[TRACE] Active session count for userId={} is {}", targetId, activeSessions.size());
        if (!activeSessions.isEmpty()) {
            log.info("[AUDIT] Active session already exists for userId={}", targetId);

            // IMPORTANT: don't login yet
//	            throw new RuntimeException("ALREADY_LOGGED_IN");
            return new ApiResponse<>("ALREADY_LOGGED_IN", "ALREADY_LOGGED_IN",
                    LoginResponse.builder()
                            .userId(Long.valueOf(targetId))
                            .emailId(email)
                            .roleId(targetRoleId)
                            .build());
        }

        // 3. Create new session
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("USER_ID", targetId);
        session.setAttribute("ROLE_ID",targetRoleId);
        log.info("[AUDIT] New session created for userId={} sessionId={}", targetId, session.getId());

        UserSession us = new UserSession();
        us.setUserId(Long.valueOf(targetId));
        us.setSessionId(session.getId());
        us.setLoginTime(LocalDateTime.now());
        us.setLastActivityTime(LocalDateTime.now());
        us.setIsActive(1);

        sessionRepo.save(us);
        log.info("[AUDIT] Session saved as active for userId={} sessionId={}", targetId, session.getId());

        //return actual data
        return new ApiResponse<>("SUCCESS", "Login Sucessful", LoginResponse.builder()
                .userId(Long.valueOf(targetId))
                .emailId(email)
                .roleId(targetRoleId)
                .sessionId(session.getId())
                .sessionTimeoutMinutes(this.sessionTimeoutMinutes)
                .build());
    }

    @Transactional
    public LoginResponse forceLogin(Long userId ,HttpServletRequest request) {

        // 1. deactivate old sessions
        sessionRepo.deactivateByUserId(userId);
        log.info("[AUDIT] Old sessions deactivated for userId={}", userId);

        String targetEmail;
        Integer targetRoleId;

        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isPresent()) {
            log.trace("[TRACE] Force login ID recognized as Candidate");
            User user = userOptional.get();
            targetEmail = user.getEmail();
            targetRoleId = user.getRoleId();
        } else {
            log.trace("[TRACE] Force login ID not in Users, checking employees");
            Employee employee = employeeRepository.findById(String.valueOf(userId))
                    .orElseThrow(() -> {
                        log.error("[ERROR] Force login failed. User not found for userId={}", userId);
                        return new RuntimeException("User not found");
                    });

            targetEmail = employee.getEmail();
            targetRoleId = employee.getRoleId();
        }

//	        User user = userRepository.findById(userId)
//	                .orElseThrow(() -> {
//	                	log.error("[ERROR] Force login failed. User not found for userId={}", userId);
//	                	return new RuntimeException("User not found");
//	                });
        HttpSession existingSession = request.getSession(false);

        if (existingSession != null) {
            log.info("[AUDIT] Invalidating existing HTTP session before force login. sessionId={}",
                    existingSession.getId());

            existingSession.invalidate();
        }

        // 2. create new session
        HttpSession session = request.getSession(true);
        session.setAttribute("USER_ID", userId);
        session.setAttribute("ROLE_ID", targetRoleId);
        log.info("[AUDIT] Force login created new session for userId={} sessionId={}", userId, session.getId());

        UserSession us = new UserSession();
        us.setUserId(userId);
        us.setSessionId(session.getId());
        us.setLoginTime(LocalDateTime.now());
        us.setLastActivityTime(LocalDateTime.now());
        us.setIsActive(1);

        sessionRepo.save(us);
        log.info("[AUDIT] Force login session saved as active for userId={} sessionId={}", userId, session.getId());



        return LoginResponse.builder()
                .userId(userId)
                .emailId(targetEmail)
                .roleId(targetRoleId)
                .sessionId(session.getId())
                .sessionTimeoutMinutes(this.sessionTimeoutMinutes)
                .build();
    }

    public void logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            log.error("[ERROR] Logout failed. No active HTTP session found");
            throw new RuntimeException("No active session found");
        }

        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            log.error("[ERROR] Logout failed. USER_ID missing for sessionId={}", session.getId());
            throw new RuntimeException("Invalid session");
        }

        sessionRepo.deactivateByUserId(userId);
        log.info("[AUDIT] User logged out. userId={} sessionId={}", userId, session.getId());
        session.invalidate();
    }



    public ResponseEntity<?> checkActiveSession(HttpServletRequest request){

        HttpSession session = request.getSession(false);

        if (session == null) {
            log.info("[AUDIT] Session validation failed. No HTTP session found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String sessionId = session.getId();
        log.trace("[TRACE] Validating sessionId={}", sessionId);
        Optional<UserSession> validSession =
                sessionRepo.findActiveBySessionId(sessionId);


        if (validSession.isEmpty()) {
            log.info("[AUDIT] Session validation failed. Inactive or missing sessionId={}", sessionId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.trace("[TRACE] Session validation successful for sessionId={}", sessionId);
        return ResponseEntity.ok().build();
    }



}

