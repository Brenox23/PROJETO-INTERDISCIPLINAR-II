document.addEventListener('DOMContentLoaded', () => {
  const alvo = document.querySelector('#alvo-esteira')
  const status = document.querySelector('#status')

  alvo.addEventListener('targetFound', () => {
    status.textContent = '✔ Marcador reconhecido'
    status.className = 'reconhecido'
  })

  alvo.addEventListener('targetLost', () => {
    status.textContent = 'Aponte a câmera para o marcador'
    status.className = 'procurando'
  })
})