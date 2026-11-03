# Simple Login Program - Implementation

This document describes the complete authentication and login system implemented in the ProjectFlow application.

## Overview

ProjectFlow implements a full-stack authentication system with JWT-based security, featuring a modern React login interface backed by a Spring Boot API with Spring Security.

## Technology Stack

### Frontend
- **Framework**: React 18 with TypeScript
- **Form Validation**: react-hook-form + Zod
- **State Management**: React Context API
- **HTTP Client**: Axios with interceptors
- **Styling**: Tailwind CSS
- **Routing**: React Router DOM

### Backend
- **Framework**: Spring Boot 3.3
- **Security**: Spring Security with JWT (JJWT)
- **Database**: MySQL 8
- **Authentication**: Stateless JWT tokens
- **Password Hashing**: BCrypt

## Architecture

### Login Flow

```
User Input (Email/Password)
    ↓
React LoginPage Form Component
    ↓
Validation (Zod Schema)
    ↓
AuthAPI.login() → POST /api/auth/login
    ↓
Backend AuthController.login()
    ↓
AuthService: Verify credentials & Generate JWT
    ↓
Return: { token, user }
    ↓
TokenStore: Save JWT to localStorage
    ↓
AuthContext: Update global auth state
    ↓
Router: Redirect to dashboard
```

## Frontend Components

### 1. LoginPage (`frontend/src/pages/LoginPage.tsx`)
- Main login interface
- Email and password form inputs
- Real-time field validation
- Error handling and display
- Loading state during submission
- Redirect to dashboard on success
- Link to register page for new users

**Features:**
- Prevents logged-in users from accessing the login page
- Displays validation errors for invalid email/password format
- Shows authentication errors (invalid credentials)
- Smooth UX with loading indicators

### 2. AuthContext (`frontend/src/frontend/src/context/AuthContext.tsx`)
- Global authentication state management
- User session persistence
- Token storage and retrieval
- Login/Logout/Register methods
- Session restoration on page reload

**Key Functionality:**
- Automatically restores session from saved JWT token
- Handles unauthorized responses (clears session)
- Provides `useAuth()` hook for components

### 3. API Client (`frontend/src/api/auth.ts`)
- Centralized authentication API calls
- Token injection in request headers
- Error parsing and normalization
- Automatic retry on token expiration

**Endpoints:**
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - New account creation
- `GET /api/auth/me` - Get current user details

### 4. Protected Route (`frontend/src/routes/ProtectedRoute.tsx`)
- Ensures only authenticated users access protected pages
- Redirects to login if session is invalid
- Waits for auth state to load before rendering

### 5. Input Component (`frontend/src/components/common/Input.tsx`)
- Reusable form input field
- Label, error message, and validation support
- Accessibility features (labels, ARIA attributes)
- Tailwind styling

### 6. Button Component (`frontend/src/components/common/Button.tsx`)
- Reusable form button
- Loading state with spinner
- Disabled state handling
- Accessible button markup

### 7. ErrorAlert Component (`frontend/src/components/common/ErrorAlert.tsx`)
- Displays error messages to user
- Dismissible with smooth animation
- Visual error styling

## Backend Implementation

### 1. AuthController (`backend/src/main/java/com/projectflow/controller/AuthController.java`)
- `POST /api/auth/login` - Authenticate user
- `POST /api/auth/register` - Create new user account
- `GET /api/auth/me` - Get authenticated user details

### 2. AuthService (`backend/src/main/java/com/projectflow/service/AuthService.java`)
- Credential verification against database
- JWT token generation
- User registration with role assignment
- Current user retrieval

### 3. Security Configuration (`backend/src/main/java/com/projectflow/config/SecurityConfig.java`)
- JWT filter integration
- CORS configuration
- Protected endpoint configuration
- Public endpoint whitelist (login, register)

### 4. JWT Processing
- **JwtTokenProvider**: Token generation, validation, claims extraction
- **JwtAuthenticationFilter**: Request interception and token validation
- **UserPrincipal**: Security context user representation

### 5. Data Models
- **User**: Username, email, password hash, role, timestamps
- **Role**: ADMIN, PROJECT_MANAGER, MEMBER
- **AuthResponse**: JWT token + User details DTO
- **LoginRequest**: Email + Password validation DTO

## Validation

### Frontend Validation (Zod Schemas)

**Login Form:**
```typescript
{
  email: string (valid email format)
  password: string (required)
}
```

**Registration Form:**
```typescript
{
  name: string (1-100 characters)
  email: string (valid email format, max 255)
  password: string (8-72 chars, must include letter + digit)
}
```

### Backend Validation
- Spring Validation annotations on DTO fields
- Email uniqueness check for registration
- Password strength requirements
- Request body validation with detailed error responses

## Security Features

1. **Password Security**
   - BCrypt hashing with salt
   - Strong password requirements for registration
   - No plaintext passwords in logs or responses

2. **Token Security**
   - JWT signed with strong secret (configurable via `JWT_SECRET`)
   - Token expiration (default 24 hours)
   - Tokens stored in browser localStorage
   - XSS protection: user content rendered as text only

3. **Access Control**
   - Server-side authorization checks on every request
   - Role-based access control (RBAC)
   - Token validation on protected endpoints
   - Automatic logout on token expiration

4. **CORS Protection**
   - Whitelist of allowed origins (configurable)
   - Only specified domains can call the API

## User Roles

| Role | Default Permissions |
|------|-------------------|
| **ADMIN** | All operations, user management, role changes |
| **PROJECT_MANAGER** | Create projects, manage own projects and members |
| **MEMBER** | View assigned projects, manage own tasks, comment |

New registrations default to MEMBER role. Admins can promote users via the Users page.

## Environment Configuration

### Frontend
- `VITE_API_URL` - Backend API base URL (default: `http://localhost:8080`)

### Backend
- `JWT_SECRET` - Base64-encoded signing key (generate: `openssl rand -base64 48`)
- `JWT_EXPIRATION_MS` - Token lifetime in milliseconds (default: 86400000 = 24 hours)
- `CORS_ALLOWED_ORIGINS` - Comma-separated list of allowed origins
- `ADMIN_INITIAL_EMAIL`, `ADMIN_INITIAL_PASSWORD` - First admin credentials
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` - Database connection

## File Structure

```
frontend/
├── src/
│   ├── pages/
│   │   ├── LoginPage.tsx          # Main login form
│   │   └── RegisterPage.tsx       # Registration form
│   ├── context/
│   │   └── AuthContext.tsx        # Global auth state
│   ├── api/
│   │   ├── auth.ts               # Auth API calls
│   │   └── client.ts             # Axios setup, token injection
│   ├── routes/
│   │   └── ProtectedRoute.tsx    # Auth guard component
│   ├── components/common/
│   │   ├── Input.tsx             # Form input
│   │   ├── Button.tsx            # Submit button
│   │   └── ErrorAlert.tsx        # Error display
│   ├── validation/
│   │   └── schemas.ts            # Zod validation schemas
│   ├── types/
│   │   └── index.ts              # TypeScript types
│   └── App.tsx                   # Route definitions

backend/
├── src/main/java/com/projectflow/
│   ├── controller/
│   │   └── AuthController.java   # Auth endpoints
│   ├── service/
│   │   └── AuthService.java      # Auth business logic
│   ├── security/
│   │   ├── JwtTokenProvider.java # Token generation/validation
│   │   ├── JwtAuthenticationFilter.java
│   │   └── UserPrincipal.java
│   ├── domain/
│   │   ├── User.java             # User entity
│   │   └── Role.java             # Role enum
│   ├── dto/
│   │   └── auth/
│   │       ├── LoginRequest.java
│   │       ├── RegisterRequest.java
│   │       └── AuthResponse.java
│   └── config/
│       └── SecurityConfig.java   # Spring Security config
```

## Testing

### Frontend Tests
- Login form validation
- Protected route access control
- Auth context state management
- API error handling

Run with: `npm test`

### Backend Tests
- Authentication service logic
- JWT token generation/validation
- Security filter behavior
- Authorization checks

Run with: `mvn test`

## Running the Application

### Development

**Backend:**
```bash
cd backend
mvn spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

### Docker
```bash
docker compose up --build
```

Access at: `http://localhost:3000`

Default credentials:
- Email: `admin@projectflow.local`
- Password: `Admin@123456`

## API Reference

### POST /api/auth/login
Authenticate user and receive JWT token.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200):**
```json
{
  "token": "eyJhbGc...",
  "user": {
    "id": "uuid",
    "name": "John Doe",
    "email": "user@example.com",
    "role": "MEMBER"
  }
}
```

**Error Responses:**
- `400` - Invalid email/password format
- `401` - Invalid credentials
- `422` - Validation errors

### POST /api/auth/register
Create new user account.

**Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

**Response (201):**
Same as login response with new user details.

### GET /api/auth/me
Get current authenticated user.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200):**
```json
{
  "id": "uuid",
  "name": "John Doe",
  "email": "user@example.com",
  "role": "MEMBER"
}
```

## Security Considerations

1. **Always use HTTPS in production** - JWT tokens should only travel over encrypted connections
2. **Rotate JWT_SECRET regularly** - Change the signing key periodically
3. **Set strong ADMIN_INITIAL_PASSWORD** - The first admin account is critical
4. **Configure CORS_ALLOWED_ORIGINS** - Only allow trusted frontend domains
5. **Monitor login attempts** - Implement rate limiting in production
6. **Keep dependencies updated** - Regularly update Spring Boot, React, and libraries

## Future Enhancements

- [ ] Multi-factor authentication (MFA)
- [ ] OAuth 2.0 social login
- [ ] Password reset via email
- [ ] Account lockout after failed attempts
- [ ] Session management (logout from all devices)
- [ ] Remember me functionality
- [ ] Refresh token rotation
- [ ] Audit logging

## Troubleshooting

### "Invalid credentials" on login
- Verify email and password match database
- Check JWT_SECRET is correctly set
- Ensure BCrypt library is properly configured

### Redirect to login after page refresh
- Check JWT token is being saved to localStorage
- Verify VITE_API_URL points to correct backend
- Check browser localStorage is enabled

### CORS errors
- Update CORS_ALLOWED_ORIGINS with frontend URL
- Verify backend CORS config matches frontend domain
- Check Authorization header is being sent

### Token expiration
- Implement refresh token endpoint
- Add automatic token refresh before expiration
- Show session timeout warning to user

## Conclusion

The ProjectFlow login system provides a secure, user-friendly authentication experience with enterprise-grade security practices. The implementation separates concerns across frontend and backend, uses proven technologies (JWT, BCrypt), and follows best practices for modern web applications.
