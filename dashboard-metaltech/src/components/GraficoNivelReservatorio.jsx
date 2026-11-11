import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ReferenceLine, ResponsiveContainer,
} from 'recharts'

function GraficoNivelReservatorio({ dados = [], limiteAtencao, limiteCritico }) {
  if (dados.length === 0) {
    return <p className="text-muted">Sem leituras de nível no período.</p>
  }

  return (
    <ResponsiveContainer width="100%" height="100%">
      <AreaChart data={dados}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="hora" />
        <YAxis domain={[0, 100]} unit="%" />
        <Tooltip formatter={(valor) => `${valor}%`} />
        <ReferenceLine y={limiteAtencao} stroke="#ffc107" strokeDasharray="4 4" label="Atenção" />
        <ReferenceLine y={limiteCritico} stroke="#dc3545" strokeDasharray="4 4" label="Crítico" />
        <Area type="monotone" dataKey="nivel" name="Ocupação" stroke="#0dcaf0" fill="#0dcaf0" fillOpacity={0.3} />
      </AreaChart>
    </ResponsiveContainer>
  )
}

export default GraficoNivelReservatorio
