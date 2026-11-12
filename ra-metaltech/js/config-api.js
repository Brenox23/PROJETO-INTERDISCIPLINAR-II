// Endereço da API REST (back-end Spring Boot) consumida pela RA.
//
// - Testando no próprio PC (página aberta em http://localhost): use http://localhost:8080
// - Publicada no GitHub Pages (HTTPS): a API TAMBÉM precisa estar em HTTPS
//   (endereço da nuvem), senão o navegador bloqueia as requisições.
//
// Também dá para trocar sem editar o arquivo, direto na URL da página:
//   .../index.html?api=https://minha-api.exemplo.com
window.CONFIG_API = {
  url: 'http://localhost:8080',
  esteiraId: 1,
  intervaloMs: 3000,
}
