const STATUS_LABEL = {
  ABERTO:      'ABERTO',
  TRIAGEM:     'TRIAGEM',
  EM_EXECUCAO: 'EM EXECUÇÃO',
  RESOLVIDO:   'RESOLVIDO',
  ENCERRADO:   'ENCERRADO',
};

function fmtStatus(s) {
  return STATUS_LABEL[(s || '').toUpperCase()] || s || '–';
}

function fmtDate(v) {
  if (!v) return '–';
  try {
    const d = new Date(v);
    return d.toLocaleDateString('pt-BR') + ' às ' +
      d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  } catch { return v; }
}

function trunc(str, n) {
  const s = str || '';
  return s.length > n ? s.substring(0, n) + '…' : s;
}

function showToast(msg, ms = 3500) {
  const t = document.getElementById('toast');
  if (!t) return;
  t.textContent = msg;
  t.classList.add('show');
  clearTimeout(t._timer);
  t._timer = setTimeout(() => t.classList.remove('show'), ms);
}

function getParam(name) {
  return new URLSearchParams(window.location.search).get(name);
}
