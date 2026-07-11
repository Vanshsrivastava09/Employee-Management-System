/* Employee CRUD UI logic (plain JS) */

(function () {
  function el(id) {
    return document.getElementById(id);
  }

  let state = {
    page: 0,
    size: 8,
    totalPages: 1,
    search: "",
    departmentId: "",
    photoFile: null
  };

  async function loadDepartments() {
    const res = await Api.get("/api/departments");
    if (!res || res.success === false) throw new Error(res && res.message ? res.message : "Failed to load departments");
    const departments = res.data || res; // fallback

    const select = el("departmentFilter");
    if (!select) return;

    select.innerHTML = "";
    const optAll = document.createElement("option");
    optAll.value = "";
    optAll.textContent = "All departments";
    select.appendChild(optAll);

    for (const d of departments) {
      const opt = document.createElement("option");
      opt.value = d.id;
      opt.textContent = d.name;
      select.appendChild(opt);
    }

    const formSelect = el("departmentId");
    if (formSelect) {
      formSelect.innerHTML = "";
      for (const d of departments) {
        const opt = document.createElement("option");
        opt.value = d.id;
        opt.textContent = d.name;
        formSelect.appendChild(opt);
      }
    }
  }

  function renderPagination(meta) {
    // meta: {page,size,totalPages}
    const container = el("pagination");
    if (!container) return;

    state.totalPages = meta.totalPages || 1;
    container.innerHTML = "";

    const prev = document.createElement("button");
    prev.className = "btn btn--ghost";
    prev.textContent = "Prev";
    prev.disabled = meta.page <= 0;
    prev.addEventListener("click", () => {
      state.page = Math.max(0, state.page - 1);
      loadEmployees();
    });

    const next = document.createElement("button");
    next.className = "btn btn--ghost";
    next.textContent = "Next";
    next.disabled = state.page >= state.totalPages - 1;
    next.addEventListener("click", () => {
      state.page = state.page + 1;
      loadEmployees();
    });

    const metaText = document.createElement("div");
    metaText.className = "meta";
    metaText.textContent = `Page ${meta.page + 1} of ${state.totalPages}`;

    container.appendChild(metaText);
    container.appendChild(prev);
    container.appendChild(next);
  }

  function renderEmployees(p) {
    // p contains: items, page, size, totalPages...
    const tbody = el("employeesTableBody");
    if (!tbody) return;

    tbody.innerHTML = "";

    const items = (p && p.items) ? p.items : [];
    
    // Empty state
    if (items.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="6">
            <div class="empty-state">
              <div class="empty-state-icon">👥</div>
              <div class="empty-state-title">No employees found</div>
              <div class="empty-state-description">
                ${state.search || state.departmentId ? 'Try adjusting your filters or search terms.' : 'Get started by adding your first employee.'}
              </div>
              ${!state.search && !state.departmentId ? '<div class="empty-state-action"><button class="btn btn--primary" onclick="EmployeesUI.openModal(\'create\', null)">Add Employee</button></div>' : ''}
            </div>
          </td>
        </tr>
      `;
      return;
    }
    
    for (const emp of items) {
      const tr = document.createElement("tr");

      const deptPill = document.createElement("span");
      deptPill.className = "pill";
      deptPill.textContent = emp.departmentName ? emp.departmentName : "N/A";

      const activePill = document.createElement("span");
      activePill.className = "pill " + (emp.active ? "pill--active" : "pill--inactive");
      activePill.textContent = emp.active ? "Active" : "Inactive";

      tr.innerHTML = `
        <td>
          <div class="employee-cell">
            <img class="avatar" src="${App.mediaUrl(emp.photoUrl)}" alt="avatar" onerror="this.style.display='none';"/>
            <div>
              <div class="employee-name">${escape(emp.fullName || '')}</div>
              <div class="employee-email">ID: ${emp.id || '-'}</div>
            </div>
          </div>
        </td>
        <td>${escape(emp.email || '')}</td>
        <td></td>
        <td>${escape(emp.phone || '')}</td>
        <td></td>
        <td class="actions"></td>
      `;

      tr.children[2].appendChild(deptPill);
      tr.children[4].appendChild(activePill);
      tr.children[5].innerHTML = `
        <div class="actions">
          <button class="btn btn--soft" type="button" data-action="view" data-id="${emp.id}">View</button>
          <button class="btn btn--danger" type="button" data-action="delete" data-id="${emp.id}">Delete</button>
        </div>
      `;

      tbody.appendChild(tr);
    }
  }

  function escape(s) {
    return App.escapeHtml(s);
  }

  function readFilters() {
    const search = el("searchInput").value.trim();
    const departmentId = el("departmentFilter").value;
    state.search = search;
    state.departmentId = departmentId;
    state.page = 0;
  }

  async function loadEmployees() {
    try {
      App.setLoaderVisible(true);

      const params = new URLSearchParams();
      if (state.search) params.set("search", state.search);
      if (state.departmentId) params.set("departmentId", state.departmentId);
      params.set("page", String(state.page));
      params.set("size", String(state.size));

      const res = await Api.get("/api/employees?" + params.toString());
      if (!res || res.success === false) throw new Error(res && res.message ? res.message : "Failed to load employees");

      const data = res.data;
      renderEmployees(data);
      updateTopStats();
      renderPagination({
        page: data.page,
        size: data.size,
        totalPages: data.totalPages
      });
    } catch (e) {
      App.showToast(e.message || "Failed to load employees", "error");
    } finally {
      App.setLoaderVisible(false);
    }
  }

  // Modal form handlers
  function openModal(mode, employee) {
    const modal = el("employeeModal");
    if (!modal) return;

    el("employeeModalMode").value = mode;
    el("modalTitle").textContent = mode === "create" ? "Add Employee" : "Update Employee";

    el("empId").value = employee && employee.id ? employee.id : "";
    el("fullName").value = employee && employee.fullName ? employee.fullName : "";
    el("email").value = employee && employee.email ? employee.email : "";
    el("phone").value = employee && employee.phone ? employee.phone : "";

    el("departmentId").value = employee && employee.departmentId ? String(employee.departmentId) : "";
    el("isActive").checked = employee && employee.active ? true : false;

    // Photo fields:
    el("photoUrlPreview").src = employee && employee.photoUrl ? App.mediaUrl(employee.photoUrl) : "";
    el("photoUrlPreview").style.display = employee && employee.photoUrl ? "block" : "none";

    el("photoFile").value = "";

    modal.classList.remove("hidden");
  }

  function closeModal() {
    const modal = el("employeeModal");
    if (!modal) return;
    modal.classList.add("hidden");
  }

  async function submitEmployeeForm(event) {
    event.preventDefault();

    const mode = el("employeeModalMode").value;
    const id = el("empId").value ? Number(el("empId").value) : null;

    const fullName = el("fullName").value.trim();
    const email = el("email").value.trim();
    const phone = el("phone").value.trim();
    const departmentId = el("departmentId").value ? Number(el("departmentId").value) : null;
    const active = el("isActive").checked;

    // Enhanced validation with inline feedback
    let hasError = false;
    
    if (!fullName) {
      el("fullName").style.borderColor = "var(--danger)";
      hasError = true;
    } else {
      el("fullName").style.borderColor = "var(--border)";
    }
    
    if (!email || !email.includes("@")) {
      el("email").style.borderColor = "var(--danger)";
      hasError = true;
    } else {
      el("email").style.borderColor = "var(--border)";
    }
    
    if (!phone || phone.length < 10) {
      el("phone").style.borderColor = "var(--danger)";
      hasError = true;
    } else {
      el("phone").style.borderColor = "var(--border)";
    }
    
    if (!departmentId) {
      el("departmentId").style.borderColor = "var(--danger)";
      hasError = true;
    } else {
      el("departmentId").style.borderColor = "var(--border)";
    }
    
    if (hasError) {
      App.showToast("Please fix the highlighted fields", "error");
      return;
    }

    // We no longer send local preview URL as photoUrl.
    // Real upload happens via multipart to backend after employee is created/updated.
    const payload = {
      fullName,
      email,
      phone,
      departmentId,
      isActive: active
    };

    try {
      App.setLoaderVisible(true);

      let res;
      if (mode === "create") {
        res = await Api.post("/api/employees", payload);
      } else {
        res = await Api.put("/api/employees/" + id, payload);
      }

      if (!res || res.success === false) throw new Error(res && res.message ? res.message : "Save failed");

      const saved = res.data || {};
      const empId = saved.id || id;

      // Upload photo if selected
      if (state.photoFile && empId) {
        const fd = new FormData();
        fd.append("file", state.photoFile);

        const photoRes = await fetch(App.API_BASE + "/api/employees/" + empId + "/photo", {
          method: "POST",
          credentials: "include",
          body: fd
        }).then(r => r.json());

        if (!photoRes || photoRes.success === false) {
          throw new Error(photoRes && photoRes.message ? photoRes.message : "Photo upload failed");
        }
      }

      App.showToast(res.message || "Saved", "success");
      closeModal();
      loadEmployees();
    } catch (e) {
      App.showToast(e.message || "Save failed", "error");
    } finally {
      App.setLoaderVisible(false);
    }
  }

  async function handlePhotoUpload(file) {
    if (!file) {
      state.photoFile = null;
      el("photoUrlPreview").src = "";
      el("photoUrlPreview").style.display = "none";
      return;
    }

    state.photoFile = file;

    const url = URL.createObjectURL(file);
    el("photoUrlPreview").src = url;
    el("photoUrlPreview").style.display = "block";
  }

  async function initEmployeePage() {
    // Bind events
    el("applyFiltersBtn").addEventListener("click", () => {
      readFilters();
      loadEmployees();
    });

    el("createEmployeeBtn").addEventListener("click", () => openModal("create", null));

    // Export CSV
    el("exportCsvBtn") && el("exportCsvBtn").addEventListener("click", async () => {
      try {
        App.setLoaderVisible(true);
        const params = new URLSearchParams();
        if (state.search) params.set("search", state.search);
        if (state.departmentId) params.set("departmentId", state.departmentId);

        const url = App.API_BASE + "/api/employees/export?" + params.toString();
        const res = await fetch(url, { method: "GET", credentials: "include" });

        if (!res.ok) throw new Error("CSV export failed");

        const blob = await res.blob();
        const a = document.createElement("a");
        a.href = URL.createObjectURL(blob);
        a.download = "employees.csv";
        document.body.appendChild(a);
        a.click();
        a.remove();
      } catch (e) {
        App.showToast(e.message || "CSV export failed", "error");
      } finally {
        App.setLoaderVisible(false);
      }
    });

    el("employeeForm").addEventListener("submit", submitEmployeeForm);
    el("saveEmployeeBtn").addEventListener("click", () => el("employeeForm").requestSubmit());

    el("closeModalBtn").addEventListener("click", () => {
      state.photoFile = null;
      closeModal();
    });

    el("photoFile").addEventListener("change", (e) => handlePhotoUpload(e.target.files[0]));

    // Delegated table actions
    el("employeesTableBody").addEventListener("click", async (e) => {
      const btn = e.target.closest("button");
      if (!btn) return;

      const action = btn.getAttribute("data-action");
      const id = Number(btn.getAttribute("data-id"));

      if (action === "view") {
        // Navigate to employee profile page
        window.location.href = "profile.html?id=" + id;
      }

      if (action === "delete") {
        const ok = await App.confirmDialog("Delete this employee record?");
        if (!ok) return;
        await Api.del("/api/employees/" + id);
        App.showToast("Employee deleted", "success");
        loadEmployees();
      }
    });

    // Add small UX polish: disable pagination buttons while loading
    const pager = el("pagination");
    if (pager) {
      pager.addEventListener("click", (e) => {
        const btn = e.target.closest("button");
        if (!btn) return;
        if (btn.disabled) return;
      });
    }

    await loadDepartments();
    const urlParams = new URLSearchParams(window.location.search);
    const departmentFromUrl = urlParams.get("departmentId");
    if (departmentFromUrl && el("departmentFilter")) {
      el("departmentFilter").value = departmentFromUrl;
      state.departmentId = departmentFromUrl;
    }
    await loadEmployees();

    // Show top stats on first load (computed from current filter/page)
    // Since backend doesn't provide analytics here, keep it simple:
    // stats will update after each load.
    updateTopStats();
  }

  function updateTopStats(){
    try{
      const search = state.search;
      const departmentId = state.departmentId;
      const lastPage = state.page;
      // We already loaded a page; compute shown count from current table.
      const tbody = el("employeesTableBody");
      const rows = tbody ? tbody.querySelectorAll("tr").length : 0;
      const shown = rows > 0 ? rows : 0;

      // Pull raw items count by reading the pager meta if available.
      // renderEmployees doesn't keep reference, so we derive from DOM pills where possible.
      // Keep it beginner-friendly: set shown to current page row count.
      const activeCount = tbody ? Array.from(tbody.querySelectorAll("span.pill--active")).length : 0;

      el("cardEmployeesShown").textContent = String(shown);
      el("cardEmployeesActive").textContent = String(activeCount);

      // Departments count from department dropdown options
      const select = el("departmentFilter");
      const deps = select ? Math.max(0, select.options.length - 1) : 0; // minus All
      el("cardEmployeesDepartments").textContent = String(deps);

    }catch(_e){ /* ignore */ }
  }

  window.EmployeesUI = { initEmployeePage, openModal, closeModal };
})();
