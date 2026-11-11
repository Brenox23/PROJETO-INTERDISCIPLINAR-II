import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
} from 'recharts'

function GraficoLinhaPecas({ dados = [] }) {
  if (dados.length === 0) {
    return <p className="text-muted">Sem peças registradas no período.</p>
  }

  return (
    <ResponsiveContainer width="100%" height="100%">
      <LineChart data={dados}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="hora" />
        <YAxis allowDecimals={false} />
        <Tooltip />
        <Legend />
        <Line type="monotone" dataKey="total" name="Total" stroke="#0d6efd" strokeWidth={2} dot={false} />
        <Line type="monotone" dataKey="metalicas" name="Metálicas" stroke="#6c757d" dot={false} />
        <Line type="monotone" dataKey="naoMetalicas" name="Não metálicas" stroke="#198754" dot={false} />
      </LineChart>
    </ResponsiveContainer>
  )
}

export default GraficoLinhaPecas