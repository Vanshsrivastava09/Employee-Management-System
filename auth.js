/* Session-based authentication helpers (uses cookie via credentials: 'include') */
(function () {
  async function login(username, password) {
    const res = await fetch(App.API_BASE + "/api/auth/login", {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json", "Accept": "application/json" },
      body: JSON.stringify({ username, password })
    });

    const data = await res.json();
    if (!res.ok || !data || data.success === false) {
      throw new Error((data && data.message) ? data.message : "Login failed");
    }
    return data;
  }

  async function logout() {
    await fetch(App.API_BASE + "/api/auth/logout", {
      method: "POST",
      credentials: "include",
      headers: { "Accept": "application/json" }
    }).catch(() => {});
  }

  async function requireAuth() {
    const data = await App.getSessionLoggedIn();
    if (!data || data.success === false || !data.data || data.data.loggedIn !== true) {
      window.location.href = "index.html";
      return false;
    }
    return true;
  }

  window.Auth = { login, logout, requireAuth };
})();
