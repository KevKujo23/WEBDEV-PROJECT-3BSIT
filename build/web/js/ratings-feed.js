/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

(() => {
  const ctx = document.body.getAttribute("data-ctx") || ( () => {
    const p = location.pathname; const i = p.indexOf("/",1);
    return i>0 ? p.substring(0,i) : "";
  })();

  // POST new rating via classic form submit (server-validated)
  const composer = document.getElementById("ratingComposer");
  if (composer) {
    composer.addEventListener("submit", () => {
      // Optional: quick client-side trim/guard
      const txt = composer.querySelector("#comment");
      if (txt && txt.value.trim().length < 5) {
        alert("Please write at least 5 characters.");
        event.preventDefault();
      }
    });
  }

  // Example: Like/Report/Reply hooks (wired to server endpoints later)
  document.getElementById("feedList")?.addEventListener("click", async (e) => {
    const btn = e.target.closest(".link");
    if (!btn) return;

    const id = btn.dataset.id;
    if (btn.classList.contains("action-like")) {
      // TODO: call /rating/like?id=...
      console.log("like", id);
    } else if (btn.classList.contains("action-report")) {
      // TODO: call /rating/report?id=...
      console.log("report", id);
    } else if (btn.classList.contains("action-reply")) {
      // TODO: open inline reply composer
      console.log("reply", id);
    }
  });

  // Optional: infinite scroll for older ratings
  let page = 1, loading = false, hasMore = true;
  window.addEventListener("scroll", async () => {
    if (loading || !hasMore) return;
    if (window.innerHeight + window.scrollY < document.body.offsetHeight - 400) return;
    loading = true;
    document.getElementById("feedLoader").hidden = false;
    try {
      const profId = document.querySelector("input[name='profId']")?.value;
      const res = await fetch(`${ctx}/rating/list?profId=${encodeURIComponent(profId)}&page=${page}`);
      if (!res.ok) throw new Error("Failed");
      const data = await res.json();
      hasMore = data.items?.length > 0;
      appendRatings(data.items || []);
      page++;
    } catch (err) {
      console.error(err);
    } finally {
      loading = false;
      document.getElementById("feedLoader").hidden = true;
    }
  });

  function appendRatings(items) {
    const feed = document.getElementById("feedList");
    const frag = document.createDocumentFragment();
    for (const r of items) {
      const el = document.createElement("article");
      el.className = "feed-item card";
      el.dataset.id = r.ratingId;
      el.innerHTML = `
        <img class="avatar" src="${ctx}/img/default-avatar.png" alt="">
        <div class="content">
          <header class="meta">
            <div class="who"><span class="name">${escapeHtml(r.userName)}</span>
              <span class="dot">•</span><span class="time">${escapeHtml(r.prettyCreatedAt)}</span>
            </div>
            <div class="score">⭐ ${Number(r.score).toFixed(0)}/5</div>
          </header>
          <div class="text">${escapeHtml(r.comment)}</div>
          <footer class="actions">
            <button class="link action-like" data-id="${r.ratingId}">Like</button>
            <button class="link action-reply" data-id="${r.ratingId}">Reply</button>
            <button class="link action-report" data-id="${r.ratingId}">Report</button>
          </footer>
        </div>`;
      frag.appendChild(el);
    }
    feed.appendChild(frag);
  }

  function escapeHtml(s) {
    return String(s ?? "").replace(/[&<>"'\/]/g, c => ({
      "&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;","'":"&#39;","/":"&#47;"
    }[c]));
  }
})();
