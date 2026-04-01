import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors as filterDoctorsService, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();
    bindFilterEvents();
    bindAddDoctorButton();
});

function bindAddDoctorButton() {
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => {
            openModal("addDoctor");
        });
    } else {
        setTimeout(bindAddDoctorButton, 300);
    }
}

function bindFilterEvents() {
    const searchBar = document.getElementById("searchBar");
    const timeFilter = document.getElementById("timeFilter");
    const specialtyFilter = document.getElementById("specialtyFilter");

    if (searchBar) {
        searchBar.addEventListener("input", filterDoctorsOnChange);
    }

    if (timeFilter) {
        timeFilter.addEventListener("change", filterDoctorsOnChange);
    }

    if (specialtyFilter) {
        specialtyFilter.addEventListener("change", filterDoctorsOnChange);
    }
}

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error loading doctors:", error);
    }
}

async function filterDoctorsOnChange() {
    const searchBar = document.getElementById("searchBar");
    const timeFilter = document.getElementById("timeFilter");
    const specialtyFilter = document.getElementById("specialtyFilter");

    const name = searchBar ? searchBar.value.trim() : "";
    const time = timeFilter ? timeFilter.value : "";
    const specialty = specialtyFilter ? specialtyFilter.value : "";

    try {
        const doctors = await filterDoctorsService(name || "", time || "", specialty || "");

        if (!doctors || doctors.length === 0) {
            const contentDiv = document.getElementById("content");
            if (contentDiv) {
                contentDiv.innerHTML = `<p class="noPatientRecord">No doctors found.</p>`;
            }
            return;
        }

        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error filtering doctors:", error);
        alert("Failed to filter doctors.");
    }
}

function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;

    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = `<p class="noPatientRecord">No doctors found.</p>`;
        return;
    }

    doctors.forEach((doctor) => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

async function adminAddDoctor() {
    const nameInput = document.getElementById("doctorName");
    const specialtyInput = document.getElementById("specialization");
    const emailInput = document.getElementById("doctorEmail");
    const passwordInput = document.getElementById("doctorPassword");
    const phoneInput = document.getElementById("doctorPhone");

    const availabilityInputs = document.querySelectorAll('input[name="availability"]:checked');
    const availableTimes = Array.from(availabilityInputs).map((checkbox) => checkbox.value);

    const token = localStorage.getItem("token");
    if (!token) {
        alert("Admin is not authenticated. Please log in again.");
        return;
    }

    const doctor = {
        name: nameInput ? nameInput.value.trim() : "",
        specialty: specialtyInput ? specialtyInput.value : "",
        email: emailInput ? emailInput.value.trim() : "",
        password: passwordInput ? passwordInput.value.trim() : "",
        phone: phoneInput ? phoneInput.value.trim() : "",
        availableTimes
    };

    try {
        const result = await saveDoctor(doctor, token);

        if (result.success) {
            alert(result.message || "Doctor added successfully.");

            const modal = document.getElementById("modal");
            if (modal) {
                modal.style.display = "none";
            }

            await loadDoctorCards();
        } else {
            alert(result.message || "Failed to add doctor.");
        }
    } catch (error) {
        console.error("Error adding doctor:", error);
        alert("Something went wrong while adding the doctor.");
    }
}

window.adminAddDoctor = adminAddDoctor;