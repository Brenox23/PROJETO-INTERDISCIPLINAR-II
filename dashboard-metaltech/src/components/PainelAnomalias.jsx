function PainelAnomalias({ anomalias }) {
  return (
    <div className="card shadow-sm border-0 mb-3">
      <div className="card-body">
        <h2 className="h6 text-muted text-uppercase mb-3">
          Possíveis anomalias ({anomalias.length})
        </h2>
        {anomalias.length === 0 ? (
          <p className="text-success mb-0">✔ Nenhuma anomalia detectada.</p>
        ) : (
          <ul className="list-group list-group-flush">
            {anomalias.map((a) => (
              <li key={a.id} className="list-group-item px-0 d-flex gap-2 align-items-start">
                <span className={`badge text-bg-${a.tipo}`}>{a.titulo}</span>
                <span>{a.mensagem}</span>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  )
}

export default PainelAnomalias