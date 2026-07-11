const NoticesUI = (function () {
  async function init() {
    App.setLoaderVisible(true);
    try {
      document.getElementById('createNoticeBtn').addEventListener('click', ()=>openModal());
      document.getElementById('closeNoticeModal').addEventListener('click', ()=>closeModal());
      document.getElementById('saveNoticeBtn').addEventListener('click', saveNotice);
      document.getElementById('searchNotice').addEventListener('input', loadNotices);
      await loadNotices();
    } catch (e) { App.showToast('Failed to init', 'error'); }
    finally { App.setLoaderVisible(false); }
  }

  async function loadNotices() {
    App.setLoaderVisible(true);
    try {
      const res = await Api.get('/api/notices');
      const items = res.data && res.data.items ? res.data.items : [];
      const q = (document.getElementById('searchNotice').value || '').toLowerCase();
      const body = document.getElementById('noticesTableBody');
      
      const filteredItems = items.filter(it => !q || (it.title||'').toLowerCase().includes(q) || (it.message||'').toLowerCase().includes(q));
      
      if (filteredItems.length === 0) {
        body.innerHTML = `
          <tr>
            <td colspan="4">
              <div class="empty-state">
                <div class="empty-state-icon">📢</div>
                <div class="empty-state-title">No notices found</div>
                <div class="empty-state-description">
                  ${q ? 'Try a different search term.' : 'Create and manage HR notices and announcements.'}
                </div>
                ${!q ? '<div class="empty-state-action"><button class="btn btn--primary" onclick="document.getElementById(\'createNoticeBtn\').click()">Add Notice</button></div>' : ''}
              </div>
            </td>
          </tr>
        `;
      } else {
        const rows = filteredItems.map(it => {
          const priorityClass = it.priority === 'HIGH' ? 'pill--warning' : it.priority === 'MEDIUM' ? 'pill--info' : 'pill--active';
          return `
          <tr>
            <td>${App.escapeHtml(it.title)}</td>
            <td>${App.escapeHtml((it.message||'').slice(0,120))}</td>
            <td><span class="pill ${priorityClass}">${App.escapeHtml(it.priority||'NORMAL')}</span></td>
            <td style="text-align:right;"><button data-id="${it.id}" class="btn btn--soft btn-edit">Edit</button> <button data-id="${it.id}" class="btn btn--danger btn-delete">Delete</button></td>
          </tr>
        `}).join('');
        body.innerHTML = rows;
      }

      document.querySelectorAll('.btn-delete').forEach(b => b.addEventListener('click', async ev => { const id = ev.currentTarget.getAttribute('data-id'); const ok = await App.confirmDialog('Delete notice?'); if (!ok) return; await Api.del('/api/notices/' + id); App.showToast('Deleted','success'); await loadNotices(); }));
      document.querySelectorAll('.btn-edit').forEach(b => b.addEventListener('click', async ev => { const id = ev.currentTarget.getAttribute('data-id'); const res = await Api.get('/api/notices/' + id); openModal(res.data); }));
    } catch (e) { App.showToast('Load failed','error'); }
    finally { App.setLoaderVisible(false); }
  }

  function openModal(n) { const modal = document.getElementById('noticeModal'); document.getElementById('noticeId').value = n ? n.id : ''; document.getElementById('noticeTitle').value = n ? n.title : ''; document.getElementById('noticeMessage').value = n ? n.message : ''; document.getElementById('noticePriority').value = n ? (n.priority||'LOW') : 'LOW'; modal.classList.remove('hidden'); }
  function closeModal(){ document.getElementById('noticeModal').classList.add('hidden'); }

  async function saveNotice(){ 
    const id = document.getElementById('noticeId').value; 
    const title = document.getElementById('noticeTitle').value; 
    const message = document.getElementById('noticeMessage').value; 
    const priority = document.getElementById('noticePriority').value;
    
    // Enhanced validation
    let hasError = false;
    
    if (!title || title.trim().length < 3) {
      document.getElementById('noticeTitle').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('noticeTitle').style.borderColor = 'var(--border)';
    }
    
    if (!message || message.trim().length < 10) {
      document.getElementById('noticeMessage').style.borderColor = 'var(--danger)';
      hasError = true;
    } else {
      document.getElementById('noticeMessage').style.borderColor = 'var(--border)';
    }
    
    if (hasError) {
      App.showToast('Please fix the highlighted fields', 'error');
      return;
    }
    
    const payload = { title: title.trim(), message: message.trim(), priority }; 
    try { 
      if (id) { 
        await Api.put('/api/notices/' + id, payload); 
        App.showToast('Updated','success'); 
      } else { 
        await Api.post('/api/notices', payload); 
        App.showToast('Created','success'); 
      } 
      closeModal(); 
      await loadNotices(); 
    } catch(e){ App.showToast('Save failed','error'); } 
  }

  return { init };
})();
