import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Cell, ResponsiveContainer,
} from 'recharts'

function GraficoBarrasPecas({ metalicas, naoMetalicas, indefinidas }) {
  const dados = [
    { nome: 'Metálicas', quantidade: metalicas, cor: '#6c757d' },
    { nome: 'Não metálicas', quantidade: naoMetalicas, cor: '#198754' },
    { nome: 'Indefinidas', quantidade: indefinidas, cor: '#ffc107' },
  ]

  return (
    <ResponsiveContainer width="100%" height="100%">
      <BarChart data={dados}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="nome" />
        <YAxis allowDecimals={false} />
        <Tooltip />
        <Bar dataKey="quantidade" name="Quantidade">
          {dados.map((item) => (
            <Cell key={item.nome} fill={item.cor} />
          ))}
        </Bar>
      </BarChart>
    </ResponsiveContainer>
  )
}

export default GraficoBarrasPecas