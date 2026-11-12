const COR_NORMAL = '#f59e0b'
const COR_ATIVA = '#22c55e'
// Distância máxima (em "meia altura de tela") entre a mira e o sensor.
// Aumente para a mira "pegar" sensores de mais longe.
const RAIO_MIRA = 0.2

document.addEventListener('DOMContentLoaded', () => {
  const cena = document.querySelector('a-scene')
  const alvo = document.querySelector('#alvo-esteira')
  const status = document.querySelector('#status')
  const mira = document.querySelector('#mira')
  const painel = document.querySelector('#painel-sensor')
  const painelNome = document.querySelector('#painel-nome')
  const painelDescricao = document.querySelector('#painel-descricao')
  const botaoVoz = document.querySelector('#botao-voz')

  const config = window.CONFIG_SENSORES || { proporcao: 0.75, sensores: [] }

  let pontos = []
  let ativo = null
  let marcadorVisivel = false
  let vozLigada = false

  // ---------- Voz ----------
  function falar(texto) {
    if (!vozLigada || !('speechSynthesis' in window)) return
    window.speechSynthesis.cancel()
    const fala = new SpeechSynthesisUtterance(texto)
    fala.lang = 'pt-BR'
    window.speechSynthesis.speak(fala)
  }

  botaoVoz.addEventListener('click', () => {
    vozLigada = !vozLigada
    botaoVoz.textContent = vozLigada ? '🔊 Voz ligada' : '🔇 Voz desligada'
    if (vozLigada) {
      falar('Voz ativada')
    } else if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel()
    }
  })

  // ---------- Painel ----------
  function atualizarPainel(ponto) {
    let nome = ''
    let descricao = ''

    if (config.sensores.length === 0) {
      nome = 'Nenhum sensor configurado'
      descricao = 'Gere o arquivo js/sensores.js com a ferramenta calibrar.html.'
    } else if (!ponto) {
      nome = 'Aponte a mira (+) para um sensor'
    } else {
      nome = ponto.sensor.nome
      descricao = ponto.sensor.descricao
    }

    painelNome.textContent = nome
    painelDescricao.textContent = descricao
    painelDescricao.style.display = descricao ? '' : 'none'
  }

  // ---------- Círculos sobre os sensores ----------
  // No MindAR a imagem tem largura 1 e altura = proporcao.
  // O centro da imagem é (0, 0); x e y salvos vão de 0 a 1, a partir do canto superior esquerdo.
  function criarPontos() {
    return config.sensores.map((sensor) => {
      const posX = sensor.x - 0.5
      const posY = (0.5 - sensor.y) * config.proporcao

      const grupo = document.createElement('a-entity')
      grupo.setAttribute('position', `${posX} ${posY} 0.02`)

      const disco = document.createElement('a-circle')
      disco.setAttribute('radius', '0.03')
      disco.setAttribute('material', `color: ${COR_NORMAL}; opacity: 0.35; transparent: true; side: double`)

      const anel = document.createElement('a-ring')
      anel.setAttribute('radius-inner', '0.03')
      anel.setAttribute('radius-outer', '0.04')
      anel.setAttribute('material', `color: ${COR_NORMAL}; side: double`)

      grupo.appendChild(disco)
      grupo.appendChild(anel)
      alvo.appendChild(grupo)

      return { sensor, grupo, disco, anel }
    })
  }

  function pintar(ponto, cor, escala) {
    ponto.disco.setAttribute('material', 'color', cor)
    ponto.anel.setAttribute('material', 'color', cor)
    ponto.grupo.setAttribute('scale', `${escala} ${escala} ${escala}`)
  }

  function selecionar(ponto) {
    if (ponto === ativo) return
    if (ativo) pintar(ativo, COR_NORMAL, 1)
    ativo = ponto
    if (ativo) {
      pintar(ativo, COR_ATIVA, 1.4)
      falar(ativo.sensor.nome)
    }
    atualizarPainel(ativo)
  }

  // ---------- Qual sensor está mais perto da mira? ----------
  const vetor = new AFRAME.THREE.Vector3()

  function ciclo() {
    requestAnimationFrame(ciclo)
    if (!marcadorVisivel || !cena.camera || pontos.length === 0) return

    const proporcaoTela = window.innerWidth / window.innerHeight
    let melhor = null
    let menor = Infinity

    for (const ponto of pontos) {
      if (!ponto.grupo.object3D) continue
      ponto.grupo.object3D.getWorldPosition(vetor)
      vetor.project(cena.camera)
      // Centro da tela = (0, 0). Corrige o x para a distância ficar igual nos dois eixos.
      const distancia = Math.hypot(vetor.x * proporcaoTela, vetor.y)
      if (vetor.z < 1 && distancia < menor) {
        menor = distancia
        melhor = ponto
      }
    }

    selecionar(melhor && menor <= RAIO_MIRA ? melhor : null)
  }

  // ---------- Eventos do marcador ----------
  alvo.addEventListener('targetFound', () => {
    marcadorVisivel = true
    status.textContent = '✔ Esteira detectada'
    status.className = 'reconhecido'
    mira.classList.remove('oculto')
    painel.classList.remove('oculto')
    atualizarPainel(ativo)
  })

  alvo.addEventListener('targetLost', () => {
    marcadorVisivel = false
    status.textContent = 'Aponte a câmera para o marcador'
    status.className = 'procurando'
    selecionar(null)
    mira.classList.add('oculto')
    painel.classList.add('oculto')
  })

  // ---------- Início ----------
  function iniciar() {
    pontos = criarPontos()
    ciclo()
  }

  if (cena.hasLoaded) {
    iniciar()
  } else {
    cena.addEventListener('loaded', iniciar)
  }
})