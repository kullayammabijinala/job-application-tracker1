const API_URL = "http://localhost:8080/api/jobs";

document.addEventListener("DOMContentLoaded", () => {
    fetchJobs();

    document.getElementById("job-form").addEventListener("submit", handleFormSubmit);
    document.getElementById("search-input").addEventListener("input", filterJobs);
    document.getElementById("filter-status").addEventListener("change", filterJobs);
    document.getElementById("sort-date").addEventListener("change", filterJobs);
});

let allJobs = [];

// Fetch all jobs from backend
async function fetchJobs() {
    try {
        const response = await fetch(API_URL);
        allJobs = await response.json();
        renderTable(allJobs);
        updateCounters(allJobs);
    } catch (error) {
        console.error("Error fetching jobs:", error);
    }
}

// Render jobs into table
function renderTable(jobs) {
    const tableBody = document.getElementById("job-table-body");
    tableBody.innerHTML = "";

    jobs.forEach(job => {
        const tr = document.createElement("tr");

        // Format ATS score badge color
        let scoreBadge = `<span style="color: #6c757d; font-weight: bold;">N/A</span>`;
        if (job.matchScore > 0) {
            let color = job.matchScore >= 70 ? "#28a745" : job.matchScore >= 40 ? "#ffc107" : "#dc3545";
            scoreBadge = `<span style="background-color: ${color}; color: white; padding: 4px 8px; border-radius: 4px; font-weight: bold;">${job.matchScore}%</span>`;
        }

        tr.innerHTML = `
            <td><strong>${job.company}</strong></td>
            <td>${job.role}</td>
            <td>${job.location}</td>
            <td>${job.salary || 'N/A'}</td>
            <td><span class="status-tag ${job.status.toLowerCase()}">${job.status}</span></td>
            <td>${scoreBadge}</td>
            <td>${job.appliedDate || ''}</td>
            <td>
                <button class="btn-delete" onclick="deleteJob(${job.id})">Delete</button>
            </td>
        `;
        tableBody.appendChild(tr);
    });
}

// Handle Form Submission with Multipart File Upload
async function handleFormSubmit(e) {
    e.preventDefault();

    const formData = new FormData();
    formData.append("company", document.getElementById("company").value);
    formData.append("role", document.getElementById("role").value);
    formData.append("location", document.getElementById("location").value);
    formData.append("salary", document.getElementById("salary").value);
    formData.append("status", document.getElementById("status").value);

    const fileInput = document.getElementById("resume");
    if (fileInput.files[0]) {
        formData.append("resume", fileInput.files[0]);
    }

    try {
        const response = await fetch(`${API_URL}/upload`, {
            method: "POST",
            body: formData
        });

        if (response.ok) {
            document.getElementById("job-form").reset();
            fetchJobs();
        }
    } catch (error) {
        console.error("Error adding job application:", error);
    }
}

// Delete job
async function deleteJob(id) {
    if (confirm("Are you sure you want to delete this job application?")) {
        try {
            await fetch(`${API_URL}/${id}`, { method: "DELETE" });
            fetchJobs();
        } catch (error) {
            console.error("Error deleting job:", error);
        }
    }
}

// Update Dashboard Counter Cards
function updateCounters(jobs) {
    document.getElementById("count-applied").textContent = jobs.filter(j => j.status === "Applied").length;
    document.getElementById("count-interview").textContent = jobs.filter(j => j.status === "Interview").length;
    document.getElementById("count-selected").textContent = jobs.filter(j => j.status === "Selected").length;
    document.getElementById("count-rejected").textContent = jobs.filter(j => j.status === "Rejected").length;
}

// Filter and Search logic
function filterJobs() {
    const search = document.getElementById("search-input").value.toLowerCase();
    const status = document.getElementById("filter-status").value;
    const sort = document.getElementById("sort-date").value;

    let filtered = allJobs.filter(job => {
        const matchesSearch = job.company.toLowerCase().includes(search) || job.role.toLowerCase().includes(search);
        const matchesStatus = status === "ALL" || job.status === status;
        return matchesSearch && matchesStatus;
    });

    if (sort === "DESC") {
        filtered.sort((a, b) => b.id - a.id);
    } else {
        filtered.sort((a, b) => a.id - b.id);
    }

    renderTable(filtered);
}