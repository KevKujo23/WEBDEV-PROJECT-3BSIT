// Tabs
document.querySelectorAll('.tab').forEach(btn => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.tab').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
    btn.classList.add('active');
    document.getElementById(btn.dataset.tab).classList.add('active');
  });
});

// Toast auto-hide
const toast = document.getElementById('toast');
if (toast) setTimeout(() => toast.classList.add('hide'), 2200);

(function () {
  const $  = (q, r=document) => r.querySelector(q);
  const $$ = (q, r=document) => Array.from(r.querySelectorAll(q));

  // ---------- Professor modal ----------
  const profModal = $("#profModal");
  const back      = $("#modalBackdrop");
  const profForm  = $("#profForm");
  const profTitle = $("#profModalTitle");
  const profId    = $("#profId");
  const profName  = $("#profName");
  const profDept  = $("#profDept");
  const profSubs  = $("#profSubjects");

  function openProf(titleText) {
    profTitle.textContent = titleText;
    profModal.classList.remove("hidden");
    back.classList.remove("hidden");
  }
  function closeAll(e) {
    if (e) e.preventDefault();
    profModal?.classList.add("hidden");
    subjModal?.classList.add("hidden");
    back?.classList.add("hidden");
  }

  $("#btnAddProfessor")?.addEventListener("click", () => {
    profForm.reset(); profId.value = "";
    if (profDept?.options.length) profDept.selectedIndex = 0;
    if (profSubs) Array.from(profSubs.options).forEach(o => o.selected = false);
    openProf("Add Professor");
  });

  // Edit Professor — works for cards or table rows
  $$(".btnEditProf").forEach(btn => {
    btn.addEventListener("click", () => {
      // Prefer card container; fallback to table row
      const card = btn.closest(".prof-card");
      const row  = btn.closest("tr");

      const host = card || row;
      if (!host) return;

      const ds = host.dataset || {};
      profId.value   = ds.id   || "";
      profName.value = ds.name || "";
      const deptVal  = ds.dept || "";
      if (deptVal && profDept) profDept.value = deptVal;

      const subjCsv = ds.subjects || "";
      if (profSubs) {
        const selected = new Set(subjCsv ? subjCsv.split(",") : []);
        Array.from(profSubs.options).forEach(o => o.selected = selected.has(o.value));
      }
      openProf("Edit Professor");
    });
  });

  back?.addEventListener("click", closeAll);
  $$("[data-close]", profModal).forEach(el => el.addEventListener("click", closeAll));
  document.addEventListener("keydown", e => { if (e.key === "Escape") closeAll(e); });

  // Delete confirmation (shared)
  $$(".confirmDelete").forEach(f => {
    f.addEventListener("submit", e => {
      if (!confirm("Delete this item? This cannot be undone.")) e.preventDefault();
    });
  });

  // ---------- Professors filters (cards or table) ----------
  const profSearch = $("#profSearch");
  const filterDept = $("#filterDept");

  function filterProfUI() {
    const q = (profSearch?.value || "").toLowerCase();
    const d = filterDept?.value || "";

    const cards = $$(".prof-card");
    const tableRows = $$("#profTable tbody tr");

    if (cards.length) {
      cards.forEach(card => {
        const name = (card.dataset.name || "").toLowerCase();
        const dept = card.dataset.dept || "";
        const show = (!q || name.includes(q)) && (!d || d === dept);
        card.style.display = show ? "" : "none";
      });
    } else if (tableRows.length) {
      tableRows.forEach(tr => {
        const name = (tr.dataset.name || "").toLowerCase();
        const dept = tr.dataset.dept || "";
        const show = (!q || name.includes(q)) && (!d || d === dept);
        tr.style.display = show ? "" : "none";
      });
    }
  }
  profSearch?.addEventListener("input", filterProfUI);
  filterDept?.addEventListener("change", filterProfUI);

  // ---------- Ratings filters (table or card feed) ----------
  function filterRatings() {
    const s = $("#scoreFilter")?.value || "";
    const y = $("#yearFilter")?.value || "";
    const t = ($("#termFilter")?.value || "").toLowerCase();
    const q = ($("#ratingSearch")?.value || "").toLowerCase();

    const table = document.querySelector("#ratingTable tbody");
    const feed  = document.getElementById("ratingFeed");

    if (table) {
      table.querySelectorAll("tr").forEach(tr => {
        const score = tr.dataset.score || "";
        const year  = tr.dataset.year || "";
        const term  = (tr.dataset.term || "").toLowerCase();
        const text  = tr.innerText.toLowerCase();
        const ok = (!s || s === score) && (!y || y === year) && (!t || term === t) && (!q || text.includes(q));
        tr.style.display = ok ? "" : "none";
      });
    } else if (feed) {
      feed.querySelectorAll(".rating-card").forEach(card => {
        const score = card.dataset.score || "";
        const year  = card.dataset.year || "";
        const term  = (card.dataset.term || "").toLowerCase();
        const text  = card.innerText.toLowerCase();
        const ok = (!s || s === score) && (!y || y === year) && (!t || term === t) && (!q || text.includes(q));
        card.style.display = ok ? "" : "none";
      });
    }
  }
  ["scoreFilter","yearFilter","termFilter","ratingSearch"].forEach(id=>{
    const el = document.getElementById(id); if (el) el.addEventListener("input", filterRatings);
  });

  // ---------- Subjects tab ----------
  const subjModal   = $("#subjectModal");
  const subjForm    = $("#subjectForm");
  const subjTitle   = $("#subjectModalTitle");
  const subjId      = $("#subjectId");
  const subjCode    = $("#subjectCode");
  const subjTitleIn = $("#subjectTitle");
  const subjDept    = $("#subjectDept");

  function openSubj(titleText) {
    subjTitle.textContent = titleText;
    subjModal.classList.remove("hidden");
    back.classList.remove("hidden");
  }

  $("#btnAddSubject")?.addEventListener("click", () => {
    subjForm.reset(); subjId.value = "";
    if (subjDept?.options.length) subjDept.selectedIndex = 0;
    openSubj("Add Subject");
  });

  $$(".btnEditSubject").forEach(btn => {
    btn.addEventListener("click", () => {
      const tr = btn.closest("tr");
      if (!tr) return;
      subjId.value      = tr.dataset.id || "";
      subjCode.value    = tr.dataset.code || "";
      subjTitleIn.value = tr.dataset.title || "";
      const deptVal     = tr.dataset.dept || "";
      if (deptVal && subjDept) subjDept.value = deptVal;
      openSubj("Edit Subject");
    });
  });

  $$("[data-close]", subjModal).forEach(el => el.addEventListener("click", closeAll));

  // Subject quick search
  const subjectSearch = $("#subjectSearch");
  subjectSearch?.addEventListener("input", () => {
    const q = subjectSearch.value.toLowerCase();
    document.querySelectorAll("#subjectTable tbody tr").forEach(tr => {
      const text = tr.innerText.toLowerCase();
      tr.style.display = (!q || text.includes(q)) ? "" : "none";
    });
  });
})();
