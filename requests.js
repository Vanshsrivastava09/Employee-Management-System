const RequestsUI = (function () {
  async function init() {
    App.setLoaderVisible(true);
    try {
      const employeesRes = await Api.get('/api/employees?size=1000&page=0');
      const employees = employeesRes.data && employeesRes.data.items ? employeesRes.data.items : [];
      const empSelect = document.getElementById('requestEmployeeFilter');
      const empSelectModal = document.getElementById('requestEmployeeId');
      employees.forEach(e => { const o = document.createElement('option'); o.value = e.id; o.textContent = e.fullName; empSelect.appendChild(o); const o2 = o.cloneNode(true); empSelectModal.appendChild(o2); });

      document.getElementById('createRequestBtn').addEventListener('click', ()=> openModal());
      document.getElementById('closeRequestModal').addEventListener('click', ()=>closeModal());
      document.getElementById('saveRequestBtn').addEventListener('click', saveRequest);
      document.getElementById('requestEmployeeFilter').addEventListener('change', loadRequests);

      await loadRequests();
    } catch (e) { App.showToast('Init failed','error'); }
    finally { App.setLoaderVisible(false); }
  }

  async function loadRequests() {
    App.setLoaderVisible(true);
    try {
      const res = await Api.get('/api/requests');
      const items = res.data && res.data.items ? res.data.items : [];
      const filter = document.getElementById('requestEmployeeFilter').value;
      const body = document.getElementById('requestsTableBody');
      const rows = items.filter(it => !filter || String(it.employee.id) === filter).map(it => `
        <tr>
          <td>${App.escapeHtml(it.employee.fullName)}</td>
          <td>${App.escapeHtml(it.title)}</td>
          <td>${App.escapeHtml(it.status||'')}</td>
          <td style="text-align:right;"><button data-id="${it.id}" class="btn btn--soft btn-approve">Approve</button> <button data-id="${it.id}" class="btn btn--danger btn-reject">Reject</button></td>
        </tr>
      `).join('') || '<tr><td colspan="4">No requests</td></tr>';
      body.innerHTML = rows;

      document.querySelectorAll('.btn-approve').forEach(b => b.addEventListener('click', async ev => { const id = ev.currentTarget.getAttribute('data-id'); await Api.put('/api/requests/' + id + '/status', { status: 'APPROVED' }); App.showToast('Approved','success'); await loadRequests(); }));
      document.querySelectorAll('.btn-reject').forEach(b => b.addEventListener('click', async ev => { const id = ev.currentTarget.getAttribute('data-id'); await Api.put('/api/requests/' + id + '/status', { status: 'REJECTED' }); App.showToast('Rejected','success'); await loadRequests(); }));

    } catch (e) { App.showToast('Load failed','error'); }
    finally { App.setLoaderVisible(false); }
  }

  function openModal(){ document.getElementById('requestModal').classList.remove('hidden'); }
  function closeModal(){ document.getElementById('requestModal').classList.add('hidden'); }

  async function saveRequest(){ const emp = document.getElementById('requestEmployeeId').value; const title = document.getElementById('requestTitle').value; const desc = document.getElementById('requestDescription').value; if (!emp) return App.showToast('Select employee','error'); try { await Api.post('/api/requests', { employeeId: Number(emp), title, description: desc }); App.showToast('Created','success'); closeModal(); await loadRequests(); } catch(e){ App.showToast('Save failed','error'); } }

  return { init };
})();
