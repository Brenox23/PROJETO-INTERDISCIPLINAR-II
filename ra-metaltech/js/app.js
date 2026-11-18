
const COR_NORMAL = '#f59e0b';
const COR_ATIVA = '#22c55e';

// Distância máxima entre a mira e o sensor
const RAIO_MIRA = 0.2;

document.addEventListener('DOMContentLoaded', () => {
  const cena = document.querySelector('a-scene');
  const alvo = document.querySelector('#alvo-esteira');
  const status = document.querySelector('#status');
  const mira = document.querySelector('#mira');
  const painel = document.querySelector('#painel-sensor');
  const painelNome = document.querySelector('#painel-nome');
  const painelDescricao = document.querySelector('#painel-descricao');

  // Configuração dos sensores
  const config = window.CONFIG_SENSORES || {
    proporcao: 0.75,
    sensores: []
  };

  let pontos = [];
  let ativo = null;
  let marcadorVisivel = false;

  // Verifica se os elementos necessários existem
  if (!cena || !alvo || !status || !mira || !painel ||
      !painelNome || !painelDescricao) {
    console.error('Erro: elementos da RA não encontrados no HTML.');
    return;
  }

  // Atualiza o painel de informações
  function atualizarPainel(ponto) {
    if (config.sensores.length === 0) {
      painelNome.textContent = 'Nenhum sensor configurado';
      painelDescricao.textContent = 'Verifique o arquivo sensores.js';
    } else if (!ponto) {
      painelNome.textContent = 'Sensores da esteira';
      painelDescricao.textContent = 'Aponte a mira (+) para um sensor';
    } else {
      painelNome.textContent = ponto.sensor.nome;
      painelDescricao.textContent = ponto.sensor.descricao;
    }
  }

  // Cria os indicadores visuais dos sensores
  function criarPontos() {
    return config.sensores.map((sensor) => {
      const posX = Number(sensor.x) - 0.5;
      const posY = (0.5 - Number(sensor.y)) * config.proporcao;

      const grupo = document.createElement('a-entity');

      grupo.setAttribute(
        'position',
        `${posX} ${posY} 0.02`
      );

      // Círculo preenchido
      const disco = document.createElement('a-circle');

      disco.setAttribute('radius', '0.03');

      disco.setAttribute(
        'material',
        `color: ${COR_NORMAL}; opacity: 0.35; transparent: true; side: double`
      );

      // Contorno do sensor
      const anel = document.createElement('a-ring');

      anel.setAttribute('radius-inner', '0.03');
      anel.setAttribute('radius-outer', '0.04');

      anel.setAttribute(
        'material',
        `color: ${COR_NORMAL}; side: double`
      );

      grupo.appendChild(disco);
      grupo.appendChild(anel);

      alvo.appendChild(grupo);

      return {
        sensor,
        grupo,
        disco,
        anel
      };
    });
  }

  // Altera a cor e o tamanho do sensor
  function pintar(ponto, cor, escala) {
    ponto.disco.setAttribute('material', 'color', cor);
    ponto.anel.setAttribute('material', 'color', cor);

    ponto.grupo.setAttribute(
      'scale',
      `${escala} ${escala} ${escala}`
    );
  }

  // Seleciona um sensor
  function selecionar(ponto) {
    if (ponto === ativo) return;

    if (ativo) {
      pintar(ativo, COR_NORMAL, 1);
    }

    ativo = ponto;

    if (ativo) {
      pintar(ativo, COR_ATIVA, 1.4);
    }

    atualizarPainel(ativo);
  }

  // Procura o sensor mais próximo da mira
  const vetor = new AFRAME.THREE.Vector3();

  function ciclo() {
    requestAnimationFrame(ciclo);

    if (
      !marcadorVisivel ||
      !cena.camera ||
      pontos.length === 0
    ) {
      return;
    }

    const proporcaoTela =
      window.innerWidth / window.innerHeight;

    let melhor = null;
    let menor = Infinity;

    for (const ponto of pontos) {
      if (!ponto.grupo.object3D) continue;

      ponto.grupo.object3D.getWorldPosition(vetor);

      vetor.project(cena.camera);

      const distancia = Math.hypot(
        vetor.x * proporcaoTela,
        vetor.y
      );

      if (vetor.z < 1 && distancia < menor) {
        menor = distancia;
        melhor = ponto;
      }
    }

    selecionar(
      melhor && menor <= RAIO_MIRA
        ? melhor
        : null
    );
  }

  // Quando o MindAR reconhece a imagem
  alvo.addEventListener('targetFound', () => {
    marcadorVisivel = true;

    status.textContent = '✔ Esteira detectada';
    status.className = 'reconhecido';

    mira.classList.remove('oculto');
    painel.classList.remove('oculto');

    atualizarPainel(ativo);
  });

  // Quando a imagem deixa de ser reconhecida
  alvo.addEventListener('targetLost', () => {
    marcadorVisivel = false;

    status.textContent = 'Aponte a câmera para o marcador';
    status.className = 'procurando';

    selecionar(null);

    mira.classList.add('oculto');
    painel.classList.add('oculto');
  });

  // Inicialização
  function iniciar() {
    pontos = criarPontos();

    atualizarPainel(null);

    ciclo();

    console.log('MetalTech RA iniciada!');
    console.log('Sensores carregados:', pontos.length);
  }

  if (cena.hasLoaded) {
    iniciar();
  } else {
    cena.addEventListener('loaded', iniciar, { once: true });
  }
});
