// Shared frontend helpers: API base, layout, loader, toast, and confirmation modal.
(function () {
  const API_BASE = "http://localhost:8080";

  function escapeHtml(str) {
    return String(str)
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
  }

  function setLoaderVisible(visible) {
    let overlay = document.getElementById("loaderOverlay");
    if (!overlay) {
      overlay = document.createElement("div");
      overlay.id = "loaderOverlay";
      overlay.className = "loader-overlay hidden";
      overlay.innerHTML = "<div class='loader'><span class='dot'></span><strong>Loading...</strong></div>";
      document.body.appendChild(overlay);
    }
    overlay.classList.toggle("hidden", !visible);
  }

  function showToast(message, type) {
    const toast = document.createElement("div");
    toast.className = "toast " + (type === "error" ? "toast--error" : "toast--success");
    toast.innerHTML =
      "<strong>" + (type === "error" ? "Error" : "Success") + "</strong><br>" +
      "<span>" + escapeHtml(message) + "</span>";
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3200);
  }

  function confirmDialog(message) {
    return new Promise((resolve) => {
      const backdrop = document.createElement("div");
      backdrop.className = "modal-backdrop";
      backdrop.innerHTML = `
        <div class="modal" style="max-width:430px;">
          <header><h3>Confirm action</h3></header>
          <div class="content"><p style="margin:0;color:var(--muted);">${escapeHtml(message)}</p></div>
          <footer>
            <button class="btn btn--ghost" type="button" data-value="no">Cancel</button>
            <button class="btn btn--danger" type="button" data-value="yes">Delete</button>
          </footer>
        </div>
      `;
      backdrop.addEventListener("click", (event) => {
        const btn = event.target.closest("button[data-value]");
        if (!btn) return;
        const ok = btn.getAttribute("data-value") === "yes";
        backdrop.remove();
        resolve(ok);
      });
      document.body.appendChild(backdrop);
    });
  }

  function mediaUrl(url) {
    if (!url) return "";
    if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("blob:")) {
      return url;
    }
    if (url.startsWith("/")) {
      return API_BASE + url;
    }
    return url;
  }

  function initLayout() {
    const menuBtn = document.getElementById("mobileMenuBtn");
    if (menuBtn) {
      menuBtn.addEventListener("click", () => document.body.classList.toggle("sidebar-open"));
    }

    // mark active link based on current path
    const path = window.location.pathname.split('/').pop() || 'dashboard.html';
    const activePath = path === "profile.html" ? "employees.html" : path;
    document.querySelectorAll('.sidebar .nav a').forEach((link) => {
      try { if (link.getAttribute('href') === activePath) link.classList.add('active'); else link.classList.remove('active'); } catch(e){}
      link.addEventListener("click", () => document.body.classList.remove("sidebar-open"));
    });
  }

  function getSessionLoggedIn() {
    return fetch(API_BASE + "/api/auth/me", {
      method: "GET",
      credentials: "include",
      headers: { "Accept": "application/json" }
    }).then((r) => r.json());
  }

  window.App = {
    API_BASE,
    escapeHtml,
    setLoaderVisible,
    showToast,
    confirmDialog,
    mediaUrl,
    initLayout,
    getSessionLoggedIn
  };

  document.addEventListener("DOMContentLoaded", initLayout);
})();
