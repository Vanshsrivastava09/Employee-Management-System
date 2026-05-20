/* Simple API client wrapper (REST calls) */
(function () {
  async function get(url) {
    const res = await fetch(App.API_BASE + url, {
      method: "GET",
      credentials: "include",
      headers: { "Accept": "application/json" }
    });
    return res.json();
  }

  async function post(url, body) {
    const res = await fetch(App.API_BASE + url, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json", "Accept": "application/json" },
      body: JSON.stringify(body)
    });
    return res.json();
  }

  async function put(url, body) {
    const res = await fetch(App.API_BASE + url, {
      method: "PUT",
      credentials: "include",
      headers: { "Content-Type": "application/json", "Accept": "application/json" },
      body: JSON.stringify(body)
    });
    return res.json();
  }

  async function del(url) {
    const res = await fetch(App.API_BASE + url, {
      method: "DELETE",
      credentials: "include",
      headers: { "Accept": "application/json" }
    });
    return res.json();
  }

  window.Api = {
    get,
    post,
    put,
    del
  };
})();
