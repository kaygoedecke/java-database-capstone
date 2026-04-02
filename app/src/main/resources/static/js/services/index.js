import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";

window.onload = function () {
    const adminBtn = document.getElementById("adminBtn");
    const doctorBtn = document.getElementById("doctorBtn");

    if (adminBtn) {
        adminBtn.addEventListener("click", () => {
            openModal("adminLogin");
        });
    }

    if (doctorBtn) {
        doctorBtn.addEventListener("click", () => {
            openModal("doctorLogin");
        });
    }
};

function setRoleAndRedirect(role) {
    const token = localStorage.getItem("token");

    if (typeof window.selectRole === "function") {
        window.selectRole(role);
    } else {
        localStorage.setItem("userRole", role);
    }

    if (role === "admin" && token) {
        window.location.href = `/adminDashboard/${token}`;
    } else if (role === "doctor" && token) {
        window.location.href = `/doctorDashboard/${token}`;
    }
}

async function adminLoginHandler() {
    const usernameInput = document.getElementById("username");
    const passwordInput = document.getElementById("password");

    const username = usernameInput ? usernameInput.value.trim() : "";
    const password = passwordInput ? passwordInput.value.trim() : "";

    const admin = { username, password };

    try {
        const response = await fetch(ADMIN_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(admin)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token);
            setRoleAndRedirect("admin");
        } else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        console.error("Admin login error:", error);
        alert("Something went wrong during admin login.");
    }
}

async function doctorLoginHandler() {
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");

    const email = emailInput ? emailInput.value.trim() : "";
    const password = passwordInput ? passwordInput.value.trim() : "";

    const doctor = { email, password };

    try {
        const response = await fetch(DOCTOR_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token);
            setRoleAndRedirect("doctor");
        } else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        console.error("Doctor login error:", error);
        alert("Something went wrong during doctor login.");
    }
}

window.adminLoginHandler = adminLoginHandler;
window.doctorLoginHandler = doctorLoginHandler;