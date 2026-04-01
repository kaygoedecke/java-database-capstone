import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

const patientTableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split("T")[0];
const token = localStorage.getItem("token");
let patientName = "null";

function showMessageRow(message) {
    if (!patientTableBody) return;

    patientTableBody.innerHTML = `
        <tr>
            <td colspan="5" class="noPatientRecord">${message}</td>
        </tr>
    `;
}

async function loadAppointments() {
    if (!patientTableBody) return;

    try {
        const appointments = await getAllAppointments(selectedDate, patientName, token);
        patientTableBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {
            showMessageRow("No Appointments found for today.");
            return;
        }

        appointments.forEach((appointment) => {
            const patient = appointment.patient || {
                id: appointment.patientId || "",
                name: appointment.patientName || "Unknown Patient",
                phone: appointment.patientPhone || "N/A",
                email: appointment.patientEmail || "N/A"
            };

            const appointmentId = appointment.id || appointment.appointmentId || "";
            const doctorId = appointment.doctorId || (appointment.doctor ? appointment.doctor.id : "");

            const row = createPatientRow(patient, appointmentId, doctorId);
            patientTableBody.appendChild(row);
        });
    } catch (error) {
        console.error("Error loading appointments:", error);
        showMessageRow("Error loading appointments. Try again later.");
    }
}

function bindSearchBar() {
    const searchBar = document.getElementById("searchBar");
    if (!searchBar) return;

    searchBar.addEventListener("input", () => {
        const value = searchBar.value.trim();
        patientName = value !== "" ? value : "null";
        loadAppointments();
    });
}

function bindTodayButton() {
    const todayButton = document.getElementById("todayButton");
    const datePicker = document.getElementById("datePicker");

    if (!todayButton) return;

    todayButton.addEventListener("click", () => {
        selectedDate = new Date().toISOString().split("T")[0];

        if (datePicker) {
            datePicker.value = selectedDate;
        }

        loadAppointments();
    });
}

function bindDatePicker() {
    const datePicker = document.getElementById("datePicker");
    if (!datePicker) return;

    datePicker.value = selectedDate;

    datePicker.addEventListener("change", () => {
        selectedDate = datePicker.value || new Date().toISOString().split("T")[0];
        loadAppointments();
    });
}

document.addEventListener("DOMContentLoaded", () => {
    if (typeof window.renderContent === "function") {
        window.renderContent();
    }

    bindSearchBar();
    bindTodayButton();
    bindDatePicker();
    loadAppointments();
});