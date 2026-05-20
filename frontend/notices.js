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
      const rows = items.filter(it => !q || (it.title||'').toLowerCase().includes(q) || (it.message||'').toLowerCase().includes(q)).map(it => `
        <tr>
          <td>${App.escapeHtml(it.title)}</td>
          <td>${App.escapeHtml((it.message||'').slice(0,120))}</td>
          <td>${App.escapeHtml(it.priority||'')}</td>
          <td style="text-align:right;"><button data-id="${it.id}" class="btn btn--soft btn-edit">Edit</button> <button data-id="${it.id}" class="btn btn--danger btn-delete">Delete</button></td>
        </tr>
      `).join('') || '<tr><td colspan="4">No notices</td></tr>';
      body.innerHTML = rows;

      document.querySelectorAll('.btn-delete').forEach(b => b.addEventListener('click', async ev => { const id = ev.currentTarget.getAttribute('data-id'); const ok = await App.confirmDialog('Delete notice?'); if (!ok) return; await Api.del('/api/notices/' + id); App.showToast('Deleted','success'); await loadNotices(); }));
      document.querySelectorAll('.btn-edit').forEach(b => b.addEventListener('click', async ev => { const id = ev.currentTarget.getAttribute('data-id'); const res = await Api.get('/api/notices/' + id); openModal(res.data); }));
    } catch (e) { App.showToast('Load failed','error'); }
    finally { App.setLoaderVisible(false); }
  }

  function openModal(n) { const modal = document.getElementById('noticeModal'); document.getElementById('noticeId').value = n ? n.id : ''; document.getElementById('noticeTitle').value = n ? n.title : ''; document.getElementById('noticeMessage').value = n ? n.message : ''; document.getElementById('noticePriority').value = n ? (n.priority||'LOW') : 'LOW'; modal.classList.remove('hidden'); }
  function closeModal(){ document.getElementById('noticeModal').classList.add('hidden'); }

  async function saveNotice(){ const id = document.getElementById('noticeId').value; const payload = { title: document.getElementById('noticeTitle').value, message: document.getElementById('noticeMessage').value, priority: document.getElementById('noticePriority').value }; try { if (id) { await Api.put('/api/notices/' + id, payload); App.showToast('Updated','success'); } else { await Api.post('/api/notices', payload); App.showToast('Created','success'); } closeModal(); await loadNotices(); } catch(e){ App.showToast('Save failed','error'); } }

  return { init };
})();
