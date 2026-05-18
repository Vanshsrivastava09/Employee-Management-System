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
      const rows = items.filter(it => !filter || String(it.employee.id) === filter).map(it => `
        <tr>
          <td>${App.escapeHtml(it.employee.fullName || it.employee.email)}</td>
          <td>${it.amount}</td>
          <td>${App.escapeHtml(it.period || '')}</td>
          <td>${App.escapeHtml(it.status || '')}</td>
          <td style="text-align:right;"><button data-id="${it.id}" class="btn btn--soft btn-edit">Edit</button> <button data-id="${it.id}" class="btn btn--danger btn-delete">Delete</button></td>
        </tr>
      `).join('') || '<tr><td colspan="5">No salaries found.</td></tr>';
      body.innerHTML = rows;

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
    try {
      if (!employeeId) return App.showToast('Select employee', 'error');
      if (!amount) return App.showToast('Enter amount', 'error');
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
