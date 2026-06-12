const API_BASE = '';

async function apiFetch(method, path, body) {
  const opts = { method, headers: { 'Content-Type': 'application/json' } };
  if (body !== undefined) opts.body = JSON.stringify(body);

  const res = await fetch(API_BASE + path, opts);

  if (!res.ok) {
    const txt = await res.text().catch(() => '');
    throw new Error(`${res.status} – ${txt || res.statusText}`);
  }

  if (res.status === 204) return null;
  const ct = res.headers.get('content-type') || '';
  return ct.includes('json') ? res.json() : null;
}

function listarSolicitacoes({ prioridade, bairro, categoriaNome } = {}) {
  const params = new URLSearchParams();
  if (prioridade)    params.append('prioridade', prioridade);
  if (bairro)        params.append('bairro', bairro);
  if (categoriaNome) params.append('categoriaNome', categoriaNome);
  const qs = params.toString() ? '?' + params.toString() : '';
  return apiFetch('GET', '/api/solicitacoes' + qs);
}

function buscarPorProtocolo(protocolo) {
  return apiFetch('GET', `/api/solicitacoes/${encodeURIComponent(protocolo)}`);
}

function buscarHistorico(id) {
  return apiFetch('GET', `/api/solicitacoes/${id}/historico`);
}

function criarSolicitacao(dados) {
  return apiFetch('POST', '/api/solicitacoes', dados);
}

function atualizarStatus(id, dados) {
  return apiFetch('PATCH', `/api/solicitacoes/${id}/status`, dados);
}
