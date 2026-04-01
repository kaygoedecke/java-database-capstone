# User Stories

## Admin User Stories

### User Story 1

**Title:**  
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**  
1. The admin can enter a valid username and password to log in.  
2. The system grants access only when the credentials are correct.  
3. Unauthorized users cannot access the admin dashboard.  

**Priority:** High  
**Story Points:** 3  

**Notes:**  
- Login should be protected with secure authentication.  
- Invalid login attempts should display an error message.  

---

### User Story 2

**Title:**  
_As an admin, I want to log out of the portal, so that I can protect system access when I finish my session._

**Acceptance Criteria:**  
1. The admin can log out using a logout option in the portal.  
2. The active session is terminated after logout.  
3. The admin must log in again to access protected pages.  

**Priority:** High  
**Story Points:** 2  

**Notes:**  
- Logout should remove session or token-based access.  
- The system should redirect the admin to the login page after logout.  

---

### User Story 3

**Title:**  
_As an admin, I want to add doctors to the portal, so that doctors can manage appointments and patients can book consultations._

**Acceptance Criteria:**  
1. The admin can create a doctor profile with required details such as name, specialization, and contact information.  
2. The system stores the doctor information successfully in the database.  
3. The new doctor becomes visible in the portal after creation.  

**Priority:** High  
**Story Points:** 5  

**Notes:**  
- Required fields should be validated before saving.  
- Duplicate doctor accounts should be prevented where necessary.  

---

### User Story 4

**Title:**  
_As an admin, I want to delete a doctor’s profile from the portal, so that inactive or incorrect profiles can be removed from the system._

**Acceptance Criteria:**  
1. The admin can select a doctor profile for deletion.  
2. The system asks for confirmation before deleting the profile.  
3. The deleted doctor profile is removed from the active doctor list.  

**Priority:** Medium  
**Story Points:** 4  

**Notes:**  
- Related appointments should be handled carefully before deletion.  
- In future versions, deactivation may be safer than permanent deletion.  

---

### User Story 5

**Title:**  
_As an admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month, so that I can track usage statistics._

**Acceptance Criteria:**  
1. The admin can execute the stored procedure from the MySQL CLI.  
2. The procedure returns the number of appointments grouped by month.  
3. The output can be used for reporting or system monitoring.  

**Priority:** Medium  
**Story Points:** 5  

**Notes:**  
- The stored procedure should be tested with sample appointment data.  
- Results should be clear and readable for reporting purposes.  

---

## Patient User Stories

### User Story 6

**Title:**  
_As a patient, I want to view a list of doctors without logging in, so that I can explore available options before registering._

**Acceptance Criteria:**  
1. Visitors can access the doctor list without authentication.  
2. The doctor list shows basic details such as name and specialization.  
3. Patients cannot book appointments until they sign up or log in.  

**Priority:** Medium  
**Story Points:** 3  

**Notes:**  
- This feature supports easier onboarding for new users.  
- Only public doctor information should be shown.  

---

### User Story 7

**Title:**  
_As a patient, I want to sign up using my email and password, so that I can create an account and book appointments._

**Acceptance Criteria:**  
1. The patient can register with a valid email and password.  
2. The system stores the new patient account securely.  
3. The patient can log in after successful registration.  

**Priority:** High  
**Story Points:** 4  

**Notes:**  
- The system should validate email format.  
- Passwords should be stored securely.  

---

### User Story 8

**Title:**  
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**  
1. The patient can log in with a valid email and password.  
2. The system grants access only when the credentials are correct.  
3. After login, the patient can access booking-related features.  

**Priority:** High  
**Story Points:** 3  

**Notes:**  
- Invalid credentials should return an error message.  
- Protected pages should not be accessible without login.  

---

### User Story 9

**Title:**  
_As a patient, I want to log out of the portal, so that I can secure my account after using the system._

**Acceptance Criteria:**  
1. The patient can log out from the portal using a logout option.  
2. The current session is ended after logout.  
3. The patient must log in again to access account-specific pages.  

**Priority:** High  
**Story Points:** 2  

**Notes:**  
- Logout should clear active session or token information.  
- The user should be redirected to a public page after logout.  

---

### User Story 10

**Title:**  
_As a patient, I want to log in and book an hour-long appointment with a doctor, so that I can consult with a doctor at a convenient time._

**Acceptance Criteria:**  
1. The patient can see available doctors and time slots after logging in.  
2. The patient can select a one-hour appointment slot and confirm the booking.  
3. The system prevents double-booking of the same appointment slot.  

**Priority:** High  
**Story Points:** 5  

**Notes:**  
- Appointment duration should be fixed to one hour for this story.  
- Bookings should be linked to the logged-in patient account.  

---

### User Story 11

**Title:**  
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**  
1. The patient can access a list of future appointments from the portal.  
2. Each appointment shows the doctor name, date, and time.  
3. Only the logged-in patient’s appointments are displayed.  

**Priority:** High  
**Story Points:** 3  

**Notes:**  
- Past appointments may be displayed separately in a future enhancement.  
- Appointment data should be shown in a clear and simple format.  

---

## Doctor User Stories

### User Story 12

**Title:**  
_As a doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**  
1. The doctor can log in using valid credentials.  
2. The system allows access only to authenticated doctors.  
3. After login, the doctor can view doctor-specific features and data.  

**Priority:** High  
**Story Points:** 3  

**Notes:**  
- Login should protect sensitive doctor and patient information.  
- Invalid credentials should return an appropriate error message.  

---

### User Story 13

**Title:**  
_As a doctor, I want to log out of the portal, so that I can protect my data when I finish using the system._

**Acceptance Criteria:**  
1. The doctor can log out through the portal interface.  
2. The active session is terminated immediately after logout.  
3. Protected pages are no longer accessible without logging in again.  

**Priority:** High  
**Story Points:** 2  

**Notes:**  
- Logout should clear any active authentication session or token.  
- The system should redirect the doctor to the login or home page.  

---

### User Story 14

**Title:**  
_As a doctor, I want to view my appointment calendar, so that I can stay organized._

**Acceptance Criteria:**  
1. The doctor can view a list or calendar of scheduled appointments.  
2. Each entry shows relevant details such as patient name, date, and time.  
3. The doctor can only view appointments assigned to them.  

**Priority:** High  
**Story Points:** 4  

**Notes:**  
- Calendar and list view can be improved in future versions.  
- Appointment data should be sorted clearly by date and time.  

---

### User Story 15

**Title:**  
_As a doctor, I want to mark my unavailability, so that patients only see available appointment slots._

**Acceptance Criteria:**  
1. The doctor can mark specific dates or times as unavailable.  
2. Unavailable time slots are removed from the patient booking options.  
3. The updated availability is saved correctly in the system.  

**Priority:** High  
**Story Points:** 5  

**Notes:**  
- This feature should help prevent invalid or conflicting bookings.  
- Existing booked appointments should not be affected without warning.  

---

### User Story 16

**Title:**  
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**  
1. The doctor can edit profile fields such as specialization and contact details.  
2. The system validates and saves the updated information.  
3. Patients see the updated profile information in the portal.  

**Priority:** Medium  
**Story Points:** 3  

**Notes:**  
- Some profile fields may be editable only by admins in future versions.  
- Updated data should appear consistently across the application.  

---

### User Story 17

**Title:**  
_As a doctor, I want to view the patient details for upcoming appointments, so that I can be prepared._

**Acceptance Criteria:**  
1. The doctor can open appointment details and see the assigned patient’s information.  
2. Only patient details related to the doctor’s own appointments are visible.  
3. The information is displayed before the scheduled appointment time.  

**Priority:** High  
**Story Points:** 4  

**Notes:**  
- Access should follow privacy and role-based access rules.  
- The displayed data should be limited to what is necessary for consultation.  
