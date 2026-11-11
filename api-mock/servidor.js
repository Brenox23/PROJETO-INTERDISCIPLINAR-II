// API DE MENTIRA (mock) que imita o contrato REAL do back-end Java/Spring Boot.
// Endpoints (todos GET):
//   /api/esteiras                              /api/esteiras/{id}
//   /api/indicadores/contagem?esteiraId=1      /api/indicadores/nivel-atual?esteiraId=1
//   /api/leituras/historico?esteiraId=1&inicio=...&fim=...
//   /api/alertas?esteiraId=1
// Não implementados no mock: endpoints paginados (/leituras/pecas, /leituras/niveis, /alertas/historico).
const http = require('http')

const PORTA = 3001
const ALTURA_CM = 30
const LIMITE_ATENCAO = 70
const LIMITE_CRITICO = 90
const MAX_LEITURAS = 20000

function dataLocalISO(data) {
  const local = new Date(data.getTime() - data.getTimezoneOffset() * 60000)
  return local.toISOString().slice(0, 23)
}

// Mesma regra da classe Esteira.java: menor distância = mais cheio
function calcularPercentual(distanciaCm) {
  const ocupado = ALTURA_CM - distanciaCm
  const percentual = (ocupado / ALTURA_CM) * 100
  const limitado = Math.max(0, Math.min(100, percentual))
  return Math.round(limitado * 100) / 100
}

function avaliarNivel(percentual) {
  if (percentual >= LIMITE_CRITICO) return 'CRITICO'
  if (percentual >= LIMITE_ATENCAO) return 'ATENCAO'
  return 'NORMAL'
}

const esteira = {
  id: 1,
  nome: 'Esteira de Triagem 01',
  localizacao: 'Linha 1 - MetalTech',
  alturaReservatorioCm: ALTURA_CM,
  limiteAtencaoPercentual: LIMITE_ATENCAO,
  limiteCriticoPercentual: LIMITE_CRITICO,
  ativa: true,
  criadoEm: dataLocalISO(new Date()),
}

let leituras = []
let alertas = []
let proximoId = 1
let distanciaAtual = 28

function limitarMemoria() {
  if (leituras.length > MAX_LEITURAS) leituras.splice(0, leituras.length - MAX_LEITURAS)
}

function registrarPeca(data) {
  const sorteio = Math.random()
  const tipoPeca = sorteio < 0.5 ? 'METALICA' : sorteio < 0.9 ? 'NAO_METALICA' : 'INDEFINIDA'
  leituras.push({
    id: proximoId++,
    esteiraId: esteira.id,
    tipoLeitura: 'PECA',
    registradoEm: dataLocalISO(data),
    tipoPeca,
    sensorIndutivo: tipoPeca === 'METALICA',
    sensorCapacitivo: tipoPeca === 'NAO_METALICA',
    distanciaCm: null,
    percentual: null,
    nivelAlerta: 'NORMAL',
  })
  limitarMemoria()
}

function atualizarAlertas(nivelAlerta, percentual, quando) {
  if (nivelAlerta === 'NORMAL') {
    alertas.forEach((a) => {
      if (!a.resolvido) {
        a.resolvido = true
        a.resolvidoEm = quando
      }
    })
    return
  }
  const jaExiste = alertas.some((a) => !a.resolvido && a.nivel === nivelAlerta)
  if (!jaExiste) {
    alertas.push({
      id: alertas.length + 1,
      esteiraId: esteira.id,
      nivel: nivelAlerta,
      mensagem: `Reservatório em ${percentual.toFixed(1)}% de ocupação`,
      resolvido: false,
      criadoEm: quando,
      resolvidoEm: null,
    })
  }
}

function registrarNivel(data) {
  distanciaAtual -= 0.2 + Math.random() * 0.6
  if (distanciaAtual <= 0) distanciaAtual = 28 // simula o esvaziamento
  const distanciaCm = Math.round(distanciaAtual * 10) / 10
  const percentual = calcularPercentual(distanciaCm)
  const nivelAlerta = avaliarNivel(percentual)
  const registradoEm = dataLocalISO(data)
  leituras.push({
    id: proximoId++,
    esteiraId: esteira.id,
    tipoLeitura: 'NIVEL',
    registradoEm,
    tipoPeca: null,
    sensorIndutivo: null,
    sensorCapacitivo: null,
    distanciaCm,
    percentual,
    nivelAlerta,
  })
  limitarMemoria()
  atualizarAlertas(nivelAlerta, percentual, registradoEm)
}

function noPeriodo(leitura, inicio, fim) {
  const t = new Date(leitura.registradoEm).getTime()
  if (inicio && t < inicio.getTime()) return false
  if (fim && t > fim.getTime()) return false
  return true
}

function responder(res, status, corpo) {
  res.writeHead(status)
  res.end(JSON.stringify(corpo))
}

// Histórico inicial: 24 leituras, uma a cada 5 segundos
const agora = Date.now()
for (let i = 24; i > 0; i--) {
  const instante = new Date(agora - i * 5000)
  registrarPeca(instante)
  registrarNivel(instante)
}

setInterval(() => registrarPeca(new Date()), 3000)
setInterval(() => registrarNivel(new Date()), 5000)

const servidor = http.createServer((req, res) => {
  res.setHeader('Access-Control-Allow-Origin', '*')
  res.setHeader('Access-Control-Allow-Methods', 'GET, OPTIONS')
  res.setHeader('Access-Control-Allow-Headers', '*')
  res.setHeader('Content-Type', 'application/json; charset=utf-8')

  if (req.method === 'OPTIONS') {
    res.writeHead(204)
    return res.end()
  }
  if (req.method !== 'GET') {
    return responder(res, 405, { erro: 'O mock só aceita GET' })
  }

  const url = new URL(req.url, 'http://localhost')
  const caminho = url.pathname
  const params = url.searchParams

  const rotasComEsteira = [
    '/api/indicadores/contagem',
    '/api/indicadores/nivel-atual',
    '/api/leituras/historico',
  ]
  if (rotasComEsteira.includes(caminho)) {
    if (!params.get('esteiraId')) return responder(res, 400, { erro: 'esteiraId é obrigatório' })
    if (Number(params.get('esteiraId')) !== esteira.id) {
      return responder(res, 404, { erro: 'Esteira não encontrada' })
    }
  }

  if (caminho === '/api/esteiras') return responder(res, 200, [esteira])

  if (caminho.startsWith('/api/esteiras/')) {
    const id = Number(caminho.split('/')[3])
    if (id !== esteira.id) return responder(res, 404, { erro: 'Esteira não encontrada' })
    return responder(res, 200, esteira)
  }

  if (caminho === '/api/indicadores/contagem') {
    const inicio = params.get('inicio') ? new Date(params.get('inicio')) : null
    const fim = params.get('fim') ? new Date(params.get('fim')) : null
    const pecas = leituras.filter((l) => l.tipoLeitura === 'PECA' && noPeriodo(l, inicio, fim))
    const metalicas = pecas.filter((l) => l.tipoPeca === 'METALICA').length
    const naoMetalicas = pecas.filter((l) => l.tipoPeca === 'NAO_METALICA').length
    const indefinidas = pecas.filter((l) => l.tipoPeca === 'INDEFINIDA').length
    const total = metalicas + naoMetalicas + indefinidas
    const perc = (n) => (total === 0 ? 0 : Math.round(((n * 100) / total) * 100) / 100)
    return responder(res, 200, {
      esteiraId: esteira.id,
      esteiraNome: esteira.nome,
      metalicas,
      naoMetalicas,
      indefinidas,
      total,
      percentualMetalicas: perc(metalicas),
      percentualNaoMetalicas: perc(naoMetalicas),
      atualizadoEm: dataLocalISO(new Date()),
    })
  }

  if (caminho === '/api/indicadores/nivel-atual') {
    const niveis = leituras.filter((l) => l.tipoLeitura === 'NIVEL')
    const ultima = niveis[niveis.length - 1]
    if (!ultima) return responder(res, 404, { erro: 'Ainda não há leitura de nível' })
    return responder(res, 200, {
      esteiraId: esteira.id,
      esteiraNome: esteira.nome,
      distanciaCm: ultima.distanciaCm,
      percentual: ultima.percentual,
      nivelAlerta: ultima.nivelAlerta,
      limiteAtencaoPercentual: LIMITE_ATENCAO,
      limiteCriticoPercentual: LIMITE_CRITICO,
      registradoEm: ultima.registradoEm,
    })
  }

  if (caminho === '/api/leituras/historico') {
    if (!params.get('inicio') || !params.get('fim')) {
      return responder(res, 400, { erro: 'inicio e fim são obrigatórios' })
    }
    const inicio = new Date(params.get('inicio'))
    const fim = new Date(params.get('fim'))
    if (isNaN(inicio) || isNaN(fim)) return responder(res, 400, { erro: 'Data inválida' })
    return responder(res, 200, leituras.filter((l) => noPeriodo(l, inicio, fim)))
  }

  if (caminho === '/api/alertas') {
    const filtro = params.get('esteiraId')
    const abertos = alertas.filter(
      (a) => !a.resolvido && (!filtro || a.esteiraId === Number(filtro))
    )
    return responder(res, 200, abertos)
  }

  responder(res, 404, { erro: 'Endpoint não encontrado no mock' })
})

servidor.listen(PORTA, () => {
  console.log(`API mock (contrato real) rodando em http://localhost:${PORTA}`)
})