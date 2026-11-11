function formatar(valor) {
  return valor ? new Date(valor).toLocaleString('pt-BR') : '--'
}

function BarraStatus({ conectado, esteiraNome, ultimaAtualizacao, ultimaLeitura }) {
  return (
    <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 bg-white rounded shadow-sm p-3 mb-4">
      <div>
        <div>
          <span className="text-muted small">Esteira: </span>
          <strong>{esteiraNome}</strong>
        </div>
        <div>
          <span className="text-muted small">Última atualização: </span>
          <strong>{formatar(ultimaAtualizacao)}</strong>
          <span className="text-muted small ms-3">Última leitura dos sensores: </span>
          <strong>{formatar(ultimaLeitura)}</strong>
        </div>
      </div>
      <span className={`badge ${conectado ? 'bg-success' : 'bg-danger'}`}>
        {conectado ? '● API conectada' : '● API desconectada'}
      </span>
    </div>
  )
}

export default BarraStatus