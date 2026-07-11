const AttendanceUI = (function () {
  async function init() {
    App.setLoaderVisible(true);
    try {
      const employeesRes = await Api.get('/api/employees?size=1000&page=0');
      const employees = employeesRes.data && employeesRes.data.items ? employeesRes.data.items : [];
      const empSelect = document.getElementById('attendanceEmployeeFilter');
      const empSelectModal = document.getElementById('attendanceEmployeeId');
      employees.forEach(e => { const o = document.createElement('option'); o.value = e.id; o.textContent = e.fullName; empSelect.appendChild(o); const o2 = o.cloneNode(true); empSelectModal.appendChild(o2); });

      document.getElementById('createAttendanceBtn').addEventListener('click', ()=> openModal());
      document.getElementById('closeAttendanceModal').addEventListener('click', ()=>closeModal());
      document.getElementById('saveAttendanceBtn').addEventListener('click', saveAttendance);
      document.getElementById('attendanceEmployeeFilter').addEventListener('change', loadAttendances);
      document.getElementById('attendanceDateFilter').addEventListener('change', loadAttendances);

      await loadAttendances();
    } catch (e) { App.showToast('Init failed','error'); }
    finally { App.setLoaderVisible(false); }
  }

  async function loadAttendances() {
    App.setLoaderVisible(true);
    try {
      const res = await Api.get('/api/attendances');
      const items = res.data && res.data.items ? res.data.items : [];
      const empFilter = document.getElementById('attendanceEmployeeFilter').value;
      const dateFilter = document.getElementById('attendanceDateFilter').value;
      const body = document.getElementById('attendancesTableBody');
      
      const filteredItems = items.filter(it => (!empFilter || String(it.employee.id)===empFilter) && (!dateFilter || it.date===dateFilter));
      
      if (filteredItems.length === 0) {
        body.innerHTML = `
          <tr>
            <td colspan="5">
              <div class="empty-state">
                <div class="empty-state-icon">📅</div>
                <div class="empty-state-title">No attendance records found</div>
                <div class="empty-state-description">
                  ${empFilter || dateFilter ? 'Try adjusting your filters.' : 'Start recording daily attendance for your employees.'}
                </div>
                ${!empFilter && !dateFilter ? '<div class="empty-state-action"><button class="btn btn--primary" onclick="document.getElementById(\'createAttendanceBtn\').click()">Record Attendance</button></div>' : ''}
              </div>
            </td>
          </tr>
        `;
      } else {
        const rows = filteredItems.map(it => {
          const statusClass = it.status === 'PRESENT' ? 'pill--active' : 'pill--inactive';
          return `
          <tr>
            <td>${App.escapeHtml(it.employee.fullName)}</td>
            <td>${it.date || ''}</td>
            <td><span class="pill ${statusClass}">${App.escapeHtml(it.status||'')}</span></td>
            <td>${App.escapeHtml(it.note||'')}</td>
            <td style="text-align:right;"><button data-id="${it.id}" class="btn btn--danger btn-delete">Delete</button></td>
          </tr>
        `}).join('');
        body.innerHTML = rows;
      }

      document.querySelectorAll('.btn-delete').forEach(b => b.addEventListener('click', async ev => { const id = ev.currentTarget.getAttribute('data-id'); const ok = await App.confirmDialog('Delete attendance?'); if (!ok) return; await Api.del('/api/attendances/' + id); App.showToast('Deleted','success'); await loadAttendances(); }));
    } catch (e) { App.showToast('Load failed','error'); }
    finally { App.setLoaderVisible(false); }
  }

  function openModal(){ document.getElementById('attendanceModal').classList.remove('hidden'); }
  function closeModal(){ document.getElementById('attendanceModal').classList.add('hidden'); }

  async function saveAttendance(){ 
    const emp = document.getElementById('attendanceEmployeeId').value; 
    const date = document.getElementById('attendanceDate').value; 
    const status = document.getElementById('attendanceStatus').value; 
    const note = document.getElementById('attendanceNote').value;
    
    // Enhanced validation
    let hasError = false;
    
    if (!emp) {
      document.getElementById('attendanceEmployeeId').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('attendanceEmployeeId').style.borderColor = 'var(--border)';
    }
    
    if (!date) {
      document.getElementById('attendanceDate').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('attendanceDate').style.borderColor = 'var(--border)';
    }
    
    if (hasError) {
      App.showToast('Please fix the highlighted fields', 'error');
      return;
    }
    
    try { 
      await Api.post('/api/attendances', { employeeId: Number(emp), date, status, note }); 
      App.showToast('Saved','success'); 
      closeModal(); 
      await loadAttendances(); 
    } catch(e){ App.showToast('Save failed','error'); } 
  }

  return { init };
})();
