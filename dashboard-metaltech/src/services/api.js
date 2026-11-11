import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 5000,
})

// A API espera datas no formato 2026-11-10T21:50:00 (horário local, sem fuso)
function paraDataHoraApi(data) {
  const local = new Date(data.getTime() - data.getTimezoneOffset() * 60000)
  return local.toISOString().slice(0, 19)
}

export async function buscarEsteiras() {
  const { data } = await api.get('/api/esteiras')
  return data
}

export async function buscarContagem(esteiraId) {
  const { data } = await api.get('/api/indicadores/contagem', { params: { esteiraId } })
  return data
}

// Se ainda não existir leitura de nível, a API pode responder 404: tratamos como "sem dados"
export async function buscarNivelAtual(esteiraId) {
  try {
    const { data } = await api.get('/api/indicadores/nivel-atual', { params: { esteiraId } })
    return data
  } catch (erro) {
    if (erro.response && erro.response.status === 404) return null
    throw erro
  }
}

// Leituras (peças + nível) dos últimos N minutos, em ordem cronológica
export async function buscarHistorico(esteiraId, minutos = 60) {
  const fim = new Date()
  const inicio = new Date(fim.getTime() - minutos * 60000)
  const { data } = await api.get('/api/leituras/historico', {
    params: {
      esteiraId,
      inicio: paraDataHoraApi(inicio),
      fim: paraDataHoraApi(fim),
    },
  })
  return data
}

export async function buscarAlertasAbertos(esteiraId) {
  const { data } = await api.get('/api/alertas', { params: { esteiraId } })
  return data
}