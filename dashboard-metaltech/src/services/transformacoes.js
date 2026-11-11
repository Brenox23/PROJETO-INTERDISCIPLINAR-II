function porData(a, b) {
  return new Date(a.registradoEm) - new Date(b.registradoEm)
}

// Contagem ACUMULADA de peças dentro do período, agrupada por minuto
export function montarSeriePecas(historico) {
  const pecas = historico.filter((l) => l.tipoLeitura === 'PECA').sort(porData)
  const porMinuto = new Map()
  let metalicas = 0
  let naoMetalicas = 0
  let indefinidas = 0

  for (const peca of pecas) {
    if (peca.tipoPeca === 'METALICA') metalicas++
    else if (peca.tipoPeca === 'NAO_METALICA') naoMetalicas++
    else indefinidas++

    const hora = new Date(peca.registradoEm).toLocaleTimeString('pt-BR', {
      hour: '2-digit',
      minute: '2-digit',
    })
    porMinuto.set(hora, {
      hora,
      total: metalicas + naoMetalicas + indefinidas,
      metalicas,
      naoMetalicas,
    })
  }
  return Array.from(porMinuto.values())
}

// Últimas 60 leituras de nível (percentual de ocupação)
export function montarSerieNivel(historico) {
  return historico
    .filter((l) => l.tipoLeitura === 'NIVEL')
    .sort(porData)
    .slice(-60)
    .map((l) => ({
      hora: new Date(l.registradoEm).toLocaleTimeString('pt-BR'),
      nivel: l.percentual,
    }))
}