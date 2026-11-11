import { useState, useEffect, useRef } from 'react'
import CartaoIndicador from '../components/CartaoIndicador.jsx'
import BarraStatus from '../components/BarraStatus.jsx'
import AlertaReservatorio from '../components/AlertaReservatorio.jsx'
import CartaoGrafico from '../components/CartaoGrafico.jsx'
import GraficoLinhaPecas from '../components/GraficoLinhaPecas.jsx'
import GraficoBarrasPecas from '../components/GraficoBarrasPecas.jsx'
import GraficoNivelReservatorio from '../components/GraficoNivelReservatorio.jsx'
import ListaAlertas from '../components/ListaAlertas.jsx'
import PainelAnomalias from '../components/PainelAnomalias.jsx'
import { detectarAnomalias } from '../services/regrasAnomalias.js'
import {
  buscarEsteiras,
  buscarContagem,
  buscarNivelAtual,
  buscarHistorico,
  buscarAlertasAbertos,
} from '../services/api.js'
import { montarSeriePecas, montarSerieNivel } from '../services/transformacoes.js'

const INTERVALO_ATUALIZACAO_MS = 5000

function Dashboard() {
  const [esteira, setEsteira] = useState(null)
  const [contagem, setContagem] = useState(null)
  const [nivel, setNivel] = useState(null)
  const [historico, setHistorico] = useState([])
  const [alertas, setAlertas] = useState([])
  const [conectado, setConectado] = useState(false)
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState(null)
  const [ultimaAtualizacao, setUltimaAtualizacao] = useState(null)
  const esteiraRef = useRef(null)

  useEffect(() => {
    let ativo = true

    async function carregar() {
      try {
        // Descobre a esteira na primeira vez (usa a primeira esteira ativa)
        if (!esteiraRef.current) {
          const lista = await buscarEsteiras()
          const escolhida = lista.find((e) => e.ativa) ?? lista[0]
          if (!escolhida) throw new Error('SEM_ESTEIRA')
          esteiraRef.current = escolhida
        }
        const e = esteiraRef.current

        const [cont, niv, hist, alts] = await Promise.all([
          buscarContagem(e.id),
          buscarNivelAtual(e.id),
          buscarHistorico(e.id, 60),
          buscarAlertasAbertos(e.id),
        ])
        if (!ativo) return

        setEsteira(e)
        setContagem(cont)
        setNivel(niv)
        setHistorico(
          [...hist].sort((a, b) => new Date(a.registradoEm) - new Date(b.registradoEm))
        )
        setAlertas(alts)
        setUltimaAtualizacao(new Date())
        setConectado(true)
        setErro(null)
      } catch (falha) {
        if (!ativo) return
        setConectado(false)
        setErro(
          falha.message === 'SEM_ESTEIRA'
            ? 'Nenhuma esteira cadastrada na API.'
            : 'Não foi possível obter os dados da API.'
        )
      } finally {
        if (ativo) setCarregando(false)
      }
    }

    carregar()
    const idIntervalo = setInterval(carregar, INTERVALO_ATUALIZACAO_MS)

    return () => {
      ativo = false
      clearInterval(idIntervalo)
    }
  }, [])

  if (carregando) {
    return (
      <div className="text-center py-5">
        <div className="spinner-border text-primary" role="status" />
        <p className="mt-3 text-muted">Carregando dados da esteira...</p>
      </div>
    )
  }

  if (!contagem) {
    return (
      <div className="alert alert-danger" role="alert">
        <strong>Erro de conexão.</strong> {erro} Verifique se a API está ligada. Uma nova
        tentativa será feita automaticamente.
      </div>
    )
  }

  const seriePecas = montarSeriePecas(historico)
  const serieNivel = montarSerieNivel(historico)
  const ultimaLeitura =
    historico.length > 0 ? historico[historico.length - 1].registradoEm : null
  const limiteAtencao = nivel?.limiteAtencaoPercentual ?? esteira.limiteAtencaoPercentual
  const limiteCritico = nivel?.limiteCriticoPercentual ?? esteira.limiteCriticoPercentual
  const anomalias = detectarAnomalias({ contagem, historico })

  return (
    <>
      <BarraStatus
        conectado={conectado}
        esteiraNome={contagem.esteiraNome}
        ultimaAtualizacao={ultimaAtualizacao}
        ultimaLeitura={ultimaLeitura}
      />

      {erro && (
        <div className="alert alert-warning" role="alert">
          {erro} Exibindo os últimos dados recebidos. 
        </div>
      )}

      <AlertaReservatorio nivelAtual={nivel} />
      <PainelAnomalias anomalias={anomalias} />

      <div className="row g-3 mb-3">
        <div className="col-12 col-sm-6 col-lg">
          <CartaoIndicador titulo="Total de peças" valor={contagem.total} cor="primary" icone="📦" />
        </div>
        <div className="col-12 col-sm-6 col-lg">
          <CartaoIndicador titulo="Peças metálicas" valor={contagem.metalicas} cor="secondary" icone="🔩" />
        </div>
        <div className="col-12 col-sm-6 col-lg">
          <CartaoIndicador titulo="Peças não metálicas" valor={contagem.naoMetalicas} cor="success" icone="🧱" />
        </div>
        <div className="col-12 col-sm-6 col-lg">
          <CartaoIndicador titulo="Peças indefinidas" valor={contagem.indefinidas} cor="warning" icone="❓" />
        </div>
        <div className="col-12 col-sm-6 col-lg">
          <CartaoIndicador
            titulo="Ocupação do reservatório"
            valor={nivel ? nivel.percentual : '--'}
            unidade={nivel ? '%' : ''}
            cor="info"
            icone="💧"
          />
        </div>
      </div>

      <div className="row g-3">
        <div className="col-12 col-lg-6">
          <CartaoGrafico titulo="Evolução das peças (última hora, acumulado)">
            <GraficoLinhaPecas dados={seriePecas} />
          </CartaoGrafico>
        </div>
        <div className="col-12 col-lg-6">
          <CartaoGrafico titulo="Metálicas x não metálicas x indefinidas">
            <GraficoBarrasPecas
              metalicas={contagem.metalicas}
              naoMetalicas={contagem.naoMetalicas}
              indefinidas={contagem.indefinidas}
            />
          </CartaoGrafico>
        </div>
        <div className="col-12">
          <CartaoGrafico titulo="Evolução da ocupação do reservatório">
            <GraficoNivelReservatorio
              dados={serieNivel}
              limiteAtencao={limiteAtencao}
              limiteCritico={limiteCritico}
            />
          </CartaoGrafico>
        </div>
      </div>

      <ListaAlertas alertas={alertas} />
    </>
  )
}

export default Dashboard