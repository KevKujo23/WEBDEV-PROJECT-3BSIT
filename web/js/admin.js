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
  const $ = (q, r=document) => r.querySelector(q);
  const $$ = (q, r=document) => Array.from(r.querySelectorAll(q));

  const modal   = $("#profModal");
  const back    = $("#modalBackdrop");
  const form    = $("#profForm");
  const title   = $("#profModalTitle");
  const idInp   = $("#profId");
  const nameInp = $("#profName");
  const deptSel = $("#profDept");

  function open(titleText) {
    title.textContent = titleText;
    modal.classList.remove("hidden");
    back.classList.remove("hidden");
  }
  function close() {
    modal.classList.add("hidden");
    back.classList.add("hidden");
  }

  // Add
  $("#btnAddProfessor")?.addEventListener("click", () => {
    form.reset();
    idInp.value = "";
    if (deptSel.options.length) deptSel.selectedIndex = 0; // default first dept
    open("Add Professor");
  });

  // Edit (prefill from <tr data-*>)
  $$(".btnEditProf").forEach(btn => {
    btn.addEventListener("click", () => {
      const tr = btn.closest("tr");
      idInp.value   = tr.dataset.id || "";
      nameInp.value = tr.dataset.name || "";
      const deptVal = tr.dataset.dept || "";
      if (deptVal) deptSel.value = deptVal;
      open("Edit Professor");
    });
  });

  // Close handlers
  back.addEventListener("click", close);
  $("[data-close]", modal)?.addEventListener("click", close);

  // Delete confirmation
  $$(".confirmDelete").forEach(f => {
    f.addEventListener("submit", (e) => {
      if (!confirm("Delete this item? This cannot be undone.")) e.preventDefault();
    });
  });

  // Professors: search + dept filter (client-side)
  const profSearch = $("#profSearch");
  const filterDept = $("#filterDept");
  function filterProfTable(){
    const q = (profSearch?.value || '').toLowerCase();
    const d = filterDept?.value || '';
    document.querySelectorAll('#profTable tbody tr').forEach(tr=>{
      const name = (tr.dataset.name || '').toLowerCase();
      const dept = tr.dataset.dept || '';
      const show = (!q || name.includes(q)) && (!d || d===dept);
      tr.style.display = show ? '' : 'none';
    });
  }
  profSearch?.addEventListener('input', filterProfTable);
  filterDept?.addEventListener('change', filterProfTable);

  // Ratings: quick filters + search (client-side)
  function filterRatings(){
    const s = $("#scoreFilter")?.value || '';
    const y = $("#yearFilter")?.value || '';
    const t = ($("#termFilter")?.value || '').toLowerCase();
    const q = ($("#ratingSearch")?.value || '').toLowerCase();
    document.querySelectorAll('#ratingTable tbody tr').forEach(tr=>{
      const score = tr.dataset.score || '';
      const year  = tr.dataset.year || '';
      const term  = (tr.dataset.term || '').toLowerCase();
      const text  = tr.innerText.toLowerCase();
      const ok = (!s || s===score) && (!y || y===year) && (!t || term===t) && (!q || text.includes(q));
      tr.style.display = ok ? '' : 'none';
    });
  }
  ['scoreFilter','yearFilter','termFilter','ratingSearch'].forEach(id=>{
    const el = document.getElementById(id); if (el) el.addEventListener('input', filterRatings);
  });
})();
