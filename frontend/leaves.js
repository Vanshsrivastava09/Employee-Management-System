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
      
      const filteredItems = items.filter(it => !filter || String(it.employee.id) === filter);
      
      if (filteredItems.length === 0) {
        body.innerHTML = `
          <tr>
            <td colspan="5">
              <div class="empty-state">
                <div class="empty-state-icon">🏖️</div>
                <div class="empty-state-title">No leave requests found</div>
                <div class="empty-state-description">
                  ${filter ? 'Try selecting a different employee.' : 'No leave requests have been submitted yet.'}
                </div>
                ${!filter ? '<div class="empty-state-action"><button class="btn btn--primary" onclick="document.getElementById(\'createLeaveBtn\').click()">Create Leave Request</button></div>' : ''}
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
            <td>${it.startDate || ''} → ${it.endDate || ''}</td>
            <td>${App.escapeHtml(it.reason || '')}</td>
            <td><span class="pill ${statusClass}">${App.escapeHtml(statusText)}</span></td>
            <td style="text-align:right;"><button data-id="${it.id}" class="btn btn--soft btn-approve">Approve</button> <button data-id="${it.id}" class="btn btn--danger btn-reject">Reject</button></td>
          </tr>
        `}).join('');
        body.innerHTML = rows;
      }

      document.querySelectorAll('.btn-approve').forEach(b => b.addEventListener('click', async ev => { const id = ev.currentTarget.getAttribute('data-id'); await Api.put('/api/leaves/' + id + '/status', { status: 'APPROVED' }); App.showToast('Approved','success'); await loadLeaves(); }));
      document.querySelectorAll('.btn-reject').forEach(b => b.addEventListener('click', async ev => { const id = ev.currentTarget.getAttribute('data-id'); await Api.put('/api/leaves/' + id + '/status', { status: 'REJECTED' }); App.showToast('Rejected','success'); await loadLeaves(); }));
    } catch (e) { App.showToast('Load failed','error'); }
    finally { App.setLoaderVisible(false); }
  }

  function openModal() { document.getElementById('leaveModal').classList.remove('hidden'); }
  function closeModal(){ document.getElementById('leaveModal').classList.add('hidden'); }

  async function saveLeave(){ 
    const emp = document.getElementById('leaveEmployeeId').value; 
    const start = document.getElementById('leaveStart').value; 
    const end = document.getElementById('leaveEnd').value; 
    const reason = document.getElementById('leaveReason').value;
    
    // Enhanced validation
    let hasError = false;
    
    if (!emp) {
      document.getElementById('leaveEmployeeId').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('leaveEmployeeId').style.borderColor = 'var(--border)';
    }
    
    if (!start) {
      document.getElementById('leaveStart').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('leaveStart').style.borderColor = 'var(--border)';
    }
    
    if (!end) {
      document.getElementById('leaveEnd').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('leaveEnd').style.borderColor = 'var(--border)';
    }
    
    if (!reason || reason.trim().length < 5) {
      document.getElementById('leaveReason').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('leaveReason').style.borderColor = 'var(--border)';
    }
    
    if (hasError) {
      App.showToast('Please fix the highlighted fields', 'error');
      return;
    }
    
    try { 
      await Api.post('/api/leaves', { employeeId: Number(emp), startDate: start, endDate: end, reason }); 
      App.showToast('Requested','success'); 
      closeModal(); 
      await loadLeaves(); 
    } catch(e){ App.showToast('Save failed','error'); } 
  }

  return { init };
})();
