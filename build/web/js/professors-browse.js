/* 
 * Browse Professors – client
 */

(() => {
  // Robust context-path: prefer data-ctx from JSP; fallback to first path segment.
  const body = document.body;
  const ctxAttr = body.getAttribute("data-ctx");
  const ctx = (ctxAttr && ctxAttr !== "/") 
    ? ctxAttr 
    : (() => {
        const p = window.location.pathname; // e.g. /ProjectWebDev/professors
        const i = p.indexOf("/", 1);
        return i > 0 ? p.substring(0, i) : "";
      })();

  const grid   = document.getElementById('grid');
  const loader = document.getElementById('loader');
  const empty  = document.getElementById('empty');

  const q         = document.getElementById('q');
  const course    = document.getElementById('course');
  const dept      = document.getElementById('dept');
  const minRating = document.getElementById('minRating');
  const sort      = document.getElementById('sort');

  const chips = document.getElementById('recent-chips');

  let page = 0;
  let hasNext = true;
  let fetching = false;
  let lastQueryKey = '';

  const debounce = (fn, ms = 300) => {
    let t; return (...args) => { clearTimeout(t); t = setTimeout(() => fn(...args), ms); };
  };

  const queryKey = () => JSON.stringify({
    q: q.value.trim(),
    course: course.value.trim(),
    dept: dept.value || '',
    minRating: minRating.value || '',
    sort: sort.value
  });

  async function fetchPage(reset = false) {
    if (fetching) return;
    if (reset) {
      page = 0; hasNext = true;
      grid.innerHTML = '';
      empty.hidden = true;
    }
    if (!hasNext) return;

    fetching = true; loader.hidden = false;

    const params = new URLSearchParams({
      q: q.value.trim(),
      course: course.value.trim(),
      dept: dept.value || '',
      minRating: minRating.value || '',
      sort: sort.value,
      page: String(page),
      size: '12'
    });

    try {
      const res = await fetch(`${ctx}/api/professors/search?` + params.toString(), {
        headers: { 'Accept': 'application/json' }
      });
      const data = await res.json();
      hasNext = !!data.hasNext;
      renderCards(data.items || []);
      if ((data.items || []).length === 0 && page === 0) empty.hidden = false;
      page++;
    } catch (e) {
      console.error(e);
    } finally {
      fetching = false; loader.hidden = true;
    }
  }

  function renderCards(items) {
    const frag = document.createDocumentFragment();
    for (const it of items) {
      const el = document.createElement('article');
      el.className = 'card';
      el.innerHTML = `
        <div class="title">
          <div>
            <div class="name">${escapeHtml(it.name)}</div>
            <div class="dept">${escapeHtml(it.deptName || '')}</div>
          </div>
          <div class="meta">
            <span title="Average Rating">⭐ ${Number(it.avgRating || 0).toFixed(2)}</span>
            <span title="Rating Count">📊 ${it.ratingCount || 0}</span>
          </div>
        </div>
        ${Array.isArray(it.topTags) && it.topTags.length
          ? `<div class="badges">${it.topTags.slice(0,3).map(t => `<span class="badge">${escapeHtml(t)}</span>`).join('')}</div>`
          : ``}
        ${it.recentComment ? `<div class="comment">“${escapeHtml(it.recentComment)}”</div>` : ``}
        <div class="actions">
          <!-- View goes to your existing servlet -->
          <a class="btn" href="${ctx}/do.professor.view?id=${it.id}">View</a>
          <!-- Rate also goes to the professor view, where the POST form to /do.ratings exists -->
          <a class="btn" href="${ctx}/do.professor.view?id=${it.id}#rate">Rate</a>
        </div>
      `;
      frag.appendChild(el);
    }
    grid.appendChild(frag);
  }

  function escapeHtml(s) {
    if (s == null) return '';
    return s.replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
  }

  const onChange = debounce(() => {
    const key = queryKey();
    if (key !== lastQueryKey) {
      lastQueryKey = key;
      rememberSearch();
      fetchPage(true);
    }
  }, 300);

  [q, course, dept, minRating, sort].forEach(el => el.addEventListener('input', onChange));
  [dept, sort].forEach(el => el.addEventListener('change', onChange));

  // Infinite scroll
  const sentinel = document.createElement('div');
  loader.insertAdjacentElement('afterend', sentinel);
  new IntersectionObserver(entries => {
    entries.forEach(e => { if (e.isIntersecting) fetchPage(false); });
  }, { rootMargin: '600px' }).observe(sentinel);

  // Recent searches (localStorage)
  function rememberSearch() {
    try {
      const key = 'rmp_recent_searches';
      const list = JSON.parse(localStorage.getItem(key) || '[]');
      const qk = JSON.parse(queryKey());
      const nice = makeSearchLabel(qk);
      const updated = [{ label: nice, qk }, ...list.filter(x => x.label !== nice)].slice(0, 6);
      localStorage.setItem(key, JSON.stringify(updated));
      renderRecent(updated);
    } catch {}
  }

  function renderRecent(list = null) {
    try {
      const key = 'rmp_recent_searches';
      if (!list) list = JSON.parse(localStorage.getItem(key) || '[]');
      chips.innerHTML = '';
      list.forEach(it => {
        const btn = document.createElement('button');
        btn.className = 'chip';
        btn.type = 'button';
        btn.textContent = it.label;
        btn.addEventListener('click', () => {
          q.value = it.qk.q || '';
          course.value = it.qk.course || '';
          dept.value = it.qk.dept || '';
          minRating.value = it.qk.minRating || '';
          sort.value = it.qk.sort || 'most';
          lastQueryKey = '';
          fetchPage(true);
        });
        chips.appendChild(btn);
      });
    } catch {}
  }

  function makeSearchLabel(o) {
    const parts = [];
    if (o.q) parts.push(`Name:${o.q}`);
    if (o.course) parts.push(`Course:${o.course}`);
    if (o.dept) parts.push(`Dept:${o.dept}`);
    if (o.minRating) parts.push(`≥${o.minRating}`);
    parts.push(`Sort:${o.sort}`);
    return parts.join(' · ');
  }

  // Initial load
  renderRecent();
  fetchPage(true);
})();
