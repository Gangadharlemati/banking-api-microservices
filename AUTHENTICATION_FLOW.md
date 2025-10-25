# User Service Authentication Flow

This document details the step-by-step process of user registration, authentication, and token-based authorization within the User Service. The system is designed as a stateless REST API, secured using JSON Web Tokens (JWT).

## Key Components

| Component                   | Role                                                                | Responsibility                                                                                       |
| --------------------------- | ------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------- |
| **`AuthController`**        | API Entry Point                                                     | Exposes `/register` and `/login` endpoints. Handles HTTP requests/responses. Delegates to `AuthService`.  |
| **`AuthService`**           | Business Logic Orchestrator                                         | Contains the core logic for user registration and authentication.                                    |
| **`SecurityConfig`**        | Security Rulebook                                                   | Configures the master security filter chain, defines public/private endpoints, and provides security beans. |
| **`AuthenticationManager`**   | Head of Security                                                    | A core Spring Security component that processes an authentication request.                           |
| **`UserDetailsServiceImpl`**| Records Officer                                                     | Implements `UserDetailsService` to load a user from the database by email.                         |
| **`PasswordEncoder`**       | Cryptography Specialist                                             | Hashes passwords for storage and securely compares passwords during login.                             |
| **`JwtUtils`**              | Token Factory & Inspector                                           | Generates, parses, and validates JWTs.                                                                 |
| **`AuthTokenFilter`**       | JWT Security Guard                                                  | A custom filter that runs on every request to validate the JWT and set the user's authentication.    |
| **`UserRepository`**        | The File Cabinet                                                    | Provides the data access layer to the `users` table.                                                 |
| **`User` Entity**           | The Employee File                                                   | Represents a user and also implements `UserDetails` to integrate with Spring Security.             |

---

## 1. User Registration Flow

This flow describes how a new user is created in the system.

```mermaid
sequenceDiagram
    participant Client
    participant AuthController
    participant AuthService
    participant PasswordEncoder
    participant RoleRepository
    participant UserRepository

    Client->>+AuthController: POST /api/auth/register (with RegisterRequest DTO)
    AuthController->>+AuthService: registerUser(dto)
    AuthService->>+UserRepository: existsByEmail(email)?
    UserRepository-->>-AuthService: false (Email is unique)
    AuthService->>+RoleRepository: findByName("ROLE_USER")
    RoleRepository-->>-AuthService: Role Entity
    AuthService->>+PasswordEncoder: encode(rawPassword)
    PasswordEncoder-->>-AuthService: hashedPassword
    Note over AuthService: Creates new User entity with hashed password and role.
    AuthService->>+UserRepository: save(newUser)
    UserRepository-->>-AuthService: Saved User Entity
    AuthService-->>-AuthController: void (Success)
    AuthController-->>-Client: 201 Created
```

# 2. User Login (Authentication) Flow

This flow describes how a registered user logs in and receives a JWT.

```mermaid
sequenceDiagram
    participant Client
    participant AuthController
    participant AuthService
    participant AuthenticationManager
    participant UserDetailsServiceImpl
    participant UserRepository
    participant JwtUtils

    Client->>+AuthController: POST /api/auth/login (with LoginRequest DTO)
    AuthController->>+AuthService: authenticateUser(dto)
    AuthService->>+AuthenticationManager: authenticate(UsernamePasswordAuthenticationToken)
    Note right of AuthenticationManager: Uses UserDetailsService & PasswordEncoder internally.
    AuthenticationManager->>+UserDetailsServiceImpl: loadUserByUsername(email)
    UserDetailsServiceImpl->>+UserRepository: findByEmail(email)
    UserRepository-->>-UserDetailsServiceImpl: User Entity (implements UserDetails)
    UserDetailsServiceImpl-->>-AuthenticationManager: UserDetails object
    Note over AuthenticationManager: Compares request password with user's hashed password.
    AuthenticationManager-->>-AuthService: Authentication object (user is valid)
    AuthService->>+JwtUtils: generateJwtToken(authentication)
    JwtUtils-->>-AuthService: "ey..." (JWT string)
    AuthService-->>-AuthController: JwtResponse DTO (containing token)
    AuthController-->>-Client: 200 OK (with JWT)
```

# 3. Accessing a Protected Resource (Authorization) Flow

```mermaid
sequenceDiagram
    participant Client
    participant SecurityFilterChain
    participant AuthTokenFilter
    participant JwtUtils
    participant UserDetailsServiceImpl
    participant SecurityContextHolder
    participant UserController

    Client->>+SecurityFilterChain: GET /api/users/me (Header: "Authorization: Bearer ey...")
    SecurityFilterChain->>+AuthTokenFilter: doFilterInternal()
    AuthTokenFilter->>+JwtUtils: validateJwtToken(token)
    JwtUtils-->>-AuthTokenFilter: true (Token is valid)
    AuthTokenFilter->>+JwtUtils: getUserNameFromJwtToken(token)
    JwtUtils-->>-AuthTokenFilter: "user@email.com"
    AuthTokenFilter->>+UserDetailsServiceImpl: loadUserByUsername("user@email.com")
    UserDetailsServiceImpl-->>-AuthTokenFilter: UserDetails object
    Note over AuthTokenFilter: Creates Authentication object for the user.
    AuthTokenFilter->>SecurityContextHolder: setAuthentication(authentication)
    Note over SecurityContextHolder: Current user is now authenticated for this request.
    AuthTokenFilter-->>-SecurityFilterChain: Continues filter chain
    SecurityFilterChain->>+UserController: getMyProfile()
    Note over UserController: Method executes because user is authenticated.
    UserController-->>-SecurityFilterChain: UserInfoResponse DTO
    SecurityFilterChain-->>-Client: 200 OK (with user data)
```    