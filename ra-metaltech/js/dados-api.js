// Dados em tempo real da esteira, consumidos da API REST.
// Mostra a contagem de peças e o nível do reservatório (com alerta)
// em sobreposição enquanto o marcador da esteira estiver visível.

document.addEventListener('DOMContentLoaded', () => {
  const alvo = document.querySelector('#alvo-esteira')
  const painel = document.querySelector('#painel-dados')
  const elMetalicas = document.querySelector('#dado-metalicas')
  const elNaoMetalicas = document.querySelector('#dado-nao-metalicas')
  const elTotal = document.querySelector('#dado-total')
  const elNivel = document.querySelector('#dado-nivel')
  const elBarra = document.querySelector('#dado-barra')
  const elAlerta = document.querySelector('#dado-alerta')
  const elAviso = document.querySelector('#dado-aviso')

  const config = window.CONFIG_API || { url: 'http://localhost:8080', esteiraId: 1, intervaloMs: 3000 }
  const apiNaUrl = new URLSearchParams(window.location.search).get('api')
  const baseUrl = (apiNaUrl || config.url).replace(/\/$/, '')
  const esteiraId = config.esteiraId

  const TEXTO_ALERTA = {
    NORMAL: 'Normal',
    ATENCAO: 'Atenção',
    CRITICO: 'Crítico',
  }

  function mostrarAviso(texto) {
    elAviso.textContent = texto || ''
    elAviso.classList.toggle('oculto', !texto)
  }

  // Página em HTTPS chamando API em HTTP é bloqueada pelo navegador (mixed content)
  if (window.location.protocol === 'https:' && baseUrl.startsWith('http:')) {
    mostrarAviso('A API precisa estar em HTTPS para funcionar nesta página.')
  }

  async function buscar(caminho) {
    const resposta = await fetch(`${baseUrl}${caminho}`)
    if (resposta.status === 404) return null
    if (!resposta.ok) throw new Error(`HTTP ${resposta.status}`)
    return resposta.json()
  }

  function atualizarContagem(contagem) {
    elMetalicas.textContent = contagem ? contagem.metalicas : '–'
    elNaoMetalicas.textContent = contagem ? contagem.naoMetalicas : '–'
    elTotal.textContent = contagem ? contagem.total : '–'
  }

  function atualizarNivel(nivel) {
    if (!nivel) {
      elNivel.textContent = 'Sem leitura'
      elBarra.style.width = '0%'
      elAlerta.textContent = ''
      painel.dataset.alerta = 'NORMAL'
      return
    }
    const percentual = Math.round(nivel.percentual)
    elNivel.textContent = `${percentual}%`
    elBarra.style.width = `${Math.min(100, Math.max(0, nivel.percentual))}%`
    elAlerta.textContent = TEXTO_ALERTA[nivel.nivelAlerta] || nivel.nivelAlerta
    painel.dataset.alerta = nivel.nivelAlerta
  }

  async function carregar() {
    try {
      const [contagem, nivel] = await Promise.all([
        buscar(`/api/indicadores/contagem?esteiraId=${esteiraId}`),
        buscar(`/api/indicadores/nivel-atual?esteiraId=${esteiraId}`),
      ])
      atualizarContagem(contagem)
      atualizarNivel(nivel)
      if (!(window.location.protocol === 'https:' && baseUrl.startsWith('http:'))) {
        mostrarAviso('')
      }
    } catch (erro) {
      mostrarAviso('Sem conexão com a API')
    }
  }

  alvo.addEventListener('targetFound', () => {
    painel.classList.remove('oculto')
    carregar()
  })

  alvo.addEventListener('targetLost', () => {
    painel.classList.add('oculto')
  })

  carregar()
  setInterval(carregar, config.intervaloMs || 3000)
})
