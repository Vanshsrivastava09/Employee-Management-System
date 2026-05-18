// Departments page API calls and rendering.
(function () {
  let departments = [];
  let employees = [];

  function el(id) { return document.getElementById(id); }

  async function loadData() {
    App.setLoaderVisible(true);
    try {
      const deptRes = await Api.get("/api/departments");
      const empRes = await Api.get("/api/employees?size=1000&page=0");
      departments = deptRes.data || [];
      employees = empRes.data && empRes.data.items ? empRes.data.items : [];
      renderDepartments();
      if (departments.length) renderDepartmentEmployees(departments[0].id);
    } catch (error) {
      App.showToast(error.message || "Failed to load departments", "error");
    } finally {
      App.setLoaderVisible(false);
    }
  }

  function renderDepartments() {
    const select = el("departmentSelect");
    select.innerHTML = departments.map(d => `<option value="${d.id}">${App.escapeHtml(d.name)}</option>`).join("");

    el("departmentCards").innerHTML = departments.map(d => {
      const count = employees.filter(e => e.departmentId === d.id).length;
      return `<article class="department-card">
        <h3>${App.escapeHtml(d.name)}</h3>
        <p>${App.escapeHtml(d.name)} department contains ${count} employee${count === 1 ? "" : "s"} in this organization.</p>
        <div class="department-meta">
          <span class="pill">${count} Employees</span>
          <button class="btn btn--primary" type="button" data-view="${d.id}">View</button>
        </div>
      </article>`;
    }).join("");
  }

  function renderDepartmentEmployees(departmentId) {
    const id = Number(departmentId);
    const dept = departments.find(d => d.id === id);
    const rows = employees.filter(e => e.departmentId === id);
    el("selectedDepartmentTitle").textContent = dept ? dept.name + " Employees" : "Department Employees";
    el("departmentSelect").value = String(id);
    el("departmentEmployeesBody").innerHTML = rows.map(e => `
      <tr>
        <td><strong>${App.escapeHtml(e.fullName)}</strong></td>
        <td>${App.escapeHtml(e.email || "-")}</td>
        <td>${App.escapeHtml(e.phone || "-")}</td>
        <td><div class="actions"><a class="btn btn--soft" href="employees.html?departmentId=${id}">View</a></div></td>
      </tr>
    `).join("") || "<tr><td colspan='4'>No employees in this department yet.</td></tr>";
  }

  async function createDepartment() {
    const name = el("departmentName").value.trim();
    if (!name) return App.showToast("Department name is required", "error");

    try {
      App.setLoaderVisible(true);
      const res = await Api.post("/api/departments", { name });
      if (!res || res.success === false) throw new Error(res && res.message ? res.message : "Create failed");
      el("departmentModal").classList.add("hidden");
      el("departmentName").value = "";
      App.showToast("Department created", "success");
      await loadData();
    } catch (error) {
      App.showToast(error.message || "Create failed", "error");
    } finally {
      App.setLoaderVisible(false);
    }
  }

  async function init() {
    el("openDepartmentModalBtn").addEventListener("click", () => el("departmentModal").classList.remove("hidden"));
    el("closeDepartmentModalBtn").addEventListener("click", () => el("departmentModal").classList.add("hidden"));
    el("saveDepartmentBtn").addEventListener("click", createDepartment);
    el("viewSelectedBtn").addEventListener("click", () => renderDepartmentEmployees(el("departmentSelect").value));
    el("departmentCards").addEventListener("click", (event) => {
      const btn = event.target.closest("button[data-view]");
      if (btn) renderDepartmentEmployees(btn.getAttribute("data-view"));
    });
    el("addEmployeeToDeptBtn").addEventListener("click", () => {
      window.location.href = "employees.html?departmentId=" + encodeURIComponent(el("departmentSelect").value);
    });
    await loadData();
  }

  window.DepartmentsUI = { init };
})();
