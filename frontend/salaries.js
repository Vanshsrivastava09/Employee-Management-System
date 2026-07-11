const SalariesUI = (function () {
  async function init() {
    App.setLoaderVisible(true);
    try {
      const employeesRes = await Api.get('/api/employees?size=1000&page=0');
      const employees = employeesRes.data && employeesRes.data.items ? employeesRes.data.items : [];
      const empSelect = document.getElementById('employeeFilter');
      const empSelectModal = document.getElementById('salaryEmployeeId');
      employees.forEach(e => { const o = document.createElement('option'); o.value = e.id; o.textContent = e.fullName; empSelect.appendChild(o); const o2 = o.cloneNode(true); empSelectModal.appendChild(o2); });

      document.getElementById('createSalaryBtn').addEventListener('click', ()=> openModal());
      document.getElementById('closeSalaryModal').addEventListener('click', ()=>closeModal());
      document.getElementById('saveSalaryBtn').addEventListener('click', saveSalary);

      document.getElementById('employeeFilter').addEventListener('change', loadSalaries);

      await loadSalaries();
    } catch (e) { App.showToast(e.message || 'Failed', 'error'); }
    finally { App.setLoaderVisible(false); }
  }

  async function loadSalaries() {
    App.setLoaderVisible(true);
    try {
      const res = await Api.get('/api/salaries');
      const items = res.data && res.data.items ? res.data.items : [];
      const filter = document.getElementById('employeeFilter').value;
      const body = document.getElementById('salariesTableBody');
      
      const filteredItems = items.filter(it => !filter || String(it.employee.id) === filter);
      
      if (filteredItems.length === 0) {
        body.innerHTML = `
          <tr>
            <td colspan="5">
              <div class="empty-state">
                <div class="empty-state-icon">💰</div>
                <div class="empty-state-title">No salary records found</div>
                <div class="empty-state-description">
                  ${filter ? 'Try selecting a different employee.' : 'Get started by adding salary records for your employees.'}
                </div>
                ${!filter ? '<div class="empty-state-action"><button class="btn btn--primary" onclick="document.getElementById(\'createSalaryBtn\').click()">Add Salary</button></div>' : ''}
              </div>
            </td>
          </tr>
        `;
      } else {
        const rows = filteredItems.map(it => `
          <tr>
            <td>${App.escapeHtml(it.employee.fullName || it.employee.email)}</td>
            <td>${it.amount}</td>
            <td>${App.escapeHtml(it.period || '')}</td>
            <td><span class="pill ${it.status === 'PAID' ? 'pill--active' : 'pill--warning'}">${App.escapeHtml(it.status || '')}</span></td>
            <td style="text-align:right;"><button data-id="${it.id}" class="btn btn--soft btn-edit">Edit</button> <button data-id="${it.id}" class="btn btn--danger btn-delete">Delete</button></td>
          </tr>
        `).join('');
        body.innerHTML = rows;
      }

      document.querySelectorAll('.btn-delete').forEach(b => b.addEventListener('click', async (ev)=>{
        const id = ev.currentTarget.getAttribute('data-id');
        const ok = await App.confirmDialog('Delete this salary record?');
        if (!ok) return;
        await Api.del('/api/salaries/' + id);
        App.showToast('Deleted', 'success');
        await loadSalaries();
      }));

      document.querySelectorAll('.btn-edit').forEach(b => b.addEventListener('click', async (ev)=>{
        const id = ev.currentTarget.getAttribute('data-id');
        const res = await Api.get('/api/salaries/' + id);
        const s = res.data;
        openModal(s);
      }));
    } catch (e) { App.showToast(e.message || 'Failed to load salaries', 'error'); }
    finally { App.setLoaderVisible(false); }
  }

  function openModal(s) {
    const modal = document.getElementById('salaryModal');
    document.getElementById('salaryId').value = s ? s.id : '';
    document.getElementById('salaryAmount').value = s ? s.amount : '';
    document.getElementById('salaryPeriod').value = s ? s.period : '';
    document.getElementById('salaryStatus').value = s ? s.status : 'PENDING';
    if (s && s.employee) document.getElementById('salaryEmployeeId').value = s.employee.id;
    modal.classList.remove('hidden');
  }

  function closeModal() { document.getElementById('salaryModal').classList.add('hidden'); }

  async function saveSalary() {
    const id = document.getElementById('salaryId').value;
    const employeeId = document.getElementById('salaryEmployeeId').value;
    const amount = document.getElementById('salaryAmount').value;
    const period = document.getElementById('salaryPeriod').value;
    const status = document.getElementById('salaryStatus').value;
    
    // Enhanced validation
    let hasError = false;
    
    if (!employeeId) {
      document.getElementById('salaryEmployeeId').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('salaryEmployeeId').style.borderColor = 'var(--border)';
    }
    
    if (!amount || isNaN(amount) || Number(amount) <= 0) {
      document.getElementById('salaryAmount').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('salaryAmount').style.borderColor = 'var(--border)';
    }
    
    if (!period) {
      document.getElementById('salaryPeriod').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('salaryPeriod').style.borderColor = 'var(--border)';
    }
    
    if (hasError) {
      App.showToast('Please fix the highlighted fields', 'error');
      return;
    }
    
    try {
      const payload = { employeeId: Number(employeeId), amount: Number(amount), period, status };
      if (id) {
        await Api.put('/api/salaries/' + id, payload);
        App.showToast('Updated', 'success');
      } else {
        await Api.post('/api/salaries', payload);
        App.showToast('Created', 'success');
      }
      closeModal();
      await loadSalaries();
    } catch (e) { App.showToast(e.message || 'Save failed', 'error'); }
  }

  return { init };
})();
