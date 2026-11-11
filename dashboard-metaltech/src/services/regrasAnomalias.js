// Regras de anomalia calculadas no FRONT-END.
// A API não possui o conceito de "anomalia": estas regras usam apenas os dados
// que ela já entrega. Ajuste os limites aqui conforme combinado com a equipe.
export const REGRAS = {
  semPecasSegundos: 120,        // esteira sem peças novas há mais de X segundos
  nivelSemLeituraSegundos: 60,  // sensor de nível sem leitura há mais de X segundos
  indefinidasPercentual: 20,    // % de peças indefinidas considerado alto
  minimoPecasParaAvaliar: 10,   // só avalia indefinidas com pelo menos X peças
  saltoNivelPontos: 15,         // subida do nível, entre duas leituras seguidas
}

function segundosDesde(dataIso, agora) {
  return (agora.getTime() - new Date(dataIso).getTime()) / 1000
}

function formatarDuracao(segundos) {
  if (segundos < 90) return `${Math.round(segundos)} segundos`
  return `${Math.round(segundos / 60)} minutos`
}

// historico deve estar em ordem cronológica (do mais antigo para o mais novo)
export function detectarAnomalias({ contagem, historico, agora = new Date() }) {
  const anomalias = []
  const pecas = historico.filter((l) => l.tipoLeitura === 'PECA')
  const niveis = historico.filter((l) => l.tipoLeitura === 'NIVEL')

  // 1) Esteira sem peças novas
  if (pecas.length === 0) {
    anomalias.push({
      id: 'sem-pecas',
      tipo: 'danger',
      titulo: 'Esteira parada?',
      mensagem: 'Nenhuma peça registrada na última hora.',
    })
  } else {
    const segundos = segundosDesde(pecas[pecas.length - 1].registradoEm, agora)
    if (segundos > REGRAS.semPecasSegundos) {
      anomalias.push({
        id: 'sem-pecas',
        tipo: 'danger',
        titulo: 'Esteira parada?',
        mensagem: `Nenhuma peça nova há ${formatarDuracao(segundos)}.`,
      })
    }
  }

  // 2) Sensor de nível sem leitura
  if (niveis.length > 0) {
    const segundos = segundosDesde(niveis[niveis.length - 1].registradoEm, agora)
    if (segundos > REGRAS.nivelSemLeituraSegundos) {
      anomalias.push({
        id: 'nivel-parado',
        tipo: 'danger',
        titulo: 'Sensor de nível',
        mensagem: `Sem leitura do nível há ${formatarDuracao(segundos)}.`,
      })
    }
  }

  // 3) Muitas peças indefinidas
  if (contagem.total >= REGRAS.minimoPecasParaAvaliar) {
    const percentual = (contagem.indefinidas * 100) / contagem.total
    if (percentual > REGRAS.indefinidasPercentual) {
      anomalias.push({
        id: 'indefinidas',
        tipo: 'warning',
        titulo: 'Classificação',
        mensagem: `${percentual.toFixed(1)}% das peças ficaram indefinidas (limite: ${REGRAS.indefinidasPercentual}%). Verifique os sensores.`,
      })
    }
  }

  // 4) Subida brusca do nível (últimas 10 leituras)
  const recentes = niveis.slice(-10)
  for (let i = 1; i < recentes.length; i++) {
    const salto = recentes[i].percentual - recentes[i - 1].percentual
    if (salto > REGRAS.saltoNivelPontos) {
      anomalias.push({
        id: 'salto-nivel',
        tipo: 'warning',
        titulo: 'Nível do reservatório',
        mensagem: `Subida brusca de ${salto.toFixed(1)} pontos entre duas leituras.`,
      })
      break
    }
  }

  return anomalias
}