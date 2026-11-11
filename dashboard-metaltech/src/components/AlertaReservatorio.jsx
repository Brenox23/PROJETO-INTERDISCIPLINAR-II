const ESTILOS = {
  NORMAL: {
    tipo: 'success',
    titulo: 'Reservatório normal',
    texto: 'Operação dentro do esperado.',
  },
  ATENCAO: {
    tipo: 'warning',
    titulo: 'Reservatório próximo da capacidade máxima',
    texto: 'Atenção ao esvaziamento.',
  },
  CRITICO: {
    tipo: 'danger',
    titulo: 'Reservatório acima do limite',
    texto: 'Nível crítico: ação imediata necessária.',
  },
}

function AlertaReservatorio({ nivelAtual }) {
  if (!nivelAtual) {
    return (
      <div className="alert alert-secondary mb-4" role="alert">
        Ainda não há leitura do nível do reservatório.
      </div>
    )
  }

  const estilo = ESTILOS[nivelAtual.nivelAlerta] ?? {
    tipo: 'secondary',
    titulo: 'Situação desconhecida',
    texto: `Valor recebido da API: ${nivelAtual.nivelAlerta}`,
  }

  return (
    <div className={`alert alert-${estilo.tipo} mb-4`} role="alert">
      <strong>{estilo.titulo}.</strong> Ocupação em {nivelAtual.percentual}%. {estilo.texto}
    </div>
  )
}

export default AlertaReservatorio