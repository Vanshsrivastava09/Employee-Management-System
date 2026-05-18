const LeavesUI = (function () {
  async function init() {
    App.setLoaderVisible(true);
    try {
      const employeesRes = await Api.get('/api/employees?size=1000&page=0');
      const employees = employeesRes.data && employeesRes.data.items ? employeesRes.data.items : [];
      const empSelect = document.getElementById('leaveEmployeeFilter');
      const empSelectModal = document.getElementById('leaveEmployeeId');
      employees.forEach(e => { const o = document.createElement('option'); o.value = e.id; o.textContent = e.fullName; empSelect.appendChild(o); const o2 = o.cloneNode(true); empSelectModal.appendChild(o2); });

      document.getElementById('createLeaveBtn').addEventListener('click', ()=> openModal());
      document.getElementById('closeLeaveModal').addEventListener('click', ()=>closeModal());
      document.getElementById('saveLeaveBtn').addEventListener('click', saveLeave);
      document.getElementById('leaveEmployeeFilter').addEventListener('change', loadLeaves);

      await loadLeaves();
    } catch (e) { App.showToast('Init failed','error'); }
    finally { App.setLoaderVisible(false); }
  }

  async function loadLeaves() {
    App.setLoaderVisible(true);
    try {
      const res = await Api.get('/api/leaves');
      const items = res.data && res.data.items ? res.data.items : [];
      const filter = document.getElementById('leaveEmployeeFilter').value;
      const body = document.getElementById('leavesTableBody');
      const rows = items.filter(it => !filter || String(it.employee.id) === filter).map(it => `
        <tr>
          <td>${App.escapeHtml(it.employee.fullName)}</td>
          <td>${it.startDate || ''} → ${it.endDate || ''}</td>
          <td>${App.escapeHtml(it.reason || '')}</td>
          <td>${App.escapeHtml(it.status || '')}</td>
          <td style="text-align:right;"><button data-id="${it.id}" class="btn btn--soft btn-approve">Approve</button> <button data-id="${it.id}" class="btn btn--danger btn-reject">Reject</button></td>
        </tr>
      `).join('') || '<tr><td colspan="5">No leave requests</td></tr>';
      body.innerHTML = rows;

      document.querySelectorAll('.btn-approve').forEach(b => b.addEventListener('click', async ev => { const id = ev.currentTarget.getAttribute('data-id'); await Api.put('/api/leaves/' + id + '/status', { status: 'APPROVED' }); App.showToast('Approved','success'); await loadLeaves(); }));
      document.querySelectorAll('.btn-reject').forEach(b => b.addEventListener('click', async ev => { const id = ev.currentTarget.getAttribute('data-id'); await Api.put('/api/leaves/' + id + '/status', { status: 'REJECTED' }); App.showToast('Rejected','success'); await loadLeaves(); }));
    } catch (e) { App.showToast('Load failed','error'); }
    finally { App.setLoaderVisible(false); }
  }

  function openModal() { document.getElementById('leaveModal').classList.remove('hidden'); }
  function closeModal(){ document.getElementById('leaveModal').classList.add('hidden'); }

  async function saveLeave(){ const emp = document.getElementById('leaveEmployeeId').value; const start = document.getElementById('leaveStart').value; const end = document.getElementById('leaveEnd').value; const reason = document.getElementById('leaveReason').value; if (!emp) return App.showToast('Select employee','error'); try { await Api.post('/api/leaves', { employeeId: Number(emp), startDate: start, endDate: end, reason }); App.showToast('Requested','success'); closeModal(); await loadLeaves(); } catch(e){ App.showToast('Save failed','error'); } }

  return { init };
})();
