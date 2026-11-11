const CORES = { NORMAL: 'success', ATENCAO: 'warning', CRITICO: 'danger' }

function ListaAlertas({ alertas }) {
  return (
    <div className="card shadow-sm border-0 mt-3">
      <div className="card-body">
        <h2 className="h6 text-muted text-uppercase mb-3">
          Alertas abertos ({alertas.length})
        </h2>
        {alertas.length === 0 ? (
          <p className="text-muted mb-0">Nenhum alerta aberto.</p>
        ) : (
          <ul className="list-group list-group-flush">
            {alertas.map((a) => (
              <li
                key={a.id}
                className="list-group-item d-flex justify-content-between align-items-center px-0"
              >
                <span>
                  {a.mensagem}
                  <br />
                  <small className="text-muted">
                    {new Date(a.criadoEm).toLocaleString('pt-BR')}
                  </small>
                </span>
                <span className={`badge text-bg-${CORES[a.nivel] ?? 'secondary'}`}>
                  {a.nivel}
                </span>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  )
}

export default ListaAlertas