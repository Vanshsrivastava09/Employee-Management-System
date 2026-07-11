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
      
      const filteredItems = items.filter(it => !filter || String(it.employee.id) === filter);
      
      if (filteredItems.length === 0) {
        body.innerHTML = `
          <tr>
            <td colspan="4">
              <div class="empty-state">
                <div class="empty-state-icon">📝</div>
                <div class="empty-state-title">No requests found</div>
                <div class="empty-state-description">
                  ${filter ? 'Try selecting a different employee.' : 'Handle employee requests and approval workflows.'}
                </div>
                ${!filter ? '<div class="empty-state-action"><button class="btn btn--primary" onclick="document.getElementById(\'createRequestBtn\').click()">Create Request</button></div>' : ''}
              </div>
            </td>
          </tr>
        `;
      } else {
        const rows = filteredItems.map(it => {
          const statusClass = it.status === 'APPROVED' ? 'pill--active' : it.status === 'REJECTED' ? 'pill--inactive' : 'pill--warning';
          const statusText = it.status || 'PENDING';
          return `
          <tr>
            <td>${App.escapeHtml(it.employee.fullName)}</td>
            <td>${App.escapeHtml(it.title)}</td>
            <td><span class="pill ${statusClass}">${App.escapeHtml(statusText)}</span></td>
            <td style="text-align:right;"><button data-id="${it.id}" class="btn btn--soft btn-approve">Approve</button> <button data-id="${it.id}" class="btn btn--danger btn-reject">Reject</button></td>
          </tr>
        `}).join('');
        body.innerHTML = rows;
      }

      document.querySelectorAll('.btn-approve').forEach(b => b.addEventListener('click', async ev => { const id = ev.currentTarget.getAttribute('data-id'); await Api.put('/api/requests/' + id + '/status', { status: 'APPROVED' }); App.showToast('Approved','success'); await loadRequests(); }));
      document.querySelectorAll('.btn-reject').forEach(b => b.addEventListener('click', async ev => { const id = ev.currentTarget.getAttribute('data-id'); await Api.put('/api/requests/' + id + '/status', { status: 'REJECTED' }); App.showToast('Rejected','success'); await loadRequests(); }));

    } catch (e) { App.showToast('Load failed','error'); }
    finally { App.setLoaderVisible(false); }
  }

  function openModal(){ document.getElementById('requestModal').classList.remove('hidden'); }
  function closeModal(){ document.getElementById('requestModal').classList.add('hidden'); }

  async function saveRequest(){ 
    const emp = document.getElementById('requestEmployeeId').value; 
    const title = document.getElementById('requestTitle').value; 
    const desc = document.getElementById('requestDescription').value;
    
    // Enhanced validation
    let hasError = false;
    
    if (!emp) {
      document.getElementById('requestEmployeeId').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('requestEmployeeId').style.borderColor = 'var(--border)';
    }
    
    if (!title || title.trim().length < 3) {
      document.getElementById('requestTitle').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('requestTitle').style.borderColor = 'var(--border)';
    }
    
    if (!desc || desc.trim().length < 10) {
      document.getElementById('requestDescription').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('requestDescription').style.borderColor = 'var(--border)';
    }
    
    if (hasError) {
      App.showToast('Please fix the highlighted fields', 'error');
      return;
    }
    
    try { 
      await Api.post('/api/requests', { employeeId: Number(emp), title: title.trim(), description: desc.trim() }); 
      App.showToast('Created','success'); 
      closeModal(); 
      await loadRequests(); 
    } catch(e){ App.showToast('Save failed','error'); } 
  }

  return { init };
})();
