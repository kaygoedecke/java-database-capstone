function renderHeader() {
    const headerDiv = document.getElementById("header");
    if (!headerDiv) return;

    const currentPath = window.location.pathname;

    // Homepage: no role-based header
    if (currentPath.endsWith("/") || currentPath.endsWith("/index.html")) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");

        headerDiv.innerHTML = `
            <header class="header">
                <div class="logo-section">
                    <img src="/assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img">
                    <span class="logo-title">Hospital CMS</span>
                </div>
            </header>
        `;
        return;
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    let headerContent = `
        <header class="header">
            <div class="logo-section">
                <img src="/assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img">
                <span class="logo-title">Hospital CMS</span>
            </div>
            <nav class="nav-links">
    `;

    if (role === "admin") {
        headerContent += `
            <button id="addDocBtn" class="adminBtn">Add Doctor</button>
            <a href="#" id="logoutBtn">Logout</a>
        `;
    } else if (role === "doctor") {
        headerContent += `
            <button id="doctorHomeBtn" class="adminBtn">Home</button>
            <a href="#" id="logoutBtn">Logout</a>
        `;
    } else if (role === "patient") {
        headerContent += `
            <button id="patientLogin" class="adminBtn">Login</button>
            <button id="patientSignup" class="adminBtn">Sign Up</button>
        `;
    } else if (role === "loggedPatient") {
        headerContent += `
            <button id="home" class="adminBtn">Home</button>
            <button id="patientAppointments" class="adminBtn">Appointments</button>
            <a href="#" id="logoutPatientBtn">Logout</a>
        `;
    }

    headerContent += `
            </nav>
        </header>
    `;

    headerDiv.innerHTML = headerContent;
    attachHeaderButtonListeners();
}

function attachHeaderButtonListeners() {
    const addDocBtn = document.getElementById("addDocBtn");
    const logoutBtn = document.getElementById("logoutBtn");
    const logoutPatientBtn = document.getElementById("logoutPatientBtn");
    const doctorHomeBtn = document.getElementById("doctorHomeBtn");
    const patientLogin = document.getElementById("patientLogin");
    const patientSignup = document.getElementById("patientSignup");
    const homeBtn = document.getElementById("home");
    const patientAppointmentsBtn = document.getElementById("patientAppointments");

    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => {
            if (typeof openModal === "function") {
                openModal("addDoctor");
            }
        });
    }

    if (logoutBtn) {
        logoutBtn.addEventListener("click", (event) => {
            event.preventDefault();
            logout();
        });
    }

    if (logoutPatientBtn) {
        logoutPatientBtn.addEventListener("click", (event) => {
            event.preventDefault();
            logoutPatient();
        });
    }

    if (doctorHomeBtn) {
        doctorHomeBtn.addEventListener("click", () => {
            if (typeof selectRole === "function") {
                selectRole("doctor");
            } else {
                window.location.href = "/doctor/doctorDashboard";
            }
        });
    }

    if (patientLogin) {
        patientLogin.addEventListener("click", () => {
            if (typeof openModal === "function") {
                openModal("patientLogin");
            }
        });
    }

    if (patientSignup) {
        patientSignup.addEventListener("click", () => {
            if (typeof openModal === "function") {
                openModal("patientSignup");
            }
        });
    }

    if (homeBtn) {
        homeBtn.addEventListener("click", () => {
            window.location.href = "/pages/loggedPatientDashboard.html";
        });
    }

    if (patientAppointmentsBtn) {
        patientAppointmentsBtn.addEventListener("click", () => {
            window.location.href = "/pages/patientAppointments.html";
        });
    }
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
}

renderHeader();