# Smart Clinic Database Design

## MySQL Database Design

The Smart Clinic Management System uses MySQL for structured and relational data that requires strong consistency, validation, and clear relationships. Core operational data such as patients, doctors, admins, and appointments fits well in a relational database because these entities are connected and need primary keys, foreign keys, and constraints. This design helps ensure data integrity and makes reporting easier.

### Table: patients
- `id`: INT, Primary Key, Auto Increment
- `first_name`: VARCHAR(50), Not Null
- `last_name`: VARCHAR(50), Not Null
- `email`: VARCHAR(100), Not Null, Unique
- `phone`: VARCHAR(20), Not Null, Unique
- `date_of_birth`: DATE, Not Null
- `gender`: VARCHAR(20), Null
- `address`: VARCHAR(255), Null
- `password`: VARCHAR(255), Not Null
- `created_at`: TIMESTAMP, Not Null, Default CURRENT_TIMESTAMP

**Constraints / Notes:**
- Email and phone should be unique for each patient.
- Password will be stored as a hashed value in the real application.
- Validation for email and phone format can be enforced in backend code.

### Table: doctors
- `id`: INT, Primary Key, Auto Increment
- `first_name`: VARCHAR(50), Not Null
- `last_name`: VARCHAR(50), Not Null
- `email`: VARCHAR(100), Not Null, Unique
- `phone`: VARCHAR(20), Not Null, Unique
- `specialization`: VARCHAR(100), Not Null
- `license_number`: VARCHAR(50), Not Null, Unique
- `availability_status`: VARCHAR(20), Not Null, Default 'AVAILABLE'
- `password`: VARCHAR(255), Not Null
- `created_at`: TIMESTAMP, Not Null, Default CURRENT_TIMESTAMP

**Constraints / Notes:**
- Each doctor should have a unique email, phone number, and license number.
- Availability can later be expanded into a separate schedule table if needed.
- Password will also be stored as a hashed value.

### Table: admins
- `id`: INT, Primary Key, Auto Increment
- `username`: VARCHAR(50), Not Null, Unique
- `email`: VARCHAR(100), Not Null, Unique
- `password`: VARCHAR(255), Not Null
- `role`: VARCHAR(20), Not Null, Default 'ADMIN'
- `created_at`: TIMESTAMP, Not Null, Default CURRENT_TIMESTAMP

**Constraints / Notes:**
- Admin accounts are kept separate for security and role clarity.
- Username and email must be unique.
- Password will be stored securely as a hash.

### Table: appointments
- `id`: INT, Primary Key, Auto Increment
- `patient_id`: INT, Not Null, Foreign Key → `patients(id)`
- `doctor_id`: INT, Not Null, Foreign Key → `doctors(id)`
- `appointment_date`: DATE, Not Null
- `start_time`: TIME, Not Null
- `end_time`: TIME, Not Null
- `status`: VARCHAR(20), Not Null, Default 'SCHEDULED'
- `reason`: VARCHAR(255), Null
- `created_at`: TIMESTAMP, Not Null, Default CURRENT_TIMESTAMP

**Constraints / Notes:**
- `patient_id` must reference an existing patient.
- `doctor_id` must reference an existing doctor.
- Appointment status can be values such as `SCHEDULED`, `COMPLETED`, or `CANCELLED`.
- In application logic, doctors should not be allowed to have overlapping appointments.
- Appointment history should be retained even if an appointment is cancelled.
- If a patient or doctor is deleted, it is safer to restrict deletion or use soft delete logic instead of losing appointment records.

### Table: doctor_availability
- `id`: INT, Primary Key, Auto Increment
- `doctor_id`: INT, Not Null, Foreign Key → `doctors(id)`
- `available_date`: DATE, Not Null
- `start_time`: TIME, Not Null
- `end_time`: TIME, Not Null
- `is_available`: BOOLEAN, Not Null, Default TRUE

**Constraints / Notes:**
- This table stores doctor availability separately from booked appointments.
- It supports future scheduling features more cleanly than storing all availability in one doctor column.
- A doctor can have many available time slots.

---

## MongoDB Collection Design

The system uses MongoDB for flexible document-based data. Prescriptions are a good fit for MongoDB because they may contain variable medication details, doctor notes, refill information, and nested pharmacy or dosage instructions. This structure can evolve over time without requiring strict table changes.

### Collection: prescriptions

```json
{
  "_id": "ObjectId('665abc1234567890def12345')",
  "appointmentId": 101,
  "patientId": 12,
  "doctorId": 4,
  "patientName": "John Smith",
  "doctorName": "Dr. Emily Adams",
  "issuedDate": "2025-05-23T10:30:00Z",
  "medications": [
    {
      "name": "Amoxicillin",
      "dosage": "500mg",
      "frequency": "Twice a day",
      "durationDays": 7,
      "instructions": "Take after meals"
    },
    {
      "name": "Vitamin C",
      "dosage": "1000mg",
      "frequency": "Once a day",
      "durationDays": 14,
      "instructions": "Take in the morning"
    }
  ],
  "doctorNotes": "Patient should rest and drink plenty of water.",
  "additionalNotes": "Return for follow-up if symptoms continue after 7 days.",
  "refillCount": 1,
  "pharmacy": {
    "name": "CityCare Pharmacy",
    "location": "Main Street Clinic Branch"
  },
  "tags": ["infection", "follow-up", "antibiotics"],
  "status": "ACTIVE"
}
